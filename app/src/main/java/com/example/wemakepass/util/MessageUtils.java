package com.example.wemakepass.util;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

/**
 * Toast, Snackbar로 메시지를 출력할 때 사용하는 유틸리티 클래스.
 *
 * @author BH-Ku
 * @since 2023-06-04
 */
public class MessageUtils {
    public static void showSnackbar(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
                .setDuration(1000)
                .show();
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
