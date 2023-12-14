package com.example.wemakepass.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.wemakepass.adapter.viewholder.PostViewHolder;
import com.example.wemakepass.data.model.dto.PostDTO;
import com.example.wemakepass.databinding.ItemPostBinding;
import com.example.wemakepass.listener.OnItemClickListener;

/**
 * BoardActivity > BoardMainFragment에서 읽어온 게시글 목록을 출력하는 RecyclerView의 Adpater 클래스.
 *
 * @author BH-Ku
 * @since 2023-12-01
 */
public class PostListAdapter extends ListAdapter<PostDTO, PostViewHolder> {
    private OnItemClickListener onItemClickListener;

    private static final DiffUtil.ItemCallback<PostDTO> diffCallback = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull PostDTO oldItem, @NonNull PostDTO newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull PostDTO oldItem, @NonNull PostDTO newItem) {
            return oldItem.equals(newItem);
        }
    };

    public PostListAdapter() {
        super(diffCallback);
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        assert onItemClickListener != null;
        return new PostViewHolder(ItemPostBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.bind(getItem(position), onItemClickListener);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}