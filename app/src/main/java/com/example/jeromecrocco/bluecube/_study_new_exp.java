package com.example.jeromecrocco.bluecube;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

    private LinearLayout parentLinearLayout;
    String              pictureImagePath;
    public ArrayList<String>        expImage_UriList;
    Bitmap bmp;

    ImageView image;
    //Uri file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        expImage_UriList = new ArrayList<String>();

        image = (ImageView) findViewById(R.id.image);
        pictureImagePath = "";

        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        For loading experimental data from JSON file
        Get the String from the SQLiteDatabse what you saved and changed into ArrayList type like below:
        */

        _study_class study  = _study_class.getInstance();
        expText             = study.getExpText();

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
                EditText  expTextBox      = (EditText) rowView.findViewById(R.id.expText);
                Spinner   expTypeSpinner  = (Spinner) rowView.findViewById(R.id.expType);
                ImageView image           = (ImageView) findViewById(R.id.imageView);

                // Set the objects for each row
                expTextBox.setText(expTextEntry);
                //Spinner
                expTypeSpinner.setAdapter(adapter);
                int spinnerPosition = adapter.getPosition(expTypeEntry);
                expTypeSpinner.setSelection(spinnerPosition);
                //Image

//                Uri tempUri = Uri.parse(expImageUriEntry);

                File imgFile = new  File(expImageUriEntry);
                if(imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    //Drawable d = new BitmapDrawable(getResources(), myBitmap);
                    image.setImageBitmap(myBitmap);
                }

            }
        }

    }


    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException{
        InputStream input = this.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        int THUMBNAIL_SIZE = 20;
        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//
        input = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }


    public void onAddField(View v) {

        //Every time we take a picture we add the path to the list
        //expImageUriList.add(pictureImagePath.toString());

        Toast.makeText(this,"Add Field",Toast.LENGTH_SHORT).show();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.content_field_exp, null);
        // Add the new row before the add field button.
        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);

        //pictureImagePath = "";
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
            Bitmap bmp = (Bitmap) extras.get("data");
            ImageView image = (ImageView) findViewById(R.id.imageView);
            image.setImageBitmap(bmp);


            }
    }


    public void DispatchTakePictureIntent(View view) {

        // Get the Filename & URI
        String timeStamp;
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File storageDir =new File(getStorageDir(), imageFileName);
        File newFile = new File(storageDir.getAbsolutePath());
        Uri contentUri = FileProvider.getUriForFile(_study_new_exp.this,
                "com.example.jeromecrocco.bluecube.fileprovider",
                newFile);

        expImage_UriList.add(contentUri.toString());

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        startActivityForResult(intent, 100);
    };

    private String getStorageDir() {
        //  return this.getExternalFilesDir(null).getAbsolutePath();
        return getCacheDir().toString();
    }


    public void onSaveExpData(View v) {
        //The Purpose of this function is to assign the data to the study_class
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
        Gson   gson     = new Gson();
        String text     = gson.toJson(expTextList);
        String spinner  = gson.toJson(expTypeList);
        String expImg   = gson.toJson(expImage_UriList);

        study.setExpData(text, spinner,expImg);

        //Update SQLITE Table
        dbHandler.addHandler(study, "exp");

        //Navigate back to study menu
        setContentView(R.layout.activity_study_new);
        Intent intent = new Intent(this, _study_new.class);
        startActivity(intent);
    }
}

