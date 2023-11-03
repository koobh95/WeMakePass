package com.example.wemakepass.view.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.wemakepass.R;
import com.example.wemakepass.databinding.ActivityMainBinding;
import com.example.wemakepass.view.auth.AuthActivity;
import com.example.wemakepass.view.community.CommunityFragment;
import com.example.wemakepass.view.examInfo.ExamInfoFragment;
import com.example.wemakepass.view.home.HomeFragment;
import com.example.wemakepass.view.myInfo.MyInfoActivity;
import com.example.wemakepass.view.workbook.WorkbookFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * - 사용자가 로그인을 정상적으로 수행한 후, 혹은 사용자가 앱에 접속했을 때 가지고 있는 토큰의 유효성이 검증되었을
 *  경우에만 이 화면에 진입할 수 있다.
 * - MainActivity는 Fragment를 부착할 ContainerView와 컨테이너에 표시할 Fragment를 변경하기 위한
 *  BottomNavigationView만을 가지고 있으며 이 클래스는 오로지 컨테이너에 표시되는 Fragment 변경에 관한 동작만
 *  수행한다.
 *
 * @author BH-Ku
 * @since 2023-11-01
 */
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private HomeFragment homeFragment;
    private ExamInfoFragment examInfoFragment;
    private CommunityFragment communityFragment;
    private WorkbookFragment workbookFragment;

    private final String TAG = "TAG_MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initBottomNavigationView();

        startActivity(new Intent(this, AuthActivity.class));
    }

    /**
     * - BottomNavigationView에 대한 설정을 수행한다.
     * - 메뉴 첫 번째에 위치한 HomeFragment가 Activity에 진입한 후 가장 먼저 표시되므로 HomeFragment를
     *  객체화하여 Container에 추가한다.
     * - HomeFragment를 제외한 나머지 3개(ExamInfo, Community, Workbook)는 사용자에 의해 선택될 수도 있고
     *  선택되지 않을 수도 있다. 따라서 사용자가 해당 메뉴를 선택할 경우애만 객체화를 수행하도록 하여 쓸데없는
     *  메모리 낭비를 최소화한다.
     * - 마지막 메뉴인 MyInfo를 선택하면 MyInfoActivity를 실행한다.
     */
    private void initBottomNavigationView() {
        BottomNavigationView bottomNav = binding.activityMainBottomNavigationView;
        addFragment(homeFragment = new HomeFragment());

        bottomNav.setOnItemSelectedListener(item -> {
            int selectedItemId = item.getItemId();

            if (selectedItemId == R.id.menu_main_bottom_exam_info) {
                if (examInfoFragment == null)
                    addFragment(examInfoFragment = new ExamInfoFragment());
            } else if (selectedItemId == R.id.menu_main_bottom_community) {
                if (communityFragment == null)
                    addFragment(communityFragment = new CommunityFragment());
            } else if (selectedItemId == R.id.menu_main_bottom_workbook) {
                if (workbookFragment == null)
                    addFragment(workbookFragment = new WorkbookFragment());
            } else if (selectedItemId == R.id.menu_main_bottom_my_info) {
                startActivity(new Intent(this, MyInfoActivity.class));
                return false;
            }

            replaceFragment(selectedItemId);
            return true;
        });
    }

    /**
     * - ContainerView에 Parameter로 들어온 Fragment를 부착한다.
     * - 이 메서드는 최소 1번, 최대 4번 호출된다. Fragment들은 관련 Menu가 선택되지 않는 이상 객체화되지 않고
     *  Container에 add 될 일도 없기 때문이다. 또한 한 번 add되면 show, hide되는 일은 있어도 replace, remove
     *  되는 일은 없다.
     *
     * @param fragment : 현재 선택된 Menu의 Fragment
     */
    private void addFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(binding.activityMainContainerView.getId(), fragment)
                .commit();
    }

    /**
     * - 현재 선택된 메뉴에 해당하는 Fragment를 제외한 나머지 모든 Fragment들은 hide 메서드를 호출하여 보이지
     *  않게 하고 선택된 메뉴에 대한 Fragment만 show를 호출하여 보이도록 한다.
     * - 이 메서드는 Reselect 시에는 호출되지 않는다.
     * - hide 시키려는 Fragment가 아직 객체화되지 않았을 가능성이 있으므로 null을 체크한다.
     * - 단순 replace나 add로 Fragment를 교체하면 메뉴를 이동한 후 이전 Fragment의 상태가 유지되지 않기 때문에
     *  이와 같이 로직을 짰다.
     *
     * @param selectedMenuId
     */
    private void replaceFragment(int selectedMenuId) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();

        if (selectedMenuId == R.id.menu_main_bottom_home)
            fragmentTransaction.show(homeFragment);
        else
            fragmentTransaction.hide(homeFragment);

        if (selectedMenuId == R.id.menu_main_bottom_exam_info)
            fragmentTransaction.show(examInfoFragment);
        else if (examInfoFragment != null)
            fragmentTransaction.hide(examInfoFragment);

        if (selectedMenuId == R.id.menu_main_bottom_community)
            fragmentTransaction.show(communityFragment);
        else if (communityFragment != null)
            fragmentTransaction.hide(communityFragment);

        if (selectedMenuId == R.id.menu_main_bottom_workbook)
            fragmentTransaction.show(workbookFragment);
        else if (workbookFragment != null)
            fragmentTransaction.hide(workbookFragment);

        fragmentTransaction.commit();
    }
}