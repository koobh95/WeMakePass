package com.example.wemakepass.view.auth.login;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.wemakepass.base.BaseViewModel;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.model.dto.JwtDTO;
import com.example.wemakepass.data.model.dto.LoginRequest;
import com.example.wemakepass.data.model.dto.UserInfoDTO;
import com.example.wemakepass.network.util.AES256Util;
import com.example.wemakepass.repository.UserRepository;

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
    private final AES256Util aes256Util = AES256Util.getInstance();

    private final String TAG = "TAG_LoginViewModel";

    public LoginViewModel() {
        userRepository = new UserRepository(getNetworkErrorLiveData());
    }

    /**
     * - 로그인 버튼을 눌렀을 때 호출되는 콜백 메서드로 xml에서 바인딩되어 있다.
     * - 로그인 요청을 보냈을 때 아직 요청을 처리 중임에도 2번 연속 호출되는 것을 방지하기 위해서 로그인 요청
     *  작업에 대한 Disposable에 대한 참조를 별도로 관리하고 있다.
     * - 로그인 요청을 보내기 전에 아이디, 비밀번호의 입력 여부와 최소 길이만 확인한 후 암호화한다. 그리고
     *  LoginRequestDTO 클래스에 담아서 UserRepository로 전달한다.
     *
     * @param view
     */
    public void onLoginButtonClick(View view){
        if(loginDisposable != null && !loginDisposable.isDisposed())
            return;

        final String id = idLiveData.getValue();
        final String password = passwordLiveData.getValue();

        if(TextUtils.isEmpty(id)) {
            systemMessageLiveData.setValue("아이디를 입력해주세요.");
            return;
        } else if(id.length() < 6) {
            systemMessageLiveData.setValue("아이디는 최소 6자 이상입니다.");
            return;
        }

        if(TextUtils.isEmpty(password)) {
            systemMessageLiveData.setValue("비밀번호를 입력해주세요.");
            return;
        } else if(password.length() < 10) {
            systemMessageLiveData.setValue("비밀번호는 최소 10자 이상입니다.");
            return;
        }

        LoginRequest loginRequestDTO = new LoginRequest(
                aes256Util.encrypt(id), aes256Util.encrypt(password));
        addDisposable(loginDisposable = userRepository.requestLogin(loginRequestDTO));
    }

    /**
     * - 로그인에 성공할 경우 JWT를 반환 받는다. JWT를 처리하는 과정에서 순차적으로 이 메서드가 호출된다.
     * - 이 메서드는 공개 가능한 최소한의 정보만을 서버에서 가져온다.
     */
    public void requestUserInfo(){
        addDisposable(userRepository.requestUserInfo(idLiveData.getValue()));
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
