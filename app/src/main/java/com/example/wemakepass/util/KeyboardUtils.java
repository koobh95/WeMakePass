package com.example.wemakepass.util;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 *  Keyboard 관련 유틸리티 클래스로 주로 Keyboard를 show/hide하는 동작을 수행한다.
 *
 * @author BH-Ku
 * @since 2023-06-05
 */
public class KeyboardUtils {
    private static final int HIDDEN_NO = Configuration.KEYBOARDHIDDEN_NO;
    private static final int HIDDEN_YES = Configuration.KEYBOARDHIDDEN_YES;

    public static void showKeyboard(Activity activity, View view) {
        if(activity.getResources().getConfiguration().hardKeyboardHidden == HIDDEN_YES)
            return;
        view.requestFocus();
        InputMethodManager manager = (InputMethodManager)activity.getSystemService(INPUT_METHOD_SERVICE);
        manager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void hideKeyboard(Activity activity, View rootView) {
        if(activity.getResources().getConfiguration().hardKeyboardHidden == HIDDEN_NO)
            return;
        InputMethodManager manager = (InputMethodManager)activity.getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(rootView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
