package com.example.wemakepass.repository;

import android.util.Log;

import com.example.wemakepass.base.BaseRepository;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.model.dto.PostDTO;
import com.example.wemakepass.data.model.vo.ErrorResponse;
import com.example.wemakepass.network.api.PostAPI;
import com.example.wemakepass.network.client.WmpClient;
import com.example.wemakepass.network.util.ErrorResponseConverter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 게시글 관련 데이터를 조회, 추가, 삭제하는 역할을 수행하는 Repository다.
 *
 * @author BH-Ku
 * @since 2023-11-30
 */
public class PostRepository extends BaseRepository {
    private SingleLiveEvent<List<PostDTO>> postListLiveData;

    private PostAPI postAPI;

    private final String TAG = "TAG_PostRepository";

    public PostRepository(SingleLiveEvent<ErrorResponse> networkErrorLiveData) {
        super(networkErrorLiveData);
        postAPI = WmpClient.getRetrofit().create(PostAPI.class);

    }

    /**
     * 특정 게시판에서 페이지 단위로 게시글을 조회한다.
     *
     * @param boardNo 게시판의 식별 번호
     * @param page 페이지 번호
     * @return
     */
    public Disposable requestPostList(long boardNo, int page) {
        return postAPI.postList(boardNo, page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if(response.isSuccessful()) {
                        setPostList(response.body());
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
     * 특정 게시판, 특정 카테고리에서 페이지 단위로 게시글을 조회한다.
     * @param boardNo
     * @param page
     * @param category
     * @return
     */
    public Disposable requestPostListByCategory(long boardNo, int page, String category){
        return postAPI.postListByCategory(boardNo, page, category)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if(response.isSuccessful()) {
                        setPostList(response.body());
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
     *  조회한 게시글 목록을 라이브 데이터에 세팅한다. 기존 리스트가 NULL일 경우 최초 로딩 혹은 카테고리 변경 후
     * 최초 로딩이라고 보고 그대로 리스트를 세팅한다. null이 아닌 경우 추가 로딩이라고 보고 기존 리스트에
     * 조회한 데이터를 추가하여 라이브 데이터에 세팅한다.
     *
     * @param lodedList
     */
    private void setPostList(List<PostDTO> lodedList) {
        List<PostDTO> oldList = postListLiveData.getValue();
        if(oldList == null){ // 처음 로딩 혹은 카테고리 변경 후 로딩
            postListLiveData.setValue(lodedList);
            return;
        }

        List<PostDTO> newList = new ArrayList<>(oldList.size() + lodedList.size());
        newList.addAll(oldList);
        newList.addAll(lodedList);
        postListLiveData.setValue(newList);
    }

    public SingleLiveEvent<List<PostDTO>> getPostListLiveData() {
        if(postListLiveData == null)
            postListLiveData = new SingleLiveEvent<>();
        return postListLiveData;
    }
}
