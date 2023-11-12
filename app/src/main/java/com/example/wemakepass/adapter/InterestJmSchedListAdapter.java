package com.example.wemakepass.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.adapter.viewholder.InterestJmNoSchedViewHolder;
import com.example.wemakepass.adapter.viewholder.InterestJmProfSchedViewHolder;
import com.example.wemakepass.adapter.viewholder.InterestJmTechSchedViewHolder;
import com.example.wemakepass.data.enums.QualificationCode;
import com.example.wemakepass.data.enums.SchedLoadStateCode;
import com.example.wemakepass.data.model.data.InterestJmSchedModel;
import com.example.wemakepass.databinding.ItemInterestJmNoSchedBinding;
import com.example.wemakepass.databinding.ItemInterestJmProfSchedBinding;
import com.example.wemakepass.databinding.ItemInterestJmTechSchedBinding;

/**
 * - HomeFragment에서 관심 종목 일정 리스트를 출력하는 RecyclerView의 Adapter 클래스.
 * - 종목의 계열에 따라 총 세 가지 ViewHolder가 사용된다. 전문 자격, 기술 자격, 그리고 모종의 이유로 시험 일정이
 *  없거나 불러오지 못한 경우로 나뉜다. ViewHolder 클래스가 상황에 따라 다르게 사용되므로 ListAdapter의
 *  두 번째 제네릭 타입은 Custom ViewHolder 클래스들의 부모 클래스인 RecyclerView.ViewHolder로 지정되어 있다.
 *
 * @author BH-Ku
 * @since 2023-11-11
 */
public class InterestJmSchedListAdapter
        extends ListAdapter<InterestJmSchedModel, RecyclerView.ViewHolder> {
    private final String TAG = "TAG_InterestJmSchedListAdapter";

    private final int NO_SCHED = 0;
    private final int SCHED_TECHNICAL = 1;
    private final int SCHED_PROFESSIONAL = 2;

    private static final DiffUtil.ItemCallback<InterestJmSchedModel> diffCallback =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull InterestJmSchedModel oldItem,
                                               @NonNull InterestJmSchedModel newItem) {
                    return oldItem == newItem;
                }

                @Override
                public boolean areContentsTheSame(@NonNull InterestJmSchedModel oldItem,
                                                  @NonNull InterestJmSchedModel newItem) {
                    return oldItem.getInterestJmModel().getJmCode()
                            .equals(newItem.getInterestJmModel().getJmCode());
                }
            };

    public InterestJmSchedListAdapter() {
        super(diffCallback);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SCHED_TECHNICAL)
            return new InterestJmTechSchedViewHolder(ItemInterestJmTechSchedBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
        else if (viewType == SCHED_PROFESSIONAL)
            return new InterestJmProfSchedViewHolder(ItemInterestJmProfSchedBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
        return new InterestJmNoSchedViewHolder(ItemInterestJmNoSchedBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final int code = getItemViewType(position);

        if(code == SCHED_TECHNICAL)
            ((InterestJmTechSchedViewHolder)holder).bind(getItem(position));
        else if(code == SCHED_PROFESSIONAL)
            ((InterestJmProfSchedViewHolder)holder).bind(getItem(position));
        else
            ((InterestJmNoSchedViewHolder)holder).bind(getItem(position));
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getSchedLoadStateCode() != SchedLoadStateCode.OK)
            return NO_SCHED;
        else if (getItem(position).getJmSchedDTO().getQualCode()
                .equals(QualificationCode.TECHNICAL.getCode()))
            return SCHED_TECHNICAL;
        return SCHED_PROFESSIONAL;
    }
}
