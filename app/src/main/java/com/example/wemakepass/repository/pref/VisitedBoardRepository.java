package com.example.wemakepass.repository.pref;

import com.example.wemakepass.base.BaseAppDataRepository;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.model.dto.BoardDTO;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 방문한 게시판 목록에 대한 데이터 저장, 삭제, 조회 등의 역할을 하는 Repository 클래스.
 *
 * @author BH-Ku
 * @since 2023-12-02
 */
public class VisitedBoardRepository extends BaseAppDataRepository<BoardDTO> {
    private final String ARG_BOARD_NO = "boardNo";
    private final String ARG_BOARD_NAME = "boardName";

    public VisitedBoardRepository(String prefName) {
        super(prefName);
    }

    @Override
    protected BoardDTO parseJsonObject(JsonObject jsonObject) {
        return new BoardDTO(
                jsonObject.get(ARG_BOARD_NO).getAsInt(),
                jsonObject.get(ARG_BOARD_NAME).getAsString());
    }

    @Override
    protected JSONObject createJson(BoardDTO boardDTO) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ARG_BOARD_NO, boardDTO.getBoardNo());
        jsonObject.put(ARG_BOARD_NAME, boardDTO.getBoardName());
        return jsonObject;
    }

    /**
     *  방문한 게시판 목록의 경우 페이지 단위로 데이터가 나뉘어져 있어 정확한 위치를 알기 어렵다. 따라서 삭제하려는
     * 객체를 파라미터로 받아서 객체가 가지고 있는 고유한 값으로 아이템의 위치를 찾아 제거한다.
     *
     * @param removeItem 삭제할 아이템
     */
    public void removeItem(BoardDTO removeItem) {
        List<BoardDTO> list = getElementListLiveData().getValue();

        for(int i = 0; i < list.size(); i++)
            if(removeItem.getBoardNo() == list.get(i).getBoardNo())
                super.removeItem(i);
    }

    /**
     * 저장된 데이터를 다시 로딩하여 라이브 데이터에 세팅한다.
     */
    public void reload() {
        getElementListLiveData().setValue(loadPrefData());
    }
}
