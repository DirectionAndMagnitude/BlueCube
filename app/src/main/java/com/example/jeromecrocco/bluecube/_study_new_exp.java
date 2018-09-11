package com.example.jeromecrocco.bluecube;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class _study_new_exp extends AppCompatActivity {

    private LinearLayout parentLinearLayout;

    Type type = new TypeToken<ArrayList<String>>() {}.getType();    // Type of Exp Value
    String      expText;                                            // Entered Text Description
    String      expType;                                            // Spinner Object String
    Gson        gson = new Gson();                                  // Conversion to / from SQLite

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {



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
            ArrayList<String> expTextList = gson.fromJson(expText, type);
            ArrayList<String> expTypeList = gson.fromJson(expType, type);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    _study_new_exp.this,
                    android.R.layout.simple_spinner_dropdown_item,
                    expTypeList);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            //Re-populate the spinner / text boxes with the previously input data
            for (int i = 0; i < expTextList.size(); i++) {

                // Get string-pair from each list
                String expTextEntry = expTextList.get(i);
                String expTypeEntry = expTypeList.get(i);


                // Insert a new row into the layout
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.content_field_exp, null);
                parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);

                // Get the objects for each row
                EditText expTextBox = (EditText) rowView.findViewById(R.id.expText);
                Spinner expTypeSpinner = (Spinner) rowView.findViewById(R.id.expType);


                // Set the objects for each row

                expTextBox.setText(expTextEntry);
                expTypeSpinner.setAdapter(adapter);
                int spinnerPosition = adapter.getPosition(expTypeEntry);
                expTypeSpinner.setSelection(spinnerPosition);

            }
        }
    }
    public void onAddField(View v) {
        Toast.makeText(this,"Add Field",Toast.LENGTH_SHORT).show();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.content_field_exp, null);
        // Add the new row before the add field button.
        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);
    }

    public void onDelete(View v) {
        Toast.makeText(this,"Delete Field",Toast.LENGTH_SHORT).show();
        parentLinearLayout.removeView((View) v.getParent());
    }

    public void onSaveExpData(View v) {

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
        study.setExpData(text, spinner);

        //Update SQLITE Table
        dbHandler.addHandler(study, "exp");

        //Navigate back to study menu
        setContentView(R.layout.activity_study_new);
        Intent intent = new Intent(this, _study_new.class);
        startActivity(intent);

    }

}

