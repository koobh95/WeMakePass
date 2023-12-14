package com.example.wemakepass.data.model.dto.request;

/**
 * 게시글 작성 요청에 필요한 데이터를 담는 DTO 클래스다.
 *
 * @author BH-Ku
 * @since 2023-12-10
 */
public class PostWriteRequest {
    private final long boardNo; // 게시글을 작성할 게시판의 식별 번호
    private final String writer; // 게시글 작성자(User ID)
    private final String category; // 게시글 카테고리
    private final String title; // 게시글 제목
    private final String content; // 게시글 내용

    public PostWriteRequest(long boardNo, String writer, String category, String title, String content) {
        this.boardNo = boardNo;
        this.writer = writer;
        this.category = category;
        this.title = title;
        this.content = content;
    }
}
