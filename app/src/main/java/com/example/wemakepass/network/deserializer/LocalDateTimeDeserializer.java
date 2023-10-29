package com.example.wemakepass.network.deserializer;

import com.example.wemakepass.util.DateUtil;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Date;

/**
 *  Retrofit 통신에서 Json으로 전송받은 데이터를 역직렬화할 때 LocalDateTime 타입이 있을 경우 문자열 상태인
 * 값을 LocalDateTime으로 변환해주는 클래스다.
 *
 * @author BH-Ku
 * @since 2023-10-25
 */
public class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
    private final String TAG = "TAG_LocalDateTimeDeserializer";

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return DateUtil.parseLocalDateTime(json.getAsString());
    }
}
