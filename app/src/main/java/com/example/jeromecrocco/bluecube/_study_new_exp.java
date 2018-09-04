package com.example.jeromecrocco.bluecube;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class _study_new_exp extends AppCompatActivity {


    private LinearLayout parentLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_study_new_exp);
        parentLinearLayout = (LinearLayout) findViewById(R.id.study_new_exp);

        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

/*  For loading experimental data from JSON file
        Get the String from the SQLiteDatabse what you saved and changed into ArrayList type like below: outputarray is a String which is get from SQLiteDatabase for this example.
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> finalOutputString = gson.fromJson(outputarray, type);
        //TODO:  Need to add access to DB to reload data, then to turn to list, then to populate child layout with list entries.
*/

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

        int count = parentLinearLayout.getChildCount();

        _study_class study = _study_class.getInstance();

        List<String> edit_text = new ArrayList<String>();
        List<String>  spinner_text = new ArrayList<String>();

        for(int i=0; i<count; i++) {
            View vnew = parentLinearLayout.getChildAt(i);
            if (vnew instanceof EditText)
                edit_text.add(v.toString());
            if (vnew instanceof Spinner)
                spinner_text.add(v.toString());

        Gson gson = new Gson();

        String text= gson.toJson(edit_text);
        String spinner= gson.toJson(spinner_text);

        study.setExpData(text,spinner);

        setContentView(R.layout.activity_study_new);
        Intent intent = new Intent(this, _study_new.class);
        startActivity(intent);

    }


}
}