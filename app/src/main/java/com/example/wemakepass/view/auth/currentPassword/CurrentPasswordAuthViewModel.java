package com.example.wemakepass.view.auth.currentPassword;

import android.text.TextUtils;
import android.view.View;

import com.example.wemakepass.base.BaseViewModel;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.network.util.AES256Util;
import com.example.wemakepass.repository.UserRepository;
import com.example.wemakepass.data.util.UserInfoUtils;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * CurrentPasswordAuthFragment의 ViewModel 클래스.
 *
 * @author BH-Ku
 * @since 2023-11-07
 */
public class CurrentPasswordAuthViewModel extends BaseViewModel {
    private SingleLiveEvent<String> passwordLiveData;
    private Disposable authDisposable;

    private UserRepository userRepository;
    private UserInfoUtils userInfoUtil;
    private AES256Util aes256Util;

    public CurrentPasswordAuthViewModel() {
        userRepository = new UserRepository(getNetworkErrorLiveData());
        userInfoUtil = new UserInfoUtils();
        aes256Util = AES256Util.getInstance();
    }

    /**
     * - 확인 버턴을 눌렀을 때 호출되는 콜백 메서드로 xml에서 바인딩되어 있다.
     * - 요청을 반복해서 보내는 것을 방지하기 위해서 Disposable을 확인한 후 없을 경우 입력한 비밀번호를
     *  최소한으로 검증한다. 검증이 완료되면 비밀번호를 암호화하여 서버로 전송한다.
     *
     * @param view
     */
    public void onAuthButtonClick(View view) {
        if(authDisposable != null && !authDisposable.isDisposed()) {
            systemMessageLiveData.setValue("처리 중입니다.");
            return;
        }

        if(!isValidPassword())
            return;

        String encryptedCurrentPassword = aes256Util.encrypt(passwordLiveData.getValue());
        addDisposable(authDisposable =
                userRepository.requestCurrentPasswordAuth(encryptedCurrentPassword));
    }

    /**
     *  사용자가 입력한 비밀번호가 정상적인지 확인한다. 단, 비밀번호 인증이기 때문에 형식 등은 검사하지 않고
     * Null 여부, 길이만 검증한다.
     *
     * @return
     */
    private boolean isValidPassword() {
        final String password = passwordLiveData.getValue();
        UserInfoUtils.PasswordValidator validator = userInfoUtil.passwordValidator();

        if(TextUtils.isEmpty(password)) {
            systemMessageLiveData.setValue(validator.ERR_MSG_EMPTY);
            return false;
        } else if(!validator.inRange(password.length())) {
            systemMessageLiveData.setValue(validator.ERR_MSG_RANGE);
            return false;
        }

        return true;
    }

    public SingleLiveEvent<String> getPasswordLiveData() {
        if(passwordLiveData == null)
            passwordLiveData = new SingleLiveEvent<>();
        return passwordLiveData;
    }

    public SingleLiveEvent<Boolean> getIsAuthLiveData() {
        return userRepository.getIsConfirmLiveData();
    }
}
