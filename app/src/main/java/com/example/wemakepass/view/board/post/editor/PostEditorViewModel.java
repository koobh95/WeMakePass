package com.example.wemakepass.view.board.post.editor;

import android.text.TextUtils;

import com.example.wemakepass.base.BaseViewModel;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.config.AppConfig;
import com.example.wemakepass.data.model.dto.request.PostWriteRequest;
import com.example.wemakepass.repository.PostRepository;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * PostEditorFragment의 ViewModel 클래스.
 *
 * @author BH-Ku
 * @since 2023-12-10
 */
public class PostEditorViewModel extends BaseViewModel {
    private SingleLiveEvent<String> titleLiveData, contentLiveData;

    private Disposable writeDisposable;

    private final PostRepository postRepository;

    public PostEditorViewModel() {
        postRepository = new PostRepository(getNetworkErrorLiveData());
    }

    /**
     * - PostRepository에서 게시글 등록 요청 메서드를 호출한다.
     * - Disposable 객체를 사용하여 요청이 중복으로 발생하는 것을 방지한다.
     *
     * @param boardNo
     * @param category
     */
    public void writePost(long boardNo, String category) {
        if(writeDisposable != null && !writeDisposable.isDisposed())
            return;

        final String title = titleLiveData.getValue();
        final String content = contentLiveData.getValue();

        if(!isWritable(title, content))
            return;

        PostWriteRequest request = createPostWriteRequest(boardNo, category, title, content);
        writeDisposable = postRepository.requestWrite(request);
        addDisposable(writeDisposable);
    }

    /**
     * EditText에 바인딩된 LiveData(제목, 내용)들을 확인하여 게시글 등록 요청이 가능한 상태인지 판단한다.
     *
     * @param title 게시글 제목
     * @param content 게시글 내용
     * @return true = 게시글 작성 가능, false = 필수 항목이 누락되었음.
     */
    private boolean isWritable(String title, String content) {
        if(TextUtils.isEmpty(title)){
            systemMessageLiveData.setValue("글 제목을 입력해주세요.");
            return false;
        } else if(TextUtils.isEmpty(content)) {
            systemMessageLiveData.setValue("글 내용을 입력해주세요.");
            return false;
        }

        return true;
    }

    /**
     * 게시글 등록 요청에 필요한 데이터들을 PostWriteRequest 객체로 초기화화여 반환한다.
     *
     * @param boardNo 게시글이 등록될 게시판의 식별 번호
     * @param category 게시글의 카테고리
     * @param title 게시글의 제목
     * @param content 게시글의 내용
     * @return
     */
    private PostWriteRequest createPostWriteRequest(long boardNo, String category, String title,
                                                    String content) {
        return new PostWriteRequest(boardNo,
                AppConfig.UserPreference.getUserId(),
                category,
                title,
                content);
    }

    public SingleLiveEvent<String> getTitleLiveData() {
        if(titleLiveData == null)
            titleLiveData = new SingleLiveEvent<>();
        return titleLiveData;
    }

    public SingleLiveEvent<String> getContentLiveData() {
        if(contentLiveData == null)
            contentLiveData = new SingleLiveEvent<>();
        return contentLiveData;
    }

    public SingleLiveEvent<Boolean> getIsSuccessfullyWritingLiveData() {
        return postRepository.getIsSuccessfullyLiveData();
    }
}
