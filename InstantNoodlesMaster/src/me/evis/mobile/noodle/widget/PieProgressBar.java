package me.evis.mobile.noodle.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Inspired by venator85 / AnimatedPieChart.
 * 
 * https://github.com/venator85/AnimatedPieChart/blob/master/src/com/animatedpiechart/PieChart.java
 */
public class PieProgressBar extends ProgressBar {
    private static int[] colors = new int[] {
            Color.argb(0xff, 0xff, 0x87, 0x32),
            Color.argb(0xff, 0xf7, 0xb8, 0x8b)
        };
    private Paint[] paints;
    private float[] sliceEndAngles;
    private float[] sliceSizes;
    
    private RectF bounds;
    private Rect tempBounds;
    
    public PieProgressBar(Context context) {
        super(context);
        init();
    }
    
    public PieProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PieProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        redraw();
    }
    
    @Override
    public synchronized void setMax(int max) {
        super.setMax(max);
        redraw();
    }
    
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int length = Math.min(getMeasuredWidth(), getMeasuredHeight());
//        
//        // square.
//        setMeasuredDimension(length, length);
//    }
    
    @Override
    protected synchronized void onDraw(Canvas canvas) {
//        if (!shouldDraw) {
//            return;
//        }
        
        int length = Math.min(getMeasuredWidth(), getMeasuredHeight());
        canvas.getClipBounds(tempBounds);
        int centerX = tempBounds.centerX();
        int centerY = tempBounds.centerY();
        tempBounds.set(centerX - length / 2, centerY - length / 2, centerX + length / 2, centerY + length / 2);
//        canvas.clipRect(tempBounds);
        
        tempBounds.inset(4, 4);
        bounds.set(tempBounds);
        RectF borderBounds = new RectF();
        int outBorderWidth = Math.min(getMeasuredWidth(), getMeasuredHeight()) / 8;
        tempBounds.inset(outBorderWidth, outBorderWidth);
        borderBounds.set(tempBounds);
        RectF innerBounds = new RectF();
        int borderWidth = Math.min(getMeasuredWidth(), getMeasuredHeight()) / 6;
        tempBounds.inset(borderWidth, borderWidth);
        innerBounds.set(tempBounds);
        
//        Path path = new Path();
//        path.moveTo(centerX, centerY);
//        path.addRect(centerX - 50f, centerY + 50f, centerX + 50f, centerY - 50f, Direction.CW);
//        path.setFillType(FillType.INVERSE_WINDING);
//        canvas.clipPath(path);
        
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(colors[0]);
        paint.setShadowLayer(3, 0, 0, Color.BLACK);
        canvas.drawOval(bounds, paint);

        float startAngle = 270f;
        for (int i = 0; i < paints.length; i++) {
            startAngle = (i == 0) ? 270f : sliceEndAngles[i - 1] + 270f;
//          Log.e("onDraw", String.format("drawing previous slice %d: start: %f, size: %f", i, startAngle, sliceSizes[i] ));
            canvas.drawArc(borderBounds, startAngle, sliceSizes[i], true, paints[i]);
//            if (i == (curSlice - 1)) {
//                startAngle = sliceEndAngles[i];
//            }
        }
        canvas.drawOval(innerBounds, paints[1]);
//        canvas.rotate(270f);
//      Log.e("onDraw", String.format("drawing current slice %d: start: %f, size: %f", curSlice, startAngle, mCurrAngle - startAngle ));
//        canvas.drawArc(bounds, startAngle, mCurrAngle - startAngle, true, paints[curSlice]);
//        if (mCurrAngle >= sliceEndAngles[curSlice]) {
//            curSlice++;
//        }
    }
    
    private void init() {
        bounds = new RectF();
        tempBounds = new Rect();
        setSlices(new float[] {getProgress(), getMax() - getProgress()});
    }
    
    private void redraw() {
        setSlices(new float[] {getProgress(), getMax() - getProgress()});
        invalidate();
    }
    
    private void setSlices(float[] slices) {
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
        
        for (int i = 0; i < paints.length; i++) {
            int colorIndex = i;
            if (colorIndex > colors.length) {
                colorIndex = colors.length;
            }
            paints[i].setColor(colors[colorIndex]);
        }
    }
}