package com.example.wemakepass.view.examInfo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wemakepass.R;
import com.example.wemakepass.view.home.HomeFragment;

public class ExamInfoFragment extends Fragment {

    public static ExamInfoFragment newInstance() {
        return new ExamInfoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exam_info, container, false);
    }
}