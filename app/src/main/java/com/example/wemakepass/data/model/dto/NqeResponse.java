package com.example.wemakepass.data.model.dto;

import androidx.annotation.NonNull;

import com.example.wemakepass.data.enums.NqeApiErrorCode;

import java.util.List;

/**
 * - 한국산업인력공단에서 제공하는 Response의 형식에서 불필요한 데이터들을 제외하고 필요한 데이터들로만 구성한
 *  Response(DTO) 클래스다.
 * - 자동으로 바인딩되는 것이 아닌 직접 파싱하고 바인딩하는 것이기 때문에 실제 한국산업인력공단에서 제공하는
 *  Response의 구조와는 차이가 있다.
 *
 * @author BH-Ku
 * @since 2023-11-11
 * @param <T>
 */
public class NqeResponse<T> {
    private final String resultCode; // 실패 시 XML을 파싱하여 에러 코드가 저장된다.
    private String resultMessage; // 실패 시 NqeApiErrorCode에서 에러 코드에 해당하는 메시지를 찾아 저장.
    private List<T> items;

    // 같은 곳에서 제공하는 API지만 성공 코드로 0을 제공하는 것도 있고 00을 제공하는 것도 있다.
    private final String SUCCESSFUL_CODE_A = "0";
    private final String SUCCESSFUL_CODE_B = "00"; // 성공 코드

    /**
     * 응답 실패 시 사용되는 생성자로서 ofError 메서드에 의해 내부에서만 호출된다.
     *
     * @param resultCode Response에서 파싱한 실제 에러 코드(숫자 한 자리 혹은 두 자리 값)
     * @param resultMessage Response에서 파싱한 ErrorCode를 토대로 NqeApiErrorCode에서 읽어온 에러 메시지
     */
    private NqeResponse(@NonNull String resultCode, @NonNull String resultMessage) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }

    /**
     * Response 파싱 성공 시 호출되는 생성자.
     *
     * @param resultCode 상태 코드, 성공했을 경우 0 혹은 00이 초기화.
     * @param items 데이터
     */
    public NqeResponse(@NonNull String resultCode, @NonNull List<T> items){
        this.resultCode = resultCode;
        this.items = items;
    }

    public static NqeResponse ofError(@NonNull String resultCode, @NonNull String resultMessage){
        return new NqeResponse(resultCode, resultMessage);
    }

    public static NqeResponse ofError(@NonNull NqeApiErrorCode nqeApiErrorCode){
        return new NqeResponse(nqeApiErrorCode.getCode(), nqeApiErrorCode.getMessage());
    }

    /**
     * API 요청에 대한 성공 여부를 반환한다.
     *
     * @return
     */
    public boolean isSuccessful() {
        return resultCode.equals(SUCCESSFUL_CODE_A) || resultCode.equals(SUCCESSFUL_CODE_B);
    }

    public String getResultCode() {
        return resultCode;
    }

    public List<T> getItems() {
        return items;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    @Override
    public String toString() {
        return "NqeResponse{" +
                "resultCode=" + resultCode +
                ", items=" + items +
                '}';
    }
}
