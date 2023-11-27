package com.example.wemakepass.data.model.data;

import com.example.wemakepass.data.enums.ExamAnswerState;

/**
 * - 객관식 문제의 선택지에서 선택한 답과 관련된 데이터들 가지는 Model 클래스다.
 * - 이 데이터가 속한 리스트의 index+1이 곧 문제의 번호가 되므로 어떤 문제의 답안이냐에 대한 데이터는 별도로 가지지
 *  않는다.
 *
 * @author BH-Ku
 * @since 2023-11-25
 */
public class ExamDocAnswerModel {
    private int answer; // 선택한 값, 최초 -1로 초기화되며 -1은 선택한 값이 없다는 것을 의마한다.
    private ExamAnswerState answerState; // 답안 선택 여부, 채점 결과 등의 상태 값을 가진다.

    public ExamDocAnswerModel() {
        answer = -1;
        answerState = ExamAnswerState.NON;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswerState(ExamAnswerState answerState) {
        this.answerState = answerState;
    }

    public ExamAnswerState getAnswerState() {
        return answerState;
    }
}
