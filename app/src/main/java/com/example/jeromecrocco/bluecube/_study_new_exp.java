package com.example.jeromecrocco.bluecube;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class _study_new_exp extends AppCompatActivity {


    private LinearLayout parentLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_study_new_exp);
        parentLinearLayout = (LinearLayout) findViewById(R.id.study_new_exp);

        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    public void onAddField(View v) {
        Toast.makeText(this,"Add Field",Toast.LENGTH_LONG).show();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.content_field, null);
        // Add the new row before the add field button.
        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);

    }

    public void onDelete(View v) {
        Toast.makeText(this,"Delete Field",Toast.LENGTH_LONG).show();
        parentLinearLayout.removeView((View) v.getParent());
    }

}