package com.example.wemakepass.repository.pref;

import android.text.TextUtils;
import android.util.Log;

import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.pref.AppDataPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * - 검색 기록을 조회, 추가, 삭제, 갱신하는 역할을 하는 Repository 클래스다.
 * - 이 클래스는 SharedPreferences를 사용하여 데이터를 조작한다.
 * - 여러 클래스에서 공용으로 사용할 수 있도록 설정 파일(SharedPreferences)의 이름은 이 객체를 초기화하는
 *  클래스에서 결정한다.(생성자 파라미터)
 *
 * @author BH-Ku
 * @since 2023-11-14
 */
public class SearchLogRepository {
    private SingleLiveEvent<List<String>> searchLogListLiveData;

    private final String PREF_KEY;
    private final String TAG = "TAG_SearchLogRepository";

    /**
     *  객체가 초기화되면서 파라미터로 전달된 설정 파일의 이름을 토대로 데이터를 로딩한다. 즉시 데이터를 로딩하기
     * 때문에 로그 리스트 관련 LiveData는 getter가 아닌 생성자에서 초기화된다.
     *
     * @param preferenceKey 로딩할 로그 파일 이름으로 AppPreferences 클래스 내부에 있는 정적 변수가 사용된다.
     */
    public SearchLogRepository(String preferenceKey){
        assert !TextUtils.isEmpty(preferenceKey);
        this.PREF_KEY = preferenceKey;
        searchLogListLiveData = new SingleLiveEvent<>();
        load();
    }

    /**
     * - Log List에 새로운 로그를 추가한다.
     * - 클래스가 객체화될 때 생성자에서 리스트를 한 번 불러오기 때문에 리스트 참조 시 null check는 하지 않는다.
     * - 추가하려는 로그가 기존 리스트에 존재하는 경우 삭제하고 새 로그를 삽입한다. 중복을 허용하지 않아야 하는
     *  이유도 있고 최신 데이터를 마지막 위치에 유지하기 위함이기도 하다.
     * - 로그를 출력하는 RecyclerView를 submitList로 업데이트하기 위해 새로운 리스트를 생성한 뒤 데이터를
     *  조작한다.
     *
     * @param keyword 추가할 데이터
     */
    public void addLog(String keyword) {
        List<String> newList = new ArrayList<>(searchLogListLiveData.getValue());
        newList.remove(keyword);
        newList.add(keyword);
        update(newList);
    }

    /**
     * - 특정 인덱스에 있는 로그를 삭제한다.
     * - 만약 리스트에 데이터가 1개 밖에 없을 경우 clear를 호출한다.
     * - 로그를 출력하는 RecyclerView를 submitList로 업데이트하기 위해 새로운 리스트를 생성한 뒤 데이터를
     *  조작한다.
     *
     * @param removeIndex 삭제할 로그의 위치(index)
     */
    public void removeLog(int removeIndex) {
        List<String> newList = new ArrayList<>(searchLogListLiveData.getValue());
        if(newList.size() == 1){
            clear();
            return;
        }
        newList.remove(removeIndex);
        update(newList);
    }

    /**
     * - 로그 추가, 삭제가 발생했을 경우 이 메서드가 호출된다.
     * - 데이터 삭제 시점에서 리스트 내에 데이터가 없을 경우 이 메서드는 호출되지 않는다.
     * - 업데이트가 완료되었을 때 라이브 데이터를 관찰하는 측(Activity or Fragment)에서 변경 사항을 감지할 수
     * 있도록 새 리스트를 생성하여 라이브 데이터에 세팅해준다.
     *
     * @param newList 데이터 수정이 적용된 리스트
     */
    private void update(List<String> newList) {
        StringBuilder searchLogBuilder = new StringBuilder();

        for(int i = 0; i < newList.size(); i++){
            searchLogBuilder.append(newList.get(i));
            if(i+1 != newList.size())
                searchLogBuilder.append("|");
        }

        AppDataPreferences.setLogData(PREF_KEY, searchLogBuilder.toString());
        searchLogListLiveData.setValue(newList);
    }

    /**
     * - SharedPreference에 저장된 로그 데이터를 불러온다.
     * - 데이터를 읽어왔으나 저장된 데이터가 없을 경우 빈 리스트를 세팅한다.
     * - Log는 문자열 하나로 저장되어 있으며 파이프 라인 문자('|')로 구분되어 있다. 따라서 문자열을 읽어온 후
     *  파이프 라인 문자를 기준으로 파싱한 뒤 리스트를 생성한다. 그 후 라이브 데이터에 리스트를 세팅한다.
     */
    public void load() {
        String data = AppDataPreferences.getLogData(PREF_KEY);
        if(data.equals("")){
            searchLogListLiveData.setValue(new ArrayList<>());
            return;
        }

        String[] searchLogArray = data.split("\\|");
        List<String> searchLogList = new ArrayList<>(Arrays.asList(searchLogArray));
        searchLogListLiveData.setValue(searchLogList);
    }

    /**
     *  로그 삭제가 발생한 뒤 update 메서드에 진입했을 때 데이터가 존재하지 않을 경우 이 메서드가 호출된다. 저장할
     * 데이터가 없으므로 초기화한다.
     */
    private void clear() {
        AppDataPreferences.setLogData(PREF_KEY, "");
        searchLogListLiveData.setValue(new ArrayList<>());
    }

    public SingleLiveEvent<List<String>> getSearchLogListLiveData() {
        return searchLogListLiveData;
    }
}
