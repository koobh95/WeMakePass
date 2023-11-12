package com.example.wemakepass.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wemakepass.data.model.vo.ExamOrganVO;
import com.example.wemakepass.databinding.ItemExamOrganBinding;

/**
 * MainActivity > HomeFragment에서 시험 주관 기관 리스트를 구성하는 아이템의 VieHolder 클래스
 *
 * @author BH-Ku
 * @since 2023-11-13
 */
public class ExamOrganViewHolder extends RecyclerView.ViewHolder {
    private ItemExamOrganBinding binding;

    private final String TAG = "TAG_ExamOrganViewHolder";

    public ExamOrganViewHolder(@NonNull ItemExamOrganBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    /**
     * - 각 아이템이 선택되면 선택한 시험 주관 기관의 홈페이지로 이동된다.
     * - URL을 실행할 때 WebView를 사용하지 않고 Intent를 사용해 디바이스에 설치된 기본 어플리케이션을
     *  사용하도록 했다. WebView 자체의 구현 난이도는 낮았지만 UI가 너무 낡아 있었고 고작 이것 하나 때문에
     *  WebView의 레이아웃을 커스텀하는 것은 낭비라고 생각했기 때문이다.
     *
     * @param item binding할 데이터 클래스
     */
    public void bind(ExamOrganVO item) {
        final Context context = binding.getRoot().getContext();
        binding.itemExamOrganNameTextView.setText(item.getName());
        binding.getRoot().setOnClickListener(v -> {
            Uri webUri = Uri.parse(item.getUrl());
            context.startActivity(new Intent(Intent.ACTION_VIEW, webUri));
        });

        Glide.with(context)
                .load(item.getIcon())
                .into(binding.itemExamOrganImageView);
    }
}
