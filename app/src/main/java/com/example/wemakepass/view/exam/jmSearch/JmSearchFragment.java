package com.example.wemakepass.view.exam.jmSearch;

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
import com.example.wemakepass.databinding.FragmentJmSearchBinding;
import com.example.wemakepass.view.exam.ExamActivity;
import com.example.wemakepass.view.exam.ExamViewModel;
import com.example.wemakepass.view.home.HomeViewModel;
import com.google.android.material.tabs.TabLayout;

/**
 * 종목을 검색하는 기능을 제공하는 Fragment
 *
 * @author BH-Ku
 * @since 2023-11-13
 */
public class JmSearchFragment extends Fragment {
    private FragmentJmSearchBinding binding;
    private ExamViewModel viewModel;

    private final int TAB_SEARCH_RESULT = 0; // 검색 결과를 나타낼 Tab의 index
    private final int TAB_SEARCH_LOG = 1; // 검색 기록을 나타낼 Tab의 index
    private final String TAG = "TAG_JmSearchFragment";

    public static JmSearchFragment newInstance() {
        return new JmSearchFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_jm_search, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(requireActivity()).get(ExamViewModel.class);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar();
        initTabLayout();
    }

    /**
     * Toolbar를 초기화한다.
     */
    private void initToolbar() {
        binding.fragmentJmSearchToolbar.setNavigationOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack());
    }

    /**
     * TabLayout에 대한 초기 설정을 수행한다.
     */
    private void initTabLayout() {
        String[] tabItemNames = getResources().getStringArray(R.array.tab_items_search_log);
        TabLayout tabLayout = binding.fragmentJmSearchTabLayout;

        for(String tabName : tabItemNames)
            tabLayout.addTab(tabLayout.newTab().setText(tabName));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tabLayout.getSelectedTabPosition() == TAB_SEARCH_RESULT) {
                    //
                } else if(tabLayout.getSelectedTabPosition() == TAB_SEARCH_LOG){
                    //
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }
}