package com.example.wemakepass.network.parser;

import com.example.wemakepass.data.enums.NqeApiErrorCode;
import com.example.wemakepass.data.model.dto.NqeResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * - 한국산업인력공단에서 제공하는 API 중 응답은 JSON을 지원하지만 ErrorResponse는 XML밖에 제공하지 않는 경우
 *  GsonConverter로는 에러에 대응할 수 없기 때문에 수동으로 파싱해주기 위해서 사용하는 Parser 클래스다.
 * - 이 클래스는 성공적인 응답이 오는 경우 한국산업인력공단이 제공하는 Response의 공통적인 부분을 모두 파싱하고
 *  실제적으로 데이터가 들어있는 <items> 내부의 Parsing은 추상 메서드인 bind를 호출하여 처리한다. bind 메서드는
 *  이 클래스를 상속받는 자식 클래스에서 구현한다.
 * - 이 클래스 하나로 이러한 구조를 가진 API들의 응답을 모두 처리하고 싶었으나 Gson으로 Json을 List<E>로
 *  파싱하는 과정에서 발생하는 문제를 해결하지 못했기에 각 타입에 맞는 하위 클래스를 추가로 생성하게 되었다.
 *  구글링을 통해 많은 해결법을 만나봤지만 이해도가 부족한 탓에 구현해내지 못했다.
 *
 *  Error : com.google.gson.internal.linkedtreemap cannot be cast ..
 *
 * @author BH-Ku
 * @since 2023-11-11
 * @param <T>
 */
public abstract class NqeResponseParser<T> {
    private final String TAG = "TAG_NqeResponseParser";

    protected NqeResponseParser(){ }

    /**
     * - String 형태의 데이터를 파싱한다.
     * - 데이터의 첫 문자가 "{"일 경우 JSON, "<"일 경우 XML로 판단한다. JSON일 경우 GSON으로 데이터를 파싱하고
     *  XML일 경우 에러 코드만 파싱한 후 결과를 반환한다.
     *
     * @param responseBody Response.body
     * @return
     */
    public NqeResponse<T> parse(String responseBody) {
        if(responseBody == null || responseBody.equals(""))
            return NqeResponse.ofError(NqeApiErrorCode.UNKNOWN_ERROR);

        NqeResponse<T> nqeResponse = null;

        try{ //Json
            if(responseBody.charAt(0) == '{') // JSON
                nqeResponse = parseJson(responseBody);
            else { // XML == Error
                String errorCode = parseErrorCode(responseBody);
                String errorMessage = getErrorMessage(errorCode);
                nqeResponse = NqeResponse.ofError(errorCode, errorMessage);
            }
        } catch(Exception e) { // Parsing Error
            e.printStackTrace();
            nqeResponse = NqeResponse.ofError(NqeApiErrorCode.API_PARSE_ERROR);
        }

        return nqeResponse;
    }

    /**
     * - 응답이 JSON일 경우 즉, 성공적인 응답을 받은 경우 호출되며 JSON을 파싱하여 반환한다.
     * - 가장 많은 일정을 가진 종목 기준 Parsing에 걸리는 시간 최대 0.007m/s
     *
     * @param jsonString String 형태의 JSON
     * @return
     * @throws Exception Parse 예외
     */
    private NqeResponse<T> parseJson(String jsonString) throws Exception {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(jsonString);
        JsonObject header = jsonElement.getAsJsonObject().get("header").getAsJsonObject();
        JsonObject body = jsonElement.getAsJsonObject().get("body").getAsJsonObject();
        JsonArray itemJsonArray = body.getAsJsonObject().get("items").getAsJsonArray();
        String resultCode = header.getAsJsonObject().get("resultCode").getAsString();
        List<T> itemList = new ArrayList<>(itemJsonArray.size());
        for(JsonElement je: itemJsonArray)
            itemList.add(parseItem(je.getAsJsonObject()));
        return new NqeResponse<>(resultCode, itemList);
    }

    /**
     *  응답이 XML일 경우 에러로 판단하고 이 메서드를 호출한다. 다른 데이터는 사용하지 않으므로 에러 코드만
     * 파싱하여 반환한다.
     *
     * @param xml 에러 메시지
     * @return
     * @throws Exception Parse 예외
     */
    private String parseErrorCode(String xml)  throws Exception{
        Document document = DocumentHelper.parseText(xml);
        Element response = document.getRootElement();
        Element header = response.element("cmmMsgHeader");
        return header.element("returnReasonCode").getText();
    }

    /**
     * 추출한 ErrorCode에 해당하는 ErrorMessage를 NqeApiErrorCode에서 찾아 반환한다.
     *
     * @param errorCode
     * @return
     */
    private String getErrorMessage(String errorCode) {
        for(NqeApiErrorCode code : NqeApiErrorCode.values())
            if(code.getCode().equals(errorCode))
                return code.getMessage();
        return NqeApiErrorCode.UNKNOWN_ERROR.getMessage();
    }

    /**
     * 아이템 리스트 <items/>가 가진 <item/> 하나를 파싱하는 추상 메서드로 자식 클래스에서 구현된다.
     *
     * @param jsonObject 데이터 하나, <item> 태그에 해당하는 JsonObject
     * @return 파싱하여 바인딩된 객체
     */
    protected abstract T parseItem(JsonObject jsonObject);
}
