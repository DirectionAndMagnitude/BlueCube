package com.example.jeromecrocco.bluecube;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class _study_new_exp extends AppCompatActivity {


    Type                type = new TypeToken<ArrayList<String>>() {}.getType();    // Type of Exp Value
    String              expText;                                            // Entered Text Description
    String              expType;                                            // Spinner Object String
    String              expImgUri;                                            // Spinner Object String

    Gson                gson = new Gson();                                  // Conversion to / from SQLite
    String[]            mTypeArray;
    String              mCurrentPhotoPath;
    File                photo;

    private LinearLayout parentLinearLayout;
    private URI         mImageUri;
    static final int    REQUEST_PICTURE_CAPTURE = 1;
    private String      pictureImagePath = "";
    List<String>        expImageUriList;

    Button takePictureButton;
    ImageView image;
    //Uri file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        image = (ImageView) findViewById(R.id.image);

        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*  
        For loading experimental data from JSON file
        Get the String from the SQLiteDatabse what you saved and changed into ArrayList type like below:
        */

        _study_class study = _study_class.getInstance();
        expText = study.getExpText();

        if (expText==null){
            setContentView(R.layout.activity_study_new_exp);
            parentLinearLayout = (LinearLayout) findViewById(R.id.study_new_exp);

            // Insert a new row into the layout
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.content_field_exp, null);
            parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);

        }

        if (expText != null) {

            //Retrieve the data from the class
            setContentView(R.layout.activity_study_new_exp);
            parentLinearLayout = (LinearLayout) findViewById(R.id.study_new_exp);

            expType = study.getExpType();
            expImgUri = study.getExpImgUri();

            ArrayList<String> expTextList = gson.fromJson(expText, type);
            ArrayList<String> expTypeList = gson.fromJson(expType, type);
            ArrayList<String> expImageUriList = gson.fromJson(expImgUri, type);

            //Correctly populate spinner box with available types
            mTypeArray = getResources().getStringArray(R.array.expTypes);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    _study_new_exp.this,
                    android.R.layout.simple_spinner_dropdown_item,
                    mTypeArray);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            //Re-populate the spinner / text boxes with the previously input data
            for (int i = 0; i < expTextList.size(); i++) {

                // Get string-pair from each list
                String expTextEntry = expTextList.get(i);
                String expTypeEntry = expTypeList.get(i);
                String expImageUriEntry = expImageUriList.get(i);


                // Insert a new row into the layout
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.content_field_exp, null);
                parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);

                // Get the objects for each row
                EditText expTextBox = (EditText) rowView.findViewById(R.id.expText);
                Spinner expTypeSpinner = (Spinner) rowView.findViewById(R.id.expType);
                image = (ImageView) findViewById(R.id.imageView);



                // Set the objects for each row
                expTextBox.setText(expTextEntry);
                //Spinner
                expTypeSpinner.setAdapter(adapter);
                int spinnerPosition = adapter.getPosition(expTypeEntry);
                expTypeSpinner.setSelection(spinnerPosition);
                //Image
                File imgFile = new  File(expImageUriEntry);
                Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                image.setImageBitmap(bmp);
            }
        }

    }
    public void onAddField(View v) {

        //Every time we take a picture we add the path to the list
        expImageUriList.add(pictureImagePath);

        Toast.makeText(this,"Add Field",Toast.LENGTH_SHORT).show();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.content_field_exp, null);
        // Add the new row before the add field button.
        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);

        pictureImagePath = "";
    }

    public void onDelete(View v) {
        Toast.makeText(this,"Delete Field",Toast.LENGTH_SHORT).show();
        parentLinearLayout.removeView((View) v.getParent());
    }

    // TODO:  CAMERA FEATURES:  Take, Save, Display, Gallery, etc..

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (RESULT_OK == resultCode) {

            // Get Extra from the intent
            Bundle extras = data.getExtras();
            // Get the returned image from extra
            Bitmap bmp = (Bitmap) extras.get("data");


            image = (ImageView) findViewById(R.id.imageView);
            image.setImageBitmap(bmp);





/*
            File imgFile = new  File(pictureImagePath);
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
*/

            }

    }


    public void DispatchTakePictureIntent(View view) {

        // Get the Filename
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
        File file = new File(pictureImagePath);
        Uri outputFileUri = Uri.fromFile(file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(intent, 100);

    };




    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }

/*
    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }
*/

    public void galleryAddPic(View view) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "blueCube_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureFile,  ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    public void onSaveExpData(View v) {

        //Every time we take a picture we add the path to the list
        expImageUriList.add(pictureImagePath);

        //Generate a list of Items we hav added, change to JSON, Store in Database
        BlueCubeHandler dbHandler = new BlueCubeHandler(this, null, null, 1);

        _study_class study = _study_class.getInstance();
        List<String>  expTextList   = new ArrayList<String>();
        List<String>  expTypeList   = new ArrayList<String>();


        for(int i=0; i<parentLinearLayout.getChildCount(); i++) {

            //iterate over each of the rowViews we have inserted and stored previously
            View viewNew = parentLinearLayout.getChildAt(i);

            // Get the objects for each row
            EditText expTextBox = (EditText) viewNew.findViewById(R.id.expText);

            if (expTextBox != null) {
                Spinner expTypeSpinner = (Spinner) viewNew.findViewById(R.id.expType);
                expTextList.add(expTextBox.getText().toString());

                TextView expTypeEntry = (TextView) expTypeSpinner.getSelectedView();
                String result = expTypeEntry.getText().toString();
                expTypeList.add(result);


            }
        }

       //Convert to JSON
        Gson gson = new Gson();
        String text = gson.toJson(expTextList);
        String spinner = gson.toJson(expTypeList);
        String expImg = gson.toJson(expImageUriList);

        study.setExpData(text, spinner,expImg);

        //Update SQLITE Table
        dbHandler.addHandler(study, "exp");

        //Navigate back to study menu
        setContentView(R.layout.activity_study_new);
        Intent intent = new Intent(this, _study_new.class);
        startActivity(intent);

    }

}

