package com.example.wemakepass.view.exam.select;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.wemakepass.R;
import com.example.wemakepass.data.model.dto.ExamInfoDTO;
import com.example.wemakepass.data.model.dto.JmInfoDTO;
import com.example.wemakepass.databinding.FragmentExamSelectBinding;
import com.example.wemakepass.util.ExpandAnimationUtils;
import com.example.wemakepass.util.MessageUtils;
import com.example.wemakepass.view.exam.ExamActivity;
import com.example.wemakepass.view.exam.ExamViewModel;
import com.example.wemakepass.view.exam.guide.ExamGuideFragment;
import com.example.wemakepass.view.exam.jmSearch.JmSearchFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 응시할 시험을 선택하는 화면을 제공하는 Fragment.
 *
 * @author BH-Ku
 * @since 2032-11-13
 */
public class ExamSelectFragment extends Fragment {
    private FragmentExamSelectBinding binding;
    private ExamSelectViewModel viewModel;

    // JmSearchFragment 에서 선택한 종목 정보
    private JmInfoDTO selectedJmInfo;

    public static final String RESULT_REQUEST_CODE_JM_SEARCH_FRAGMENT = "jmSearchFragment";
    private final String TAG = "TAG_ExamSelectFragment";

    public static ExamSelectFragment newInstance() {
        return new ExamSelectFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_exam_select, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(requireActivity()).get(ExamSelectViewModel.class);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFragmentResultListener();
        initSpinnerItemSelectedListener();
        initEventListener();
        initObserver();
        initToolbar();
    }

    /**
     * JmSearchFragment에서 종목을 선택하면 Fragment를 종료할 때 선택한 종목 정보를 이전 Fragment에
     * 전달하는데 이 데이터를 수신하기 위한 FragmentResultListener를 설정한다.
     */
    private void initFragmentResultListener() {
        getParentFragmentManager()
                .setFragmentResultListener(RESULT_REQUEST_CODE_JM_SEARCH_FRAGMENT, requireActivity(),
                        (requestKey, result) -> {
                            selectedJmInfo = (JmInfoDTO) result
                                    .getSerializable(ExamActivity.ARG_SELECTED_JM_INFO);
                            viewModel.loadExamInfoList(selectedJmInfo.getJmCode());
                            binding.fragmentExamSelectJmNameTextView.setText(selectedJmInfo.getJmName());
                        });
    }

    /**
     * LiveData와 직접적인 연관이 없는 View에 대한 이벤트를 설정하는 메서드.
     */
    private void initEventListener() {
        binding.fragmentExamSelectJmSearchLayout.setOnClickListener(v ->
                ((ExamActivity) requireActivity()).addFragment(JmSearchFragment.newInstance(),
                        R.anim.slide_from_end,
                        R.anim.slide_in_end));

        /**
         * - "시험 시작" 버튼에 대한 이벤트를 설정한다.
         * - 이 버튼이 Enable 상태가 되기 위해서는 2번 째 Spinner인 "시험 회차" 스피너가 Visible 된 상태여야
         *  한다.
         * - 시험 구분 항목이 선택되지 않았을 경우 메서드를 종료한다. 스피너의 첫 번째 아이템에는 반드시 아이템
         *  선택 문구가 들어가기 때문에 선택된 아이템 위치가 0일 경우 시험 구분 항목이 선택되지 않았다고 판단한다.
         * - "시행 년도", "시행 회차", "시험 구분"이 모두 선택될 경우 이벤트 처리 로직을 수행한다.
         *  examInfoList에는 JmSearchFragment에서 선택했던 종목 코드에 대한 시험만 존재한다. 따라서 각
         *  Spinner에서 값을 읽어 온 후 이 값들과 일치하는 항목을 리스트에서 특정한 뒤 다음 화면인
         *  ExamGuideFragment를 실행하면서 파라미터로 전달한다.
         *
         */
        binding.fragmentExamSelectStartButton.setOnClickListener(v -> {
            if (binding.fragmentExamSelectFormatSpinner.getSelectedItemPosition() == 0) {
                MessageUtils.showToast(requireContext(), "시험 구분을 선택해주세요.");
                return;
            }

            ExamInfoDTO selectedExamInfo = null;
            String implYear = binding.fragmentExamSelectImplYearSpinner.getSelectedItem().toString();
            String implSeq = binding.fragmentExamSelectImplSeqSpinner.getSelectedItem().toString();
            String format = binding.fragmentExamSelectFormatSpinner.getSelectedItem().toString();
            for (ExamInfoDTO examInfoDTO : viewModel.getExamInfoListLiveData().getValue())
                if (examInfoDTO.getImplYear().equals(implYear)
                        && examInfoDTO.getImplSeq().equals(implSeq)
                        && examInfoDTO.getExamFormat().equals(format)) {
                    selectedExamInfo = examInfoDTO;
                    break;
                }

            ((ExamActivity) requireActivity())
                    .addFragment(ExamGuideFragment.newInstance(selectedJmInfo, selectedExamInfo),
                            R.anim.slide_from_end, R.anim.slide_in_end);
        });
    }

