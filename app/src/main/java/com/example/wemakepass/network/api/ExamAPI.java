package com.example.wemakepass.network.api;

import com.example.wemakepass.annotations.LoginRequired;
import com.example.wemakepass.data.model.dto.ExamDocAnswerDTO;
import com.example.wemakepass.data.model.dto.ExamDocQuestionDTO;
import com.example.wemakepass.data.model.dto.ExamResultDTO;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * 시험 문제, 답안 조회나 시험 결과 저장 등과 관련된 작업을 모아놓은 API 인터페이스
 *
 * @author BH-Ku
 * @since 2023-11-23
 */
public interface ExamAPI {
    String BASE_URI = "api/exam/";

    // 특정 시험이 가지는 문제 데이터들을 조회
    @LoginRequired
    @GET(BASE_URI + "doc/question")
    Observable<Response<List<ExamDocQuestionDTO>>> examDocQuestionList(
            @Query("examId") long examId);

    // 특정 시험이 가지는 답안 데이터들을 조회
    @LoginRequired
    @GET(BASE_URI + "doc/answer")
    Observable<Response<List<ExamDocAnswerDTO>>> examDocAnswerList(
            @Query("examId") long examId);

    // 시험 결과를 서버로 전송
    @LoginRequired
    @PUT(BASE_URI + "save")
    Observable<Response<String>> saveExamResult(
            @Body ExamResultDTO examResultDTO);
}
