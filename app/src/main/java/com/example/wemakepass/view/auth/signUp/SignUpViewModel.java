package com.example.wemakepass.view.auth.signUp;

import android.text.TextUtils;
import android.view.View;

import com.example.wemakepass.base.BaseViewModel;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.model.dto.UserSignUpDTO;
import com.example.wemakepass.network.util.AES256Util;
import com.example.wemakepass.repository.UserRepository;
import com.example.wemakepass.util.UserInfoUtil;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * SignUpFragment의 ViewModel 클래스.
 *
 * @author BH-Ku
 * @since 2023-07-10
 */
public class SignUpViewModel extends BaseViewModel {
    private SingleLiveEvent<String> idLiveData, nicknameLiveData, passwordLiveData,
            passwordReLiveData, emailLiveData;
    private SingleLiveEvent<String> idErrMsgLiveData, nicknameErrMsgLiveData, passwordErrMsgLiveData,
            passwordReErrMsgLiveData, emailErrMsgLiveData;
    private Disposable signUpDisposable;

    private UserRepository userRepository;
    private UserInfoUtil userUtil;
    private final AES256Util aes256Util = AES256Util.getInstance();

    private final String TAG = "TAG_SignUpViewModel";

    public SignUpViewModel() {
        userRepository = new UserRepository(getNetworkErrorLiveData());
        userUtil = new UserInfoUtil();
    }

    /**
     * - 회원가입 버튼을 눌렀을 때 호출되는 콜백 메서드로 xml에서 바인딩되어 있다.
     * - Disposable 객체를 통해 회원가입 요청이 진행 중인이 확인한 후 진행 중이라면 메서드를 종료한다.
     * - EditText에 있는 값들의 유효성을 체크하여 유효할 경우 데이터를 암호화하여 Repository에 전달한다.
     *
     * @param view
     */
    public void onSignUpButtonClick(View view){
        if(signUpDisposable != null && !signUpDisposable.isDisposed())
            return;

        if(userUtil.isValidId(idLiveData.getValue(), idErrMsgLiveData)
                && userUtil.isValidNickname(nicknameLiveData.getValue(), nicknameErrMsgLiveData)
                && userUtil.isValidPassword(passwordLiveData.getValue(), passwordErrMsgLiveData)
                && userUtil.isValidPassword(passwordReLiveData.getValue(), passwordReErrMsgLiveData)
                && userUtil.passwordEquals(passwordLiveData.getValue(),
                    passwordReLiveData.getValue(), passwordReErrMsgLiveData)
                && userUtil.isValidEmail(emailLiveData.getValue(), emailErrMsgLiveData)){
            addDisposable(signUpDisposable = userRepository.requestSignUp(createUserSignUpDTO()));
        }
    }

    /**
     * - id, password, nickname, email에 대한 데이터를 암호화하여 UserSignUpDTO에 초기화, 반환한다.
     * - 이 메서드가 호출되는 시점은 위 4개 값이 모두 검증된 이후이기 때문에 별도의 데이터 검사는 하지 않는다.
     * @return
     */
    private UserSignUpDTO createUserSignUpDTO() {
        String id = aes256Util.encrypt(idLiveData.getValue());
        String password = aes256Util.encrypt(passwordLiveData.getValue());
        String nickname = aes256Util.encrypt(nicknameLiveData.getValue());
        String email = aes256Util.encrypt(emailLiveData.getValue());
        return new UserSignUpDTO(id, password, nickname, email);
    }

    /**
     * - 모든 EditText를 대상으로 값의 존재 유무룰 판단하는 메서드다.
     * - 화면에 입력하던 값이 있음에도 불구하고 화면을 종료하려 할 때 정말로 종료할 것인지 다이얼로그를 띄우게
     *  되는데 이 때 다이얼로그를 띄울지 말지를 결정하는 판단 기준이 된다.
     *
     * @return
     */
    public boolean isEmptyEditText() {
        return TextUtils.isEmpty(idLiveData.getValue()) &&
                TextUtils.isEmpty(nicknameLiveData.getValue()) &&
                TextUtils.isEmpty(passwordLiveData.getValue()) &&
                TextUtils.isEmpty(passwordReLiveData.getValue()) &&
                TextUtils.isEmpty(emailLiveData.getValue());
    }

    public SingleLiveEvent<String> getIdLiveData() {
        if(idLiveData == null)
            idLiveData = new SingleLiveEvent<>("user1008");
        return idLiveData;
    }

    public SingleLiveEvent<String> getNicknameLiveData() {
        if(nicknameLiveData == null)
            nicknameLiveData = new SingleLiveEvent<>("user8");
        return nicknameLiveData;
    }

    public SingleLiveEvent<String> getPasswordLiveData() {
        if(passwordLiveData == null)
            passwordLiveData = new SingleLiveEvent<>("abcdefgh1!");
        return passwordLiveData;
    }

    public SingleLiveEvent<String> getPasswordReLiveData() {
        if(passwordReLiveData == null)
            passwordReLiveData = new SingleLiveEvent<>("abcdefgh1!");
        return passwordReLiveData;
    }

    public SingleLiveEvent<String> getEmailLiveData() {
        if(emailLiveData == null)
            emailLiveData = new SingleLiveEvent<>("abcdefg8@naver.com");
        return emailLiveData;
    }

    public SingleLiveEvent<String> getIdErrMsgLiveData() {
        if(idErrMsgLiveData == null)
            idErrMsgLiveData = new SingleLiveEvent<>();
        return idErrMsgLiveData;
    }

    public SingleLiveEvent<String> getNicknameErrMsgLiveData() {
        if(nicknameErrMsgLiveData == null)
            nicknameErrMsgLiveData = new SingleLiveEvent<>();
        return nicknameErrMsgLiveData;
    }

    public SingleLiveEvent<String> getPasswordErrMsgLiveData() {
        if(passwordErrMsgLiveData == null)
            passwordErrMsgLiveData = new SingleLiveEvent<>();
        return passwordErrMsgLiveData;
    }

    public SingleLiveEvent<String> getPasswordReErrMsgLiveData() {
        if(passwordReErrMsgLiveData == null)
            passwordReErrMsgLiveData = new SingleLiveEvent<>();
        return passwordReErrMsgLiveData;
    }

    public SingleLiveEvent<String> getEmailErrMsgLiveData() {
        if(emailErrMsgLiveData == null)
            emailErrMsgLiveData = new SingleLiveEvent<>();
        return emailErrMsgLiveData;
    }

    public SingleLiveEvent<Boolean> getIsSignUpLiveData() {
        return userRepository.getIsSignUpLiveData();
    }
}
