package com.example.wemakepass.view.exam.doc;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.wemakepass.R;
import com.example.wemakepass.adapter.ExamDocAnswerListAdapter;
import com.example.wemakepass.adapter.ExamDocOptionListAdapter;
import com.example.wemakepass.adapter.divider.DividerWithoutLast;
import com.example.wemakepass.data.enums.ErrorCode;
import com.example.wemakepass.data.model.dto.ExamDocAnswerDTO;
import com.example.wemakepass.data.model.dto.ExamDocQuestionDTO;
import com.example.wemakepass.data.model.dto.ExamInfoDTO;
import com.example.wemakepass.data.model.dto.JmDTO;
import com.example.wemakepass.databinding.FragmentExamDocBinding;
import com.example.wemakepass.network.util.FileRequestURLBuilder;
import com.example.wemakepass.task.exam.ExamDocScoringTask;
import com.example.wemakepass.util.DialogUtils;
import com.example.wemakepass.util.MessageUtils;
import com.example.wemakepass.view.exam.ExamActivity;
import com.example.wemakepass.view.exam.select.jmSearch.JmSearchFragment;
import com.example.wemakepass.view.exam.result.ExamResultViewerDialog;

/**
 * - 객관식 시험 환경을 제공하는 Fragment
 * - 시험 선택 화면에서 시험 형식이 객관식(필기 시험 및 1차, 2차, 3차)인 시험을 선택했을 경우 실행된다.
 *
 * @author BH-Ku
 * @since 2023-11-22
 */
public class ExamDocFragment extends Fragment {
    private FragmentExamDocBinding binding;
    private ExamDocViewModel viewModel;

    private ExamResultViewerDialog examResultViewerDialog;

    private ExamDocOptionListAdapter examDocOptionListAdapter;
    private ExamDocAnswerListAdapter examDocAnswerListAdapter;

    private JmDTO jmDTO; // 선택된 종목 데이터
    private ExamInfoDTO examInfoDTO; // 선택된 시험 데이터
    private int examCursor;

    private final int DRAWER_GRAVITY = GravityCompat.START; // DrawerLayout의 위치
    private final String TAG = "TAG_ExamDocFragment";

    public static ExamDocFragment newInstance(JmDTO jmDTO, ExamInfoDTO examInfoDTO) {
        ExamDocFragment fragment = new ExamDocFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(JmSearchFragment.ARG_SELECTED_JM_INFO, jmDTO);
        bundle.putSerializable(ExamActivity.ARG_SELECTED_EXAM_INFO, examInfoDTO);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        assert bundle != null;
        jmDTO = (JmDTO) bundle.getSerializable(JmSearchFragment.ARG_SELECTED_JM_INFO);
        examInfoDTO = (ExamInfoDTO) bundle.getSerializable(ExamActivity.ARG_SELECTED_EXAM_INFO);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_exam_doc, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(ExamDocViewModel.class);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.loadExamData(examInfoDTO.getExamId()); // 문제 로딩
        viewModel.initAnswerList(examInfoDTO.getNumOfQuestion()); // 사용자 답안 리스트 초기화
        initBackPressedListener();
        initEventListener();
        initObserver();
        initToolbar();
        initDrawerLayout();
        initQuestionOptionRecyclerView();
        initAnswerRecyclerView();
    }

