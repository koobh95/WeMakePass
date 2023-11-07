package com.example.wemakepass.view.accountSetting;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wemakepass.R;
import com.example.wemakepass.adapter.SettingCategoryListAdapter;
import com.example.wemakepass.config.AppConfig;
import com.example.wemakepass.data.model.data.SettingOptionModel;
import com.example.wemakepass.data.model.vo.SettingCategoryVO;
import com.example.wemakepass.databinding.FragmentAccountSettingBinding;
import com.example.wemakepass.view.accountSetting.nicknameChange.NicknameChangeFragment;
import com.example.wemakepass.view.accountSetting.passwordReset.PasswordResetFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * - 사용자 계정 정보를 보여주거나 일부를 변경할 수 있는 환경을 제공하는 Fragment.
 * - AccountSettingActivity가 실행되었을 때 가장 먼저 실행된다.
 *
 * @author BH-Ku
 * @since 2023-11-05
 */
public class AccountSettingFragment extends Fragment {
    private FragmentAccountSettingBinding binding;

    private List<SettingCategoryVO> categoryList;
    private SettingCategoryListAdapter settingCategoryListAdapter;

    public static final String REQUEST_CODE_NICKNAME_CHANGE_FRAGMENT = "nicknameChangeFragment";
    public static final String RESULT_KEY_NICKNAME_CHANGED = "nicknameChanged";
    private final String TAG = "TAG_AccountSettingFragment";

    public static AccountSettingFragment newInstance() {
        return new AccountSettingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountSettingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFragmentResultListener();
        initToolbar();
        initSettingRecyclerView();
    }

    /**
     * NicknameChangeFragment에서 닉네임이 변경될 경우 결과를 수신하여 RecyclerView의 닉네임을 업데이트한다.
     */
    public void initFragmentResultListener() {
        getParentFragmentManager().setFragmentResultListener(
                REQUEST_CODE_NICKNAME_CHANGE_FRAGMENT, requireActivity(),
                (requestKey, result) -> {
                    categoryList.get(0).getOptionList().get(1).setContent(
                            AppConfig.UserPreference.getNickname());
                    settingCategoryListAdapter.notifyItemChanged(0);
                });
    }

    /**
     * Toolbar의 NavigationButton에 대한 이벤트를 설정한다.
     */
    public void initToolbar(){
        binding.fragmentAccountSettingToolbar.setNavigationOnClickListener(v ->
                requireActivity().finish());
    }

    /**
     * SettingRecyclerView 에 대한 초기 설정을 수행한다.
     */
    private void initSettingRecyclerView(){
        List<SettingCategoryVO> categoryList = getSettingList();
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setInitialPrefetchItemCount(categoryList.size());
        settingCategoryListAdapter = new SettingCategoryListAdapter(categoryList);
        binding.fragmentAccountSettingSettingRecyclerView.setLayoutManager(layoutManager);
        binding.fragmentAccountSettingSettingRecyclerView.setAdapter(settingCategoryListAdapter);
    }

    /**
     * - SettingRecyclerView에 표시할 데이터를 생성 및 반환한다.
     * - SharedPreferences에 저장된 아이디, 닉네임, 이메일 등을 제외하고는 모두 하드 코딩이기 때문에 조금
     *  불만족스럽지만 데이터들이 규칙적이지 않아 string.xml에 리소스를 생성하기도 애매하다고 판단하여 하드 코딩을
     *  하였다. 나중에 괜찮은 방안이 생각나면 변경하도록 한다.
     *
     * @return
     */
    private List<SettingCategoryVO> getSettingList() {
        categoryList = new ArrayList<>();

        List<SettingOptionModel> myInfoOptionList = new ArrayList<>();
        myInfoOptionList.add(new SettingOptionModel.SettingOptionBuilder()
                .setSummary("아이디")
                .setContent(AppConfig.UserPreference.getUserId())
                .build());
        myInfoOptionList.add(new SettingOptionModel.SettingOptionBuilder()
                .setSummary("닉네임")
                .setContent(AppConfig.UserPreference.getNickname())
                .setOnClickListener(v ->
                    ((AccountSettingActivity)requireActivity())
                            .addFragment(NicknameChangeFragment.newInstance(),
                                    R.anim.slide_from_bottom, R.anim.slide_to_bottom))
                .build());
        myInfoOptionList.add(new SettingOptionModel.SettingOptionBuilder()
                .setSummary("이메일")
                .setContent(AppConfig.UserPreference.getEmail())
                .build());
        categoryList.add(new SettingCategoryVO("개인 정보", myInfoOptionList));

        List<SettingOptionModel> accountModifyOptionList = new ArrayList<>();
        accountModifyOptionList.add(new SettingOptionModel.SettingOptionBuilder()
                .setContent("비밀번호 변경")
                .setOnClickListener(v -> {

                })
                .build());
        categoryList.add(new SettingCategoryVO(accountModifyOptionList));

        List<SettingOptionModel> accountManageOptionList = new ArrayList<>();
        accountManageOptionList.add(new SettingOptionModel.SettingOptionBuilder()
                .setContent("로그아웃")
                .setOnClickListener(v -> {
                    //
                })
                .build());
        accountManageOptionList.add(new SettingOptionModel.SettingOptionBuilder()
                .setContent("서비스 탈퇴")
                .setOnClickListener(v -> {
                    //
                })
                .build());
        categoryList.add(new SettingCategoryVO(accountManageOptionList));

        return categoryList;
    }
}