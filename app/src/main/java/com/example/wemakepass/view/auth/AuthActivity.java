package com.example.wemakepass.view.auth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;

import com.example.wemakepass.R;
import com.example.wemakepass.databinding.ActivityAuthBinding;
import com.example.wemakepass.listener.AttachFragmentListener;
import com.example.wemakepass.view.auth.login.LoginFragment;

/**
 * - 인증 관련 작업을 처리하는 Activity로서 Container View 역할을 한다.
 * - 최초 실행 시 LoginFragment가 표시된다.
 * - 표시되는 Fragment
 *  LoginFragment
 *  SignUpFragment
 * - 전환되는 Activity
 *  AccountFindActivity
 *
 * @author BH-Ku
 * @since 2023-10-24
 */
public class AuthActivity extends AppCompatActivity implements AttachFragmentListener {
    private ActivityAuthBinding binding;

    private final String TAG = "TAG_AuthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new LoginFragment());
    }

    @Override
    public void addFragment(Fragment fragment, @Nullable int pushAnim, @Nullable int popAnim) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(pushAnim, popAnim, pushAnim, popAnim)
                .add(binding.activityAuthContainerView.getId(), fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.activityAuthContainerView.getId(), fragment)
                .commit();
    }
}