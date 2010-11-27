package me.evis.mobile.noodle;

import me.evis.mobile.noodle.provider.ManufacturerContentProvider;
import me.evis.mobile.noodle.provider.NoodlesContentProvider;
import me.evis.mobile.util.AssetUtil;
import me.evis.mobile.util.DateTimeUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Toast;

import com.android.internal.widget.NumberPicker;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class NoodlesMaster extends Activity {
	
	private static final String[] projection = {
		"noodles." + NoodlesContentProvider._ID,   // 0 
		NoodlesContentProvider.BRAND_ID,       // 1
		NoodlesContentProvider.NAME,           // 2
		NoodlesContentProvider.NET_WEIGHT,     // 3
		NoodlesContentProvider.NOODLES_WEIGHT, // 4
		NoodlesContentProvider.STEP_1_ID,      // 5
		NoodlesContentProvider.STEP_2_ID,      // 6
		NoodlesContentProvider.STEP_3_ID,      // 7
		NoodlesContentProvider.STEP_4_ID,      // 8
		NoodlesContentProvider.SOAKAGE_TIME,   // 9
		NoodlesContentProvider.DESCRIPTION,    // 10
		NoodlesContentProvider.LOGO            // 11
	};
    
    private static final int MESSAGE_WHAT_CODE = 0;
	
	private static final int DIALOG_TIME_PICKER = 1;
	
	// Menu item id
	private static final int MENU_ITEM_LIST = Menu.FIRST;
	
	// Progress counter interval
	private static final int COUNTER_INTERVAL_SECS = 1;
	
	// Keep the track so that scheduled work can be 
	// stopped by user.
	protected Handler counterHandler;
	// protected Handler alarmHandler;
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
            intent.setData(ContentUris.withAppendedId(NoodlesContentProvider.ID_FIELD_CONTENT_URI, 1));
        }
        Noodles noodles = retrieveNoodlesDetail(intent.getData());
		displayNoodlesDetail(noodles);
		
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
		
		// Browse button behavior.
		getBrowseButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				browseNoodles();
			}
		});
		
		// Capture button behavior.
		getCaptureButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IntentIntegrator.initiateScan(NoodlesMaster.this);
			}
		});
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
                browseNoodles();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Handles Barcode scanner result.
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
    	if (scanResult != null) {
    		// Handle the barcode returned by zxing.
    		Toast.makeText(this, scanResult.getContents(), Toast.LENGTH_LONG);
    		startActivity(new Intent(Intent.ACTION_VIEW, 
    				Uri.parse(NoodlesContentProvider.CODE_FIELD_CONTENT_URI.toString() + "/" + scanResult.getContents())));
    	}
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
    /**
     * Launch activity to list the noodles.
     */
    protected void browseNoodles() {
    	startActivity(new Intent(Intent.ACTION_VIEW, ManufacturerContentProvider.CONTENT_URI));
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
    	Noodles noodles = new Noodles();
        
    	Cursor cursor = managedQuery(uri, projection, 
    			null, null, "name ASC");
    	if (cursor.getCount() > 0 )
    	{
    		cursor.moveToFirst();
    		noodles.id = cursor.getLong(0);
    		noodles.brand = cursor.getString(1);
    		noodles.name = cursor.getString(2);
    		noodles.soakageTime = cursor.getInt(9);
    		noodles.description = cursor.getString(10);
    	}
    	
        return noodles;
    }
    
    private void displayNoodlesDetail(Noodles noodles) {
    	((TextView) findViewById(R.id.NoodleName)).setText(noodles.name);
    	((TextView) findViewById(R.id.NoodleDescription)).setText(noodles.description);
    	
    	prepareLogo();
    	prepareSteps();
    }
    
    /**
     * Initialize noodle's logo.
     */
    private void prepareLogo() {
        ImageView noodleLogo = (ImageView) findViewById(R.id.NoodleLogo);
        AssetUtil.setAssetImage(noodleLogo, "logos/masterkong.png");
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
        AssetUtil.setAssetImage(stepIcon, "step_icons/step" + stepNumber + "_icon.png");
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
    private ImageButton getBrowseButton() {
        return (ImageButton) findViewById(R.id.BrowseButton);
    }
    private ImageButton getCaptureButton() {
        return (ImageButton) findViewById(R.id.CaptureButton);
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