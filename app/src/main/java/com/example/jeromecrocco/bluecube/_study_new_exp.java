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


    Type type = new TypeToken<ArrayList<String>>() {}.getType();  // Type of Exp Value
    String text; // Entered Text Description
    String spinner; // Spinner Object String
    Gson gson = new Gson(); // For data conversion to / from SQLite database

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        setContentView(R.layout.activity_study_new_exp);
        parentLinearLayout = (LinearLayout) findViewById(R.id.study_new_exp);

        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*  For loading experimental data from JSON file
        Get the String from the SQLiteDatabse what you saved and changed into ArrayList type like below:
        outputarray is a String which is get from SQLiteDatabase for this example.*/

        _study_class study = _study_class.getInstance();

        text = study.getExpText();

/*        if (text == null) {
            //Initialize
            setContentView(R.layout.activity_study_new_exp);
            parentLinearLayout = (LinearLayout) findViewById(R.id.study_new_exp);
        }*/
        if (text != null) {

            //Retrieve the data from the class

            spinner = study.getExpType();
            ArrayList<String> finalTextString = gson.fromJson(text, type);
            ArrayList<String> finalSpinnerString = gson.fromJson(spinner, type);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    _study_new_exp.this,
                    android.R.layout.simple_spinner_dropdown_item,
                    finalSpinnerString);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            //Re-populate the spinner / text boxes with the previously input data
            for (int i = 0; i < finalTextString.size(); i++) {


                // Get string-pair from each list
                String l = finalTextString.get(i);
                String s = finalSpinnerString.get(i);

                // Insert a new row into the layout
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.content_field, null);
                parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);

                // Get the objects for each row
                EditText edit = (EditText) rowView.findViewById(R.id.edit_text);
                Spinner type = (Spinner) rowView.findViewById(R.id.type_spinner);


                // Set the objects for each row
                edit.setText(l);
                type.setAdapter(adapter);
                if (s != null) {
                    int spinnerPosition = adapter.getPosition(s);
                    type.setSelection(spinnerPosition);
                }

            }
        }
            


    }

    public void onAddField(View v) {
        Toast.makeText(this,"Add Field",Toast.LENGTH_SHORT).show();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.content_field, null);
        // Add the new row before the add field button.
        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);

    }

    public void onDelete(View v) {
        Toast.makeText(this,"Delete Field",Toast.LENGTH_SHORT).show();
        parentLinearLayout.removeView((View) v.getParent());
    }

    public void onSaveExpData(View v) {

        BlueCubeHandler dbHandler = new BlueCubeHandler(this, null, null, 1);
        //Generate a list of Items we hav added, change to JSON, Store in Database
        int count = parentLinearLayout.getChildCount();

        _study_class study = _study_class.getInstance();

        List<String>  edit_text    = new ArrayList<String>();
        List<String>  spinner_text = new ArrayList<String>();

        for(int i=0; i<count; i++) {
            View vnew = parentLinearLayout.getChildAt(i);
/*
            if (vnew instanceof EditText)
                edit_text.add(v.toString());
            if (vnew instanceof Spinner)
                spinner_text.add(v.toString());
*/

            // Get the objects for each row
            EditText e = (EditText) vnew.findViewById(R.id.edit_text);
            if (e != null) {
                Spinner t = (Spinner) vnew.findViewById(R.id.type_spinner);
                edit_text.add(e.getText().toString());

                TextView textView = (TextView) t.getSelectedView();
                String result = textView.getText().toString();
                spinner_text.add(result);

            }
        }

        if (edit_text != null) {
            //Convert to JSON
            Gson gson = new Gson();
            String text = gson.toJson(edit_text);
            String spinner = gson.toJson(spinner_text);
            study.setExpData(text, spinner);

            //Update SQLITE Table
            dbHandler.addHandler(study, "exp");

            //Navigate back to study menu
            setContentView(R.layout.activity_study_new);
            Intent intent = new Intent(this, _study_new.class);
            startActivity(intent);
        }
    }


}