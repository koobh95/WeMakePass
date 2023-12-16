package com.example.wemakepass.view.board.post.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;

import com.example.wemakepass.R;
import com.example.wemakepass.adapter.PostListAdapter;
import com.example.wemakepass.adapter.SearchLogListAdapter;
import com.example.wemakepass.data.model.dto.BoardDTO;
import com.example.wemakepass.data.model.dto.PostDTO;
import com.example.wemakepass.data.model.dto.response.PostPageResponse;
import com.example.wemakepass.databinding.FragmentPostSearchBinding;
import com.example.wemakepass.util.DialogUtils;
import com.example.wemakepass.util.KeyboardUtils;
import com.example.wemakepass.util.MessageUtils;
import com.example.wemakepass.view.board.BoardActivity;
import com.example.wemakepass.view.board.post.viewer.PostViewerFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 게시글 검색 환경을 제공하는 Fragment
 *
 * @author BH-Ku
 * @since 2023-12-15
 */
public class PostSearchFragment extends Fragment {
    private FragmentPostSearchBinding binding;
    private PostSearchViewModel viewModel;

    private SearchLogListAdapter searchLogListAdapter;
    private PostListAdapter postListAdapter;

    private BoardDTO boardDTO; // 검색할 게시판
    private String[] categoryArray; // 게시판에서 제공하는 카테고리 목록
    private int pageNo; // 현재 검색 중인 페이지 번호
    private boolean lastPage; // 마지막 페이지 여부

    private static final String ARG_BOARD_DTO = "boardDTO";
    private static final String ARG_CATEGORY_LIST = "categoryList";
    private final int LOG_LAYOUT_VIEW = 0;
    private final int SEARCH_RESULT_LAYOUT_VIEW = 1;
    private final String TAG = "TAG_PostSearchFragment";

    public static PostSearchFragment newInstance(BoardDTO boardDTO, String[] category) {
        PostSearchFragment fragment = new PostSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_BOARD_DTO, boardDTO);
        bundle.putStringArray(ARG_CATEGORY_LIST, category);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        assert bundle != null;
        boardDTO = (BoardDTO) bundle.getSerializable(ARG_BOARD_DTO);

