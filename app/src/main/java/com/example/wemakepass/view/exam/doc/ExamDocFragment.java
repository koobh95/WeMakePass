package com.example.wemakepass.view.exam.doc;

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

import com.example.wemakepass.BuildConfig;
import com.example.wemakepass.R;
import com.example.wemakepass.data.model.dto.ExamInfoDTO;
import com.example.wemakepass.data.model.dto.JmInfoDTO;
import com.example.wemakepass.databinding.FragmentExamDocBinding;
import com.example.wemakepass.view.exam.ExamActivity;

import java.util.List;

public class ExamDocFragment extends Fragment {
    private FragmentExamDocBinding binding;
    private ExamDocViewModel viewModel;

    private JmInfoDTO jmInfoDTO;
    private ExamInfoDTO examInfoDTO;

    private final String TAG = "TAG_ExamDocFragment";

    public static ExamDocFragment newInstance(JmInfoDTO jmInfoDTO, ExamInfoDTO examInfoDTO) {
        ExamDocFragment fragment = new ExamDocFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ExamActivity.ARG_SELECTED_JM_INFO, jmInfoDTO);
        bundle.putSerializable(ExamActivity.ARG_SELECTED_EXAM_INFO, examInfoDTO);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        assert bundle != null;
        jmInfoDTO = (JmInfoDTO)bundle.getSerializable(ExamActivity.ARG_SELECTED_JM_INFO);
        examInfoDTO = (ExamInfoDTO)bundle.getSerializable(ExamActivity.ARG_SELECTED_EXAM_INFO);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_exam_doc, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(ExamDocViewModel.class);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "SelectedJmInfo=" + jmInfoDTO);
        Log.d(TAG, "SelectedExamInfo=" + examInfoDTO);
    }
}