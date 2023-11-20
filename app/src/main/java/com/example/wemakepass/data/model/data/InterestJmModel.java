package com.example.wemakepass.data.model.data;

/**
 * - 시험 종목의 식별 정보를 가지는 모델 클래스.
 * - JmInfoDTO와 다른 점은 QualCode 즉, 시험 구분 정보(T, S)를 갖지 않는다는 것이다.
 *
 * @author BH-Ku
 * @since 2023-11-08
 */
public class InterestJmModel {
    private final String jmCode; // 종목 식별 아이디
    private final String jmName; // 종목 이름

    public InterestJmModel(String jmCode, String jmName) {
        this.jmCode = jmCode;
        this.jmName = jmName;
    }

    public String getJmCode() {
        return jmCode;
    }

    public String getJmName() {
        return jmName;
    }
}
