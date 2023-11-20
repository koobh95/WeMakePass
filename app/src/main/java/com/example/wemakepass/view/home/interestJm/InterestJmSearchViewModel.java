package com.example.wemakepass.view.home.interestJm;

import android.text.TextUtils;

import com.example.wemakepass.base.BaseViewModel;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.model.data.InterestJmModel;
import com.example.wemakepass.data.model.dto.JmInfoDTO;
import com.example.wemakepass.data.util.StringUtils;
import com.example.wemakepass.repository.JmRepository;
import com.example.wemakepass.repository.pref.InterestJmRepository;

import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * InterestJmSearchActivity의 ViewModel 클래스.
 *
 * @author BH-Ku
 * @since 2023-11-08
 */
public class InterestJmSearchViewModel extends BaseViewModel {
    private SingleLiveEvent<String> keywordLiveData;

    private InterestJmRepository interestJmRepository;
    private JmRepository jmRepository;
    private Disposable searchDisposable;

    private final int KEYWORD_LEN_MIM = 2; // 검색어 최소 길이(공백 미포함)
    private final int KEYWORD_LEN_MAX = 20; // 검색어 최대 길이(공백 미포함)
    private final String TAG = "TAG_InterestJmSearchViewModel";

    public InterestJmSearchViewModel() {
        interestJmRepository = new InterestJmRepository();
        jmRepository = new JmRepository(getNetworkErrorLiveData());
    }

    /**
     * - TextInputEditText에서 검색어를 입력하고 "완료"를 누를 경우 호출되는 메서드다.
     * - 검색 작업이 중복 호출되지 않도록 백그라운드 작업이 실행 중인지 확인한다. 이미 검색이 실행 중일 경우 기존
     *  작업을 취소한 뒤 새로 들어온 검색을 요청한다.
     * - 한국산업인력공단에서 제공하는 종목 정보 611개 중 6개를 제외한 모든 데이터(종목 이름)가 띄어 쓰기를 하고
     *  있지 않다. 따라서 검색을 수월하게 하기 위해 공백을 모두 지운 채로 검색을 수행한다.
     * - 공백 입력은 허용하지만 실제 검증, 요청 단계에서는 공백을 취급하지 않기 때문에 공백을 입력해도 1글자로
     *  보지 않는다.
     */
    public void search() {
        if(searchDisposable != null && !searchDisposable.isDisposed())
            searchDisposable.isDisposed();

        final String keyword = StringUtils.removeAllSpace(keywordLiveData.getValue());
        if(!isValidKeyword(keyword))
            return;

        addDisposable(searchDisposable =
                jmRepository.requestSearch(keyword.toUpperCase(Locale.KOREA)));
    }

    /**
     *  검색어에 대해 유효한 값인지 검증을 수행한다. 검증 조건은 값이 존재하는가, 값의 최소 최대 길이에 적합한가에
     * 대해서만 검증한다.
     *
     * @param keyword 검색어
     * @return 검색어의 유효성 여부
     */
    private boolean isValidKeyword(String keyword) {
        if(TextUtils.isEmpty(keyword)){
            systemMessageLiveData.setValue("검색어를 입력해주세요,");
            return false;
        }

        if(keyword.length() > KEYWORD_LEN_MAX || keyword.length() < KEYWORD_LEN_MIM){
            systemMessageLiveData.setValue("검색어는 " + KEYWORD_LEN_MIM + "자 이상 "
                    + KEYWORD_LEN_MAX + "자 이하로 입력해주세요.");
            return false;
        }

        return true;
    }

    /**
     * - 종목 검색 결과에서 특정 Item의 "+" 버튼을 누를 경우 호출되는 메서드다.
     * - 리스트에 있는 데이터의 최대 수를 확인하여 삽입 가능한 상태인지 확인한 후 삽입을 결정한다.
     * - 리스트에 값이 하나라도 존재할 경우 삽입하려는 값이 중복인지 확인한 후 중복이 아니라면 삽입한다.
     *
     * @param interestJmModel 삽입하려는 데이터
     */
    public void addInterestJmItem(InterestJmModel interestJmModel) {
        final List<InterestJmModel> list = interestJmRepository.getInterestJmListLiveData().getValue();
        if(list.size() == InterestJmRepository.MAX_ELEMENT){
            systemMessageLiveData.setValue("최대 5개까지 추가할 수 있습니다.");
            return;
        }

        if(list.size() != 0){
            for(InterestJmModel item : list){
                if(item.getJmCode().equals(interestJmModel.getJmCode())){
                    systemMessageLiveData.setValue("이미 추가된 종목입니다.");
                    return;
                }
            }
        }

        interestJmRepository.addItem(interestJmModel);
    }

    /**
     * - 관심 종목에서 특정 아이템을 삭제하기 위해서 아이템의 x 버튼을 누를 경우 호출되는 메서드다.
     *
     * @param position 삭제하고자 하는 아이템의 index
     */
    public void removeInterestJmItem(int position) {
        interestJmRepository.removeItem(position);
    }

    public SingleLiveEvent<String> getKeywordLiveData() {
        if(keywordLiveData == null)
            keywordLiveData = new SingleLiveEvent<>();
        return keywordLiveData;
    }

    public SingleLiveEvent<List<InterestJmModel>> getInterestJmListLiveData() {
        return interestJmRepository.getInterestJmListLiveData();
    }

    public SingleLiveEvent<List<JmInfoDTO>> getJmInfoListLiveData() {
        return jmRepository.getJmInfoListLiveData();
    }
}
