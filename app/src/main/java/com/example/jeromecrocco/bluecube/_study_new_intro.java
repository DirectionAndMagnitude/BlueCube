package com.example.jeromecrocco.bluecube;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class _study_new_intro extends AppCompatActivity {

    EditText title;
    EditText motivation;
    EditText objective;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_new_intro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        title   = (EditText) findViewById(R.id.intro_title);
        motivation = (EditText) findViewById(R.id.intro_motivation);
        objective = (EditText) findViewById(R.id.intro_objective);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    public void onSave(View view){
        Toast.makeText(this,"Saving Text",Toast.LENGTH_SHORT).show();

        BlueCubeHandler dbHandler = new BlueCubeHandler(this, null, null, 1);

        int id   = 1;
        String t = title.getText().toString();
        String m = motivation.getText().toString();
        String o = objective.getText().toString();

        _study_class study = new _study_class(id,t,m,o);
        dbHandler.addHandler(study);

        setContentView(R.layout.activity_study_new);
        Intent intent = new Intent(this, MainActivity.class);

        i.putExtras("intro_data",dbHandler);

        startActivity(intent);


    }
}
