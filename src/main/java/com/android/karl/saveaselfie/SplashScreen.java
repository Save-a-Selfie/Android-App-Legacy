package com.android.karl.saveaselfie;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreen extends Activity {

    private int SplashScreenTimeout = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // This is only run on devices running Lollipop or Marshmallow
        if (Build.VERSION.RELEASE.startsWith("5")|| Build.VERSION.RELEASE.startsWith("6")) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        new Handler().postDelayed(new Runnable() {

            // This method is run after the timer has expired
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);

                overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom);
                finish();
            }
        }, SplashScreenTimeout);
    }
}