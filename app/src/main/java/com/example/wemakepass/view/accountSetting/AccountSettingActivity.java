package com.example.wemakepass.view.accountSetting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.wemakepass.databinding.ActivityAccountSettingBinding;
import com.example.wemakepass.listener.AttachFragmentListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 회원 정보 확인, 정보 변경 등을 수행하는 계정 관련 설정 화면에 해당하는 Activity다.
 *
 * @author BH-Ku
 * @since 2023-11-02
 */
public class AccountSettingActivity extends AppCompatActivity implements AttachFragmentListener {
    private ActivityAccountSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(AccountSettingFragment.newInstance());
    }

    @Override
    public void addFragment(Fragment fragment, @Nullable int pushAnim, @Nullable int popAnim) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(pushAnim, popAnim, pushAnim, popAnim)
                .add(binding.activityAccountSettingContainerView.getId(), fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.activityAccountSettingContainerView.getId(), fragment)
                .commit();
    }
}