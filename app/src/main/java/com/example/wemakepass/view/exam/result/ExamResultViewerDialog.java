package com.example.wemakepass.view.exam.result;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.wemakepass.data.model.data.ExamSubjectResultModel;
import com.example.wemakepass.data.model.dto.ExamInfoDTO;
import com.example.wemakepass.data.model.dto.JmInfoDTO;
import com.example.wemakepass.databinding.DialogExamResultViewerBinding;
import com.example.wemakepass.task.exam.ExamDocScoringTask;

import java.util.List;

/**
 * 시험 결과를 출력하기 위한 Dialog다.
 *
 * @author BH-Ku
 * @since 2023-11-24
 */
public class ExamResultViewerDialog extends Dialog {
    private DialogExamResultViewerBinding binding;

    private final String TAG = "TAG_ExamResultViewerDialog";

    public ExamResultViewerDialog(@NonNull Context context) {
        super(context);
        binding = DialogExamResultViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initWindow();
        initEventListener();
    }

    /**
     * Dialog의 사이즈를 조정한다.
     */
    private void initWindow() {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(layoutParams);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);
    }

    /**
     * View에 이벤트를 초기화한다.
     */
    private void initEventListener() {
        binding.dialogExamResultDismissButton.setOnClickListener(v -> dismiss());
    }

    /**
     * Dialog에 데이터를 초기화하기 위해 호출되는 메서드.
     *
     * @param jmInfoDTO 종목 정보 데이터
     * @param examInfoDTO 시험 정보 데이터
     * @param task 채점을 수행한 객체로 채점 결과 데이터를 가지고 있음.
     * @param elapsedTime 시험을 응시하는데 걸린 초 단위 시간
     */
    public void setView(JmInfoDTO jmInfoDTO, ExamInfoDTO examInfoDTO, ExamDocScoringTask task,
                        long elapsedTime) {
        setMainTitle(jmInfoDTO.getJmName());
        setSubTitle(examInfoDTO.getImplYear(), examInfoDTO.getImplSeq(), examInfoDTO.getExamFormat());
        setSubjectDetail(task.getSubjectResultList());
        setDetailedScore(task.getScore(), task.getCorrectAnswerCount(),
                task.getIncorrectAnswerCount(), task.getUnWriteAnswerCount());
        setResultMessage(task.getReasonForRejection());
        setElapsedTime(elapsedTime);
    }

    /**
     * 주제목을 TextView에 초기화한다.
     *
     * @param mainTitle 종목 이름
     */
    private void setMainTitle(String mainTitle) {
        binding.dialogExamResultViewerMainTitleTextView.setText(mainTitle);
    }

    /**
     * 부제목을 TextView에 초기화한다.
     *
     * @param implYear 시행 년도
     * @param implSeq  시행 회차
     * @param format   시험 구분
     */
    private void setSubTitle(String implYear, String implSeq, String format) {
        String subTitle = implYear + "년 제 " + implSeq + "회 " + format;
        binding.dialogExamResultViewerSubTitleTextView.setText(subTitle);
    }


    /**
     * 시험 최종 점수, 채점 상세 정보를 TextView에 초기화한다.
     *
     * @param answerCount          맞춘 문제의 수
     * @param incorrectAnswerCount 틀린 문제의 수
     * @param unWriteAnswerCount   정답을 선택하지 않은 문제의 수
     */
    private void setDetailedScore(int resultScore, int answerCount, int incorrectAnswerCount,
                                 int unWriteAnswerCount) {
        StringBuilder sb = new StringBuilder("시험 결과 세부 사항\n");
        int total = answerCount + incorrectAnswerCount + unWriteAnswerCount;

        sb.append("\n총 문항 : ").append(total);
        if (answerCount != 0)
            sb.append("\n정답 : ").append(answerCount);

        if (incorrectAnswerCount != 0)
            sb.append("\n오답 : ").append(incorrectAnswerCount);

        if (unWriteAnswerCount != 0)
            sb.append("\n미입력 : ").append(unWriteAnswerCount);

        sb.append("\n\n최종 점수 : ").append(resultScore).append("점");
        binding.dialogExamResultViewerDetailedScoreTextView.setText(sb.toString());
    }

    /**
     * 과목별 채점 결과를 TextView에 초기화한다.
     *
     * @param subjectResultList 과목 단위 채점 결과를 가진 리스트
     */
    private void setSubjectDetail(List<ExamSubjectResultModel> subjectResultList) {
        StringBuilder sb = new StringBuilder("과목별 채점 결과\n\n");

        for (int i = 0; i < subjectResultList.size(); i++) {
            final ExamSubjectResultModel item = subjectResultList.get(i);

            sb.append(i + 1).append("과목 ").append(item.getSubjectName()).append(" (")
                    .append(item.getScore()).append('/').append(item.getTotalScore()).append(')');

            if(item.getPercentageScore() < 40)
                sb.append(" 과락");

            if (i + 1 != subjectResultList.size())
                sb.append('\n');
        }

        binding.dialogExamResultViewerSubjectResultTextView.setText(sb.toString());
    }

    /**
     * 시험 응시에 걸린 총 시간을 초기화하되 시/분/초 단위로 재구성하여 TextView에 초기화한다.
     *
     * @param elapsedTime 응시에 걸린 시단(초 단위)
     */
    private void setElapsedTime(long elapsedTime) {
        long hour = elapsedTime / 3600;
        long minute = (elapsedTime % 3600) / 60;
        long second = elapsedTime % 60;
        StringBuilder sb = new StringBuilder("총 소요 시간은");

        if(hour != 0)
            sb.append(' ').append(hour).append("시간");
        if(minute != 0)
            sb.append(' ').append(minute).append("분");
        if(second != 0)
            sb.append(' ').append(second).append("초");
        sb.append(" 입니다.");
        binding.dialogExamResultViewerElapsedTimeTextView.setText(sb.toString());
    }

    /**
     * 합격 여부, 불합격일 경우 사유를 TextView에 초기화한다.
     *
     * @param reasonForRejection 최종 획득 점수
     */
    private void setResultMessage(String reasonForRejection) {
        AppCompatTextView textView = binding.dialogExamResultViewerResultMessageTextView;
        if(TextUtils.isEmpty(reasonForRejection))
            textView.setText("축하합니다. 합격입니다.");
        else
            textView.setText(reasonForRejection + "(으)로 인한 불합격입니다.");
    }
}
