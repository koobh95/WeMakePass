package com.example.wemakepass.adapter.viewholder;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.databinding.ItemExamDocOptionBinding;
import com.example.wemakepass.listener.OnItemClickListener;

/**
 * ExamActivity > ExamDocFragment에서 객관식 선택지 리스트를 구성하는 아이템의 ViewHolder 클래스
 *
 * @author BH-Ku
 * @since 2023-11-23
 */
public class ExamDocOptionViewHolder extends RecyclerView.ViewHolder {
    private ItemExamDocOptionBinding binding;

    public ExamDocOptionViewHolder(@NonNull ItemExamDocOptionBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    /**
     * - 선택지의 번호, 각 선택지에 대한 설명을 각각 TextView에 세팅한다.
     * - isAnswer의 여부에 따라 체크 ImageView의 Visibility를 조정한다.
     *
     * @param explanation 설명
     * @param isAnswer 이 문항이 정답으로 체크되어 있는가에 대한 여부
     */
    @SuppressLint("SetTextI18n")
    public void bind(String explanation, boolean isAnswer) {
        binding.itemExamDocOptionNumberTextView.setText("[" + (getAdapterPosition()+1) + "]");
        binding.itemExamDocOptionExplanationTextView.setText(explanation);
        binding.itemExamDocOptionCheckImageView.setVisibility(isAnswer ? View.VISIBLE : View.GONE);
    }

    /**
     * - 아이템을 클릭했을 때 발생시킬 이벤트를 설정한다.
     * - 설정되는 이벤트는 선택지의 아이템이 선택될 때 체크 ImageView의 Visibility를 조정하는 것인데 채점이
     *  이루어지고 난 후에는 이벤트가 발생하지 않아야 하므로 채점이 이루어지면 null이 세팅된다. 따라서 null일 경우
     *  이벤트 리스너를 설정하지 않고 참조가 존재할 경우에만 리스너를 세팅한다.
     *
     *
     * @param onItemClickListener 아이템을 클릭했을 때 발생시킬 이벤트
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if(onItemClickListener == null)
            binding.getRoot().setOnClickListener(null);
        else
            binding.getRoot().setOnClickListener(v ->
                    onItemClickListener.onItemClick(getAdapterPosition()));
    }
}
