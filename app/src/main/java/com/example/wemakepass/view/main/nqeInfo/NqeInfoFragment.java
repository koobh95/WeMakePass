package com.example.wemakepass.view.main.nqeInfo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wemakepass.R;

/**
 *
 * @author BH-Ku
 */
public class NqeInfoFragment extends Fragment {
    public static NqeInfoFragment newInstance() {
        return new NqeInfoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nqe_info, container, false);
    }
}