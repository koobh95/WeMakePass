package com.example.wemakepass.data.model.dto.request;

/**
 * - 패스워드를 변경할 때 서버로 전송할 데이터를 담는 DTO 클래스다.
 *
 * @author BH-Ku
 * @since 2023-05-27
 */
public class PasswordResetRequest {
    private String userId; // 비밀번호를 변경하고자 하는 유저의 ID
    private String password; // 암호화된 변경하고자 하는 비밀번호

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
