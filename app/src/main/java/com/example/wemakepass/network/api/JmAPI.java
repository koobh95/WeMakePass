package com.example.wemakepass.network.api;

import com.example.wemakepass.annotations.LoginRequired;
import com.example.wemakepass.data.model.dto.JmInfoDTO;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * - 시험 종목 관련 Api를 모아놓은 API 인터페이스
 * - Jm은 종목을 의미하며 한국산업인력공단에서 제공하는 API들 중 시험 종목 관련 데이터들을 "Jm*"으로 사용하고
 *  있기 때문에 서버의 데이터베이스에서도 같은 이름으로 사용한다.
 * - 종목 코드에 대한 데이터는 한국산업인력공단에서 제공하고 있지만 유독 이 API에 대한 응답 속도의 편차가 심하다.
 *  빠를 땐 1초~3초지만 느릴 때는 2분도 넘게 걸린다.(타임 아웃은 걸리지 않는 듯 하다.) 모듈은 한국산업인력공단의
 *  API를 사용하여 구현했었지만 속도가 너무 느리다 판단하여 서버에 데이터를 저장하고 서버에서 데이터를 제공하기로
 *  했다.
 */
public interface JmAPI {
    String BASE_URI = "api/jm/";

    // 특정 검색어와 부분 일치하는 종목 이름을 가진 종목 정보를 조회한다.
    @LoginRequired
    @GET(BASE_URI + "search")
    Observable<Response<List<JmInfoDTO>>> search(
            @Query("keyword") String keyword);

    // 시험 관련 데이터를 가지고 있는 종목을 대상으로 조회한다.
    @LoginRequired
    @GET(BASE_URI + "search/with_exam_info")
    Observable<Response<List<JmInfoDTO>>> searchForJmWithExamInfo(
            @Query("keyword") String keyword);

    // 게시판이 존재하는 종목을 대상으로 조회한다.
    @LoginRequired
    @GET(BASE_URI + "search/with_board")
    Observable<Response<List<JmInfoDTO>>> searchForJmWithBoard(
            @Query("keyword") String keyword);
}
