package me.evis.mobile.noodle;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.ValueAnimator;

public class PieChart extends View {

    public interface OnSliceClickListener {
        void onSliceClicked(PieChart pieChart, int sliceNumber);
    }

    private class AngleEvaluator extends FloatEvaluator {
        @Override
        public Float evaluate(float fraction, Number startValue, Number endValue) {
            float num = (Float) super.evaluate(fraction, startValue, endValue);
            mCurrAngle = num;
            invalidate();
            return num;
        }
    }

    public static enum DominantMeasurement {
        DOMINANT_WIDTH, DOMINANT_HEIGHT
    }

    private Paint[] paints;
    private float[] sliceEndAngles;
    private float[] sliceSizes;

    private float mCurrAngle;
    private int curSlice;
    private boolean shouldDraw;

    private RectF bounds;
    private Rect tempBounds;

    private OnSliceClickListener onSliceClickListener;
    private DominantMeasurement dominantMeasurement = DominantMeasurement.DOMINANT_WIDTH;

    public PieChart(Context context) {
        super(context);
        bounds = new RectF();
        tempBounds = new Rect();
    }

    public void setSlices(float[] slices) {
        paints = new Paint[slices.length];
        for (int i = 0; i < paints.length; i++) {
            paints[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
        }

        float total = 0.0f;
        for (int i = 0; i < slices.length; i++) {
            total += slices[i];
        }

        sliceSizes = new float[slices.length];
        for (int i = 0; i < slices.length; i++) {
            sliceSizes[i] = slices[i] / total * 360.0f;
        }

        float sliceStart = 0.0f;
        sliceEndAngles = new float[slices.length];
        for (int i = 0; i < sliceSizes.length; i++) {
            sliceEndAngles[i] = sliceStart + sliceSizes[i];
            sliceStart = sliceEndAngles[i];
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int newWidth;
        int newHeight;

        if (dominantMeasurement == DominantMeasurement.DOMINANT_WIDTH) {
            newWidth = getMeasuredWidth();
            newHeight = newWidth;
        } else {
            newHeight = getMeasuredHeight();
            newWidth = newHeight;
        }

        setMeasuredDimension(newWidth, newHeight);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if (!shouldDraw) {
            return;
        }

        canvas.getClipBounds(tempBounds);
        bounds.set(tempBounds);

        float startAngle = 0f;
        for (int i = 0; i < curSlice; i++) {
            startAngle = (i == 0) ? 0f : sliceEndAngles[i - 1];
//          Log.e("onDraw", String.format("drawing previous slice %d: start: %f, size: %f", i, startAngle, sliceSizes[i] ));
            canvas.drawArc(bounds, startAngle, sliceSizes[i], true, paints[i]);
            if (i == (curSlice - 1)) {
                startAngle = sliceEndAngles[i];
            }
        }
//      Log.e("onDraw", String.format("drawing current slice %d: start: %f, size: %f", curSlice, startAngle, mCurrAngle - startAngle ));
        canvas.drawArc(bounds, startAngle, mCurrAngle - startAngle, true, paints[curSlice]);
        if (mCurrAngle >= sliceEndAngles[curSlice]) {
            curSlice++;
        }
    }

    public void anima() {
        curSlice = 0;

        // TODO add method to provide these Paint's
        Random random = new Random();
        for (int i = 0; i < paints.length; i++) {
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            int color = Color.argb(0xff, r, g, b);
            paints[i].setColor(color);
        }

        shouldDraw = true;
        ValueAnimator animator = ValueAnimator.ofObject(new AngleEvaluator(), 0.0f, 360.0f);
        animator.setDuration(2000); // TODO make method to provide duration
        // TODO add method to provide an interpolator
        animator.start();
    }

    public float getAngle(float x, float y) {
        final float mCenterX = bounds.width() / 2;
        final float mCenterY = bounds.height() / 2;

        float a = y - mCenterY;
        float c = x - mCenterX;

        double angle = (Math.toDegrees(Math.atan2(a, c)) + 360.0) % 360.0;
        Log.e("getAngle", String.format("angle: %f", angle));

        return (float) angle;
    }

    public boolean isOnPieChart(float x, float y) {
        final float mCenterX = bounds.width() / 2;
        final float mCenterY = bounds.height() / 2;

        double distance = Math.sqrt(Math.pow(x - mCenterX, 2) + Math.pow(y - mCenterY, 2));
        Log.e("isOnPieChart", "radius: " + mCenterX);
        Log.e("isOnPieChart", "distance: " + distance);

        boolean isOnPieChart = distance <= mCenterX;
        return isOnPieChart;
    }

    public void setOnSliceClickListener(OnSliceClickListener onSliceClickListener) {
        this.onSliceClickListener = onSliceClickListener;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        throw new UnsupportedOperationException("You should use setOnSliceClickListener() to set a click listener on a slice of the chart.");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (onSliceClickListener != null) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                float x = event.getX();
                float y = event.getY();

                Log.e("onTouchEvent", String.format("touch up - x: %f, y: %f", x, y));

                boolean inChart = isOnPieChart(x, y);
                if (inChart) {
                    float angle = getAngle(x, y);

                    int slice = -1;
                    float startAngle = 0f;
                    for (int i = 0; i < sliceEndAngles.length; i++) {
                        float sliceSize = sliceSizes[i];
                        float endAngle = startAngle + sliceSize;

                        Log.e("onTouchEvent", String.format("slice %d, angle: %f, startAngle: %f, endAngle: %f", i, angle, startAngle, endAngle));
                        if (angle >= startAngle && angle <= (startAngle + sliceSize)) {
                            slice = i;
                            break;
                        }
                        startAngle += sliceSize;
                    }

                    onSliceClickListener.onSliceClicked(this, slice);
                }
                return true;
            }
        }

        return super.onTouchEvent(event);
    }

    public void setDominantMeasurement(DominantMeasurement dominantMeasurement) {
        this.dominantMeasurement = dominantMeasurement;
        requestLayout();
    }

    public DominantMeasurement getDominantMeasurement() {
        return dominantMeasurement;
    }
}