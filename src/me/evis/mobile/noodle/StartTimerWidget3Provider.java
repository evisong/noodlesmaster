package me.evis.mobile.noodle;

import android.content.Context;


public class StartTimerWidget3Provider extends AbstractStartTimerWidgetProvider {
    @Override
    protected String getTotalSecsLabel(Context context) {
        return context.getString(R.string.widget3_label);
    }
    
    @Override
    protected int getTotalSecs() {
        return 4 * 60;
    };
}
