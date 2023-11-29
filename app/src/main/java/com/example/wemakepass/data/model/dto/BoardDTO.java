package com.example.wemakepass.data.model.dto;

import java.io.Serializable;

/**
 * 게시판에 대한 정보를 가지는 DTO 클래스
 *
 * @author BH-Ku
 * @since 2023-11-29
 */
public class BoardDTO implements Serializable {
    private int boardId;
    private String boardName;

    public int getBoardId() {
        return boardId;
    }

    public String getBoardName() {
        return boardName;
    }
}
