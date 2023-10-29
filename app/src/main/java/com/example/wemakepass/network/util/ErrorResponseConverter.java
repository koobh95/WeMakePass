package com.example.wemakepass.network.util;

import android.util.Log;

import com.example.wemakepass.data.model.vo.ErrorResponse;
import com.example.wemakepass.network.client.WmpClient;

import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 *  서버와 통신에는 성공했으나 정상적인 결과(Status code 2xx)를 받지 못하는 경우(3xx~) ErrorResponse를
 * 받게 된다. 이 부분은 JSON을 데이터를 받았을 때 자동으로 직렬화되는 것처럼 자동으로 처리되지 않기 때문에
 * 정상적인 결과를 받지 못한 경우 Response 객체를 받아와 파싱, ErrorResponse로 변환하하고 반환하는 역할을 하는
 * 클래스다.
 *
 * @author BH-Ku
 * @see 2023-10-28
 */
public class ErrorResponseConverter {
    private static final String TAG = "TAG_ErrorResponseConverter";

    public static ErrorResponse parseError(Response<?> response) {
        Converter<ResponseBody, ErrorResponse> converter =
                WmpClient.getRetrofit().responseBodyConverter(ErrorResponse.class, new Annotation[0]);
        ErrorResponse errorResponse = null;

        try{
            errorResponse = converter.convert(response.errorBody());
        } catch (Exception e) {
            if(e.getMessage() != null)
                Log.i(TAG, e.getMessage());
            errorResponse = ErrorResponse.ofUnknownError();
        }

        return errorResponse;
    }
}
