package me.evis.mobile.noodle;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.evis.mobile.noodle.product.Product;
import me.evis.mobile.noodle.product.QueryProductByEanTask;
import me.evis.mobile.noodle.product.QueryProductByEanTask.OnFailureListener;
import me.evis.mobile.noodle.product.QueryProductByEanTask.OnSuccessListener;
import me.evis.mobile.noodle.scan.ScanIntentIntegrator;
import me.evis.mobile.noodle.scan.ScanIntentResult;
import me.evis.mobile.noodle.share.WeiboShareProvider;
import me.evis.mobile.util.AdsUtil;
import me.evis.mobile.util.DateTimeUtil;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.internal.widget.NumberPicker;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboShareException;

public class NoodlesMaster extends ActionBarActivity implements IWeiboHandler.Response {
    private static final String TAG = "NoodlesMaster";

    private static final String PREF_APP_REGISTERED = "appRegisteredOnInternet";
    private static final int DIALOG_TIME_PICKER = 1;
    
    // -----------------------------------------------------------------------
    // Weibo related constants / variables
    // -----------------------------------------------------------------------

    /** Weibo Share */
    private IWeiboShareAPI mWeiboShareAPI;
	
    // -----------------------------------------------------------------------
    // Timer related constants / variables
    // -----------------------------------------------------------------------
	
	/**
	 * Intent indicating noodles' time up.
	 */
	public static final String NOODLES_TIMER_COMPLETE = "me.evis.intent.action.NOODLES_TIMER_COMPLETE";
	public static final String URI_SCHEME = "me.evis.mobile.noodle";
	public static final String URI_SEGMENT_START = "start";
	public static final String START_TIMER_URI = URI_SCHEME + "://n.evis.me/" + URI_SEGMENT_START + "/{}";
	
	private static final int DEFAULT_TOTAL_SECS = 3 * 60;
	
	// Progress counter interval
	private static final int COUNTER_INTERVAL_MILLISECS = 100;
	private static final int MESSAGE_WHAT_CODE = 0;
	
	// Keep the track so that scheduled work can be 
	// stopped by user.
	private Handler counterHandler;
	// According to user input.
	private int totalSecs;
	/**
	 * Is timer running.
	 */
	private boolean timerRunning = false;
	
