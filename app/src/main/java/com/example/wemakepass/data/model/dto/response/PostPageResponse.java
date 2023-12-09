package com.example.wemakepass.data.model.dto.response;

import com.example.wemakepass.data.model.dto.PostDTO;

import java.util.List;

/**
 *  게시판 메인에서 게시글 목록을 페이지 단위로 읽을 때 사용하는 DTO 클래스로서 데이터를 가지는 List와 읽어 온
 * 페이지에 대한 정보를 갖는다.
 *
 * @author BH-Ku
 * @since 2023-12-09
 */
public class PostPageResponse {
    private List<PostDTO> postList; // 게시글 리스트
    private int pageNo; // 읽어 온 페이지 번호
    private boolean last; // 마지막 페이지 여부

    public PostPageResponse(List<PostDTO> postList, int pageNo, boolean last) {
        this.postList = postList;
        this.pageNo = pageNo;
        this.last = last;
    }

    public List<PostDTO> getPostList() {
        return postList;
    }

    public int getPageNo() {
        return pageNo;
    }

    public boolean isLast() {
        return last;
    }
}
