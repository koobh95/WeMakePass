package com.example.wemakepass.data.enums;

/**
 * - 공공 데이터 포털의 한국산업인력공단에서 제공하는 API를 사용할 때 발생할 수 있는 에러 코드가 정의된 열거형
 *  클래스다.
 * - 국가자격시험(National Qualification Examination)의 약자인 NQE를 사용하였다.
 * - 한국산업인력공단에서 제공하는 API들 중 숫자 코드를 제외한 "결과 코드", "결과 메시지" 들이 조금씩 다른 경우가
 *  많았기에 결과를 파싱했을 때 혹은 파싱하는 과정에서 문제가 생기는 경우 ErrorResponse에서 코드만 Parsing하여
 *  사용하고 메시지는 이 클래스에 있는 값을 사용한다.
 * - 다른 경우의 예를 들자면 숫자 코드가 같음에도 불구하고 문자열로 이루어진 결과 코드가 온전히 같은 문자열임에도
 *  불구하고 단어 간에 띄어 쓰기를 사용하는 경우가 있고 언더 바를 사용하는 경우가 있다. 또 메시지에 한글을 사용하는
 *  API가 있는가 하면 영어를 사용하는 API도 있다.
 *
 * @author BH-Ku
 * @since 2023-11-10
 */
public enum NqeApiErrorCode {
    NORMAL_SERVICE("00", "API를 불러왔습니다."),
    APPLICATION_ERROR("1", "어플리케이션 오류가 발생했습니다."),
    HTTP_ERROR("4", "Http 통신 오류가 발생했습니다."),
    NO_OPENAPI_SERVICE_ERROR("12", "해당 API는 더 이상 지원되지 않습니다."),
    SERVICE_ACCESS_DENIED_ERROR("20", "서비스 접근이 제한된 상태입니다. 관리자에게 문의하세요."),
    LIMITED_NUMBER_OF_SERVICE_REQUESTS_EXCEEDS_ERROR("22", "(Test) 금일 요청 제한 횟수를 초과했습니다."),
    SERVICE_KEY_IS_NOT_REGISTERED_ERROR("30", "(Test) 서비스 키가 올바르지 않습니다."),
    DEADLINE_HAS_EXPIRED_ERROR("31", "(Test) 서비스 키의 기한이 만료되었습니다."),
    UNREGISTERED_IP_ERROR("32", "등록되지 않은 IP입니다."), // 사실상 쓰일 일 없음.
    UNKNOWN_ERROR("99", "데이터를 불러오는 도중 알 수 없는 에러가 발생했습니다."),
    API_PARSE_ERROR("-1", "읽어온 데이터를 변환하는 과정에서 에러가 발생했습니다.");

    private final String code;
    private final String message;

    NqeApiErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
