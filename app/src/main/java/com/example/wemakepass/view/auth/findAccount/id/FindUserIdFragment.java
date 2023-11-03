package com.example.wemakepass.view.auth.findAccount.id;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wemakepass.R;
import com.example.wemakepass.databinding.FragmentFindUserIdBinding;
import com.example.wemakepass.util.DateUtil;
import com.example.wemakepass.util.DialogUtil;

/**
 *
 * @author BH-Ku
 * @since 2023-10-27
 */
public class FindUserIdFragment extends Fragment {
    private FragmentFindUserIdBinding binding;
    private FindUserIdViewModel viewModel;

    private final String TAG = "TAG_FindUserIdFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_user_id, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(FindUserIdViewModel.class);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObserver();
    }

    /**
     * LiveData에 대한 옵저빙을 설정한다.
     */
    public void initObserver() {
        /**
         * 비지니스 로직을 처리하면서 발생하는 메시지 출력
         */
        viewModel.getSystemMessageLiveData().observe(this, systemMessage ->
                DialogUtil.showAlertDialog(requireContext(), systemMessage));

        /**
         * 서버로부터 메일이 발송되었다는 응답을 받았을 때 사용자에게 Dialog를 출력하여 알림
         */
        viewModel.getIsSendMailLiveData().observe(this, aBoolean ->
                DialogUtil.showAlertDialog(requireContext(),
                        "입력한 이메일로 회원님의 아이디를 전송했습니다."));
    }
}