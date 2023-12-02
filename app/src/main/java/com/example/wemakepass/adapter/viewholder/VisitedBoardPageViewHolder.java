package com.example.wemakepass.adapter.viewholder;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.adapter.VisitedBoardLogListAdapter;
import com.example.wemakepass.data.model.dto.BoardDTO;
import com.example.wemakepass.databinding.ItemVisitedBoardLogPageBinding;
import com.example.wemakepass.listener.OnVisitedBoardItemClickListener;

import java.util.List;

/**
 *  MainActivity > CommunityFragment에서 최근 방문한 게시판 목록을 페이지 단위로 보여주기 위해 구성된
 * ViewHolder 클래스다.
 *
 * @author BH-Ku
 * @since 2023-12-02
 */
public class VisitedBoardPageViewHolder extends RecyclerView.ViewHolder {
    private ItemVisitedBoardLogPageBinding binding;

    private final String TAG = "TAG_VisitedBoardPageViewHolder";

    public VisitedBoardPageViewHolder(@NonNull ItemVisitedBoardLogPageBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(List<BoardDTO> list, OnVisitedBoardItemClickListener onVisitedBoardItemClickListener) {
        final Context context = binding.getRoot().getContext();
        RecyclerView recyclerView = binding.getRoot();
        GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
        VisitedBoardLogListAdapter adapter = new VisitedBoardLogListAdapter();
        adapter.setOnBoardItemClickListener(onVisitedBoardItemClickListener);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.submitList(list);
    }
}
