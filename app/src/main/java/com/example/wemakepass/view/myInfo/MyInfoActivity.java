package com.example.wemakepass.view.myInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.example.wemakepass.R;
import com.example.wemakepass.adapter.SettingCategoryListAdapter;
import com.example.wemakepass.config.AppConfig;
import com.example.wemakepass.data.model.data.SettingOptionModel;
import com.example.wemakepass.data.model.vo.SettingCategoryVO;
import com.example.wemakepass.databinding.ActivityMyInfoBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * - 회원 정보 확인, 정보 변경 등을 수행하는 설정 화면에 해당하는 Activity다.
 *
 * @author BH-Ku
 * @since 2023-11-02
 */
public class MyInfoActivity extends AppCompatActivity {
    private ActivityMyInfoBinding binding;
    private MyInfoViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_info);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(MyInfoViewModel.class);
        binding.setViewModel(viewModel);
        initToolbar();
        initSettingRecyclerView();
    }

    /**
     * Toolbar의 NavigationButton에 대한 이벤트를 설정한다.
     */
    public void initToolbar(){
        binding.activityMyInfoToolbar.setNavigationOnClickListener(v -> finish());
    }

    /**
     * SettingRecyclerView 에 대한 초기 설정을 수행한다.
     */
    private void initSettingRecyclerView(){
        List<SettingCategoryVO> categoryList = getSettingList();
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setInitialPrefetchItemCount(categoryList.size());
        SettingCategoryListAdapter adapter = new SettingCategoryListAdapter(categoryList);
        binding.activityMyInfoSettingRecyclerView.setLayoutManager(layoutManager);
        binding.activityMyInfoSettingRecyclerView.setAdapter(adapter);
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
        List<SettingCategoryVO> categoryList = new ArrayList<>();

        List<SettingOptionModel> myInfoOptionList = new ArrayList<>();
        myInfoOptionList.add(new SettingOptionModel.SettingOptionBuilder()
                .setSummary("아이디")
                .setContent(AppConfig.UserPreference.getUserId())
                .build());
        myInfoOptionList.add(new SettingOptionModel.SettingOptionBuilder()
                .setSummary("닉네임")
                .setContent(AppConfig.UserPreference.getNickname())
                .setOnClickListener(v -> {

                })
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

                })
                .build());
        accountManageOptionList.add(new SettingOptionModel.SettingOptionBuilder()
                .setContent("서비스 탈퇴")
                .setOnClickListener(v -> {

                })
                .build());
        categoryList.add(new SettingCategoryVO(accountManageOptionList));

        return categoryList;
    }
}