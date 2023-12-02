package com.example.wemakepass.view.auth.login;

import android.text.TextUtils;
import android.view.View;

import com.example.wemakepass.base.BaseViewModel;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.model.dto.JwtDTO;
import com.example.wemakepass.data.model.dto.request.LoginRequest;
import com.example.wemakepass.data.model.dto.UserInfoDTO;
import com.example.wemakepass.network.util.AES256Util;
import com.example.wemakepass.repository.UserRepository;
import com.example.wemakepass.data.util.UserInfoUtils;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * LoginFragment의 ViewModel 클래스
 *
 * @author BH-Ku
 * @since 2023-07-10
 */
public class LoginViewModel extends BaseViewModel {
    private SingleLiveEvent<String> systemMessageLiveData, idLiveData, passwordLiveData;
    private Disposable loginDisposable;

    private final UserRepository userRepository;
    private final AES256Util aes256Util;
    private final UserInfoUtils userInfoUtil;

    private final String TAG = "TAG_LoginViewModel";

    public LoginViewModel() {
        userRepository = new UserRepository(getNetworkErrorLiveData());
        aes256Util = AES256Util.getInstance();
        userInfoUtil = new UserInfoUtils();
    }

    /**
     * - 로그인 버튼을 눌렀을 때 호출되는 콜백 메서드로 xml에서 바인딩되어 있다.
     * - 로그인 요청을 보냈을 때 아직 요청을 처리 중임에도 2번 연속 호출되는 것을 방지하기 위해서 로그인 요청
     *  작업에 대한 Disposable에 대한 참조를 별도로 관리하고 있다.
     * - 로그인 요청을 보내기 전에 입력된 아이디, 비밀번호에 대한 최소한의 검증만 수행한 후 암호화하여
     *  LoginRequestDTO 클래스에 초기화하여 UserRepository로 전달한다.
     *
     * @param view
     */
    public void onLoginButtonClick(View view){
        if(loginDisposable != null && !loginDisposable.isDisposed())
            return;

        if(isValidId() && isValidPassword()){
            LoginRequest loginRequestDTO = new LoginRequest(
                    aes256Util.encrypt(idLiveData.getValue()),
                    aes256Util.encrypt(passwordLiveData.getValue()));
            addDisposable(loginDisposable = userRepository.requestLogin(loginRequestDTO));
        }
    }

    /**
     * Id에 대한 유효성을 검증하되 형식은 검사하지 않고 null 여부, 최소 최대 길이만 검증한다.
     *
     * @return
     */
    private boolean isValidId() {
        final String id = idLiveData.getValue();
        final UserInfoUtils.IdValidator validator = userInfoUtil.idValidator();

        if(TextUtils.isEmpty(id)) {
            systemMessageLiveData.setValue(validator.ERR_MSG_EMPTY);
            return false;
        } else if(!validator.inRange(id.length())) {
            systemMessageLiveData.setValue("아이디는 " + validator.ERR_MSG_RANGE);
            return false;
        }

        return true;
    }

    /**
     * Password에 대한 유효성을 검증하되 형식은 검사하지 않고 null 여부, 최소 최대 길이만 검증한다.
     *
     * @return
     */
    private boolean isValidPassword() {
        final String password = passwordLiveData.getValue();
        final UserInfoUtils.PasswordValidator  validator = userInfoUtil.passwordValidator();

        if(TextUtils.isEmpty(password)) {
            systemMessageLiveData.setValue(validator.ERR_MSG_EMPTY);
            return false;
        } else if(!validator.inRange(password.length())) {
            systemMessageLiveData.setValue("비밀번호는 " + validator.ERR_MSG_RANGE);
            return false;
        }
        return true;
    }

    /**
     * - 로그인에 성공할 경우 JWT를 반환 받는다. JWT를 처리하는 과정에서 순차적으로 이 메서드가 호출된다.
     * - 이 메서드는 공개 가능한 최소한의 정보만을 서버에서 가져온다.
     */
    public void requestUserInfo(){
        addDisposable(userRepository.requestUserInfo());
    }

    public SingleLiveEvent<String> getSystemMessageLiveData() {
        if(systemMessageLiveData == null)
            systemMessageLiveData = new SingleLiveEvent<>();
        return systemMessageLiveData;
    }

    public SingleLiveEvent<String> getIdLiveData() {
        if(idLiveData == null)
            //idLiveData = new SingleLiveEvent<>(AppConfig.AuthPreference.getStoredId());
            idLiveData = new SingleLiveEvent<>("user1235");
        return idLiveData;
    }

    public SingleLiveEvent<String> getPasswordLiveData() {
        if(passwordLiveData == null)
            passwordLiveData = new SingleLiveEvent<>("user1234!@");
        return passwordLiveData;
    }

    public SingleLiveEvent<UserInfoDTO> getUserInfoLiveData() {
        return userRepository.getUserInfoLiveData();
    }

    public SingleLiveEvent<JwtDTO> getJwtLiveData() {
        return userRepository.getJwtLiveData();
    }
}