    /**
     * LiveData에 대한 옵저빙을 설정한다.
     */
    private void initObserver() {
        /**
         *  통신에 실패했을 때 문제 목록을 읽어왔는지 확인한다. 읽어온 데이터가 없는 경우 이 통신 실패는 문제를
         * 읽어오다가 발생한 통신 오류로 간주하고 실패했다는 메시지를 담은 다이얼로그를 실행한 뒤 화면을 종료한다.
         */
        viewModel.getNetworkErrorLiveData().observe(this, errorResponse -> {
            if (errorResponse.getCode().equals(ErrorCode.CONNECTION_FAILED.name()))
                if (viewModel.getExamQuestionListLiveData().getValue() == null)
                    startLoadingFailedDialog();
        });

        /**
         * - 시험 문제를 읽어오는 것을 관찰한다.
         * - 시험 문제를 읽어오지 못했거나 읽어온 데이터가 없는 경우 조회에 실패했다고 간주하고 로딩 실패
         *  다이얼로그를 실행시킨 뒤 화면을 종료한다.
         * - 시험 문제를 성공적으로 읽어왔을 경우 화면을 덮은 다이얼로그의 Visibility를 조정하여 숨기고
         *  타이머를 실행한다. 그리고 첫번째 문제를 화면에 업데이트하기 우해 적절한 메서드를 호출한다.
         */
        viewModel.getExamQuestionListLiveData().observe(this, examQuestionList -> {
            if (examQuestionList == null || examQuestionList.size() == 0) {
                startLoadingFailedDialog();
                return;
            }
            binding.fragmentExamDocContentLoadingProgressBar.getRoot().setVisibility(View.GONE);
            examCursor = 0;
            updateQuestionViews();
            viewModel.startTimer(examInfoDTO.getTimeLimit());
        });

        /**
         *  타이머가 중지되는 것을 관찰한다. 타이머가 중지될 경우(timerRunning == false) 시험 결과를 표시하는
         * 다이얼로그를 실행하는 메서드를 호출한다.
         */
        viewModel.getTimerRunningLiveData().observe(this, timerRunning -> {
            if (!timerRunning)
                startExamResultViewerDialog();
        });
    }

