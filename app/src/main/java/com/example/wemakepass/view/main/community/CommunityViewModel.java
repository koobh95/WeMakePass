package com.example.wemakepass.view.main.community;

import com.example.wemakepass.base.BaseViewModel;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.model.dto.BoardDTO;
import com.example.wemakepass.data.pref.AppDataPreferences;
import com.example.wemakepass.repository.pref.VisitedBoardRepository;

import java.util.List;

/**
 * CommunityFragment의 ViewModel 클래스.
 *
 * @author BH-Ku
 * @since 2023-12-02
 */
public class CommunityViewModel extends BaseViewModel {
    private VisitedBoardRepository visitedBoardRepository;

    public CommunityViewModel() {
        visitedBoardRepository = new VisitedBoardRepository(AppDataPreferences.KEY_VISITED_BOARD);
    }

    /**
     * 방문한 게시판 목록을 재로딩한다.
     */
    public void reloadingVisitedBoarLogList() {
        visitedBoardRepository.reload();
    }

    /**
     * 방믄한 게시판 목록에서 특정 아이템을 제거한다.
     *
     * @param boardDTO 제거하려는 대상 객체
     */
    public void deleteVisitedBoardLog(BoardDTO boardDTO) {
        visitedBoardRepository.deleteItem(boardDTO);
    }

    /**
     * 방문한 게시판 목록을 초기화한다.
     */
    public void deleteAllVisitedBoardLog() {
        visitedBoardRepository.clear();
    }

    public SingleLiveEvent<List<BoardDTO>> getVisitedBoardListLiveData() {
        return visitedBoardRepository.getElementListLiveData();
    }
}
