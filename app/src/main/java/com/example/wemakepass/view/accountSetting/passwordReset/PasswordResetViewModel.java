package com.example.wemakepass.view.accountSetting.passwordReset;

import android.text.TextUtils;

import com.example.wemakepass.base.BaseViewModel;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.model.dto.request.PasswordResetRequest;
import com.example.wemakepass.network.util.AES256Util;
import com.example.wemakepass.repository.UserRepository;
import com.example.wemakepass.data.util.UserInfoUtils;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * PasswordResetFragment의 ViewModel 클래스.
 *
 * @author BH-Ku
 * @since 2023-11-02
 */
public class PasswordResetViewModel extends BaseViewModel {
    private SingleLiveEvent<String> passwordLiveData, passwordReLiveData;
    private SingleLiveEvent<String> passwordErrMsgLiveData, passwordReErrMsgLiveData;

    private Disposable passwordResetDisposable;
    private UserRepository userRepository;
    private UserInfoUtils userInfoUtil;

    private final AES256Util aes256Util = AES256Util.getInstance();

    public PasswordResetViewModel(){
        userRepository = new UserRepository(getNetworkErrorLiveData());
        userInfoUtil = new UserInfoUtils();
    }

    /**
     * - 비밀번호 변경 버튼을 눌렀을 때 호출되는 콜백 메서드로 xml에서 바인딩되어 있다.
     * - 입력된 비밀번호의 유효성을 확인한 후 문제가 없다면 데이터의 식별자인 userId와 비밀번호를 암호화하여
     *  Repository로 전달한다.
     *
     * @param userId
     */
    public void passwordReset(String userId){
        if(passwordResetDisposable != null && !passwordResetDisposable.isDisposed())
            return;

        if(isValidPassword(passwordLiveData.getValue(), passwordErrMsgLiveData)
                && isValidPassword(passwordReLiveData.getValue(), getPasswordReErrMsgLiveData())
                && passwordEquals()){
            PasswordResetRequest passwordResetRequestDTO = new PasswordResetRequest(
                    aes256Util.encrypt(userId),
                    aes256Util.encrypt(passwordLiveData.getValue()));
            addDisposable(passwordResetDisposable =
                    userRepository.requestPasswordReset(passwordResetRequestDTO));
        }
    }

    /**
     * Password에 대한 검증을 수행한다.
     *
     * @return
     */
    private boolean isValidPassword(String password, SingleLiveEvent<String> errMsgLiveData) {
        final UserInfoUtils.PasswordValidator  validator = userInfoUtil.passwordValidator();

        errMsgLiveData.setValue("");

        if(TextUtils.isEmpty(password))
            errMsgLiveData.setValue(validator.ERR_MSG_EMPTY);
        else if(!validator.isValidFormat(password))
            errMsgLiveData.setValue(validator.ERR_MSG_FORMAT);
        else if(!validator.isValidConditions(password))
            errMsgLiveData.setValue(validator.ERR_MSG_CONDITIONS);
        else if(!validator.inRange(password.length()))
            errMsgLiveData.setValue(validator.ERR_MSG_RANGE);

        return TextUtils.isEmpty(errMsgLiveData.getValue());
    }

    /**
     *  Password 일치 여부를 검증한다. 이 메서드는 두 Password에 대한 검증을 마친 상태에서 호출되기 때문에
     * null 여부는 검증하지 않는다.
     *
     * @return
     */
    private boolean passwordEquals() {
        final String password = passwordLiveData.getValue();
        final String passwordRe = passwordReLiveData.getValue();

        passwordReErrMsgLiveData.setValue("");

        if(!password.equals(passwordRe)){
            passwordReErrMsgLiveData.setValue(UserInfoUtils.PasswordValidator.ERR_MSG_MISMATCH);
            return false;
        }
        return true;
    }

    public SingleLiveEvent<String> getPasswordLiveData() {
        if(passwordLiveData == null)
            passwordLiveData = new SingleLiveEvent<>("user1234!@");
        return passwordLiveData;
    }

    public SingleLiveEvent<String> getPasswordReLiveData() {
        if(passwordReLiveData == null)
            passwordReLiveData = new SingleLiveEvent<>("user1234!@");
        return passwordReLiveData;
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

    public SingleLiveEvent<Boolean> getIsPasswordResetCompleteLiveData() {
        return userRepository.getIsConfirmLiveData();
    }
}
