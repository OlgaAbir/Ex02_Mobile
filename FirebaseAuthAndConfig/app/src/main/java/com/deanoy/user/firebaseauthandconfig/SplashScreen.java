package com.deanoy.user.firebaseauthandconfig;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class SplashScreen extends Activity {

    public static final String TAG = "Splash Activity";

    private static final int mNumOfSeconds = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "SplashCreate >>");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        setTitle("");

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            TextView tvVersion = findViewById(R.id.tvVersionInfo);

            tvVersion.setText(pInfo.versionName + "."+ String.valueOf(pInfo.versionCode));

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        }, mNumOfSeconds * 1000);

        Log.e(TAG, "SplashCreate <<");
    }
}
