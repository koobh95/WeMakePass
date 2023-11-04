package com.example.wemakepass.view.accountSetting.nicknameChange;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wemakepass.R;
import com.example.wemakepass.databinding.FragmentNicknameChangeBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NicknameChangeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NicknameChangeFragment extends Fragment {
    private FragmentNicknameChangeBinding binding;
    private NicknameChangeViewModel viewModel;

    public static NicknameChangeFragment newInstance() {
        return new NicknameChangeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_nickname_change, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(NicknameChangeViewModel.class);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar();
    }

    private void initToolbar() {
        binding.fragmentNicknameChangeToolbar.setNavigationOnClickListener(t ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack());
    }
}