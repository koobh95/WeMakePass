package com.example.wemakepass.adapter.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.config.AppConfig;
import com.example.wemakepass.data.model.dto.ReplyDTO;
import com.example.wemakepass.databinding.ItemReplyBinding;
import com.example.wemakepass.listener.OnItemClickListener;

import java.time.format.DateTimeFormatter;

/**
 *  BoardMainActivity > BoardMainFragment > PostViewerFragment 에서 댓글 목록을 출력하는 RecyclerView의
 * Adapter 클래스.
 *
 * @author BH-Ku
 * @since 2023-12-12
 */
public class ReplyViewHolder extends RecyclerView.ViewHolder {
    private ItemReplyBinding binding;

    private final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    public ReplyViewHolder(@NonNull ItemReplyBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(ReplyDTO item,
                     OnItemClickListener onReplyAnswerButtonClickListener,
                     OnItemClickListener onReplyDeleteButtonClickListener) {
        binding.itemReplyWriterTextView.setText(item.getWriterNickname());
        binding.itemReplyContentTextView.setText(item.getContent());
        binding.itemReplyRegDateTextView.setText(item.getRegDate()
                .format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));

        if(!item.isReReply()) // 답글이 아닌 경우 답글을 작성하는 Button에 리스너 설정
            binding.itemReplyAnswerButton.setOnClickListener(v ->
                    onReplyAnswerButtonClickListener.onItemClick(getAdapterPosition()));
        else { // 답글인 경우
            binding.itemReplyAnswerImageView.setVisibility(View.VISIBLE);
            binding.itemReplyAnswerButton.setVisibility(View.GONE);
        }

        // 현재 사용자가 작성한 댓글인 경우 삭제 버튼을 활성화
        if(item.getWriterId().equals(AppConfig.UserPreference.getUserId())){
            binding.itemReplyDeleteButton.setVisibility(View.VISIBLE);
            binding.itemReplyDeleteButton.setOnClickListener(v ->
                    onReplyDeleteButtonClickListener.onItemClick(getAdapterPosition()));
        } else {
            binding.itemReplyDeleteButton.setVisibility(View.GONE);
        }
    }
}
