package com.example.wemakepass.view.accountSetting.passwordReset;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wemakepass.R;
import com.example.wemakepass.databinding.FragmentPasswordResetBinding;
import com.example.wemakepass.util.DialogUtil;
import com.google.android.material.textfield.TextInputLayout;

/**
 * 비밀번호 변경을 수행하는 Fragment
 *
 * @author BH-Ku
 * @since 2023-11-02
 */
public class PasswordResetFragment extends Fragment {
    private FragmentPasswordResetBinding binding;
    private PasswordResetViewModel viewModel;

    private String userId; // 인증할 때 사용된 아이디, 즉 변경할 유저의 식별 정보

    private static final String ARG_USER_ID = "userId";
    private final String TAG = "TAG_PasswordResetFragment";

    public static PasswordResetFragment newInstance(String userId) {
        PasswordResetFragment fragment = new PasswordResetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        assert bundle != null;
        userId = bundle.getString(ARG_USER_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_password_reset, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(PasswordResetViewModel.class);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initOnBackPressedListener();
        initToolbar();
        initEventListener();
        initObserver();
    }

    /**
     * 뒤로 가기 버튼 선택 시 인증이 다시 이루어지지 않으면 이 화면에 들어올 수 없음을 경고한다.
     */
    public void initOnBackPressedListener() {
        requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        DialogUtil.showConfirmDialog(requireContext(),
                                "이 화면을 나가면 인증정보가 초기화됩니다. 나가시겠습니까?",
                                dialog -> {
                                    dialog.dismiss();
                                    requireActivity()
                                            .getSupportFragmentManager()
                                            .popBackStack();});

                    }
                });
    }

    /**
     * Toolbar의 Navigation 버튼(←) 선택 시 인증이 다시 이루어지지 않으면 이 화면에 들어올 수 없음을 경고한다.
     */
    private void initToolbar() {
        binding.fragmentPasswordResetToolbar.setNavigationOnClickListener(v -> {
            DialogUtil.showConfirmDialog(requireContext(),
                    "이 화면을 나가면 인증정보가 초기화됩니다. 나가시겠습니까?",
                    dialog -> {
                        dialog.dismiss();
                        requireActivity()
                                .getSupportFragmentManager()
                                .popBackStack();
                    });
        });
    }

    /**
     *  "비밀번호 변경" 버튼에 대해 클릭 리스너를 설정한다. xml에 바인딩하지 않은 이유는 패스워드 변경을 위해
     * 필요한 user id를 Fragment가 가지고 있기 때문이다. 데이터를 viewModel로 넘겨줘도 되지만 굳이 그렇게까지
     * 해서 바인딩을 활용해야 하는가 하면 아닌 것 같아서 Activity에 작성했다.
     */
    private void initEventListener() {
        binding.fragmentPasswordResetConfirmButton.setOnClickListener(v ->
                viewModel.passwordReset(userId));
    }

    /**
     * LiveData에 대한 옵저빙을 설정한다.
     */
    private void initObserver() {
        viewModel.getSystemMessageLiveData().observe(this, systemMessage ->
                DialogUtil.showAlertDialog(requireContext(), systemMessage));

        viewModel.getNetworkErrorLiveData().observe(this, errorResponse ->
                DialogUtil.showAlertDialog(requireContext(), errorResponse.getMessage()));

        viewModel.getPasswordErrMsgLiveData().observe(this, errorMessage ->
                setErrorMessage(binding.fragmentPasswordResetPasswordTextLayout, errorMessage));

        viewModel.getPasswordReErrMsgLiveData().observe(this, errorMessage ->
                setErrorMessage(binding.fragmentPasswordResetPasswordReTextLayout, errorMessage));

        /**
         * Password 변경에 성공했을 경우, 성공 메시지를 띄우고 Fragment를 종료한다.
         */
        viewModel.getIsPasswordResetCompleteLiveData().observe(this, isPasswordReset -> {
            DialogUtil.showAlertDialog(requireContext(), "비밀번호가 변경되었습니다.",
                    dialog -> {
                        dialog.dismiss();
                        requireActivity()
                                .getSupportFragmentManager()
                                .popBackStack();
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
        if (TextUtils.isEmpty(errorMessage))
            textInputLayout.setErrorEnabled(false);
        else {
            binding.getRoot().clearFocus();
            textInputLayout.setError(errorMessage);
            textInputLayout.requestFocus();
        }
    }
}