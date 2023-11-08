package com.example.wemakepass.data.pref;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * - 관심 종목에 대한 데이터를 저장하는 Preferences 클래스로서 Application 클래스를 상속받은 클래스에 의해서
 *  어플리케이션 실행 초기에 할당된다.
 * - 본래 Room으로 구현하였었지만 저장되는 데이터가 최대 5 뿐이었기 때문에 SharedPreferences에 데이터를
 *  저장하기로 했다.
 *
 * @author BH-Ku
 * @since 2023-11-08
 */
public class InterestJmPreferences {
    private static SharedPreferences pref;

    private static final String PREF_NAME = "interestJmPref";
    private static final String KEY_INTEREST_JM = "interestJm";

    public InterestJmPreferences(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * @return String 형태의 JsonArray를 반환한다.
     */
    public static String getJmData(){
        return pref.getString(KEY_INTEREST_JM, "");
    }

    /**
     * @param jmData String 형태의 JsonArray
     */
    public static void setJmData(String jmData){
        pref.edit().putString(KEY_INTEREST_JM, jmData).apply();
    }
}
