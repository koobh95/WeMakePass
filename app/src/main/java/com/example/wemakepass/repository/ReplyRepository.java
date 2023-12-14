package com.example.wemakepass.repository;

import android.util.Log;

import com.example.wemakepass.base.BaseRepository;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.model.dto.ReplyDTO;
import com.example.wemakepass.data.model.dto.request.ReplyWriteRequest;
import com.example.wemakepass.data.model.vo.ErrorResponse;
import com.example.wemakepass.network.api.ReplyAPI;
import com.example.wemakepass.network.client.WmpClient;
import com.example.wemakepass.network.util.ErrorResponseConverter;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 댓글 관련 데이터를 조회, 작성하는 역할을 수행하는 Repository다.
 *
 * @author BH-Ku
 * @since 2023-11-30
 */
public class ReplyRepository extends BaseRepository {
    private SingleLiveEvent<List<ReplyDTO>> replyListLiveData; // 댓글 목록
    private SingleLiveEvent<Boolean> writeSuccessfullyLiveData; // 댓글 작성 성공 여부
    private SingleLiveEvent<Boolean> deleteSuccessfullyLiveData; // 댓글 작성 성공 여부

    private final ReplyAPI replyAPI;

    private final String TAG = "TAG_ReplyRepository";

    public ReplyRepository(SingleLiveEvent<ErrorResponse> networkErrorLiveData) {
        super(networkErrorLiveData);
        replyAPI = WmpClient.getRetrofit().create(ReplyAPI.class);
    }

    /**
     * 특정 게시글에 대한 댓글 목록을 조회한다.
     *
     * @param postNo 조회할 게시글의 식별 번호
     * @return
     */
    public Disposable requestReplyList(long postNo) {
        return replyAPI.replyList(postNo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if(response.isSuccessful()) {
                        replyListLiveData.setValue(response.body());
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
     * 특정 게시글에 댓글을 쓰기를 요청한다.
     *
     * @param replyWriteRequest
     * @return
     */
    public Disposable requestWrite(ReplyWriteRequest replyWriteRequest) {
        Log.d(TAG, replyWriteRequest.toString());
        return replyAPI.write(replyWriteRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if(response.isSuccessful()) {
                        writeSuccessfullyLiveData.setValue(true);
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
     * 특정 댓글의 삭제를 요청한다.
     *
     * @param replyNo 삭제할 댓글의 식별 번호
     * @return
     */
    public Disposable requestDelete(long replyNo) {
        return replyAPI.delete(replyNo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if(response.isSuccessful()) {
                        deleteSuccessfullyLiveData.setValue(true);
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

    public SingleLiveEvent<Boolean> getWriteSuccessfullyLiveData() {
        if(writeSuccessfullyLiveData == null)
            writeSuccessfullyLiveData = new SingleLiveEvent<>();
        return writeSuccessfullyLiveData;
    }

    public SingleLiveEvent<Boolean> getDeleteSuccessfullyLiveData() {
        if(deleteSuccessfullyLiveData == null)
            deleteSuccessfullyLiveData = new SingleLiveEvent<>();
        return deleteSuccessfullyLiveData;
    }

    public SingleLiveEvent<List<ReplyDTO>> getReplyListLiveData() {
        if(replyListLiveData == null)
            replyListLiveData = new SingleLiveEvent<>();
        return replyListLiveData;
    }
}
