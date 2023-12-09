package com.example.wemakepass.network.api;

import com.example.wemakepass.data.model.dto.response.PostPageResponse;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 게시글 관련 조회, 삭제, 추가 등의 API를 모아놓은 인터페이스.
 *
 * @author BH-Ku
 * @since 2023-12-01
 */
public interface PostAPI {
    String BASE_URI = "/api/post";

    // 특정 게시판의 게시글을 페이지 단위로 조회한다.
    @GET(BASE_URI)
    Observable<Response<PostPageResponse>> postList(
            @Query("boardNo") long boardNo,
            @Query("pageNo") int pageNo);

    // 특정 게시판, 특정 카테고리의 게시글을 페이지 단위로 조회한다.
    @GET("/api/post/category")
    Observable<Response<PostPageResponse>> postListByCategory(
            @Query("boardNo") long boardNo,
            @Query("pageNo") int pageNo,
            @Query("category") String category);
}
