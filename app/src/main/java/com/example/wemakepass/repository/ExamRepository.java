package com.example.wemakepass.repository;

import android.util.Log;

import com.example.wemakepass.base.BaseRepository;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.model.dto.ExamDocAnswerDTO;
import com.example.wemakepass.data.model.dto.ExamDocQuestionDTO;
import com.example.wemakepass.data.model.dto.ExamResultDTO;
import com.example.wemakepass.data.model.vo.ErrorResponse;
import com.example.wemakepass.network.api.ExamAPI;
import com.example.wemakepass.network.client.WmpClient;
import com.example.wemakepass.network.util.ErrorResponseConverter;

import java.time.LocalDate;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 필기, 실기 등의 문제, 답안 데이터를 조회하거나 시험 결과를 전송하는데 사용되는 Repository다.
 *
 * @author BH-Ku
 * @since 2023-11-23
 */
public class ExamRepository extends BaseRepository {
    private SingleLiveEvent<List<ExamDocQuestionDTO>> examDocQuestionListLiveData;
    private SingleLiveEvent<List<ExamDocAnswerDTO>> examDocAnswerListLiveData;

    private final ExamAPI examAPI;

    private final String TAG = "TAG_ExamRepository";

    public ExamRepository(SingleLiveEvent<ErrorResponse> networkErrorLiveData) {
        super(networkErrorLiveData);
        examAPI = WmpClient.getRetrofit().create(ExamAPI.class);
    }

    /**
     * 특정 필기(객관식) 시험의 문제 데이터를 요청한다.
     *
     * @param examId 요청할 시험의 식별값
     * @return
     */
    public Disposable requestExamDocQuestionList(long examId) {
        return examAPI.examDocQuestionList(examId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if(response.isSuccessful()){
                        examDocQuestionListLiveData.setValue(response.body());
                    } else {
                        ErrorResponse errorResponse = ErrorResponseConverter.parseError(response);
                        networkErrorLiveData.setValue(errorResponse);
                        Log.d(TAG, errorResponse.toString());
                    }
                }, t -> {
                    networkErrorLiveData.setValue(ErrorResponse.ofConnectionFailed());
                    Log.d(TAG, networkErrorLiveData.getValue().toString());
                    t.printStackTrace();
                });
    }

    /**
     * 특정 필기(객관식) 시험의 답안 데이터를 요청한다.
     *
     * @param examId 요청할 시험의 식별값
     * @return
     */
    public Disposable requestExamDocAnswerList(long examId){
        return examAPI.examDocAnswerList(examId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if(response.isSuccessful()){
                        examDocAnswerListLiveData.setValue(response.body());
                    } else {
                        ErrorResponse errorResponse = ErrorResponseConverter.parseError(response);
                        networkErrorLiveData.setValue(errorResponse);
                        Log.d(TAG, errorResponse.toString());
                    }
                }, t -> {
                    networkErrorLiveData.setValue(ErrorResponse.ofConnectionFailed());
                    Log.d(TAG, networkErrorLiveData.getValue().toString());
                    t.printStackTrace();
                });

    }

    /**
     * 시험이 끝났을 때 결과를 서버로 전송한다.
     *
     * @param examResultDTO 시험 결과 데이터
     * @return
     */
    public Disposable requestSaveExamResult(ExamResultDTO examResultDTO) {
        return examAPI.saveExamResult(examResultDTO)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if(response.isSuccessful()){
                        Log.d(TAG, response.body());
                    } else {
                        ErrorResponse errorResponse = ErrorResponseConverter.parseError(response);
                        networkErrorLiveData.setValue(errorResponse);
                        Log.d(TAG, errorResponse.toString());
                    }
                }, t -> {
                    networkErrorLiveData.setValue(ErrorResponse.ofConnectionFailed());
                    Log.d(TAG, networkErrorLiveData.getValue().toString());
                    t.printStackTrace();
                });
    }

    public SingleLiveEvent<List<ExamDocQuestionDTO>> getExamDocQuestionListLiveData() {
        if(examDocQuestionListLiveData == null) {
            examDocQuestionListLiveData = new SingleLiveEvent<>();
            examDocAnswerListLiveData = new SingleLiveEvent<>(); // 별도로 Observe하지 않으니 같이 초기화.
        }
        return examDocQuestionListLiveData;
    }

    public SingleLiveEvent<List<ExamDocAnswerDTO>> getExamDocAnswerListLiveData() {
        return examDocAnswerListLiveData;
    }
}
