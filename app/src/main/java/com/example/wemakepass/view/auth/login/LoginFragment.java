package com.example.wemakepass.view.auth.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wemakepass.R;
import com.example.wemakepass.config.AppConfig;
import com.example.wemakepass.data.enums.ErrorCode;
import com.example.wemakepass.databinding.FragmentLoginBinding;
import com.example.wemakepass.network.util.AES256Util;
import com.example.wemakepass.util.DialogUtil;
import com.example.wemakepass.util.MessageUtil;
import com.example.wemakepass.view.auth.AuthActivity;
import com.example.wemakepass.view.auth.cert.EmailCertFragment;
import com.example.wemakepass.view.auth.findAccount.FindAccountActivity;
import com.example.wemakepass.view.auth.signUp.SignUpFragment;

/**
 * 로그인을 수행하는 Fragment.
 *
 */
public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private LoginViewModel viewModel;

    public final String ARG_IS_LOGIN = "isLogin";
    private final String TAG = "TAG_LoginFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.fragmentLoginStoredIdCheckBox.setChecked(!AppConfig.AuthPreference.getStoredId());
        binding.fragmentLoginKeepLoginCheckBox.setChecked(AppConfig.AuthPreference.isKeepLogin());

        setupOnBackPressedListener();
        setupEventListener();
        setObserver();

        //test
        // ((AuthActivity)requireActivity()).addFragment(new SignUpFragment(), R.anim.slide_from_bottom, R.anim.slide_to_bottom);
    }

    /**
     * - Fragment 뒤로가기 처리를 담당하는 메서드.
     * - 이 Fragment를 기준으로 Backstack top에는 AuthActivity가 있고 바로 아래에는 MainActivity가 있다.
     *  이 화면에서 뒤로가기를 누른다는 것은 로그인을 완료하지 않은 채 어플을 종료한다는 것을 의미하므로
     *  MainActivity로 돌아가서는 안되기 때문에 즉시 어플리케이션을 종료한다.
     */
    private void setupOnBackPressedListener(){
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finishAffinity();
            }
        };

        requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), onBackPressedCallback);
    }

    private void setupViews(){
        binding.fragmentLoginKeepLoginCheckBox.setChecked(AppConfig.AuthPreference.isKeepLogin());

    }

    /**
     * LiveData와 직접적인 연관이 없는 View에 대한 이벤트를 설정하는 메서드다.
     */
    private void setupEventListener(){
        binding.fragmentLoginFindAccountButton.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), FindAccountActivity.class)));

        binding.fragmentLoginSignUpButton.setOnClickListener(v -> {
            ((AuthActivity)requireActivity()).addFragment(new SignUpFragment(), R.anim.slide_from_bottom,
                    R.anim.slide_to_bottom);
        });
    }

    /**
     * LiveData에 대한 옵저빙을 설정한다.
     */
    private void setObserver(){
        // ViewModel이 비지니스 로직 처리 과정에서 발생하는 메시지가 발생할 경우 출력한다.
        viewModel.getSystemMessageLiveData().observe(this, systemMessage -> {
            DialogUtil.showAlertDialog(requireContext(), systemMessage);
        });

        /**
         * ViewModel-Repository 에서 네트워크 작업을 수행하다가 에러가 발생하는 것을 감지한다.
         */
        viewModel.getNetworkErrorLiveData().observe(this, errorResponse -> {
            if(errorResponse.getCode().equals(ErrorCode.UNCERT_USER.name())){ // 인증되지 않은 유저
                DialogUtil.showConfirmDialog(requireContext(),
                        "이메일 인증이 필요합니다. 인증 화면으로 이동합니다.",
                        dialog -> {
                            dialog.dismiss();
                            ((AuthActivity)requireActivity()).addFragment(
                                    EmailCertFragment.newInstance(viewModel.getIdLiveData().getValue()),
                                    R.anim.slide_from_bottom, R.anim.slide_to_bottom);
                        });
                return;
            }
            Log.d(TAG, "★★★★★★★★★★★★★★★★★★★★★★★★ ErrorResponse=" + errorResponse);
            DialogUtil.showAlertDialog(requireContext(), errorResponse.getMessage());
        });

        /**
         *  로그인이 완료될 경우 Jwt를 반환받게 되는데 이를 감시하다가 설정 파일에 토큰을 저장한 뒤 유저에 대한
         * 데이터를 요청한다.
         */
        viewModel.getJwtLiveData().observe(requireActivity(), jwt -> {
            AppConfig.AuthPreference.setRefreshToken(jwt.getRefreshToken());
            AppConfig.AuthPreference.setAccessToken(jwt.getAccessToken());
            viewModel.requestUserInfo();
        });

        /**
         * - 암호화된 상태의 유저 정보를 받는다. id, nickname, email 등이 있으며 어플리케이션 운용 도중
         *  필요한 상황에서 쓰기 위해서 설정 파일에 데이터를 저장한다.
         * - 체크 박스 "아이디 저장", "로그인 유지"의 체크 여부를 설정 파일에 저장한다.
         * - "아이디 저장" 여부는 서버에서 얻어 온 아이디를 저장함에 있어서 아무런 영향을 끼치지 않으며 로그아웃
         *  되었을 때 저장된 아이디를 삭제할 것인지 아닌지를 결정하는데 쓰인다. 만약 체크 상태일 경우 로그아웃 시에도
         *  아이디를 삭제하지 않으며 다시 로그인 화면에 진입할 때 Id EditText에 아이디 값을 세팅한다.
         *  결졍한다.
         * - "로그인 유지"는 어플리케이션이 완전히 종료된 후 다시 접속했을 때 로그인 여부 즉, 토큰과 유저 정보
         *  등의 값들을 유지할 것인지 여부를 결정한다.
         */
        viewModel.getUserInfoLiveData().observe(this, userInfoDTO -> {
            AES256Util aes256Util = AES256Util.getInstance();
            AppConfig.UserPreference.setUserId(aes256Util.decrypt(userInfoDTO.getUserId()));
            AppConfig.UserPreference.setNickname(aes256Util.decrypt(userInfoDTO.getNickname()));
            AppConfig.UserPreference.setEmail(aes256Util.decrypt(userInfoDTO.getEmail()));

            AppConfig.AuthPreference.setStoredId(binding.fragmentLoginStoredIdCheckBox.isChecked());
            AppConfig.AuthPreference.setKeepLogin(binding.fragmentLoginKeepLoginCheckBox.isChecked());

            MessageUtil.showToast(requireContext(), "로그인되었습니다.");
            requireActivity().finish();
        });
    }
}