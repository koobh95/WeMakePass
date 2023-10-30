package com.example.wemakepass.data.model.dto;

/**
 *  Jwt 토큰을 재발급받기 위해 서버로 요청을 보낼 때 사용하는 DTO 클래스다.
 *
 * @author BH-Ku
 * @since 2023-10-31
 */
public class JwtReissueTokenRequest {
    private String userId;
    private String refreshToken;

    public JwtReissueTokenRequest(String userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }
}
