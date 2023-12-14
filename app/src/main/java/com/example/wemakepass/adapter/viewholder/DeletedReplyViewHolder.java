package com.example.wemakepass.adapter.viewholder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.databinding.ItemDeletedReplyBinding;

/**
 *  BoardMainActivity > BoardMainFragment > PostViewerFragment 에서 댓글 목록을 구성할 때 삭제된 댓글에
 * 답글이 달려 있는 경우 해당 답글은 삭제된 댓글의 답글임을 알려주기 위해 사용되는 ViewHolder 클래스다.
 *
 * @author BH-Ku
 * @since 2023-12-12
 */
public class DeletedReplyViewHolder extends RecyclerView.ViewHolder {

    public DeletedReplyViewHolder(@NonNull ItemDeletedReplyBinding binding) {
        super(binding.getRoot());
    }
}
