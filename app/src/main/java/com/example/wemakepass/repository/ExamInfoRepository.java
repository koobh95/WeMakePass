package com.example.wemakepass.repository;

import android.util.Log;

import com.example.wemakepass.base.BaseRepository;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.model.dto.ExamInfoDTO;
import com.example.wemakepass.data.model.dto.JmInfoDTO;
import com.example.wemakepass.data.model.vo.ErrorResponse;
import com.example.wemakepass.network.api.ExamInfoAPI;
import com.example.wemakepass.network.client.WmpClient;
import com.example.wemakepass.network.util.ErrorResponseConverter;

import java.util.EnumMap;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 시험 정보와 관련된 데이터를 조회하는 네트워크 작업을 수행하는 Repository
 *
 * @author BH-Ku
 * @since 2023-11-14
 */
public class ExamInfoRepository extends BaseRepository {
    private SingleLiveEvent<List<ExamInfoDTO>> examInfoListLiveData;

    private final ExamInfoAPI examInfoAPI;

    private final String TAG = "TAG_ExamInfoRepository";

    public ExamInfoRepository(SingleLiveEvent<ErrorResponse> networkErrorLiveData) {
        super(networkErrorLiveData);
        examInfoAPI = WmpClient.getRetrofit().create(ExamInfoAPI.class);
    }

    /**
     * 특정 종목 코드에 해당하는 시험 정보 목록을 불러 온다.
     *
     * @param jmCode
     * @return
     */
    public Disposable requestExamInfoList(String jmCode) {
        return examInfoAPI.examInfoList(jmCode)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if(response.isSuccessful()){
                        examInfoListLiveData.setValue(response.body());
                    } else {
                        ErrorResponse errorResponse = ErrorResponseConverter.parseError(response);
                        networkErrorLiveData.setValue(errorResponse);
                        Log.e(TAG, errorResponse.toString());
                    }
                }, t -> {
                    networkErrorLiveData.setValue(ErrorResponse.ofConnectionFailed());
                    Log.e(TAG, networkErrorLiveData.getValue().toString());
                });
    }

    public SingleLiveEvent<List<ExamInfoDTO>> getExamInfoListLiveData() {
        if(examInfoListLiveData == null)
            examInfoListLiveData = new SingleLiveEvent<>();
        return examInfoListLiveData;
    }
}
