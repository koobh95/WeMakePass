package com.example.wemakepass.view.main.community;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wemakepass.R;
import com.example.wemakepass.adapter.VisitedBoardLogPageAdapter;
import com.example.wemakepass.adapter.divider.CircleTypePagerIndicatorDecoration;
import com.example.wemakepass.data.model.dto.BoardDTO;
import com.example.wemakepass.databinding.FragmentCommunityBinding;
import com.example.wemakepass.listener.OnVisitedBoardItemClickListener;
import com.example.wemakepass.util.DialogUtils;
import com.example.wemakepass.view.board.BoardActivity;
import com.example.wemakepass.view.board.search.BoardSearchActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * - MainActivity의 BottomNavigation 메뉴에서 게시판이 선택될 경우 전환되는 Fragment다.
 * - 게시판 검색 화면을 실행하거나 이전에 방문했던 게시판 목록을 볼 수 있으며 이 목록을 통해 즉시 해당 게시판으로
 *  이동할 수 있다.
 *
 * @author BH-Ku
 * @since 2023-12-02
 */
public class CommunityFragment extends Fragment {
    private FragmentCommunityBinding binding;
    private CommunityViewModel viewModel;

    private VisitedBoardLogPageAdapter visitedBoardLogPageAdapter;

    private final String TAG = "TAG_CommunityFragment";

    public static CommunityFragment newInstance() {
        return new CommunityFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_community, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(CommunityViewModel.class);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBoarLogRecyclerView();
        initEventListener();
        iniObserver();
    }

    /**
     *  이 Fragment가 실행되었을 때 방문한 게시판 목록을 로딩하는데에도 쓰이지만 특정 게시판을 검색해서 게시판을
     * 실행했을 경우 로그에 추가되는데 다시 이 화면으로 돌아왔을 때 최신 데이터를 유지하려는 의도가 더 크다.
     */
    @Override
    public void onResume() {
        super.onResume();
        viewModel.reloadingVisitedBoarLogList();
    }

    /**
     * 방문 기록 RecyclerView를 초기화한다.
     */
    private void initBoarLogRecyclerView() {
        RecyclerView recyclerView = binding.fragmentCommunityVisitedBoardRecyclerView;
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper(); // 페이징을 위한 객체
        pagerSnapHelper.attachToRecyclerView(recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false);
        visitedBoardLogPageAdapter = new VisitedBoardLogPageAdapter();
        visitedBoardLogPageAdapter.setOnBoardItemClickListener(new OnVisitedBoardItemClickListener() {
            /**
             * 특정 아이템을 클릭할 경우 해당 게시판을 실행한다.
             *
             * @param boardDTO 선택된 게시판 정보
             */
            @Override
            public void onClick(BoardDTO boardDTO) {
                Intent intent = new Intent(requireActivity(), BoardActivity.class);
                intent.putExtra(BoardActivity.ARG_SELECTED_BOARD, boardDTO);
                startActivity(intent);
            }

            /**
             * 특정 아이템을 길게 클릭할 경우 로그를 삭제할 것인지 묻는 Dialog를 출력한다.
             *
             * @param boardDTO 선택된 게시판 정보
             */
            @Override
            public void onLongClick(BoardDTO boardDTO) {
                DialogUtils.showConfirmDialog(requireContext(),
                        "최근 방문 목록에서 삭제하시겠습니까?",
                        dialog -> {
                            viewModel.removeVisitedBoardLog(boardDTO);
                            dialog.dismiss();
                        });
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(visitedBoardLogPageAdapter);
        recyclerView.addItemDecoration(new CircleTypePagerIndicatorDecoration(requireContext()));
    }


    /**
     * LiveData와 직접적인 연관이 없는 View에 대한 이벤트를 설정하는 메서드.
     */
    private void initEventListener() {
        // 게시판 검색
        binding.fragmentCommunitySearchBarLayout.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), BoardSearchActivity.class)));
    }

    /**
     * LiveData에 대한 옵저빙을 설정한다.
     */
    private void iniObserver() {
        /**
         *  방문 게시판 목록이 조회되는 것을 관찰한다. 읽어 온 데이터가 없을 경우 관련 TextView를, 데이터가 있을
         * 경우 RecyclerView를 보여준다.
         */
        viewModel.getVisitedBoardListLiveData().observe(this, list -> {
            if(list == null || list.size() == 0) {
                binding.fragmentCommunityVisitedBoardRecyclerView.setVisibility(View.GONE);
                binding.fragmentCommunityVisitedBoardAlertMsgTextView.setVisibility(View.VISIBLE);
            }else {
                binding.fragmentCommunityVisitedBoardRecyclerView.setVisibility(View.VISIBLE);
                binding.fragmentCommunityVisitedBoardAlertMsgTextView.setVisibility(View.GONE);
                updateVisitedBoardRecyclerView(list);
            }
        });
    }

    /**
     * - 검색 기록이 1개 이상 존재할 경우 이 메서드가 호출되며 방문한 게시판 리스트를 초기화 혹은 갱신한다.
     * - 기록을 보여줄 때 아이템을 10개 단위로 끊어서 페이지로 나눠 보여준다. 페이지 하나는 RecylcerView 하나로
     *  구성되어 있기 때문에 한 페이지(RecyclerView)에 10개 이하의 데이터 수를 전달하기 위해서 데이터를 끊어
     *  낸다. 그리고 분류가 끝난 리스트를 사용하여 목록을 갱신한다.
     * - 각 페이지의 아이템 수가 홀수 개 일 경우 우측에는 아이템이 비어 다소 어색한 느낌을 준다. 따라서 한 페이지에
     *  들어갈 데이터를 담은 리스트가 홀수 개 일 경우 마지막에 null을 삽입하여 Item View만 생성하도록 한다.
     *
     * @param list
     */
    private void updateVisitedBoardRecyclerView(List<BoardDTO> list){
        int size = list.size() % 10 != 0 ? (list.size()/10) + 1 : list.size()/10;
        List<List<BoardDTO>> pageList = new ArrayList<>(size);

        // i = 전체 index, k = 페이지 내부 아이템 count
        for(int i = list.size()-1, k = 0; i >= 0; i--) {
            if(k++ % 10 == 0)
                pageList.add(new ArrayList<>(10));
            pageList.get(pageList.size()-1).add(list.get(i));
            if(k == 10)
                k = 0;
        }

        if(pageList.get(pageList.size()-1).size() % 2 == 1)
            pageList.get(pageList.size()-1).add(null);

        visitedBoardLogPageAdapter.setPageList(pageList);
        visitedBoardLogPageAdapter.notifyDataSetChanged();
    }
}