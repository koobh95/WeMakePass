package com.example.wemakepass.view.auth.passwordReset;

import com.example.wemakepass.base.BaseViewModel;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.model.dto.PasswordResetRequest;
import com.example.wemakepass.network.util.AES256Util;
import com.example.wemakepass.repository.UserRepository;
import com.example.wemakepass.util.UserInfoUtil;

import io.reactivex.rxjava3.disposables.Disposable;

public class PasswordResetViewModel extends BaseViewModel {
    private SingleLiveEvent<String> passwordLiveData, passwordReLiveData;
    private SingleLiveEvent<String> passwordErrMsgLiveData, passwordReErrMsgLiveData;

    private Disposable passwordResetDisposable;
    private UserRepository userRepository;
    private UserInfoUtil userUtil;

    private final AES256Util aes256Util = AES256Util.getInstance();

    public PasswordResetViewModel(){
        userRepository = new UserRepository(getNetworkErrorLiveData());
        userUtil = new UserInfoUtil();
    }

    /**
     * - 비밀번호 변경 버튼을 눌렀을 때 호출되는 콜백 메서드로 xml에서 바인딩되어 있다.
     * - 입력된 비밀번호의 유효성을 확인한 후 문제가 없다면 데이터의 식별자인 user id와 비밀번호를 암호화하여
     *  Repository로 전달한다.
     *
     * @param userId
     */
    public void passwordReset(String userId){
        if(passwordResetDisposable != null && !passwordResetDisposable.isDisposed())
            return;

        if(userUtil.isValidPassword(passwordLiveData.getValue(), passwordErrMsgLiveData)
                && userUtil.isValidPassword(passwordReLiveData.getValue(), passwordReErrMsgLiveData)
                && userUtil.passwordEquals(passwordLiveData.getValue(),
                passwordReLiveData.getValue(), passwordReErrMsgLiveData)){

            PasswordResetRequest passwordResetRequestDTO = new PasswordResetRequest(
                    aes256Util.encrypt(userId),
                    aes256Util.encrypt(passwordLiveData.getValue()));
            addDisposable(passwordResetDisposable =
                    userRepository.requestPasswordReset(passwordResetRequestDTO));
        }
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
        return userRepository.getIsPasswordResetCompleteLiveData();
    }
}
