package com.example.wemakepass.listener;

import android.app.Dialog;
import android.view.View;

/**
 * - Custom한 다이얼로그에 부착될 Button의 OnClickListener 에 사용될 리스너 인터페이스로서 현재 출력 중인
 *  다이얼로그를 다루기 위해서 인자로 Dialog 객체를 갖는다.
 *
 * @author BH-Ku
 * @since 2023-10-26
 */
public interface OnDialogButtonClickListener {
    void onClick(Dialog dialog);
}
