package com.example.wemakepass.network.api;

import com.example.wemakepass.annotations.LoginRequired;
import com.example.wemakepass.data.model.dto.ExamInfoDTO;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 특정 종목의 시험 정보와 관련된 데이터를 조회하는 API를 모아놓은 인터페이스다.
 *
 * @author BH-Ku
 * @since 2023-11-14
 */
public interface ExamInfoAPI {
    String BASE_URI = "api/exam_info/";

    // 특정 종목 코드에 해당하는 시험들을 모두 조회한다.
    @LoginRequired
    @GET(BASE_URI + "list")
    Observable<Response<List<ExamInfoDTO>>> examInfoList(
            @Query("jmCode") String jmCode);

    // 특정 시험의 과목 목록을 조회한다.
    @LoginRequired
    @GET(BASE_URI + "subject_list")
    Observable<Response<List<String>>> subjectList(
            @Query("examId") long examId);
}
