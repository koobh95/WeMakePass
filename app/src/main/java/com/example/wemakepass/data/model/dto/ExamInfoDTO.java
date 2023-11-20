package com.example.wemakepass.data.model.dto;

/**
 * 특정 종목의 시험에 대한 정보를 가지는 DTO 클래스다.
 *
 * @author BH-Ku
 * @since 2023-11-14
 */
public class ExamInfoDTO {
    private int examId; // 시험 정보의 식별 아이디
    private String jmCode; // 시험이 속한 종목의 종목 식별 아이디
    private int implYear; // 시행 년도
    private int implSeq; // 시행 회차
    private String examFormat; // 시험 구분(필기, 실기, 1차, 2차, 3차)
    private int numOfQuestion; // 문항 개수
    private int timeLimit; // 시험 제한 시간

    public int getExamId() {
        return examId;
    }

    public String getJmCode() {
        return jmCode;
    }

    public int getImplYear() {
        return implYear;
    }

    public int getImplSeq() {
        return implSeq;
    }

    public String getExamFormat() {
        return examFormat;
    }

    public int getNumOfQuestion() {
        return numOfQuestion;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    @Override
    public String toString() {
        return "ExamInfoDTO{" +
                "examId=" + examId +
                ", jmCode='" + jmCode + '\'' +
                ", implYear=" + implYear +
                ", implSeq=" + implSeq +
                ", examFormat='" + examFormat + '\'' +
                ", numOfQuestion=" + numOfQuestion +
                ", timeLimit=" + timeLimit +
                '}';
    }
}
