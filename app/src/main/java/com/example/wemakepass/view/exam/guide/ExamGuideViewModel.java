package com.example.wemakepass.view.exam.guide;

import com.example.wemakepass.base.BaseViewModel;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.repository.ExamInfoRepository;

import java.util.List;

/**
 * ExamGuideFragment의 ViewModel 클래스.
 *
 * @author BH-Ku
 * @since 2023-11-21
 */
public class ExamGuideViewModel extends BaseViewModel {
    private ExamInfoRepository examInfoRepository;

    public ExamGuideViewModel(){
        examInfoRepository = new ExamInfoRepository(getNetworkErrorLiveData());
    }

    /**
     * 특정 시험의 과목 목록 조회를 요청한다.
     *
     * @param examId
     */
    public void loadSubjectList(int examId) {
        addDisposable(examInfoRepository.requestSubjectList(examId));
    }

    public SingleLiveEvent<List<String>> getSubjectListLiveData() {
        return examInfoRepository.getSubjectListLiveData();
    }
}
