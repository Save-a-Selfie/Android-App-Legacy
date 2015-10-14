package com.android.karl.saveaselfie;

/*
*   SplashScreenActivity
*
*   Author: Karl Jones
*   Function: Show the user the splash screen of the application
*
* */

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;

// TODO: Add SAS logo over splash screen

public class SplashScreen extends Activity {

    /*
    *   To change the duration of the splash screen
    *   change the SplashScreenSeconds and put the
    *   duration in seconds, always needs to be x.x
    * */
    final static double SplashScreenSeconds = 0.5; // needs to be changed to a larger double before release, 2 seconds
    final static double SplashScreenTimeout = SplashScreenSeconds * 1000;  // This should never be changed.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        setUpStatusBarLollipop();

        // Delay the changing of the intent and give the intent an animation  as it changes.
        new Handler().postDelayed(new Runnable() {

            // This method is run when the SlashScreenTimeout has expired
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, MainActivity.class);

                startActivity(i);

                // TODO: CHANGE THESE TO LOCAL ANIMATIONS, MAYBE CHOOSE NICER ONES
                overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom);

                finish();
            }
        }, (int)SplashScreenTimeout);
    }

    // This allows the app to set the status bar on lollipop devices to be transparent
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setUpStatusBarLollipop() {
        this.getWindow().setStatusBarColor(Color.TRANSPARENT);
    }
}