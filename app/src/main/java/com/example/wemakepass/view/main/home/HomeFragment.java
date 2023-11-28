package com.example.wemakepass.view.main.home;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wemakepass.R;
import com.example.wemakepass.adapter.ExamOrganListAdapter;
import com.example.wemakepass.adapter.InterestJmSchedListAdapter;
import com.example.wemakepass.adapter.divider.DividerWithoutLast;
import com.example.wemakepass.data.model.vo.ExamOrganVO;
import com.example.wemakepass.databinding.FragmentHomeBinding;
import com.example.wemakepass.view.main.home.interestJm.InterestJmSearchActivity;
import com.example.wemakepass.view.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity의 BottomNavigation 메뉴에서 홈이 선택될 경우 표시되는 Fragment로 다음과 같은 데이터를
 * 표시한다.
 * - 관심 등록한 종목의 시험 일정을 API로 조회하여 리스트로 보여준다.
 *
 * @author BH-Ku
 * @since 2023-11-09
 */
public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    private InterestJmSchedListAdapter interestJmSchedListAdapter;

    private final int VIEW_TYPE_CONTENT_LOADING_PROGRESS_BAR = 0;
    private final int VIEW_TYPE_TEXT_VIEW = 1;
    private final int VIEW_TYPE_RECYCLER_VIEW = 2;
    private final String TAG = "TAG_HomeFragment";

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initEventListener();
        initObserver();
        initInterestJmSchedRecyclerView();
        initExamOrganRecyclerView();
    }

    /**
     * LiveData와 직접적인 연관이 없는 View에 대한 이벤트를 설정하는 메서드.
     */
    private void initEventListener() {
        /**
         *  관심 종목 검색 화면(InterestJmSearchActivity)에서 변경 사항이 있었는지에 대해 확인하기 위해서
         * startActivity가 아닌 MainActivity에서 구현된 ActivityResultLauncher를 사용하여 Activity를
         * 실행한다.
         */
        binding.fragmentHomeInterestJmSearchButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), InterestJmSearchActivity.class);
            ((MainActivity) requireActivity()).getActivityResultLauncher().launch(intent);
        });
    }

    /**
     * LiveData에 대한 옵저빙을 설정한다.
     */
    private void initObserver() {
        /**
         * SharedPreferences에 저장되어 있는 관심 종목 리스트를 불러 온다.
         */
        viewModel.getInterestJmListLiveData().observe(this, list -> {
            if (list.size() == 0) {
                changeVisibilityJmSchedView(VIEW_TYPE_TEXT_VIEW);
            } else {
                viewModel.loadInterestJmSchedule();
            }
        });

        /**
         * - 관심 등록한 종목에 대한 일정 조회 및 선별이 완료되는 것을 옵저빙한다. 각 종목에 대한 일정 조회는
         *  병렬적으로 이루어지므로 관심 종목 수와 로드된 일정 수를 비교하여 일치할 경우에만 추가 작업을 진행한다.
         * - 모든 일정이 로드된 경우 일정을 정렬한 뒤 ContentLoadingProgressBar의 VISIBILITY를 조정하여
         *  숨기고 RecyclerView를 VISIBLE 상태로 변경 후 로드된 데이터들을 제출하여 업데이트한다.
         *
         */
        viewModel.getInterestJmSchedListLiveData().observe(this, interestJmSchedList -> {
            if (viewModel.getInterestJmListLiveData().getValue().size() != interestJmSchedList.size())
                return; // 아직 모든 일정이 로드되지 않음.
            viewModel.sortInterestJmSchedList(interestJmSchedList);
            changeVisibilityJmSchedView(VIEW_TYPE_RECYCLER_VIEW);
            interestJmSchedListAdapter.submitList(interestJmSchedList);
        });
    }

    /**
     * 관심 종목 일정 RecyclerView를 초기화한다.
     */
    private void initInterestJmSchedRecyclerView() {
        interestJmSchedListAdapter = new InterestJmSchedListAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false);
        binding.fragmentHomeScheduleRecyclerView.setLayoutManager(layoutManager);
        binding.fragmentHomeScheduleRecyclerView.suppressLayout(false);
        binding.fragmentHomeScheduleRecyclerView.addItemDecoration(
                new DividerWithoutLast(requireContext()));
        binding.fragmentHomeScheduleRecyclerView.setAdapter(interestJmSchedListAdapter);
    }

    /**
     * - 시험 주관 기관 리스트를 표시하는 RecyclerView를 초기화한다.
     * - 이 RecyclerView에 사용되는 데이터는 기존에 arrays.xml에 준비된 데이터를 사용하여 만든다.
     */
    private void initExamOrganRecyclerView() {
        TypedArray iconArr = getResources().obtainTypedArray(R.array.exam_organ_icon_array);
        String[] nameArr = getResources().getStringArray(R.array.exam_organ_title_array);
        String[] urlArr = getResources().getStringArray(R.array.exam_organ_url_array);
        List<ExamOrganVO> list = new ArrayList<>(nameArr.length);

        for (int i = 0; i < nameArr.length; i++)
            list.add(new ExamOrganVO(iconArr.getDrawable(i), nameArr[i], urlArr[i]));

        ExamOrganListAdapter examOrganListAdapter = new ExamOrganListAdapter(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false);
        binding.fragmentHomeExamOrganRecyclerView.setLayoutManager(layoutManager);
        binding.fragmentHomeExamOrganRecyclerView.setAdapter(examOrganListAdapter);
    }

    /**
     * - 관심 종목 일정 관련 View의 Visibility를 조정하는 메서드다. 초기에는 ContentLoadingProgressBar가
     * Visibility 상태로 있다가 관심 종목 로딩을 끝냈을 때 가지고 있는 데이터가 없을 경우 TextView를 VISIBLE
     * 상태로 하여 상태 메시지를 띄운다. 반대로 데이터가 있을 경우 일정 로딩을 시작하게 되는데 일정 로딩이 끝날
     * 경우 RecyclerView를 VISIBLE 상태로 변경한다.
     * - Parameter로 들어오는 viewType은 멤버 변수로 선언된 상수들을 사용한다.
     * - Parameter의 viewType과 일치하는 View는 VISIBLE, 일치하지 않는 View는 GONE 상태로 변경된다.
     *
     * @param viewType ContentLoadingProgressBar(0), TextView(1), RecyclerView(2)
     */
    private void changeVisibilityJmSchedView(int viewType) {
        binding.fragmentHomeScheduleLoadingProgressBar.getRoot().setVisibility(
                viewType == VIEW_TYPE_CONTENT_LOADING_PROGRESS_BAR ? View.VISIBLE : View.GONE);
        binding.fragmentHomeScheduleLoadingStatusTextView.setVisibility(
                viewType == VIEW_TYPE_TEXT_VIEW ? View.VISIBLE : View.GONE);
        binding.fragmentHomeScheduleRecyclerView.setVisibility(
                viewType == VIEW_TYPE_RECYCLER_VIEW ? View.VISIBLE : View.GONE);
    }

    public void updateInterestJmList() {
        viewModel.interestJmListUpdate();
    }
}