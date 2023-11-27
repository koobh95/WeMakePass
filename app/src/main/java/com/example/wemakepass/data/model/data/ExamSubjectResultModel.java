package com.example.wemakepass.data.model.data;

/**
 *  객관식 시험은 과목별로 데이터가 나누어져 있고 과목별 점수에 따라 과락 여부를 따져 합격의 유무를 결정해야 한다.
 * 따라서 채점이 이루어졌을 때 각 과목에 대한 별도의 확인이 필요하기에 채점 결과 저장 및 확인에 이 Model 클래스를
 * 사용한다.
 *
 * @author BH-Ku
 * @since 2023-11-24
 */
public class ExamSubjectResultModel {
    private String subjectName; // 과목 이름
    private int numOfQuestion; // 과목의 문항 수
    private int totalScore; // 과목의 총 점수
    private int score; // 획득한 시험 점수

    public ExamSubjectResultModel(String subjectName) {
        this.subjectName = subjectName;
        numOfQuestion = 0;
        totalScore = 0;
        score = 0;
    }

    /**
     * 과목의 문항 수를 1 증가시킨다.
     */
    public void increaseNumOfQuestion(){
        numOfQuestion++;
    }

    /**
     * 과목의 총 점수를 누적시킨다.
     *
     * @param score 누적시킬 점수
     */
    public void addTotalScore(int score){
        totalScore += score;
    }

    /**
     * 획득한 시험 점수를 누적시킨다.
     *
     * @param score 획득한 점수
     */
    public void addScore(int score) {
        this.score += score;
    }

    /**
     * 과목의 이름을 반환한다.
     *
     * @return 과목명
     */
    public String getSubjectName() {
        return subjectName;
    }

    /**
     * 과목의 총 점수를 반환한다.
     *
     * @return 과목의 총 점수
     */
    public int getTotalScore() {
        return totalScore;
    }

    /**
     * 획득한 총 점수를 반환한다.
     *
     * @return
     */
    public int getScore() {
        return score;
    }

    /**
     * 총 점수와 획득한 점수의 백분율을 계산하여 반환한다.
     *
     * @return
     */
    public int getPercentageScore() {
        return ((int) ((double)score / (double)totalScore * 100.0));
    }

    @Override
    public String toString() {
        return "ExamSubjectResultModel{" +
                "subjectName='" + subjectName + '\'' +
                ", numOfQuestion=" + numOfQuestion +
                ", totalScore=" + totalScore +
                ", score=" + score +
                '}';
    }
}
