package com.example.wemakepass.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.adapter.viewholder.ExamAnswerViewHolder;
import com.example.wemakepass.data.model.data.ExamDocAnswerModel;
import com.example.wemakepass.databinding.ItemExamAnswerBinding;
import com.example.wemakepass.listener.OnItemClickListener;

import java.util.List;

/**
 * - ExamActivity > ExamDocFragment에서 답안 리스트를 출력하는 RecyclerView의 Adapter 클래스.
 * - 이 리스트는 객관식 선택지에서 선택된 값이 세팅 및 갱신되는데 이 경우 업데이트 대상 아이템의 위치가 명확하므로
 *  ListAdapter가 아닌 RecyclerView.Adapter를 상속받고 있다.
 *
 * @author BH-Ku
 * @since 2023-11-23
 */
public class ExamDocAnswerListAdapter extends RecyclerView.Adapter<ExamAnswerViewHolder> {
    private final List<ExamDocAnswerModel> list;
    private OnItemClickListener onItemClickListener;

    public ExamDocAnswerListAdapter (List<ExamDocAnswerModel> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ExamAnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExamAnswerViewHolder(ItemExamAnswerBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ExamAnswerViewHolder holder, int position) {
        holder.setAnswer(list.get(position).getAnswer());
        holder.setOnItemClickListener(onItemClickListener);
        holder.setScoringResult(list.get(position).getAnswerState());
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    /**
     * 스크롤될 때 재사용을 막기 위해 Overriding된 메서드다.
     *
     * @param position position to query
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
