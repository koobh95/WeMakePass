package com.example.wemakepass.data.util;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * - 날짜 관련 유틸리티 클래스.
 * - 이름이 Date이긴 하지만 "java.sql.date"와 "java.util.date"는 사용하지 않는다.
 * - LocalDateTime, LocalDate, LocalTime 등을 다룬다.
 *
 * @author BH-Ku
 * @since 2023-10-07
 */
public class DateUtils {
    private static final DateTimeFormatter localDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter localDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final String TAG = "TAG_DateUtil";

    /**
     * 파라미터로 받은 문자열 형태의 데이터를 LocalDate로 전환한다.
     *
     * @param value 문자열 형태의 날짜값
     * @return
     */
    public static LocalDate parseLocalDate(@NonNull final String value) {
        if(value.equals(""))
            return null;
        return LocalDate.parse(value, localDateFormat);
    }

    /**
     * 파라미터로 받은 문자열 형태의 데이터를 LocalDateTime으로 전환한다.
     *
     * @param value 문자열 형태의 날짜값
     * @return
     */
    public static LocalDateTime parseLocalDateTime(@NonNull final String value){
        if(value.equals(""))
            return null;
        return LocalDateTime.parse(value, localDateTimeFormat);
    }
}
