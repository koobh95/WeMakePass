package com.example.wemakepass.config;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

/**
 * - SharedPreferences에 접근할 때 사용되는 싱글톤 클래스다.
 * - 설정의 특성별로 내부 클래스를 생성하였고 각 값에 접근할 키값을 멤버 변수로 갖는다.
 *
 * @author BH-Ku
 * @since 2023-10-28
 */
public class AppConfig {
    private static SharedPreferences pref;

    public AppConfig(Context context) {
        AppConfig.pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public SharedPreferences getPreferences(){
        return pref;
    }

    /**
     * 사용자 로그인 설정 정보, 인증 정보(Token) 등을 갖는 설정 파일이다.
     */
    public static class AuthPreference {
        private static final String KEY_KEEP_LOGIN = "keepLogin";
        private static final String KEY_STORED_ID = "storedId";
        private static final String KEY_ACCESS_TOKEN = "accessToken";
        private static final String KEY_REFRESH_TOKEN = "refreshToken";

        public static boolean isKeepLogin(){
            return pref.getBoolean(KEY_KEEP_LOGIN, false);
        }

        public static void setKeepLogin(boolean keepLogin){
            pref.edit().putBoolean(KEY_KEEP_LOGIN, keepLogin).apply();
        }

        public static boolean isStoredId(){
            return pref.getBoolean(KEY_STORED_ID, false);
        }

        public static void setStoredId(boolean storedId) {
            pref.edit().putBoolean(KEY_STORED_ID, storedId).apply();
        }

        public static String getAccessToken() {
            return pref.getString(KEY_ACCESS_TOKEN, "");
        }

        public static void setAccessToken(String accessToken){
            pref.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply();
        }

        public static String getRefreshToken(){
            return pref.getString(KEY_REFRESH_TOKEN, "");
        }

        public static void setRefreshToken(String refreshToken){
            pref.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply();
        }
    }

    /**
     *  로그인된 유저에 대한 정보를 저장하는 설정 파일이다. 여기에 저장되는 정보들은 유출에 민감하지 않은 정보여야
     * 한다.
     */
    public static class UserPreference{
        private static final String KEY_USER_ID = "userId";
        private static final String KEY_NICKNAME = "nickname";
        private static final String KEY_EMAIL = "email";

        public static String getUserId(){
            return pref.getString(KEY_USER_ID, "");
        }

        public static void setUserId(String userId) {
            pref.edit().putString(KEY_USER_ID, userId).apply();
        }

        public static String getNickname() {
            return pref.getString(KEY_NICKNAME, "");
        }

        public static void setNickname(String nickname) {
            pref.edit().putString(KEY_NICKNAME, nickname).apply();
        }

        public static String getEmail() {
            return pref.getString(KEY_EMAIL, "");
        }

        public static void setEmail(String email){
             pref.edit().putString(KEY_EMAIL, email).apply();
        }
    }
}
