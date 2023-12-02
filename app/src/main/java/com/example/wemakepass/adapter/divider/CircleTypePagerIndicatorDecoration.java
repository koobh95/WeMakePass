package com.example.wemakepass.adapter.divider;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wemakepass.R;

/**
 * - RecyclerView에서 페이지 단위로 아이템을 보여줄 때 전체 페이지의 수, 현재 페이지 위치 등을 보여주는
 * Indicator를 그리는 역할을 수행하는 클래스다.
 * - 구글링을 하다가 얻은 샘플 코드를 극히 일부만 수정하고 그대로 사용하였다.
 *
 * 코드 출처 : https://stackoverflow.com/questions/33841363/how-to-make-a-page-indicator-for-horizontal-recyclerview
 *
 * @author BH-Ku
 * @since 2023-12-02
 */
public class CircleTypePagerIndicatorDecoration extends RecyclerView.ItemDecoration {
    private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private final Paint mPaint = new Paint();

    private final int colorActive;
    private final int colorInactive;
    private static final float DP = Resources.getSystem().getDisplayMetrics().density;
    private final int mIndicatorHeight = (int) (DP * 16);
    private final float mIndicatorStrokeWidth = DP * 4;
    private final float mIndicatorItemLength = DP * 4;
    private final float mIndicatorItemPadding = DP * 8;

    public CircleTypePagerIndicatorDecoration(Context context) {
        colorActive = ContextCompat.getColor(context, R.color.ship_cove);
        colorInactive = ContextCompat.getColor(context, R.color.baltic_sea);
        mPaint.setStrokeWidth(mIndicatorStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        int itemCount = parent.getAdapter().getItemCount();
        float totalLength = mIndicatorItemLength * itemCount;
        float paddingBetweenItems = Math.max(0, itemCount - 1) * mIndicatorItemPadding;
        float indicatorTotalWidth = totalLength + paddingBetweenItems;
        float indicatorStartX = (parent.getWidth() - indicatorTotalWidth) / 2F;
        float indicatorPosY = parent.getHeight() - mIndicatorHeight / 2F + 10;

        drawInactiveIndicators(c, indicatorStartX, indicatorPosY, itemCount);

        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        int activePosition = layoutManager.findFirstVisibleItemPosition();
        if (activePosition == RecyclerView.NO_POSITION)
            return;

        final View activeChild = layoutManager.findViewByPosition(activePosition);
        int left = activeChild.getLeft();
        int width = activeChild.getWidth();
        float progress = mInterpolator.getInterpolation(left * -1 / (float) width);

        drawHighlights(c, indicatorStartX, indicatorPosY, activePosition, progress);
    }

    private void drawInactiveIndicators(Canvas c, float indicatorStartX, float indicatorPosY,
                                        int itemCount) {
        mPaint.setColor(colorInactive);
        final float itemWidth = mIndicatorItemLength + mIndicatorItemPadding;

        float start = indicatorStartX;
        for (int i = 0; i < itemCount; i++) {
            c.drawCircle(start, indicatorPosY, mIndicatorItemLength / 2F, mPaint);
            start += itemWidth;
        }
    }

    private void drawHighlights(Canvas c, float indicatorStartX, float indicatorPosY,
                                int highlightPosition, float progress) {
        mPaint.setColor(colorActive);
        final float itemWidth = mIndicatorItemLength + mIndicatorItemPadding;

        if (progress == 0F) {
            float highlightStart = indicatorStartX + itemWidth * highlightPosition;
            c.drawCircle(highlightStart, indicatorPosY, mIndicatorItemLength / 2F, mPaint);
        } else {
            float highlightStart = indicatorStartX + itemWidth * highlightPosition;
            float partialLength = mIndicatorItemLength * progress + mIndicatorItemPadding*progress;
            c.drawCircle(highlightStart + partialLength, indicatorPosY,
                    mIndicatorItemLength / 2F, mPaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = mIndicatorHeight;
    }
}