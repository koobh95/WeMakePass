package com.example.wemakepass.network.api;

import com.example.wemakepass.data.model.dto.JwtDTO;
import com.example.wemakepass.data.model.dto.JwtReissueTokenRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 *  Jwt 관련 API를 모아놓은 인터페이스
 *
 * @author BH-Ku
 * @since 2023-10-30
 */
public interface JwtAPI {
    @POST("api/jwt/reissue")
    Call<JwtDTO> reissueToken(@Body JwtReissueTokenRequest jwtReissueTokenRequest);
}
