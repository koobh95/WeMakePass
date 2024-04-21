package com.example.wemakepass.view.auth.findAccount.password;

import android.text.TextUtils;
import android.view.View;

import com.example.wemakepass.base.BaseViewModel;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.repository.MailRepository;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * FindPasswordFragment의 ViewModel 클래스.
 *
 * @author BH-Ku
 * @since 2023-10-24
 */
public class FindPasswordViewModel extends BaseViewModel {
    private SingleLiveEvent<String> idLiveData, codeLiveData, timerLiveData;
    private SingleLiveEvent<Boolean> isTimeOver;
    private Disposable timerDisposable;
    private final MailRepository mailRepository;

    public static final int CODE_LEN = 6; // 인증 코드 길이
    private final int LIMIT_TIME = 179; // 인증 시간(seconds)
    private final String TAG = "TAG_FindPasswordViewModel";

    public FindPasswordViewModel(){
        mailRepository = new MailRepository(getNetworkErrorLiveData());
    }

    /**
     * - 인증 요청 버튼을 눌렀을 때 호출되는 콜백 메서드로 xml에서 바인딩되어 있다.
     * - 가입할 때 사용했던 이메일로 인증 번호를 전송하기 때문에 아이디가 필수적으로 필요하다. 따라서 아이디의
     *  입력 여부를 판단한다.
     *
     * @param view
     */
    public void onSendMailButtonClick(View view){
        if(TextUtils.isEmpty(idLiveData.getValue())) {
            systemMessageLiveData.setValue("아이디를 입력하세요.");
            return;
        }

        addDisposable(mailRepository.sendForgetPasswordMail(idLiveData.getValue()));
    }

    /**
     * - 인증번호 확인 버튼을 눌렀을 때 호출되는 콜백 메서드로 xml에서 바인딩되어 있다.
     * - 인증 번호의 입력 여부와 정상적인 입력(코드 길이)를 확인한다.
     * @param view
     */
    public void onCodeConfirmButtonClick(View view){
        if(TextUtils.isEmpty(codeLiveData.getValue())) {
            systemMessageLiveData.setValue("코드를 입력하세요.");
            return;
        }

        if(codeLiveData.getValue().length() != CODE_LEN) {
            systemMessageLiveData.setValue("인증 번호는 6자리입니다.");
            return;
        }

        addDisposable(mailRepository.certCodeConfirm(idLiveData.getValue(), codeLiveData.getValue()));
    }

    /**
     *  타이머 역할을 하는 Observable을 생성하고 타이머의 동작 유무(isDispose)를 판단하기 위해서 Disposable을
     * 별도로 관리한다.
     */
    public void startTimer() {
        timerDisposable = Observable.interval(1000L, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .take(LIMIT_TIME+2)
                .subscribe(time -> {
                    timerLiveData.setValue(String.format("%tM:%<tS",  ((LIMIT_TIME - time) * 1000L)));
                }, t -> {
                    // onError
                } ,() -> isTimeOver.setValue(true));
        addDisposable(timerDisposable);
    }

    /**
     *  타이머 Observable이 동작 중인지 여부를 확인한다. isDisposed 즉, 폐기된 상태가 아니라면 동작 중인 것으로
     * 간주한다.
     *
     * @return
     */
    public boolean isRunningTimer() {
        return timerDisposable != null && !timerDisposable.isDisposed();
    }

    public SingleLiveEvent<String> getIdLiveData() {
        if(idLiveData == null)
            idLiveData = new SingleLiveEvent<>();
        return idLiveData;
    }

    public SingleLiveEvent<String> getCodeLiveData() {
        if(codeLiveData == null)
            codeLiveData = new SingleLiveEvent<>();
        return codeLiveData;
    }

    public SingleLiveEvent<String> getTimerLiveData() {
        if(timerLiveData == null)
            timerLiveData = new SingleLiveEvent<>();
        return timerLiveData;
    }

    public SingleLiveEvent<Boolean> getIsTimeOver() {
        if(isTimeOver == null)
            isTimeOver = new SingleLiveEvent<>();
        return isTimeOver;
    }

    public SingleLiveEvent<Boolean> getIsSendMailLiveData() {
        return mailRepository.getIsSendMailLiveData();
    }

    public SingleLiveEvent<Boolean> getIsConfirmLiveData() {
        return mailRepository.getIsConfirmLiveData();
    }
}
