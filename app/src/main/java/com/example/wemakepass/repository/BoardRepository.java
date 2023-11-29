package com.example.wemakepass.repository;

import android.util.Log;

import com.example.wemakepass.annotations.LoginRequired;
import com.example.wemakepass.base.BaseRepository;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.model.dto.BoardDTO;
import com.example.wemakepass.data.model.vo.ErrorResponse;
import com.example.wemakepass.network.api.BoardAPI;
import com.example.wemakepass.network.client.WmpClient;
import com.example.wemakepass.network.util.ErrorResponseConverter;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 게시판 정보와 관련된 네트워크 작업을 처리하는 Repository
 *
 * @author BH-Ku
 * @since 2023-11-29
 */
public class BoardRepository extends BaseRepository {
    private BoardAPI boardAPI;
    private SingleLiveEvent<List<BoardDTO>> boardListLiveData;

    private final String TAG = "TAG_BoardRepository";

    public BoardRepository(SingleLiveEvent<ErrorResponse> networkErrorLiveData) {
        super(networkErrorLiveData);
        boardAPI = WmpClient.getRetrofit().create(BoardAPI.class);
    }

    /**
     * 특정 Keyword와 일치하는 데이터들을 조회한다.
     *
     * @param keyword 검색어
     * @return
     */
    @LoginRequired
    public Disposable requestSearch(String keyword) {
        return boardAPI.search(keyword)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if(response.isSuccessful()) {
                        boardListLiveData.setValue(response.body());
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

    public SingleLiveEvent<List<BoardDTO>> getBoardListLiveData() {
        if(boardListLiveData == null)
            boardListLiveData = new SingleLiveEvent<>();
        return boardListLiveData;
    }
}
