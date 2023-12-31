package com.example.wemakepass.view.auth.findAccount;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.wemakepass.R;
import com.example.wemakepass.databinding.ActivityFindAccountBinding;
import com.example.wemakepass.util.DialogUtils;
import com.example.wemakepass.view.auth.findAccount.id.FindUserIdFragment;
import com.example.wemakepass.view.auth.findAccount.password.FindPasswordFragment;
import com.google.android.material.tabs.TabLayout;

/**
 * - 아이디, 비밀번호 찾기를 처리하는 Activity로서 Container View 역할을 한다.
 * - 최초 실행시 첫 번째 탭인 아이디 찾기가 Container에 attach된다.
 * - 기존에 만들었던 구조를 개선하여 새로 작성하였다.
 *
 * @author BH-Ku
 * @since 2023-10-24
 */
public class FindAccountActivity extends AppCompatActivity {
    private ActivityFindAccountBinding binding;
    private FindPasswordFragment findPasswordFragment;

    private final String TAG = "TAG_FindAccountActivity";
    private final int TAB_FIND_ID = 0;
    private final int TAB_FIND_PASSWORD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFindAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initTabLayout();
    }

    /**
     *  Toolbar의 Navigation Button(←)을 누를 경우 비밀번호 찾기가 진행 중인지 확인하고 진행 중일 경우
     * 정말로 종료할 것인지 묻는 Dialog를 출력한다.
     */
    private void initToolbar() {
        binding.activityFindAccountToolbar.setNavigationOnClickListener(v -> {
            if(findPasswordFragment != null && findPasswordFragment.isRunningTimer())
                findPasswordFragment.showShutdownConfirmDialog();
        });
    }

    /**
     * - TabLayout에 대한 초기 설정을 수행한다.
     * - strings.xml에 작성해놓은 Tab 이름들을 불러와 세팅한 뒤 탭이 선택되었을 때 호출되는 콜백 리스너를 작성한다.
     * - 아이디 찾기 상태에서 비밀번호 찾기로의 전환에는 제약은 없다. 반대로 비밀번호 찾기를 수행할 때 인증이
     *  진행 중이라면 인증이 초기화됨을 경고하는 Dialog를 출력하여 정말로 종료할 것인지 여부를 묻는다.
     *
     * - 특이점으로 TabIndicator를 제어하는데 실패했다. 비밀번호 찾기가 수행되고 있을 때 아이디 찾기를 누르면
     *  탭을 가리키는 Indicator가 이동되기 전에 탭 전환 여부를 먼저 확인하고 싶었는데 탭을 선택하는 순간 무조건
     *  indicator가 이동했다. 각 Tab에 직접 OnClick, OnTouchListener를 부착하는 등의 여러 시도를 해봤지만
     *  실패했다. 다만 기능상 문제가 있는 것은 아니다.
     */
    private void initTabLayout() {
        String[] tabItemNames = getResources().getStringArray(R.array.tab_items_find_account);
        TabLayout tabLayout = binding.activityFindAccountTabLayout;

        for(String tabName : tabItemNames)
            tabLayout.addTab(tabLayout.newTab().setText(tabName));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(findPasswordFragment != null &&
                        tabLayout.getSelectedTabPosition() != TAB_FIND_PASSWORD &&
                        findPasswordFragment.isRunningTimer()){
                    DialogUtils.showConfirmDialog(binding.getRoot().getContext(),
                            "비밀번호 찾기가 진행중입니다. 종료하시겠습니까?",
                            dialog -> {
                                dialog.dismiss();
                                replaceFragment();
                            }, dialog -> {
                                dialog.dismiss();
                                tabLayout.selectTab(tabLayout.getTabAt(TAB_FIND_PASSWORD));
                            });
                    return;
                }
                replaceFragment();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        replaceFragment();
    }

    /**
     * - 현재 Container View에 표시된 Fragment를 현재 선택된 Tab에 맞는 Fragment로 교체한다.
     * - 아이디 찾기 프래그먼트와 달리 비밀번호 찾기 프래그먼트는 타이머 백그라운드 작업(Observable)이 관찰
     *  가능한 상태인지 확인해야 하므로 멤버 변수에 등록한다. 다만 메모리 누수 방지를 위해서 아이디 찾기 탭이
     *  선택될 경우 비밀번호 찾기 프래그먼트를 Unreachable Object로 만들어준다.
     * - 비밀번호 찾기 인증 수행 중 아이디 찾기를 선택했지만 Dialog에서 취소를 누를 경우 이미 indicator가 아이디
     *  찾기로 넘어간 상태이므로 indicator를 이동시키는 메서드(selectedTab)를 호출하여 다시 indicator를
     *  돌려놔야 한다. 이 과정에서 별도의 처리를 해주지 않으면 FindPasswordFragment가 새로 초기화되므로
     *  ContainerView에 부착된 View를 확인하여 같은 View인지 확인하여 같을 경우 업데이트하지 않도록 한다.
     */
    public void replaceFragment() {
        int selectedTab = binding.activityFindAccountTabLayout.getSelectedTabPosition();
        int containerViewId = binding.activityFindAccountContainerView.getId();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();

        if(selectedTab == TAB_FIND_ID) {
            fragmentTransaction.replace(containerViewId, FindUserIdFragment.newInstance());
            findPasswordFragment = null; // 메모리 낭비 방지
        } else {
            if(findPasswordFragment != null &&
                    binding.activityFindAccountContainerView.getChildAt(0) == findPasswordFragment.getView()) {
                return;
            }
            findPasswordFragment = FindPasswordFragment.newInstance();
            fragmentTransaction.replace(containerViewId, findPasswordFragment);
        }

        fragmentTransaction.commit();
    }
}