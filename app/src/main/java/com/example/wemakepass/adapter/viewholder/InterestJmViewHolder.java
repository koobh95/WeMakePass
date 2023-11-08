package com.example.wemakepass.adapter.viewholder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.data.model.data.JmInfoModel;
import com.example.wemakepass.databinding.ItemInterestJmBinding;
import com.example.wemakepass.listener.OnItemClickListener;

/**
 * InterestJmSearchActivity에서 검색 결과 리스트를 구성하는 아이템의 ViewHolder 클래스.
 *
 * @author BH-Ku
 * @since 2023-11-08
 */
public class InterestJmViewHolder extends RecyclerView.ViewHolder {
    private ItemInterestJmBinding binding;

    public InterestJmViewHolder(@NonNull ItemInterestJmBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(JmInfoModel item) {
        binding.itemInterestJmTextView.setText(item.getJmName());
    }

    public void setOnRemoveButtonClickListener(OnItemClickListener onItemClickListener) {
        binding.itemInterestJmRemoveButton.setOnClickListener(v ->
                onItemClickListener.onItemClick(getAdapterPosition()));
    }
}
