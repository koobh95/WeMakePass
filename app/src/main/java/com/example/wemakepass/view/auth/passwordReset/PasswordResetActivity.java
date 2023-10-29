package com.example.wemakepass.view.auth.passwordReset;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.TextUtils;

import com.example.wemakepass.R;
import com.example.wemakepass.databinding.ActivityPasswordResetBinding;
import com.example.wemakepass.util.DialogUtil;
import com.example.wemakepass.view.auth.findAccount.password.FindPasswordFragment;
import com.google.android.material.textfield.TextInputLayout;

import java.util.EventListener;

/**
 * 비밀번호 변경을 담당하는 Activity.
 *
 * @author BH-Ku
 * @since 2023-10-24
 */
public class PasswordResetActivity extends AppCompatActivity {
    private ActivityPasswordResetBinding binding;
    private PasswordResetViewModel viewModel;

    private String userId; // 이전 Activity(혹은 Fragment)에서 받아오며 비밀번호 변경의 식별자가 된다.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_password_reset);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(PasswordResetViewModel.class);
        binding.setViewModel(viewModel);

        userId = getIntent().getStringExtra(FindPasswordFragment.ARG_USER_ID);
        userId = "user1235"; // test;
        assert userId != null;

        setupToolbar();
        setupEventListener();
        setupObserver();
    }

    /**
     * 뒤로 가기 버튼 선택 시 인증이 다시 이루어지지 않으면 이 화면에 들어올 수 없음을 경고한다.
     */
    @Override
    public void onBackPressed() {
        DialogUtil.showConfirmDialog(this, "이 화면을 나가면 인증정보가 초기화됩니다. 나가시겠습니까?",
                dialog -> super.onBackPressed());
    }

    /**
     * Toolbar의 Navigation 버튼(←) 선택 시 인증이 다시 이루어지지 않으면 이 화면에 들어올 수 없음을 경고한다.
     */
    private void setupToolbar() {
        binding.activityPasswordResetToolbar.setNavigationOnClickListener(v -> {
            DialogUtil.showConfirmDialog(this, "이 화면을 나가면 인증정보가 초기화됩니다. 나가시겠습니까?",
                    dialog -> finish());
        });
    }

    /**
     *  "비밀번호 변경" 버튼에 대해 클릭 리스너를 설정한다. xml에 바인딩하지 않은 이유는 패스워드 변경을 위해
     * 필요한 user id가 가지고 있기 때문이다. viewModel로 넘겨줘도 되지만 굳이 그렇게까지 해서 바인딩을
     * 사용해야 하는가 하면 아닌 것 같아서 Activity에 작성했다.
     */
    private void setupEventListener(){
        binding.activityPasswordResetConfirmButton.setOnClickListener(v ->
            viewModel.passwordReset(userId));
    }

    /**
     * LiveData에 대한 옵저빙을 설정한다.
     */
    private void setupObserver() {
        viewModel.getSystemMessageLiveData().observe(this, systemMessage->
                DialogUtil.showAlertDialog(this, systemMessage));

        viewModel.getNetworkErrorLiveData().observe(this, errorResponse ->
                DialogUtil.showAlertDialog(this, errorResponse.getMessage()));

        viewModel.getPasswordErrMsgLiveData().observe(this, errorMessage ->
                setErrorMessage(binding.activityPasswordResetPasswordTextLayout, errorMessage));

        viewModel.getPasswordReErrMsgLiveData().observe(this, errorMessage ->
                setErrorMessage(binding.activityPasswordResetPasswordReTextLayout, errorMessage));

        viewModel.getIsPasswordResetCompleteLiveData().observe(this, isPasswordReset -> {
            DialogUtil.showAlertDialog(this, "비밀번호가 변경되었습니다.",
                    dialog -> {
                        dialog.dismiss();
                        this.finish();
                    });
        });
    }

    /**
     * TextInputLayout에 에러 메시지를 세팅하는 메서드
     *
     * @param textInputLayout
     * @param errorMessage
     */
    private void setErrorMessage(TextInputLayout textInputLayout, String errorMessage) {
        if(TextUtils.isEmpty(errorMessage))
            textInputLayout.setErrorEnabled(false);
        else {
            binding.getRoot().clearFocus();
            textInputLayout.setError(errorMessage);
            textInputLayout.requestFocus();
        }
    }
}