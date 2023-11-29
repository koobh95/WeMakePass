package com.example.wemakepass.view.board.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wemakepass.R;

public class BoardMainFragment extends Fragment {

    public static BoardMainFragment newInstance() {
        return new BoardMainFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_board_main, container, false);
    }
}