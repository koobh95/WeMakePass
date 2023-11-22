package com.example.wemakepass.view.exam.select;

import androidx.lifecycle.ViewModel;

import com.example.wemakepass.base.BaseViewModel;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.model.dto.ExamInfoDTO;
import com.example.wemakepass.repository.ExamInfoRepository;

import java.util.List;

/**
 * ExamSelectFragment의 ViewModel 클래스.
 *
 * @author BH-Ku
 * @since 2023-11-20
 */
public class ExamSelectViewModel extends BaseViewModel {
    private final ExamInfoRepository examInfoRepository;

    public ExamSelectViewModel() {
        examInfoRepository = new ExamInfoRepository(getNetworkErrorLiveData());
    }

    /**
     * 특정 종목이 가지는 시험 목록을 요청한다.
     * @param jmCode 시험 목록을 조회할 종목 코드
     */
    public void loadExamInfoList(String jmCode){
        addDisposable(examInfoRepository.requestExamInfoList(jmCode));
    }

    public SingleLiveEvent<List<ExamInfoDTO>> getExamInfoListLiveData() {
        return examInfoRepository.getExamInfoListLiveData();
    }
}
