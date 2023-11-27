package com.example.wemakepass.task.exam;

import android.util.Log;

import com.example.wemakepass.data.enums.ExamAnswerState;
import com.example.wemakepass.data.model.data.ExamDocAnswerModel;
import com.example.wemakepass.data.model.data.ExamSubjectResultModel;
import com.example.wemakepass.data.model.dto.ExamDocAnswerDTO;
import com.example.wemakepass.data.model.dto.ExamDocQuestionDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * 필기 시험을 채점하는 클래스.
 *
 * @author BH-Ku
 * @since 2023-11-24
 */
public class ExamDocScoringTask {
    private final List<ExamDocQuestionDTO> questionList; // 문제 목록
    private final List<ExamDocAnswerDTO> answerList; // 답안 목록
    private final List<ExamDocAnswerModel> myAnswerList; // 사용자가 작성한 답안 목록
    private final List<ExamSubjectResultModel> subjectResultList; // 과목 별 채점 결과를 저장할 목록

    // 순서대로 정답 수, 오답 수, 미입력 수
    private int correctAnswerCount, incorrectAnswerCount, unWriteAnswerCount;
    private int score; // 최종 점수
    private String reasonForRejection; // 불합격 시 불합격 사유

    private final String TAG = "TAG_ExamDocScoringTask";

    public ExamDocScoringTask(List<ExamDocQuestionDTO> questionList,
                              List<ExamDocAnswerDTO> answerList,
                              List<ExamDocAnswerModel> myAnswerList) {
        this.questionList = questionList;
        this.answerList = answerList;
        this.myAnswerList = myAnswerList;
        subjectResultList = new ArrayList<>();
    }

    /**
     * 외부에서 호출되어 채점을 수행한다.
     */
    public void scoring() {
        ExamSubjectResultModel subject =
                new ExamSubjectResultModel(questionList.get(0).getSubjectName());
        subjectResultList.add(subject);

        for(int i = 0; i < questionList.size(); i++) {
            final ExamDocQuestionDTO question = questionList.get(i);
            final ExamDocAnswerDTO answer = answerList.get(i);
            final ExamDocAnswerModel myAnswer = myAnswerList.get(i);

            if(!subject.getSubjectName().equals(question.getSubjectName())){
                subject = new ExamSubjectResultModel(question.getSubjectName());
                subjectResultList.add(subject);
            }

            subject.increaseNumOfQuestion();
            subject.addTotalScore(question.getScore());

            if(myAnswer.getAnswer() == -1){ // 미입력
                unWriteAnswerCount++;
                myAnswer.setAnswerState(ExamAnswerState.UN_WRITE_ANSWER);
            } else if(answer.getAnswer() == myAnswer.getAnswer()+1){ // 정답
                subject.addScore(question.getScore());
                myAnswer.setAnswerState(ExamAnswerState.CORRECT_ANSWER);
                correctAnswerCount++;
                //
            } else { // 오답
                incorrectAnswerCount++;
                myAnswer.setAnswerState(ExamAnswerState.INCORRECT_ANSWER);
            }
        }

        int totalScore = getTotalScore(); // 최종 점수
        int myScore = getMyScore(); // 획득한 총 점수
        score = ((int) ((double)myScore / (double)totalScore * 100.0)); // 백분율 계산
        initReasonForRejection();
    }

    /**
     * 점수를 확인하여 불합격 여부를 확인한다.
     */
    private void initReasonForRejection(){
        if(score < 60){
            reasonForRejection = "평균 미달";
            return;
        }

        for(ExamSubjectResultModel model : subjectResultList) {
            Log.d(TAG, "Subject Score=" + model.getPercentageScore());
            if (model.getPercentageScore() < 40) {
                reasonForRejection = "과락";
                return;
            }
        }
    }

    /**
     * 채점이 완료된 각 과목에서 총점을 더해 시험의 총점을 구한다.
     * @return 시험의 총 점수
     */
    private int getTotalScore() {
        int totalScore = 0;
        for(ExamSubjectResultModel item : subjectResultList)
            totalScore += item.getTotalScore();
        return totalScore;
    }

    /**
     * 채점이 완료된 각 과목에서 횟득한 점수의 총점을 더해 최종 회득 점수를 반환한다.
     * @return 최종 획득 점수
     */
    private int getMyScore() {
        int totalMyScore = 0;
        for(ExamSubjectResultModel item : subjectResultList)
            totalMyScore += item.getScore();
        return totalMyScore;
    }

    public List<ExamSubjectResultModel> getSubjectResultList() {
        return subjectResultList;
    }

    public int getScore() {
        return score;
    }

    public int getCorrectAnswerCount() {
        return correctAnswerCount;
    }

    public int getIncorrectAnswerCount() {
        return incorrectAnswerCount;
    }

    public int getUnWriteAnswerCount() {
        return unWriteAnswerCount;
    }

    public String getReasonForRejection() {
        return reasonForRejection;
    }
}
