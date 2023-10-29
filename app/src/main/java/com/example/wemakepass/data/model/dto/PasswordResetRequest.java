package com.example.wemakepass.data.model.dto;

/**
 * - 패스워드를 변경할 때 서버로 전송할 데이터를 담는 모델 클래스다.
 *
 * @author BH-Ku
 * @since 2023-05-27
 */
public class PasswordResetRequest {
    private String userId;
    private String password;

    public PasswordResetRequest(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "PasswordResetRequestDTO{" +
                "userId='" + userId + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
