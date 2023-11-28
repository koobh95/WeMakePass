package com.example.wemakepass.view.main.community.boardSearch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.wemakepass.R;
import com.example.wemakepass.data.enums.JmSearchType;
import com.example.wemakepass.data.model.dto.JmInfoDTO;
import com.example.wemakepass.databinding.ActivityBoardSearchBinding;
import com.example.wemakepass.view.jmSearch.JmSearchFragment;

/**
 *  MainActivity > CommunityFragment 에서 검색 바를 누르면 실행되는 화면으로 게시판이 존재하는 종목을 검색하는
 * JmSearchFragment를 실행할 ContainerView를 제공한다. MainActivity는 BottomNavigator로 인해 풀 스크린을
 * 제공할 수 없어서 별도의 Activity를 생성했다. 
 * 
 *  * Community 관련 UI가 모두 완성될 때 로직을 구현한다.
 * 
 * @author BH-Ku
 * @since 2023-11-29
 */
public class BoardSearchActivity extends AppCompatActivity {
    private ActivityBoardSearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBoardSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initFragment();
    }

    private void initFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.activityBoardSearchContainerView.getId(),
                        JmSearchFragment.newInstance(JmSearchType.SEARCH_BOARD))
                .commit();
    }
}