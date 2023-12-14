package com.example.wemakepass.repository;

import android.util.Log;

import com.example.wemakepass.annotations.LoginRequired;
import com.example.wemakepass.base.BaseRepository;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.enums.ErrorCode;
import com.example.wemakepass.data.model.dto.PostDetailDTO;
import com.example.wemakepass.data.model.dto.request.PostWriteRequest;
import com.example.wemakepass.data.model.dto.response.PostPageResponse;
import com.example.wemakepass.data.model.vo.ErrorResponse;
import com.example.wemakepass.network.api.PostAPI;
import com.example.wemakepass.network.client.WmpClient;
import com.example.wemakepass.network.util.ErrorResponseConverter;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 게시글 관련 데이터를 조회, 추가하는 역할을 수행하는 Repository다.
 *
 * @author BH-Ku
 * @since 2023-11-30
 */
public class PostRepository extends BaseRepository {
    private SingleLiveEvent<PostPageResponse> postResponseLiveData; // 게시글 목록
    private SingleLiveEvent<PostDetailDTO> postDetailLiveData; // 게시글 상세 정보
    private SingleLiveEvent<Boolean> writeSuccessfullyLiveData; // 게시글 작성 성공 여부

    private PostAPI postAPI;

    private final String TAG = "TAG_PostRepository";

    public PostRepository(SingleLiveEvent<ErrorResponse> networkErrorLiveData) {
        super(networkErrorLiveData);
        postAPI = WmpClient.getRetrofit().create(PostAPI.class);

    }

    /**
     * - 특정 게시판에서 페이지 단위로 게시글을 조회한다.
     * - 다른 카테고리를 선택하면 그와 동시에 카테고리에 해당하는 게시글이 조회되는데 이 때 TabLayout의 Tab
     *  변경 Animation이 버벅이는 현상을 확인했다. Animation이 완료되기 까지 약 400ms~500ms가 소요되는데
     *  데이터 조회에서 RecyclerView가 업데이트되기 까지 평균 100ms가 걸리지 않는다. 사실상 TabLayout의 탭
     *  변경 Animation이 발생하는 와중에 RecyclerView 갱신이 동시에 이루어지는 것이다. 이를 해결하기 위해서
     *  카테고리 변경 후 첫 조회일 경우 즉, pageNo가 0일 경우 400ms의 딜레이를 강제로 발생시켜서 TabLayout의
     *  Animation이 수행될 시간을 벌어준다.
     *
     * @param boardNo 게시판 식별 번호
     * @param pageNo 페이지 번호
     * @return
     */
    public Disposable requestPostList(long boardNo, int pageNo) {
        long delay = pageNo == 0 ? 400 : 0;
        return postAPI.postList(boardNo, pageNo)
                .delay(delay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if(response.isSuccessful()) {
                        postResponseLiveData.setValue(response.body());
                    } else {
                        ErrorResponse errorResponse = ErrorResponseConverter.parseError(response);
                        networkErrorLiveData.setValue(errorResponse);
                        Log.d(TAG, errorResponse.toString());
                    }
                }, t -> {
                    ErrorResponse errorResponse = new ErrorResponse(
                            ErrorCode.POST_LIST_LOADING_FAILED.name(),
                            "게시글 목록을 읽어오는데 실패했습니다.");
                    networkErrorLiveData.setValue(errorResponse);
                    Log.d(TAG, networkErrorLiveData.getValue().toString());
                    t.printStackTrace();
                });
    }

    /**
     * 특정 게시판, 특정 카테고리에서 페이지 단위로 게시글을 조회한다.
     *
     * @param boardNo 게시판 식별 번호
     * @param pageNo 조회할 페이지 번호
     * @param category 조회할 게시판의 카테고리 이름
     * @return
     */
    public Disposable requestPostListByCategory(long boardNo, int pageNo, String category){
        long delay = pageNo == 0 ? 400 : 0;
        return postAPI.postListByCategory(boardNo, pageNo, category)
                .delay(delay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if(response.isSuccessful()) {
                        postResponseLiveData.setValue(response.body());
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
     * 새로운 게시글을 등록하기 위해 관련 데이터를 서버로 전송한다.
     *
     * @param request 게시글 작성에 필요한 데이터를 가진 객체
     * @return
     */
    @LoginRequired
    public Disposable requestWrite(PostWriteRequest request) {
        return postAPI.write(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    writeSuccessfullyLiveData.setValue(response.isSuccessful());
                }, t -> {
                    writeSuccessfullyLiveData.setValue(false);
                    Log.d(TAG, networkErrorLiveData.getValue().toString());
                    t.printStackTrace();
                });
    }

    /**
     * 게시판 > 게시글 목록에서 특정 게시글이 선택되었을 때 그 게시글을 화면에 표시하기 위해 관련 정보를 요창한다.
     *
     * @param postNo 데이터를 요청할 게시글의 식별 번호
     * @return
     */
    public Disposable requestPostDetail(long postNo) {
        return postAPI.postDetail(postNo)
                .delay(300, TimeUnit.MILLISECONDS) // Fragment Animation 실행 시간을 위한 딜레이
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if(response.isSuccessful()) {
                        postDetailLiveData.setValue(response.body());
                    } else {
                        ErrorResponse errorResponse = ErrorResponseConverter.parseError(response);
                        networkErrorLiveData.setValue(errorResponse);
                        Log.d(TAG, errorResponse.toString());
                    }
                }, t -> {
                    ErrorResponse errorResponse = new ErrorResponse(
                            ErrorCode.POST_LOADING_FAILED_NETWORK_ERROR.name(),
                            "게시글을 읽어 오지 못했습니다.");
                    networkErrorLiveData.setValue(errorResponse);
                    Log.d(TAG, networkErrorLiveData.getValue().toString());
                    t.printStackTrace();
                });
    }

    public SingleLiveEvent<PostPageResponse> getPostResponseLiveData() {
        if(postResponseLiveData == null)
            postResponseLiveData = new SingleLiveEvent<>();
        return postResponseLiveData;
    }

    public SingleLiveEvent<PostDetailDTO> getPostDetailLiveData() {
        if(postDetailLiveData == null)
            postDetailLiveData = new SingleLiveEvent<>();
        return postDetailLiveData;
    }

    public SingleLiveEvent<Boolean> getWriteSuccessfullyLiveData() {
        if(writeSuccessfullyLiveData == null)
            writeSuccessfullyLiveData = new SingleLiveEvent<>();
        return writeSuccessfullyLiveData;
    }
}
