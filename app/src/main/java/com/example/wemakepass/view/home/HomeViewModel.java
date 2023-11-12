package com.example.wemakepass.view.home;

import com.example.wemakepass.base.BaseViewModel;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.enums.SchedLoadStateCode;
import com.example.wemakepass.data.model.data.InterestJmModel;
import com.example.wemakepass.data.model.data.InterestJmSchedModel;
import com.example.wemakepass.repository.JmSchedRepository;
import com.example.wemakepass.repository.pref.InterestJmRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * HomeFragment의 ViewModel 클래스
 *
 * @author BH-Ku
 * @since 2023-11-09
 */
public class HomeViewModel extends BaseViewModel {
    private final InterestJmRepository interestJmRepository;
    private final JmSchedRepository jmSchedRepository;

    private final SingleLiveEvent<List<InterestJmModel>> interestJmListLiveData;

    private final String TAG = "TAG_HomeViewModel";

    public HomeViewModel() {
        interestJmRepository = new InterestJmRepository();
        jmSchedRepository = new JmSchedRepository(getNetworkErrorLiveData());
        interestJmListLiveData = interestJmRepository.getInterestJmListLiveData();
    }

    /**
     * 관심 종목 일정 리스트를 조건에 맞게 정렬한다.
     *
     * 정렬 기준은 다음과 같다.
     * 1. "일정을 불러왔는가?"를 1차 분류 기준으로 한다.
     *  - 둘 중 일정을 불러오지 못한 종목이 있을 경우 일정을 불러온 종목의 우선순위를 높게 잡는다.
     *  - 둘 다 일정을 불러오지 못한 종목일 경우 먼저 삽입된 종목의 우선순위를 높게 잡는다.
     * 2. 둘 다 일정이 존재하는 경우 시험 날짜를 분류 기준으로 한다.
     *  - 모든 종목은 시험 응시 날짜가 필기, 실기 시험이 치뤄지는 도중 탄력적으로 정해지기 때문에 시험 날짜가
     *   정확하게 정해져 있지 않고 n~m일로 되어 있다. 또한 정확한 시험 날짜가 정해지더라도 API로는 언제 시험이
     *   치뤄지는지 알 수 없다. 따라서 앞 날짜인 n일을 기준으로 더 빠른 날을 가진 종목이 우선순위가 높다고 본다.
     * 3. 두 종목에서 각각 비교할 날짜를 선별한다.
     *  - 기술 자격 시험은 2차까지 있으며 전문 자격 시험은 1차까지 밖에 없다. 따라서 비교 대상이 기술 자격 시험인
     *   경우 2차, 전문 자격 시험인 경우 1차를 기준으로 한다.
     * 4. 두 날짜를 비교하여 더 빠른 날짜를 가진 종목의 우선 순위를 높게 잡는다.
     *
     * @param list 정렬이 필요한 일정 리스트
     */
    public void sortInterestJmSchedList(List<InterestJmSchedModel> list) {
        list.sort((jmSched1, jmSched2) -> {
            if(jmSched1.getSchedLoadStateCode() == SchedLoadStateCode.OK
                    && jmSched2.getSchedLoadStateCode() != SchedLoadStateCode.OK)
                return -1;
            else if(jmSched1.getSchedLoadStateCode() != SchedLoadStateCode.OK
                    && jmSched2.getSchedLoadStateCode() == SchedLoadStateCode.OK)
                return 1;
            else if(jmSched1.getSchedLoadStateCode() != SchedLoadStateCode.OK
                    && jmSched2.getSchedLoadStateCode() != SchedLoadStateCode.OK){
                return 1;
            }

            LocalDate o1Date, o2Date; // 비교할 날짜를 아래에서 초기화

            // 전문 시험은 1차 시험일을, 기술 시험은 2차 시험일을 초기화한다.
            if((o1Date = jmSched1.getJmSchedDTO().getPracExamStartDate()) == null)
                o1Date = jmSched1.getJmSchedDTO().getDocExamStartDate();
            if((o2Date = jmSched2.getJmSchedDTO().getPracExamStartDate()) == null)
                o2Date = jmSched2.getJmSchedDTO().getDocExamStartDate();

            if(o1Date.isAfter(o2Date))
                return -1;
            if(o2Date.isAfter(o1Date))
                return 1;
            return 0;
        });
    }

    /**
     * - HomeFragment 시작 초기에 Preferences에 저장된 관심 종목이 있는지 확인하여 존재할 경우 이 메서드를
     *  호출하여 각 종목 코드에 대한 일정 데이터를 요청한다.
     */
    public void loadInterestJmSchedule() {
        final List<InterestJmModel> jmList = interestJmListLiveData.getValue();

        for(InterestJmModel interestJmModel : jmList)
            addDisposable(jmSchedRepository.requestJmSched(interestJmModel));
    }

    public SingleLiveEvent<List<InterestJmModel>> getInterestJmListLiveData() {
        return interestJmRepository.getInterestJmListLiveData();
    }

    public SingleLiveEvent<List<InterestJmSchedModel>> getInterestJmSchedListLiveData() {
        return jmSchedRepository.getInterestJmSchedListLiveData();
    }
}
