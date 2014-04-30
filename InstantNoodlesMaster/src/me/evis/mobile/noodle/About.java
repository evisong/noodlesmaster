package me.evis.mobile.noodle;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;

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
        
        EasyTracker.getInstance().setContext(this);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        try {
            String appVer = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            TextView versionNameTxt = (TextView) findViewById(R.id.versionName);
            versionNameTxt.setText(appVer);
        }
        catch (NameNotFoundException e) {
            Log.v(TAG, e.getMessage());
        }
        
        Button visitWeiboBtn = (Button) findViewById(R.id.visitWeiboBtn);
        visitWeiboBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Track the click
                EasyTracker.getTracker().sendEvent(
                        TrackerEvent.CATEGORY_UI, TrackerEvent.ACTION_BUTTON, 
                        "about_visitWeiboBtn", null);
                
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(OFFICIAL_WEIBO))); 
            }
        });
        
        Button visitWebsiteBtn = (Button) findViewById(R.id.visitWebsiteBtn);
        visitWebsiteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Track the click
                EasyTracker.getTracker().sendEvent(
                        TrackerEvent.CATEGORY_UI, TrackerEvent.ACTION_BUTTON, 
                        "about_visitWebsiteBtn", null);
                
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(OFFICIAL_WEBSITE))); 
            }
        });
        
        ImageButton visitGooglePlayBtn = (ImageButton) findViewById(R.id.visitGooglePlayBtn);
        visitGooglePlayBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Track the click
                EasyTracker.getTracker().sendEvent(
                        TrackerEvent.CATEGORY_UI, TrackerEvent.ACTION_BUTTON, 
                        "about_visitGooglePlayBtn", null);
                
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY))); 
            }
        });
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        // start Google Analytics
        EasyTracker.getInstance().activityStart(this);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        // stop Google Analytics
        EasyTracker.getInstance().activityStop(this);
    }
}
