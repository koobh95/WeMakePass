package com.example.wemakepass.data.model.dto;

import java.io.Serializable;

/**
 * 특정 종목의 시험에 대한 정보를 가지는 DTO 클래스다.
 *
 * @author BH-Ku
 * @since 2023-11-14
 */
public class ExamInfoDTO implements Serializable {
    private long examId; // 시험 정보의 식별 아이디
    private String jmCode; // 시험이 속한 종목의 종목 식별 아이디
    private String implYear; // 시행 년도, DB에서 int값으로 저장되어 있지만 편의상 String으로 변경
    private String implSeq; // 시행 회차, DB에서 int값으로 저장되어 있지만 편의상 String으로 변경
    private String examFormat; // 시험 구분(필기, 실기, 1차, 2차, 3차)
    private int numOfQuestion; // 문항 개수
    private int timeLimit; // 시험 제한 시간

    public ExamInfoDTO(long examId, String jmCode, String implYear, String implSeq, String examFormat, int numOfQuestion, int timeLimit) {
        this.examId = examId;
        this.jmCode = jmCode;
        this.implYear = implYear;
        this.implSeq = implSeq;
        this.examFormat = examFormat;
        this.numOfQuestion = numOfQuestion;
        this.timeLimit = timeLimit;
    }

    public long getExamId() {
        return examId;
    }

    public String getJmCode() {
        return jmCode;
    }

    public String getImplYear() {
        return implYear;
    }

    public String getImplSeq() {
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
