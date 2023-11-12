package com.example.wemakepass.network.client;

import com.example.wemakepass.BuildConfig;
import com.example.wemakepass.network.deserializer.LocalDateDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 공공 데이터 포털에서 제공하는 API 중 URL이 "http://apis.data.go.kr/"인 API에 대한 Retrofit 싱글톤 클래스
 *
 * @author BH-Ku
 * @since 2023-08-02
 */
public class DataPortalClient {
    private static Retrofit retrofit;
    public static final String BASE_URL = BuildConfig.DATA_PORTAL_BASE_URL;
    public static final String SERVICE_KEY_ENC = BuildConfig.DATA_PORTAL_SERVICE_KEY_ENC;
    public static final String SERVICE_KEY_DEC = BuildConfig.DATA_PORTAL_SERVICE_KEY_DEC;
    public static final String DATA_FORMAT_JSON = "JSON";
    public static final String DATA_FORMAT_XML = "XML";

    public static Retrofit getRetrofit() {
        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getOkHttpClient())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();
        }

        return retrofit;
    }

    /**
     *  한국산업인력공단에서 제공하는 API는 API별로 응답 편차가 심각한 수준이다. 예로 종목 코드는 1초 이내에서
     * 3분 이내까지 걸리는 편이다. 그래도 이 API는 평균적으로 1초를 넘기지 않지만 드물게 5초를 넘긴 적이 있었기에
     * 타임 아웃을 길게 잡았다.
     *
     * @return
     */
    private static OkHttpClient getOkHttpClient(){
        return new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }
}
