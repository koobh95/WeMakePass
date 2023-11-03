package com.example.wemakepass.view.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.wemakepass.databinding.DialogAlertBinding;
import com.example.wemakepass.listener.OnDialogButtonClickListener;

/**
 * - 메시지와 확인 버튼만 존재하는 AlertDialog.
 * - 기능은 기본 다이얼로그와 다르지 않지만 CheckBox가 들어간 Dialog와의 디자인 통일을 위해 작성되었다.
 * 
 * @author BH-Ku 
 * @since 2023-10-25
 */
public class AlertDialog extends Dialog {
    private DialogAlertBinding binding;

    public AlertDialog(@NonNull Context context) {
        super(context);
        binding = DialogAlertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initWindow();
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
     * Dialog에 표시될 메시지를 설정하는 메서드.
     * @param message
     */
    public void setMessage(String message) {
        binding.dialogAlertMessageTextView.setText(message);
    }

    /**
     * 확인 버튼의 기능을 구현하는 메서드다.
     *
     * @param onDialogButtonClickListener
     */
    public void setOkButtonClickListener(OnDialogButtonClickListener onDialogButtonClickListener) {
        binding.dialogAlertOkButton.setOnClickListener(v ->
                onDialogButtonClickListener.onClick(this));
    }
}
