package com.example.wemakepass.adapter.viewholder;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.data.model.dto.PostDTO;
import com.example.wemakepass.databinding.ItemPostBinding;
import com.example.wemakepass.listener.OnItemClickListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 게시판(BoardMainFragment)을 실행했을 때 불러오는 게시글 리스트를 구성하는 아이템의 ViewHolder 클래스.
 *
 * @author BH-Ku
 * @since 2023-12-01
 */
public class PostViewHolder extends RecyclerView.ViewHolder {
    private ItemPostBinding binding;

    private final String POST_TIME_FORMAT_TODAY = "hh:mm";
    private final String POST_TIME_FORMAT_THIS_YEAR = "MM-dd";
    private final String POST_TIME_FORMAT_OLD_DATE = "yyyy-MM-dd";
    private final String TAG = "TAG_PostViewHolder";

    public PostViewHolder(@NonNull ItemPostBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(PostDTO item, OnItemClickListener onItemClickListener) {
        binding.itemPostTitleTextView.setText(item.getTitle());
        binding.itemPostCommentCntTextView.setText("[" + item.getCommentCount() + "]");
        binding.itemPostWriterTextView.setText(item.getNickname());
        binding.itemPostHitTextView.setText(item.getHit() + "");
        binding.itemPostWriteDateTextView.setText(localDateToString(item.getRegDate()));
        if(!item.getCategory().equals("일반"))
            binding.itemPostCategoryTextView.setText(item.getCategory());
        binding.getRoot().setOnClickListener(position ->
                onItemClickListener.onItemClick(getAdapterPosition()));
    }

    /**
     * - 포스팅 작성 날짜를 조건에 맞게 문자열로 변환하여 반환한다.
     * - 각 조건은 커먼 케이스가 아닌 비교 조건 수가 적은 케이스를 우선적으로 비교하였다.
     *
     * 1. 포스트 작성 날짜가 당해가 아닌 경우 "yyyy-MM-dd"
     * 2. 포스트 작성 날짜가 당일이 아니고 당해인 경우 "MM-dd"
     * 포스트 작성 날짜가 당일인 경우 "hh:mm" 로 표기
     *
     * @param regDate
     */
    public String localDateToString(LocalDateTime regDate) {
        LocalDate now = LocalDate.now();
        if(now.getYear() != regDate.getYear())
            return regDate.format(DateTimeFormatter.ofPattern(POST_TIME_FORMAT_OLD_DATE));
        else if(now.getMonth() != regDate.getMonth() || now.getDayOfMonth() != regDate.getDayOfMonth())
            return regDate.format(DateTimeFormatter.ofPattern(POST_TIME_FORMAT_THIS_YEAR));
        return regDate.format(DateTimeFormatter.ofPattern(POST_TIME_FORMAT_TODAY));
    }
}
