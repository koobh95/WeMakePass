package com.example.wemakepass.adapter.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.adapter.SettingOptionListAdapter;
import com.example.wemakepass.adapter.divider.DividerWithoutLast;
import com.example.wemakepass.data.model.vo.SettingCategoryVO;
import com.example.wemakepass.databinding.ItemSettingCategoryBinding;

/**
 *  MyInfoActivity에 표시될 설정 화면을 구성하는 NestedRecyclerView의 Parent View에 해당하는 ViewHolder
 * 클래스다.
 *
 * 관련 ListAdapter : SettingCategoryListAdapter
 * 관련 xml : item_setting_category.xml
 *
 * @author BH-Ku
 * @since 2023-11-02
 */
public class SettingCategoryViewHolder extends RecyclerView.ViewHolder {
    private ItemSettingCategoryBinding binding;

    private final String TAG = "TAG_SettingCategoryViewHolder";

    public SettingCategoryViewHolder(@NonNull ItemSettingCategoryBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    /**
     * - 중첩 리스트이기 때문에 여기서 Child RecyclerView에 대한 초기화를 진행한다.
     * - 카테고리 값의 경우 선택사항이기 때문에 값의 유무에 따라 화면에 표시할지 말지를 결정한다.
     *
     * @param item
     */
    public void bind(SettingCategoryVO item){
        final Context context = binding.getRoot().getContext();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false);
        SettingOptionListAdapter adapter = new SettingOptionListAdapter(item.getOptionList());
        binding.itemSettingCategoryRecyclerView.setLayoutManager(layoutManager);
        binding.itemSettingCategoryRecyclerView.setAdapter(adapter);
        binding.itemSettingCategoryRecyclerView.addItemDecoration(new DividerWithoutLast(context));
        if(!TextUtils.isEmpty(item.getCategoryTitle())) {
            binding.itemSettingCategoryTitleTextView.setVisibility(View.VISIBLE);
            binding.itemSettingCategoryTitleTextView.setText(item.getCategoryTitle());
        }
    }
}