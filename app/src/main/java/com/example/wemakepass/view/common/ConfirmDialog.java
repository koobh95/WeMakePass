package com.example.wemakepass.view.common;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.wemakepass.databinding.DialogConfirmBinding;
import com.example.wemakepass.listener.OnDialogButtonClickListener;

/**
 * - 메시지, 확인, 취소 버튼을 가지는 의사 표현이 가능한 ConfirmDialog.
 * - 기능은 기본 다이얼로그와 다르지 않지만 CheckBox가 들어간 Dialog와의 디자인 통일을 위해 작성되었다.
 *
 * @author BH-Ku
 * @since 2023-10-26
 */
public class ConfirmDialog extends Dialog {
    private DialogConfirmBinding binding;

    public ConfirmDialog(@NonNull Context context) {
        super(context);
        binding = DialogConfirmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupWindow();
    }

    /**
     * Dialog의 사이즈를 조정한다.
     */
    private void setupWindow() {
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
        binding.dialogConfirmMessageTextView.setText(message);
    }

    /**
     * 확인 버튼의 기능을 구현하는 메서드다. 
     *
     * @param onDialogButtonClickListener
     */
    public void setOkButtonClickListener(OnDialogButtonClickListener onDialogButtonClickListener) {
        binding.dialogConfirmOkButton.setOnClickListener(v ->
                onDialogButtonClickListener.onClick(this));
    }

    /**
     * 취소 버튼의 기능을 구현하는 메서드다. 
     *
     * @param onDialogButtonClickListener
     */
    public void setCancelButtonClickListener(OnDialogButtonClickListener onDialogButtonClickListener){
        binding.dialogConfirmCancelButton.setOnClickListener(v ->
                onDialogButtonClickListener.onClick(this));
    }
}
