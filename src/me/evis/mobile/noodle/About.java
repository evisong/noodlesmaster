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
import android.widget.TextView;

public class About extends Activity {
    private static final String TAG = "me.evis.mobile.noodle.About";
    private static final String OFFICIAL_WEBSITE = "http://n.evis.me";
    
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
                String url = OFFICIAL_WEBSITE; 
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))); 
            }
        });
    }
}
