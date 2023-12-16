package com.example.wemakepass.view.exam.guide;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wemakepass.R;
import com.example.wemakepass.data.model.dto.ExamInfoDTO;
import com.example.wemakepass.data.model.dto.JmInfoDTO;
import com.example.wemakepass.databinding.FragmentExamGuideBinding;
import com.example.wemakepass.util.DialogUtils;
import com.example.wemakepass.util.ExpandAnimationUtils;
import com.example.wemakepass.view.exam.ExamActivity;
import com.example.wemakepass.view.exam.doc.ExamDocFragment;
import com.example.wemakepass.view.exam.select.jmSearch.JmSearchFragment;

import java.util.List;

/**
 *  ExamSelectFragment에서 응시할 종목, 시험을 선택했을 때 이 Fragment가 실행된다. 이 Fragment는 응시할
 * 시험에 대한 간단한 요약 정보, 주의사항 등을 출력한다.
 *
 * @author BH-Ku
 * @since 2023-11-21
 */
public class ExamGuideFragment extends Fragment {
    private FragmentExamGuideBinding binding;
    private ExamGuideViewModel viewModel;

    // 시험 선택 화면에서 선택된 종목, 시험 정보
    private JmInfoDTO jmInfoDTO;
    private ExamInfoDTO examInfoDTO;

    public static ExamGuideFragment newInstance(@NonNull JmInfoDTO selectedJmInfo,
                                                @NonNull ExamInfoDTO selectedExamInfo) {
        ExamGuideFragment fragment = new ExamGuideFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(JmSearchFragment.ARG_SELECTED_JM_INFO, selectedJmInfo);
        bundle.putSerializable(ExamActivity.ARG_SELECTED_EXAM_INFO, selectedExamInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        assert bundle != null;
        jmInfoDTO = (JmInfoDTO) bundle.getSerializable(JmSearchFragment.ARG_SELECTED_JM_INFO);
        examInfoDTO = (ExamInfoDTO) bundle.getSerializable(ExamActivity.ARG_SELECTED_EXAM_INFO);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_exam_guide, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(ExamGuideViewModel.class);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar();
        initView();
        initEventListener();
        initOnBackPressedListener();
        initObserver();
    }

    /**
     * - 시험 선택 화면(ExamSelectFragment)에서 전달받은 데이터를 기반으로 레이아웃에 초기 데이터를 세팅한다.
     * - 시험 형식이 실기가 아니라면 나머지(필기, 1차, 2차, 3차)는 시험 항목이 과목으로 나누어져 있기 때문에
     *  과목 목록을 요청한다.
     */
    @SuppressLint("SetTextI18n")
    private void initView() {
        binding.fragmentExamGuideMainTitleTextView.setText(jmInfoDTO.getJmName());
        binding.fragmentExamGuideSubTitleTextView.setText(examInfoDTO.getImplYear() +
                "년 제 " + examInfoDTO.getImplSeq() + "회");
        binding.fragmentExamGuideNumOfQuestionContentTextView
                .setText(examInfoDTO.getNumOfQuestion() + "");
        binding.fragmentExamGuideTimeLimitContentTextView
                .setText(examInfoDTO.getTimeLimit() + "분");
        binding.fragmentExamGuideFormatContentTextView.setText(examInfoDTO.getExamFormat());
        if (!examInfoDTO.getExamFormat().equals("실기"))
            viewModel.loadSubjectList(examInfoDTO.getExamId());
    }

    /**
     * LiveData와 직접적인 연관이 없는 View에 대한 이벤트를 설정하는 메서드.
     */
    private void initEventListener() {
        binding.fragmentExamGuideStartButton.setOnClickListener(v ->
                startExamFragment());
    }

    /**
     * 기기의 뒤로 가기 버튼이 클릭되었을 때 이벤트를 처리한다.
     */
    private void initOnBackPressedListener() {
        requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(),
                        new OnBackPressedCallback(true) {
                            @Override
                            public void handleOnBackPressed() {
                                showFinishConfirmDialog();
                            }
                        });
    }

    /**
     * LiveData에 대한 옵저빙을 설정한다.
     */
    private void initObserver() {
        viewModel.getSubjectListLiveData().observe(this, this::initSubjectLayout);
    }

    /**
     * Toolbar를 초기화하는 메서드.
     */
    private void initToolbar() {
        binding.fragmentExamGuideToolbar.setNavigationOnClickListener(v ->
                showFinishConfirmDialog());
    }

    /**
     * - 과목 목록을 서버에서 조회했을 경우 이 메서드가 호출된다.
     * - 사용자가 보기 편하도록 StringBuilder를 통해 데이터를 가공한 뒤 관련 TextView에 세팅한다. 그리고
     *  기본적으로 Visibility가 GONE 상태인 과목 Layout을 Visible 상태로 변경해주는데 자연스러움을 더하기
     *  위해서 Animation을 사용한다.
     *
     * @param subjectList 과목 목록
     */
    private void initSubjectLayout(List<String> subjectList) {
        if (subjectList == null || subjectList.size() == 0)
            return;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < subjectList.size(); i++) {
            sb.append(i + 1).append("과목 ").append(subjectList.get(i));
            if (i + 1 != subjectList.size())
                sb.append('\n');
        }

        binding.fragmentExamGuideSubjectContentTextView.setText(sb.toString());
        ExpandAnimationUtils.expand(binding.fragmentExamGuideSubjectLayout);
    }

    /**
     *  기기의 뒤로 가기 버튼, Toolbar의 NavigationButton이 선택되었을 때 호출되어 종료 의사를 묻는
     * 다이얼로그를 출력한다.
     */
    private void showFinishConfirmDialog() {
        DialogUtils.showConfirmDialog(
                requireContext(),
                "시험 선택 화면으로 돌아가시겠습니까?",
                dialog -> {
                    dialog.dismiss();
                    requireActivity().getSupportFragmentManager().popBackStack();
                });
    }

    /**
     * 시험 형식에 따라 다른 Fragment를 실행한다.
     *
     * 필기, 1차, 2차, 3차 -> ExamDocFragment
     *
     * @since 2023-11-28 수정 → 실기는 채점 방식이 난해하여 잠시 보류.
     */
    private void startExamFragment(){
        ExamActivity examActivity = (ExamActivity)requireActivity();
        if(examInfoDTO.getExamFormat().equals("실기"))
            //examActivity.replaceFragment(ExamPracFragment.newInstance(jmInfoDTO, examInfoDTO));
            ; // 준비 중, 실기 데이터를 DB에서 지웠기 때문에 분기될 일이 없음. 추후 수정을 위해 코드는 남겨둠.
        else
            examActivity.replaceFragment(ExamDocFragment.newInstance(jmInfoDTO, examInfoDTO));
    }
}
