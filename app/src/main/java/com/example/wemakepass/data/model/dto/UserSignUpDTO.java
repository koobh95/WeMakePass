package com.example.wemakepass.data.model.dto;

/**
 *  회원가입 시 서버에 전달해야하는 데이터들을 모아놓은 모델 클래스다.
 *
 * @author BH-Ku
 * @since 2023-05-14
 */
public class UserSignUpDTO {
    private String userId;
    private String password;
    private String nickname;
    private String email;

    public UserSignUpDTO(String userId, String password, String nickname, String email){
        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserSignUpDTO{" +
                "userId='" + userId + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
