package com.android.karl.saveaselfie;

/*
    UploadActivity
    Function: Get the user to input information into the system for upload

    Copyright (c) 2015 Karl Jones. All rights reserved.
 */


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

// TODO: Add clear all menu button, add icon to the menu item
// TODO: Have the image scale into an ImageView correctly
// TODO: Settle layout

public class UploadActivity extends AppCompatActivity {

    // GUI Components
    public ImageView image;

    public Button btnUpload;

    public ImageButton button_difib;
    public ImageButton button_first_aid_kit;
    public ImageButton button_hydrant;
    public ImageButton button_life_ring;

    public EditText userComment;

    public TableLayout device_selection_layout;

    public TextView text_view_device_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setTitle(getString(R.string.upload_title));
        } catch (NullPointerException e){
            throw new NullPointerException("Error: " + e);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*
        *   This method targets android 5.+ and 6.+ to change the system bar and set the status colour
        *   This is needed to avoid a null pointer exception being created with older android devices.
        * */
        setUpStatusBar();

        // Set up the views of the layout
        setUpViews();

        // Hide all the views until the user has choosen their photo
        hideViews();

        // Set listeners
        setUpListeners();

        // Get the photo that the user wants to upload into the system
        selectImage();
    }

    //  This sets up the status bar for devices 5.x +
    public void setUpStatusBar() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            this.getWindow().setStatusBarColor(this.getResources().getColor(R.color.ColorPrimaryDark));
        }
    }

    public final static int REQUEST_CAMERA = 1;
    public final static int SELECT_FILE = 2;

    // Ask the user to either take a photo or chose one from the gallery
    private void selectImage() {
        final CharSequence[] items = {getString(R.string.gallery), getString(R.string.camera)};

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.choose_image_location));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(items[which].equals(getString(R.string.camera))) {
                    // If the user chooses the camera, start the camera and get the image
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(i, REQUEST_CAMERA);
                } else if (items[which].equals(getString(R.string.gallery))) {
                    // If the user chooses the gallery, bring them to the gallery
                    Intent i = new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    i.setType("image/*");
                    startActivityForResult(Intent.createChooser(i, getString(R.string.choose_an_image)), SELECT_FILE);
                }
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                builder.setCancelable(true);
                dialog.cancel();
                Intent i = new Intent(UploadActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }).setCancelable(false).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            // If the user chooses camera read in the image
            if (requestCode == REQUEST_CAMERA){
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                try {
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                } catch (NullPointerException e){
                    e.printStackTrace();
                }

                File destination = new File (Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");

                FileOutputStream fo;

                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Rotation needs ot be fixed, image read in from camera does not rotate
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap rotatedBitmap = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true);

                image.setImageBitmap(rotatedBitmap);

                // Show the layout of the page
                showViews();
            }

            // If the user wants to read in from the gallery
            if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                Cursor cursor = managedQuery(selectedImageUri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();

                String selectedImagePath = cursor.getString(column_index);

                Bitmap bm;

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE) {
                    scale *= 2;
                }
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);

                image.setImageBitmap(bm);

                // Show the layout of the page
                showViews();
            }
        }

    } // End onActivityResult()

    /*
    *   This method checks that the required fields needed for the
    *   entry have been filled in. If this has validated it successfully,
    *   then passs the data on to be uploaded to the server
    *   @return true if everything is okay.
    * */
    public boolean validateAndUpload() {

        // Ensure the user has put in their comment
        if(userComment.getText().toString().equals("")) {
            makeSnackbar(getString(R.string.error_data_needs_to_be_entered_comment));
            return false;
        }

        // Ensure the user has entered an image to upload
        if(image.getDrawable() == null) {
            makeSnackbar(getString(R.string.error_data_needs_to_be_entered_image));
            return false;
        }

        // Push the information to the server
        return pushToServer();
    }

    /*
    *   Push the information to the server, called from validateAndUpload()
    *   @return true if the data has been successful.
    *   @return false if the data has not been successful.
    * */
    public boolean pushToServer() {
        // TODO: Finish method to push to the server

        return true;
    }


    final public static String DEFIBRILLATOR = "Defibrillator";
    final public static String LIFE_RING = "Life Ring";
    final public static String FIRST_AID_KIT = "First Aid Kit";
    final public static String HYDRANT = "Hydrant";
    public String device_type;

    /*
        Set the device type that the user is uploading
        @param type - type of device that the user is uploading
     */
    public void setDeviceType(String type) {
        switch(type){
            case DEFIBRILLATOR:
                device_type = DEFIBRILLATOR;
                break;
            case LIFE_RING:
                device_type = LIFE_RING;
                break;

        }
        if (type.equals(DEFIBRILLATOR)) {
            device_type = DEFIBRILLATOR;
        } else if (type.equals(LIFE_RING)) {
            device_type = LIFE_RING;
        } else if (type.equals(FIRST_AID_KIT)) {
            device_type = FIRST_AID_KIT;
        } else if (type.equals(HYDRANT)) {
            device_type = HYDRANT;
        } else {
            device_type = DEFIBRILLATOR;
        }
    }

    // The user has selected the defibrillator
    public void setDeviceDefibrillator() {
        setDeviceType(DEFIBRILLATOR);

        // Show the right device to be selected
        button_difib.setImageResource(R.drawable.selected_defibrillator);
        button_first_aid_kit.setImageResource(R.drawable.unselected_first_aid_kit);
        button_life_ring.setImageResource(R.drawable.unselected_life_ring);
        button_hydrant.setImageResource(R.drawable.unselected_fire_hydrant);
    }

    // The user has selected the first aid kit
    public void setDeviceFirstAidKit() {
        setDeviceType(FIRST_AID_KIT);

        // Show the right device to be selected
        button_difib.setImageResource(R.drawable.unselected_defibrillator);
        button_first_aid_kit.setImageResource(R.drawable.selected_first_aid_kit);
        button_life_ring.setImageResource(R.drawable.unselected_life_ring);
        button_hydrant.setImageResource(R.drawable.unselected_fire_hydrant);
    }

    // The user has selected the life ring
    public void setDeviceLifeRing() {
        setDeviceType(LIFE_RING);

        // Show the right device to be selected
        button_difib.setImageResource(R.drawable.unselected_defibrillator);
        button_first_aid_kit.setImageResource(R.drawable.unselected_first_aid_kit);
        button_life_ring.setImageResource(R.drawable.selected_life_ring);
        button_hydrant.setImageResource(R.drawable.unselected_fire_hydrant);
    }

    // The user has selected the hydrant
    public void setDeviceHydrant() {
        setDeviceType(HYDRANT);

        // Show the right device to be selected
        button_difib.setImageResource(R.drawable.unselected_defibrillator);
        button_first_aid_kit.setImageResource(R.drawable.unselected_first_aid_kit);
        button_life_ring.setImageResource(R.drawable.unselected_life_ring);
        button_hydrant.setImageResource(R.drawable.selected_fire_hydrant);
    }

    // Set up views
    public void setUpViews() {
        image = (ImageView) findViewById(R.id.image_choosen_by_user);

        btnUpload = (Button) findViewById(R.id.upload_button);
        button_difib = (ImageButton) findViewById(R.id.defib_button);
        button_first_aid_kit = (ImageButton) findViewById(R.id.first_aid_kit_button);
        button_life_ring = (ImageButton) findViewById(R.id.life_ring_button);
        button_hydrant = (ImageButton) findViewById(R.id.hydrant_button);

        userComment = (EditText) findViewById(R.id.enter_text);

        device_selection_layout = (TableLayout) findViewById(R.id.device_selection_table_layout);

        text_view_device_type = (TextView) findViewById(R.id.enter_device_text_view);
    }   // End setUpViews()

    // Set up listeners
    public void setUpListeners() {

        // What to do when the user clicks on the image
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Allows the user to choose another photo if they are not happy with their choice
                selectImage();
            }
        });

        // What do do when the user clicks POST
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndUpload();
            }
        });

        // Image buttons for device selection
        button_difib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDeviceDefibrillator();
            }
        });
        button_life_ring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDeviceLifeRing();
            }
        });
        button_first_aid_kit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDeviceFirstAidKit();
            }
        });
        button_hydrant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDeviceHydrant();
            }
        });
    }   // End setUpListeners();

    // Hide all the views
    public void hideViews() {
        image.setVisibility(View.GONE);
        btnUpload.setVisibility(View.GONE);
        userComment.setVisibility(View.GONE);
        device_selection_layout.setVisibility(View.GONE);
        text_view_device_type.setVisibility(View.GONE);
    }   // End hideViews()

    // Show all the views
    public void showViews() {
        image.setVisibility(View.VISIBLE);
        btnUpload.setVisibility(View.VISIBLE);
        userComment.setVisibility(View.VISIBLE);
        device_selection_layout.setVisibility(View.VISIBLE);
        text_view_device_type.setVisibility(View.VISIBLE);
    }   // End showViews()

    /*
        This shows the user a snackbar with the message received inside.
        @param message to be displayed
        @return void
     */
    public void makeSnackbar(String message) {Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();}

}
