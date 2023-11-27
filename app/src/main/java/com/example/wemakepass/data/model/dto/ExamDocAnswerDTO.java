package com.example.wemakepass.data.model.dto;

/**
 * - 객관식 문항 1개에 대한 답과 해설 데이터를 가지는 DTO 클래스다.
 * - 이 데이터는 서버 DB에 문제 순서와 같은 순서로 저장되어 있고 조회될 때 고유 ID를 기준으로 정렬된 상태로 전달
 *  받는다. 따라서 데이터를 조회했을 때 List에 이 데이터가 있는 위치(index)가 문제의 번호로 사용된다.
 *
 * 때문에 각 데이터의 고유 ID를 기준으로 정렬된
 *  상태로 받게 된다. 따라서 이 데이터를 가지는 List의 index가 곧 문제의 문제 번호가 된다.
 *
 * @author BH-Ku
 * @since 2023-11-24
 */
public class ExamDocAnswerDTO {
    private int answer;
    private String explanation;

    public int getAnswer() {
        return answer;
    }

    public String getExplanation() {
        return explanation;
    }

    @Override
    public String toString() {
        return "ExamDocAnswerDTO{" +
                "answer=" + answer +
                ", explain='" + explanation + '\'' +
                '}';
    }
}
