package com.example.wemakepass.data.model.dto;

import androidx.annotation.Nullable;

import java.io.Serializable;

/**
 * 게시판에 대한 정보를 가지는 DTO 클래스
 *
 * @author BH-Ku
 * @since 2023-11-29
 */
public class BoardDTO implements Serializable {
    private long boardNo;
    private String boardName;

    public BoardDTO(long boardNo, String boardName) {
        this.boardNo = boardNo;
        this.boardName = boardName;
    }

    public long getBoardNo() {
        return boardNo;
    }

    public String getBoardName() {
        return boardName;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        BoardDTO target = (BoardDTO)obj;
        return target.getBoardNo() == boardNo;
    }

    @Override
    public String toString() {
        return "BoardDTO{" +
                "boardNo=" + boardNo +
                ", boardName='" + boardName + '\'' +
                '}';
    }
}
