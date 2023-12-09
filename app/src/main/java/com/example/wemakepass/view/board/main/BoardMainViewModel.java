package com.example.wemakepass.view.board.main;

import com.example.wemakepass.base.BaseViewModel;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.model.dto.response.PostPageResponse;
import com.example.wemakepass.repository.BoardRepository;
import com.example.wemakepass.repository.PostRepository;

import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * BoardMainFragment의 ViewModel 클래스.
 *
 * @author BH-Ku
 * @since 2023-11-30
 */
public class BoardMainViewModel extends BaseViewModel {
    private BoardRepository boardRepository;
    private PostRepository postRepository;
    private Disposable postLoadingDisposable;

    private final String TAG = "TAG_BoardMainViewModel";

    public BoardMainViewModel() {
        boardRepository = new BoardRepository(getNetworkErrorLiveData());
        postRepository = new PostRepository(getNetworkErrorLiveData());
    }

    /**
     * 게시판이 가지는 카테고리 목록을 요청한다.
     *
     * @param boardNo 게시판의 식별 번호
     */
    public void loadCategory(long boardNo) {
        boardRepository.requestCategoryList(boardNo);
    }

    /**
     * - 데이터 초기 조회 혹은 추가 조회를 요청한다.
     * - 스크롤이 가장 아래에 있는 상태에서 위아래로 반복적으로 스크롤링하여 계속해서 추가 데이터를 요청하는 상황이
     *  발생하지 않도록 로딩이 수행 중일 경우 작업을 수행하지 않는다.
     *
     * @param boardNo 게시판의 식별 번호
     * @param pageNo 조회할 페이지 번호
     * @param categoryIdx TabLayout에서 선택된 조회하려는 카테고리
     */
    public void loadPosts(long boardNo, int pageNo, int categoryIdx) {
        if(postLoadingDisposable != null && !postLoadingDisposable.isDisposed())
            return;

        if(categoryIdx == 0){ // 전체
            postLoadingDisposable = postRepository.requestPostList(boardNo, pageNo);
        } else { // 카테고리
            String category = boardRepository.getCategoryListLiveData().getValue().get(categoryIdx-1);
            postLoadingDisposable = postRepository.requestPostListByCategory(boardNo, pageNo, category);
        }

        addDisposable(postLoadingDisposable);
    }

    public SingleLiveEvent<List<String>> getCategoryListLiveData() {
        return boardRepository.getCategoryListLiveData();
    }

    public SingleLiveEvent<PostPageResponse> getPostListLiveData() {
        return postRepository.getPostResponseLiveData();
    }
}
