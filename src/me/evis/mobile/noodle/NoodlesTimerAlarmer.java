package me.evis.mobile.noodle;

import me.evis.mobile.util.WakeLocker;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

public class NoodlesTimerAlarmer extends BroadcastReceiver {
    private static final String SOUND_PATH = "android.resource://" + NoodlesMaster.class.getPackage().getName() + "/";
    // Free sound from http://www.freesound.org/people/Georgeantoniv/sounds/169584/
    private static Uri soundUri = Uri.parse(SOUND_PATH + R.raw.microwave_beep);
    
    private static final int NOTIFICATION_ID = R.layout.main;
    
	/**
	 * @see android.content.BroadcastReceiver#onReceive(Context,Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
	    WakeLocker.acquire(context, "NoodlesTimerAlarmer");

	    sendNotification(context);
	    
	    Toast.makeText(context, R.string.notification_timer_complete_text, Toast.LENGTH_SHORT).show();
	    
	    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
	    Intent newIntent = new Intent(NoodlesMaster.NOODLES_TIMER_COMPLETE);
	    newIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//	    PendingIntent newPendingIntent = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		localBroadcastManager.sendBroadcastSync(newIntent);
//	    context.sendBroadcast(newIntent);
        
		WakeLocker.release();
	}
	
	private void sendNotification(Context context) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.noodles_master_logo)
                .setContentTitle(context.getString(R.string.notification_timer_complete_title))
                .setContentText(context.getString(R.string.notification_timer_complete_text))
                .setSound(soundUri)
                .setAutoCancel(true);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, NoodlesMaster.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.from(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(NoodlesMaster.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.getNotification());
    }
}