    /**
     * - Toolbar를 초기화한다.
     * - 이 화면은 ExamActivity의 첫 화면이나 다름없기 때문에 뒤로 가기가 선택되었을 경우 Activity를 종료한다.
     */
    private void initToolbar() {
        binding.fragmentExamSelectToolbar.setNavigationOnClickListener(v ->
                requireActivity().finish());
    }

    /**
     * LiveData에 대한 옵저빙을 설정한다.
     */
    private void initObserver() {
        viewModel.getNetworkErrorLiveData().observe(this, errorResponse ->
                MessageUtils.showToast(requireContext(), errorResponse.getMessage()));

        /**
         * - 특정 종목에 대한 시험 목록을 읽어 오는 것을 관찰한다.
         * - JmSearchFragment에서 특정 종목이 선택되면서 종료되면 관련 종목 정보를 받아온다. 이 때 종목 코드와
         *  일치하는 시험 일정 정보들을 서버에 요청하게 되고 데이터를 수신하면 "시행 년도"를 출력하는 Spinner에
         *  데이터를 세팅하기 위해 관련 메서드를 호출한다.
         */
        viewModel.getExamInfoListLiveData().observe(this, this::setImplYearLayout);
    }

    /**
     * - "시행 년도", "시행 회차" Spinner에 대한 이벤트 리스너를 작성한다.
     * - 각 스피너의 0번 index에는 선택을 요구하는 메시지가 포함되어 있기 때문에 선택된 아이템이 0이 아닐 경우에만
     *  이벤트 로직을 수행한다.
     * - "시행 년도"에서 특정 아이템이 선택될 경우 선택된 년도와 일치하는 데이터들 중에서 "시행 회차"를 파싱하여
     *  "시행 회차" Spinner를 세팅한다.
     * - "시험 구분" Spinner에 대한 이벤트 리스너는 작성하지 않는다. 추가적으로 취할 스탠스가 없기 때문이다.
     */
    private void initSpinnerItemSelectedListener() {
        binding.fragmentExamSelectImplYearSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0)
                            setImplSeqLayout();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        binding.fragmentExamSelectImplSeqSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    setFormatLayout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * - "시행 년도" Spinner에 아이템을 세팅한다.
     * - 첫 번째 아이템은 선택을 요구하는 메시지를 포함한다.
     * - 이 메서드는 종목 선택이 이루어지고 시험 목록이 호출되었을 때 호출된다.
     * - 이 메서드가 호출되는 경우는 최초로 종목 선택이 이루어지는 경우, 재선택이 발생하는 경우가 있다. 재선택이
     *  발생하는 경우 다른 Spinner들이 표시하고 데이터들이 의미가 없어지므로 VISIBILITY를 조정하여 선택을 막는다.
     * - 시험 목록에서 시행 년도에 대한 데이터를 추출할 때 중복이 없어야 하므로 Set을 사용했고 순서를 유지하기 위해
     *  LinkedHashSet을 사용했다.
     *
     * @param examInfoList 선택된 종목이 가진 시험 목록
     */
    private void setImplYearLayout(List<ExamInfoDTO> examInfoList) {
        Set<String> implYearSet = new LinkedHashSet<>();
        implYearSet.add("선택해주세요.");
        for (ExamInfoDTO examInfoDTO : examInfoList)
            implYearSet.add(examInfoDTO.getImplYear());

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireActivity(),
                android.R.layout.simple_spinner_dropdown_item, new ArrayList<>(implYearSet));
        binding.fragmentExamSelectImplYearSpinner.setAdapter(arrayAdapter);
        binding.fragmentExamSelectImplYearLayout.setVisibility(View.VISIBLE);
        binding.fragmentExamSelectImplSeqLayout.setVisibility(View.GONE);
        binding.fragmentExamSelectFormatLayout.setVisibility(View.GONE);
        binding.fragmentExamSelectStartButton.setEnabled(false);
    }

    /**
     * - "시행 년도" Spinner에서 아이템이 선택될 경우 호출되며 "시행 회차" Spinner에 아이템을 세팅한다.
     * - Spinner에 세팅하는 아이템은 "시행 년도"에서 선택된 값과 일치하는 데이터로 선별한다.
     */
    private void setImplSeqLayout() {
        final String selectedImplYear =
                binding.fragmentExamSelectImplYearSpinner.getSelectedItem().toString();
        List<ExamInfoDTO> examInfoList = viewModel.getExamInfoListLiveData().getValue();
        Set<String> implSeqSet = new LinkedHashSet<>();
        implSeqSet.add("선택해주세요.");
        for (ExamInfoDTO examInfoDTO : examInfoList)
            if (examInfoDTO.getImplYear().equals(selectedImplYear))
                implSeqSet.add(examInfoDTO.getImplSeq());

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireActivity(),
                android.R.layout.simple_spinner_dropdown_item, new ArrayList<>(implSeqSet));
        binding.fragmentExamSelectImplSeqSpinner.setAdapter(arrayAdapter);
        ExpandAnimationUtils.expand(binding.fragmentExamSelectImplSeqLayout);
        binding.fragmentExamSelectFormatLayout.setVisibility(View.GONE);
        binding.fragmentExamSelectStartButton.setEnabled(false);
    }

    /**
     * - "시행 회차" Spinner에서 아이템이 선택될 경우 호출되며 "시험 구분" Spinner에 아이템을 세팅한다.
     * - Spinner에 세팅하는 아이템은 "시행 년도", "시행 회차"에서 선택된 값과 일치하는 데이터로 선별한다.
     */
    private void setFormatLayout() {
        final String selectedImplYear =
                binding.fragmentExamSelectImplYearSpinner.getSelectedItem().toString();
        final String selectedImplSeq =
                binding.fragmentExamSelectImplSeqSpinner.getSelectedItem().toString();
        List<ExamInfoDTO> examInfoList = viewModel.getExamInfoListLiveData().getValue();
        Set<String> formatSet = new LinkedHashSet<>();
        formatSet.add("선택해주세요.");
        for (ExamInfoDTO examInfoDTO : examInfoList)
            if (examInfoDTO.getImplYear().equals(selectedImplYear)
                    && examInfoDTO.getImplSeq().equals(selectedImplSeq))
                formatSet.add(examInfoDTO.getExamFormat());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireActivity(),
                android.R.layout.simple_spinner_dropdown_item, new ArrayList<>(formatSet));
        binding.fragmentExamSelectFormatSpinner.setAdapter(arrayAdapter);
        binding.fragmentExamSelectFormatLayout.setVisibility(View.VISIBLE);
        binding.fragmentExamSelectStartButton.setEnabled(true);
    }
}
