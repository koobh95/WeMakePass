package com.example.wemakepass.view.exam.select;

import androidx.lifecycle.ViewModel;

import com.example.wemakepass.base.BaseViewModel;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.repository.ExamInfoRepository;

import java.util.List;

public class ExamSelectViewModel extends BaseViewModel {
    private final ExamInfoRepository examInfoRepository;

    public ExamSelectViewModel() {
        examInfoRepository = new ExamInfoRepository(getNetworkErrorLiveData());
    }
}
