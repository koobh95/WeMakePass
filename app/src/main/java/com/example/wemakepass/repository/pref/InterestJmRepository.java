package com.example.wemakepass.repository.pref;

import android.text.TextUtils;

import com.example.wemakepass.base.BaseAppDataRepository;
import com.example.wemakepass.common.SingleLiveEvent;
import com.example.wemakepass.data.model.data.InterestJmModel;
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
 * InterestJmPreferences에서 데이터를 불러오거나 저장하는 역할을 하는 Repository 클래스다.
 *
 * @author BH-Ku
 * @since 2023-12-02
 */
public class InterestJmRepository extends BaseAppDataRepository<InterestJmModel> {
    private final String ARG_JM_CODE = "boardNo";
    private final String ARG_JM_NAME = "boardName";
    public static final int MAX_ELEMENT = 5; // 삽입 가능한 데이터 최대 개수

    public InterestJmRepository(String prefName) {
        super(prefName);
    }

    @Override
    protected InterestJmModel parseJsonObject(JsonObject jsonObject) throws Exception {
        return new InterestJmModel(
                jsonObject.get(ARG_JM_CODE).getAsString(),
                jsonObject.get(ARG_JM_NAME).getAsString());
    }

    @Override
    protected JSONObject createJson(InterestJmModel interestJmModel) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ARG_JM_CODE, interestJmModel.getJmCode());
        jsonObject.put(ARG_JM_NAME, interestJmModel.getJmName());
        return jsonObject;
    }

    /**
     *  현재 가지고 있는 관심 종목 데이터가 변경되었는지 비교하기 위해 현재 설정 파일에 저장되어 있는 데이터를
     * 읽어와 반환한다.
     *
     * @return 설정 파일에서 읽어 온 데이터
     */
    public List<InterestJmModel> getInterestJmData() {
        return super.loadPrefData();
    }
}
