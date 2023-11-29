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

    public static final String KEY_EXAM_JM_SEARCH_LOG = "examJmSearchLog_"; // 시험 종목 검색
    public static final String KEY_BOARD_SEARCH_LOG = "boardSearchLog_"; // 게시판 검색

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
     * - 검색 기록 데이터를 조회하거나 저장한다.
     * - 문자열에서 데이터는 파이프 라인 문자('|')를 사용하여 구분한다.
     * - 모든 검색 기록이 같은 형태를 취하고 있기 때문에 비슷한 코드들이 반복되는 것을 줄이기 위해서
     *  LogRepository에서 로그 관련 조회, 검색, 삭제, 초기화등을 모두 수행한다. 단 어떤 로그인지를 구분하기
     *  위해서 Key을 상이하게 사용한다. 자세한 내용은 LogRepository를 참고.
     *
     * @return
     */
    public static String getLogData(String key) {
        return pref.getString(key + AppConfig.UserPreference.getUserId(), "");
    }

    public static void setLogData(String key, String data) {
        pref.edit().putString(key + AppConfig.UserPreference.getUserId(), data).apply();
    }
}
