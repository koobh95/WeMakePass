package com.example.wemakepass.data.enums;

/**
 * - 객관식 시험에서 답안 데이터(ExamDocAnswerModel)가 멤버 변수로 이 타입을 가지며 "현재 답안 상태"를 나타낸다.
 *
 * @author BH-Ku
 * @since 2023-11-25
 */
public enum ExamAnswerState {
    NON, // 채점된 상태가 아님
    CORRECT_ANSWER, // 정답
    INCORRECT_ANSWER, // 오답
    UN_WRITE_ANSWER // 입력되지 않음.
}
