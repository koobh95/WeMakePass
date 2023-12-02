package com.example.wemakepass.data.model.dto.request;

/**
 * 서버에 로그인 요청 시 암호화된 아이디와 비밀번호를 저장할 DTO 클래스다.
 *
 * @author BH-Ku
 * @since 2023-05-16
 */
public class LoginRequest {
    private String userId; // 유저의 Id
    private String password; // 암호화된 유저의 비밀번호

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
