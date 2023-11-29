package com.example.wemakepass.view.board.post.write;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wemakepass.R;

public class PostWriteFragment extends Fragment {

    public static PostWriteFragment newInstance() {
        return new PostWriteFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_write, container, false);
    }
}