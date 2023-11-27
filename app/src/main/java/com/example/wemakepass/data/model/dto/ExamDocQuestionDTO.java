package com.example.wemakepass.data.model.dto;

/**
 * 객관식 문제 하나에 대한 데이터를 갖는 DTO 클래스다.
 *
 * @author BH-Ku
 * @since 2023-11-22
 */
public class ExamDocQuestionDTO {
    private long questionId; // 문제의 식별값
    private long examId; // 문제가 속한 시험의 식별값
    private String subjectName; // 문제가 속한 과목의 이름
    private String question; // 질의
    private String options; // Json 형태의 질의문
    private int score; // 배점
    private String refImage; // 참고 이미지 이름

    public long getQuestionId() {
        return questionId;
    }

    public long getExamId() {
        return examId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getQuestion() {
        return question;
    }

    public String getOptions() {
        return options;
    }

    public int getScore() {
        return score;
    }

    public String getRefImage() {
        return refImage;
    }

    @Override
    public String toString() {
        return "ExamDocQuestionDTO{" +
                "questionId=" + questionId +
                ", examId=" + examId +
                ", subjectName='" + subjectName + '\'' +
                ", question='" + question + '\'' +
                ", options='" + options + '\'' +
                ", score=" + score +
                ", refImage='" + refImage + '\'' +
                '}';
    }
}
