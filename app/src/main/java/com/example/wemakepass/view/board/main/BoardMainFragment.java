package com.example.wemakepass.view.board.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.example.wemakepass.R;
import com.example.wemakepass.adapter.PostListAdapter;
import com.example.wemakepass.data.model.dto.BoardDTO;
import com.example.wemakepass.data.model.dto.PostDTO;
import com.example.wemakepass.data.model.dto.response.PostPageResponse;
import com.example.wemakepass.databinding.FragmentBoardMainBinding;
import com.example.wemakepass.util.MessageUtils;
import com.example.wemakepass.view.board.BoardActivity;
import com.example.wemakepass.view.board.post.search.PostSearchFragment;
import com.example.wemakepass.view.board.post.viewer.PostViewerFragment;
import com.example.wemakepass.view.board.post.editor.PostEditorFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * - 특정 게시판의 홈페이지 역할을 하는 Fragment다.
 * - 페이징을 위한 page 변수의 경우 ViewModel에 영향을 받지 않도록 View 단에 선언했다. 화면에 리프레쉬 될 경우
 *  게시글 목록도 같이 리프레쉬되도록 하기 위함이다.
 *
 * @author BH-Ku
 * @since 2023-11-30
 */
public class BoardMainFragment extends Fragment {
    private FragmentBoardMainBinding binding;
    private BoardMainViewModel viewModel;

    private PostListAdapter postListAdapter;

    private BoardDTO boardDTO; // 표시할 게시판 정보
    private int pageNo = 0; // 페이징을 수행하기 위한 변수로 현재 출력하고 있는 페이지를 나타낸다.
    private boolean lastPage; // 마지막에 조회한 페이지가 마지막 페이지인지 여부

    private final String TAG = "TAG_BoardMainFragment";

    public static BoardMainFragment newInstance(BoardDTO boardDTO) {
        BoardMainFragment fragment = new BoardMainFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BoardActivity.ARG_SELECTED_BOARD, boardDTO);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        assert bundle != null;
        boardDTO = (BoardDTO) bundle.getSerializable(BoardActivity.ARG_SELECTED_BOARD);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_board_main, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(BoardMainViewModel.class);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar();
        initPostRecyclerView();
        initPostRecyclerViewScrollListener();
        initObserver();
        initEventListener();
        initFragmentResultListener();

