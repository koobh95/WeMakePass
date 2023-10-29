package com.example.wemakepass.data.model.dto;

/**
 * 서버에 로그인 요청 시 암호화된 아이디와 비밀번호를 저장할 모델 클래스다.
 *
 * @author BH-Ku
 * @since 2023-05-16
 */
public class LoginRequest {
    private String userId;
    private String password;

    public LoginRequest(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }
}
