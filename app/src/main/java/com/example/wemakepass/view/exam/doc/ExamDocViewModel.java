package com.example.wemakepass.view.exam.doc;

import com.example.wemakepass.base.BaseViewModel;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.config.AppConfig;
import com.example.wemakepass.data.model.data.ExamDocAnswerModel;
import com.example.wemakepass.data.model.dto.ExamDocAnswerDTO;
import com.example.wemakepass.data.model.dto.ExamDocQuestionDTO;
import com.example.wemakepass.data.model.dto.ExamResultDTO;
import com.example.wemakepass.repository.ExamRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * ExamDocFragment의 ViewModel 클래스.
 *
 * @author BH-Ku
 * @since 2023-11-22
 */
public class ExamDocViewModel extends BaseViewModel {
    private SingleLiveEvent<String> timerLiveData;
    private SingleLiveEvent<Boolean> timerRunningLiveData;
    private Disposable timerDisposable;

    private final ExamRepository examRepository;

    private List<ExamDocAnswerModel> myAnswerList; // 사용자가 입력한 정답
    private int secondTimeLimit; // 시험의 제한 시간 단위(분)를 초 단위로 변경한 값
    private long elapsedTime; // 시험을 다 치루는데 소요된 시간

    private final String TAG = "TAG_ExamDocViewModel";

    public ExamDocViewModel() {
        examRepository = new ExamRepository(getNetworkErrorLiveData());
    }

    /**
     * Fragment의 생성주기 onViewCreated에서 이 메서드가 호출된다. 시험에 대한 문제, 답안을 로딩한다.
     *
     * @param examId 로딩하고자 하는 시험의 식별값
     */
    public void loadExamData(long examId) {
        addDisposable(examRepository.requestExamDocQuestionList(examId));
        addDisposable(examRepository.requestExamDocAnswerList(examId));
    }

    /**
     * 시험을 모두 치뤘을 때 시험 결과를 서버로 전송한다.
     * @param examId 시험의 식별값
     * @param reasonForRejection 불합격 사유, 합격했다면 null
     * @param score 최종 획득한 점수
     */
    public void saveResultData(long examId, String reasonForRejection, int score) {
        ExamResultDTO examResultDTO = new ExamResultDTO.ExamResultBuilder()
                .setExamId(examId)
                .setUserId(AppConfig.UserPreference.getUserId())
                .setReasonForRejection(reasonForRejection)
                .setElapsedTime(elapsedTime)
                .setScore(score)
                .setAnswerSheet(createAnswerSheet())
                .build();
        addDisposable(examRepository.requestSaveExamResult(examResultDTO));
    }

    /**
     * - 문제가 로딩되었을 때 호출되며 타이머를 실행한다.
     * -
     * @param timeLimit 응시하고 있는 시험의 제한 시간(분 단위)
     */
    public void startTimer(int timeLimit) {
        secondTimeLimit = timeLimit * 60; // 초 단위로 변경
        timerDisposable = Observable.interval(1000L, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .take(secondTimeLimit + 1) // 00초까지 시간을 세기 위해서 1초 추가
                .subscribe(second -> {
                    elapsedTime = second; // 소요 시간 누적
                    long remainingSeconds = secondTimeLimit - second; // ??
                    timerLiveData.setValue(String.format(Locale.KOREA,
                            "남은 시간\n%d:%02d:%02d",
                            remainingSeconds / 3600,
                            (remainingSeconds % 3600) / 60,
                            remainingSeconds % 60));
                }, t -> { }, () -> timerRunningLiveData.setValue(false));
    }

    /**
     *  타이머(Observable)가 시간이 다해서 종료되거나 사용자가 직접 "채점하기"를 통해 종료할 경우 이 메서드가
     * 호출된다. 작업을 종료한다.
     */
    public void terminationOfTimer(){
        if(timerDisposable != null && timerDisposable.isDisposed())
            return;

        timerDisposable.dispose();
        timerLiveData.setValue("시험 종료");
    }

    /**
     * - 사용자가 입력한 답안을 서버에 제출하기 위해서 하나의 문자열로 만든다.
     * - 답안이 미입력된 상태라면 -1이, 답이 입력되어 있다면 인덱스를 기준으로 입력되어 있기 때문에 실제 값(1~4)
     *  보다 1씩 적은 값(0~3)을 가지고 있다. 답안 문자열을 만들 때 모든 값에 1을 더해 0이 미표기 값이 되도록
     *  하는데 이렇게 하는 이유는 하이픈 문자('-')를 제거하여 문항의 개수와 답안 문자열의 길이와 같게 만들기
     *  위함이다.(나중에 파싱할 때 수월해지기 때문)
     */
    private String createAnswerSheet() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < myAnswerList.size(); i++)
            sb.append(myAnswerList.get(i).getAnswer()+1);
        return sb.toString();
    }

    /**
     *  사용자가 선택한 답안을 저장하기 위한 리스트를 초기화한다. 리스트를 초기화할 때 리스트의 크기가 이미 정해져
     * 있기 때문에 불필요한 메모리 낭비를 줄이기 위해 파라미터로 크기를 받아 초기화한다.
     *
     * @param size 문제의 총 개수
     */
    public void initAnswerList(int size) {
        myAnswerList = new ArrayList<>(size);
        for(int i = 0; i < size; i++)
            myAnswerList.add(new ExamDocAnswerModel());
    }

    /**
     * - 정답이 선택되지 않은 문항(answer == -1)을 카운팅하여 반환한다.
     * - 시험을 채점하려 할 때 아직 풀지 않은 문제가 있는지 판단하기 위한 목적으로 사용된다.
     *
     * @return 정답이 선택되지 않은 문항의 수
     */
    public int getIncorrectAnswerCount() {
        int count = 0;
        for(ExamDocAnswerModel item : myAnswerList)
            if(item.getAnswer() == -1)
                count++;
        return count;
    }

    public List<ExamDocAnswerModel> getMyAnswerList() {
        return myAnswerList;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public SingleLiveEvent<String> getTimerLiveData() {
        if(timerLiveData == null)
            timerLiveData = new SingleLiveEvent<>();
        return timerLiveData;
    }

    public SingleLiveEvent<Boolean> getTimerRunningLiveData() {
        if(timerRunningLiveData == null)
            timerRunningLiveData = new SingleLiveEvent<>(true);
        return timerRunningLiveData;
    }

    public SingleLiveEvent<List<ExamDocQuestionDTO>> getExamQuestionListLiveData() {
        return examRepository.getExamDocQuestionListLiveData();
    }

    public SingleLiveEvent<List<ExamDocAnswerDTO>> getExamDocAnswerListLiveData() {
        return examRepository.getExamDocAnswerListLiveData();
    }
}
