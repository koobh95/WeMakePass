package com.example.wemakepass.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.wemakepass.adapter.viewholder.InterestJmViewHolder;
import com.example.wemakepass.data.model.data.InterestJmModel;
import com.example.wemakepass.databinding.ItemInterestJmBinding;
import com.example.wemakepass.listener.OnItemClickListener;

/**
 * InterestJmSearchActivity에서 검색 결과를 출력하는 RecyclerView의 Adapter 클래스.
 *
 * @author BH-Ku
 * @since 2023-11-08
 */
public class InterestJmListAdapter extends ListAdapter<InterestJmModel, InterestJmViewHolder> {
    private OnItemClickListener onRemoveClickListener;

    private final String TAG = "TAG_InterestJmListAdapter";

    private static final DiffUtil.ItemCallback<InterestJmModel> diffCallback = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull InterestJmModel oldItem, @NonNull InterestJmModel newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull InterestJmModel oldItem, @NonNull InterestJmModel newItem) {
            return oldItem.getJmCode().equals(newItem.getJmCode());
        }
    };

    public InterestJmListAdapter() {
        super(diffCallback);
    }

    @NonNull
    @Override
    public InterestJmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        assert onRemoveClickListener != null;
        return new InterestJmViewHolder(ItemInterestJmBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InterestJmViewHolder holder, int position) {
        holder.bind(getItem(position));
        holder.setOnRemoveButtonClickListener(onRemoveClickListener);
    }

    public void setOnRemoveClickListener(OnItemClickListener onRemoveClickListener) {
        this.onRemoveClickListener = onRemoveClickListener;
    }
}
