package com.example.wemakepass.repository;

import android.util.Log;

import com.example.wemakepass.base.BaseRepository;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.enums.QualificationCode;
import com.example.wemakepass.data.enums.SchedLoadStateCode;
import com.example.wemakepass.data.model.data.InterestJmModel;
import com.example.wemakepass.data.model.data.InterestJmSchedModel;
import com.example.wemakepass.data.model.dto.JmSchedDTO;
import com.example.wemakepass.data.model.dto.NqeResponse;
import com.example.wemakepass.data.model.vo.ErrorResponse;
import com.example.wemakepass.network.api.JmSchedAPI;
import com.example.wemakepass.network.client.DataPortalClient;
import com.example.wemakepass.network.parser.JmSchedDTOParser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * - 종목 일정 조회 관련 네트워크 작업을 처리하는 Repository.
 * - 여기서 발생하는 Request에 대한 Response는 모두 수동으로 Parsing된다.
 *
 * @author BH-Ku
 * @since 2023-11-10
 */
public class JmSchedRepository extends BaseRepository {
    private SingleLiveEvent<List<InterestJmSchedModel>> interestJmSchedListLiveData;

    private final JmSchedAPI jmSchedAPI;
    private final JmSchedDTOParser nqeResponseParser;

    private final String TAG = "TAG_JmSchedRepository";

    public JmSchedRepository(SingleLiveEvent<ErrorResponse> networkErrorLiveData) {
        super(networkErrorLiveData);
        jmSchedAPI = DataPortalClient.getRetrofit().create(JmSchedAPI.class);
        nqeResponseParser = new JmSchedDTOParser();
    }

