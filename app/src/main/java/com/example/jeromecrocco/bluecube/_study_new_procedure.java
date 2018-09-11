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

public class _study_new_procedure extends AppCompatActivity {

    private LinearLayout parentLinearLayout;

    Type type = new TypeToken<ArrayList<String>>() {}.getType();    // Type of Exp Value
    String      procedureText;                                           // Entered Text Description
    String      procedureStep;                                            // Spinner Object String
    Gson        gson = new Gson();                                  // Conversion to / from SQLite

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_study_new_procedure);
        parentLinearLayout = (LinearLayout) findViewById(R.id.study_new_procedure);

        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*  
        For loading experimental data from JSON file
        Get the String from the SQLiteDatabse what you saved and changed into ArrayList type like below:
        */

        _study_class study  = _study_class.getInstance();
        procedureText       = study.getProcText();


        if (procedureText==null){
            setContentView(R.layout.activity_study_new_procedure);
            parentLinearLayout = (LinearLayout) findViewById(R.id.study_new_procedure);

            // Insert a new row into the layout
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.content_field_procedure,null);
            parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);

        }

        if (procedureText != null) {

            //Retrieve the data from the class

            procedureStep = study.getProcSteps();
            ArrayList<String> procedureStepList = gson.fromJson(procedureText, type);
            ArrayList<String> procedureStepNo = gson.fromJson(procedureStep, type);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    _study_new_procedure.this,
                    android.R.layout.simple_spinner_dropdown_item,
                    procedureStepNo);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            //Re-populate the spinner / text boxes with the previously input data
            for (int i = 0; i < procedureStepList.size(); i++) {

                // Get string-pair from each list
                String procTextEntry = procedureStepList.get(i);
                String procTypeEntry = procedureStepNo.get(i);

                // Insert a new row into the layout
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.content_field_procedure, null);
                parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);

                // Get the objects for each row
                EditText procTextBox = (EditText) rowView.findViewById(R.id.procText);
                Spinner procStepSpinner = (Spinner) rowView.findViewById(R.id.procStep);

                // Set the objects for each row
                procTextBox.setText(procTextEntry);
                procStepSpinner.setAdapter(adapter);
                int spinnerPosition = adapter.getPosition(procTypeEntry);
                procStepSpinner.setSelection(spinnerPosition);

            }
        }
    }
    public void onAddField(View v) {
        Toast.makeText(this,"Add Field",Toast.LENGTH_SHORT).show();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.content_field_procedure, null);
        // Add the new row before the add field button.
        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);
    }

    public void onDelete(View v) {
        Toast.makeText(this,"Delete Field",Toast.LENGTH_SHORT).show();
        parentLinearLayout.removeView((View) v.getParent());
    }

    public void onSaveProcedure(View v) {

        //Generate a list of Items we hav added, change to JSON, Store in Database
        BlueCubeHandler dbHandler = new BlueCubeHandler(this, null, null, 1);

        _study_class study = _study_class.getInstance();
        List<String>  ProcTextList   = new ArrayList <String>();
        List<String>  ProcTypeList   = new ArrayList <String>();


        for(int i=0; i<parentLinearLayout.getChildCount(); i++) {

            //iterate over each of the rowViews we have inserted and stored previously
            View viewNew = parentLinearLayout.getChildAt(i);

            // Get the objects for each row
            EditText ProcTextBox = (EditText) viewNew.findViewById(R.id.procText);

            if (ProcTextBox != null) {
                Spinner procTypeSpinner = (Spinner) viewNew.findViewById(R.id.procStep);
                ProcTextList.add(ProcTextBox.getText().toString());

                TextView procTypeEntry = (TextView) procTypeSpinner.getSelectedView();
                String result = procTypeEntry.getText().toString();
                ProcTypeList.add(result);
            }
        }

       //Convert to JSON
        Gson gson = new Gson();
        String text = gson.toJson(ProcTextList);
        String spinner = gson.toJson(ProcTypeList);
        study.setProcData(text, spinner);

        //Update SQLITE Table
        dbHandler.addHandler(study, "proc");

        //Navigate back to study menu
        setContentView(R.layout.activity_study_new);
        Intent intent = new Intent(this, _study_new.class);
        startActivity(intent);

    }

}

