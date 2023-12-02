package com.example.wemakepass.listener;

import com.example.wemakepass.data.model.dto.BoardDTO;

/**
 * - 방문한 게시판 목록의 아이템이 클릭되었을 때 이벤트를 처리할 리스너 인터페이스.
 * - 방문한 게시판 목록의 경우 중첩 RecyclerView로 구성되어 있는데 실질적으로 데이터를 가진 RecyclerView는
 *  ChildRecylcerView이기 때문에 View 측에서 Child View Adapter에 자유롭게 접근하기 힘들 것이라 판단해
 *  전용 리스너 인터페이스를 만들었다.
 *
 * @author BH-Ku
 * @since 2023-12-02
 */
public interface OnVisitedBoardItemClickListener {
    void onClick(BoardDTO boardDTO);

    void onLongClick(BoardDTO boardDTO);
}
