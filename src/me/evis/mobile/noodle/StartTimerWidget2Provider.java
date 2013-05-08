package me.evis.mobile.noodle;

import android.content.Context;



public class StartTimerWidget2Provider extends AbstractStartTimerWidgetProvider {
    @Override
    protected String getTotalSecsLabel(Context context) {
        return context.getString(R.string.widget2_label);
    }
    
    @Override
    protected int getTotalSecs() {
        return 3 * 60 + 30;
    };
}
