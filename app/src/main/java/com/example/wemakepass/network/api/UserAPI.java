package com.example.wemakepass.network.api;


import com.example.wemakepass.annotations.LoginRequired;
import com.example.wemakepass.data.model.dto.JwtDTO;
import com.example.wemakepass.data.model.dto.LoginRequest;
import com.example.wemakepass.data.model.dto.PasswordResetRequest;
import com.example.wemakepass.data.model.dto.UserInfoDTO;
import com.example.wemakepass.data.model.dto.UserSignUpDTO;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * User 정보 관련 API를 모아놓은 인터페이스
 *
 * @author BH-Ku
 * @since 2023-06-07
 */
public interface UserAPI {
    String BASE_URI = "api/user/";

    // 회원가입 요청
    @POST(BASE_URI + "sign_up")
    Observable<Response<String>> signUp(@Body UserSignUpDTO userSignUpDTO);

    // 로그인 요청
    @POST(BASE_URI + "login")
    Observable<Response<JwtDTO>> login(@Body LoginRequest loginRequestDTO);

    // 어플리케이션 내부에 저장해도 괜찮은 최소한의 정보로만 이루어진 유저 정보 요청
    @LoginRequired
    @GET(BASE_URI + "info")
    Observable<Response<UserInfoDTO>> userInfo();

    // 비밀번호 리셋 요청
    @PUT(BASE_URI + "password_reset")
    Observable<Response<String>> passwordReset(@Body PasswordResetRequest passwordResetRequestDTO);

    // 닉네임 변경 요청
    @LoginRequired
    @GET(BASE_URI + "nickname_change")
    Observable<Response<String>> nicknameChange(@Query("newNickname") String newNickname);

    // 현재 비밀번호 인증. 로그인 된 상태에서 비밀번호 변경을 수행할 때 사용.
    @LoginRequired
    @POST(BASE_URI + "password_auth")
    Observable<Response<String>> currentPasswordAuth(@Body String currentPassword);
}