    /**
     * - Fragment 뒤로가기 처리를 담당하는 메서드.
     * - 다음과 같은 상황을 가정하여 뒤로 가기 이벤트를 처리한다.
     * * 아직 데이터를 로딩 중인 경우 → 아무것도 하지 않음.
     * * DrawerLayout이 열려 있는 경우 → 레이아웃을 닫음.
     * * 타이머가 실행 중인 경우 → 시험이 실행 중인 것을 알리고 정말로 죵료할 것인지 묻는 다이얼로그 실행.
     * * 타이머가 종료된 경우 → 시험이 끝났음을 알리고 정말로 화면을 종료할 것인지를 묻는 다이얼로그를 실행.
     */
    private void initBackPressedListener() {
        requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(),
                        new OnBackPressedCallback(true) {
                            @Override
                            public void handleOnBackPressed() {
                                if (viewModel.getExamQuestionListLiveData().getValue() == null)
                                    return;

                                if (binding.fragmentExamDocDrawerLayout.isDrawerOpen(DRAWER_GRAVITY)) {
                                    binding.fragmentExamDocDrawerLayout.closeDrawer(DRAWER_GRAVITY);
                                    return;
                                }

                                if (viewModel.getTimerRunningLiveData().getValue()) {
                                    DialogUtils.showConfirmDialog(requireContext(),
                                            "시험이 진행 중 입니다. 이 화면을 벗어나면 " +
                                                    "시험 내역이 저장되지 않습니다. " +
                                                    "정말로 종료하시겠습니까?",
                                            dialog -> requireActivity().finish());
                                    return;
                                }

                                DialogUtils.showConfirmDialog(requireContext(),
                                        "시험이 종료되었습니다. 화면을 나가시겠습니까?",
                                        dialog -> requireActivity().finish());
                            }
                        });
    }

    /**
     * LiveData와 직접적인 연관이 없는 View에 대한 이벤트를 설정하는 메서드.
     */
    private void initEventListener() {
        // "다음 문제" 버튼을 클릭했을 때 호출된다.
        binding.fragmentExamDocNextButton.setOnClickListener(v -> {
            if (examCursor == examInfoDTO.getNumOfQuestion() - 1) {
                MessageUtils.showToast(requireContext(), "마지막 문제입니다.");
                return;
            }
            examCursor++;
            updateQuestionViews();
        });

        // "이전 문제" 버튼을 클릭했을 때 호출된다.
        binding.fragmentExamDocPreviousButton.setOnClickListener(v -> {
            if (examCursor == 0) {
                MessageUtils.showToast(requireContext(), "첫 문제입니다.");
                return;
            }
            examCursor--;
            updateQuestionViews();
        });

        /**
         * - "채점 하기" 버튼을 클릭했을 때 호출된다.
         * - 채점을 하기 전에 답을 선택하지 않은 문제가 있는 확인한 후 사용자에게 알리면서 최종 의사를 묻는다.
         */
        binding.fragmentExamDocScoringButton.setOnClickListener(v -> {
            int incorrectAnswerCount = viewModel.getIncorrectAnswerCount();
            if (incorrectAnswerCount != 0) {
                DialogUtils.showConfirmDialog(requireContext(),
                        "아직 풀지 않은 문제가 " + incorrectAnswerCount + "개 존재합니다. " +
                                "그대로 채점하시겠습니까?",
                        dialog -> {
                            viewModel.getTimerRunningLiveData().setValue(false);
                            dialog.dismiss();
                        });
            } else {
                DialogUtils.showConfirmDialog(requireContext(),
                        "채점하시겠습니까?",
                        dialog -> {
                            viewModel.getTimerRunningLiveData().setValue(false);
                            dialog.dismiss();
                        });
            }
        });

        // "결과 확인" 버튼을 클릭했을 때 호출된다.
        binding.fragmentExamDocResultButton.setOnClickListener(v ->
                startExamResultViewerDialog());

        /**
         *  ContentLoadingProgressBar가 전면에 나와 있을 때 ChildView들에 대한 터치를 불가능하게 만들기
         * 위해서 터치 리스너를 설정했다.
         */
        binding.fragmentExamDocContentLoadingProgressBar.getRoot().
                setOnTouchListener((v, event) -> true);
    }

    /**
     *  Toolbar에 대한 초기 설정을 수행한다. Navigation 버튼을 누르면 DrawerLayout이 펼쳐지거나 접혀야 하므로
     * 이에 관련된 이벤트를 설정한다.
     */
    private void initToolbar() {
        binding.fragmentExamDocToolbarTitleTextView.setText(jmDTO.getJmName());
        binding.fragmentExamDocToolbar.setNavigationOnClickListener(v -> {
            DrawerLayout drawerLayout = binding.fragmentExamDocDrawerLayout;
            if (drawerLayout.isDrawerOpen(DRAWER_GRAVITY))
                drawerLayout.closeDrawer(DRAWER_GRAVITY);
            else
                drawerLayout.openDrawer(DRAWER_GRAVITY);
        });
    }

    /**
     * 제스쳐로 DrawerLayout을 열고 닫는 것을 불가능하게 하기 위해 관련 설정을 초기화한다.
     */
    private void initDrawerLayout() {
        binding.fragmentExamDocDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    /**
     * 선택지를 보여줄 RecyclerView를 초기화한다.
     */
    private void initQuestionOptionRecyclerView() {
        examDocOptionListAdapter = new ExamDocOptionListAdapter();
        /**
         * - 특정 아이템이 선택될 때 해당 아이템에 대한 이벤트를 설정한다.
         * - 특정 아이템이 선택되었을 때 해당 아이템에 체크 ImageView를 Visible 상태로 변경하여 해당 아이템이
         *  선택되었음을 시각적으로 보여준다. 반대로 이전에 선택했던 값이 있을 경우 해당 아이템의 ImageView도
         *  같이 갱신하여 하나의 선택만 유지되도록 한다.
         */
        examDocOptionListAdapter.setOnItemClickListener(newAnswer -> {
            final int oldAnswer = viewModel.getMyAnswerList().get(examCursor).getAnswer();
            if (oldAnswer == newAnswer)
                return;

            viewModel.getMyAnswerList().get(examCursor).setAnswer(newAnswer);
            examDocOptionListAdapter.setAnswer(newAnswer);
            if (oldAnswer != -1)
                examDocOptionListAdapter.notifyItemChanged(oldAnswer);
            examDocOptionListAdapter.notifyItemChanged(newAnswer);
            examDocAnswerListAdapter.notifyItemChanged(examCursor);
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false);
        binding.fragmentExamDocOptionRecyclerView.setAdapter(examDocOptionListAdapter);
        binding.fragmentExamDocOptionRecyclerView.setLayoutManager(layoutManager);
    }

    /**
     * DrawerLayout 내에 유저가 선택한 답안을 출력할 RecyclerView를 초기화한다.
     */
    private void initAnswerRecyclerView() {
        examDocAnswerListAdapter = new ExamDocAnswerListAdapter(viewModel.getMyAnswerList());
        // 특정 아이템이 선택되면 DrawerLayout을 접고 해당 문제를 화면에 세팅한다.
        examDocAnswerListAdapter.setOnItemClickListener(position -> {
            examCursor = position;
            updateQuestionViews();
            binding.fragmentExamDocDrawerLayout.closeDrawer(DRAWER_GRAVITY);
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false);
        binding.fragmentExamDocAnswerRecyclerView.setAdapter(examDocAnswerListAdapter);
        binding.fragmentExamDocAnswerRecyclerView.setLayoutManager(layoutManager);
        binding.fragmentExamDocAnswerRecyclerView
                .addItemDecoration(new DividerWithoutLast(requireContext()));
    }

    /**
     * - 화면에 표시된 문제와 관련된 View들을 갱신하며 갱신하는 데이터들은 다음과 같다.
     * * 과목명
     * * 문제 번호
     * * 문제의 질의 및 배점
     * * 문제의 선택지
     * * 타이머가 종료되었을 경우에는 문제에 대한 답안과 해설
     *
     * - examCursor가 현재 문항의 위치를 가리키고 있으므로 이 값을 통해 현재 문항에 대한 정보를 불러와 화면에
     * 세팅한다.
     * - 이 메서드가 호출되는 경우는 다음과 같다.
     * * 최초 문제 리스트가 서버로부터 로딩되었을 때
     * * 다음 문항, 이전 문항 버튼이 클릭되었을 때
     * * DrawerLayout에서 특정 문항이 클릭되었을 때
     */
    private void updateQuestionViews() {
        final ExamDocQuestionDTO item =
                viewModel.getExamQuestionListLiveData().getValue().get(examCursor);
        binding.fragmentExamDocSubjectTextView.setText(item.getSubjectName());
        binding.fragmentExamDocQuestionTextView.setText(new StringBuilder()
                .append("[").append(examCursor + 1).append("] ").append(item.getQuestion())
                .append(" (").append(item.getScore()).append("점)").toString());
        if (item.getRefImage() == null) {
            binding.fragmentExamDocReferenceImageView.setVisibility(View.GONE);
        } else {
            Glide.with(this)
                    .load(FileRequestURLBuilder.getExamRefImageURL(item.getRefImage()))
                    .error(R.drawable.image_loading_failed)
                    .into(binding.fragmentExamDocReferenceImageView);
            binding.fragmentExamDocReferenceImageView.setVisibility(View.VISIBLE);
        }
        examDocOptionListAdapter.setOptionList(item.getOptions());
        examDocOptionListAdapter.setAnswer(viewModel.getMyAnswerList().get(examCursor).getAnswer());
        examDocOptionListAdapter.notifyDataSetChanged(); // 높은 확률로 모든 데이터가 바뀜, 비교 자체가 낭비

        if (!viewModel.getTimerRunningLiveData().getValue()) {
            StringBuilder sb = new StringBuilder();
            int myAnswer = viewModel.getMyAnswerList().get(examCursor).getAnswer();
            ExamDocAnswerDTO answerDto = viewModel.getExamDocAnswerListLiveData().
                    getValue().get(examCursor);
            if (myAnswer + 1 == answerDto.getAnswer())
                sb.append("정답입니다.\n");
            else
                sb.append("오답입니다.\n정답은 ").append(answerDto.getAnswer()).append("번입니다.\n");

            if (!TextUtils.isEmpty(answerDto.getExplanation()))
                sb.append("\n[해설]\n").append(answerDto.getExplanation());

            binding.fragmentExamDocAnswerAndCommentaryTextView.setText(sb.toString());
        }
    }

    /**
     * - 시험이 끝났을 때 호출되며 더 이상 동작하지 않아야 하는 View나 이벤트를 정리한다.
     * - "채점하기" 버튼은 Disable되며 "결과보기" 버튼은 Enable된다.
     * - 채점이 완료되었기 때문에 각 문항의 값이 변경되어 혼란을 주지 않도록 설정했던 리스너에 null을 세팅한 뒤
     * 아답터를 업데이트한다. 이 때 모든 Item View의 리스너가 제거되어야 하므로 전체 데이터를 업데이트한다.
     * - 채점이 이루어지면서 답안 목록(myAnswerList)의 Item이 가지는 열거형 타입(ExamAnswerType) 데이터가
     * 변경되었다. 정답, 오답, 미선택 등이 설정되었으며 각 결과에 맞게 텍스트 색상을 변경해야 하므로 예외없이 모든
     * 데이터를 갱신한다.
     */
    private void examEndViewSetting() {
        binding.fragmentExamDocScoringButton.setEnabled(false);
        binding.fragmentExamDocResultButton.setEnabled(true);
        binding.fragmentExamDocAnswerAndCommentaryTextView.setVisibility(View.VISIBLE);
        examDocOptionListAdapter.setOnItemClickListener(null);
        examDocOptionListAdapter.notifyDataSetChanged();
        examDocAnswerListAdapter.notifyDataSetChanged();
        updateQuestionViews();
    }

    /**
     * - 시험 결과를 보여주기 위한 다이얼로그를 초기화한다.
     * - 채점을 수행하는 클래스로 채점을 수행한 뒤 결과를 서버에 전송한다. 그리고 결과를 출력할 다이얼로그를
     *  생성하면서 다이얼로그에 표시할 데이터들을 전달한다.
     */
    private void createExamResultViewerDialog() {
        ExamDocScoringTask task = new ExamDocScoringTask(
                viewModel.getExamQuestionListLiveData().getValue(),
                viewModel.getExamDocAnswerListLiveData().getValue(),
                viewModel.getMyAnswerList()); // 채점에 필요한 데이터를 생성자로 전달.
        task.scoring(); // 채점
        viewModel.saveResultData(examInfoDTO.getExamId(), task.getReasonForRejection(),
                task.getScore());
        examResultViewerDialog = new ExamResultViewerDialog(requireContext());
        examResultViewerDialog.setView(jmDTO, examInfoDTO, task, viewModel.getElapsedTime());
    }

    /**
     * Fragment가 실행되었을 때 문제 데이터를 읽어오지 못할 경우 이 메서드가 호출된다.
     */
    private void startLoadingFailedDialog() {
        DialogUtils.showAlertDialog(requireContext(),
                "데이터를 불러오는데 실패했습니다.",
                dialog -> requireActivity().finish());
    }

    /**
     * - 시험 결과 다이얼로그를 실행한다.
     * - 이 메서드가 호출되었을 때 시험 결과 다이얼로그가 초기화되지 않은 상태라면 다이얼로그를 초기화하는 메서드를
     *  호출한다. 이 때 채점이 같이 수행한다.
     * - 이 메서드는 "채점하기", "결과 보기" 버튼을 클릭했을 때 호출되거나 제한 시간을 모두 사용했을 경우 자동으로
     *  호출된다.
     */
    private void startExamResultViewerDialog() {
        if (examResultViewerDialog == null) {
            viewModel.terminationOfTimer();
            createExamResultViewerDialog();
            examEndViewSetting();
        }
        binding.fragmentExamDocDrawerLayout.closeDrawer(DRAWER_GRAVITY);
        examResultViewerDialog.show();
    }
}