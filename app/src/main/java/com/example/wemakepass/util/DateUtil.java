package com.example.wemakepass.util;

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
public class DateUtil {
    private static final DateTimeFormatter FORMAT_LOCAL_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FORMAT_LOCAL_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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
        return LocalDate.parse(value, FORMAT_LOCAL_DATE);
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
        return LocalDateTime.parse(value, FORMAT_LOCAL_DATE_TIME);
    }
}
