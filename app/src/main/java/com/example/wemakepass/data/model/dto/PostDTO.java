package com.example.wemakepass.data.model.dto;


import androidx.annotation.Nullable;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 게시글에 대한 요약된 데이터를 갖는 DTO 클래스다.
 *
 * @author BH-Ku
 * @since 2023-12-01
 */
public class PostDTO implements Serializable {
    private long postNo; // 게시글의 식별 번호
    private String category; // 게시글의 카테고리
    private String nickname; // 게시글 작성자의 닉네임
    private String title; // 게시글 제목
    private LocalDateTime regDate; // 게시글 작성 시간
    private long hit; // 게시글 조회수
    private int replyCount; // 게시글에 등록된 댓글 수

    public PostDTO(long postNo, String nickname, String title, LocalDateTime regDate, long hit, int replyCount) {
        this.postNo = postNo;
        this.nickname = nickname;
        this.title = title;
        this.regDate = regDate;
        this.hit = hit;
        this.replyCount = replyCount;
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

    public int getReplyCount() {
        return replyCount;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        PostDTO target = (PostDTO) obj;

        return postNo == target.getPostNo() &&
                nickname.equals(target.getNickname()) &&
                hit == target.getHit() &&
                replyCount == target.getReplyCount();
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
                ", commentCount=" + replyCount +
                '}';
    }
}