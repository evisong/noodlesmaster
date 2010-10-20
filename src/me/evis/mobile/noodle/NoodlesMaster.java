package me.evis.mobile.noodle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class NoodlesMaster extends Activity {
	
	private static final String KEY_NOODLE_NAME = "noodleName";
	private static final String KEY_NOODLE_TIME = "noodleTime";
	
	// Progress counter interval
	private static final int COUNTER_INTERVAL_SECS = 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // If no data was given in the intent (because we were started
        // as a MAIN activity), then use our default content provider.
        Intent intent = getIntent();
        if (intent.getStringExtra(KEY_NOODLE_NAME) == null) {
            intent.putExtra(KEY_NOODLE_NAME, "传统方便面");
            intent.putExtra(KEY_NOODLE_TIME, 15);
        }
        
        setContentView(R.layout.main);
        Button button = (Button) findViewById(R.id.Button01);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setAlarm(5);
			}
		});
		
		Button button2 = (Button) findViewById(R.id.Button02);
		button2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setAlarm(10);
			}
		});
    }
    
//    private void setAlarm(int secs) {
//    	Intent intent = new Intent(this, NoodlesAlarmer.class);
//    	PendingIntent pendingIntent = 
//    		PendingIntent.getBroadcast(this, 0, intent, 0);
//    	
//    }
	
	private Handler counterHandler = new Handler() {
		public void handleMessage(Message msg) {
			int currentSec = msg.arg1;
			int totalSec = msg.arg2;
			setTimingProgress(currentSec, totalSec);
			
			if (currentSec < totalSec) {
				Message newMsg = Message.obtain(msg);
				newMsg.arg1 += COUNTER_INTERVAL_SECS;
				sendMessageDelayed(newMsg, COUNTER_INTERVAL_SECS * 1000);
			}
		}
	};
    
	private void setAlarm(int secs) {
		// Set up counter.
		Message msg = counterHandler.obtainMessage();
		msg.arg1 = 0;
		msg.arg2 = secs;
		counterHandler.sendMessage(msg);
		
		new Handler().postDelayed(new AlarmRunner(this), secs * 1000);
	}
	
	private void setTimingProgress(int currentSec, int totalSec) {
		final ProgressBar progressBar = 
			(ProgressBar)this.findViewById(R.id.ProgressBar01);
		progressBar.setProgress(currentSec);
		progressBar.setMax(totalSec);
		
		final TextView textView = 
			(TextView)this.findViewById(R.id.TimerSecond);
		textView.setText(currentSec + "/" + totalSec);
	}
	
	private class AlarmRunner implements Runnable {
		private Context context;
		
		public AlarmRunner(Context context) {
			this.context = context;
		}
		
		public void run() {
			Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
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
}