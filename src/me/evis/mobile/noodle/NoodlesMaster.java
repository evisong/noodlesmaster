package me.evis.mobile.noodle;

import java.io.IOException;
import java.io.InputStream;

import me.evis.mobile.noodle.provider.NoodlesContentProvider;
import me.evis.mobile.util.DateTimeUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.internal.widget.NumberPicker;

public class NoodlesMaster extends Activity {
	
    private static final String AUTHORITY = "me.evis.mobile.noodle";
    private static final String TIME = "time";
    private static final String CODE = "code";
    private static final Uri NOODLES_URI = 
        Uri.parse("noodles://" + AUTHORITY + "/noodles");
    private static final Uri DEFAULT_NOODLES_URI = 
        Uri.parse("noodles://" + AUTHORITY + "/noodles/0?" + TIME + "=10");
    
    private static final int MESSAGE_WHAT_CODE = 0;
	
	private static final int DIALOG_TIME_PICKER = 1;
	
	// Menu item id
	private static final int MENU_ITEM_LIST = Menu.FIRST;
	
	// Progress counter interval
	private static final int COUNTER_INTERVAL_SECS = 1;
	
	// Keep the track so that scheduled work can be 
	// stopped by user.
	protected Handler counterHandler;
//	protected Handler alarmHandler;
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
        if (intent.getData() == null) {
            intent.setData(DEFAULT_NOODLES_URI);
        }
        Noodles noodles = retrieveNoodlesDetail(intent.getData());
        
        totalSecs = noodles.soakageTime;
        updateTimer();
        
