package com.example.wemakepass.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.adapter.viewholder.ExamOrganViewHolder;
import com.example.wemakepass.data.model.vo.ExamOrganVO;
import com.example.wemakepass.databinding.ItemExamOrganBinding;

import java.util.List;

/**
 * - MainActivity > HomeFragment에서 시험 주관 기관 목록을 출력하는 RecyclerView의 Adapter 클래스.
 * - 이 목록은 소스 코드상에서 데이터를 추가하지 않는 이상 변경될 일이 없기 때문에 ListAdapter를 사용하지 않았다.
 *
 * @author BH-Ku
 * @since 2023-11-13
 */
public class ExamOrganListAdapter extends RecyclerView.Adapter<ExamOrganViewHolder> {
    final List<ExamOrganVO> list;

    public ExamOrganListAdapter(List<ExamOrganVO> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ExamOrganViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExamOrganViewHolder(ItemExamOrganBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ExamOrganViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
}
