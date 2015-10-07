package com.android.karl.saveaselfie;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
*   This activity is in control of the uploads that the user makes, it allows the user to enter the intofmration about
*   the entry that they are uploading, the image that they are uploading, the location and any other information
*   that the entry requires. It allows the user to choose the image that they want to use.
* */

// TODO: Add clear all menu button
// TODO: Have the image scale into an ImageView correctly
// TODO: Settle layout

public class UploadActivity extends AppCompatActivity {

    // GUI Components
    public ImageView image;
    public Button btnUpload;
    public EditText userComment;

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

        // Connect to the layout
        image = (ImageView) findViewById(R.id.image_choosen_by_user);
        btnUpload = (Button) findViewById(R.id.upload_button);
        userComment = (EditText) findViewById(R.id.enter_text);

        // Set onClickListeners
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Allows the user to choose another photo if they are not happy with their choice
                openImageIntent();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndUpload();
            }
        });

        // Get the photo that the user wants to upload into the system
        openImageIntent();

        /*
         *  Plan: This activity is going to ask the user to choose a photo to upload,
         *  it is then going to show the photo (? with the watermark) and ask the user
         *  to enter the details about the emergency equipment that they are uploading
         *
         *  After the user has picked which picture they would like to use, the views
         *  will then be visible for them to fill out the information about the upload
         *
         *  showViews();
         */
    }

    /*
    *   This method is used to prompt the user to choose a photo
    *   Input: void
    *   Output: void
    * */
    final int ACTION_REQUEST_CAMERA = 1;

    private Uri outputFileUri;
    public File sdImageMainDirectory;
    public String fname;

    private void openImageIntent() {
        // This finds the root file, if it cannot find the Save A Selfie image, then it will create the directory
        final File root = new File (Environment.getExternalStorageDirectory() + File.separator + "Save A Selfie" + File.separator);
        boolean mkdir = root.mkdirs();
        if(!mkdir){System.out.println("mkdir failed");}
        fname = "img_" + System.currentTimeMillis() + ".jpg";
        sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        //Camera
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        final Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.choose_image_location));

        // Add the camera options
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, ACTION_REQUEST_CAMERA);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){

            // Check if the result is being received from the image chooser activity
            if(requestCode == ACTION_REQUEST_CAMERA){
                final boolean isCamera;

                if(data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();

                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                    } // End if..else
                } // End if..else

                Uri selectedImageUri;
                if(isCamera){
                    selectedImageUri = outputFileUri;
                    if (selectedImageUri == null) {
                        Toast.makeText(UploadActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
                else {selectedImageUri = (data == null) ? null : data.getData();}

                // Set the image view to be the image we just read in.
                Bitmap myImage = BitmapFactory.decodeFile(sdImageMainDirectory.getAbsolutePath());
                image.setImageBitmap(myImage);

            } // End if(requestCode == ACTION_REQUEST_CAMERA)

        } // End if(resultCode == RESULT_OK)

    } // End onActivityResult()

    /*
    *   This method checks that the required fields needed for the
    *   entry have been filled in. If this has validated it successfully,
    *   then passs the data on to be uploaded to the server
    *   Returns true if everything is okay.
    * */
    public boolean validateAndUpload() {

        // Ensure the user has put in their comment
        if(userComment.getText().toString().equals("")) {
            Toast.makeText(UploadActivity.this, R.string.error_data_needs_to_be_entered_comment, Toast.LENGTH_SHORT).show();
            return false;
        }

        // Ensure the user has entered an image to upload
        if(image.getDrawable() == null) {
            Toast.makeText(UploadActivity.this, R.string.error_data_needs_to_be_entered_image, Toast.LENGTH_SHORT).show();
            return false;
        }

        // Push the information to the server
        return pushToServer();
    }

    /*
    *   Push the information to the server, called from validateAndUpload()
    *   Returns true if the data has been successful.
    *   Return false if the data has not been successful.
    * */
    public boolean pushToServer() {
        return true;
    }
}
