package com.example.wemakepass.data.model.dto;


import androidx.annotation.Nullable;

import java.time.LocalDateTime;

/**
 * 게시글에 대한 요약된 데이터를 갖는 DTO 클래스다.
 *
 * @author BH-Ku
 * @since 2023-12-01
 */
public class PostDTO {
    private long postNo;
    private String category;
    private String nickname;
    private String title;
    private LocalDateTime regDate;
    private long hit;
    private int commentCount;

    public PostDTO(long postNo, String nickname, String title, LocalDateTime regDate, long hit, int commentCount) {
        this.postNo = postNo;
        this.nickname = nickname;
        this.title = title;
        this.regDate = regDate;
        this.hit = hit;
        this.commentCount = commentCount;
    }

    public long getPostNo() {
        return postNo;
    }

    public String getCategory() {
        return category;
    }

    public String getNickname() {
        return nickname;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getRegDate() {
        return regDate;
    }

    public long getHit() {
        return hit;
    }

    public int getCommentCount() {
        return commentCount;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        PostDTO target = (PostDTO) obj;

        return postNo == target.getPostNo() &&
                nickname.equals(target.getNickname()) &&
                hit == target.getHit() &&
                commentCount == target.getCommentCount();
    }

    @Override
    public String toString() {
        return "PostDTO{" +
                "postNo=" + postNo +
                ", category='" + category + '\'' +
                ", nickname='" + nickname + '\'' +
                ", title='" + title + '\'' +
                ", regDate=" + regDate +
                ", hit=" + hit +
                ", commentCount=" + commentCount +
                '}';
    }
}