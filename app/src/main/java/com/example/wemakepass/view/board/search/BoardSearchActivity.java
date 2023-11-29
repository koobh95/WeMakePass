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
import com.example.wemakepass.view.board.main.BoardMainFragment;

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

    private final int LAYOUT_LOG = 0;
    private final int LAYOUT_SEARCH_RESULT = 1;
    public static final String ARG_SELECTED_BOARD = "selectedBoardDTO";
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

    private void initObserver() {
        viewModel.getSystemMessageLiveData().observe(this, systemMessage ->
                MessageUtils.showToast(this, systemMessage));

        viewModel.getNetworkErrorLiveData().observe(this, errorResponse ->
                MessageUtils.showToast(this, errorResponse.getMessage()));

        viewModel.getKeywordLiveData().observe(this, keyword -> {
            if(TextUtils.isEmpty(keyword))
                changeLayoutVisibility(LAYOUT_LOG);
        });

        viewModel.getSearchLogListLiveData().observe(this, list -> {
            if(list != null) //dddddddddddddddddddddddddddddddd
                searchLogListAdapter.submitList(list);
        });

        viewModel.getBoardListLiveData().observe(this, list ->{
            boardSearchListAdapter.submitList(list);
            if(list.size() == 0)
                MessageUtils.showToast(this, "검색 결과가 없습니다.");
            changeLayoutVisibility(LAYOUT_SEARCH_RESULT);
        });
    }

    private void initEventListener(){
        binding.activityBoardSearchBarEditText.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH)
                viewModel.search();
            return true;
        });

        binding.activityBoardSearchLogDeleteAllButton.setOnClickListener(v -> {
            DialogUtils.showConfirmDialog(this,
                    "모든 로그를 삭제하시겠습니까?",
                    dialog -> {
                        viewModel.deleteLogAll();
                        dialog.dismiss();
                    });
        });

        binding.activityBoardSearchResultClearButton.setOnClickListener(v ->
                changeLayoutVisibility(LAYOUT_LOG));
    }

    private void initToolbar() {
        binding.activityBoardSearchToolbar.setNavigationOnClickListener(v ->
                finish());
    }

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

    private void initSearchLogRecyclerView() {
        RecyclerView recyclerView = binding.activityBoardSearchResultRecyclerView;
        boardSearchListAdapter = new BoardSearchListAdapter();
        boardSearchListAdapter.setOnItemClickListener(position ->{
            final BoardDTO selectedBoardDTO = boardSearchListAdapter.getCurrentList().get(position);
            Intent intent = new Intent(this, BoardActivity.class);
            intent.putExtra(ARG_SELECTED_BOARD, selectedBoardDTO);
            startActivity(intent);
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

        if(visibleLayout == LAYOUT_LOG) {
            logLayout.setVisibility(View.VISIBLE);
            searchLayout.setVisibility(View.GONE);
        } else if (visibleLayout == LAYOUT_SEARCH_RESULT){
            logLayout.setVisibility(View.GONE);
            searchLayout.setVisibility(View.VISIBLE);
        }
    }
}
