package com.android.karl.saveaselfie;

/*
*   SplashScreenActivity
*   Author: Karl Jones
*   Function: Show the user the splash screen of the application
*
* */

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.WindowManager;

// TODO: Add SAS logo over splash screen
// TODO: Ensure system bar is translucent on Android 5.x+

public class SplashScreen extends Activity {

    /*
    *   To change the duration of the splash screen
    *   change the SplashScreenSeconds and put the
    *   duration in seconds, always needs to be x.x
    * */
    private double SplashScreenSeconds = 0.5; // needs to be changed to a larger double before release
    private final double SplashScreenTimeout = SplashScreenSeconds * 1000;  // This should never be changed.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // This is only run on devices running Lollipop or Marshmallow
        if (Build.VERSION.RELEASE.startsWith("5")|| Build.VERSION.RELEASE.startsWith("6")) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // Delay the changing of the intent
        new Handler().postDelayed(new Runnable() {

            // This method is run when the SlashScreenTimeout has expired
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);

                overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom);
                finish();
            }
        }, (int)SplashScreenTimeout);
    }
}