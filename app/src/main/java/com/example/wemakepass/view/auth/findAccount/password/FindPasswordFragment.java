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
import com.example.wemakepass.util.DialogUtils;
import com.example.wemakepass.util.KeyboardUtils;
import com.example.wemakepass.view.auth.AuthActivity;

/**
 * 비밀번호 변경을 위한 인증 수행을 담당하는 Fragment
 *
 * @author BH-Ku
 * @since 2023-10-27
 */
public class FindPasswordFragment extends Fragment {
    private FragmentFindPasswordBinding binding;
    private FindPasswordViewModel viewModel;

    private final String TAG = "TAG_FindPasswordFragment";

    public static FindPasswordFragment newInstance() {
        return new FindPasswordFragment();
    }

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
        initOnBackPressedListener();
        initObserver();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * 실행 결과를 전달하지 않기 때문에 finish()로 Activity를 종료한다.
     */
    private void initOnBackPressedListener(){
        requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if(isRunningTimer()){
                            showShutdownConfirmDialog();
                            return;
                        }
                        requireActivity().finish();
                    }
                });
    }

    /**
     * LiveData에 대한 옵저빙을 설정한다.
     */
    private void initObserver() {
        viewModel.getSystemMessageLiveData().observe(this, systemMessage ->
                DialogUtils.showAlertDialog(requireContext(), systemMessage));

        viewModel.getNetworkErrorLiveData().observe(this, errorResponse ->
                DialogUtils.showAlertDialog(requireContext(), errorResponse.getMessage()));

        /**
         * 인증이 시작됨
         *
         * 1. 아이디 EditText, 인증 요청 버튼을 Disable.
         * 2. 인증 번호 확인 버튼, 타이머 TextView를 Enable.
         *
         */
        viewModel.getIsSendMailLiveData().observe(this, aBoolean -> {
            binding.fragmentFindPasswordIdEditText.setEnabled(false);
            binding.fragmentFindPasswordRequestButton.setEnabled(false);
            binding.fragmentFindPasswordConfirmButton.setEnabled(true);
            binding.fragmentFindPasswordTimerTextView.setVisibility(View.VISIBLE);
            binding.fragmentFindPasswordCodeEditText.setEnabled(true);
            viewModel.startTimer();
            DialogUtils.showAlertDialog(requireContext(), "이메일을 전송했습니다.",
                    dialog -> {
                        KeyboardUtils.showKeyboard(
                                requireActivity(), binding.fragmentFindPasswordCodeEditText);
                        dialog.dismiss();
                    });
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
            binding.fragmentFindPasswordCodeEditText.setEnabled(false);
            viewModel.getTimerLiveData().setValue("인증 제한 시간이 초과되었습니다. 다시 시도해주세요.");
        });

        /**
         * - 인증이 완료된 경우, 현재 Fragment가 부착된 FindAccountActivity를 종료하면서 이전 Activity인
         *  AuthActivity로 PasswordResetFragment 실행 여부(boolean), 변경할 계정의 아이디를 전달한다.
         */
        viewModel.getIsConfirmLiveData().observe(this, aBoolean -> {
            DialogUtils.showAlertDialog(requireContext(),
                    "인증되었습니다. 비밀번호 변경 화면으로 이동합니다.",
                    dialog -> {
                        dialog.dismiss();
                        Intent intent = new Intent();
                        intent.putExtra(AuthActivity.ARG_PASSWORD_RESET_FRAGMENT, true);
                        intent.putExtra(AuthActivity.ARG_USER_ID, viewModel.getIdLiveData().getValue());
                        requireActivity().setResult(AuthActivity.CODE_PASSWORD_RESET_FRAGMENT, intent);
                        requireActivity().finish();
                    });
        });
    }

    /**
     * 인증이 진행 중인지 확인한다.
     *
     * @return
     */
    public boolean isRunningTimer(){
        return viewModel.isRunningTimer();
    }

    public void showShutdownConfirmDialog(){
        DialogUtils.showConfirmDialog(requireContext(),
                "비밀번호 찾기가 진행중입니다. 종료하시겠습니까?",
                dialog -> requireActivity().finish());
    }
}