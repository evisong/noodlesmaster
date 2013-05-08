package me.evis.mobile.noodle;

import android.content.Context;


public class StartTimerWidget5Provider extends AbstractStartTimerWidgetProvider {
    @Override
    protected String getTotalSecsLabel(Context context) {
        return context.getString(R.string.widget5_label);
    }
    
    @Override
    protected int getTotalSecs() {
        return 5 * 60;
    };
}
