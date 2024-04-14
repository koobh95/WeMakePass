package com.example.wemakepass.base;

import android.text.TextUtils;

import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.pref.AppDataPreferences;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * - SharedPreferences에 데이터를 저장할 때 사용하는 Base Class로 String이 아닌 객체 타입의 데이터를 저장하는
 *  경우에 사용된다.
 * - 데이터는 Object → JSONObject로 변환한 뒤 JSONArray에 추가, 최종적으로 JSONArray를 String으로 변환한
 *  값을 저장한다.
 * - 저장할 SharedPreferences의 이름은 AppDataPreferences 클래스가 가진 문자열 멤버 상수로 결정된다. 사용되는
 *  모든 설정 파일의 이름을 상수로 가지고 있기 때문에 이 클래스를 상속받은 자식 클래스를 객체화할 때 사용할 설정
 *  파일의 이름을 넘기면 된다.
 * - 데이터 초기 로딩, 추가, 삭제, 저장 등의 공통적인 기능을 담고 있으며 이 클래스를 상속받는 자식 클래스들은 오로지
 *  JSON을 파싱하는 메서드, Element를 JSONObject로 변환하는 메서드만 구현하면 된다.
 *
 * @author BH-Ku
 * @since 2023-12-02
 * @param <E> 저장하고자 하는 객체 타입
 */
public abstract class BaseAppDataRepository<E> {
    private SingleLiveEvent<List<E>> elementListLiveData;
    private final String PREF_NAME;

    protected BaseAppDataRepository(String prefName){
        elementListLiveData = new SingleLiveEvent<>();
        PREF_NAME = prefName;
        initElementListLiveData();
    }

    /**
     * - 리스트의 끝에 데이터를 추가한다.
     * - 최대 갯수 검사, 중복 검사는 이 메서드를 호출하는 ViewModel에서 수행하므로 별도의 검사는 수행하지 않는다.
     * - submitList로 RecyclerView를 Update하기 위해서는 새로운 List를 submit해야 하므로 리스트의 내용을
     *  복사한 새로운 리스트를 만들고 데이터를 추가한다. 그 후 업데이트를 수행하는 메서드를 호출한다.
     *
     * @param e 추가될 데이터
     */
    public void addItem(E e){
        List<E> newList = new ArrayList<>(elementListLiveData.getValue());

        for(int i = 0; i < newList.size(); i++){
            E ee = newList.get(i);
            if(e.equals(ee)){
                newList.remove(ee);
                break;
            }
        }

        newList.add(e);
        updateList(newList);
    }

    /**
     * - 특정 데이터를 삭제한다.
     * - RecyclerView의 특정 Item을 삭제하기 위해서 관련 버튼을 빠르게 연타하면 실제로 아이템이 화면상에서
     *  사라지기 전까지는 두 번째부터 -1 값을 반환한다. 특히 애니메이션을 사용해 천천히 사라지는 아이템이라면
     *  이러한 현상이 더욱 빈번하게 발생한다. 이와 관련해 발생할 수 있는 Exception을 방지하기 위해서 파라미터의
     *  값이 -1인 경우 메서드를 수행하지 않는다.
     *
     * @param deleteIdx 삭제할 아이템의 index
     */
    public void deleteItem(int deleteIdx){
        if(deleteIdx == -1)
            return;
        List<E> newList = new ArrayList<>(elementListLiveData.getValue());
        newList.remove(deleteIdx);
        updateList(newList);
    }

    /**
     * - 특정 아이템이 선택되었을 때 그 아이템을 최상위로 이동시키는 메서드다.
     * - 리스트에 아이템에 2개 미만일 경우 변경이 의미가 없으므로 아무 작업도 하지 않는다.
     * - 리스트에 아이템이 2개 이상일 경우 선택된 아이템을 삭제한 뒤 삭제했던 아이템을 다시 삽입한다.
     *
     * @param selectedIndex 선택된 아이템의 index
     */
    public void reorderList(int selectedIndex) {
        List<E> newList = new ArrayList<>(getElementListLiveData().getValue());
        if(newList.size() < 2)
            return;
        E element = newList.remove(selectedIndex);
        newList.add(element);
        elementListLiveData.setValue(newList);
    }

    /**
     * - 데이터가 추가/삭제되는 경우 이 메서드를 호출하여 List를 JsonArray로 변환, 다시 String으로 변환하여
     *  SharedPreferences에 저장한다.
     *
     * @param newList 변경 사항이 반영되어 있는 새로운 리스트
     */
    protected void updateList(List<E> newList) {
        try{
            JSONArray jsonArray = new JSONArray();
            for(E e : newList)
                jsonArray.put(createJson(e));
            AppDataPreferences.setData(PREF_NAME, jsonArray.toString());
            elementListLiveData.setValue(newList);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 모든 데이터를 삭제한다.
     */
    public void clear() {
        AppDataPreferences.setData(PREF_NAME, "");
        elementListLiveData.setValue(new ArrayList<>());
    }

    /**
     * 클래스가 객체화될 때 생성자에 의해서 단 한 번 호출되어 리스트를 초기화한다.
     */
    private void initElementListLiveData() {
        elementListLiveData.setValue(loadPrefData());
    }

    /**
     * - LiveData에 초기 데이터를 세팅하기 위해 SharedPreferences로부터 데이터를 읽어온다.
     * - 불러온 값은 JsonArray가 String으로 변환된 상태이므로 다시 JsonArray로 변환한 뒤 Json을 하나씩 읽어
     *  파싱하여 List로 저장하고 반복 작업이 끝나면 리스트를 반환한다.
     *
     * @return SHaredPreferences로부터 읽어온 데이터
     */
    public List<E> loadPrefData() {
        final String jsonString = AppDataPreferences.getData(PREF_NAME);
        List<E> list = new ArrayList<>();

        if(TextUtils.isEmpty(jsonString))
            return list;
        try{
            JsonParser jsonParser = new JsonParser();
            JsonElement json = jsonParser.parse(jsonString);
            JsonArray jsonArray = json.getAsJsonArray();

            for(JsonElement jsonElement : jsonArray){
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                list.add(parseJsonObject(jsonObject));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public SingleLiveEvent<List<E>> getElementListLiveData() {
        return elementListLiveData;
    }

    /**
     * JsonObject를 제네릭 타입에 맞게 파싱한다.
     *
     * @param jsonObject SharedPreferences에 저장되어 있던 데이터로 파싱 대상이다.
     * @return JsonObejct를 제네릭 타입에 맞게 파싱한 객체
     */
    protected abstract E parseJsonObject(JsonObject jsonObject);

    /**
     * 제네릭 타입을 JsonObject로 변환한다.
     *
     * @param e SharedPreferences에 저장하기 위해 제네릭 타입 객체를 JSONObject로 변환한다.
     * @return 제네릭 타입 객체를 JSON으로 변환한 객체
     */
    protected abstract JSONObject createJson(E e) throws Exception;
}
