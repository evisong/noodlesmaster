package me.evis.mobile.noodle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class NoodlesTimerAlarmer extends BroadcastReceiver {
	/**
	 * @see android.content.BroadcastReceiver#onReceive(Context,Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
	    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	    WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "NoodlesTimerAlarmer");
	    wl.acquire();

		context.sendBroadcast(new Intent(NoodlesMaster.NOODLES_TIMER_COMPLETE));
        
		wl.release();
	}
}
