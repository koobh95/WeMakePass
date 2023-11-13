package com.example.wemakepass.data.pref;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.wemakepass.config.AppConfig;

/**
 * - 어플리케이션 내에서 DB를 사용하기에는 데이터가 너무 단순하거나 다루는 데이터의 최대 갯수가 적어 Local DB를
 * 사용하기엔 애매한 경우 SharedPreferences를 사용한다.
 * - Application 클래스를 상속받은 클래스에 의해서 어플리케이션 실행 초기에 초기화된다.
 * - 여기에 저장되는 데이터들은 보안상 문제가 없는 데이터들이다.
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

    private static final String KEY_INTEREST_JM = "interestJm_";
    private static final String KEY_EXAM_JM_SEARCH_LOG = "examJmSearchLog_";

    public AppDataPreferences(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * - 관심 종목 데이터
     * - InterestJmModel 객체를 JsonObject로 변환한 후 JsonArray에 추가, String으로 변환된 형태로
     * 저장하고 있다. 그렇기 때문에 반환할 때 역시 String 형태로 반환하며 직렬화 역직렬화는 호출하는 곳에서
     * 수행한다.
     *
     * @return String 형태의 JsonArray를 반환한다.
     */
    public static String getInterestJmData() {
        return pref.getString(KEY_INTEREST_JM + AppConfig.UserPreference.getUserId(), "");
    }

    public static void setInterestJmData(String data) {
        pref.edit().putString(KEY_INTEREST_JM + AppConfig.UserPreference.getUserId(), data).apply();
    }

    /**
     * - ExamActivity > JmSearchFragment 에서 검색 기록 데이터
     * - 문자열에서 데이터는 파이프 라인 문자('|')를 사용하여 구분한다.
     *
     * @return
     */
    public static String getExamJmSearchLogData() {
        return pref.getString(KEY_EXAM_JM_SEARCH_LOG + AppConfig.UserPreference.getUserId(), "");
    }

    public static void setExamJmSearchLogData(String data) {
        pref.edit().putString(KEY_EXAM_JM_SEARCH_LOG + AppConfig.UserPreference.getUserId(), data)
                .apply();
    }
}
