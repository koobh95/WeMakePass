package com.example.wemakepass.network.deserializer;

import com.example.wemakepass.util.DateUtil;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDate;

/**
 *  Retrofit 통신에서 Json으로 전송받은 데이터를 역직렬화할 때 LocalDate 타입이 있을 경우 문자열 상태인 값을
 * LocalDate로 변환해주는 클래스다.
 *
 * @author BH-Ku
 * @since 2023-08-31
 */
public class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
    private final String TAG = "TAG_LocalDateDeserializer";

    /**
     *  문자열 형태의 날짜 데이터를 LocalDate로 변경한다. API에 따라 날짜값이 없는 경우(공백)가 있기 때문에
     * 공백 처리를 해주었다.
     *
     * @param json The Json data being deserialized
     * @param typeOfT The type of the Object to deserialize to
     * @param context
     * @return null or LocalDate
     * @throws JsonParseException
     */
    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return DateUtil.parseLocalDate(json.getAsString());
    }
}
