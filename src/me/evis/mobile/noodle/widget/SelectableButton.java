package me.evis.mobile.noodle.widget;

import me.evis.mobile.noodle.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.Button;

@Deprecated
public class SelectableButton extends Button {
//    private String 
    public SelectableButton(Context context) {
        super(context);
    }

    public SelectableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectableButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
//        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.me_evis_mobile_noodle_widget_SelectableButton);
//        CharSequence s = a.getString(R.styleable.me_evis_mobile_noodle_widget_SelectableButton_selectedDrawableLeft);
//        if (s != null) {
//            //
//        }
//        a.recycle();
    }
    
    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        
        if (selected) {
            
        }
        invalidate();
    }
}
