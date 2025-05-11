package com.b21dccn216.pocketcocktail.customView;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;

import android.graphics.drawable.Drawable;

/**
 * Created by chris on 7/27/16.
 */
public class CenterTopCropImageView extends androidx.appcompat.widget.AppCompatImageView {

    public CenterTopCropImageView(Context context) {
        super(context);
        init();
    }

    public CenterTopCropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CenterTopCropImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }



    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        recomputeImgMatrix();
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        recomputeImgMatrix();
        return super.setFrame(l, t, r, b);
    }

    private void init() {
        setScaleType(ScaleType.MATRIX);
    }

    private void recomputeImgMatrix() {

        final Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        final Matrix matrix = getImageMatrix();

        float scale;
        float dx = 0, dy = 0;

        final int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        final int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();

        if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
            scale = (float) viewHeight / (float) drawableHeight;
            dx = (viewWidth - drawableWidth * scale) * 0.5f;
        } else {
            scale = (float) viewWidth / (float) drawableWidth;
        }

        matrix.setScale(scale, scale);
        matrix.postTranslate(dx, dy);
        setImageMatrix(matrix);
    }
}
//
///**
// * ImageView to display top-crop scale of an image view.
// *
// * @author Chris Arriola
// */
//public class CenterTopCropImageView extends AppCompatImageView {
//
//    public CenterTopCropImageView(Context context) {
//        super(context);
//        setScaleType(ScaleType.MATRIX);
//    }
//
//    public CenterTopCropImageView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        setScaleType(ScaleType.MATRIX);
//    }
//
//    public CenterTopCropImageView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        setScaleType(ScaleType.MATRIX);
//    }
//
//    @Override
//    protected boolean setFrame(int l, int t, int r, int b) {
//        final Matrix matrix = getImageMatrix();
//
//        float scale;
//        final int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
//        final int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
//        final int drawableWidth = getDrawable().getIntrinsicWidth();
//        final int drawableHeight = getDrawable().getIntrinsicHeight();
//
//        if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
//            scale = (float) viewHeight / (float) drawableHeight;
//        } else {
//            scale = (float) viewWidth / (float) drawableWidth;
//        }
//
//        matrix.setScale(scale, scale);
//        setImageMatrix(matrix);
//
//        return super.setFrame(l, t, r, b);
//    }
//}