package com.example.wemakepass.data.enums;

/**
 * 국가 기술 자격의 계열 코드를 정의한 열거형 클래스다. 계열은 단 두 개만 존재한다.
 *
 * @author BH-Ku
 * @since 2023-11-11
 */
public enum QualificationCode {
    TECHNICAL("T", "국가기술자격"),
    PROFESSIONAL("S", "국가전문자격");

    private String code;
    private String value;

    QualificationCode(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
