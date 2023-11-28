package com.example.wemakepass.view.exam;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.wemakepass.databinding.ActivityExamBinding;
import com.example.wemakepass.listener.AttachFragmentListener;
import com.example.wemakepass.view.exam.select.ExamSelectFragment;

/**
 * 응시할 시험 선택, 종목 검색, 시험 응시 등 시험 응시와 관련 전반적인 화면(Fragment)을 제공하는 Activity.
 *
 * @author BH-Ku
 * @since 2303-11-13
 */
public class ExamActivity extends AppCompatActivity implements AttachFragmentListener {
    private ActivityExamBinding binding;

    public static final String ARG_SELECTED_JM_INFO = "selectedJmInfo";
    public static final String ARG_SELECTED_EXAM_INFO = "selectedExamInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExamBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(ExamSelectFragment.newInstance());
    }

    @Override
    public void addFragment(Fragment fragment, @Nullable int pushAnim, @Nullable int popAnim) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(pushAnim, popAnim, pushAnim, popAnim)
                .add(binding.activityExamContainerView.getId(), fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.activityExamContainerView.getId(), fragment)
                .commit();
    }
}