        List<String> categoryList = new ArrayList<>(
                Arrays.asList(bundle.getStringArray(ARG_CATEGORY_LIST)));
        categoryList.add(0, "전체");
        categoryArray = categoryList.toArray(new String[categoryList.size()]);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post_search, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(PostSearchViewModel.class);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar();
        initCategorySpinner();
        initSearchOptionSpinner();
        initSearchLogRecyclerView();
        initSearchResultRecyclerView();
        initEventListener();
        initObserver();
    }

    @Override
    public void onStop() {
        super.onStop();
        KeyboardUtils.hideKeyboard(requireActivity(), binding.getRoot()); // 키보드가 열려 있다면 숨김
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Toolbar를 초기화한다.
     */
    private void initToolbar() {
        binding.fragmentPostSearchToolbar.setNavigationOnClickListener(v ->
            requireActivity().getSupportFragmentManager().popBackStack());
    }

    /**
     * 게시판의 카테고리를 스피너에 초기화한다.
     */
    private void initCategorySpinner() {
        ArrayAdapter<String> categoryArrayAdapter = new ArrayAdapter<>(requireContext(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categoryArray);
        binding.fragmentPostSearchCategorySpinner.setAdapter(categoryArrayAdapter);
    }

    /**
     * 검색 옵션을 스피너에 초기화한다.
     */
    private void initSearchOptionSpinner() {
        String[] optionArr = getResources().getStringArray(R.array.post_search_option);
        ArrayAdapter<String> optionArrayAdapter = new ArrayAdapter<>(requireContext(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, optionArr);
        binding.fragmentPostSearchOptionSpinner.setAdapter(optionArrayAdapter);
    }

    /**
     * 검색 기록 RecyclerView를 초기화한다.
     */
    private void initSearchLogRecyclerView() {
        RecyclerView recyclerView = binding.fragmentPostSearchLogRecyclerView;
        searchLogListAdapter = new SearchLogListAdapter();
        searchLogListAdapter.setOnItemClickListener(position -> {
            String keyword = searchLogListAdapter.getCurrentList().get(position);
            viewModel.getKeywordLiveData().setValue(keyword);
            search();
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
     * 검색 결과 RecyclerView를 초기화한다.
     */
    private void initSearchResultRecyclerView() {
        RecyclerView recyclerView = binding.fragmentPostSearchResultRecyclerView;
        postListAdapter = new PostListAdapter();
        postListAdapter.setOnItemClickListener(position -> {
            ((BoardActivity)requireActivity()).addFragment(PostViewerFragment.newInstance(
                            boardDTO.getBoardName(),
                            postListAdapter.getCurrentList().get(position).getPostNo()),
                    R.anim.slide_from_end,
                    R.anim.slide_to_end);
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setAdapter(postListAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(),
                RecyclerView.VERTICAL));
        initNestedScrollViewScrollListener();
    }

    /**
     *  무한 로딩을 수행하기 위해서 스크롤 리스너를 구현하였다. 스크롤이 될 때 스크롤의 위치를 확인하여 끝까지
     * 내려왔다면 ContentLoadingProgressBar를 표시하고 다음 데이터를 요청한다.
     */
    private void initNestedScrollViewScrollListener(){
        binding.fragmentPostSearchResultNestedScrollView.setOnScrollChangeListener(
                (NestedScrollView.OnScrollChangeListener)
                        (v, scrollX, scrollY, oldScrollX, oldScrollY) -> { // 스크롤이 끝까지 내려왔음.
                            if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
                                // 마지막으로 조회한 데이터가 마지막 페이지였을 경우 추가 조회를 수행하지 않음.
                                if(lastPage)
                                    MessageUtils.showSnackbar(binding.getRoot(), "마지막 페이지입니다.");
                                else
                                    loadMore();
                            }
                        });
    }

    /**
     * LiveData와 직접적인 연관이 없는 View에 대한 이벤트를 설정하는 메서드.
     */
    private void initEventListener() {
        // 검색 버튼
        binding.fragmentPostSearchButton.setOnClickListener(v -> search());

        // 검색 기록 모두 삭제
        binding.fragmentPostSearchLogDeleteAllImageButton.setOnClickListener(v -> {
            DialogUtils.showAlertDialog(requireContext(), "기록을 모두 삭제하시겠습니까?",
                    dialog -> viewModel.deleteLogAll());
        });

        // 검색 결과 화면 닫기(숨기기)
        binding.fragmentPostSearchResultCloseButton.setOnClickListener(v ->
                changeLayoutVisibility(LOG_LAYOUT_VIEW));

        binding.fragmentPostSearchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH)
                search();
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

        // 게시글 검색 결과 옵저빙
        viewModel.getPostResponseLiveData().observe(this, this::setPostList);

        // 게시글 검색 기록 조회 결과 옵저빙
        viewModel.getSearchLogListLiveData().observe(this, list ->
                searchLogListAdapter.submitList(list));
    }

    /**
     * 검색 기록 레이아웃과 검색 결과 레이아웃 중 화면에 보여질 레이아웃을 결정한다.
     *
     * @param visibleLayout 화면에 보여줄 레이아웃
     */
    private void changeLayoutVisibility(int visibleLayout) {
        ConstraintLayout logLayout = binding.fragmentPostSearchLogLayout;
        ConstraintLayout searchLayout = binding.fragmentPostSearchResultLayout;

        if(visibleLayout == LOG_LAYOUT_VIEW) {
            logLayout.setVisibility(View.VISIBLE);
            searchLayout.setVisibility(View.GONE);
        } else if (visibleLayout == SEARCH_RESULT_LAYOUT_VIEW){
            logLayout.setVisibility(View.GONE);
            searchLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * - 게시글 검색에 성공했을 경우 호출되며 조회한 데이터를 기반으로 RecyclerView를 갱신한다.
     * - 조회한 데이터의 페이지 번호가 0이고 읽어온 데이터가 없을 경우 검색 결과가 없다고 판단한다.
     * - 조회한 데이터의 페이지 번호가 0이고 데이터가 1개 이상 존재할 경우 최초 로딩이라고 판단하고
     *  RecyclerView를 즉시 갱신한다.
     * - 조회한 데이터의 페이지 번호 1 이상일 때 추가 로딩이라고 판단하고 기존 리스트와 새로운 리스트를 병합한다.
     * - 검색 혹은 추가 조회한 데이터가 마지막일 경우(PostPageResponse.last == true) 상태를 lastPage 변수에
     *  저장하여 새로운 검색이 발생하기 전까지는 추가 로딩을 시도해도 요청이 발생하지 않도록 방지한다.
     *
     * @param postPageResponse
     */
    private void setPostList(PostPageResponse postPageResponse) {
        binding.fragmentPostSearchResultLoadingProgressBar.setVisibility(View.GONE);

        if(postPageResponse.getPageNo() == 0 && postPageResponse.getPostList().size() == 0){
            MessageUtils.showToast(requireContext(), "검색 결과가 없습니다.");
            return;
        }

        lastPage = postPageResponse.isLast();

        if(postPageResponse.getPageNo() == 0)
            postListAdapter.submitList(postPageResponse.getPostList());
        else {
            List<PostDTO> list = new ArrayList<>(postListAdapter.getCurrentList().size() +
                    postPageResponse.getPostList().size());
            list.addAll(postListAdapter.getCurrentList());
            list.addAll(postPageResponse.getPostList());
            postListAdapter.submitList(list);
        }

        changeLayoutVisibility(SEARCH_RESULT_LAYOUT_VIEW);
    }

    /**
     * 선택된 게시판 카테고리를 반환
     * @return 카테고리가 "전체"일 경우 공백, 아닐 경우 아이템의 텍스트 반환
     */
    private String getSelectedCategory() {
        int categoryIdx = binding.fragmentPostSearchCategorySpinner.getSelectedItemPosition();
        if(categoryIdx != 0)
            return categoryArray[categoryIdx];
        return "";
    }

    /**
     * 추가 로딩을 요청한다.
     */
    private void loadMore() {
        binding.fragmentPostSearchResultLoadingProgressBar.setVisibility(View.VISIBLE);

        int optionIdx = binding.fragmentPostSearchOptionSpinner.getSelectedItemPosition();
        viewModel.search(optionIdx, pageNo++, boardDTO.getBoardNo(), getSelectedCategory());
    }

    /**
     * 검색을 요청한다.
     */
    private void search() {
        if (!viewModel.isValidKeyword())
            return;
        lastPage = false;
        pageNo = 0;

        int optionIdx = binding.fragmentPostSearchOptionSpinner.getSelectedItemPosition();
        viewModel.search(optionIdx, pageNo++, boardDTO.getBoardNo(), getSelectedCategory());
    }
}