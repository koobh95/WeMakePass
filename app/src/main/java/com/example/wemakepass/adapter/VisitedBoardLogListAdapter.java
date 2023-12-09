package com.example.wemakepass.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.wemakepass.adapter.viewholder.VisitedBoardLogViewHolder;
import com.example.wemakepass.data.model.dto.BoardDTO;
import com.example.wemakepass.databinding.ItemVisitedBoardLogBinding;
import com.example.wemakepass.listener.OnVisitedBoardItemClickListener;

/**
 * - MainActivity > CommunityFragment에서 방문한 게시판 목록을 출력하는 RecyclerView의 Adpater 클래스.
 * - 방문한 게시판은 Page 단위로 나뉘어져 출력되는데 이 클래스는 페이지 내에 아이템 목록을 구성하는
 *  RecyclerView에 대한  Adpater 클래스다.
 *
 * @author BH-Ku
 * @since 2023-12-02
 */
public class VisitedBoardLogListAdapter extends ListAdapter<BoardDTO, VisitedBoardLogViewHolder> {
    private OnVisitedBoardItemClickListener onVisitedBoardItemClickListener;

    private final String TAG = "TAG_VisitedBoardLogListAdapter";

    private final static DiffUtil.ItemCallback<BoardDTO> diffCallback = new DiffUtil.ItemCallback<BoardDTO>() {
        @Override
        public boolean areItemsTheSame(@NonNull BoardDTO oldItem, @NonNull BoardDTO newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull BoardDTO oldItem, @NonNull BoardDTO newItem) {
            return oldItem.getBoardNo() == newItem.getBoardNo();
        }
    };

    public VisitedBoardLogListAdapter() {
        super(diffCallback);
    }

    @NonNull
    @Override
    public VisitedBoardLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        assert onVisitedBoardItemClickListener != null;
        return new VisitedBoardLogViewHolder(ItemVisitedBoardLogBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    /**
     *  GridLayout을 짝수로 맞추기 위해서 실제 아이템의 개수가 홀수인 경우 리스트의 마지막을 null으로 채우고
     * 있다. 따라서 null인 경우 binding할 값이 없으므로 여기서는 아무것도 하지 않는다.
     */
    @Override
    public void onBindViewHolder(@NonNull VisitedBoardLogViewHolder holder, int position) {
        if(getItem(position) == null)
            return;
        holder.bind(getItem(position), onVisitedBoardItemClickListener);
    }

    public void setOnBoardItemClickListener(OnVisitedBoardItemClickListener onVisitedBoardItemClickListener) {
        this.onVisitedBoardItemClickListener = onVisitedBoardItemClickListener;
    }
}
