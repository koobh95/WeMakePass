package com.example.wemakepass.view.board.post.editor;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wemakepass.R;

import com.example.wemakepass.databinding.FragmentPostEditorBinding;
import com.example.wemakepass.util.DialogUtils;
import com.example.wemakepass.util.MessageUtils;
import com.google.android.material.tabs.TabLayout;

/**
 * 게시글을 작성하는 환경을 제공하는 Fragment.
 *
 * @author BH-Ku
 * @since 2023-12-10
 */
public class PostEditorFragment extends Fragment {
    private FragmentPostEditorBinding binding;
    private PostEditorViewModel viewModel;

    private long boardNo; // 게시글을 작성할 게시판의 식별 번호
    private String[] category; // 게시판에서 제공하는 카테고리 목록

    public static final String RESULT_REQUEST_CODE_POST_EDITOR_FRAGMENT = "postEditorFragment";
    public static final String ARG_WRITE_STATUS = "writeStatus";
    public static final String ARG_BOARD_NO = "boardNo";
    public static final String ARG_CATEGORY_LIST = "categoryList";
    private final String TAG = "TAG_PostEditorFragment";

    public static PostEditorFragment newInstance(long boardNo, String[] category) {
        PostEditorFragment fragment = new PostEditorFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_BOARD_NO, boardNo);
        bundle.putStringArray(ARG_CATEGORY_LIST, category);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        assert bundle != null;
        boardNo = bundle.getLong(ARG_BOARD_NO);
        category = bundle.getStringArray(ARG_CATEGORY_LIST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post_editor, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(PostEditorViewModel.class);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTabLayout();
        initObserver();
        initEventListener();
        initOnBackPressedListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     *  작성하는 게시글의 카테고리 선택지를 제공하는 역할을 하는 TabLayout을 초기화한다. 이 TabLayout은 단순히
     * 작성할 게시글의 Category를 결정하는 것이므로 별다른 이벤트 처리는 하지 않는다.
     */
    private void initTabLayout() {
        TabLayout tabLayout = binding.fragmentPostEditorCategoryTabLayout;

        for(String c : category)
            if(!c.equals("공지사항"))
                tabLayout.addTab(tabLayout.newTab().setText(c));
    }

    /**
     * LiveData와 직접적인 연관이 없는 View에 대한 이벤트를 설정하는 메서드.
     */
    private void initEventListener() {
        binding.fragmentPostEditorWriteButton.setOnClickListener(v -> {
            TabLayout tabLayout = binding.fragmentPostEditorCategoryTabLayout;
            viewModel.writePost(boardNo, category[tabLayout.getSelectedTabPosition()]);
        });

        binding.fragmentPostEditorExitButton.setOnClickListener(v ->
                exitFragment());
    }

    /**
     * LiveData에 대한 옵저빙을 설정한다.
     */
    private void initObserver() {
        viewModel.getSystemMessageLiveData().observe(this, systemMessage ->
                MessageUtils.showToast(requireContext(), systemMessage));

        /**
         *  게시글 작성을 요청했을 때 성공했을 경우 true를, 실패했을 경우 false가 초기화 된다. 성공했을 경우
         * 이전 Fragment인 BoardMainFragment에 게시글 작성에 성공했다는 사실을 알리면서 현재 Fragment를
         * 종료한다.
         */
        viewModel.getIsSuccessfullyWritingLiveData().observe(this, isSuccessfully -> {
            if(isSuccessfully){
                Bundle bundle = new Bundle();
                bundle.putBoolean(ARG_WRITE_STATUS, true);
                getParentFragmentManager().setFragmentResult
                        (RESULT_REQUEST_CODE_POST_EDITOR_FRAGMENT, bundle);
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack();
            } else {
                MessageUtils.showToast(requireContext(), "작성에 실패했습니다. 다시 시도해주세요.");
            }
        });
    }

    /**
     * 뒤로가기 처리를 담당하는 메서드.
     */
    private void initOnBackPressedListener() {
        requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(),
                        new OnBackPressedCallback(true) {
                            @Override
                            public void handleOnBackPressed() {
                                exitFragment();
                            }
                        });
    }

    /**
     *  화면 좌측 상단의 "X" 버튼 혹은 뒤로 가기를 눌렀을 때 호출되는 메서드다. EditText 내에 입력된 데이터가
     * 있는지 확인하여 없을 경우 Fragment를 즉시 종료, 데이터가 있다면 정말로 종료할 것인지 여부를 묻는다.
     */
    private void exitFragment() {
        if(!TextUtils.isEmpty(viewModel.getTitleLiveData().getValue()) ||
                !TextUtils.isEmpty(viewModel.getContentLiveData().getValue())){
            DialogUtils.showConfirmDialog(requireContext(),
                    "작성 중인 내용이 있습니다. 종료하시겠습니까?",
                    dialog ->{
                        requireActivity()
                                .getSupportFragmentManager()
                                .popBackStack();
                        dialog.dismiss();
                    });
            return;
        }

        requireActivity()
                .getSupportFragmentManager()
                .popBackStack();
    }
}
