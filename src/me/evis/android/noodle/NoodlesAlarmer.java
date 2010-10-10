package me.evis.android.noodle;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

public class NoodlesAlarmer extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		final Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
		ringtone.play();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(R.string.noodle_ready)
		       .setCancelable(false)
		       .setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							ringtone.stop();
						}
					});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

}
