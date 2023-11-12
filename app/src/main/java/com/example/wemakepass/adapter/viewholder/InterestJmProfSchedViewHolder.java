package com.example.wemakepass.adapter.viewholder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.data.model.data.InterestJmSchedModel;
import com.example.wemakepass.databinding.ItemInterestJmProfSchedBinding;
import com.example.wemakepass.databinding.ItemInterestJmTechSchedBinding;
import com.example.wemakepass.util.ExpandAnimationUtils;

/**
 *  HomeFragment에서 관심 종목 일정 리스트를 구성하는 아이템의 ViewHolder 클래스로 총 세 가지 View 타입이
 * 있으며 이 클래스는 일정이 존재하면서 종목 계열이 전문 자격인 경우에 사용하는 클래스다.
 *
 * @author BH-Ku
 * @since 2023-11-11
 */
public class InterestJmProfSchedViewHolder extends RecyclerView.ViewHolder {
    private ItemInterestJmProfSchedBinding binding;

    public InterestJmProfSchedViewHolder(@NonNull ItemInterestJmProfSchedBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(InterestJmSchedModel item){
        // main title
        binding.itemInterestJmProfSchedMainTitleTextView.setText(item.getSchedTitle());

        // 확장/축소 이벤트
        binding.itemInterestJmProfSchedExpandButton.setOnClickListener(v -> {
            toggleLayout(item.isExpanded(), binding.itemInterestJmProfSchedExpandButton,
                    binding.itemInterestJmProfSchedContentLayout);
            item.changeExpandState();
        });

        // 실기 시험 구분명(면접, 실기 등)
        binding.itemInterestJmProfSchedDocTitleTextView.setText(
                getDocExamTitle(item.getJmSchedDTO().getDescription()));

        StringBuilder sb = new StringBuilder();
        sb.append("- 접수\t").append(item.getJmSchedDTO().getDocRegStartDate()).append(" ~ ")
                .append(item.getJmSchedDTO().getDocRegEndDate()).append("\n- 응시\t")
                .append(item.getJmSchedDTO().getDocExamStartDate()).append(" ~ ")
                .append(item.getJmSchedDTO().getDocExamEndDate()).append("\n- 합격자 발표 \t")
                .append(item.getJmSchedDTO().getDocPassDate());

        // 시험 일정
        binding.itemInterestJmProfSchedDocScheduleTextView.setText(sb);
    }

    private String getDocExamTitle(String description) {
        int idx = description.lastIndexOf(' ');
        return description.substring(idx, description.length()-1);
    }

    private void toggleLayout(boolean isExpanded, View arrowView, ViewGroup expandLayout) {
        ExpandAnimationUtils.toggleArrow(arrowView, isExpanded);
        if(isExpanded)
            ExpandAnimationUtils.collapse(expandLayout);
        else
            ExpandAnimationUtils.expand(expandLayout);
    }
}
