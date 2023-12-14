package com.example.wemakepass.view.board.post.viewer;

import android.text.TextUtils;
import android.util.Log;

import com.example.wemakepass.base.BaseViewModel;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.config.AppConfig;
import com.example.wemakepass.data.model.dto.PostDetailDTO;
import com.example.wemakepass.data.model.dto.ReplyDTO;
import com.example.wemakepass.data.model.dto.request.ReplyWriteRequest;
import com.example.wemakepass.repository.PostRepository;
import com.example.wemakepass.repository.ReplyRepository;

import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * PostViewFragment의 ViewModel 클래스.
 *
 * @author BH-Ku
 * @since 2023-12-12
 */
public class PostViewerViewModel extends BaseViewModel {
    private SingleLiveEvent<String> replyContentLiveData; // 댓글 내용 EditText, Two-way binding

    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;

    private Disposable replyLoadingDisposable; // 댓글 로딩 작업
    private Disposable replyWriteDisposable; // 댓글 작성 작업
    private Disposable replyDeleteDisposable; // 댓글 삭제 작업

    private final int MAXIMUM_LENGTH_REPLY_CONTENT = 500; // 댓글 최대 길이
    private final String TAG = "TAG_PostViewerViewModel";

    public PostViewerViewModel() {
        postRepository = new PostRepository(getNetworkErrorLiveData());
        replyRepository = new ReplyRepository(getNetworkErrorLiveData());
    }

    /**
     * 화면에 표시할 게시글 정보를 요청한다.
     *
     * @param postNo 요청할 게시글의 식별 번호
     */
    public void requestPostDetail(long postNo) {
        postRepository.requestPostDetail(postNo);
    }

    /**
     * 특정 게시글의 댓글 목록을 요청한다.
     *
     * @param postNo 요청할 게시글의 식별 번호
     */
    public void requestReplyList(long postNo) {
        if(replyLoadingDisposable != null && !replyLoadingDisposable.isDisposed()) {
            systemMessageLiveData.setValue("처리 중입니다.");
            return;
        }
        replyLoadingDisposable = replyRepository.requestReplyList(postNo);
        addDisposable(replyLoadingDisposable);
    }

    /**
     * 댓글 쓰기를 요청한다.
     *
     * @param postNo 작성할 게시글의 식별 번호
     * @param parentReplyNo 답글인지 여부를 확인할 부모 댓글의 식별 번호. 답글이 아닐 경우 -1.
     */
    public void replyWrite(long postNo, long parentReplyNo) {
        final String content = replyContentLiveData.getValue();
        replyWriteDisposable = replyRepository.requestWrite(
                createReplyWriteRequest(postNo, parentReplyNo, content));
        addDisposable(replyWriteDisposable);
    }

    /**
     * 댓글 삭제를 요청한다.
     *
     * @param replyNo
     */
    public void replyDelete(long replyNo) {
        if(replyDeleteDisposable != null && !replyDeleteDisposable.isDisposed()) {
            systemMessageLiveData.setValue("처리 중입니다.");
            return;
        }
        replyDeleteDisposable = replyRepository.requestDelete(replyNo);
        addDisposable(replyDeleteDisposable);
    }

    /**
     *  댓글 쓰기를 요청하기 전에 데이터가 유효한지 판단한다. 판단 기준은 데이터가 비어있지 않은가와 최대 길이를
     * 넘지 않는가에 대해서 확인한다.
     *
     * @return 댓글 내용의 유효성 여부
     */
    public boolean isValidReplyContent() {
        final String content = replyContentLiveData.getValue();

        if(TextUtils.isEmpty(content)){
            systemMessageLiveData.setValue("댓글을 입력해주세요.");
            return false;
        }

        if(content.length() > MAXIMUM_LENGTH_REPLY_CONTENT) {
            systemMessageLiveData.setValue("댓글은 " + MAXIMUM_LENGTH_REPLY_CONTENT +
                    "자를 초과할 수 없습니다.");
            return false;
        }

        return true;
    }

    /**
     * 댓글 쓰기 요청에 필요한 데이터를 DTO 객체에 초기화한다.
     *
     * @param postNo 작성할 게시글의 식별 번호
     * @param parentReplyNo 부모 댓글의 식별 번호(답글이 아닐 경우 -1)
     * @param content 댓글 내용
     * @return
     */
    private ReplyWriteRequest createReplyWriteRequest(long postNo, long parentReplyNo, String content) {
        return new ReplyWriteRequest(postNo,
                parentReplyNo,
                AppConfig.UserPreference.getUserId(),
                content);
    }

    /**
     *  댓글 작성을 요청했을 때 요청이 이미 진행 중인지 판단한다. 키보드를 다루기 위해서 Fragment에서 호출되어
     * 사용된다.
     *
     * @return 댓글 쓰기 요청의 진행 여부
     */
    public boolean isRunningReplyWriting() {
        if(replyWriteDisposable != null && !replyWriteDisposable.isDisposed()) {
            systemMessageLiveData.setValue("처리 중입니다.");
            return false;
        }
        return false;
    }

    public SingleLiveEvent<String> getReplyContentLiveData() {
        if(replyContentLiveData == null)
            replyContentLiveData = new SingleLiveEvent<>();
        return replyContentLiveData;
    }

    public SingleLiveEvent<Boolean> getWriteSuccessfullyLiveData() {
        return replyRepository.getWriteSuccessfullyLiveData();
    }

    public SingleLiveEvent<Boolean> getDeleteSuccessfullyLiveData() {
        return replyRepository.getDeleteSuccessfullyLiveData();
    }

    public SingleLiveEvent<PostDetailDTO> getPostDetailLiveData() {
        return postRepository.getPostDetailLiveData();
    }

    public SingleLiveEvent<List<ReplyDTO>> getReplyListLiveData() {
        return replyRepository.getReplyListLiveData();
    }
}
