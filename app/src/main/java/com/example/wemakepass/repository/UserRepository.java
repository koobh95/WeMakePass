package com.example.wemakepass.repository;

import android.util.Log;

import com.example.wemakepass.base.BaseRepository;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.model.dto.JwtDTO;
import com.example.wemakepass.data.model.dto.LoginRequest;
import com.example.wemakepass.data.model.dto.PasswordResetRequest;
import com.example.wemakepass.data.model.dto.UserInfoDTO;
import com.example.wemakepass.data.model.dto.UserSignUpDTO;
import com.example.wemakepass.data.model.vo.ErrorResponse;
import com.example.wemakepass.network.api.UserAPI;
import com.example.wemakepass.network.client.WmpClient;
import com.example.wemakepass.network.util.ErrorResponseConverter;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 *  유저 관련 네트워크 작업을 처리하는 Repository.
 *
 * @author BH-Ku
 * @since 2023-08-06
 */
public class UserRepository extends BaseRepository {
    private SingleLiveEvent<UserInfoDTO> userInfoLiveData;
    private SingleLiveEvent<JwtDTO> jwtLiveData;
    private SingleLiveEvent<Boolean> isSignUpLiveData, isPasswordResetCompleteLiveData;
    private UserAPI userAPI;

    private final String TAG = "TAG_UserRepository";

    public UserRepository(SingleLiveEvent<ErrorResponse> networkErrorLiveData) {
        super(networkErrorLiveData);
        userAPI = WmpClient.getRetrofit().create(UserAPI.class);
    }

    /**
     *  암호화된 아이디, 비밀번호를 서버로 전송하여 로그인을 요청한다. 로그인이 완료될 경우 Token을 발급받는다.
     * @param loginRequest
     * @return
     */
    public Disposable requestLogin(LoginRequest loginRequest) {
        return userAPI.login(loginRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if(response.isSuccessful()){
                        jwtLiveData.setValue(response.body());
                    } else {
                        ErrorResponse errorResponse = ErrorResponseConverter.parseError(response);
                        networkErrorLiveData.setValue(errorResponse);
                        Log.d(TAG, errorResponse.toString());
                    }
                }, t -> {
                    networkErrorLiveData.setValue(ErrorResponse.ofConnectionFailed());
                    Log.d(TAG, networkErrorLiveData.getValue().toString());
                });
    }

    /**
     * 일반적으로 로그인이 완료되었을 때 호출되며 앱 설정 파일에 저장할 유저 정보를 요청한다.
     *
     * @param userId
     * @return
     */
    public Disposable requestUserInfo(String userId){
        return userAPI.userInfo(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if(response.isSuccessful()) {
                        userInfoLiveData.setValue(response.body());
                    } else {
                        ErrorResponse errorResponse = ErrorResponseConverter.parseError(response);
                        networkErrorLiveData.setValue(errorResponse);
                        Log.d(TAG, errorResponse.toString());
                    }
                }, t ->{
                    networkErrorLiveData.setValue(ErrorResponse.ofConnectionFailed());
                    Log.d(TAG, networkErrorLiveData.getValue().toString());
                });
    }

    /**
     *  회원가입에 필요한 정보들을 모두 암호화하여 서버로 전송하요 회원가입 요청을 수행한다.
     *
     * @param userSignUpDTO
     * @return
     */
    public Disposable requestSignUp(UserSignUpDTO userSignUpDTO){
        return userAPI.signUp(userSignUpDTO)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if(response.isSuccessful()) {
                        isSignUpLiveData.setValue(true);
                    } else {
                        ErrorResponse errorResponse = ErrorResponseConverter.parseError(response);
                        networkErrorLiveData.setValue(errorResponse);
                        Log.d(TAG, errorResponse.toString());
                    }
                }, t -> {
                    networkErrorLiveData.setValue(ErrorResponse.ofConnectionFailed());
                    Log.d(TAG, networkErrorLiveData.getValue().toString());
                });
    }

    /**
     * 비밀번호 변경
     * 식별자가 될 아이디, 비밀번호를 ㅂ
     *
     * @param passwordResetRequest
     * @return
     */
    public Disposable requestPasswordReset(PasswordResetRequest passwordResetRequest) {
        return userAPI.passwordReset(passwordResetRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if(response.isSuccessful()) {
                        isPasswordResetCompleteLiveData.setValue(true);
                    } else {
                        ErrorResponse errorResponse = ErrorResponseConverter.parseError(response);
                        networkErrorLiveData.setValue(errorResponse);
                        Log.d(TAG, errorResponse.toString());
                    }
                }, t -> {
                    networkErrorLiveData.setValue(ErrorResponse.ofConnectionFailed());
                    Log.d(TAG, networkErrorLiveData.getValue().toString());
                });
    }

    public SingleLiveEvent<JwtDTO> getJwtLiveData() {
        if(jwtLiveData == null)
            jwtLiveData = new SingleLiveEvent<>();
        return jwtLiveData;
    }

    public SingleLiveEvent<UserInfoDTO> getUserInfoLiveData() {
        if(userInfoLiveData == null)
            userInfoLiveData = new SingleLiveEvent<>();
        return userInfoLiveData;
    }

    public SingleLiveEvent<Boolean> getIsSignUpLiveData() {
        if(isSignUpLiveData == null)
            isSignUpLiveData = new SingleLiveEvent<>();
        return isSignUpLiveData;
    }

    public SingleLiveEvent<Boolean> getIsPasswordResetCompleteLiveData() {
        if(isPasswordResetCompleteLiveData == null)
            isPasswordResetCompleteLiveData = new SingleLiveEvent<>();
        return isPasswordResetCompleteLiveData;
    }
}
