package com.example.wemakepass.view.accountSetting.nicknameChange;

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
import com.example.wemakepass.databinding.FragmentNicknameChangeBinding;
import com.example.wemakepass.util.DialogUtils;
import com.example.wemakepass.util.MessageUtils;
import com.example.wemakepass.view.accountSetting.AccountSettingFragment;

/**
 * Nickname 변경을 담당하는 Fragment
 *
 * @author BH-Ku
 * @since 2023-11-06
 */
public class NicknameChangeFragment extends Fragment {
    private FragmentNicknameChangeBinding binding;
    private NicknameChangeViewModel viewModel;

    public static NicknameChangeFragment newInstance() {
        return new NicknameChangeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_nickname_change, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(NicknameChangeViewModel.class);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initEventListener();
        initToolbar();
        initObserver();
    }

    /**
     * - EditText를 제어하기 위해 닉네임 변경 버튼의 이벤트를 Fragment에서 정의했다. 요청을 보낸 후 결과가
     *  true일 경우 요청을 성공적으로 보냈다는 것이므로 그 동안 EditText를 변경하기 못하도록 disable 한다.
     * - EditText에 대한 처리를 하는 이유는 성공적으로 닉네임이 변경되었을 경우 EditText에 있는 값을 그대로
     * SharedPreferences에 저장하기 때문에 결과를 받는 동안 변경이 있어선 안되기 때문이다.
     */
    private void initEventListener() {
        binding.fragmentNicknameChangeButton.setOnClickListener(v -> {
            if(viewModel.changeNickname())
                binding.fragmentNicknameChangeNicknameEditText.setEnabled(false);
        });
    }

    /**
     * Toolbar 초기화
     */
    private void initToolbar() {
        binding.fragmentNicknameChangeToolbar.setNavigationOnClickListener(t ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack());
    }

    /**
     * - Observer 세팅하는 메서드
     * - 닉네임 변경 관련 에러를 처리하고 난 후 EditText를 enable 상태로 되돌린다.
     */
    private void initObserver() {
        viewModel.getSystemMessageLiveData().observe(this, systemMessage ->{
            MessageUtils.showToast(requireContext(), systemMessage);
            binding.fragmentNicknameChangeNicknameEditText.setEnabled(true);
        });

        viewModel.getNetworkErrorLiveData().observe(this, errorResponse -> {
            MessageUtils.showToast(requireContext(), errorResponse.getMessage());
            binding.fragmentNicknameChangeNicknameEditText.setEnabled(true);
        });

        /**
         * - 닉네임 변경 완료 수신 후 다음과 같은 작업 수행
         * 1. 설정 파일에 변경한 닉네임 갱신
         * 2. 변경 완료 메시지를 사용자에게 알림.
         * 3. 변경 여부를 이전 Fragment인 AccountSettingFragment에게 알림.
         * 4. EditText를 Enable 상태로 변경.
         *
         */
        viewModel.getNicknameChangedLiveData().observe(this, aBoolean -> {
            AppConfig.UserPreference.setNickname(viewModel.getNicknameLiveData().getValue());
            DialogUtils.showAlertDialog(requireContext(), "변경되었습니다.");
            Bundle bundle = new Bundle();
            bundle.putSerializable(AccountSettingFragment.RESULT_KEY_NICKNAME_CHANGED, true);
            getParentFragmentManager()
                    .setFragmentResult(AccountSettingFragment.REQUEST_CODE_NICKNAME_CHANGE_FRAGMENT,
                            bundle);
            binding.fragmentNicknameChangeNicknameEditText.setEnabled(true);
        });
    }
}