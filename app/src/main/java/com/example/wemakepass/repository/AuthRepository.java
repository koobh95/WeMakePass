package com.example.wemakepass.repository;

import android.util.Log;

import androidx.annotation.MainThread;

import com.example.wemakepass.base.BaseRepository;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.config.AppConfig;
import com.example.wemakepass.data.model.dto.JwtDTO;
import com.example.wemakepass.data.model.dto.request.JwtReissueTokenRequest;
import com.example.wemakepass.data.model.vo.ErrorResponse;
import com.example.wemakepass.network.api.JwtAPI;
import com.example.wemakepass.network.client.WmpClient;
import com.example.wemakepass.network.util.AES256Util;
import com.example.wemakepass.network.util.ErrorResponseConverter;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * - Jwt 인증 관련 네트워크 작업을 수행하는 레포지토리다.
 * - 이미 JwtRepository 클래스가 있는 만큼 역할이 겹친다. JwtRepository와의 차이는 JwtRepository는
 *  Inteceptor가 동작하는 과정에서 동기적으로 실행되는 작업만 모아놓았다는 것이고 이 클래스는 비동기적으로
 *  수행되는 네트워크 작업을 모아놓았다는 차이가 있다. 처음부터 이렇게 설계했던 것은 아니나 JwtRepository에
 *  두 개의 메서드가 객체화되어 사용되는 시점이 달랐기 때문에 고민 끝에 나누게 되었다. 여기서는 단지 토큰이 유효한지
 *  판단하는 비동기 작업을 처리하고 JwtRepository에서는 토큰 재발급을 요청하는 비동기 작업을 수행한다.
 *
 * @author BH-Ku
 * @since 2023-10-31
 */
public class AuthRepository extends BaseRepository {
    private SingleLiveEvent<Boolean> isReissueLiveData;

    private final JwtAPI jwtAPI;
    private AES256Util aes256Util;

    private final String TAG = "TAG_AuthRepository";

    public AuthRepository(SingleLiveEvent<ErrorResponse> networkErrorLiveData) {
        super(networkErrorLiveData);
        jwtAPI = WmpClient.getRetrofit().create(JwtAPI.class);
        aes256Util = AES256Util.getInstance();
    }

    /**
     * - 토큰의 유효성을 체크하기 위한 메서드다.
     * - 내부에 저장된 UserId와 RefreshToken을 서버로 보내 유효한 토큰인지 검사 후 유효할 경우 토큰을 재발급
     *  받는다.
     * - 정상적인 토큰을 발급받지 못한 경우 사유가 어떻든 간에 유저에게는 토큰의 유효 기간이 끝난 것으로만 알린다.
     * @return
     */
    @MainThread
    public Disposable tokenValidationCheck() {
        JwtReissueTokenRequest jwtReissueTokenRequest = new JwtReissueTokenRequest(
                AppConfig.UserPreference.getUserId(),
                aes256Util.decrypt(AppConfig.AuthPreference.getRefreshToken()));
        return jwtAPI.asyncReissueToken(jwtReissueTokenRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if(response.isSuccessful()) {
                        JwtDTO jwtDTO = response.body();
                        AppConfig.AuthPreference.setAccessToken(jwtDTO.getAccessToken());
                        AppConfig.AuthPreference.setRefreshToken(jwtDTO.getRefreshToken());
                        isReissueLiveData.setValue(true);
                    } else {
                        ErrorResponse errorResponse = ErrorResponseConverter.parseError(response);
                        Log.d(TAG, errorResponse.toString());
                        networkErrorLiveData.setValue(ErrorResponse.ofAuthenticationFailed());
                    }
                }, t -> {
                    networkErrorLiveData.setValue(ErrorResponse.ofConnectionFailed());
                    Log.d(TAG, networkErrorLiveData.getValue().toString());
                });
    }

    public SingleLiveEvent<Boolean> getIsReissueLiveData() {
        if(isReissueLiveData == null)
            isReissueLiveData = new SingleLiveEvent<>();
        return isReissueLiveData;
    }
}
