package com.example.wemakepass.util;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.example.wemakepass.listener.OnDialogButtonClickListener;
import com.example.wemakepass.view.common.AlertDialog;
import com.example.wemakepass.view.common.ConfirmDialog;

/**
 * - MessageUtil에서 Toast, Snacber, Dialog를 모두 관리하고 있다가 Dialog 파트가 분리되었다.
 * - Custom하여 생성한 Dialog들을 출력하는 역할을 하는 클래스다.
 *
 * @author BH-Ku
 * @since 2023-10-25
 */
public class DialogUtils {
    private static final String TAG = "TAG_DialogUtil";

    /**
     * - 알림에만 목적을 둔 다이얼로그로서 메시지와 버튼 하나만을 제공한다.
     * - 표시할 메시지를 파라미터로 받는다.
     * - 확인 버튼이 미리 정의되어 있으며 다이얼로그를 종료하는 역할을 수행한다.
     *
     * @param context
     * @param message 출력할 메시지
     */
    public static void showAlertDialog(@NonNull Context context, @NonNull String message) {
        showAlertDialog(context, message, Dialog::dismiss);
    }

    /**
     * - 알림에만 목적을 둔 다이얼로그로서 메시지와 버튼 하나만을 제공한다.
     * - 표시할 메시지를 파라미터로 받는다.
     * - 확인 버튼이 정의되어 있지 않아 파라미터로 인터페이스를 전달하고 호출하는 측에서 구현된다.
     *
     * @param context
     * @param message 출력할 메시지
     * @param onDialogButtonClickListener 확인 버튼이 선택되었을 때 수행될 작업
     */
    public static void showAlertDialog(@NonNull Context context, @NonNull String message,
                                       OnDialogButtonClickListener onDialogButtonClickListener) {
        AlertDialog alertDialog = new AlertDialog(context);
        alertDialog.setMessage(message);
        alertDialog.setOkButtonClickListener(onDialogButtonClickListener);
        alertDialog.show();
    }

    /**
     * - 메시지와 함께 의사를 결정할 수 있는 버튼 2개를 제공한다.
     * - 표시할 메시지를 파라미터로 받는다.
     * - 취소 버튼은 미리 정의되어 있으며 다이얼로그를 종료하는 역할을 수행한다.
     * - 확인 버튼이 정의되어 있지 않아 파라미터로 인터페이스를 전달하고 호출하는 측에서 구현된다.
     *
     * @param context
     * @param message 출력할 메시지
     * @param onOkButtonClickListener 확인 버튼이 선택되었을 때 수행될 작업
     */
    public static void showConfirmDialog(@NonNull Context context, String message,
                                         OnDialogButtonClickListener onOkButtonClickListener) {
        showConfirmDialog(context, message, onOkButtonClickListener, Dialog::dismiss);
    }

    /**
     * - 메시지와 함께 의사를 결정할 수 있는 버튼 2개를 제공한다.
     * - 표시할 메시지를 파라미터로 받는다.
     * - 확인, 취소 버튼이 모두 정의되어 있지 않아 파라미터로 인터페이스를 전달하고 호출하는 측에서 구현된다.
     *
     * @param context
     * @param message 출력할 메시지
     * @param onOkButtonClickListener 확인 버튼이 선택되었을 때 수행될 작업
     * @param onCancelButtonClickListener 취소 버튼이 선택되었을 때 수행될 작업
     */
    public static void showConfirmDialog(@NonNull Context context, String message,
                                         OnDialogButtonClickListener onOkButtonClickListener,
                                         OnDialogButtonClickListener onCancelButtonClickListener) {
        ConfirmDialog confirmDialog = new ConfirmDialog(context);
        confirmDialog.setMessage(message);
        confirmDialog.setOkButtonClickListener(onOkButtonClickListener);
        confirmDialog.setCancelButtonClickListener(onCancelButtonClickListener);
        confirmDialog.show();
    }
}
