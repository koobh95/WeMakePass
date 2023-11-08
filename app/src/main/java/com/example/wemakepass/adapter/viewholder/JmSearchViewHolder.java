package com.example.wemakepass.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.data.model.dto.JmInfoDTO;
import com.example.wemakepass.databinding.ItemSearchAddBinding;
import com.example.wemakepass.listener.OnItemClickListener;

/**
 * InterestJmSearchActivity에서 관심 종목 리스트를 구성하는 아이템의 ViewHolder 클래스.
 *
 * @author BH-Ku
 * @since 2023-11-08
 */
public class JmSearchViewHolder extends RecyclerView.ViewHolder {
    private ItemSearchAddBinding binding;

    public JmSearchViewHolder(ItemSearchAddBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(JmInfoDTO item) {
        binding.itemSearchAddTextView.setText(item.getJmName());
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        binding.itemSearchAddButton.setOnClickListener(v ->
                onItemClickListener.onItemClick(getAdapterPosition()));
    }
}
