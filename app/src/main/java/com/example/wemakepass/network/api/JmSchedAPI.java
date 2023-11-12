package com.example.wemakepass.network.api;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * - 특정 시험 종목에 관한 일정을 조회하는 작업을 모아놓은 API 인터페이스.
 * - 이 API는 공공 데이터 포털에서 제공하는 두 개의 Service Key 중 Decoding 키로만 데이터를 요청할 수 있다.
 *  Encoding 키는 요청이 거부되는 특이사항이 있음.
 *
 *  특정 시험 종목의 시험 일정은 다음과 같은 특징을 갖는다. (일정 관련 모든 데이터를 파싱하여 통계를 낸 것임.)
 * - 종목은 계열에 따라 "기술" 자격 시험과 "전문" 자격 시험으로 나뉘는데 전문 자격의 경우 1차 시험만 시행하고
 *  기술 자격은 1, 2차 모두 시행한다.
 * - 모든 시험은 시험 회차가 누적되지만 0회차를 갖는 특수 종목이 있다. 국가 기술 자격인 기능사 자격증 152개
 *  중 93개 종목에서는 "산업 수요 맞춤형 고등학교 및 특성화 고등학교 필기시험 면제자 검정"라는 명목하에
 *  연 1회 특수한 일정인 0회차를 시행한다. 특수한 시험인 만큼 일반인은 필기 면제자라 해도 응시가 불가능하다.
 *  따라서 이 일정은 일정 선택 대상에서 제외한다.
 * - 통계를 내 본 결과 총 611 종목이 있으며 기술 자격은 511개, 전문 자격은 100개 존재한다 이 중 폐지 혹은
 *  통합된 종목이 기술 자격에 31개, 전문 자격에 7개 존재한다.
 *
 * @author BH-Ku
 * @since 2023-11-10
 */
public interface JmSchedAPI {
    /**
     *  이 값들은 데이터를 페이징 형태로 조회할 때 한 페이지에 담을 수 있는 최대 데이터 개수를 의미한다. 2023년
     * 08월, "종목별 시험 일정 조회" 기준으로 총 611개의 데이터 중 가장 많은 일정을 가진 종목은
     * "3D프린터운용기능사"로 총 9개였다. 이 수는 특수 시험 일정 1건와 정기 시험 일정(총 4회) 4건, 빈자리 추가
     * 접수 4건을 합한 값이다. 그럼에도 불구하고 20을 설정한 이유는 빈자리 추가 접수의 경우 기간 내에 자리가 모두
     * 채워지지 않으면 새로운 일정이 추가되는 경우가 있었기 때문에 예외 상황을 고려, 20으로 지정하였다.(타 종목의
     * 일정을 조회하여 확인한 사례)
     */
    int PAGE_NO = 1;
    int NUM_OF_ROWS = 20;

    /**
     * 특정 종목에 대한 시험 일정을 조회한다.
     *
     * @param serviceKey 공공 데이터 포털에서 발급받은 서비스 키
     * @param dataFormat 데이터 형식
     * @param pageNo 조회된 데이터가 많을 경우 페이지 단위로 나뉘게 되는데 몇 페이지의 데이터를 조회할 것인가
     * @param numOfRows 페이징으로 데이터를 조회하는 경우 한 페이지에 몇 개씩 데이터를 표시할 것인가
     * @param year 몇 년도 시험 일정을 조회할 것인가
     * @param jmCode 조회하려는 종목의 식별 코드
     * @return
     */
    @GET("B490007/qualExamSchd/getQualExamSchdList")
    Observable<Response<String>> getJmSched(
            @Query("serviceKey") String serviceKey,
            @Query("dataFormat") String dataFormat,
            @Query("pageNo") int pageNo,
            @Query("numOfRows") int numOfRows,
            @Query("implYy" ) int year,
            @Query("jmCd") String jmCode);
}
