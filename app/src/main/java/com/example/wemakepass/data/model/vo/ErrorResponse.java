package com.example.wemakepass.data.model.vo;

import com.example.wemakepass.data.enums.ErrorCode;
/**
 * - API 호출 과정에서 에러가 발생하는 경우 에러를 파싱하여 Code, Message를 저장하는 모델 클래스다.
 * - 변수에 저장되는 값들은 크게 두 종류로, 하나는 내 서버에서 내가 임의로 생성한 ErrorCode, 한국산업인력공단에서 제공하는
 *  API ErrorCode가 있다.
 *
 * @author BH-Ku
 * @since 2023-05-14
 */
public class ErrorResponse {
    private String code;
    private String message;

    private static final String CONNECTION_FAILED_MESSAGE = "서버로부터 응답이 없습니다. 다시 시도해주세요.";
    private static final String UNKNOWN_ERROR_MESSAGE = "네트워크 통신 중 알 수 없는 오류가 발생했습니다.";

    public ErrorResponse() {}

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }

    public String getMessage() { return message; }

    /**
     *  통신에 실패했을 경우 서버와 연결되지 않기 때문에 에러 코드를 받을 수 없다. 이러한 경우 직접 통신
     * 실패에 관한 코드와 메시지를 설정하여 객체를 반환하기 위한 정적 메서드다.
     *
     * @return
     */
    public static ErrorResponse ofConnectionFailed() {
        return new ErrorResponse(ErrorCode.CONNECTION_FAILED.name(), CONNECTION_FAILED_MESSAGE);
    }

    /**
     *  통신이 이루어지지 않은 경우는 ofConnectionFailed 에서 처리한다. 반면 통신이 이루어졌지만 StatusCode가
     * 정상적인 코드가 아닌 경우 ErrorResponseConverter에 의해서 파싱되는데 이 과정에서도 문제가 생길 경우
     * 이 메서드가 호출된다.
     *
     * @return
     */
    public static ErrorResponse ofUnknownError() {
        return new ErrorResponse(ErrorCode.WMP_UNKNOWN_ERROR.name(), UNKNOWN_ERROR_MESSAGE);
    }

    @Override
    public String toString() {
        return "ErrorResponse{ code=" + code + ", message=" + message + '}';
    }
}
