package me.evis.mobile.noodle;

import java.util.ArrayList;
import java.util.List;

import me.evis.mobile.noodle.provider.NoodlesContentProvider;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
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
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.ads.mediation.admob.AdMobAdapterExtras;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

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
	private static final int MENU_ITEM_SYNC = Menu.FIRST + 2;
	private static final int MENU_ITEM_PREFERENCE = Menu.FIRST + 3;
	
	
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
            
            stopTimer();
            
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // If no data was given in the intent (because we were started
        // as a MAIN activity), then use our default content provider.
        Intent intent = getIntent();
        if (intent.getData() == null) {
            intent.setData(ContentUris.withAppendedId(NoodlesContentProvider._ID_FIELD_CONTENT_URI, 1));
        }
        
        initNoodles();
        
        // Workaround view.setScaleX() is since API Level 11, use opensouce lib to provide initial value.
        ObjectAnimator.ofFloat(getStopTimerButton(), "scaleX", 1f, 0f).setDuration(1).start();
//        LinearLayout layout = (LinearLayout) findViewById(R.id.ButtonArea);
//        p = new PieChart(this);
//        LayoutParams lp = new LayoutParams(200, 200);
//        p.setLayoutParams(lp);
//        p.setBackgroundColor(0xffffffff);
//        p.setOnSliceClickListener(new OnSliceClickListener() {
//            @Override
//            public void onSliceClicked(PieChart pieChart, int sliceNumber) {
//                Toast.makeText(NoodlesMaster.this, "slice: " + sliceNumber, Toast.LENGTH_SHORT).show();
//            }
//        });
//        layout.addView(p);
        
        // StartTimer buttons behavior.
        getStartTimerButton01().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int secs = 3 * 60;
                setTimerTotalSecs(secs);
				startTimer(secs);
			}
		});
        getStartTimerButton02().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int secs = 3 * 60 + 30;
                setTimerTotalSecs(secs);
                startTimer(secs);
            }
        });
        getStartTimerButton03().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int secs = 4 * 60;
                setTimerTotalSecs(secs);
                startTimer(secs);
            }
        });
        getStartTimerButton04().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int secs = 4 * 60 + 30;
                setTimerTotalSecs(secs);
                startTimer(secs);
            }
        });
        getStartTimerButton05().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int secs = 5 * 60;
                setTimerTotalSecs(secs);
                startTimer(secs);
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
				stopTimer();
			}
		});
		
		// Menu button behavior.
		getMenuButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openOptionsMenu();
			}
		});
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(timerCompleteReceiver, new IntentFilter(NOODLES_TIMER_COMPLETE));
    }
    
    @Override
    protected void onStop() {
        super.onStop();
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
        menu.add(0, MENU_ITEM_SYNC, 0, R.string.menu_sync)
            .setShortcut('3', 'c')
            .setIcon(R.drawable.ic_menu_refresh);
        menu.add(0, MENU_ITEM_PREFERENCE, 0, R.string.menu_preference)
            .setShortcut('4', 'd')
            .setIcon(R.drawable.ic_menu_preferences);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_SYNC:
                // TODO
                return true;
            case MENU_ITEM_PREFERENCE:
                Intent preferenceIntent = new Intent(this, Preference.class);
                startActivity(preferenceIntent);
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
                    
                    Button okButton = (Button) findViewById(R.id.StartTimerButton);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            calculateTotalSecs();
                            dismiss();
                            startTimer(getTimerTotalSecs());
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
    
    
    /**
     * Query and initialize view.
     */
    protected void initNoodles() {
        if (Log.isLoggable(getClass().getSimpleName(), Log.DEBUG)) {
            Log.d(getClass().getSimpleName(), "Noodles URI to query: " + getIntent().getData());
        }
        
        Cursor cursor = managedQuery(getIntent().getData(), projection, 
                null, null, "noodles.name ASC");
        
        if (cursor.getCount() > 0 ) {
            Noodles noodles = new Noodles();
            
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
            
            // Timer
            setTimerTotalSecs(noodles.soakageTime);
        } else {
            Toast.makeText(this, R.string.noodles_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    // -----------------------------------------------------------------------
    // Timer logics
    // -----------------------------------------------------------------------
    
    protected PendingIntent alarmSender;
    
	protected void startTimer(final int secs) {
	    playShowStopButtonAnimation();
        
		// Disable to avoid multiple timers in one time.
	    setStartTimerButtonsEnabled(false);
		getStopTimerButton().setEnabled(true);
		// Workaround: button state change will cause PieProgressBar messed up.
		getTimerProgress().invalidate();
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
//				p.setSlices(new float[] {_currentSecs, _totalSecs - _currentSecs});
//				p.anima();
				
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
		// Add 1s to ensure last message is handled.
		am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, alarmMillisecs + 1000, alarmSender);
	}
	
    protected void stopTimer() {
        playHideStopButtonAnimation();
        
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.cancel(alarmSender);
		if (counterHandler != null) {
		    counterHandler.removeMessages(MESSAGE_WHAT_CODE);
		}
		
		setStartTimerButtonsEnabled(true);
		getStopTimerButton().setEnabled(false);
		// Workaround: button state change will cause PieProgressBar messed up.
		getTimerProgress().invalidate();
		
        showAd();
	}
	
	private void updateTimerCurrent(final int currentSec) {
	    final int[] currentDhms = DateTimeUtil.calculateDhms(currentSec);
	    ((TextView) findViewById(R.id.TimerCurrentHour)).setText(String.valueOf(currentDhms[1]));
	    ((TextView) findViewById(R.id.TimerCurrentMinute)).setText(formatNumber(currentDhms[2]));
	    ((TextView) findViewById(R.id.TimerCurrentSecond)).setText(formatNumber(currentDhms[3]));
	    getTimerProgress().setProgress(currentSec);
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
            adRequest = new AdRequest();
            AdMobAdapterExtras extras = new AdMobAdapterExtras()
                .addExtra("color_bg", "FFFFFF")
                .addExtra("color_bg_top", "FFFFFF")
                .addExtra("color_border", "DDDDDD")
                .addExtra("color_link", "000080")
                .addExtra("color_text", "0088FF")
                .addExtra("color_url", "333333");
    
            adRequest.setNetworkExtras(extras);
        }
        
        // Look up the AdView as a resource and load a request.
        AdView adView = (AdView) this.findViewById(R.id.adView);
        if (!adView.isReady() && !adView.isRefreshing()) {
            adView.loadAd(adRequest);
        }
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
    private ProgressBar getTimerProgress() {
        return (ProgressBar) this.findViewById(R.id.TimerProgress);
    }
}