package com.example.wemakepass.network.api;

import com.example.wemakepass.annotations.LoginRequired;
import com.example.wemakepass.data.model.dto.ReplyDTO;
import com.example.wemakepass.data.model.dto.request.ReplyWriteRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 댓글 조회 및 댓글 작성 등의 API를 모아놓은 인터페이스.
 *
 * @author BH-Ku
 * @since 2023-12-12
 */
public interface ReplyAPI {
    String BASE_URI = "/api/reply/";

    // 특정 게시글의 댓글 목록을 조회한다.
    @LoginRequired
    @GET(BASE_URI)
    Observable<Response<List<ReplyDTO>>> replyList(
            @Query("postNo") long postNo);

    // 특정 게시글에 대한 댓글 쓰기를 요청한다.
    @LoginRequired
    @POST(BASE_URI)
    Observable<Response<String>> write(
            @Body ReplyWriteRequest replyWriteRequest);

    // 특정 댓글에 대한 삭제를 요청한다.
    @LoginRequired
    @DELETE(BASE_URI)
    Observable<Response<String>> delete(
            @Query("replyNo") long replyNo);
}
