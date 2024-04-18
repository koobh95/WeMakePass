package com.example.wemakepass.data.model.dto;

import java.time.LocalDateTime;

/**
 * - 게시판의 게시글 목록에서 특정 게시글(요약된 정보)을 선택했을 때 게시글에 대한 상세 정보를 요청하게 되는데 이 때
 *  사용되는 DTO 클래스다.
 * - PostDTO와 크게 다르지 않은 구성이지만 닉네임과 조회수의 최신 정보를 받아오기 위한 목적과 조회한 게시글이
 *  게시글을 선택한 시점에서 삭제되었을 수 있끼 때문에 별도로 사용 된다.
 *
 * @author BH-Ku
 * @since 2023-12-12
 */
public class PostDetailDTO {
    private String category; // 게시글의 카테고리
    private String nickname; // 작성자 닉네임
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private LocalDateTime regDate; // 게시글 작성 시간
    private long hit; // 조회수

    public PostDetailDTO(String category, String nickname, String title, String content,
                         LocalDateTime regDate, long hit) {
        this.category = category;
        this.nickname = nickname;
        this.title = title;
        this.content = content;
        this.regDate = regDate;
        this.hit = hit;
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

    public String getContent() {
        return content;
    }

    public LocalDateTime getRegDate() {
        return regDate;
    }

    public long getHit() {
        return hit;
    }

    @Override
    public String toString() {
        return "PostDetailDTO{" +
                "category='" + category + '\'' +
                ", nickname='" + nickname + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", regDate=" + regDate +
                ", hit=" + hit +
                '}';
    }
}
