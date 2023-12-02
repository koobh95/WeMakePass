package com.example.wemakepass.data.pref;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.wemakepass.config.AppConfig;

/**
 * - 어플리케이션 내에서 DB를 사용하기에는 데이터가 너무 단순하거나 다루는 데이터의 최대 갯수가 적어 Local DB를
 * 사용하기엔 애매한 경우 SharedPreferences를 사용한다.
 * - Application 클래스를 상속받은 클래스에 의해서 어플리케이션 실행 초기에 초기화된다.
 * - 여기에 저장되는 데이터들은 외부에 유출되어도 보안상 아무런 문제가 없는 데이터들이다.
 * - 여기서 다루는 데이터들은 사용자간 공유되는 데이터가 아니므로 Key값에서 유저마다 차이를 둔다. 데이터를
 * 저장하거나 읽을 때 사용하는 Key값에 현재 UserPreferences에 등록된 사용자의 ID를 더하여 사용한다. 즉
 * key값이 "myData_", id가 "user12"일 경우 키값은 "myData_user12"가 된다.
 *
 * @author BH-Ku
 * @since 2023-11-08
 */
public class AppDataPreferences {
    private static SharedPreferences pref;

    private static final String PREF_NAME = "wmpPreferences";

    public static final String KEY_INTEREST_JM = "interestJm_";
    public static final String KEY_VISITED_BOARD = "visitedBoard_";
    public static final String KEY_EXAM_JM_SEARCH_LOG = "examJmSearchLog_"; // 시험 종목 검색
    public static final String KEY_BOARD_SEARCH_LOG = "boardSearchLog_"; // 게시판 검색

    public AppDataPreferences(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static String getData(String key) {
        return pref.getString(key + AppConfig.UserPreference.getUserId(), "");
    }

    public static void setData(String key, String data) {
        pref.edit().putString(key + AppConfig.UserPreference.getUserId(), data).apply();
    }
}
