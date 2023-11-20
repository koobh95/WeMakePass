package com.example.wemakepass.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.wemakepass.adapter.viewholder.SearchRemoveTypeViewHolder;
import com.example.wemakepass.databinding.ItemSearchRemoveTypeBinding;
import com.example.wemakepass.listener.OnItemClickListener;

/**
 * - 검색 기록을 출력하는 RecyclerView가 공통으로 사용하는 Adapter 클래스.
 * - 아이템을 선택했을 때 처리할 이벤트와 검색 기록을 삭제하는 x 버튼에 대한 이벤트를 모두 구현해야만 한다.
 *
 * @author BH-Ku
 * @since 2023-11-17
 */
public class SearchLogListAdapter extends ListAdapter<String, SearchRemoveTypeViewHolder> {
    private OnItemClickListener onItemClickListener, onRemoveButtonClickListener;

    private static final DiffUtil.ItemCallback<String> diffCallback = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }
    };

    public SearchLogListAdapter() {
        super(diffCallback);
    }

    @NonNull
    @Override
    public SearchRemoveTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        assert onItemClickListener != null;
        assert onRemoveButtonClickListener != null;
        return new SearchRemoveTypeViewHolder(ItemSearchRemoveTypeBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchRemoveTypeViewHolder holder, int position) {
        holder.setContent(getItem(position));
        holder.setOnItemClickListener(onItemClickListener);
        holder.setOnRemoveButtonClickListener(onRemoveButtonClickListener);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnRemoveButtonClickListener(OnItemClickListener onRemoveButtonClickListener) {
        this.onRemoveButtonClickListener = onRemoveButtonClickListener;
    }
}
