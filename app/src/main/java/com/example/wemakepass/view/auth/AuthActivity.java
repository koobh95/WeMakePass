package com.example.wemakepass.view.auth;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.example.wemakepass.R;
import com.example.wemakepass.databinding.ActivityAuthBinding;
import com.example.wemakepass.listener.AttachFragmentListener;
import com.example.wemakepass.view.auth.login.LoginFragment;
import com.example.wemakepass.view.accountSetting.passwordReset.PasswordResetFragment;

/**
 * - 인증 관련 작업을 처리하는 Activity로서 Container View 역할을 한다.
 * - 최초 실행 시 LoginFragment가 표시된다.
 * - 표시되는 Fragment
 *  LoginFragment
 *  SignUpFragment
 *  PasswordReset
 * - 전환되는 Activity
 *  AccountFindActivity
 *
 * @author BH-Ku
 * @since 2023-10-24
 */
public class AuthActivity extends AppCompatActivity implements AttachFragmentListener {
    private ActivityAuthBinding binding;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    public static final String ARG_USER_ID = "userId";
    public static final String ARG_PASSWORD_RESET = "passwordReset";
    public static final int CODE_PASSWORD_RESET = 100;
    private final String TAG = "TAG_AuthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initActivityResultLauncher();
        replaceFragment(LoginFragment.newInstance());
    }

    /**
     *  AuthActivity가 실행되고 FindPasswordFragment에서 인증이 완료될 경우 이 Activity에서
     * PasswordResetFragment를 실행해주기 위해 activityResultLauncher를 생성해둔다.
     */
    private void initActivityResultLauncher() {
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == CODE_PASSWORD_RESET &&
                            result.getData().getBooleanExtra(ARG_PASSWORD_RESET, false)){
                        addFragment(PasswordResetFragment.newInstance(
                                result.getData().getStringExtra(ARG_USER_ID)),
                                R.anim.slide_from_bottom,
                                R.anim.slide_to_bottom);
                    }
                }
        );
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

    /**
     *  LoginFragment에서 "계정 찾기" 버튼이 선택될 경우 이 메서드를 호출하여 ActivityResultLauncher에 대한
     * 참조를 얻고 lunch 메서드를 호출하여 FindAccountActivity를 실행한다.
     *
     * @return
     */
    public ActivityResultLauncher<Intent> getActivityResultLauncher() {
        return activityResultLauncher;
    }
}