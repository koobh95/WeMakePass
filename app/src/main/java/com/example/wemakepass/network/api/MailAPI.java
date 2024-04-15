package com.example.wemakepass.network.api;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 메일 관련 작업을 모아놓은 API 인터페이스.
 *
 * @author BH-Ku
 * @since 2023-06-07
 */
public interface MailAPI {
    String BASE_URI = "api/mail/";

    // 회원가입은 하였지만 인증은 되지 않았을 경우 인증 번호를 담은 메일 발송 요청
    @GET(BASE_URI + "account-cert")
    Observable<Response<String>> accountCert(@Query("userId") String userId);

    // 이메일로 받은 코드 인증 요청
    @GET(BASE_URI + "confirm")
    Observable<Response<String>> confirm(@Query("userId") String userId,
                                         @Query("code") String code);

    // 비밀번호 찾기 매일 발송 요청
    @GET(BASE_URI + "forget-password")
    Observable<Response<String>> forgetPassword(@Query("userId") String userId);

    // 아이디 찾기 메일 발송 요청
    @GET(BASE_URI + "forget-id")
    Observable<Response<String>> forgetUserId(@Query("email") String email);
}
