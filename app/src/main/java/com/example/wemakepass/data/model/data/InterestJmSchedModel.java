package com.example.wemakepass.data.model.data;

import androidx.annotation.Nullable;

import com.example.wemakepass.data.enums.SchedLoadStateCode;
import com.example.wemakepass.data.model.dto.JmSchedDTO;

/**
 * - 종목 정보, 시험 일정 정보를 갖는 Model 클래스.
 * - 시험 일정 정보는 종목 정보에 대한 정보를 가지지 않으므로 시험 일정을 출력하면서 시험 정보를 같이 출력하려면
 *  두 데이터가 모두 필요하다. 두 데이터를 별도의 리스트로 관리하면서 인덱스를 통해 접근하는 방법도 있지만 두
 *  데이터를 모두 가지는 새로운 모델 클래스가 있는 편이 더 효율적이라 판단했다. 왜냐하면 일정을 모두 불러온 후
 *  일정의 존재 여부, 시험 응시일에 따라 재정렬이 한 번 이루어지기 때문이다.
 *
 * @author BH-Ku
 * @since 2023-11-10
 *
 */
public class InterestJmSchedModel {
    private InterestJmModel interestJmModel; // 관심 종목 정보
    private JmSchedDTO jmSchedDTO; // 관심 종목 정보에 대한 시험 일정 정보
    private SchedLoadStateCode schedLoadStateCode; // 일정 로딩 여부
    private boolean expanded; // MainActivity > HomeFragment에서 일정 펼침 여부

    /**
     * 특정 종목의 일정을 조회했을 때 유효한 일정이 있을 경우 이 생성자를 통해 초기화한다.
     *
     * @param interestJmModel 종목 정보
     * @param jmSchedDTO 일정 정보
     */
    public InterestJmSchedModel(InterestJmModel interestJmModel, JmSchedDTO jmSchedDTO) {
        this.interestJmModel = interestJmModel;
        this.jmSchedDTO = jmSchedDTO;
        schedLoadStateCode = SchedLoadStateCode.OK;
        expanded = false;
    }

    /**
     *  특정 종목의 일정을 조회했으나 모종의 이유로 일정을 찾지 못했을 경우 해당 종목의 정보와 일정을 찾기 못한
     * 이유(코드)를 이 생성자를 통해 초기화한다.
     *
     * @param interestJmModel 종목 정보
     * @param schedLoadStateCode 일정 조회 실패 이유(코드)
     */
    public InterestJmSchedModel(InterestJmModel interestJmModel, SchedLoadStateCode schedLoadStateCode) {
        this.interestJmModel = interestJmModel;
        this.schedLoadStateCode = schedLoadStateCode;
    }

    public InterestJmModel getInterestJmModel() {
        return interestJmModel;
    }

    public JmSchedDTO getJmSchedDTO() {
        return jmSchedDTO;
    }

    public SchedLoadStateCode getSchedLoadStateCode() {
        return schedLoadStateCode;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void changeExpandState() {
        expanded = !expanded;
    }

    /**
     * 종목 정보, 일정 정보를 토대로 일정에 대한 타이틀을 생성한다.
     *
     * @return 일정의 타이틀
     */
    public String getSchedTitle() {
        if(schedLoadStateCode != SchedLoadStateCode.OK)
            return null;
        return jmSchedDTO.getImplYy() + "년도 제 " + jmSchedDTO.getImplSeq() + "회 "
                + interestJmModel.getJmName();
    }

}
