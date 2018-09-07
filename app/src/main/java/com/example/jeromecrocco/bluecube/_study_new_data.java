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

public class _study_new_data extends AppCompatActivity {

    private LinearLayout parentLinearLayout;

    Type type = new TypeToken<ArrayList<String>>() {}.getType();    // Type of data Value
    String      dataText;                                           // Entered Text Description
    String      dataType;                                            // Spinner Object String
    Gson        gson = new Gson();                                  // Conversion to / from SQLite



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_study_new_data);
        parentLinearLayout = (LinearLayout) findViewById(R.id.study_new_data);

        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*  
        For loading data data from JSON file
        Get the String from the SQLiteDatabse what you saved and changed into ArrayList type like below:
        */

        _study_class study  = _study_class.getInstance();
        dataText            = study.getDataText();

        if (dataText != null) {

            //Retrieve the data from the class

            dataType = study.getDataType();
            ArrayList<String> dataList      = gson.fromJson(dataText, type);
            ArrayList<String> dataTypeList  = gson.fromJson(dataType, type);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    _study_new_data.this,
                    android.R.layout.simple_spinner_dropdown_item,
                    dataTypeList);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            //Re-populate the spinner / text boxes with the previously input data
            for (int i = 0; i < dataList.size(); i++) {

                // Get string-pair from each list
                String dataTextEntry = dataList.get(i);
                String dataTypeEntry = dataTypeList.get(i);

                // Insert a new row into the layout
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.content_field_data , null);
                parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);

                // Get the objects for each row
                EditText dataTextBox    = (EditText) rowView.findViewById(R.id.dataText);
                Spinner  dataTypeSpinner = (Spinner) rowView.findViewById(R.id.dataType);

                // Set the objects for each row
                dataTextBox.setText(dataTextEntry);
                dataTypeSpinner.setAdapter(adapter);
                int spinnerPosition = adapter.getPosition(dataTypeEntry);
                dataTypeSpinner.setSelection(spinnerPosition);
            }
        }
    }
    public void onAddField(View v) {
        Toast.makeText(this,"Add Field",Toast.LENGTH_SHORT).show();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.content_field_data, null);
        // Add the new row before the add field button.
        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);
    }

    public void onDelete(View v) {
        Toast.makeText(this,"Delete Field",Toast.LENGTH_SHORT).show();
        parentLinearLayout.removeView((View) v.getParent());
    }

    public void onSaveData(View v) {

        //Generate a list of Items we hav added, change to JSON, Store in Database
        BlueCubeHandler dbHandler = new BlueCubeHandler(this, null, null, 1);

        _study_class study          = _study_class.getInstance();
        List<String>  dataTextList   = new ArrayList <String>();
        List<String>  dataTypeList   = new ArrayList <String>();

        for(int i=0; i<parentLinearLayout.getChildCount(); i++) {

            //iterate over each of the rowViews we have inserted and stored previously
            View viewNew = parentLinearLayout.getChildAt(i);

            // Get the objects for each row
            EditText dataTextBox = (EditText) viewNew.findViewById(R.id.dataText);

            if (dataTextBox != null) {
                Spinner dataTypeSpinner = (Spinner) viewNew.findViewById(R.id.dataType);
                dataTextList.add(dataTextBox.getText().toString());

                TextView dataTypeEntry = (TextView) dataTypeSpinner.getSelectedView();
                String result = dataTypeEntry.getText().toString();
                dataTypeList.add(result);
            }
        }

       //Convert to JSON
        Gson gson = new Gson();
        String text = gson.toJson(dataTextList);
        String spinner = gson.toJson(dataTypeList);
        study.setData(text, spinner);

        //Update SQLITE Table
        dbHandler.addHandler(study, "data");

        //Navigate back to study menu
        setContentView(R.layout.activity_study_new);
        Intent intent = new Intent(this, _study_new.class);
        startActivity(intent);

    }

}

