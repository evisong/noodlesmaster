package me.evis.mobile.noodle;

import me.evis.mobile.noodle.provider.ManufacturerContentProvider;
import me.evis.mobile.noodle.provider.NoodlesContentProvider;
import me.evis.mobile.util.AssetUtil;
import me.evis.mobile.util.DateTimeUtil;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
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
		"noodles." + NoodlesContentProvider.NAME,             // 1
		NoodlesContentProvider.NET_WEIGHT,     // 2
		NoodlesContentProvider.NOODLES_WEIGHT, // 3
		NoodlesContentProvider.SOAKAGE_TIME,   // 4
		"noodles." + NoodlesContentProvider.DESCRIPTION,    // 5
		"noodles." + NoodlesContentProvider.LOGO,           // 6
		"manufacturer.name",                   // 7
		"manufacturer.logo",                   // 8
		"step1.description",                   // 9
		"step1.icon",                          // 10
		"step2.description",                   // 11
		"step2.icon",                          // 12
		"step3.description",                   // 13
		"step3.icon",                          // 14
		"step4.description",                   // 15
		"step4.icon",                          // 16
	};
	
	private static final String LOGO_PATH = "logos/";
	private static final String STEP_ICON_PATH = "step_icons/";
    
    
    // Request code for browse
    private static final int REQUEST_CODE_BROWSE_MANUFACTURERS = 2010100901;
	
    // Dialog id
	private static final int DIALOG_TIME_PICKER = 1;
	
	// Menu item id
	private static final int MENU_ITEM_BROWSE = Menu.FIRST;
	private static final int MENU_ITEM_SCAN = Menu.FIRST + 1;
	
	
    // -----------------------------------------------------------------------
    // Timer related constants / variables
    // -----------------------------------------------------------------------
	
	/**
	 * Intent indicating noodles' time up.
	 */
	public static final String NOODLES_TIMER_COMPLETE = "me.evis.intent.action.NOODLES_TIMER_COMPLETE";
	
	// Progress counter interval
	private static final int COUNTER_INTERVAL_SECS = 1;
	private static final int MESSAGE_WHAT_CODE = 0;
	
	// Keep the track so that scheduled work can be 
	// stopped by user.
	private Handler counterHandler;
	// According to user input.
	private int totalSecs;
	
    // Receiver for NOODLES_TIMER_COMPLETE intent.
    private BroadcastReceiver timerCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
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
            
            stopTimer();
        }
    };
	
    
    // -----------------------------------------------------------------------
    // Activity methods
    // -----------------------------------------------------------------------
    
    /** 
     * Called when the activity is first created. 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // If no data was given in the intent (because we were started
        // as a MAIN activity), then use our default content provider.
        Intent intent = getIntent();
        if (intent.getData() == null) {
            intent.setData(ContentUris.withAppendedId(NoodlesContentProvider._ID_FIELD_CONTENT_URI, 1));
        }
        
        initNoodles();
        
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
		
		// Scan button behavior.
		getScanButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				scanNoodlesBarcode();
			}
		});
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
     	registerReceiver(timerCompleteReceiver, new IntentFilter(NOODLES_TIMER_COMPLETE));
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	unregisterReceiver(timerCompleteReceiver);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_ITEM_BROWSE, 0, R.string.menu_browse)
                .setShortcut('1', 'a')
                .setIcon(R.drawable.btn_browse);
        menu.add(0, MENU_ITEM_SCAN, 0, R.string.menu_scan)
                .setShortcut('2', 'b')
                .setIcon(R.drawable.btn_scan);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_BROWSE:
                browseNoodles();
                return true;
            case MENU_ITEM_SCAN:
                scanNoodlesBarcode();
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
    		Toast.makeText(this, scanResult.getContents(), Toast.LENGTH_SHORT).show();
    		Uri newIntentData = Uri.parse(NoodlesContentProvider.CODE_FIELD_CONTENT_URI.toString() + "/" + scanResult.getContents());
    		getIntent().setData(newIntentData);
    		initNoodles();
    	}
    	
    	if (requestCode == REQUEST_CODE_BROWSE_MANUFACTURERS && resultCode == RESULT_OK) {
    		getIntent().setData(data.getData());
    		initNoodles();
    	}
    	
    	super.onActivityResult(requestCode, resultCode, data);
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
    
    
    /**
     * Launch activity to browse the noodles.
     */
    protected void browseNoodles() {
        startActivityForResult(new Intent(Intent.ACTION_VIEW, ManufacturerContentProvider.CONTENT_URI), REQUEST_CODE_BROWSE_MANUFACTURERS);
    }
    /**
     * Call BarcodeScanner for noodles barcode.
     */
    protected void scanNoodlesBarcode() {
        IntentIntegrator.initiateScan(NoodlesMaster.this);
    }
    
    /**
     * 
     */
    protected void initNoodles() {
        if (Log.isLoggable(getClass().getSimpleName(), Log.DEBUG)) {
            Log.d(getClass().getSimpleName(), "Noodles URI to query: " + getIntent().getData());
        }
        
        Noodles noodles = new Noodles();
        
        Cursor cursor = managedQuery(getIntent().getData(), projection, 
                null, null, "noodles.name ASC");
        
        if (cursor.getCount() > 0 )
        {
            cursor.moveToFirst();
            noodles.id = cursor.getLong(0);
            noodles.name = cursor.getString(1);
            noodles.soakageTime = cursor.getInt(4);
            noodles.description = cursor.getString(5);
            noodles.logo = cursor.getString(6);
            noodles.manufacturerName = cursor.getString(7);
            noodles.manufacturerLogo = cursor.getString(8);
            noodles.step1Description = cursor.getString(9);
            noodles.step1IconUrl = cursor.getString(10);
            noodles.step2Description = cursor.getString(11);
            noodles.step2IconUrl = cursor.getString(12);
            noodles.step3Description = cursor.getString(13);
            noodles.step3IconUrl = cursor.getString(14);
            noodles.step4Description = cursor.getString(15);
            noodles.step4IconUrl = cursor.getString(16);
        }
        
        // Noodle's name, description
        ((TextView) findViewById(R.id.NoodleName)).setText(noodles.name);
        ((TextView) findViewById(R.id.NoodleDescription)).setText(noodles.description);
        
        // Logo
        ImageView noodleLogo = (ImageView) findViewById(R.id.NoodleLogo);
        AssetUtil.setAssetImage(noodleLogo, LOGO_PATH, noodles.manufacturerLogo);
        
        // Steps
        prepareStep(R.id.Step1, "1", noodles.step1Description, noodles.step1IconUrl);
        prepareStep(R.id.Step2, "2", noodles.step2Description, noodles.step2IconUrl);
        prepareStep(R.id.Step3, "3", noodles.step3Description, noodles.step3IconUrl);
        prepareStep(R.id.Step4, "4", noodles.step4Description, noodles.step4IconUrl);
        
        // Timer
        totalSecs = noodles.soakageTime;
        updateTimer();
    }

    private void prepareStep(int id, String stepNumber, String desc, String icon) {
    	// Step container.
        RelativeLayout step = (RelativeLayout) findViewById(id);
        // Set step number.
        TextView stepNumberText = (TextView) step.findViewById(R.id.StepNumber);
        stepNumberText.setText(stepNumber);
        // Set step icon.
        ImageView stepIcon = (ImageView) step.findViewById(R.id.StepIcon);
        AssetUtil.setAssetImage(stepIcon, STEP_ICON_PATH, icon);
        // Set step description.
        TextView stepDesc= (TextView) step.findViewById(R.id.StepDesc);
        stepDesc.setText(desc);
    }
    
    // -----------------------------------------------------------------------
    // Timer logics
    // -----------------------------------------------------------------------
    
    protected PendingIntent alarmSender;
    
	protected void startTimer(int secs) {
		// Disable to avoid multiple timers in one time.
		getStartTimerButton().setEnabled(false);
		getStopTimerButton().setEnabled(true);
		getTimerProgress().setMax(secs);
		// Setup counter.
		Intent intent = new Intent(this, NoodlesTimerAlarmer.class);
		alarmSender = PendingIntent.getBroadcast(this, 0, intent, 0);
		
		long startMillisecs = SystemClock.elapsedRealtime();
		long alarmMillisecs = startMillisecs + secs * 1000;
		
		counterHandler = new Handler() {
			public void handleMessage(Message msg) {
				Long[] times = (Long[]) msg.obj;
				long _startMillisecs = times[0];
				long _alarmMillisecs = times[1];
				long _currentMillisecs = SystemClock.elapsedRealtime();
				
				int _totalSecs = (int) (_alarmMillisecs - _startMillisecs) / 1000;
				int _currentSecs = (int) (_currentMillisecs - _startMillisecs) / 1000;
				
				updateTimerCurrent(_currentSecs);
				
				if (_currentSecs < _totalSecs) {
					Message newMsg = Message.obtain(msg);
					sendMessageDelayed(newMsg, COUNTER_INTERVAL_SECS * 1000);
				}
			}
		};
		Message msg = counterHandler.obtainMessage();
		msg.what = MESSAGE_WHAT_CODE;
		msg.obj = new Long[] {startMillisecs, alarmMillisecs};
		counterHandler.sendMessage(msg);
		
		// Setup alarm. AlarmManager must be used instead of handler or other 
		// scheduled ways in this scenario, since AlarmManager is the only one 
		// guaranteed active when the phone goes sleep. All other schedulers 
		// will be paused during the standby.
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, alarmMillisecs, alarmSender);
	}
	
	protected void stopTimer() {
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.cancel(alarmSender);
		counterHandler.removeMessages(MESSAGE_WHAT_CODE);
		
		updateTimerCurrent(0);
		getStartTimerButton().setEnabled(true);
		getStopTimerButton().setEnabled(false);
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
	
    // -----------------------------------------------------------------------
    // Widget getters
    // -----------------------------------------------------------------------
	
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
    private ImageButton getScanButton() {
        return (ImageButton) findViewById(R.id.ScanButton);
    }
    private ProgressBar getTimerProgress() {
        return (ProgressBar) this.findViewById(R.id.TimerProgress);
    }
}