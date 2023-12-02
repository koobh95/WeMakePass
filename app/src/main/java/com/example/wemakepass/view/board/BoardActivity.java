package com.example.wemakepass.view.board;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.example.wemakepass.R;
import com.example.wemakepass.data.model.dto.BoardDTO;
import com.example.wemakepass.databinding.ActivityBoardBinding;
import com.example.wemakepass.listener.AttachFragmentListener;
import com.example.wemakepass.view.board.main.BoardMainFragment;
import com.example.wemakepass.view.board.search.BoardSearchActivity;

/**
 * 게시판, 포스팅 등에 관련한 Fragment를 실행하는 Container 역할을 하는 Activity
 *
 * @author BH-Ku
 * @since 2023-11-30
 */
public class BoardActivity extends AppCompatActivity implements AttachFragmentListener {
    private ActivityBoardBinding binding;

    public static final String ARG_SELECTED_BOARD = "selectedBoardDTO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        BoardDTO boardDTO = (BoardDTO) intent.getSerializableExtra(ARG_SELECTED_BOARD);
        replaceFragment(BoardMainFragment.newInstance(boardDTO));
    }

    @Override
    public void addFragment(Fragment fragment, @Nullable int pushAnim, @Nullable int popAnim) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(pushAnim, popAnim, pushAnim, popAnim)
                .add(binding.activityBoardContainerView.getId(), fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.activityBoardContainerView.getId(), fragment)
                .commit();
    }
}
