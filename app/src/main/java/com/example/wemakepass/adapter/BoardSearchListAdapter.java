package com.example.wemakepass.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.wemakepass.adapter.viewholder.SearchTextTypeViewHolder;
import com.example.wemakepass.data.model.dto.BoardDTO;
import com.example.wemakepass.databinding.ItemSearchTextTypeBinding;
import com.example.wemakepass.listener.OnItemClickListener;

/**
 *  BoardSearchActivity에서 게시판 검색 결과를 출력하는 RecyclerView의 Adapter 클래스.
 *
 * @author BH-Ku
 * @since 2023-11-29
 */
public class BoardSearchListAdapter extends ListAdapter<BoardDTO, SearchTextTypeViewHolder> {
    private OnItemClickListener onItemClickListener;

    private final String TAG = "TAG_BoardSearchListAdapter";

    private static final DiffUtil.ItemCallback<BoardDTO> diffCallback = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull BoardDTO oldItem, @NonNull BoardDTO newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull BoardDTO oldItem, @NonNull BoardDTO newItem) {
            return oldItem.getBoardId() == newItem.getBoardId();
        }
    };

    public BoardSearchListAdapter() {
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
        holder.setContent(getItem(position).getBoardName());
        holder.setOnItemClickListener(onItemClickListener);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
