package com.example.wemakepass.data.model.dto;

import java.io.Serializable;

/**
 * 시험 종목의 식별 정보를 가지는 Model 클래스.
 *
 * @author BH-Ku
 * @since 2023-11-08
 */
public class JmDTO implements Serializable {
    private String jmCode; // 종목 식별 아이디
    private String jmName; // 종목 이름

    public String getJmCode() {
        return jmCode;
    }

    public String getJmName() {
        return jmName;
    }

    @Override
    public String toString() {
        return "JmDTO{" +
                "jmCode='" + jmCode + '\'' +
                ", jmName='" + jmName + '\'' +
                '}';
    }
}
