package com.example.wemakepass.view.main.community;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wemakepass.data.model.dto.JmInfoDTO;
import com.example.wemakepass.databinding.FragmentCommunityBinding;
import com.example.wemakepass.view.jmSearch.JmSearchFragment;
import com.example.wemakepass.view.main.community.boardSearch.BoardSearchActivity;

/**
 *
 *
 * @author BH-Ku
 * @since 2023-11-28
 */
public class CommunityFragment extends Fragment {
    private FragmentCommunityBinding binding;

    private final String TAG = "TAG_CommunityFragment";

    public static CommunityFragment newInstance() {
        return new CommunityFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCommunityBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initEventListener();
    }

    private void initEventListener() {
        // 게시판 검색
        binding.fragmentCommunitySearchBarLayout.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), BoardSearchActivity.class)));
    }
}