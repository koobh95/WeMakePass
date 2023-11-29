package com.example.wemakepass.data.model.dto;

import java.io.Serializable;

/**
 * 시험 종목의 식별 정보를 가지는 Model 클래스.
 *
 * @author BH-Ku
 * @since 2023-11-08
 */
public class JmInfoDTO implements Serializable {
    private String jmCode; // 종목 식별 아이디
    private String jmName; // 종목 이름
    private char qualCode; // 계열 코드(T = 국가기술자격, S = 국가전문자격)

    public JmInfoDTO(String jmCode, String jmName, char qualCode) {
        this.jmCode = jmCode;
        this.jmName = jmName;
        this.qualCode = qualCode;
    }

    public String getJmCode() {
        return jmCode;
    }

    public String getJmName() {
        return jmName;
    }

    public char getQualCode() {
        return qualCode;
    }

    @Override
    public String toString() {
        return "JmInfoDTO{" +
                "jmCode='" + jmCode + '\'' +
                ", jmName='" + jmName + '\'' +
                ", qualCode=" + qualCode +
                '}';
    }
}
