package com.example.wemakepass.repository;

import android.util.Log;

import com.example.wemakepass.base.BaseRepository;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.model.dto.JmInfoDTO;
import com.example.wemakepass.data.model.vo.ErrorResponse;
import com.example.wemakepass.network.api.JmAPI;
import com.example.wemakepass.network.client.WmpClient;
import com.example.wemakepass.network.util.ErrorResponseConverter;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 종목 정보와 관련된 네트워크 작업을 처리하는 레포지토리
 *
 * @author BH-Ku
 * @since 2023-11-08
 */
public class JmRepository extends BaseRepository {
    private JmAPI jmAPI;
    private List<JmInfoDTO> jmInfoList;
    private SingleLiveEvent<List<JmInfoDTO>> jmInfoListLiveData;

    private final String TAG = "TAG_JmRepository";

    public JmRepository(SingleLiveEvent<ErrorResponse> networkErrorLiveData) {
        super(networkErrorLiveData);
        jmAPI = WmpClient.getRetrofit().create(JmAPI.class);
    }

    /**
     * 특정 Keyword와 일치하는 데이터들을 조회한다.
     *
     * @param keyword 검색어
     * @return
     */
    public Disposable search(String keyword){
        return jmAPI.findJmList(keyword)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if(response.isSuccessful()) {
                        jmInfoList = response.body();
                        jmInfoListLiveData.setValue(jmInfoList);
                    } else {
                        ErrorResponse errorResponse = ErrorResponseConverter.parseError(response);
                        networkErrorLiveData.setValue(errorResponse);
                        Log.d(TAG, errorResponse.toString());
                    }
                }, t -> {
                    networkErrorLiveData.setValue(ErrorResponse.ofConnectionFailed());
                    Log.d(TAG, networkErrorLiveData.getValue().toString());
                });
    }

    public SingleLiveEvent<List<JmInfoDTO>> getJmInfoListLiveData() {
        if(jmInfoListLiveData == null)
            jmInfoListLiveData = new SingleLiveEvent<>();
        return jmInfoListLiveData;
    }
}