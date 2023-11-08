package com.example.wemakepass.data.model.dto;

/**
 * 시험 종목의 식별 정보를 가지는 모델 클래스.
 *
 * @author BH-Ku
 * @since 2023-11-08
 */
public class JmInfoDTO {
    private String jmCode;
    private String jmName;
    private char qualCode;

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
}
