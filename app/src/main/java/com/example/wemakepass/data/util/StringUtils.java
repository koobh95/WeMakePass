package com.example.wemakepass.data.util;

import androidx.annotation.NonNull;

/**
 * String 관련 Utility 클래스
 *
 * @author BH-Ku
 * @since 2023-11-08
 */
public class StringUtils {

    /**
     *  파라미터로 받은 String 객체에서 공백을 모두 제거하여 반환한다. 만약 파라미터로 들어온 객체가 null을
     * 가지고 있을 경우 빈 문자열을 반환한다.
     *
     * @param str 공백을 제거하고자 하는 String 객체
     * @return 공백이 제거된 String 객체
     */
    public static String removeAllSpace(String str) {
        if(str == null)
            return "";
        return str.replaceAll("\\s+", "");
    }
}
