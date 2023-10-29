package com.example.wemakepass.util;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 *  Keyboard 관련 유틸리티 클래스로 주로 Keyboard를 show/hide하는 동작을 수행한다.
 *
 * @author BH-Ku
 * @since 2023-06-05
 */
public class KeyboardUtil {
    public static void showKeyboard(Activity activity, View view) {
        view.requestFocus();
        InputMethodManager manager = (InputMethodManager)activity.getSystemService(INPUT_METHOD_SERVICE);
        manager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager manager = (InputMethodManager)activity.getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
