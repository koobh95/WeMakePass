package com.example.wemakepass.view.board.post.viewer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wemakepass.R;

public class PostViewerFragment extends Fragment {
    public static PostViewerFragment newInstance() {
        return new PostViewerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_viewer, container, false);
    }
}