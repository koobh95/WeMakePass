package com.example.wemakepass.view.exam.select;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
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

    public static final String ARG_SELECTED_JM_CODE = "selectedJmCode";
    public static final String RESULT_REQUEST_CODE_JM_SEARCH_FRAGMENT = "jmSearchFragment";
    private final String TAG = "TAG_ExamSelectFragment";

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
        initFragmentResultListener();
        initEventListener();
        initToolbar();
    }

    /**
     *  JmSearchFragment에서 종목을 선택하면 Fragment를 종료할 때 선택한 종목 정보를 이전 Fragment에
     * 전달하는데 이 데이터를 수신하기 위한 FragmentResultListener를 설정한다.
     */
    private void initFragmentResultListener() {
        getParentFragmentManager()
                .setFragmentResultListener(RESULT_REQUEST_CODE_JM_SEARCH_FRAGMENT, requireActivity(),
                        (requestKey, result) -> {
                            String jmCode = result.getString(ARG_SELECTED_JM_CODE);
                            Log.d(TAG, "JmCode=" + jmCode);
                        });
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
