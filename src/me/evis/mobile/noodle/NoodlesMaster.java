package me.evis.mobile.noodle;

import com.android.internal.widget.NumberPicker;

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
import android.widget.ViewSwitcher;

public class NoodlesMaster extends Activity {
	
	private static final String KEY_NOODLE_NAME = "noodleName";
	private static final String KEY_NOODLE_TIME = "noodleTime";
	
	// Progress counter interval
	private static final int COUNTER_INTERVAL_SECS = 1;
	
	// Keep the track so that scheduled work can be 
	// stopped by user.
	protected Handler counterHandler;
	protected Runnable alarmRunner;
	// According to user input.
	protected int totalSecs;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // If no data was given in the intent (because we were started
        // as a MAIN activity), then use our default content provider.
        Intent intent = getIntent();
        if (intent.getStringExtra(KEY_NOODLE_NAME) == null) {
            intent.putExtra(KEY_NOODLE_NAME, "传统方便面");
            intent.putExtra(KEY_NOODLE_TIME, 15);
        }
        
        totalSecs = 15;
        
        getStartTimerButton().setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startTimer(totalSecs);
			}
		});
		
		getStopTimerButton().setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				stopTimer();
			}
		});
		
		NumberPicker timerHourPicker = getTimerHourPicker();
		timerHourPicker.setRange(0, Integer.MAX_VALUE);
		timerHourPicker.setOnChangeListener(new NumberPicker.OnChangedListener() {
			@Override
			public void onChanged(NumberPicker picker, int oldVal, int newVal) {
				calculateTotalSecs();
			}
		});
		
		NumberPicker timerMinutePicker = getTimerMinutePicker();
		//TODO
//		timerMinutePicker.setFormatter(new NumberPicker.Formatter() {
//			@Override
//			public String toString(int value) {
//				// TODO Auto-generated method stub
//				return "";
//			}
//		});
		timerMinutePicker.setRange(0, 60);
		timerMinutePicker.setOnChangeListener(new NumberPicker.OnChangedListener() {
			@Override
			public void onChanged(NumberPicker picker, int oldVal, int newVal) {
				if (newVal == 60) {
					picker.setCurrent(0);
					NumberPicker timerHourPicker = getTimerHourPicker();
					timerHourPicker.setCurrent(timerHourPicker.getCurrent() + 1);
				}
				calculateTotalSecs();
			}
		});
		
		NumberPicker timerSecondPicker = getTimerSecondPicker();
		timerSecondPicker.setRange(0, 60);
		timerSecondPicker.setOnChangeListener(new NumberPicker.OnChangedListener() {
			@Override
			public void onChanged(NumberPicker picker, int oldVal, int newVal) {
				if (newVal == 60) {
					picker.setCurrent(0);
					NumberPicker timerMinutePicker = getTimerMinutePicker();
					timerMinutePicker.setCurrent(timerMinutePicker.getCurrent() + 1);
				}
				calculateTotalSecs();
			}
		});
    }
    
//    private void setAlarm(int secs) {
//    	Intent intent = new Intent(this, NoodlesAlarmer.class);
//    	PendingIntent pendingIntent = 
//    		PendingIntent.getBroadcast(this, 0, intent, 0);
//    	
//    }
    
	protected void startTimer(int secs) {
		// Hide timer editor and show timer.
		getTimerView().showNext();
		// Disable to avoid multiple timers in one time.
		getStartTimerButton().setEnabled(false);
		// Setup counter.
		counterHandler = new Handler() {
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
		Message msg = counterHandler.obtainMessage();
		msg.arg1 = 0;
		msg.arg2 = secs;
		msg.obj = this;
		counterHandler.sendMessage(msg);
		// Setup alarm.
		alarmRunner = new AlarmRunner(this);
		counterHandler.postDelayed(alarmRunner, secs * 1000);
	}
	
	protected void stopTimer() {
		counterHandler.removeCallbacksAndMessages(this);
		getTimerView().showNext();
	}
	
	protected void calculateTotalSecs() {
		int hour = getTimerHourPicker().getCurrent();
		int minute = getTimerMinutePicker().getCurrent();
		int second = getTimerSecondPicker().getCurrent();
		totalSecs = hour * 3600 + minute * 60 + second;
	}
	
	private Button getStartTimerButton() {
		return (Button) findViewById(R.id.StartTimer);
	}
	private Button getStopTimerButton() {
		return (Button) findViewById(R.id.StopTimer);
	}
	private NumberPicker getTimerHourPicker() {
		return (NumberPicker) findViewById(R.id.TimerHourPicker);
	}
	private NumberPicker getTimerMinutePicker() {
		return (NumberPicker) findViewById(R.id.TimerMinutePicker);
	}
	private NumberPicker getTimerSecondPicker() {
		return (NumberPicker) findViewById(R.id.TimerSecondPicker);
	}
	private ViewSwitcher getTimerView() {
		return (ViewSwitcher) findViewById(R.id.TimerView);
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
			getStartTimerButton().setEnabled(true);
			// TODO verify necessary of this.
//			stopTimer();
		}
	}
}