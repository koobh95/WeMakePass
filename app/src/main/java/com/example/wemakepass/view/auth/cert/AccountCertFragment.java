package com.example.wemakepass.view.auth.cert;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wemakepass.R;
import com.example.wemakepass.data.enums.ErrorCode;
import com.example.wemakepass.databinding.FragmentAccountCertBinding;
import com.example.wemakepass.util.DialogUtils;

/**
 *  계정에 대한 이메일 인증을 수행하는 Activity다.
 *
 * @author BH-Ku
 * @since 2023-10-24
 */
public class AccountCertFragment extends Fragment {
    private FragmentAccountCertBinding binding;
    private AccountCertViewModel viewModel;

    private String userId;

    private static final String ARG_USER_ID = "userId";
    private final String TAG = "TAG_AccountCertFragment";

    /**
     * - 이전 화면에서 받아야 할 데이터가 있으므로 new 키워드가 아닌 newInstance 메서드를 사용한다.
     *
     * @param userId
     * @return
     */
    public static AccountCertFragment newInstance(String userId) {
        AccountCertFragment accountCertFragment = new AccountCertFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_USER_ID, userId);
        accountCertFragment.setArguments(bundle);
        return accountCertFragment;
    }

    /**
     * newInstance 메서드로 저장해놓은 bundle에서 userId를 꺼내 멤버 변수에 저장한다.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        assert bundle != null;
        userId = bundle.getString(ARG_USER_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_cert, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(AccountCertViewModel.class);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initOnBackPressedListener();
        initEventListener();
        initObserver();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * - 뒤로 가기 처리를 담당하는 메서드다.
     * - 타이머 Observable이 동작 중인지 판단하여 동작 중이라면 정말로 종료할 것인지 여부를 묻는다.
     * - 이 화면은 LoginFragment 위에 있으므로 화면 종료가 아닌 Backstack에서 pop하여 화면을 제거한다.
     */
    private void initOnBackPressedListener(){
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(viewModel.isRunningTimer()){
                    DialogUtils.showConfirmDialog(requireContext(),
                            "인증이 진행중입니다. 정말로 종료하시겠습니까?",
                            dialog -> {
                                dialog.dismiss();
                                requireActivity().getSupportFragmentManager().popBackStack();
                            });
                } else {
                    DialogUtils.showConfirmDialog(requireContext(),
                            "다음에 인증을 하지 않고 종료하시겠습니까?\n" +
                                    "(로그인 시 인증 화면으로 이동됩니다.)",
                            dialog -> {
                                dialog.dismiss();
                                requireActivity().getSupportFragmentManager().popBackStack();
                            });
                }
            }
        };

        requireActivity().getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), onBackPressedCallback);
    }

    /**
     * - LiveData와 직접적인 연관이 없는 View에 대한 이벤트를 설정하는 메서드다.
     * - 두 버튼 모두 userId를 필요로 하지만 userId는 Fragment에서 가지고 있다. setter를 통해 ViewModel에
     *  데이터를 넘겨줘도 되지만
     */
    private void initEventListener(){
        binding.fragmentAccountCertRequestButton.setOnClickListener(v ->
                viewModel.sendMailRequest(userId));

        binding.fragmentAccountCertConfirmButton.setOnClickListener(v ->
                viewModel.certCodeConfirmRequest(userId));
    }

    /**
     * LiveData에 대한 옵저빙을 설정한다.
     */
    private void initObserver() {
        viewModel.getSystemMessageLiveData().observe(this, systemMessage ->
                DialogUtils.showAlertDialog(requireContext(), systemMessage));

        viewModel.getNetworkErrorLiveData().observe(this, errorResponse -> {
            if(errorResponse.getMessage().equals(ErrorCode.CERT_CODE_MISMATCH.name())){
                DialogUtils.showAlertDialog(requireContext(), errorResponse.getMessage());
                return;
            }
            DialogUtils.showAlertDialog(requireContext(), errorResponse.getMessage());
        });

        /**
         * - 인증 이메일이 성공적으로 전송되는 것을 옵저빙한다.
         *
         * 인증 셋업 과정
         * - 먼저 남은 시간을 카운팅할 TextView의 VISIBILITY를 조정한다.(GONE → VISIBLE)
         * - 재요청을 할 수 없도록 "인증 요청" 버튼을 Disable 시킨다. 아이디나 이메일을 직접 입력하는 경우 실수로
         *  다른 값을 입력할 수도 있지만 이 경우에는 프래그먼트가 시작할 때 인증을 요청할 아이디 값을 받기 때문에
         *  문제가 일어날 가능성을 배제한다. 인증 시간 내에 인증을 완료하지 못했을 경우에만 다시 Enable한다.
         * - 이메일로 받은 인증번호를 서버로 전송할 수 있도록 "인증번호 확인" 버튼을 Enable시킨다.
         * - ViewModel에서 타이머를 시작하는 메서드를 호출한다.
         */
        viewModel.getIsSendMailLiveData().observe(this, send -> {
            DialogUtils.showAlertDialog(requireContext(), "이메일을 전송했습니다.");
            binding.fragmentAccountCertTimerTextView.setVisibility(View.VISIBLE);
            binding.fragmentAccountCertRequestButton.setEnabled(false);
            binding.fragmentAccountCertConfirmButton.setEnabled(true);
            binding.fragmentAccountCertCodeEditText.setEnabled(true);
            viewModel.startTimer();
        });

        /**
         * 인증이 완료되지 않고 타이머의 시간이 종료될 경우 화면의 View들을 인증 이전 상태로 되돌린다.
         */
        viewModel.getIsTimeOver().observe(this, aBoolean -> {
            viewModel.getTimerLiveData().setValue("인증 제한 시간이 초과되었습니다. 다시 시도해주세요.");
            binding.fragmentAccountCertRequestButton.setEnabled(true);
            binding.fragmentAccountCertConfirmButton.setEnabled(false);
            binding.fragmentAccountCertCodeEditText.setEnabled(false);
        });

        /**
         * 인증이 완료된 경우 알림과 함께 Fragment를 종료한다.
         */
        viewModel.getIsConfirmLiveData().observe(this, aBoolean -> {
            DialogUtils.showAlertDialog(requireContext(), "인증되었습니다. 다시 로그인해주세요.", dialog -> {
                dialog.dismiss();
                requireActivity().getSupportFragmentManager().popBackStack();
            });
        });
    }
}