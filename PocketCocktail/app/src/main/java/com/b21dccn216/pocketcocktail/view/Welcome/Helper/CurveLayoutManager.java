package com.b21dccn216.pocketcocktail.view.Welcome.Helper;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class CurveLayoutManager extends RecyclerView.LayoutManager {

    private int radius = 800;
    private int centerX;
    private int centerY;


    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);

        int itemCount = getItemCount();
        if (itemCount == 0) return;

        centerX = getWidth()/2;
        centerY = getHeight()/2;

        float angleStep = 180f / (itemCount + 1);
        float angleStart = 180 + angleStep;

        for (int i = 0; i < itemCount; i++) {
            View view = recycler.getViewForPosition(i);
            addView(view);
            measureChildWithMargins(view, 0, 0);

            float angle = (float) Math.toRadians(angleStart + i * angleStep);
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));

            int viewWidth = getDecoratedMeasuredWidth(view);
            int viewHeight = getDecoratedMeasuredHeight(view);

            layoutDecorated(view,
                    x - viewWidth / 2,
                    y - viewHeight / 2,
                    x + viewWidth / 2,
                    y + viewHeight / 2);
        }

    }
}
