package com.example.wemakepass.adapter.viewholder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.data.model.dto.BoardDTO;
import com.example.wemakepass.databinding.ItemVisitedBoardLogBinding;
import com.example.wemakepass.listener.OnVisitedBoardItemClickListener;

/**
 * MainActivity > CommunityFragment에서 최근 방문한 게시판 목록을 구성하는 아이템의 ViewHolder 클래스.
 *
 * @author BH-Ku
 * @since 2023-12-02
 */
public class VisitedBoardLogViewHolder extends RecyclerView.ViewHolder {
    private ItemVisitedBoardLogBinding binding;

    private final String TAG = "TAG_VisitedBoardLogViewHolder";

    public VisitedBoardLogViewHolder(@NonNull ItemVisitedBoardLogBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(BoardDTO boardDTO, OnVisitedBoardItemClickListener onVisitedBoardItemClickListener) {
        binding.getRoot().setText(boardDTO.getBoardName());
        binding.getRoot().setOnClickListener(v ->
                onVisitedBoardItemClickListener.onClick(boardDTO));
        binding.getRoot().setOnLongClickListener(v -> {
            onVisitedBoardItemClickListener.onLongClick(boardDTO);
            return true;
        });
    }
}
