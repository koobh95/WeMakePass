package com.example.wemakepass.view.home.interestJm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.example.wemakepass.R;
import com.example.wemakepass.adapter.InterestJmListAdapter;
import com.example.wemakepass.adapter.JmSearchListAdapter;
import com.example.wemakepass.data.model.data.InterestJmModel;
import com.example.wemakepass.data.model.dto.JmInfoDTO;
import com.example.wemakepass.databinding.ActivityInterestJmSearchBinding;
import com.example.wemakepass.util.ExpandAnimationUtils;
import com.example.wemakepass.util.MessageUtils;
import com.example.wemakepass.view.main.MainActivity;

/**
 * 관심 종목을 검색/추가/삭제할 수 있는 Activity
 *
 * @author BH-Ku
 * @since 2023-11-08
 */
public class InterestJmSearchActivity extends AppCompatActivity {
    private ActivityInterestJmSearchBinding binding;
    private InterestJmSearchViewModel viewModel;

    private JmSearchListAdapter jmSearchListAdapter;
    private InterestJmListAdapter interestJmListAdapter;

    private boolean interestJmEdited = false; // 관심 종목의 변경 여부

    private final String TAG = "TAG_InterestJmSearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_interest_jm_search);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(InterestJmSearchViewModel.class);
        binding.setViewModel(viewModel);

        initToolbar();
        initEventListener();
        initObserver();
        initJmSearchRecyclerView();
        initInterestJmRecyclerView();
    }

    /**
     *  뒤로 가기를 눌러 화면을 종료했을 때 관심 종목 리스트의 변경 여부를 이전 Activity(MainActivity)에
     * 전달한다. 세세하게 변경 여부를 따지지 않고 단순히 관심 종목 리스트에서 삭제 혹은 추가가 일어났는지에 대한
     * 여부만 전달한다.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(MainActivity.ARG_INTEREST_JM_EDITED, interestJmEdited);
        setResult(MainActivity.CODE_INTEREST_JM_SEARCH_ACTIVITY, intent);
        super.onBackPressed();
    }

    /**
     * Toolbar를 초기화하는 메서드.
     */
    private void initToolbar() {
        binding.activityInterestJmSearchToolbar.setNavigationOnClickListener(v ->
                onBackPressed());
    }

    /**
     * LiveData와 직접적인 연관이 없는 View에 대한 이벤트를 설정하는 메서드.
     */
    private void initEventListener() {
        /**
         *  검색창(TextInputEditText)의 입력 값을 감시하는 리스너를 구현한다. 입력되는 값 중 "완료"가 선택되면
         * 검색을 실행한다.
         */
        binding.activityInterestJmSearchSearchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE)
                viewModel.search();
            return true;
        });
    }

    /**
     * LiveData에 대한 옵저빙을 설정한다.
     */
    private void initObserver() {
        viewModel.getSystemMessageLiveData().observe(this, systemMessage ->
                MessageUtils.showToast(this, systemMessage));

        viewModel.getNetworkErrorLiveData().observe(this, errorResponse ->
                MessageUtils.showToast(this, errorResponse.getMessage()));

        /**
         * - 관심 종목이 초기 로드되거나 추가/삭제되어 갱신되는 경우를 감시한다.
         * - 변경된 값을 가진 리스트를 submit하여 갱신하는 동시에 추가된 아이템을 볼 수 있도록 스크롤을 처음으로
         *  이동시킨다.
         * - Load/Update된 List의 아이템이 없을 경우 RectclerView의 Visibility를 확인, Visible 상태일 경우
         *  Gone 상태로 변경한다. 반대로 아이템이 하나라도 있을 경우 Visibility를 확인, Gone일 경우 Visible
         *  상태로 변경한다.
         */
        viewModel.getInterestJmListLiveData().observe(this, list -> {
            RecyclerView recyclerView = binding.activityInterestJmSearchInterestJmRecyclerView;
            AppCompatTextView statusTextView = binding.activityInterestJmSearchStatusTextView;
            interestJmListAdapter.submitList(list);
            recyclerView.smoothScrollToPosition(list.size());
            statusTextView.setText("관심 종목(" + list.size() + "/5)");

            if(list.size() == 0) {
                if(recyclerView.getVisibility() == View.VISIBLE)
                    ExpandAnimationUtils.collapse(recyclerView);
            } else {
                if(recyclerView.getVisibility() == View.GONE)
                    ExpandAnimationUtils.expand(recyclerView);
            }
        });

        /**
         * - 검색 수행 시 결과를 감시한다.
         * - 검색 수행 결과가 없을 경우 토스트로 메시지를 띄어준다.
         * - 검색 수행 결과가 1개라도 있을 경우 submitList로 RecyclerView를 갱신한다.
         */
        viewModel.getJmInfoListLiveData().observe(this, list -> {
            if(list == null || list.size() == 0){
                MessageUtils.showToast(this, "검색 결과가 없습니다.");
                return;
            }
            jmSearchListAdapter.submitList(list);
        });
    }

    /**
     * - 관심 종목 RecyclerView를 초기화한다.
     * - 횡스크롤이다.
     * - 가장 최근 추가한 종목을 우선 순위로 보여주기 위해서 역순으로 출력한다.
     */
    private void initInterestJmRecyclerView() {
        interestJmListAdapter = new InterestJmListAdapter();
        interestJmListAdapter.setOnRemoveClickListener(position ->{
            viewModel.removeInterestJmItem(position);
            interestJmEdited = true; // 관심 종목 리스트 갱신 여부
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, true);
        layoutManager.setStackFromEnd(true);
        binding.activityInterestJmSearchInterestJmRecyclerView.setLayoutManager(layoutManager);
        binding.activityInterestJmSearchInterestJmRecyclerView.setAdapter(interestJmListAdapter);
    }

    /**
     * 검색 결과 RecyclerView를 초기화한다.
     */
    private void initJmSearchRecyclerView() {
        jmSearchListAdapter = new JmSearchListAdapter();
        jmSearchListAdapter.setOnItemClickListener(position -> {
            final JmInfoDTO jmInfoDTO = jmSearchListAdapter.getCurrentList().get(position);
            InterestJmModel interestJmModel = new InterestJmModel(jmInfoDTO.getJmCode(), jmInfoDTO.getJmName());
            viewModel.addInterestJmItem(interestJmModel);
            interestJmEdited = true; // 관심 종목 리스트 갱신 여부
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        binding.activityInterestJmSearchRecyclerView.setLayoutManager(layoutManager);
        binding.activityInterestJmSearchRecyclerView.setAdapter(jmSearchListAdapter);
    }
}