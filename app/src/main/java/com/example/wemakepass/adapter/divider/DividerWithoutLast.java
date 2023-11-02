package com.example.wemakepass.adapter.divider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.R;

/**
 * - RecyclerView에 구분선을 넣기 위해 사용되는 클래스로 특정 상황에서 DividerItemDecoration 클래스를
 *  대체하여 사용된다.
 * - 이 구분선은 좌우 15dp의 여백을 가지고 가장 마지막 아이템 아래에는 구분선을 넣지 않는다. RecyclerView의
 *  Background에 drawable을 사용할 경우 거의 필수적으로 사용될 것으로 보인다.
 *
 * @author BH-Ku
 * @since 203-11-02
 */
public class DividerWithoutLast extends RecyclerView.ItemDecoration {
    private Drawable dividerDrawable;

    private final String TAG = "TAG_DividerWithoutLast";

    public DividerWithoutLast(@Nullable Context context) {
        dividerDrawable = context.getDrawable(R.drawable.bg_list_divider);
    }

    /**
     * - 이 메서드는 구분선이 그려질 때 자동으로 호출되는 메서드로 마지막 선은 생략하기 위해서 Overriding 하였다.
     * - 마지막 선을 생략하기 위해선 ChildView, 그러니까 RecyclerView에 표시되는 ItemView를 최대 수보다 1
     *  적게 그려주기만 하면 되기에 간단해 보이지만 대신 drawable을 화면에 그릴 코드를 직접 작성해야 한다는
     *  단점이 있다.
     * - 로직은 간단하다. 먼저 아이템의 왼쪽과 오른쪽의 여백을 계산하여 시작점과 끝점이 그려질 x 좌표를 구한다.
     *  그리고 아이템의 bottom, bottom margin을 더해 시작 y 좌표를 구한다. 마지막으로 시작 y 좌표에
     *  구분선(Drawable)의 높이를 더해주면 끝 y 좌표를 구할 수 있다. 이제 시작 x, y, 끝 x, y가 구해졌으므로
     *  화면에 그려주기만 하면 된다.
     *
     * @param c Canvas to draw into
     * @param parent RecyclerView this ItemDecoration is drawing into
     * @param state The current state of RecyclerView.
     */
    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int divStart = parent.getPaddingStart();
        int divEnd = parent.getWidth() - parent.getPaddingEnd();
        int childCount = parent.getChildCount();

        for(int i = 0; i < childCount-1; i++){
            View childView = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)childView.getLayoutParams();
            int divTop = childView.getBottom() + params.bottomMargin;
            int divBottom = divTop + dividerDrawable.getIntrinsicHeight();

            dividerDrawable.setBounds(divStart, divTop, divEnd, divBottom);
            dividerDrawable.draw(c);
        }
    }
}
