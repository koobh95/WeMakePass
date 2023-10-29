package com.example.wemakepass.data.model.dto;

/**
 *  로그인이 완료된 후 SharedPreferences에 저장할 유저의 데이터를 불러오는데 이 때 사용되는 모델 클래스로서
 * 민감한 데이터들을 제외한 최소한의 데이터들로만 이루어져 있다.
 *
 * @author BH-Ku
 * @since 2023-10-26
 */
public class UserInfoDTO {
    private String userId;
    private String nickname;
    private String email;

    public UserInfoDTO(String userId, String nickname, String email) {
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }
}