        viewModel.loadCategory(boardDTO.getBoardNo());
        viewModel.loadPosts(boardDTO.getBoardNo(), pageNo++, 0);
    }

    /**
     * - Toolbar를 초기화한다.
     * - NavigationButton(←), 메뉴 버튼에 대한 이벤트를 설정한다.
     */
    private void initToolbar() {
        binding.fragmentBoardMainToolbar.setTitle(boardDTO.getBoardName());
        binding.fragmentBoardMainToolbar.setNavigationOnClickListener(v ->
                requireActivity().finish());
        binding.fragmentBoardMainToolbar.setOnMenuItemClickListener(item -> {
            ((BoardActivity)requireActivity()).addFragment(PostSearchFragment.newInstance(),
                    R.anim.slide_from_bottom,
                    R.anim.slide_to_bottom);
            return true;
        });
    }

    /**
     * - TabLayout에 대한 초기화를 수행한다.
     * - 이 화면이 실행되면 표시하려는 게시판의 카테고리 목록을 불러온다. 데이터가 조회될 경우에 이 메서드가
     *  호출되어 불러온 카테고리 목록을 기반으로 TabLayout을 구성한다.
     * - 첫 번째 탭은 반드시 "전체"가 된다.
     *
     * @param categoryList
     */
    private void initTabLayout(List<String> categoryList) {
        TabLayout tabLayout = binding.fragmentBoardMainTabLayout;

        tabLayout.addTab(tabLayout.newTab().setText("전체"));
        for(String category : categoryList)
            tabLayout.addTab(tabLayout.newTab().setText(category));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            /**
             *  새로운 탭이 선택되었다는 것은 특정 카테고리가 선택되었기 때문에 새로운 목록을 조회해야 함을 뜻한다.
             * 따라서 pageNo를 0으로 초기화하고 게시글 RecyclerView을 초기화한다.
             *
             * @param tab The tab that was selected
             */
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                lastPage = false; // 마지막 페이지 여부 초기화
                pageNo = 0; // 페이지 초기화
                postListAdapter.submitList(null); // 리스트 초기화
                binding.fragmentBoardMainPostLoadingProgressBar.setVisibility(View.VISIBLE);
                viewModel.loadPosts(boardDTO.getBoardNo(), pageNo++, tabLayout.getSelectedTabPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    /**
     * - 게시글 목록 RecylcerView를 초기화한다.
     * - 각 아이템을 선택하면 게시글 내용이나 댓글 등의 데이터를 띄어주는 프래그먼트를 실행한다.
     */
    private void initPostRecyclerView() {
        RecyclerView recyclerView = binding.fragmentBoardMainPostRecyclerView;
        postListAdapter = new PostListAdapter();
        postListAdapter.setOnItemClickListener(position -> {
            ((BoardActivity)requireActivity()).addFragment(PostViewerFragment.newInstance(),
                    R.anim.slide_from_end,
                    R.anim.slide_to_end);
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setAdapter(postListAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(),
                RecyclerView.VERTICAL));
    }

    /**
     *  무한 로딩을 수행하기 위해서 스크롤 리스너를 구현하였다. 스크롤이 될 때 스크롤의 위치를 확인하여 끝까지
     * 내려왔다면 ContentLoadingProgressBar를 표시하고 다음 데이터를 요청한다.
     */
    private void initPostRecyclerViewScrollListener(){
        binding.fragmentBoardMainNestedScrollView.setOnScrollChangeListener(
                (NestedScrollView.OnScrollChangeListener)
                        (v, scrollX, scrollY, oldScrollX, oldScrollY) -> { // 스크롤이 끝까지 내려왔음.
            if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
                // 마지막에 조회한 데이터가 마지막 페이지였을 경우 추가 조회를 수행하지 않음.
                if(lastPage) {
                    MessageUtils.showSnackbar(binding.getRoot(), "마지막 페이지입니다.");
                    return;
                }

                binding.fragmentBoardMainPostLoadingProgressBar.setVisibility(View.VISIBLE);
                viewModel.loadPosts(boardDTO.getBoardNo(), pageNo++,
                        binding.fragmentBoardMainTabLayout.getSelectedTabPosition());
            }
        });
    }

    /**
     * - PostEditorFragment에서 게시글 작성에 성공할 경우 게시글 목록을 다시 로딩하기 위해
     *  FragmentResultListener를 설정한다.
     * - 목록을 갱신은 반드시 전체 카테고리를 기준으로 하기 때문에 현재 TabLayout에 선택된 Tab이 "전체"라면
     *  page를 초기화하여 목록 갱신을, "전체"가 아니라면 Tab을 강제로 변경한다.
     */
    private void initFragmentResultListener() {
        getParentFragmentManager()
                .setFragmentResultListener(PostEditorFragment.RESULT_REQUEST_CODE_POST_EDITOR_FRAGMENT,
                        requireActivity(),
                        (requestKey, result) -> {
                            if(result.getBoolean(PostEditorFragment.ARG_WRITE_STATUS)){
                                TabLayout tabLayout = binding.fragmentBoardMainTabLayout;
                                if(tabLayout.getSelectedTabPosition() == 0){
                                    lastPage = false;
                                    viewModel.loadPosts(boardDTO.getBoardNo(), pageNo = 0, 0);
                                } else {
                                    tabLayout.selectTab(tabLayout.getTabAt(0));
                                }
                            }
                        });
    }

    /**
     * LiveData와 직접적인 연관이 없는 View에 대한 이벤트를 설정하는 메서드.
     */
    private void initEventListener() {
        // 게시글 작성
        binding.fragmentBoardMainPostWriteFab.setOnClickListener(v -> {
            if(viewModel.getCategoryListLiveData().getValue() == null)
                return;

            List<String> categoryList = viewModel.getCategoryListLiveData().getValue();
            String[] category = new String[categoryList.size()];
            for(int i = 0; i < categoryList.size(); i ++)
                category[i] = categoryList.get(i);

            ((BoardActivity) requireActivity()).
                    addFragment(PostEditorFragment.newInstance(boardDTO.getBoardNo(), category),
                            R.anim.slide_from_end,
                            R.anim.slide_to_end);
        });
    }

    /**
     * LiveData에 대한 옵저빙을 설정한다.
     */
    private void initObserver() {
        // 게시판의 카테고리를 읽어 오는 것을 관찰하고 데이터가 감지되면 TabLayout을 초기화한다.
        viewModel.getCategoryListLiveData().observe(this, this::initTabLayout);

        // 게시글 조회에 성공했을 경우 처리를 위한 메서드를 호출한다.
        viewModel.getPostListLiveData().observe(this, this::setPostList);
    }

    /**
     * - 게시글 조회에 성공했을 경우 이 메서드가 호출되며 조회한 데이터를 기반으로 RecyclerView를 갱신한다.
     * - 조회한 데이터의 페이지 번호가 0이고 읽어온 데이터가 없을 경우 해당 게시판, 카테고리에 등록된 게시글이
     * 없다고 판단하고 메시지를 출력한다.
     * - 조회한 페이지가 마지막일 경우 상태(lastPage)를 저장하여 카테고리가 변경되기 전까지는 추가 조회가 발생하지
     *  않도록 한다.
     * - 조회한 페이지 번호가 0일 경우 조회한 데이터 목록을 그대로 submit하여 갱신하고 0이 아닐 경우 추가 조회된
     *  데이터라고 판단, 기존 리스트와 새로 조회된 리스트를 병합하여 submit한다.
     *
     * @param postPageResponse
     */
    private void setPostList(PostPageResponse postPageResponse) {
        binding.fragmentBoardMainPostLoadingProgressBar.setVisibility(View.GONE);

        if(postPageResponse.getPageNo() == 0 && postPageResponse.getPostList().size() == 0){
            MessageUtils.showToast(requireContext(), "등록된 게시물이 없습니다.");
            return;
        } // 특정 Board, Category에 대한 게시물이 전혀 없음.

        if(postPageResponse.isLast()) // 현재 읽은 페이지가 마지막 페이지
            lastPage = true;

        if(postPageResponse.getPageNo() == 0){
            postListAdapter.submitList(postPageResponse.getPostList());
            return;
        } // 첫 페이지

        List<PostDTO> list = new ArrayList<>(postListAdapter.getCurrentList().size() + postPageResponse.getPostList().size());
        list.addAll(postListAdapter.getCurrentList());
        list.addAll(postPageResponse.getPostList());
        postListAdapter.submitList(list);
    }
}
