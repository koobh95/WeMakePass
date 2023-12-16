package com.example.wemakepass.view.board.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.example.wemakepass.R;
import com.example.wemakepass.adapter.BoardSearchListAdapter;
import com.example.wemakepass.adapter.SearchLogListAdapter;
import com.example.wemakepass.data.model.dto.BoardDTO;
import com.example.wemakepass.databinding.ActivityBoardSearchBinding;
import com.example.wemakepass.util.DialogUtils;
import com.example.wemakepass.util.MessageUtils;
import com.example.wemakepass.view.board.BoardActivity;

/**
 * 게시판을 검색하는 환경을 제공되는 Activity.
 *
 * @author BH-Ku
 * @since 2023-11-29
 */
public class BoardSearchActivity extends AppCompatActivity {
    private ActivityBoardSearchBinding binding;
    private BoardSearchViewModel viewModel;

    private SearchLogListAdapter searchLogListAdapter;
    private BoardSearchListAdapter boardSearchListAdapter;

    private final int LOG_LAYOUT_VIEW = 0;
    private final int SEARCH_RESULT_LAYOUT_VIEW = 1;
    private final String TAG = "TAG_BoardSearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_search);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(BoardSearchViewModel.class);
        binding.setViewModel(viewModel);

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
                MessageUtils.showToast(this, systemMessage));

        viewModel.getNetworkErrorLiveData().observe(this, errorResponse ->
                MessageUtils.showToast(this, errorResponse.getMessage()));

        viewModel.getKeywordLiveData().observe(this, keyword -> {
            if(TextUtils.isEmpty(keyword))
                changeLayoutVisibility(LOG_LAYOUT_VIEW);
        });

        viewModel.getSearchLogListLiveData().observe(this, list -> {
            if(list != null)
                searchLogListAdapter.submitList(list);
        });

        viewModel.getBoardListLiveData().observe(this, list ->{
            boardSearchListAdapter.submitList(list);
            if(list.size() == 0)
                MessageUtils.showToast(this, "검색 결과가 없습니다.");
            changeLayoutVisibility(SEARCH_RESULT_LAYOUT_VIEW);
        });
    }

    /**
     * LiveData와 직접적인 연관이 없는 View에 대한 이벤트를 설정하는 메서드.
     */
    private void initEventListener(){
        binding.activityBoardSearchBarEditText.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH)
                viewModel.search();
            return true;
        });

        // 검색 기록 모두 삭제 버튼
        binding.activityBoardSearchLogDeleteAllButton.setOnClickListener(v -> {
            DialogUtils.showConfirmDialog(this,
                    "모든 로그를 삭제하시겠습니까?",
                    dialog -> {
                        viewModel.deleteLogAll();
                        dialog.dismiss();
                    });
        });

        // 검색 결과를 보여주고 있는 상태에서 검색 기록 목록으로 화면 전환
        binding.activityBoardSearchResultClearButton.setOnClickListener(v ->
                changeLayoutVisibility(LOG_LAYOUT_VIEW));
    }

    /**
     * TabLayout에 대한 초기화를 수행한다.
     */
    private void initToolbar() {
        binding.activityBoardSearchToolbar.setNavigationOnClickListener(v ->
                finish());
    }

    /**
     * 검색 결과 RecyclerView를 초기화한다.
     */
    private void initSearchResultRecyclerView() {
        RecyclerView recyclerView = binding.activityBoardSearchLogRecyclerView;
        searchLogListAdapter = new SearchLogListAdapter();
        searchLogListAdapter.setOnItemClickListener(position -> {
            viewModel.getKeywordLiveData().setValue(
                    searchLogListAdapter.getCurrentList().get(position));
            viewModel.search();
        });
        searchLogListAdapter.setOnRemoveButtonClickListener(position ->
                viewModel.deleteLog(position));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(searchLogListAdapter);
    }

    /**
     * 검색 기록 RecyclerView를 초기화한다.
     */
    private void initSearchLogRecyclerView() {
        RecyclerView recyclerView = binding.activityBoardSearchResultRecyclerView;
        boardSearchListAdapter = new BoardSearchListAdapter();
        boardSearchListAdapter.setOnItemClickListener(position ->{
            final BoardDTO selectedBoardDTO = boardSearchListAdapter.getCurrentList().get(position);
            Intent intent = new Intent(this, BoardActivity.class);
            intent.putExtra(BoardActivity.ARG_SELECTED_BOARD, selectedBoardDTO);
            startActivity(intent);
            viewModel.addVisitedBoardLog(selectedBoardDTO);
            finish();
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(boardSearchListAdapter);
    }


    /**
     * 검색 기록 레이아웃과 검색 결과 레이아웃 중 화면에 보여질 레이아웃을 결정한다.
     *
     * @param visibleLayout 화면에 보여줄 레이아웃
     */
    private void changeLayoutVisibility(int visibleLayout) {
        ConstraintLayout logLayout = binding.activityBoardSearchLogLayout;
        ConstraintLayout searchLayout = binding.activityBoardSearchResultLayout;

        if(visibleLayout == LOG_LAYOUT_VIEW) {
            logLayout.setVisibility(View.VISIBLE);
            searchLayout.setVisibility(View.GONE);
        } else if (visibleLayout == SEARCH_RESULT_LAYOUT_VIEW){
            logLayout.setVisibility(View.GONE);
            searchLayout.setVisibility(View.VISIBLE);
        }
    }
}
