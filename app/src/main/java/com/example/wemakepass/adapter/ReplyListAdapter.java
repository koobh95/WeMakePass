package com.example.wemakepass.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.adapter.viewholder.DeletedReplyViewHolder;
import com.example.wemakepass.adapter.viewholder.ReplyViewHolder;
import com.example.wemakepass.data.model.dto.ReplyDTO;
import com.example.wemakepass.databinding.ItemDeletedReplyBinding;
import com.example.wemakepass.databinding.ItemReplyBinding;
import com.example.wemakepass.listener.OnItemClickListener;

/**
 *  BoardActivity > BoardMainFragment > PostViewerFragment 에서 댓글 목록을 출력하는 RecyclerView의
 * Adpater 클래스.
 *
 * @author BH-Ku
 * @since 2023-12-12
 */
public class ReplyListAdapter extends ListAdapter<ReplyDTO, RecyclerView.ViewHolder> {
    private OnItemClickListener onReplyAnswerButtonClickListener; // 답글 작성 이벤트 리너스 설정
    private OnItemClickListener onReplyDeleteButtonClickListener; // 댓글 삭제 이벤트 리스너 설정

    private final int NORMAL_REPLY = 0; // 댓글, 답글
    private final int DELETED_REPLY = 1; // 삭제된 댓글

    private static final String TAG = "TAG_ReplyListAdapter";

    private static final DiffUtil.ItemCallback<ReplyDTO> diffCallback = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull ReplyDTO oldItem, @NonNull ReplyDTO newItem) {
            return true;
        } // 매번 새로운 리스트에 데이터를 받기에 아이템의 주소값은 비교하지 않음.

        @Override
        public boolean areContentsTheSame(@NonNull ReplyDTO oldItem, @NonNull ReplyDTO newItem) {
            return oldItem.equals(newItem);
        }
    };

    public ReplyListAdapter() {
        super(diffCallback);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        assert onReplyAnswerButtonClickListener != null;
        assert onReplyDeleteButtonClickListener != null;

        if(viewType == NORMAL_REPLY)
            return new ReplyViewHolder(ItemReplyBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
        return new DeletedReplyViewHolder(ItemDeletedReplyBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == NORMAL_REPLY)
            ((ReplyViewHolder)holder).bind(getItem(position),
                    onReplyAnswerButtonClickListener,
                    onReplyDeleteButtonClickListener);
    }

    @Override
    public int getItemViewType(int position) {
        if(getItem(position).isDeleted())
            return DELETED_REPLY;
        return NORMAL_REPLY;
    }

    public void setOnReplyAnswerButtonClickListener(OnItemClickListener onReplyAnswerButtonClickListener) {
        this.onReplyAnswerButtonClickListener = onReplyAnswerButtonClickListener;
    }

    public void setOnReplyDeleteButtonClickListener(OnItemClickListener onReplyDeleteButtonClickListener) {
        this.onReplyDeleteButtonClickListener = onReplyDeleteButtonClickListener;
    }
}
