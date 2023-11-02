package com.example.wemakepass.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.adapter.viewholder.SettingCategoryViewHolder;
import com.example.wemakepass.data.model.vo.SettingCategoryVO;
import com.example.wemakepass.databinding.ItemSettingCategoryBinding;

import java.util.List;

/**
 * - MyInfoListActivity > (parent)SettingRecyclerView 의 Adapter 클래스.
 * - 리스트 내의 값이 바뀔 일이 없기에 ListAdapter가 아닌 RecyclerView.Adapter로 구현하였다.
 *
 * @author BH-Ku
 * @since 2023-11-02
 */
public class SettingCategoryListAdapter extends RecyclerView.Adapter<SettingCategoryViewHolder> {
    private final List<SettingCategoryVO> list;

    public SettingCategoryListAdapter(List<SettingCategoryVO> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public SettingCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SettingCategoryViewHolder(ItemSettingCategoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SettingCategoryViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
}
