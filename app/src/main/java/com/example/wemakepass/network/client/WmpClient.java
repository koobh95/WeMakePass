package com.example.wemakepass.network.client;

import android.text.TextUtils;

import com.example.wemakepass.BuildConfig;
import com.example.wemakepass.config.AppConfig;
import com.example.wemakepass.data.enums.ErrorCode;
import com.example.wemakepass.data.model.dto.JwtDTO;
import com.example.wemakepass.data.model.vo.ErrorResponse;
import com.example.wemakepass.network.api.JwtAPI;
import com.example.wemakepass.network.client.interceptor.WmpInterceptor;
import com.example.wemakepass.network.deserializer.LocalDateDeserializer;
import com.example.wemakepass.network.deserializer.LocalDateTimeDeserializer;
import com.example.wemakepass.network.util.AES256Util;
import com.example.wemakepass.network.util.ErrorResponseConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * - 서버와 통신할 때 사용될 Retrofit 객체를 싱글톤 패턴으로 생성하는 클래스.
 * - 날짜 관련 데이터를 저장할 때 Date가 아닌 LocalDateTime, LocalDate 클래스를 사용하기 때문에 관련
 *  Deserializer들을 커스텀하여 사용하고 있다.
 * - 보안을 위해 Jwt를 사용하기 때문에 Request의 Header에 AccessToken을 추가해 주기 위해, 또 AccessToken이
 *  만료되었을 경우 재발급, 재요청을 하기 위한 목적으로 Interceptor를 사용한다.
 *
 * @author BH-Ku
 * @since 2023-05-14
 */
public class WmpClient {
    private static Retrofit retrofit;

    public static final String BASE_URL = BuildConfig.WMP_BASE_URL; // local.properties 에 저장된 주소

    public static Retrofit getRetrofit() {
        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getOkHttpClient())
                    .addConverterFactory(ScalarsConverterFactory.create()) // Response를 문자열로 받을 수 있게 해주는 아답터
                    .addConverterFactory(GsonConverterFactory.create(getGson())) // Request, Response를 Json Format으로 자동 변환해주는 아답터
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create()) // Retrofit의 Call 대신 Observable, Single 등을 사용할 수 있게 해주는 아답터
                    .build();
        }

        return retrofit;
    }

    private static Gson getGson(){
        return new GsonBuilder() // JSON 관련 설정 초기화
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                .setLenient() // Parsing 규정을 완화
                .create();
    }

    private static OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder() // 응답 시간 설정
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .addInterceptor(new WmpInterceptor())
                .build();
    }
}
