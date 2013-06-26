package me.evis.mobile.noodle;

import me.evis.mobile.util.WakeLocker;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class NoodlesTimerAlarmer extends BroadcastReceiver {
    private static final String TAG = "NoodlesTimerAlarmer";
    
    // Default value of system notification sounds
    private static final String DEFAULT_SOUND_PREFERENCE_KEY = "content://settings/system/notification_sound";
    private static final String SOUND_PATH = "android.resource://" + NoodlesMaster.class.getPackage().getName() + "/";
    // Free sound from http://www.freesound.org/people/Georgeantoniv/sounds/169584/
    private static Uri defaultSoundUri = Uri.parse(SOUND_PATH + R.raw.microwave_beep);
    private static long[] vibrationPattern = new long[] {0L, 100L, 50L};
    private static final int NOTIFICATION_ID = R.layout.main;
    
	/**
	 * @see android.content.BroadcastReceiver#onReceive(Context,Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
	    WakeLocker.acquire(context, "NoodlesTimerAlarmer");

	    sendNotification(context);
	    
	    Toast.makeText(context, R.string.notification_timer_complete_text, Toast.LENGTH_SHORT).show();
	    
	    // LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
	    Intent newIntent = new Intent(NoodlesMaster.NOODLES_TIMER_COMPLETE);
	    newIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    // PendingIntent newPendingIntent = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		// localBroadcastManager.sendBroadcastSync(newIntent);
	    context.sendBroadcast(newIntent);
        
		WakeLocker.release();
	}
	
	private void sendNotification(Context context) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(context.getString(R.string.notification_timer_complete_title))
                .setContentText(context.getString(R.string.notification_timer_complete_text))
                .setSound(getSound(context))
                .setAutoCancel(true);
        
        if (isVibrationEnabled(context)) {
            mBuilder.setVibrate(vibrationPattern);
        }
        
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, NoodlesMaster.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent resultPendingIntent = 
                PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        
        NotificationManager mNotificationManager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.getNotification());
    }
	
	private Uri getSound(Context context) {
	    Uri sound = null;
	    
	    SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
	    String ringtonePref = preference.getString("ringtone", DEFAULT_SOUND_PREFERENCE_KEY);
	    if (DEFAULT_SOUND_PREFERENCE_KEY.equals(ringtonePref)) {
	        sound = defaultSoundUri;
	    } else {
	        sound = Uri.parse(ringtonePref);
	    }
	    
	    Log.d(TAG, "About to play notification sound: " + sound.toString());
	    return sound;
	}
	
    private boolean isVibrationEnabled(Context context) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        return preference.getBoolean("vibration", false);
    }
}
