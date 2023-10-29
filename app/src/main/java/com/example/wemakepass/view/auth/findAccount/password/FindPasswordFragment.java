package com.example.wemakepass.view.auth.findAccount.password;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wemakepass.R;
import com.example.wemakepass.databinding.FragmentFindPasswordBinding;
import com.example.wemakepass.util.DialogUtil;
import com.example.wemakepass.view.auth.findAccount.FindAccountActivity;
import com.example.wemakepass.view.auth.passwordReset.PasswordResetActivity;

public class FindPasswordFragment extends Fragment {
    private FragmentFindPasswordBinding binding;
    private FindPasswordViewModel viewModel;

    public static final String ARG_USER_ID = "userId";
    private final String TAG = "TAG_FindPasswordFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_password, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(FindPasswordViewModel.class);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupOnBackPressedListener();
        setupObserver();
    }

    private void setupOnBackPressedListener(){
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(isRunningTimer()){
                    showShutdownConfirmDialog();
                    return;
                }

                requireActivity().finish();
            }
        };

        requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), onBackPressedCallback);
    }

    /**
     * LiveData에 대한 옵저빙을 설정한다.
     */
    private void setupObserver() {
        viewModel.getSystemMessageLiveData().observe(this, systemMessage ->
                DialogUtil.showAlertDialog(requireContext(), systemMessage));

        viewModel.getNetworkErrorLiveData().observe(this, errorResponse ->
                DialogUtil.showAlertDialog(requireContext(), errorResponse.getMessage()));

        /**
         * 인증이 시작됨
         *
         * 1. 아이디 EditText, 인증 요청 버튼을 Disable.
         * 2. 인증 번호 확인 버튼, 타이머 TextView를 Enable.
         *
         */
        viewModel.getIsSendMailLiveData().observe(this, aBoolean -> {
            DialogUtil.showAlertDialog(requireContext(), "이메일을 전송했습니다.");
            binding.fragmentFindPasswordIdEditText.setEnabled(false);
            binding.fragmentFindPasswordRequestButton.setEnabled(false);
            binding.fragmentFindPasswordConfirmButton.setEnabled(true);
            binding.fragmentFindPasswordTimerTextView.setVisibility(View.VISIBLE);
            viewModel.startTimer();
        });

        /**
         *  인증 시간이 만료되어 인증이 종료됨.
         * 1. 아이디 EditText, 인증 요청 버튼을 Enable.
         * 2. 인증 번호 확인 버튼을 Disable.
         * 3. 타이머 TextView에 인증 시간이 만료되었음을 알리는 텍스트 세팅.
         */
        viewModel.getIsTimeOver().observe(this, aBoolean -> {
            binding.fragmentFindPasswordIdEditText.setEnabled(true);
            binding.fragmentFindPasswordRequestButton.setEnabled(true);
            binding.fragmentFindPasswordConfirmButton.setEnabled(false);
            viewModel.getTimerLiveData().setValue("인증 제한 시간이 초과되었습니다. 다시 시도해주세요.");
        });

        /**
         * - 인증이 완료된 경우, 현재 Fragment를 가진 Activity를 종료하고 PasswordResetActivity를 실행한다.
         * - 비밀번호를 변경할 때 사용할 식별자인 userId를 전달.
         */
        viewModel.getIsConfirmLiveData().observe(this, aBoolean -> {
            DialogUtil.showAlertDialog(requireContext(),
                    "인증되었습니다. 비밀번호 변경 화면으로 이동합니다.",
                    dialog -> {
                        dialog.dismiss();
                        Intent intent = new Intent(requireContext(), PasswordResetActivity.class);
                        intent.putExtra(ARG_USER_ID, viewModel.getIdLiveData().getValue());
                        requireActivity().startActivity(intent);
                        requireActivity().finish();
                    });
        });
    }

    public boolean isRunningTimer(){
        return viewModel.isRunningTimer();
    }

    public void showShutdownConfirmDialog(){
        DialogUtil.showConfirmDialog(requireContext(),
                "비밀번호 찾기가 진행중입니다. 종료하시겠습니까?",
                dialog -> requireActivity().finish());
    }
}