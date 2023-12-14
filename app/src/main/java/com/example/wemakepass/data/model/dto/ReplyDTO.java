package com.example.wemakepass.data.model.dto;

import java.time.LocalDateTime;

/**
 * 게시글에서 표시할 댓글의 정보를 가지는 DTO 클래스.
 *
 * @author BH-Ku
 * @since 2023-12-12
 */
public class ReplyDTO {
    private boolean reReply; // 답글 여부
    private long replyNo; // 댓글의 식별 번호
    private String writerId; // 작성자의 User Id
    private String writerNickname; // 작성자의 닉네임
    private String content; // 댓글 내용
    private LocalDateTime regDate; // 댓글의 작성 시간
    private boolean deleted; // 댓글의 삭제 여부, true인 경우 답글이 있는데 삭제된 댓글이라는 뜻

    public boolean isReReply() {
        return reReply;
    }

    public long getReplyNo() {
        return replyNo;
    }

    public String getWriterId() {
        return writerId;
    }

    public String getWriterNickname() {
        return writerNickname;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getRegDate() {
        return regDate;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean equals(ReplyDTO target) {
        if(replyNo != target.replyNo)
            return false;
        if(!writerNickname.equals(target.writerNickname))
            return false;
        if(deleted != target.deleted)
            return false;
        return true;
    }
}
