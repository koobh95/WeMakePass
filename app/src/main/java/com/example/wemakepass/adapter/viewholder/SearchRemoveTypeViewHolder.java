package com.example.wemakepass.adapter.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.databinding.ItemSearchRemoveTypeBinding;
import com.example.wemakepass.listener.OnItemClickListener;

/**
 * - 검색 결과를 텍스트로 표시하면서 우측에 X Button을 가지는 Item의 ViewHolder 클래스다.
 * - 검색 결과를 표시하는데 있어서 공용으로 사용된다.
 *
 * @author BH-Ku
 * @since 2023-11-17
 */
public class SearchRemoveTypeViewHolder extends RecyclerView.ViewHolder {
    private ItemSearchRemoveTypeBinding binding;

    public SearchRemoveTypeViewHolder(@NonNull ItemSearchRemoveTypeBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    /**
     * @param content Item에 표시할 문자열
     */
    public void setContent(String content) {
        binding.itemSearchRemoveTypeContentTextView.setText(content);
    }

    /**
     * Item을 클릭했을 때 처리할 이벤트 설정
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        binding.getRoot().setOnClickListener(v ->
                onItemClickListener.onItemClick(getAdapterPosition()));
    }

    /**
     * X 버튼을 클릭했을 때 처리할 이벤트 설정
     * @param onItemClickListener
     */
    public void setOnRemoveButtonClickListener(OnItemClickListener onItemClickListener) {
        binding.itemSearchRemoveTypeRemoveButton.setOnClickListener(v ->
                onItemClickListener.onItemClick(getAdapterPosition()));
    }
}
