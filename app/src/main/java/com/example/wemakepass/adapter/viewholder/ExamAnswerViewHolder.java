package com.example.wemakepass.adapter.viewholder;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.R;
import com.example.wemakepass.data.enums.ExamAnswerState;
import com.example.wemakepass.databinding.ItemExamAnswerBinding;
import com.example.wemakepass.listener.OnItemClickListener;

/**
 *  ExamActivity > ExamDocFragment, ExamPracFragment > DrawerLayout에 사용자가 입력한 답안 리스트를
 * 구성하는 ViewHolder 클래스.
 *
 * @author BH-Ku
 * @since 2023-11-23
 */
public class ExamAnswerViewHolder extends RecyclerView.ViewHolder {
    private ItemExamAnswerBinding binding;

    public ExamAnswerViewHolder(@NonNull ItemExamAnswerBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    /**
     * - 사용자가 입력한 답안을 TextView에 세팅한다.
     * - 오버로딩된 메서드로서 답안이 String인 경우, 즉 실기(주관식) 시험인 경우 이 메서드가 호출된다.
     * @param answer 사용자가 입력한 문제의 답안
     */
    public void setAnswer(String answer) {
        setQuestionNo();
        if (TextUtils.isEmpty(answer)) // 빈 문자열인 경우 입력된 값이 없으므로 세팅할 값이 없음.
            return;
        binding.itemExamAnswerTextView.setText(answer);
    }

    /**
     * - 사용자가 선택지에서 선택한 번호를 TextView에 세팅한다.
     * - 오버로딩된 메서드로서 답이 오로지 숫자인 경우, 즉 필기(객관식) 시험인 경우 이 메서드가 호출된다.
     * @param answer 사용자가 선택한 선택지
     */
    public void setAnswer(int answer) {
        setQuestionNo();
        if (answer == -1) // -1인 경우 선택된 값이 없으므로 세팅할 값이 없음.
            return;
        binding.itemExamAnswerTextView.setText(Integer.toString((answer + 1)));
    }

    /**
     * 답안이 채점된 상태인가 아닌가를 체크하여 TextColor를 변경한다.
     * - NON : 채점되지 않은 상태, 아무 것도 하지 않음.
     * - CORRECT_ANSWER : 정답, 글자 색상을 초록색으로 변경
     * - INCORRECT_ANSWER : 오답, 글자 색상을 주홍색으로 변경(빨간색은 너무 눈이 아픔)
     * - UN_WRITE_ANSWER : 미입력, 표시할 텍스트(답안)가 없으므로 텍스트를 "미입력으로 변경하고 "글자 색상을
     *  회색으로 변경한다.
     *
     * @param answerType 현재 답안의 채점 상태를 나타내며 채점되지 않음(NON), 정답, 오답, 미입력 등이 있다.
     */
    public void setScoringResult(ExamAnswerState answerType) {
        if (answerType == ExamAnswerState.NON)
            return;

        final Context context = binding.getRoot().getContext();
        final AppCompatTextView textView = binding.itemExamAnswerTextView;
        switch (answerType) {
            case CORRECT_ANSWER:
                textView.setTextColor(ContextCompat.getColor(context, R.color.green));
                break;
            case INCORRECT_ANSWER:
                textView.setTextColor(ContextCompat.getColor(context, R.color.rose));
                break;
            case UN_WRITE_ANSWER:
                textView.setText("미입력");
                textView.setTextColor(ContextCompat.getColor(context, R.color.dove_gray_approx));
        }
    }

    /**
     * 아이템을 클릭했을 때 발생시킬 이벤트를 설정한다.
     *
     * @param onItemClickListener 아이템을 클릭했을 때 발생시킬 이벤트
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        binding.getRoot().setOnClickListener(v ->
                onItemClickListener.onItemClick(getAdapterPosition()));
    }

    /**
     * - 표시할 답안이 어떤 문제의 답안인지 나타내기 위해 문제의 번호를 TextView에 세팅한다.
     * - 이 메서드는 setAnswer 메서드가 호출될 때 각각 호출된다.(생성자에서 호출하면 getAdapterPosition()을
     *  사용할 수 없기에 실제 바인딩될 때 호출해야 함.)
     * - 실제 문항의 번호는 반드시 1부터 시작하여 n(문제의 수)까지 순서대로 카운팅되기 때문에 바인딩되는 데이터와
     *  상관없이 아이템의 현재 위치를 사용하여 문자열을 생성한다.
     */
    private void setQuestionNo() {
        binding.itemExamAnswerQuestionNoTextView.setText("[" + (getAdapterPosition() + 1) + "]");
    }
}
