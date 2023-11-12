package com.example.wemakepass.adapter.viewholder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.data.model.data.InterestJmSchedModel;
import com.example.wemakepass.databinding.ItemInterestJmTechSchedBinding;
import com.example.wemakepass.util.ExpandAnimationUtils;

/**
 *  HomeFragment에서 관심 종목 일정 리스트를 구성하는 아이템의 ViewHolder 클래스로 총 세 가지 View 타입이
 * 있으며 이 클래스는 일정이 존재하면서 종목 계열이 기술 자격인 경우에 사용하는 클래스다.
 *
 * @author BH-Ku
 * @since 2023-11-11
 */
public class InterestJmTechSchedViewHolder extends RecyclerView.ViewHolder {
    private ItemInterestJmTechSchedBinding binding;

    public InterestJmTechSchedViewHolder(@NonNull ItemInterestJmTechSchedBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(InterestJmSchedModel item) {
        // main title
        binding.itemInterestJmTechSchedMainTitleTextView.setText(item.getSchedTitle());

        // 확장/축소 이벤트
        binding.itemInterestJmTechSchedExpandButton.setOnClickListener(v -> {
            toggleLayout(item.isExpanded(),
                    binding.itemInterestJmTechSchedExpandButton,
                    binding.itemInterestJmTechSchedContentLayout);
            item.changeExpandState();
        });

        StringBuilder sb = new StringBuilder();
        sb.append("접수\t").append(item.getJmSchedDTO().getDocRegStartDate())
                .append(" ~ ")
                .append(item.getJmSchedDTO().getDocRegEndDate())
                .append("\n응시\t")
                .append(item.getJmSchedDTO().getDocExamStartDate())
                .append(" ~ ")
                .append(item.getJmSchedDTO().getDocExamEndDate())
                .append("\n합격자 발표 \t")
                .append(item.getJmSchedDTO().getDocPassDate());
        binding.itemInterestJmTechSchedDocScheduleTextView.setText(sb.toString());
        sb.setLength(0);

        sb.append("접수\t").append(item.getJmSchedDTO().getPracRegStartDate())
                .append(" ~ ")
                .append(item.getJmSchedDTO().getPracRegEndDate())
                .append("\n응시\t")
                .append(item.getJmSchedDTO().getPracExamStartDate())
                .append(" ~ ")
                .append(item.getJmSchedDTO().getPracExamEndDate())
                .append("\n합격자 발표 \t")
                .append(item.getJmSchedDTO().getPracPassDate());

        binding.itemInterestJmTechSchedPracScheduleTextView.setText(sb.toString());
    }

    private void toggleLayout(boolean isExpanded, View arrowView, ViewGroup expandLayout) {
        ExpandAnimationUtils.toggleArrow(arrowView, isExpanded);
        if (isExpanded)
            ExpandAnimationUtils.collapse(expandLayout);
        else
            ExpandAnimationUtils.expand(expandLayout);
    }
}
