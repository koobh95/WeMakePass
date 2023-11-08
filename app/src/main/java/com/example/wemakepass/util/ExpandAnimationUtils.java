package com.example.wemakepass.util;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 *  View의 Visibility를 [GONE ↔ VISIBLE]로 조정할 때 자연스럽게 보여주기 위해 Animation을 발생시키기 위한
 * Animation Utility Class다.
 *
 * 애니메이션은 다음과 같다.
 * - toggleArrow : 아이템을 확장/접기하는데 사용될 버튼에 이벤트가 발생했을 때 애니메이션
 *  (접기/펴기를 수행하는 레이아웃에 적용 시 선택적 사용)
 * - expand : 확장 이벤트가 발생했을 때 애니메이션
 * - collapse : 접기 이벤트가 발생했을 때 애니메이션
 *
 * References
 * - https://betterprogramming.pub/recyclerview-expanded-1c1be424282c
 * - https://stackoverflow.com/questions/4946295/android-expand-collapse-animation
 *
 * @author BH-Ku
 * @since 2023-08-28
 */
public class ExpandAnimationUtils {
    /**
     *
     * @param view 회전시키고자 하는 View
     * @param isExpanded 현재 View의 확장 여부. true = (▲), false = (▼)
     */
    public static void toggleArrow(View view, boolean isExpanded) {
        if(isExpanded)
            view.animate().setDuration(200).rotation(0);
        else
            view.animate().setDuration(200).rotation(180);
    }

    /**
     * View를 펼치는 Animation 수행
     *
     * @param v 애니메이션 대상 View
     */
    public static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    /**
     * 확장된 View를 접는 Animation 수행
     *
     * @param v 애니메이션 대상 View
     */
    public static void collapse(View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
}
