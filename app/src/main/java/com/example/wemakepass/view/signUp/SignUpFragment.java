package com.example.wemakepass.view.signUp;

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
import com.example.wemakepass.databinding.FragmentSignUpBinding;
import com.example.wemakepass.util.DialogUtils;
import com.example.wemakepass.util.MessageUtils;
import com.example.wemakepass.view.auth.AuthActivity;
import com.example.wemakepass.view.auth.cert.AccountCertFragment;
import com.google.android.material.textfield.TextInputLayout;

/**
 *  회원가입을 담당하는 Fragment.
 *
 * @author BH-Ku
 * @since 2023-10-24
 */
public class SignUpFragment extends Fragment {
    private FragmentSignUpBinding binding;
    private SignUpViewModel viewModel;

    private final String TAG = "TAG_SignUpFragment";

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initOnBackPressedListener();
        initObserver();
    }

    /**
     * - Fragment 뒤로가기 처리를 담당하는 메서드.
     * - 뒤로가기를 눌렀을 때 EditText 중 하나라도 값이 존재한다면 Dialog를 출력하여 정말로 종료할 것인지 묻는다.
     */
    private void initOnBackPressedListener(){
        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(viewModel.isEmptyEditText()){
                    ((AuthActivity)requireActivity())
                            .getSupportFragmentManager()
                            .popBackStack();
                    return;
                }

                DialogUtils.showConfirmDialog(requireContext(), "이 화면을 나가면 입력 중인 값은 모두 사라지게 됩니다.",
                        dialog -> {
                            dialog.dismiss();
                            ((AuthActivity)requireActivity())
                                    .getSupportFragmentManager()
                                    .popBackStack();
                });
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), backPressedCallback);
    }

    /**
     * LiveData에 대한 옵저빙을 설정한다.
     */
    private void initObserver(){
        viewModel.getNetworkErrorLiveData().observe(this, errorResponse -> {
            DialogUtils.showAlertDialog(requireContext(), errorResponse.getMessage());
        });

        viewModel.getIdErrMsgLiveData().observe(this, errorMessage ->
                setErrorMessage(binding.fragmentSignUpIdTextLayout, errorMessage));

        viewModel.getNicknameErrMsgLiveData().observe(this, errorMessage ->
                setErrorMessage(binding.fragmentSignUpNicknameTextLayout, errorMessage));

        viewModel.getPasswordErrMsgLiveData().observe(this, errorMessage ->
                setErrorMessage(binding.fragmentSignUpPasswordTextLayout, errorMessage));

        viewModel.getPasswordReErrMsgLiveData().observe(this, errorMessage ->
                setErrorMessage(binding.fragmentSignUpPasswordReTextLayout, errorMessage));

        viewModel.getEmailErrMsgLiveData().observe(this, errorMessage ->
                setErrorMessage(binding.fragmentSignUpEmailTextLayout, errorMessage));

        viewModel.getIsSignUpLiveData().observe(this, isSignUp -> {
            MessageUtils.showToast(requireContext(), "회원이 되신것을 환영합니다.");
            ((AuthActivity)requireActivity())
                    .getSupportFragmentManager()
                    .popBackStack();

            ((AuthActivity)requireActivity())
                    .addFragment(
                            AccountCertFragment.newInstance(viewModel.getIdLiveData().getValue()),
                            R.anim.slide_from_bottom,
                            R.anim.slide_to_bottom);
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
            binding.fragmentSignUpRootLayout.clearFocus();
            textInputLayout.setError(errorMessage);
            textInputLayout.requestFocus();
        }
    }
}