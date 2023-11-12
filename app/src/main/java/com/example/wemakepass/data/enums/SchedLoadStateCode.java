package com.example.wemakepass.data.enums;

/**
 * - 관심 종목 시험 일정을 불러오는 과정에서 발생할 수 있는 문제의 경우의 수를 정의하고 있는 열거형 클래스다.
 * - 모든 관심 종목 일정 객체(InterestJmSchedModel)은 일정을 불러오는데 성공했던 실패했던 반드시 이 코드를
 *  갖는다.
 * - 여기에 정의된 코드들은 데이터들을 정렬할 때 우선순위를 결정하거나 RecyclerView에서 어떤 ViewHolder를
 *  사용할 지 결정하는 기준이 된다.
 *
 * @author BH-Ku
 * @since 2023-11-11
 */
public enum SchedLoadStateCode {
    OK(0,  "일정을 불러왔습니다."),
    NO_SCHEDULE(1, "현재 예정된 시험이 없습니다."), // 현재 년도, 당일 기준으로 아직 치뤄지지 않은 일정이 없음.
    NOT_FOUND_SCHEDULE(2,
            "종목에 대한 데이터를 찾을 수 없습니다. 종목이 폐지되었거나 통합되었을 수 있습니다."), // 일정이 아예 존재하지 않음.
    NETWORK_ERROR(3, "서버가 응답하지 않습니다."), // 네트워크 오류
    API_ERROR(4, "API를 호출하는 중 에러가 발생했습니다."); // API 호출 실패

    private final int code;
    private final String message;

    SchedLoadStateCode(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
