package com.example.wemakepass.view.board.post.viewer;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.wemakepass.R;
import com.example.wemakepass.adapter.ReplyListAdapter;
import com.example.wemakepass.data.enums.ErrorCode;
import com.example.wemakepass.data.model.dto.PostDetailDTO;
import com.example.wemakepass.data.model.dto.ReplyDTO;
import com.example.wemakepass.databinding.FragmentPostViewerBinding;
import com.example.wemakepass.util.DialogUtils;
import com.example.wemakepass.util.KeyboardUtils;
import com.example.wemakepass.util.MessageUtils;
import com.google.android.material.appbar.MaterialToolbar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 특정 게시글에 대한 상세 정보, 댓글 목록을 출력하는 환경을 제공하는 Fragment
 *
 * @author BH-Ku
 * @since 2023-12-12
 */
public class PostViewerFragment extends Fragment {
    private FragmentPostViewerBinding binding;
    private PostViewerViewModel viewModel;

    private ReplyListAdapter replyListAdapter;

    private String boardName; // Toolbar에 표시할 게시판 이름
    private long postNo; // 데이터를 요청할 게시글의 식별 번호
    private long reReplyNo = -1; // 특정 댓글에 대한 답글을 작성 중인 경우 댓글의 식별 번호를 초기화

    private final int REPLY_CONTENT_PROGRESS_BAR_VIEW = 0;
    private final int REPLY_STATUS_TEXT_VIEW = 1;
    private final int REPLY_RECYCLER_VIEW = 2;

    private final String DATE_TIME_FORMAT_OLD_DATE = "yyyy-MM-dd HH:mm";
    private final String DATE_TIME_FORMAT_THIS_YEAR = "MM-dd HH:mm";

    private static final String ARG_BOARD_NAME = "boardName";
    private static final String ARG_POST_NO = "postNo";
    private final String TAG = "TAG_PostViewerFragment";

    public static PostViewerFragment newInstance(String boardName, long postNo) {
        PostViewerFragment fragment = new PostViewerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_BOARD_NAME, boardName);
        bundle.putLong(ARG_POST_NO, postNo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        assert bundle != null;
        boardName = bundle.getString(ARG_BOARD_NAME);
        postNo = bundle.getLong(ARG_POST_NO);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post_viewer, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(PostViewerViewModel.class);
        binding.setViewModel(viewModel);
        KeyboardUtils.hideKeyboard(requireActivity(), binding.getRoot()); // 키보드가 열려 있다면 숨김
        // 로딩이 끝날 때까지 터치 방지
        requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initReplyRecyclerView();
        initEventListener();
        initObserver();
        initOnBackPressedListener();
        viewModel.requestPostDetail(postNo);
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
     * - 게시글 정보를 읽어왔을 때 호출되며 Toolbar를 초기화한다.
     * - Fragment가 실행될 때 이전 Fragment로부터 받아온 게시판 이름을 Title에 설정하고 읽어온 게시글 정보에서
     *  카테고리를 subTitle에 설정한다.
     *
     * @param category
     */
    private void initToolbar(String category) {
        MaterialToolbar toolbar = binding.fragmentPostViewerToolbar;
        toolbar.setTitle(boardName);
        toolbar.setSubtitle(category);
        toolbar.setNavigationOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());
    }

