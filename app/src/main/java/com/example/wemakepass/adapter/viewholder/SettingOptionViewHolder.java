package com.example.wemakepass.adapter.viewholder;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.data.model.data.SettingOptionModel;
import com.example.wemakepass.databinding.ItemSettingOptionBinding;

/**
 *  MyInfoActivity에 표시될 설정 화면을 구성하는 NestedRecyclerView의 Child View에 해당하는 ViewHolder
 * 클래스다.
 *
 * 관련 ListAdapter : SettingOptionListAdapter
 * 관련 xml : item_setting_option.xml
 *
 * @author BH-Ku
 * @since 2023-11-02
 */
public class SettingOptionViewHolder extends RecyclerView.ViewHolder {
    private ItemSettingOptionBinding binding;

    private final String TAG = "TAG_SettingOptionViewHolder";

    public SettingOptionViewHolder(@NonNull ItemSettingOptionBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    /**
     * summary, 클릭 이벤트의 경우 선택사항이기 때문에 값의 유무에 따라 화면에 표시할지 말지를 결정한다.
     *
     * @param item
     */
    public void bind(SettingOptionModel item){
        if(!TextUtils.isEmpty(item.getSummary())) {
            binding.itemSettingOptionSummaryTextView.setVisibility(View.VISIBLE);
            binding.itemSettingOptionSummaryTextView.setText(item.getSummary());
        }
        binding.itemSettingOptionContentTextView.setText(item.getContent());
        if(item.getOnClickListener() != null) {
            binding.itemSettingOptionArrowImageView.setVisibility(View.VISIBLE);
            binding.getRoot().setOnClickListener(v -> item.getOnClickListener().onClick(v));
        }
    }
}