    /**
     * 특정 종목 코드(JmCode)에 해당하는 올해 시험 일정을 조회한다.
     *
     * @param interestJmModel 일정을 조회하기 위한 종목 정보를 가진 객체
     * @return
     */
    public Disposable requestJmSched(InterestJmModel interestJmModel) {
        return jmSchedAPI.getJmSched(
                        DataPortalClient.SERVICE_KEY_DEC,
                        DataPortalClient.DATA_FORMAT_JSON,
                        JmSchedAPI.PAGE_NO,
                        JmSchedAPI.NUM_OF_ROWS,
                        LocalDate.now().getYear(),
                        interestJmModel.getJmCode())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    NqeResponse<JmSchedDTO> nqeResponse = nqeResponseParser.parse(response.body());
                    if (nqeResponse.isSuccessful()) {
                        List<JmSchedDTO> jmSchedList = nqeResponse.getItems();
                        if (jmSchedList.size() == 0) { // 일정이 없음, 종목이 폐지되었거나 다른 종목에 통합됨.
                            addInterestJmSched(new InterestJmSchedModel(interestJmModel,
                                    SchedLoadStateCode.NOT_FOUND_SCHEDULE));
                        } else { // 일정이 존재함. 최신 일정을 확인
                            JmSchedDTO latestJmSched = getLatestSched(jmSchedList);
                            if (latestJmSched == null) { // 올해 더 이상 유효한 일정이 없음.
                                requestNextYearJmSched(interestJmModel); // 내년도 조회
                            } else { // 올해 유효한 일정이 있음.
                                addInterestJmSched(new InterestJmSchedModel(interestJmModel,
                                        latestJmSched));
                            }
                        }
                    } else {
                        addInterestJmSched(new InterestJmSchedModel(interestJmModel,
                                SchedLoadStateCode.API_ERROR));
                    }
                }, t -> {
                    addInterestJmSched(new InterestJmSchedModel(interestJmModel,
                            SchedLoadStateCode.NETWORK_ERROR));
                });
    }

    /**
     *  올해 일정이 조회되었으나 현재 날짜 기준 아직 치뤄지지 않은 시험이 없을 경우 내년 시험 일정 조회를 요청하는
     * 메서드다.
     *
     * @param interestJmModel 조회할 종목 정보
     */
    private void requestNextYearJmSched(InterestJmModel interestJmModel) {
        Disposable disposable = jmSchedAPI.getJmSched(
                        DataPortalClient.SERVICE_KEY_DEC,
                        DataPortalClient.DATA_FORMAT_JSON,
                        JmSchedAPI.PAGE_NO,
                        JmSchedAPI.NUM_OF_ROWS,
                        LocalDate.now().getYear() + 1, // 다음 년
                        interestJmModel.getJmCode())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    NqeResponse<JmSchedDTO> nqeResponse = nqeResponseParser.parse(response.body());
                    if (nqeResponse.isSuccessful()) {
                        List<JmSchedDTO> jmSchedList = nqeResponse.getItems();
                        if (jmSchedList.size() == 0) { // 다음 년도 시험 일정이 아직 발표되지 않음.
                            addInterestJmSched(new InterestJmSchedModel(interestJmModel,
                                    SchedLoadStateCode.NO_SCHEDULE));
                        } else { // 내년 일정이 존재, 가장 가까운 일정 조회
                            addInterestJmSched(new InterestJmSchedModel(interestJmModel,
                                    getLatestSched(jmSchedList)));
                        }
                    } else {
                        addInterestJmSched(new InterestJmSchedModel(interestJmModel,
                                SchedLoadStateCode.API_ERROR));
                    }
                }, t -> {
                    addInterestJmSched(new InterestJmSchedModel(interestJmModel,
                            SchedLoadStateCode.NETWORK_ERROR));
                });
    }

    /**
     * - 조회한 일정 목록으로부터 가장 최신의 일정을 찾아 반환한다.
     * - 시험 일정은 시간 역순으로 정렬되어 있다. 예를 들어 2월 1일이 첫 시험이고 12월 1일이 마지막 시험일 경우
     *  0번 인덱스에는 12월 1일 시험이 있다.
     *
     * 조회해온 일정 리스트에서 다음 조건을 만족하는 일정을 찾는다.
     *   - 현재 날짜 이후로 시행되지 않은 시험을 선택한다.
     *   - 시험 응시 시작 날짜를 기준으로 한다.
     *   - 전문 자격 시험의 경우 1차 시험만 시행하기 때문에 1차 시험을 기준으로 한다.
     *   - 기술 자격 시험의 경우 1, 2차 모두 시행하기 때문에 2차 시험을 기준으로 한다.
     *
     * @param list 한국산업인력공단으로부터 조회한 특정 종목의 시험 일정 목록
     * @return 현재 날짜 이후 가장 가까운 날짜의 시험,
     *         null = 올해 더 이상 유효한 시험 일정이 없음.
     */
    private JmSchedDTO getLatestSched(List<JmSchedDTO> list) {
        LocalDate today = LocalDate.now();

        try{
            for (int i = list.size() - 1; i >= 0; i--) {
                JmSchedDTO sched = list.get(i);

                if (sched.getImplSeq() == 0) // 0회차가 존재할 경우 반드시 0번 index에 있음.
                    continue;

                if (sched.getQualCode().equals(QualificationCode.TECHNICAL.getCode())) { // 기술 자격
                    if (sched.getPracExamStartDate().isAfter(today))
                        return sched;
                } else { // 전문 자격
                    if (sched.getDocExamStartDate().isAfter(today))
                        return sched;
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     *  관심 종목에 대한 일정이 존재하거나 존재하지 않는다면 존재하지 않는 이유 등이 결정났을 때 이 메서드를
     * 호출하여 LiveData List에 추가한다.
     *
     * @param interestJmSchedModel
     */
    private void addInterestJmSched(InterestJmSchedModel interestJmSchedModel) {
        List<InterestJmSchedModel> list = interestJmSchedListLiveData.getValue();
        if (list == null)
            list = new ArrayList<>();
        list.add(interestJmSchedModel);
        interestJmSchedListLiveData.setValue(list);
    }

    public SingleLiveEvent<List<InterestJmSchedModel>> getInterestJmSchedListLiveData() {
        if (interestJmSchedListLiveData == null)
            interestJmSchedListLiveData = new SingleLiveEvent<>();
        return interestJmSchedListLiveData;
    }
}
