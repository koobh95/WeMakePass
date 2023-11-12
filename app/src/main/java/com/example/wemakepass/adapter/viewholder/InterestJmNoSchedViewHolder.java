package com.example.wemakepass.adapter.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.data.model.data.InterestJmSchedModel;
import com.example.wemakepass.databinding.ItemInterestJmNoSchedBinding;

/**
 *  HomeFragment에서 관심 종목 일정 리스트를 구성하는 아이템의 ViewHolder 클래스로 총 세 가지 View 타입이
 * 있으며 이 클래스는 일정이 없는 경우에 사용한다.
 *
 * @author BH-Ku
 * @since 2023-11-11
 */
public class InterestJmNoSchedViewHolder extends RecyclerView.ViewHolder {
    private ItemInterestJmNoSchedBinding binding;

    public InterestJmNoSchedViewHolder(@NonNull ItemInterestJmNoSchedBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(InterestJmSchedModel interestJmSchedModel) {
        binding.itemInterestJmNoSchedTitleTextView
                .setText(interestJmSchedModel.getInterestJmModel().getJmName());
        binding.itemInterestJmNoSchedFailReasonTextView
                .setText(interestJmSchedModel.getSchedLoadStateCode().getMessage());
    }
}
