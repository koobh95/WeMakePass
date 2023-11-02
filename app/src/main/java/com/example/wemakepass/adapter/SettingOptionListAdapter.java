package com.example.wemakepass.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.adapter.viewholder.SettingOptionViewHolder;
import com.example.wemakepass.data.model.data.SettingOptionModel;
import com.example.wemakepass.databinding.ItemSettingOptionBinding;

import java.util.List;

/**
 * - MyInfoListActivity > (parent)SettingRecyclerView > (child)SettingRecyclerView의 Adapter 클래스.
 * - 리스트 내의 값이 바뀔 일이 없기에 ListAdapter가 아닌 RecyclerView.Adapter로 구현하였다.
 *
 * @author BH-Ku
 * @since 2023-11-02
 */
public class SettingOptionListAdapter extends RecyclerView.Adapter<SettingOptionViewHolder>{
    private final List<SettingOptionModel> list;

    public SettingOptionListAdapter(List<SettingOptionModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public SettingOptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SettingOptionViewHolder(ItemSettingOptionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SettingOptionViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
}
