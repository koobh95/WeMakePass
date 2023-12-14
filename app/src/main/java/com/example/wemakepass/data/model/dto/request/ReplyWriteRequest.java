package com.example.wemakepass.data.model.dto.request;

/**
 * 댓글을 작성 요청에 필요한 데이터를 담는 DTO 클래스다.
 *
 * @author BH-Ku
 * @since 2023-12-12
 */
public class ReplyWriteRequest {
    private final long postNo; // 작성할 게시글의 식별 번호
    private final long parentReplyNo; // 답글일 경우 댓글의 식별 번호, 댓글일 경우 -1이 초기화된다.
    private final String writerId; // 작성자의 User Id
    private final String content; // 댓글 내용

    public ReplyWriteRequest(long postNo, long parentReplyNo, String writerId, String content) {
        this.postNo = postNo;
        this.parentReplyNo = parentReplyNo;
        this.writerId = writerId;
        this.content = content;
    }

    public long getPostNo() {
        return postNo;
    }

    public long getParentReplyNo() {
        return parentReplyNo;
    }

    public String getWriterId() {
        return writerId;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "ReplyWriteRequest{" +
                "postNo=" + postNo +
                ", parentReplyNo=" + parentReplyNo +
                ", writerId='" + writerId + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
