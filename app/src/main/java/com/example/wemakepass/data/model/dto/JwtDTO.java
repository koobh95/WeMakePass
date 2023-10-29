package com.example.wemakepass.data.model.dto;

/**
 *
 *  사용자가 로그인에 성공했을 때 혹은 토큰이 만료되어 새로운 토큰을 발급받았을 때 토큰을 받는 용도로 사용되는
 * 모델 클래스다.
 * 
 *  관련 기능들은 프로젝트 막바지에 추가할 예정
 *
 * @author BH-Ku
 * @since 2023-05-16
 */
public class JwtDTO {
    private String accessToken;
    private String refreshToken;

    public JwtDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public String toString() {
        return "Jwt{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
