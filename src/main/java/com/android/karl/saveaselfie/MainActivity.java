package com.android.karl.saveaselfie;

/*
    MainActivity
    Function: Show the markers on a map for the user to easily see

    Copyright (c) 2015 Karl Jones. All rights reserved.
 */

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

// TODO: Fix Google Maps not showing

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    // These need to make the keys that are in the database
    private final String TYPE_DEFIB = "defib";
    private final String TYPE_LIFE_RINGS = "life_ring";
    private final String TYPE_FIRST_AID_KIT = "first_aid_kit";
    private final String TYPE_HYDRANTS = "hydrants";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setUpMapIfNeeded();

        // -------------- Initialise buttons & onClickListeners -----------------------
        // Floating action button set up
        ImageButton floatingActionBtn = (ImageButton) findViewById(R.id.floatingActionButton);
        floatingActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToUpload();
            }
        });

        // Set up the statusbar
        setUpStatusBarLollipop();
        // setUpStatusBarMarshmallow();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setUpStatusBarLollipop() {
        this.getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /*
    *   Sets up the map for use
    *   Ensure that it loads on app start up
    * */

    private GoogleMap supportmapfragment;
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (supportmapfragment == null) {
            supportmapfragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

            // Check if the map was instantiated or not.
            if (supportmapfragment != null) {
                setUpMap();
            } else {
                makeSnackbar(getString(R.string.error_maps_not_loading));
            }
        } else {
            makeSnackbar("Maps loaded sucessfully");
        }
    }

    /*
    *   This method is called when the map has finihed either loading or has failed to load
    * */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(supportmapfragment != null) {makeToast(getString(R.string.maps_ready));}
        else {makeToast(getString(R.string.error_maps_not_loading));}
    }

    /*
    *   Set the map up, add the markers to the map
    *   and the listeners for the markers
    * */
    ArrayList<Marker> markers = new ArrayList<Marker>();
    private void setUpMap() {

        // TODO: Get the markers from the server and add them to the map

        // Set the map type
        supportmapfragment.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // UI setting for the locate me button
        supportmapfragment.getUiSettings().setMyLocationButtonEnabled(true);

        // UI setting for gestures for zooming
        supportmapfragment.getUiSettings().setZoomGesturesEnabled(true);

        // UI setting for gestures for rotating
        supportmapfragment.getUiSettings().setRotateGesturesEnabled(true);

        // Debugging marker, needs to be removed before release
        supportmapfragment.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("This is a test"));

        // TODO: LOOP TO READ IN ALL OF THE MARKERS FROM THE SERVER AND ADD THEM INTO THE PROGRAM

        // Add the markers to the map, checking the type and adding the correct icons to the map
        for(int i = 0; i < markers.size() ; i++ ) {
            if(markers.get(i).getType().equals(TYPE_DEFIB)){
                // If the type is defib then set the icon to a specific type
            } else if (markers.get(i).getType().equals(TYPE_LIFE_RINGS)){
                // If the type is life rings
            } else if (markers.get(i).getType().equals(TYPE_FIRST_AID_KIT)){
                // If the type is a first aid kit
            } else if (markers.get(i).getType().equals(TYPE_HYDRANTS)){
                // If the type is a hydrant
            } else {
                // Do not show the marker and remove it from the arraylist as this is an error
            }
        } // End for
    }
    private void getMarkersFromServer() {
        // This needs to get the markers from the server
    }

    /*
    *   This shows the user a toast with the message received inside.
    *   @param message to be displayed
    *   @return void
    * */
    public void makeToast (String message){Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();}

    /*
        This shows the user a snackbar with the message received inside.
        @param message to be displayed
        @return void
     */
    public void makeSnackbar(String message) {Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();}

    /*
    *   This method sends the user to the activity to upload their photo
    * */
    public void sendUserToUpload() {
        Intent i = new Intent(MainActivity.this, UploadActivity.class);
        startActivity(i);
    }
}