    // Receiver for NOODLES_TIMER_COMPLETE intent.
    private BroadcastReceiver timerCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "timerCompleteReceiver.onReceive");
            
            PowerManager pm = (PowerManager) NoodlesMaster.this.getSystemService(Context.POWER_SERVICE);
            // TODO bug: screen won't turn on
            WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "NoodlesTimerAlarmer");
            wl.acquire();
            
            AlertDialog.Builder builder = new AlertDialog.Builder(NoodlesMaster.this);
            builder.setMessage(R.string.noodles_ready)
                   .setCancelable(false)
                   .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //ringtone.stop();
                            }
                        });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            
            stopTimer(true);
            
            wl.release();
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
        Log.v(TAG, "NoodlesMaster.onCreate");
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        registerApp();
        
        initWeibo();
        
        // Prepare actionbar
        ActionBar actionBar = getSupportActionBar();
        
        setTimerTotalSecs(DEFAULT_TOTAL_SECS);
        
        getStopTimerButton().setEnabled(false);
        // Workaround view.setScaleX() is since API Level 11, use opensouce lib to provide initial value.
        ObjectAnimator.ofFloat(getStopTimerButton(), "scaleX", 1f, 0f).setDuration(1).start();
        // StartTimer buttons behavior.
        getStartTimerButton01().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int secs = 3 * 60;
                setTimerTotalSecs(secs);
				startTimer(secs);
				// Track the click
				EasyTracker.getInstance(NoodlesMaster.this).send(MapBuilder
				        .createEvent(TrackerEvent.CATEGORY_UI, 
				                     TrackerEvent.ACTION_BUTTON, 
				                     "noodlesMaster_StartTimerButton01", 
				                     Long.valueOf(secs))
				        .build());
			}
		});
        getStartTimerButton02().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int secs = 3 * 60 + 30;
                setTimerTotalSecs(secs);
                startTimer(secs);
                // Track the click
                EasyTracker.getInstance(NoodlesMaster.this).send(MapBuilder
                        .createEvent(TrackerEvent.CATEGORY_UI, 
                                     TrackerEvent.ACTION_BUTTON, 
                                     "noodlesMaster_StartTimerButton02", 
                                     Long.valueOf(secs))
                        .build());
            }
        });
        getStartTimerButton03().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int secs = 4 * 60;
                setTimerTotalSecs(secs);
                startTimer(secs);
                // Track the click
                EasyTracker.getInstance(NoodlesMaster.this).send(MapBuilder
                        .createEvent(TrackerEvent.CATEGORY_UI, 
                                     TrackerEvent.ACTION_BUTTON, 
                                     "noodlesMaster_StartTimerButton03", 
                                     Long.valueOf(secs))
                        .build());
            }
        });
        getStartTimerButton04().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int secs = 4 * 60 + 30;
                setTimerTotalSecs(secs);
                startTimer(secs);
                // Track the click
                EasyTracker.getInstance(NoodlesMaster.this).send(MapBuilder
                        .createEvent(TrackerEvent.CATEGORY_UI, 
                                     TrackerEvent.ACTION_BUTTON, 
                                     "noodlesMaster_StartTimerButton04", 
                                     Long.valueOf(secs))
                        .build());
            }
        });
        getStartTimerButton05().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int secs = 5 * 60;
                setTimerTotalSecs(secs);
                startTimer(secs);
                // Track the click
                EasyTracker.getInstance(NoodlesMaster.this).send(MapBuilder
                        .createEvent(TrackerEvent.CATEGORY_UI, 
                                     TrackerEvent.ACTION_BUTTON, 
                                     "noodlesMaster_StartTimerButton05", 
                                     Long.valueOf(secs))
                        .build());
            }
        });
		
        // AdjustTimer button behavior.
        getAdjustTimerButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_TIME_PICKER);
            }
        });
        
        // StopTimer button behavior.
		getStopTimerButton().setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
				stopTimer(true);
                // Track the click
                EasyTracker.getInstance(NoodlesMaster.this).send(MapBuilder
                        .createEvent(TrackerEvent.CATEGORY_UI, 
                                     TrackerEvent.ACTION_BUTTON, 
                                     "noodlesMaster_StopTimerButton", 
                                     null)
                        .build());
			}
		});
		
		// Menu button behavior.
		getMenuButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//openOptionsMenu();
			    doStartPref();
			}
		});
		
	    // Scan button behavior.
        getScanButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doScan();
            }
        });
        
        // Share button behavior.
        getShareButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doShareWB();
            }
        });
    }

    @Override
    protected void onStart() {
        Log.v(TAG, "NoodlesMaster.onStart");
        
        super.onStart();
        Log.d(TAG, "register inner Receiver for me.evis.intent.action.NOODLES_TIMER_COMPLETE");
        registerReceiver(timerCompleteReceiver, new IntentFilter(NOODLES_TIMER_COMPLETE));
        // start Google Analytics
        EasyTracker.getInstance(this).activityStart(this);
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        Log.v(TAG, "NoodlesMaster.onNewIntent");
        
        super.onNewIntent(intent);
        
        if (WeiboShareProvider.isWeiboCallbackIntent(intent)) {
            mWeiboShareAPI.handleWeiboResponse(intent, this);
        }
        
        if (isStartTimerByShortcut(intent)) {
            if (timerRunning) {
                Toast.makeText(this, R.string.timer_already_running, Toast.LENGTH_LONG).show();
            } else {
                setIntent(intent);
            }
        }
    }
    
    @Override
    public void onResponse(BaseResponse baseResp) {
        switch (baseResp.errCode) {
        case WBConstants.ErrorCode.ERR_OK:
            Toast.makeText(NoodlesMaster.this, R.string.weibosdk_toast_share_success, Toast.LENGTH_LONG).show();
            // Hide Ad once shared
            hideAd();
            break;
        case WBConstants.ErrorCode.ERR_CANCEL:
            Toast.makeText(NoodlesMaster.this, R.string.weibosdk_toast_share_canceled, Toast.LENGTH_LONG).show();
            break;
        case WBConstants.ErrorCode.ERR_FAIL:
            Toast.makeText(NoodlesMaster.this, 
                    getString(R.string.weibosdk_toast_share_failed) + "Error Message: " + baseResp.errMsg, 
                    Toast.LENGTH_LONG).show();
            break;
        }
    }
    
    @Override
    public void onPause() {
        Log.v(TAG, "NoodlesMaster.onPause");
        getAdView().pause();
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        Log.v(TAG, "NoodlesMaster.onResume");
        
        getAdView().resume();
        super.onResume();
        
        if (isStartTimerByShortcut(getIntent()) && !timerRunning) {
            startTimerByShortcut();
        }
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "NoodlesMaster.onStop");
        
        super.onStop();
        
        Log.d(TAG, "unregister inner Receiver for me.evis.intent.action.NOODLES_TIMER_COMPLETE");
        unregisterReceiver(timerCompleteReceiver);
        // stop Google Analytics
        EasyTracker.getInstance(this).activityStop(this); 
    }
    
    @Override
    protected void onDestroy() {
        Log.v(TAG, "NoodlesMaster.onDestroy");
        getAdView().destroy();
        super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                doScan();
                return true;
                
            case R.id.menu_preference:
                doStartPref();
                return true;
                
            case R.id.menu_about:
                Intent aboutIntent = new Intent(this, About.class);
                startActivity(aboutIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_TIME_PICKER:
            return new Dialog(this, true, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    getAdjustTimerButton().setChecked(false);
                }
            }) {
                @Override
                protected void onCreate(Bundle savedInstanceState) {
                    getTimerProgress().invalidate();
                    setTitle(R.string.adjust_timer);
                    setContentView(R.layout.time_picker);
                    
                    final int[] totalDhms = DateTimeUtil.calculateDhms(getTimerTotalSecs());
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
                    timerMinutePicker.setRange(0, 59);
                    timerMinutePicker.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
                    timerMinutePicker.setCurrent(totalDhms[2]);
                    timerMinutePicker.setOnChangeListener(new NumberPicker.OnChangedListener() {
                        @Override
                        public void onChanged(NumberPicker picker, int oldVal, int newVal) {
                            calculateTotalSecs();
                        }
                    });
                    
                    NumberPicker timerSecondPicker = getTimerSecondPicker();
                    timerSecondPicker.setRange(0, 59);
                    timerSecondPicker.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
                    timerSecondPicker.setCurrent(totalDhms[3]);
                    timerSecondPicker.setOnChangeListener(new NumberPicker.OnChangedListener() {
                        @Override
                        public void onChanged(NumberPicker picker, int oldVal, int newVal) {
                            calculateTotalSecs();
                        }
                    });
                    
                    final Button okButton = (Button) findViewById(R.id.StartTimerButton);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            calculateTotalSecs();
                            dismiss();
                            startTimer(getTimerTotalSecs());
                            
                            // Track the click
                            EasyTracker.getInstance(NoodlesMaster.this).send(MapBuilder
                                    .createEvent(TrackerEvent.CATEGORY_UI, 
                                                 TrackerEvent.ACTION_BUTTON, 
                                                 "noodlesMaster_StartTimerButton", 
                                                 Long.valueOf(getTimerTotalSecs()))
                                    .build());
                        }
                    });
                }
                
                private void calculateTotalSecs() {
                    int secs = DateTimeUtil.calculateSeconds(new int[] {
                            0,
                            getTimerHourPicker().getCurrent(),
                            getTimerMinutePicker().getCurrent(),
                            getTimerSecondPicker().getCurrent()
                    });
                    setTimerTotalSecs(secs);
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
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // No using onBackPressed() (API Level 5) in order to support API Level 4
        if (keyCode == KeyEvent.KEYCODE_BACK && timerRunning) {
            moveTaskToBack(true);
            return true; 
        }

        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        ScanIntentResult scanResult = ScanIntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            Log.d(TAG, "Scan result: " + scanResult.getContents() + "; type: " + scanResult.getFormatName());
            
            EasyTracker.getInstance(this).send(MapBuilder
                    .createEvent(TrackerEvent.CATEGORY_SCAN, 
                                 TrackerEvent.ACTION_SCAN_RESULT, 
                                 scanResult.getContents(), 
                                 null)
                    .build());
            
            new QueryProductByEanTask(this, 
                    new OnSuccessListener() {
                        @Override
                        public void onSuccess(Product product) {
                            Log.d(TAG, "Query success: " + product.getName());
                            getSupportActionBar().setSubtitle(product.getName());
                            
                            EasyTracker.getInstance(NoodlesMaster.this).send(MapBuilder
                                    .createEvent(TrackerEvent.CATEGORY_SCAN, 
                                                 TrackerEvent.ACTION_PRODUCT_RESULT, 
                                                 Locale.getDefault().toString() + "|AMZN|" + product.getProductId() + "|" + product.getName(), 
                                                 null)
                                    .build());
                        }
                    }, 
                    new OnFailureListener() {
                        @Override
                        public void onFailure(String failure) {
                            Toast.makeText(NoodlesMaster.this, failure, Toast.LENGTH_SHORT).show();
                            
                            EasyTracker.getInstance(NoodlesMaster.this).send(MapBuilder
                                    .createEvent(TrackerEvent.CATEGORY_SCAN, 
                                                 TrackerEvent.ACTION_PRODUCT_FAILURE, 
                                                 failure, 
                                                 null)
                                    .build());
                        }
                    }
                ).execute(scanResult.getContents());
        }
    }
    
    // -----------------------------------------------------------------------
    // Timer logics
    // -----------------------------------------------------------------------
    
    protected PendingIntent alarmSender;
    
	protected void startTimer(final int secs) {
	    Log.i(TAG, "start timer, total seconds: " + secs);
	    
	    this.timerRunning = true;
	    playShowStopButtonAnimation();
        
		// Disable to avoid multiple timers in one time.
	    setStartTimerButtonsEnabled(false);
		getStopTimerButton().setEnabled(true);
		// Workaround: button state change will cause PieProgressBar messed up.
		getTimerProgress().invalidate();
		getTimerProgress().setMax(secs * 1000 / COUNTER_INTERVAL_MILLISECS);
		
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
				long _elapsedRealtime = SystemClock.elapsedRealtime();
				
				int _totalSecs = (int) (_alarmMillisecs - _startMillisecs) / 1000;
				long _currentMilliSecs = _elapsedRealtime - _startMillisecs;
				int _currentSecs = (int) _currentMilliSecs / 1000;
				
				updateTimerCurrent(_currentMilliSecs);
//				p.setSlices(new float[] {_currentSecs, _totalSecs - _currentSecs});
//				p.anima();
				
				if (_currentSecs < _totalSecs) {
					Message newMsg = Message.obtain(msg);
					sendMessageDelayed(newMsg, COUNTER_INTERVAL_MILLISECS);
				} else {
				    stopTimer(false);

				    // Show ads since first timer completes
		            showAd();
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
		// Add 1s to ensure last message is handled.
		am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, alarmMillisecs + 1000, alarmSender);
	}
	
    protected void startTimerByShortcut() {
        int totalSecsParam = Integer.valueOf(getIntent().getData().getLastPathSegment());
        setTimerTotalSecs(totalSecsParam);
        startTimer(totalSecsParam);
        
        // Track the widget click
        EasyTracker.getInstance(this).send(MapBuilder
                .createEvent(TrackerEvent.CATEGORY_UI, 
                             TrackerEvent.ACTION_WIDGET, 
                             "appwidget_startTimerWidget",
                             Long.valueOf(totalSecsParam))
                .build());
    }
	
    protected void stopTimer(boolean stopAlarmAndHandler) {
        Log.i(TAG, "stop timer");
        
        if (stopAlarmAndHandler) {
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            am.cancel(alarmSender);
            if (counterHandler != null) {
                counterHandler.removeMessages(MESSAGE_WHAT_CODE);
            }
        }
        
        if (this.timerRunning) {
            this.timerRunning = false;
            // Avoid from auto-restart a completed timer (originally started by appwidget) when resumed from sleep
            setIntent(new Intent(NOODLES_TIMER_COMPLETE));
            getTimerCenterLogo().setImageResource(R.drawable.timer_center_icon2);
            playHideStopButtonAnimation();
    
    		setStartTimerButtonsEnabled(true);
    		getStopTimerButton().setEnabled(false);
    		// Workaround: button state change will cause PieProgressBar messed up.
    		getTimerProgress().invalidate();
        }
	}
	
	private void updateTimerCurrent(final long currentMilliSec) {
	    final int currentSec = (int) currentMilliSec / 1000;
	    final int[] currentDhms = DateTimeUtil.calculateDhms(currentSec);
	    ((TextView) findViewById(R.id.TimerCurrentHour)).setText(String.valueOf(currentDhms[1]));
	    ((TextView) findViewById(R.id.TimerCurrentMinute)).setText(formatNumber(currentDhms[2]));
	    ((TextView) findViewById(R.id.TimerCurrentSecond)).setText(formatNumber(currentDhms[3]));
	    getTimerProgress().setProgress((int) currentMilliSec / COUNTER_INTERVAL_MILLISECS);
	}
	
	private void setTimerTotalSecs(final int totalSecs) {
	    this.totalSecs = totalSecs; 
        final int[] dhms = DateTimeUtil.calculateDhms(totalSecs);
        ((TextView) findViewById(R.id.TimerHour)).setText(String.valueOf(dhms[1]));
        ((TextView) findViewById(R.id.TimerMinute)).setText(formatNumber(dhms[2]));
        ((TextView) findViewById(R.id.TimerSecond)).setText(formatNumber(dhms[3]));
    }
	
	private int getTimerTotalSecs() {
	    return this.totalSecs;
	}
	
	private String formatNumber(int value) {
	    return NumberPicker.TWO_DIGIT_FORMATTER.toString(value);
	}
	
	private void setStartTimerButtonsEnabled(boolean enabled) {
	    for (ToggleButton button : getStartTimerButtons()) {
	        button.setEnabled(enabled);
	        if (enabled) {
	            button.setChecked(false);
	        }
	    }
	}
	
    private boolean isStartTimerByShortcut(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null 
                && URI_SCHEME.equals(uri.getScheme()) 
                && !uri.getPathSegments().isEmpty()
                && URI_SEGMENT_START.equals(uri.getPathSegments().get(0))) {
            return true;
        }

        return false;
    }

    private void registerApp() {
        // Restore preferences
        SharedPreferences settings = 
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean registered = settings.getBoolean(PREF_APP_REGISTERED, false);
        
        if (!registered) {
            Log.i(TAG, "run app for the first time, register it on internet.");
            ApplicationInfo info;
            try {
                info = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                String channel = info.metaData.getString(getString(R.string.app_dist_channel));
                if (channel == null) {
                    channel = getString(R.string.app_dist_channel1);
                }
                
                // Track the first run after installation.
                EasyTracker.getInstance(NoodlesMaster.this).send(MapBuilder
                        .createEvent(TrackerEvent.CATEGORY_APP, 
                                     TrackerEvent.ACTION_REGISTER,
                                     channel, 
                                     null)
                        .build());
            } catch (NameNotFoundException e) {
                // Log & do nothing.
                Log.e(TAG, "cannot find applicationInfo with package name: " + getPackageName(), e);
            } finally {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(PREF_APP_REGISTERED, true);
                editor.commit();
            }
            
        }
    }
	
	/**
	 * Ensure pieProgressBar is redrawn during the center animation.
	 */
	private Animator.AnimatorListener timerProgressRefresher = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator arg0) {
            getTimerProgress().postInvalidate();
        }
        
        @Override
        public void onAnimationRepeat(Animator arg0) {
            getTimerProgress().postInvalidate();
        }
        
        @Override
        public void onAnimationEnd(Animator arg0) {
            getTimerProgress().postInvalidate();
        }
        
        @Override
        public void onAnimationCancel(Animator arg0) {
            getTimerProgress().postInvalidate();
        }
    };
    
    private ValueAnimator.AnimatorUpdateListener timerProgressUpdateRefresher = 
            new ValueAnimator.AnimatorUpdateListener() {
        
        @Override
        public void onAnimationUpdate(ValueAnimator arg0) {
            getTimerProgress().postInvalidate();
        }
    };
	
    private void playShowStopButtonAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator ani1 = ObjectAnimator.ofFloat(getTimerCenterLogo(), "scaleX", 1f, 0f);
        ani1.setDuration(500);
        ani1.setInterpolator(new AccelerateInterpolator());
        ani1.addUpdateListener(timerProgressUpdateRefresher);
        ObjectAnimator ani2 = ObjectAnimator.ofFloat(getStopTimerButton(), "scaleX", 0f, 1f);
        ani2.setDuration(500);
        ani2.setInterpolator(new DecelerateInterpolator());
        ani2.addUpdateListener(timerProgressUpdateRefresher);
        animatorSet.playSequentially(ani1, ani2);
        animatorSet.addListener(timerProgressRefresher);
        animatorSet.start();
    }
       
    private void playHideStopButtonAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator ani1 = ObjectAnimator.ofFloat(getStopTimerButton(), "scaleX", 1f, 0f);
        ani1.setDuration(500);
        ani1.setInterpolator(new AccelerateInterpolator());
        ani1.addUpdateListener(timerProgressUpdateRefresher);
        ObjectAnimator ani2 = ObjectAnimator.ofFloat(getTimerCenterLogo(), "scaleX", 0f, 1f);
        ani2.setDuration(500);
        ani2.setInterpolator(new DecelerateInterpolator());
        ani2.addUpdateListener(timerProgressUpdateRefresher);
        animatorSet.playSequentially(ani1, ani2);
        animatorSet.addListener(timerProgressRefresher);
        animatorSet.start();
    }
    
    private AdRequest adRequest;
    
    private void showAd() {
        if (adRequest == null) {
            adRequest = AdsUtil.createAdRequest();
        }
        
        // Look up the AdView and load a request.
        AdView adView = getAdView();
        adView.setVisibility(View.VISIBLE);
        adView.loadAd(adRequest);
    }
    
    private void hideAd() {
        AdView adView = getAdView();
        adView.pause();
        adView.setVisibility(View.GONE);
    }
    
    private void initWeibo() {
        // Create Weibo SDK instance 
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.WEIBO_APP_KEY);
        mWeiboShareAPI.registerApp();
    }
	
    // -----------------------------------------------------------------------
    // Widget getters
    // -----------------------------------------------------------------------
	
	private List<ToggleButton> buttons;
	
	private List<ToggleButton> getStartTimerButtons() {
	    if (buttons == null) {
    	    buttons = new ArrayList<ToggleButton>();
    	    
    	    buttons.add(getStartTimerButton01());
    	    buttons.add(getStartTimerButton02());
    	    buttons.add(getStartTimerButton03());
    	    buttons.add(getStartTimerButton04());
    	    buttons.add(getStartTimerButton05());
    	    buttons.add(getAdjustTimerButton());
	    }
	    return buttons;
	}
	
	private ToggleButton getStartTimerButton01() {
	    return (ToggleButton) findViewById(R.id.StartTimerButton01);
	}
	private ToggleButton getStartTimerButton02() {
	    return (ToggleButton) findViewById(R.id.StartTimerButton02);
	}
	private ToggleButton getStartTimerButton03() {
	    return (ToggleButton) findViewById(R.id.StartTimerButton03);
	}
	private ToggleButton getStartTimerButton04() {
	    return (ToggleButton) findViewById(R.id.StartTimerButton04);
	}
	private ToggleButton getStartTimerButton05() {
	    return (ToggleButton) findViewById(R.id.StartTimerButton05);
	}
	private ToggleButton getAdjustTimerButton() {
	    return (ToggleButton) findViewById(R.id.AdjustTimerButton);
	}
    private ImageButton getStopTimerButton() {
        return (ImageButton) findViewById(R.id.StopTimerButton);
    }
    private ImageView getTimerCenterLogo() {
        return (ImageView) findViewById(R.id.TimerCenterLogo);
    }
    private ImageButton getMenuButton() {
        return (ImageButton) findViewById(R.id.MenuButton);
    }
    private ImageButton getScanButton() {
        return (ImageButton) findViewById(R.id.ScanButton);
    }
    private ImageButton getShareButton() {
        return (ImageButton) findViewById(R.id.ShareButton);
    }
    private ProgressBar getTimerProgress() {
        return (ProgressBar) this.findViewById(R.id.TimerProgress);
    }
    private AdView getAdView() {
        return (AdView) this.findViewById(R.id.adView);
    }
    
    // -----------------------------------------------------------------------
    // UI logics
    // -----------------------------------------------------------------------

    private void doScan() {
        ScanIntentIntegrator integrator = new ScanIntentIntegrator(NoodlesMaster.this);
        integrator.addExtra("PROMPT_MESSAGE", NoodlesMaster.this.getString(R.string.scan_prompt_message));
        integrator.initiateScan(ScanIntentIntegrator.PRODUCT_CODE_TYPES);
        
        EasyTracker.getInstance(this).send(MapBuilder
                .createEvent(TrackerEvent.CATEGORY_SCAN, 
                             TrackerEvent.ACTION_INIT_SCAN, 
                             null, 
                             null)
                .build());
    }
    
    private void doShareWB() {
        // If Weibo is not installed, register a download callback.
        if (!mWeiboShareAPI.isWeiboAppInstalled()) {
            mWeiboShareAPI.registerWeiboDownloadListener(new IWeiboDownloadListener() {
                @Override
                public void onCancel() {
                    Toast.makeText(NoodlesMaster.this, 
                            R.string.weibosdk_cancel_download_weibo, 
                            Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        
        TextObject textObject = new TextObject();
        textObject.text = WeiboShareProvider.getShareText(this, 
                (String) getSupportActionBar().getSubtitle(), totalSecs);

        ImageObject imageObject = new ImageObject();
        Bitmap imageBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.share_image);
        imageObject.setImageObject(imageBitmap);
        
        try {
            WeiboShareProvider.share(mWeiboShareAPI, textObject, imageObject);
            
            EasyTracker.getInstance(this).send(MapBuilder
                    .createEvent(TrackerEvent.CATEGORY_SHARE, 
                                 TrackerEvent.ACTION_SHARE_WB, 
                                 null, 
                                 Long.valueOf(totalSecs))
                    .build());
        } catch (WeiboShareException e) {
            Log.e(TAG, "Erro sharing to Weibo", e);
            Toast.makeText(NoodlesMaster.this, e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {
            Log.w(TAG, "Weibo app not installed or is not official version", e);
            Toast.makeText(this, R.string.weibosdk_not_support_api_hint, Toast.LENGTH_SHORT).show();
        }
    }

    private void doStartPref() {
        Intent prefIntent = new Intent(NoodlesMaster.this, Preference.class);
        startActivity(prefIntent);
    }
}