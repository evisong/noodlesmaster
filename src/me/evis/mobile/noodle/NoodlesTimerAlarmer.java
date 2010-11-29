package me.evis.mobile.noodle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NoodlesTimerAlarmer extends BroadcastReceiver {
	/**
	 * @see android.content.BroadcastReceiver#onReceive(Context,Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		context.sendBroadcast(new Intent(NoodlesMaster.NOODLES_TIMER_COMPLETE));
	}
}
