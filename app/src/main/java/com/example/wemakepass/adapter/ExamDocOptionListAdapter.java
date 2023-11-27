package com.example.wemakepass.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.adapter.viewholder.ExamDocOptionViewHolder;
import com.example.wemakepass.databinding.ItemExamDocOptionBinding;
import com.example.wemakepass.listener.OnItemClickListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;

/**
 * - ExamActivity > ExamDocFragment에서 객관식 문제에 대한 선택지 리스트를 출력하는 RecyclerView의
 *  Adapter 클래스다.
 * - 화면에 표시할 문제가 바뀌면 그에 따라 선택지가 갱신되는데 이 때 굉장히 높은 확률로 모든 데이터가 업데이트된다.
 *  그렇기 때문에 ListAdapter를 사용해 데이터를 비교하는 것조차 낭비라고 생각해 RecyclerView.Adapter를
 *  상속받았다.
 *
 * @author BH-Ku
 * @since 2023-11-23
 */
public class ExamDocOptionListAdapter extends RecyclerView.Adapter<ExamDocOptionViewHolder> {
    private String[] optionArray; // 선택지 리스트
    // 선택지에 사용자가 선택한 답이 있을 경우 실제 index 값을 가지며 선택된 값이 없으면 -1을 가진다.
    private int answer;

    private OnItemClickListener onItemClickListener;

    @NonNull
    @Override
    public ExamDocOptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExamDocOptionViewHolder(ItemExamDocOptionBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ExamDocOptionViewHolder holder, int position) {
        holder.bind(optionArray[position], answer == position);
        holder.setOnItemClickListener(onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return optionArray == null ? 0 : optionArray.length;
    }

    /**
     * 한 문제의 선택지는 JsonString 형태로 DB에 저장되어 있기 때문에 String 형태로 데이터를 받는다.
     *
     * @param optionJsonString Json 형태로 선택지 데이터
     */
    public void setOptionList(String optionJsonString) {
        parseOptionJsonString(optionJsonString);
    }

    /**
     *  현재 표시하고자 하는 선택지에서 이전에 사용자가 선택한 답을 세팅한다. 이전에 사용자가 값을 선택한 적이
     * 없을 경우 -1이 초기화되어 있다.
     *
     * @param answer 이전에 사용자가 선택한 선택지
     */
    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * - Json으로 이루어진 선택지를 파싱한다.
     * - 선택지의 순번을 가지는 key는 ["opt" + n]과 같은 문자열로 이루어져 있다.
     * - 선택지는 일반적으로 4개 내지 5개로 유동적이기 때문에 상수가 아닌 JsonObject의 개수를 사용하여
     * 반복문을 수행하고 값을 바인딩한다.
     *
     * @param json 선택지 데이터
     */
    private void parseOptionJsonString(String json) {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(json);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if(optionArray == null)
            optionArray = new String[jsonObject.size()];
        for(int i = 0; i < jsonObject.size(); i++)
            optionArray[i] = jsonObject.get("opt" + (i+1)).getAsString();
    }
}
