package com.android.karl.saveaselfie;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

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
                makeToast("Boop \uD83D\uDC31");
            }
        });

        // If Lollipop or Marshmallow, set the status bar up
        if (Build.VERSION.RELEASE.startsWith("5")||Build.VERSION.RELEASE.startsWith("6")){
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {

        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {

            FragmentManager fmanager = getSupportFragmentManager();
            Fragment fragment = fmanager.findFragmentById(R.id.map);
            SupportMapFragment supportmapfragment = (SupportMapFragment)fragment;
            mMap = supportmapfragment.getMap();

            // Check if the map was instantiated or not.
            if (mMap != null) {
                setUpMap();
            } else {
                makeToast(getString(R.string.error_maps_not_loading));
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    private void setUpMap() {
        // Sample marker, for debugging purposes.
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    // This shows the user a toast with the message received inside.
    // Input: string
    // Output: void
    public void makeToast (String message){
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }


}
