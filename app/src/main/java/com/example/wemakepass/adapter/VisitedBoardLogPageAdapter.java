package com.example.wemakepass.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.adapter.viewholder.VisitedBoardPageViewHolder;
import com.example.wemakepass.data.model.dto.BoardDTO;
import com.example.wemakepass.databinding.ItemVisitedBoardLogPageBinding;
import com.example.wemakepass.listener.OnVisitedBoardItemClickListener;

import java.util.List;

/**
 * - MainActivity > CommunityFragment에서 방문한 게시판 목록을 출력하는 RecyclerView의 Adpater 클래스.
 * - 방문한 게시판은 Page 단위로 나뉘어져 출력되는데 이 클래스는 하나의 페이지를 구성하는 RecyclerView에 대한
 *  Adpater 클래스다.
 *
 * @author BH-Ku
 * @since 2023-12-02
 */
public class VisitedBoardLogPageAdapter extends RecyclerView.Adapter<VisitedBoardPageViewHolder> {
    private List<List<BoardDTO>> pagelist;
    private OnVisitedBoardItemClickListener onVisitedBoardItemClickListener;

    private final String TAG = "TAG_VisitedBoardLogPageAdapter";

    @NonNull
    @Override
    public VisitedBoardPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        assert onVisitedBoardItemClickListener != null;
        return new VisitedBoardPageViewHolder(ItemVisitedBoardLogPageBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VisitedBoardPageViewHolder holder, int position) {
        holder.bind(pagelist.get(position), onVisitedBoardItemClickListener);
    }

    @Override
    public int getItemCount() {
        return pagelist == null ? 0 : pagelist.size();
    }

    public void setOnBoardItemClickListener(OnVisitedBoardItemClickListener onVisitedBoardItemClickListener) {
        this.onVisitedBoardItemClickListener = onVisitedBoardItemClickListener;
    }

    public void setPageList(List<List<BoardDTO>> pagelist) {
        this.pagelist = pagelist;
    }
}
