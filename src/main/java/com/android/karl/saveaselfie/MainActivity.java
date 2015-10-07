package com.android.karl.saveaselfie;

/*
*   MainActivity
*   Author: Karl Jones
*   Function: The main screen of the application,
*   this shows the user the map and the locations
*   of the emergency equipment from the servers
*
* */

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
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

        // Set up the map
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

        /*
        *   This method targets android 5.+ and 6.+ to change the system bar and set the status colour
        *   This is needed to avoid a null pointer exception being created with older android devices.
        * */
        if (Build.VERSION.RELEASE.startsWith("5")|| Build.VERSION.RELEASE.startsWith("6")) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            this.getWindow().setStatusBarColor(this.getResources().getColor(R.color.ColorPrimaryDark));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /*
    *   Sets up the map for use
    * */

    GoogleMap supportmapfragment;

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (supportmapfragment == null) {
            supportmapfragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

            // Check if the map was instantiated or not.
            if (supportmapfragment != null) {
                setUpMap();
            } else {
                makeToast(getString(R.string.error_maps_not_loading));
            }
        } else {
            makeToast(getString(R.string.maps_loaded_success));
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
        // Sample marker, for debugging purposes.
        // TODO: Get the markers from the server and add them to the map
        supportmapfragment.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("This is a test"));
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
                // Else just add a regular marker
            }
        }
    }
    private void getMarkersFromServer() {
    }

    /*
    *   This shows the user a toast with the message received inside.
    *   Input: string
    *   Output: void
    * */
    public void makeToast (String message){Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();}

    /*
    *   This method sends the user to the activity to upload their photo
    * */
    public void sendUserToUpload() {
        Intent i = new Intent(MainActivity.this, UploadActivity.class);
        startActivity(i);
    }
}
