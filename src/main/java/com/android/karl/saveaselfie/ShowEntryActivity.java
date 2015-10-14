package com.android.karl.saveaselfie;

/*
    ShowEntryActivity
    Function: Show the user the information about the marker that they selected

    Copyright (c) 2015 Karl Jones. All rights reserved.
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

// TODO: Settle layout

public class ShowEntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_entry);
    }

    public void setContent() {
        // Get the content that is passed to the activity
    }

    public void initialiseViews() {
        // TODO: INITIALISE VIEWS FOR THE LAYOUT HERE
    }


    public void showViews() {
        // TODO: SHOW THE VIEWS OF THE LAYOUT
    }

    public void hideViews() {
        // TODO: HIDE THE VIEWS OF THE LAYOUT

        // The reason for this method is that it is to be used if there are any pop-outs, the views disappear behind it
        // and reappear after the user is done with the dialog
    }
}
