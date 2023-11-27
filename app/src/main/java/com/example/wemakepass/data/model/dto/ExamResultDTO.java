package com.example.wemakepass.data.model.dto;

import java.time.LocalDateTime;

/**
 * 시험 결과를 서버로 전송할 때 사용되는 DTO 클래스
 *
 * @author BH-Ku
 * @since 2023-11-26
 */
public class ExamResultDTO {
    private long examId; // 시험의 식별값
    private String userId; // 응시한 유저의 아이디
    private String reasonForRejection; // 불합격 사유
    private long elapsedTime; // 응시하는데 소요된 시간
    private int score; // 최종 획득 점수
    private String answerSheet; // 사용자가 입력한 답안지

    public ExamResultDTO(ExamResultBuilder builder) {
        examId = builder.examId;
        userId = builder.userId;
        reasonForRejection = builder.reasonForRejection;
        elapsedTime = builder.elapsedTime;
        score = builder.score;
        answerSheet = builder.answerSheet;
    }

    public long getExamId() {
        return examId;
    }

    public String getUserId() {
        return userId;
    }

    public String getReasonForRejection() {
        return reasonForRejection;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public int getScore() {
        return score;
    }

    public String getAnswerSheet() {
        return answerSheet;
    }

    public static class ExamResultBuilder {
        private long examId;
        private String userId;
        private String reasonForRejection;
        private long elapsedTime;
        private int score;
        private String answerSheet;

        public ExamResultBuilder setExamId(long examId) {
            this.examId = examId;
            return this;
        }

        public ExamResultBuilder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public ExamResultBuilder setReasonForRejection(String reasonForRejection) {
            this.reasonForRejection = reasonForRejection;
            return this;
        }

        public ExamResultBuilder setElapsedTime(long elapsedTime) {
            this.elapsedTime = elapsedTime;
            return this;
        }

        public ExamResultBuilder setScore(int score) {
            this.score = score;
            return this;
        }

        public ExamResultBuilder setAnswerSheet(String answerSheet) {
            this.answerSheet = answerSheet;
            return this;
        }

        public ExamResultDTO build() {
            return new ExamResultDTO(this);
        }
    }

    @Override
    public String toString() {
        return "ExamResultDTO{" +
                "examId=" + examId +
                ", userId='" + userId + '\'' +
                ", reasonForRejection='" + reasonForRejection + '\'' +
                ", elapsedTime=" + elapsedTime +
                ", score=" + score +
                ", answerSheet='" + answerSheet + '\'' +
                '}';
    }
}
