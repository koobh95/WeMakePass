package com.example.wemakepass.repository;

import androidx.annotation.WorkerThread;

import com.example.wemakepass.config.AppConfig;
import com.example.wemakepass.data.model.dto.JwtDTO;
import com.example.wemakepass.data.model.dto.request.JwtReissueTokenRequest;
import com.example.wemakepass.network.api.JwtAPI;
import com.example.wemakepass.network.client.WmpClient;
import com.example.wemakepass.network.util.AES256Util;

import retrofit2.Call;

/**
 * Jwt 관련 네트워크 작업을 처리하는 레포지토리
 *
 * @author BH-Ku
 * @since 2023-10-30
 */
public class JwtRepository {
    private JwtAPI jwtAPI;
    private AES256Util aes256Util;

    public JwtRepository(){
        jwtAPI = WmpClient.getRetrofit().create(JwtAPI.class);
        aes256Util = AES256Util.getInstance();
    }

    /**
     * - 토큰 재발급 요청.
     * - Interceptor에 의해서만 사용되기 때문에 Worker Thread에서만 호출할 수 있도록 하였다.
     * - Worker Thread에서 호출하기 때문에 비동기가 아닌 동기로 동작한다. 따라서 RxJava의 Observable이 아닌
     *  Retrofit의 Call을 사용하였다.
     * - 저장된 RefreshToken은 ASE256에 의해 암호화된 상태이기 때문에 요청을 보낼 때 암호화를 해제하여 전송한다.
     *
     * @return
     */
    @WorkerThread
    public Call<JwtDTO> syncReissueToken(){
        JwtReissueTokenRequest jwtReissueTokenRequest = new JwtReissueTokenRequest(
                AppConfig.UserPreference.getUserId(),
                aes256Util.decrypt(AppConfig.AuthPreference.getRefreshToken()));
        return jwtAPI.syncReissueToken(jwtReissueTokenRequest);
    }
}