        // StartTimer button behavior.
        getStartTimerButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				startTimer(totalSecs);
			}
		});
		
        // StopTimer button behavior.
		getStopTimerButton().setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
				stopTimer();
			}
		});
		
        // AdjustTimer button behavior.
		getAdjustTimerButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_TIME_PICKER);
            }
        });
		
		prepareLogo();
		prepareSteps();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // This is our one standard application action -- inserting a
        // new note into the list.
        menu.add(0, MENU_ITEM_LIST, 0, R.string.menu_list)
                .setShortcut('3', 'a')
                .setIcon(android.R.drawable.ic_menu_view);

        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_LIST:
                // Launch activity to list the noodles.
                startActivity(new Intent(Intent.ACTION_VIEW, NoodlesContentProvider.CONTENT_URI));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_TIME_PICKER:
            return new Dialog(this) {
                @Override
                protected void onCreate(Bundle savedInstanceState) {
                    setTitle(R.string.adjust_timer);
                    setContentView(R.layout.time_picker);
                    
                    final int[] totalDhms = DateTimeUtil.calculateDhms(totalSecs);
                    NumberPicker timerHourPicker = getTimerHourPicker();
                    timerHourPicker.setRange(0, Integer.MAX_VALUE);
                    timerHourPicker.setCurrent(totalDhms[1]);
                    timerHourPicker.setOnChangeListener(new NumberPicker.OnChangedListener() {
                        @Override
                        public void onChanged(NumberPicker picker, int oldVal, int newVal) {
                            calculateTotalSecs();
                        }
                    });
                    
                    NumberPicker timerMinutePicker = getTimerMinutePicker();
                    timerMinutePicker.setRange(0, 60);
                    timerMinutePicker.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
                    timerMinutePicker.setCurrent(totalDhms[2]);
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
                    timerSecondPicker.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
                    timerSecondPicker.setCurrent(totalDhms[3]);
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
                    
                    Button okButton = (Button) findViewById(R.id.TimePickerOk);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismiss();
                        }
                    });
                }
                
                private void calculateTotalSecs() {
                    totalSecs = DateTimeUtil.calculateSeconds(new int[] {
                            0,
                            getTimerHourPicker().getCurrent(),
                            getTimerMinutePicker().getCurrent(),
                            getTimerSecondPicker().getCurrent()
                    });
                    updateTimer();
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
            };

        default:
            return super.onCreateDialog(id);
        }
    }
    
    private Noodles retrieveNoodlesDetail(Uri uri) {
        if (uri.getQueryParameter(CODE) != null) {
            String barcode = uri.getQueryParameter(CODE);
            // TODO Search by barcode
        }
        // TODO DB.
        Noodles noodles = new Noodles();
        if (uri.getQueryParameter(TIME) != null) {
            noodles.soakageTime = Integer.parseInt(uri.getQueryParameter(TIME));
        }
        return noodles;
    }
    
    /**
     * Initialize noodle's logo.
     */
    private void prepareLogo() {
        ImageView noodleLogo = (ImageView) findViewById(R.id.NoodleLogo);
        InputStream is = null;
        try {
            is = getAssets().open("logos/masterkong.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        noodleLogo.setImageBitmap(bitmap);
    }
    
    /**
     * Initialize noodle's steps, icons and description.
     */
    private void prepareSteps() {
        prepareStep(new int[] {R.id.Step1, R.id.Step2, R.id.Step3, R.id.Step4});
    }
    
    private void prepareStep(int[] stepIds) {
        for (int i = 0; i < 4; i++ ) {
            prepareStep(i + 1, stepIds[i]);
        }
    }
    
    private void prepareStep(int stepNumber, int stepId) {
        // Step container.
        RelativeLayout step = (RelativeLayout) findViewById(stepId);
        // Set step number.
        TextView stepNumberText = (TextView) step.findViewById(R.id.StepNumber);
        stepNumberText.setText(String.valueOf(stepNumber));
        // Set step icon.
        ImageView stepIcon = (ImageView) step.findViewById(R.id.StepIcon);
        InputStream is = null;
        try {
            is = getAssets().open("step_icons/step" + stepNumber + "_icon.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        stepIcon.setImageBitmap(bitmap);
        // Set step description.
        TextView stepDesc= (TextView) step.findViewById(R.id.StepDesc);
        stepDesc.setText("这是泡面第" + stepNumber + "步");
    }
    
//    private void setAlarm(int secs) {
//    	Intent intent = new Intent(this, NoodlesAlarmer.class);
//    	PendingIntent pendingIntent = 
//    		PendingIntent.getBroadcast(this, 0, intent, 0);
//    	
//    }
    
	protected void startTimer(int secs) {
		// Disable to avoid multiple timers in one time.
		getStartTimerButton().setEnabled(false);
		getTimerProgress().setMax(totalSecs);
		// Setup counter.
		counterHandler = new Handler() {
			public void handleMessage(Message msg) {
				int currentSec = msg.arg1;
				int totalSec = msg.arg2;
				updateTimerCurrent(currentSec);
				
				if (currentSec < totalSec) {
					Message newMsg = Message.obtain(msg);
					newMsg.arg1 += COUNTER_INTERVAL_SECS;
					sendMessageDelayed(newMsg, COUNTER_INTERVAL_SECS * 1000);
				}
			}
		};
		Message msg = counterHandler.obtainMessage();
		msg.what = MESSAGE_WHAT_CODE;
		msg.arg1 = 0;
		msg.arg2 = secs;
		msg.obj = this;
		counterHandler.sendMessage(msg);
		// Setup alarm.
//		alarmHandler = new Handler();
		alarmRunner = new AlarmRunner(this);
//		alarmHandler.postDelayed(alarmRunner, secs * 1000);
		counterHandler.postDelayed(alarmRunner, secs * 1000);
	}
	
	protected void stopTimer() {
		counterHandler.removeMessages(MESSAGE_WHAT_CODE);
		counterHandler.removeCallbacks(alarmRunner);
//		alarmHandler.removeCallbacksAndMessages(this);
		
		updateTimerCurrent(0);
		getStartTimerButton().setEnabled(true);
	}
	
	private void updateTimerCurrent(int currentSec) {
	    final int[] currentDhms = DateTimeUtil.calculateDhms(currentSec);
	    ((TextView) findViewById(R.id.TimerCurrentHour)).setText(String.valueOf(currentDhms[1]));
	    ((TextView) findViewById(R.id.TimerCurrentMinute)).setText(formatNumber(currentDhms[2]));
	    ((TextView) findViewById(R.id.TimerCurrentSecond)).setText(formatNumber(currentDhms[3]));
	    getTimerProgress().setProgress(currentSec);
	}
	
	private void updateTimer() {
        final int[] dhms = DateTimeUtil.calculateDhms(totalSecs);
        ((TextView) findViewById(R.id.TimerHour)).setText(String.valueOf(dhms[1]));
        ((TextView) findViewById(R.id.TimerMinute)).setText(formatNumber(dhms[2]));
        ((TextView) findViewById(R.id.TimerSecond)).setText(formatNumber(dhms[3]));
    }
	
	private String formatNumber(int value) {
	    return NumberPicker.TWO_DIGIT_FORMATTER.toString(value);
	}
	
    private Button getStartTimerButton() {
        return (Button) findViewById(R.id.StartTimerButton);
    }
    private Button getStopTimerButton() {
        return (Button) findViewById(R.id.StopTimerButton);
    }
    private ImageButton getAdjustTimerButton() {
        return (ImageButton) findViewById(R.id.AdjustTimerButton);
    }
    private ProgressBar getTimerProgress() {
        return (ProgressBar) this.findViewById(R.id.TimerProgress);
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