    /**
     * 댓글 목록 RecyclerView를 초기화한다.
     */
    private void initReplyRecyclerView() {
        RecyclerView recyclerView = binding.fragmentPostViewerReplyRecyclerView;
        replyListAdapter = new ReplyListAdapter();
        // 답글 작성 리스너 구현
        replyListAdapter.setOnReplyAnswerButtonClickListener(position ->
            setReReplyLayout(replyListAdapter.getCurrentList().get(position)));
        // 댓글 삭제 리스너 구현
        replyListAdapter.setOnReplyDeleteButtonClickListener(position ->{
            DialogUtils.showConfirmDialog(requireContext(),
                    "댓글을 삭제하시겠습니까?",
                    dialog -> {
                        long replyNo = replyListAdapter.getCurrentList().get(position).getReplyNo();
                        viewModel.replyDelete(replyNo);
                        dialog.dismiss();
                    });
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setAdapter(replyListAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(),
                RecyclerView.VERTICAL));
    }

    /**
     * API를 통해 조회한 게시글 데이터를 토대로 레이아웃을 세팅한다.
     *
     * @param item API를 통해 조회한 데이터
     */
    private void initPostViews(PostDetailDTO item){
        binding.fragmentPostViewerTitleTextView.setText(item.getTitle());
        binding.fragmentPostViewerWriterTextView.setText(item.getNickname());
        binding.fragmentPostViewerHitTextView.setText("조회수 " + item.getHit());
        String regDateString = item.getRegDate().getYear() == LocalDate.now().getYear() ?
                item.getRegDate().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_THIS_YEAR)) :
                item.getRegDate().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_OLD_DATE));
        binding.fragmentPostViewerRegDateTextView.setText(regDateString);
        binding.fragmentPostViewerContentTextView.setText(item.getContent());
    }

    /**
     * - 뒤로가기 처리를 담당하는 메서드.
     * - 댓글 EditText에 입력된 데이터가 있을 경우 정말로 종료할 것인지 확인한다.
     */
    private void initOnBackPressedListener() {
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(TextUtils.isEmpty(viewModel.getReplyContentLiveData().getValue()))
                    requireActivity()
                            .getSupportFragmentManager()
                            .popBackStack();
                else
                    DialogUtils.showConfirmDialog(requireContext(),
                            "작성 중인 댓글이 있습니다. 종료하시겠습니까?",
                            dialog -> {
                                requireActivity()
                                        .getSupportFragmentManager()
                                        .popBackStack();
                                dialog.dismiss();
                            });
            }
        };

        requireActivity().getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(),onBackPressedCallback);
    }

    /**
     * LiveData와 직접적인 연관이 없는 View에 대한 이벤트를 설정하는 메서드.
     */
    private void initEventListener() {
        // "댓글" 버튼을 눌렀을 때 댓글 목록(RecyclerView)으로 스크롤을 이동시킨다.
        binding.fragmentPostViewerReplyListLinkButton.setOnClickListener(v ->
            binding.fragmentPostViewerScrollView.smoothScrollBy(0,
                    binding.fragmentPostViewerReplyBarLayout.getTop()));

        // 댓글 목록을 재로딩한다.
        binding.fragmentPostViewerReplyReloadImageButton.setOnClickListener(v ->
                viewModel.requestReplyList(postNo));

        // 답글 작성 여부를 취소한다.
        binding.fragmentPostViewerReReplyCancelButton.setOnClickListener(v ->
                setReReplyLayout(null));

        /**
         *  EditText에 입력된 데이터를 초대로 댓글 작성 요청을 발생시킨다. 키보드를 숨기는 조작을 수행하기 위해
         * 작업의 수행 여부, 데이터에 대한 검증을 ViewModel이 아닌 여기서 수행하며 조건을 만족할 경우 키보드를
         * 숨기고 요청을 발생시킨다.
         */
        binding.fragmentPostViewerReplyWriteButton.setOnClickListener(v -> {
            if(viewModel.isRunningReplyWriting())
                return;
            if(!viewModel.isValidReplyContent())
                return;
            KeyboardUtils.hideKeyboard(requireActivity(), binding.getRoot());
            viewModel.replyWrite(postNo, reReplyNo);
        });
    }

    /**
     * LiveData에 대한 옵저빙을 설정한다.
     */
    private void initObserver() {
        viewModel.getSystemMessageLiveData().observe(this,systemMessage ->
                MessageUtils.showToast(requireContext(), systemMessage));

        viewModel.getNetworkErrorLiveData().observe(this, errorResponse -> {
            String errorCode = errorResponse.getCode();
            if(errorCode.equals(ErrorCode.POST_LOADING_FAILED_POST_DELETED.name()) ||
                    errorCode.equals(ErrorCode.POST_LOADING_FAILED_NETWORK_ERROR.name())) {
                // 게시글 삭제로 인한 게시글 조회 실패, 네트워크 에러로 인한 게시글 조회 실패
                DialogUtils.showAlertDialog(requireContext(),
                        errorResponse.getMessage(),
                        dialog -> {
                            requireActivity().getSupportFragmentManager().popBackStack();
                            dialog.dismiss();
                        });
                return;
            } else if(errorCode.equals(ErrorCode.REPLY_WRITE_FAILED_POST_DELETED.name()) ||
                    errorCode.equals(ErrorCode.REPLY_WRITE_FAILED_PARENT_REPLY_DELETED.name()) ||
                    errorCode.equals(ErrorCode.REPLY_DELETE_FAILED_POST_DELETED.name()) ||
                    errorCode.equals(ErrorCode.REPLY_LOADING_FAILED_POST_DELETED.name())){
                // 게시글 삭제로 인한 댓글 작성 실패, 상위 댓글 삭제로 인한 답글 작성 실패
                // 게시글 삭제로 인한 댓글 삭제 실패, 게시글 삭제로 인한 댓글 목록 재로딩 실패
                DialogUtils.showAlertDialog(requireContext(), errorResponse.getMessage());
                return;
            }

            MessageUtils.showToast(requireContext(), errorResponse.getMessage());
        });

        /**
         *  Fragment에 표시할 게시글 정보가 옵저빙되면 툴바, 전체적인 레이아웃 등을 초기화하는 메서드를 호출하고
         * 댓글 목록을 요청한다.
         */
        viewModel.getPostDetailLiveData().observe(this, postDetailDTO -> {
            initToolbar(postDetailDTO.getCategory());
            initPostViews(postDetailDTO);
            changeReplyViewVisibility(REPLY_CONTENT_PROGRESS_BAR_VIEW);
            requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            viewModel.requestReplyList(postNo);
        });

        // 댓글 목록 조회 결과 옵저빙
        viewModel.getReplyListLiveData().observe(this, this::setReplyLayout);

        // 댓글 작성 성공 여부 옵저빙
        viewModel.getWriteSuccessfullyLiveData().observe(this, successfully -> {
            if(successfully){
                setReReplyLayout(null);
                viewModel.requestReplyList(postNo);
                viewModel.getReplyContentLiveData().setValue("");
                binding.fragmentPostViewerScrollView.smoothScrollBy(0,
                        binding.getRoot().getBottom());
            }
        });

        // 댓글 삭제 성공 여부 옵저빙
        viewModel.getDeleteSuccessfullyLiveData().observe(this, successfully -> {
            if(successfully)
                viewModel.requestReplyList(postNo);
        });
    }

    /**
     * 댓글 목록을 조회했을 때 호출된다.
     *
     * @param replyList 댓글 목록
     */
    private void setReplyLayout(List<ReplyDTO> replyList) {
        if(replyList.size() == 0) {
            binding.fragmentPostViewerReplyStatusTextView.setText("댓글이 없습니다.");
            changeReplyViewVisibility(REPLY_STATUS_TEXT_VIEW);
        } else { // 댓글 RecyclerView 갱신
            replyListAdapter.submitList(replyList);
            changeReplyViewVisibility(REPLY_RECYCLER_VIEW);
        }

        int replyCount = 0; // 삭제되지 않은 댓글을 카운팅
        for(ReplyDTO replyDTO : replyList)
            if(!replyDTO.isDeleted())
                replyCount++;

        binding.fragmentPostViewerReplyLabelTextView.setText("댓글 " + replyCount + "개");

    }

    /**
     * - 댓글 관련 View의 Visiblity를 갱신한다.
     * - 댓글 목록을 읽어왔을 때 댓글이 존재할 경우 RecyclerView의 Visibility를 Visible로 변경하고 없을 경우
     *  Gone으로 변경한다. 이와 반대로 댓글이 존재하지 않을 경우 TextView에 댓글이 없음을 알리는 메시지를 세팅하고
     *  Visibility를 Visible 상태로 변경, 댓글이 있을 경우 Gone으로 변경한다.
     *
     * @param visibleView Visibility를 Visible로 조정할 View
     */
    private void changeReplyViewVisibility(int visibleView) {
        ConstraintLayout loadingProgressBar =
                binding.fragmentPostViewerReplyLoadingProgressBar.getRoot();
        RecyclerView recyclerView = binding.fragmentPostViewerReplyRecyclerView;
        AppCompatTextView statusTextView = binding.fragmentPostViewerReplyStatusTextView;

        switch (visibleView) {
            case REPLY_RECYCLER_VIEW:
                loadingProgressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                statusTextView.setVisibility(View.GONE);
                break;
            case REPLY_STATUS_TEXT_VIEW:
                loadingProgressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                statusTextView.setVisibility(View.VISIBLE);
                break;
            case REPLY_CONTENT_PROGRESS_BAR_VIEW:
                loadingProgressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                statusTextView.setVisibility(View.GONE);
        }
    }

    /**
     * 특정 댓글의 답글 버튼을 클릭할 경우 답글 대상을 화면에 명확하게 표시하기 위해 View를 세팅한다.
     *
     * @param replyDTO 답글 대상
     */
    @SuppressLint("SetTextI18n")
    private void setReReplyLayout(@Nullable ReplyDTO replyDTO){
        ConstraintLayout reReplyLayout = binding.fragmentPostViewerReReplyLayout;

        if(replyDTO == null) { // 답글을 취소했을 경우 View의 Visibility를 GONE으로 변경
            reReplyNo = -1;
            reReplyLayout.setVisibility(View.GONE);
            return;
        }

        reReplyNo = replyDTO.getReplyNo();
        binding.fragmentPostViewerReReplyTextView.setText(replyDTO.getWriterNickname() + "에게 답글");
        reReplyLayout.setVisibility(View.VISIBLE);
        KeyboardUtils.showKeyboard(requireActivity(), binding.fragmentPostViewerReplyInputEditText);
    }
}
