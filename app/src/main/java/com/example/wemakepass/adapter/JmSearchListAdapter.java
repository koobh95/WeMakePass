package com.example.wemakepass.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.wemakepass.adapter.viewholder.SearchTextTypeViewHolder;
import com.example.wemakepass.data.model.dto.JmInfoDTO;
import com.example.wemakepass.databinding.ItemSearchTextTypeBinding;
import com.example.wemakepass.listener.OnItemClickListener;

/**
 *  InterestJmSearchActivity, JmSearchActivity에서 종목에 대한 검색 결과를 출력하는 RecyclerView의
 * Adapter 클래스
 *
 * @author BH-Ku
 * @since 2023-11-17
 */
public class JmSearchListAdapter extends ListAdapter<JmInfoDTO, SearchTextTypeViewHolder> {
    private OnItemClickListener onItemClickListener;

    private static final DiffUtil.ItemCallback<JmInfoDTO> diffCallback = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull JmInfoDTO oldItem, @NonNull JmInfoDTO newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull JmInfoDTO oldItem, @NonNull JmInfoDTO newItem) {
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
