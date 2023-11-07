package com.example.wemakepass.network.api;

import com.example.wemakepass.data.model.dto.JwtDTO;
import com.example.wemakepass.data.model.dto.JwtReissueTokenRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 *  Jwt 관련 API를 모아놓은 인터페이스
 *
 * @author BH-Ku
 * @since 2023-10-30
 */
public interface JwtAPI {
    String BASE_URI = "api/jwt/";

    // 동기 토큰 재발급 요청
    @POST(BASE_URI + "reissue")
    Call<JwtDTO> syncReissueToken(@Body JwtReissueTokenRequest jwtReissueTokenRequest);

    // 비동기 토큰 재발급 요청
    @POST(BASE_URI + "reissue")
    Observable<Response<JwtDTO>> asyncReissueToken(@Body JwtReissueTokenRequest jwtReissueTokenRequest);
}
