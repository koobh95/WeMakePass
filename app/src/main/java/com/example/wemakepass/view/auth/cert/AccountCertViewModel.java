package com.example.wemakepass.view.auth.cert;

import android.text.TextUtils;
import android.util.Log;

import com.example.wemakepass.base.BaseViewModel;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.repository.MailRepository;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * AccountCertActivity의 ViewModel 클래스.
 *
 * @author BH-Ku
 * @since 2023-10-24
 */
public class AccountCertViewModel extends BaseViewModel {
    private SingleLiveEvent<String> codeLiveData, timerLiveData;
    private SingleLiveEvent<Boolean> isTimeOver;
    private MailRepository mailRepository;
    private Disposable timerDisposable;

    public static final int CODE_LEN = 6;
    private final int LIMIT_TIME = 179;
    private final String TAG = "TAG_EmailCertViewModel";

    public AccountCertViewModel(){
        mailRepository = new MailRepository(getNetworkErrorLiveData());
    }

    public void sendMailRequest(String userId){
        addDisposable(mailRepository.sendAccountCertMail(userId));
    }

    public void certCodeConfirmRequest(String userId){
        if(TextUtils.isEmpty(getCodeLiveData().getValue())){
            systemMessageLiveData.setValue("인증번호를 입력하세요.");
            return;
        }

        if(codeLiveData.getValue().length() != AccountCertViewModel.CODE_LEN) {
            systemMessageLiveData.setValue("인증번호는 6자리입니다.");
            return;
        }

        addDisposable(mailRepository.certCodeConfirm(userId, codeLiveData.getValue()));
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
                            Log.d(TAG, "Time=" + time);
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
     * @return 타이머 동작 여부
     */
    public boolean isRunningTimer() {
        return timerDisposable != null && !timerDisposable.isDisposed();
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
