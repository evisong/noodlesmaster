package me.evis.mobile.noodle;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class About extends Activity {
    private static final String TAG = "me.evis.mobile.noodle.About";
    private static final String OFFICIAL_WEBSITE = "http://n.evis.me";
    private static final String GOOGLE_PLAY = "https://play.google.com/store/apps/details?id=me.evis.mobile.noodle";
    // private static final String GOOGLE_PLAY = "market://details?id=me.evis.mobile.noodle";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        
        try {
            String appVer = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            TextView versionNameTxt = (TextView) findViewById(R.id.versionName);
            versionNameTxt.setText(appVer);
        }
        catch (NameNotFoundException e) {
            Log.v(TAG, e.getMessage());
        }
        
        Button visitWebsiteBtn = (Button) findViewById(R.id.visitWebsiteBtn);
        visitWebsiteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(OFFICIAL_WEBSITE))); 
            }
        });
        
        ImageButton visitGooglePlayBtn = (ImageButton) findViewById(R.id.visitGooglePlayBtn);
        visitGooglePlayBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY))); 
            }
        });
    }
}
