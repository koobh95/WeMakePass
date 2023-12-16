package com.example.wemakepass.view.board.post.search;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.example.wemakepass.base.BaseViewModel;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.model.dto.response.PostPageResponse;
import com.example.wemakepass.data.pref.AppDataPreferences;
import com.example.wemakepass.repository.PostRepository;
import com.example.wemakepass.repository.pref.SearchLogRepository;

import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * PostSearchFragment의 ViewModel 클래스.
 *
 * @author BH-Ku
 * @since 2023-12-15
 */
public class PostSearchViewModel extends BaseViewModel {
    private SingleLiveEvent<String> keywordKeywordLiveData;
    private Disposable searchDisposable;

    private final SearchLogRepository searchLogRepository;
    private final PostRepository postRepository;

    private final int MINIMUM_KEYWORD_LENGTH = 2;

    public PostSearchViewModel() {
        searchLogRepository = new SearchLogRepository(AppDataPreferences.KEY_POST_SEARCH_LOG);
        postRepository = new PostRepository(getNetworkErrorLiveData());
    }

    /**
     *
     * @param searchType
     * @param pageNo
     * @param boardNo
     * @param category
     */
    public void search(int searchType, int pageNo, long boardNo, String category) {
        if(searchDisposable != null && !searchDisposable.isDisposed())
            searchDisposable.dispose();

        final String keyword = keywordKeywordLiveData.getValue();
        searchLogRepository.addLog(keyword);
        searchDisposable = postRepository.requestSearch(searchType, pageNo, boardNo, category,
                keyword);
        addDisposable(searchDisposable);
    }

    /**
     * 특정 검색 기록을 삭제한다.
     *
     * @param removeIndex
     */
    public void deleteLog(int removeIndex) {
        searchLogRepository.deleteLog(removeIndex);
    }

    /**
     * 검색 기록 목록을 모두 삭제한다.
     */
    public void deleteLogAll() {
        searchLogRepository.clear();
    }

    public boolean isValidKeyword() {
        final String keyword = keywordKeywordLiveData.getValue();
        if(TextUtils.isEmpty(keyword)) {
            systemMessageLiveData.setValue("검색어를 입력해주세요.");
            return false;
        }

        if(keyword.length() < MINIMUM_KEYWORD_LENGTH){
            systemMessageLiveData.setValue("검색어는 최소 " + MINIMUM_KEYWORD_LENGTH +
                    "자 이상 입력해주세요.");
            return false;
        }
        return true;
    }

    public SingleLiveEvent<String> getKeywordLiveData() {
        if(keywordKeywordLiveData == null)
            keywordKeywordLiveData = new SingleLiveEvent<>();
        return keywordKeywordLiveData;
    }

    public SingleLiveEvent<PostPageResponse> getPostResponseLiveData() {
        return postRepository.getPostResponseLiveData();
    }

    public SingleLiveEvent<List<String>> getSearchLogListLiveData() {
        return searchLogRepository.getSearchLogListLiveData();
    }
}
