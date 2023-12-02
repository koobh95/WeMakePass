package com.example.wemakepass.network.api;

import com.example.wemakepass.annotations.LoginRequired;
import com.example.wemakepass.data.model.dto.BoardDTO;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 게시판 관련 API를 모아놓은 API 인터페이스.
 *
 * @author BH-Ku
 * @since 2023-11-29
 */
public interface BoardAPI {
    String BASE_URI = "api/board/";

    // 특정 키워드가 포함된 게시판 이름을 조회한다.
    @LoginRequired
    @GET(BASE_URI + "search")
    Observable<Response<List<BoardDTO>>> search(
            @Query("keyword") String keyword);

    // 특정 게시판의 카테고리 리스트를 조회한다.
    @LoginRequired
    @GET(BASE_URI + "category")
    Observable<Response<List<String>>> categoryList(
            @Query("boardNo") long boardNo);
}
