package me.evis.mobile.noodle;

import me.evis.mobile.util.AdsUtil;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class About extends ActionBarActivity {
    private static final String TAG = "About";
    private static final String OFFICIAL_WEBSITE = "http://n.evis.me";
    private static final String OFFICIAL_WEIBO = "http://weibo.com/noodlesmaster";
    private static final String GOOGLE_PLAY = "https://play.google.com/store/apps/details?id=me.evis.mobile.noodle";
    // private static final String GOOGLE_PLAY = "market://details?id=me.evis.mobile.noodle";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        try {
            String appVer = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            TextView versionNameTxt = (TextView) findViewById(R.id.versionName);
            versionNameTxt.setText(appVer);
        }
        catch (NameNotFoundException e) {
            Log.v(TAG, e.getMessage());
        }
        
        showAd();
        
        Button visitWeiboBtn = (Button) findViewById(R.id.visitWeiboBtn);
        visitWeiboBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Track the click
                EasyTracker.getInstance(About.this).send(MapBuilder
                        .createEvent(TrackerEvent.CATEGORY_UI,
                                     TrackerEvent.ACTION_BUTTON, 
                                     "about_visitWeiboBtn",
                                     null)
                        .build());
                
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(OFFICIAL_WEIBO))); 
            }
        });
        
        Button visitWebsiteBtn = (Button) findViewById(R.id.visitWebsiteBtn);
        visitWebsiteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Track the click
                EasyTracker.getInstance(About.this).send(MapBuilder
                        .createEvent(TrackerEvent.CATEGORY_UI, 
                                     TrackerEvent.ACTION_BUTTON, 
                                     "about_visitWebsiteBtn", 
                                     null)
                        .build());
                
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(OFFICIAL_WEBSITE))); 
            }
        });
        
        ImageButton visitGooglePlayBtn = (ImageButton) findViewById(R.id.visitGooglePlayBtn);
        visitGooglePlayBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Track the click
                EasyTracker.getInstance(About.this).send(MapBuilder
                        .createEvent(TrackerEvent.CATEGORY_UI, 
                                     TrackerEvent.ACTION_BUTTON, 
                                     "about_visitGooglePlayBtn",
                                     null)
                        .build());
                
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY))); 
            }
        });
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        // start Google Analytics
        EasyTracker.getInstance(this).activityStart(this);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        // stop Google Analytics
        EasyTracker.getInstance(this).activityStop(this);
    }
    
    @Override
    public void onPause() {
        Log.v(TAG, "About.onPause");
        getAdView().pause();
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        Log.v(TAG, "About.onResume");
        getAdView().resume();
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
        Log.v(TAG, "About.onDestroy");
        getAdView().destroy();
        super.onDestroy();
    }
    
    
    private AdRequest adRequest;
    
    private void showAd() {
        if (adRequest == null) {
            adRequest = AdsUtil.createAdRequest();
        }
        
        // Look up the AdView and load a request.
        getAdView().loadAd(adRequest);
    }
    
    private AdView getAdView() {
        return (AdView) this.findViewById(R.id.adView);
    }
}
