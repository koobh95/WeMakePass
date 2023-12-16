package com.example.wemakepass.view.exam.select.jmSearch;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
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

/**
 * 시험을 제공하는 종목 검색을 제공하는 Fragment.
 *
 * @author BH-Ku
 * @since 2023-11-13
 */
public class JmSearchFragment extends Fragment {
    private FragmentJmSearchBinding binding;
    private JmSearchViewModel viewModel;

    private JmSearchListAdapter jmSearchResultListAdapter;
    private SearchLogListAdapter searchLogListAdapter;

    private final int LAYOUT_LOG = 0;
    private final int LAYOUT_SEARCH_RESULT = 1;
    public static final String RESULT_REQUEST_CODE_JM_SEARCH_FRAGMENT = "jmSearchFragment";
    public static final String ARG_SELECTED_JM_INFO = "selectedJmInfo";
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
        initToolbar();
        initObserver();
        initEventListener();
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
         *  검색어를 입력하는 EditText에 검색어가 모두 지워질 경우 검색 기록 레이아웃이 보여지도록 Visibility를
         * 변경한다.
         */
        viewModel.getKeywordLiveData().observe(this, keyword ->{
            if(TextUtils.isEmpty(keyword))
                changeLayoutVisibility(LAYOUT_LOG);
        });

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
         * - 검색 결과가 1개 이상 있다면 검색 기록 레이아웃과 검색 결과 레이아웃의 Visibility를 조정한다.
         */
        viewModel.getJmInfoListLiveData().observe(this, list -> {
            jmSearchResultListAdapter.submitList(list);
            if (list.size() == 0)
                MessageUtils.showToast(requireContext(), "검색 결과가 없습니다.");
            changeLayoutVisibility(LAYOUT_SEARCH_RESULT);
        });
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

        // 검색 기록 레이아웃에서 로그 삭제 버튼
        binding.fragmentJmSearchLogDeleteAllButton.setOnClickListener(v -> {
            DialogUtils.showConfirmDialog(requireContext(),
                    "모든 로그를 삭제하시겠습니까?",
                    dialog -> {
                        viewModel.deleteLogAll();
                        dialog.dismiss();
                    });
        });

        // 검색 결과 레이아웃에서 X 버튼
        binding.fragmentJmSearchResultClearButton.setOnClickListener(v ->
                changeLayoutVisibility(LAYOUT_LOG));
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
     * - 검색 기록 RecyclerView를 초기화한다.
     * - 가장 최근에 검색한 기록을 가장 위에 보여주기 위해서 역순으로 출력한다.
     */
    private void initSearchLogRecyclerView() {
        RecyclerView recyclerView = binding.fragmentJmSearchLogRecyclerView;
        searchLogListAdapter = new SearchLogListAdapter();
        searchLogListAdapter.setOnItemClickListener(position -> {
            viewModel.getKeywordLiveData().setValue(
                    searchLogListAdapter.getCurrentList().get(position));
            viewModel.search();
        });
        searchLogListAdapter.setOnRemoveButtonClickListener(position ->
                viewModel.deleteLog(position));
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(searchLogListAdapter);
    }

    /**
     * 종목 검색 결과 RecylcerView를 초기화한다.
     */
    private void initSearchResultRecyclerView() {
        jmSearchResultListAdapter = new JmSearchListAdapter();
        jmSearchResultListAdapter.setOnItemClickListener(position -> {
            JmInfoDTO selectedJmInfo = jmSearchResultListAdapter.getCurrentList().get(position);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ARG_SELECTED_JM_INFO, selectedJmInfo);
            getParentFragmentManager().setFragmentResult
                    (RESULT_REQUEST_CODE_JM_SEARCH_FRAGMENT, bundle);
            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false);
        binding.fragmentJmSearchResultRecyclerView.setLayoutManager(layoutManager);
        binding.fragmentJmSearchResultRecyclerView.setAdapter(jmSearchResultListAdapter);
    }

    /**
     * 검색 기록 레이아웃과 검색 결과 레이아웃 중 화면에 보여질 레이아웃을 결정한다.
     *
     * @param visibleLayout 화면에 보여줄 레이아웃
     */
    private void changeLayoutVisibility(int visibleLayout) {
        ConstraintLayout logLayout = binding.fragmentJmSearchLogLayout;
        ConstraintLayout searchLayout = binding.fragmentJmSearchResultLayout;

        if(visibleLayout == LAYOUT_LOG) {
            logLayout.setVisibility(View.VISIBLE);
            searchLayout.setVisibility(View.GONE);
        } else if (visibleLayout == LAYOUT_SEARCH_RESULT){
            logLayout.setVisibility(View.GONE);
            searchLayout.setVisibility(View.VISIBLE);
        }
    }
}