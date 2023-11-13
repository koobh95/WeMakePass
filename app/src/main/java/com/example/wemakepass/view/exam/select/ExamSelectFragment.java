package com.example.wemakepass.view.exam.select;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wemakepass.R;
import com.example.wemakepass.databinding.FragmentExamSelectBinding;
import com.example.wemakepass.view.exam.ExamActivity;
import com.example.wemakepass.view.exam.ExamViewModel;
import com.example.wemakepass.view.exam.jmSearch.JmSearchFragment;

/**
 * 응시할 시험을 선택할 수 있는 레이아웃을 제공하는 Fragment.
 *
 * @author BH-Ku
 * @since 2032-11-13
 */
public class ExamSelectFragment extends Fragment {
    private FragmentExamSelectBinding binding;
    private ExamViewModel viewModel;

    public static ExamSelectFragment newInstance(){
        return new ExamSelectFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_exam_select, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(requireActivity()).get(ExamViewModel.class);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initEventListener();
        initToolbar();
    }

    /**
     * LiveData와 직접적인 연관이 없는 View에 대한 이벤트를 설정하는 메서드.
     */
    private void initEventListener() {
        binding.fragmentExamSelectJmFindLayout.setOnClickListener(v ->
            ((ExamActivity)requireActivity()).addFragment(JmSearchFragment.newInstance(),
                    R.anim.slide_from_end,
                    R.anim.slide_in_end));
    }

    /**
     * Toolbar를 초기화한다.
     */
    private void initToolbar() {
        binding.fragmentExamSelectToolbar.setNavigationOnClickListener(v ->
                requireActivity().finish());
    }
}
