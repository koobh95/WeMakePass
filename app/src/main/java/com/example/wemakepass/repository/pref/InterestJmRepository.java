package com.example.wemakepass.repository.pref;

import android.text.TextUtils;

import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.model.data.InterestJmModel;
import com.example.wemakepass.data.pref.InterestJmPreferences;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *  InterestJmPreferences에서 데이터를 불러오거나 저장하는 역할을 하는 Repository 클래스다. LiveData나
 * 네트워크 작업을 하지 않기 때문에 BaseRepository는 상속받지 않는다.
 *
 * @author BH-Ku
 * @since 2023-11-08
 */
public class InterestJmRepository {
    private SingleLiveEvent<List<InterestJmModel>> interestJmListLiveData;

    public static final int MAX_ELEMENT = 5; // 삽입 가능한 데이터 최대 개수

    private final String TAG = "TAG_InterestRepository";

    public InterestJmRepository(){
        interestJmListLiveData = new SingleLiveEvent<>();
        initInterestJmListLiveData();
    }

    /**
     * - 기존 관심 종목 목록에 데이터를 추가한다.
     * - 최대 갯수 검사, 중복 검사는 이 메서드를 호출하는 ViewModel에서 수행하므로 별도의 검사는 수행하지 않는다.
     * - submitList로 RecyclerView를 Update하기 위해서는 새로운 List를 submit해야 하므로 리스트의 내용을
     *  복사한 새로운 리스트를 만들고 데이터를 추가한다. 그 후 업데이트를 수행하는 메서드를 호출한다.
     *
     * @param item 추가될 데이터
     */
    public void addItem(InterestJmModel item) {
        List<InterestJmModel> newList = new ArrayList<>(interestJmListLiveData.getValue());
        newList.add(item);
        updateList(newList);
    }

    /**
     * - 기존 관심 종목에서 특정 데이터를 삭제한다.
     * - 새로운 리스트를 만드는 이유는 addItem과 같다.
     * - RecyclerView의 특정 Item을 삭제하기 위해서 Item을 빠르게 연타하면 두 번째부터는 -1 값이 반환되는 것을
     *  확인했다. 원인은 정확히는 모르겠으나 View가 화면에서 제거될 때 애니메이션을 발생시키며 삭제되는데
     *  애니메이션이 실행되기 전에 이미 값은 삭제되었기 때문에 -1을 반환하는 것이 아닌가 생각한다. 따라서 -1이
     *  넘어 오는 경우 사용자가 아이템이 화면에서 사라지기 전에 같은 Item에 대하여 이벤트를 2번 이상 발생시킨
     *  것으로 보고 아무런 동작도 하지 않도록 조치했다.
     *
     * @param removeItemPosition 삭제될 아이템의 index
     */
    public void removeItem(int removeItemPosition){
        if(removeItemPosition == -1)
            return;
        List<InterestJmModel> newList = new ArrayList<>(interestJmListLiveData.getValue());
        newList.remove(removeItemPosition);
        updateList(newList);
    }

    /**
     * - 관심 종목이 추가/삭제되는 경우 이 메서드를 호출하여 List를 JsonArray로 변환, 다시 String으로 변환하여
     *  SharedPreferences에 저장한다.
     *
     * @param newList 변경 사항이 반영되어 있는 새로운 리스트
     */
    private void updateList(List<InterestJmModel> newList) {
        try {
            JSONArray jsonArray = new JSONArray();
            for(InterestJmModel interestJmModel : newList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("jmCode", interestJmModel.getJmCode());
                jsonObject.put("jmName", interestJmModel.getJmName());
                jsonArray.put(jsonObject);
            }
            InterestJmPreferences.setJmData(jsonArray.toString()); // save
            interestJmListLiveData.setValue(newList);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * - 내부 메서드인 loadInterestJmList를 호출하여 Preferences 내의 데이터를 받아 오고 null이 아닐 경우
     *  읽어온 리스트를 그대로 LiveData에 초기화한다.
     * - 읽어온 데이터가 없을 경우 LiveData를 Observing하는 곳에서 NullPointException이 발생하지 않도록
     *  비어 있는 ArrayList를 할당한다.
     * - 이 메서드는 class가 초기화될 때 생성자에 의해 단 한 번 호출된다.
     *
     *
     * @return
     */
    private void initInterestJmListLiveData(){
        List<InterestJmModel> list = getInterestJmList();
        if(list == null)
            interestJmListLiveData.setValue(new ArrayList<>());
        else
            interestJmListLiveData.setValue(list);
    }

    /**
     * - Preferences로 부터 InterestJmList를 읽어온다.
     * - 내부에서 호출하는 경우 LiveData 초기화가 목적이며 외부(ViewModel)에서 호출되는 경우 현재 메모리 상에서
     *  가지고 있는 데이터와 실제 데이터를 비교하는 것이 목적이다.
     * - 불러온 값은 JsonArray가 String으로 변환된 상태이므로 다시 JsonArray로 변환한 뒤 Json을 하나씩
     *  읽고 파싱하여 List로 변환한다.
     */
    public List<InterestJmModel> getInterestJmList(){
        final String jsonString = InterestJmPreferences.getJmData();
        if(TextUtils.isEmpty(jsonString))
            return null;

        List<InterestJmModel> list = new ArrayList<>();
        try {
            JsonParser jsonParser = new JsonParser();
            JsonElement json = jsonParser.parse(jsonString);
            JsonArray jsonArray = json.getAsJsonArray();

            for(JsonElement jsonElement : jsonArray){
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                InterestJmModel interestJmModel = new InterestJmModel(
                        jsonObject.get("jmCode").getAsString(),
                        jsonObject.get("jmName").getAsString());
                list.add(interestJmModel);
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 최초 로딩 시 반드시 초기화되기 때문에 타 LiveData와 달리 null을 체크하여 초기화하지 않는다.
     * @return
     */
    public SingleLiveEvent<List<InterestJmModel>> getInterestJmListLiveData() {
        return interestJmListLiveData;
    }
}
