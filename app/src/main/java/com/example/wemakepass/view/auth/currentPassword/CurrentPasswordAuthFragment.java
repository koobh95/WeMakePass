package com.example.wemakepass.view.auth.currentPassword;

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
import com.example.wemakepass.config.AppConfig;
import com.example.wemakepass.databinding.FragmentCurrentPasswordAuthBinding;
import com.example.wemakepass.util.DialogUtils;
import com.example.wemakepass.view.accountSetting.AccountSettingActivity;
import com.example.wemakepass.view.accountSetting.passwordReset.PasswordResetFragment;

/**
 * 로그인된 상태에서 비밀번호 변경을 하기 위해 현재 비밀번호 인증을 수행하는 Fragment다.
 *
 * @author BH-Ku
 * @since 2023-11-07
 */
public class CurrentPasswordAuthFragment extends Fragment {
    private FragmentCurrentPasswordAuthBinding binding;
    private CurrentPasswordAuthViewModel viewModel;

    public static CurrentPasswordAuthFragment newInstance() {
        return new CurrentPasswordAuthFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_current_password_auth, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(CurrentPasswordAuthViewModel.class);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar();
        initObserver();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Toolbar를 초기화하는 메서드.
     */
    private void initToolbar() {
        binding.fragmentCurrentPasswordAuthToolbar.setNavigationOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack());
    }

    /**
     * Observer를 초기화하는 메서드.
     */
    private void initObserver() {
        viewModel.getSystemMessageLiveData().observe(this, systemMessage ->
                DialogUtils.showAlertDialog(requireContext(), systemMessage));

        viewModel.getNetworkErrorLiveData().observe(this, errorResponse ->
                DialogUtils.showAlertDialog(requireContext(), errorResponse.getMessage()));

        /**
         * 비밀번호 인증이 완료되는 것을 감시하며 인증될 경우 다음을 수행한다.
         *  1. AlertDialog를 닫는다.
         *  2. 현재 Fragment를 종료한다.
         *  3. PasswordResetFragment를 실행한다.
         */
        viewModel.getIsAuthLiveData().observe(this, aBoolean -> {
            DialogUtils.showAlertDialog(requireContext(),
                    "인증되었습니다. 비밀번호 변경 화면으로 이동합니다.",
                    dialog -> {
                        dialog.dismiss();
                        requireActivity()
                                .getSupportFragmentManager()
                                .popBackStack();
                        ((AccountSettingActivity)requireActivity())
                                .addFragment(PasswordResetFragment.newInstance(AppConfig.UserPreference.getUserId()),
                                        R.anim.slide_from_bottom, R.anim.slide_to_bottom);
                    });
        });
    }
}