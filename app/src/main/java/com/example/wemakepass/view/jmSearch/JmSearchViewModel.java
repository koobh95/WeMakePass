package com.example.wemakepass.view.jmSearch;

import android.text.TextUtils;
import android.view.View;

import com.example.wemakepass.base.BaseViewModel;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.model.dto.JmInfoDTO;
import com.example.wemakepass.data.pref.AppDataPreferences;
import com.example.wemakepass.data.util.StringUtils;
import com.example.wemakepass.repository.JmRepository;
import com.example.wemakepass.repository.pref.SearchLogRepository;

import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * JmSearchFragment의 ViewModel 클래스.
 *
 * @author BH-Ku
 * @since 2023-11-13
 */
public class JmSearchViewModel extends BaseViewModel {
    private SingleLiveEvent<String> keywordLiveData;
    private Disposable searchDisposable;

    private SearchLogRepository searchLogRepository;
    private JmRepository jmRepository;

    private final int KEYWORD_LEN_MIM = 2; // 검색어 최소 길이(공백 미포함)
    private final int KEYWORD_LEN_MAX = 20; // 검색어 최대 길이(공백 미포함)
    private final String TAG = "TAG_JmSearchViewModel";

    public JmSearchViewModel() {
        jmRepository = new JmRepository(getNetworkErrorLiveData());
        searchLogRepository = new SearchLogRepository(AppDataPreferences.KEY_EXAM_JM_SEARCH_LOG);
    }

    /**
     * 검색 ImageButton을 눌렀을 때 호출되는 콜백 메서드로 xml에서 바인딩되어 있다.
     *
     * @param view
     */
    public void onSearchButtonClick(View view){
        search();
    }

    /**
     * - EditText에 존재하는 문자열(keyword)를 토대로 검색을 수행한다.
     * - 이미 검색을 진행 중인 경우 기존 작업은 dispose 시킨 뒤 뒤에 발생항 검색을 수행한다.
     * - 데이터베이스에서 종목 이름 데이터는 애초에 공백이 없기 때문에 keyword는 공백을 허용하지 않는다. 따라서
     *  검색어를 검증할 때 공백을 모두 삭제한 후 검증을 진행한다.
     * - 데이터베이스에서 종목 이름 데이터에 일부 영어를 포함한 데이터는 모두 대문자로 되어 있기 때문에 소문자가
     *  있을 경우 모두 대문자로 변경한다.
     * - 검색어가 검색 조건을 만족했을 경우 검색을 수행하는 동시에 검색어 기록에도 추가한다.
     */
    public void search() {
        if(searchDisposable != null && !searchDisposable.isDisposed())
            searchDisposable.isDisposed();

        String keyword = StringUtils.removeAllSpace(keywordLiveData.getValue());
        if(!isValidKeyword(keyword))
            return;

        searchLogRepository.addLog(keyword);
        searchDisposable = jmRepository.
                requestSearchForJmWithExamInfo(keyword.toUpperCase(Locale.KOREA));
        addDisposable(searchDisposable);
    }

    /**
     * 검색 기록 목록에서 특정 기록을 삭제한다.
     *
     * @param removeIndex 삭제할 아이템의 index
     */
    public void deleteLog(int removeIndex){
        searchLogRepository.deleteLog(removeIndex);
    }

    /**
     * 검색 기록 목록을 모두 삭제한다.
     */
    public void deleteLogAll() {
        searchLogRepository.clear();
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

    public SingleLiveEvent<List<String>> getSearchLogListLiveData() {
        return searchLogRepository.getSearchLogListLiveData();
    }

    public SingleLiveEvent<String> getKeywordLiveData() {
        if(keywordLiveData == null)
            keywordLiveData = new SingleLiveEvent<>();
        return keywordLiveData;
    }

    public SingleLiveEvent<List<JmInfoDTO>> getJmInfoListLiveData() {
        return jmRepository.getJmInfoListLiveData();
    }
}
