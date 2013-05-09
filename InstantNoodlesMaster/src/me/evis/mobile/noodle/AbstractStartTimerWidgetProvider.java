package me.evis.mobile.noodle;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

/**
 * Cannot add/remove appWidget on home screen programmatically, 
 * according to https://groups.google.com/forum/#!topic/android-developers/5kqWygpdk4g
 */
public abstract class AbstractStartTimerWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            int initialLayout = appWidgetManager.getAppWidgetInfo(appWidgetId).initialLayout;
            RemoteViews views =  new RemoteViews(context.getPackageName(), initialLayout);
            
            views.setTextViewText(R.id.totalTime, getTotalSecsLabel(context));
            
            String uri = NoodlesMaster.START_TIMER_URI.replace("{}", String.valueOf(getTotalSecs()));
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(uri));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.startTimerWidget, pendingIntent);
            
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
    
    protected abstract String getTotalSecsLabel(Context context);
    protected abstract int getTotalSecs();
}
