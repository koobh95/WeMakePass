package com.example.wemakepass.view.exam.jmSearch;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.example.wemakepass.R;
import com.example.wemakepass.adapter.JmSearchListAdapter;
import com.example.wemakepass.adapter.SearchLogListAdapter;
import com.example.wemakepass.data.model.dto.JmInfoDTO;
import com.example.wemakepass.databinding.FragmentJmSearchBinding;
import com.example.wemakepass.util.DialogUtils;
import com.example.wemakepass.util.MessageUtils;
import com.example.wemakepass.view.exam.ExamActivity;
import com.example.wemakepass.view.exam.select.ExamSelectFragment;
import com.google.android.material.tabs.TabLayout;

/**
 * 시험 선택 화면에서 종목을 검색하는 기능을 제공하는 Fragment
 *
 * @author BH-Ku
 * @since 2023-11-13
 */
public class JmSearchFragment extends Fragment {
    private FragmentJmSearchBinding binding;
    private JmSearchViewModel viewModel;

    private JmSearchListAdapter jmSearchResultListAdapter;
    private SearchLogListAdapter searchLogListAdapter;

    private final int TAB_SEARCH_LOG = 0; // 검색 기록을 나타낼 Tab의 Position
    private final int TAB_SEARCH_RESULT = 1; // 검색 결과를 나타낼 Tab의 Position
    private final String TAG = "TAG_JmSearchFragment";

    public static JmSearchFragment newInstance() {
        return new JmSearchFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_jm_search, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(JmSearchViewModel.class);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSearchLogRecyclerView();
        initSearchResultRecyclerView();
        initEventListener();
        initObserver();
        initToolbar();
        initTabLayout();
    }

    /**
     * LiveData와 직접적인 연관이 없는 View에 대한 이벤트를 설정하는 메서드.
     */
    private void initEventListener() {
        binding.fragmentJmSearchBarEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH)
                viewModel.search();
            return true;
        });
    }

    /**
     * LiveData에 대한 옵저빙을 설정한다.
     */
    private void initObserver() {
        viewModel.getSystemMessageLiveData().observe(this, systemMessage ->
                MessageUtils.showToast(requireContext(), systemMessage));

        viewModel.getNetworkErrorLiveData().observe(this, errorResponse ->
                MessageUtils.showToast(requireContext(), errorResponse.getMessage()));

        /**
         * 검색 기록 리스트를 읽어왔을 때 리스트를 업데이트한다. 업데이트하는 시점은 다음과 같다.
         * 1. Fragment 실행 시 저장된 로그를 읽어왔을 때
         * 2. 검색이 정상적으로 수행되어 검색어가 로그에 정상적으로 추가되었을 때
         * 3. 검색 기록이 삭제되었을 때
         */
        viewModel.getSearchLogListLiveData().observe(requireActivity(), list ->
            searchLogListAdapter.submitList(list));

        /**
         * - 검색을 실행한 후 검색 결과를 관찰한다.
         * - 검색 결과가 없을 경우 Toast로 메시지를 출력한다. 또한 검색 결과가 없더라도 이전 검색 결과를
         *  갱신하기 위해서 리스트는 업데이트한다.
         * - 검색 결과가 1개 이상 있을 때 현재 TabLayout에 선택된 Tab이 "검색 기록"일 경우 "검색 결과"로
         *  강제로 변경하기 위해 관련 메서드를 호출한다.
         */
        viewModel.getJmInfoListLiveData().observe(this, list -> {
            jmSearchResultListAdapter.submitList(list);
            if (list.size() == 0)
                MessageUtils.showToast(requireContext(), "검색 결과가 없습니다.");
            changeRecyclerViewVisibility(TAB_SEARCH_RESULT);
        });
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

        for (String tabName : tabItemNames)
            tabLayout.addTab(tabLayout.newTab().setText(tabName));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changeRecyclerViewVisibility(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    /**
     * - 검색 기록 RecyclerView를 초기화한다.
     * - 가장 최근에 검색한 기록을 가장 위에 보여주기 위해서 역순으로 출력한다.
     */
    private void initSearchLogRecyclerView() {
        searchLogListAdapter = new SearchLogListAdapter();
        searchLogListAdapter.setOnItemClickListener(position -> {
            viewModel.getKeywordLiveData().setValue(
                    searchLogListAdapter.getCurrentList().get(position));
            viewModel.search();
        });
        searchLogListAdapter.setOnRemoveButtonClickListener(position -> viewModel.removeLog(position));
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        binding.fragmentJmSearchLogRecyclerView.setLayoutManager(layoutManager);
        binding.fragmentJmSearchLogRecyclerView.setAdapter(searchLogListAdapter);
    }

    /**
     * 종목 검색 결과 RecylcerView를 초기화한다.
     */
    private void initSearchResultRecyclerView() {
        jmSearchResultListAdapter = new JmSearchListAdapter();
        jmSearchResultListAdapter.setOnItemClickListener(position -> {
            JmInfoDTO selectedJmInfo = jmSearchResultListAdapter.getCurrentList().get(position);
            DialogUtils.showConfirmDialog(requireContext(),
                    "\"" + selectedJmInfo.getJmName()
                            + "\"을(를) 선택하시겠습니까?",
                    dialog -> {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(ExamActivity.ARG_SELECTED_JM_INFO,
                                selectedJmInfo);
                        getParentFragmentManager()
                                .setFragmentResult(ExamSelectFragment.RESULT_REQUEST_CODE_JM_SEARCH_FRAGMENT, bundle);
                        requireActivity()
                                .getSupportFragmentManager()
                                .popBackStack();
                        dialog.dismiss();
                    });
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false);
        binding.fragmentJmSearchResultRecyclerView.setLayoutManager(layoutManager);
        binding.fragmentJmSearchResultRecyclerView.setAdapter(jmSearchResultListAdapter);
    }

    /**
     * - TabLayout에서 선택된 Tab에 따라 다른 RecyclerView를 보여주기 위해서 Visibility를 조정하는 역할을
     *  하는 메서드다.
     * - 검색을 수행했을 때 조회된 데이터가 있고 현재 선택된 탭이 "검색 기록" 탭일 경우 강제로 "검색 결과" 탭으로
     *  이동하도록 하고 있다. 그렇기 때문에 RecyclerView의 Visibility를 조정하기에 앞서 현재 선택된 Tab을
     *  확인하여 일치하지 않을 경우 변경한다.
     *
     * @param visibleTabPosition 선택된 Tab 혹은 선택할 Tab의 Position이다.
     */
    private void changeRecyclerViewVisibility(int visibleTabPosition) {
        TabLayout tabLayout = binding.fragmentJmSearchTabLayout;
        RecyclerView searchLogRecyclerView = binding.fragmentJmSearchLogRecyclerView;
        RecyclerView searchResultRecyclerView = binding.fragmentJmSearchResultRecyclerView;

        if (tabLayout.getSelectedTabPosition() != visibleTabPosition) {
            if (visibleTabPosition == TAB_SEARCH_LOG)
                tabLayout.selectTab(tabLayout.getTabAt(TAB_SEARCH_LOG));
            else
                tabLayout.selectTab(tabLayout.getTabAt(TAB_SEARCH_RESULT));
        }

        if (visibleTabPosition == TAB_SEARCH_LOG) {
            searchResultRecyclerView.setVisibility(View.GONE);
            searchLogRecyclerView.setVisibility(View.VISIBLE);
        } else {
            searchLogRecyclerView.setVisibility(View.GONE);
            searchResultRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}