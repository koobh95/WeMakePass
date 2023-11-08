package com.example.wemakepass.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.wemakepass.adapter.viewholder.JmSearchViewHolder;
import com.example.wemakepass.data.model.dto.JmInfoDTO;
import com.example.wemakepass.databinding.ItemSearchAddBinding;
import com.example.wemakepass.listener.OnItemClickListener;

/**
 * InterestJmSearchActivity에서 관심 종목 목록을 출력하는 RecyclerView의 Adapter 클래스.
 *
 * @author BH-Ku
 * @since 2023-11-08
 */
public class JmSearchListAdapter extends ListAdapter<JmInfoDTO, JmSearchViewHolder> {
    private OnItemClickListener onItemClickListener;

    private final String TAG = "TAG_InterestJmListSearchAdapter";

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
    public JmSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        assert onItemClickListener != null;
        return new JmSearchViewHolder(ItemSearchAddBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull JmSearchViewHolder holder, int position) {
        holder.bind(getItem(position));
        holder.setOnItemClickListener(onItemClickListener);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
