package com.example.jeromecrocco.bluecube;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class StudentActivity extends AppCompatActivity {


    TextView lst;
    EditText studentid;
    EditText studentname;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        lst   = (TextView) findViewById(R.id.lst);
        studentid = (EditText) findViewById(R.id.studentid);
        studentname = (EditText) findViewById(R.id.studentname);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }



    public void loadStudents(View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        lst.setText(dbHandler.loadHandler());
        studentid.setText("");
        studentname.setText("");

    }


    public void addStudent(View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        int id = Integer.parseInt(studentid.getText().toString());
        String name = studentname.getText().toString();
        Student student = new Student(id, name);
        dbHandler.addHandler(student);
        studentid.setText("");
        studentname.setText("");

    }


    public void findStudent(View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        Student student =
                dbHandler.findHandler(studentname.getText().toString());
        if (student != null) {
            lst.setText(String.valueOf(student.getID()) + " " + student.getStudentName() + System.getProperty("line.separator"));
            studentid.setText("");
            studentname.setText("");
        } else {
            lst.setText("No Match Found");
            studentid.setText("");
            studentname.setText("");
        }
    }


    public void removeStudent(View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null,
                null, 1);
        boolean result = dbHandler.deleteHandler(Integer.parseInt(
                studentid.getText().toString()));
        if (result) {
            studentid.setText("");
            studentname.setText("");
            lst.setText("Record Deleted");
        } else
            studentid.setText("No Match Found");
    }

    public void updateStudent(View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null,
                null, 1);
        boolean result = dbHandler.updateHandler(Integer.parseInt(
                studentid.getText().toString()), studentname.getText().toString());
        if (result) {
            studentid.setText("");
            studentname.setText("");
            lst.setText("Record Updated");
        } else
            studentid.setText("No Match Found");
    }


}
