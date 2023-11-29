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

    // 게시판 검색에 이용된다.
    @LoginRequired
    @GET(BASE_URI + "search")
    Observable<Response<List<BoardDTO>>> search(
            @Query("keyword") String keyword);
}
