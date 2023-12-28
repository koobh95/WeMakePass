package com.example.wemakepass.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.wemakepass.adapter.viewholder.SearchTextTypeViewHolder;
import com.example.wemakepass.data.model.dto.JmDTO;
import com.example.wemakepass.databinding.ItemSearchTextTypeBinding;
import com.example.wemakepass.listener.OnItemClickListener;

/**
 *  InterestJmSearchActivity, JmSearchActivity에서 종목에 대한 검색 결과를 출력하는 RecyclerView의
 * Adapter 클래스
 *
 * @author BH-Ku
 * @since 2023-11-17
 */
public class JmSearchListAdapter extends ListAdapter<JmDTO, SearchTextTypeViewHolder> {
    private OnItemClickListener onItemClickListener;

    private static final DiffUtil.ItemCallback<JmDTO> diffCallback = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull JmDTO oldItem, @NonNull JmDTO newItem) {
            return true;
        } // 기존 리스트를 재활용하지 않기 때문에 비교가 의미가 없음.

        @Override
        public boolean areContentsTheSame(@NonNull JmDTO oldItem, @NonNull JmDTO newItem) {
            return oldItem.getJmCode().equals(newItem.getJmCode());
        }
    };

    public JmSearchListAdapter() {
        super(diffCallback);
    }

    @NonNull
    @Override
    public SearchTextTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        assert onItemClickListener != null;
        return new SearchTextTypeViewHolder(ItemSearchTextTypeBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchTextTypeViewHolder holder, int position) {
        holder.setContent(getItem(position).getJmName());
        holder.setOnItemClickListener(onItemClickListener);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
