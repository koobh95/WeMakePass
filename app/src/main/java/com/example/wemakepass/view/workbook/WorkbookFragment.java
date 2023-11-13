package com.example.wemakepass.view.workbook;

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
import com.example.wemakepass.databinding.FragmentWorkbookBinding;
import com.example.wemakepass.view.exam.ExamActivity;


/**
 *
 *
 * @author BH-Ku
 * @since 2023-11-13
 */
public class WorkbookFragment extends Fragment {
    private FragmentWorkbookBinding binding;

    public static WorkbookFragment newInstance() {
        return new WorkbookFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkbookBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initEventListener();
    }

    private void initEventListener(){
        binding.fragmentWorkbookExamSelectFindButton.setOnClickListener(v->
                startActivity(new Intent(requireContext(), ExamActivity.class)));
    }
}