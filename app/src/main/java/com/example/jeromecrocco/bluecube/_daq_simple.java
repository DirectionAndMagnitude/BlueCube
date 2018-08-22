package com.example.jeromecrocco.bluecube;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class _daq_simple extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,SensorEventListener2 {

    SensorManager manager;
    private Sensor mSensor;

    boolean isRunning;
    final String TAG = "SensorLog";
    FileWriter writer;

    String filename = "sensors_" + System.currentTimeMillis() + ".csv";

    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_daq_simple);


        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        isRunning = false;

        mChart = buildChart();


        // Populate Spinner with list of internal sensors on device
        final Spinner sItems = (Spinner) findViewById(R.id.intsen_spinner);
        final List<String> spinnerArray = new ArrayList<String>();
        List<Sensor> sensorList = manager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor s : sensorList) {
            spinnerArray.add(s.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                _daq_simple.this,
                android.R.layout.simple_spinner_dropdown_item,
                spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItems.setAdapter(adapter);

        FloatingActionButton fab_Home  = (FloatingActionButton) findViewById(R.id.fab_Home);
        FloatingActionButton fab_Start = (FloatingActionButton) findViewById(R.id.fab_Start);
        FloatingActionButton fab_Pause = (FloatingActionButton) findViewById(R.id.fab_Pause);
        FloatingActionButton fab_Reset = (FloatingActionButton) findViewById(R.id.fab_Reset);
        FloatingActionButton fab_Share = (FloatingActionButton) findViewById(R.id.fab_Share);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //Initializaiton of thread
        feedMultiple();


        fab_Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.activity_main);
                Intent intent = new Intent(_daq_simple.this, MainActivity.class);
                startActivity(intent);

            }

        });

        fab_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "DAQ STARTED", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                // Create writer to write file
                Log.d(TAG, "Writing to " + getStorageDir());
                try {
                    writer = new FileWriter(new File(getStorageDir(), filename));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                List<Sensor> sensorList = manager.getSensorList(Sensor.TYPE_ALL);

                //Compare which spinner item is selected with all sensors on device.
                //Register selected sensor
                for (Sensor s : sensorList) {
                    if (s.getName() == sItems.getSelectedItem()) {
                        //TODO:  need to get index of accelerometer instead of hard code
                        manager.registerListener(_daq_simple.this, manager.getDefaultSensor(s.getType()), 0);
                    }
                }

                isRunning = true;

            }
        });

        fab_Pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "DAQ PAUSED", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                isRunning = false;
                manager.flush(_daq_simple.this);
                manager.unregisterListener(_daq_simple.this);
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


        fab_Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Resetting Data " + getStorageDir());
                try {
                    writer = new FileWriter(new File(getStorageDir(), filename));
                } catch (IOException e) {
                    e.printStackTrace();
                }


                LineData data = mChart.getData();
                data.removeDataSet(0);

                ILineDataSet set = createSet();
                data.addDataSet(set);

//            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 80) + 10f), 0);
                data.notifyDataChanged();

                // let the chart know it's data has changed
                mChart.notifyDataSetChanged();
/*
                // limit the number of visible entries
                mChart.setVisibleXRangeMaximum(150);
                // mChart.setVisibleYRange(30, AxisDependency.LEFT);

                // move to the latest entry
                mChart.moveViewToX(data.getEntryCount());
 */               }
            });

        fab_Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Need to put a try catch here in case directory is wrong

                File newFile = new File(getStorageDir(), filename);

                String[] emails = {"Jerome.Crocco@gmail.com"};

                Uri contentUri = FileProvider.getUriForFile(_daq_simple.this,
                        "com.example.jeromecrocco.bluecube.fileprovider",
                        newFile);

                Intent shareIntent = ShareCompat.IntentBuilder.from(_daq_simple.this)
                        .setStream(contentUri)
                        .setText("Attached is Android Sensor Data") // uri from FileProvider
                        .setSubject("Android Sensor Data")
                        .setEmailTo(emails)
                        .setType(getContentResolver().getType(contentUri))
                        .getIntent()
                        .setAction(Intent.ACTION_SEND) //Change if needed
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(shareIntent);

            }
        });


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            setContentView(R.layout.activity_main);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    //Three Dot Settings upper Right
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.daq_simple, menu);
        return true;
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            setContentView(R.layout.activity_main);
        }
        else if (id == R.id.nav_daq_start) {
            Toast.makeText(this,"Start",Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.nav_daq_stop) {
            Toast.makeText(this,"Stop",Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.nav_daq_share) {
            Toast.makeText(this,"Share",Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.nav_bluetooth) {
        }

        else if (id == R.id.nav_send) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onSensorChanged(SensorEvent evt) {
        if(isRunning) {
            if(plotData){
                addEntry(evt);
                plotData = false;
            }

            try {
                switch(evt.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        writer.write(String.format("%d, ACC, %f, %f, %f, %f, %f, %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], 0.f, 0.f, 0.f));
                        break;
                    case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                        writer.write(String.format("%d, GYRO_UN, %f, %f, %f, %f, %f, %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], evt.values[3], evt.values[4], evt.values[5]));
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        writer.write(String.format("%d, GYRO, %f, %f, %f, %f, %f, %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], 0.f, 0.f, 0.f));
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        writer.write(String.format("%d, MAG, %f, %f, %f, %f, %f, %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], 0.f, 0.f, 0.f));
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                        writer.write(String.format("%d, MAG_UN, %f, %f, %f, %f, %f, %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], 0.f, 0.f, 0.f));
                        break;
                    case Sensor.TYPE_ROTATION_VECTOR:
                        writer.write(String.format("%d, ROT, %f, %f, %f, %f, %f, %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], evt.values[3], 0.f, 0.f));
                        break;
                    case Sensor.TYPE_GAME_ROTATION_VECTOR:
                        writer.write(String.format("%d, GAME_ROT, %f, %f, %f, %f, %f, %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], evt.values[3], 0.f, 0.f));
                        break;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getStorageDir() {
        //  return this.getExternalFilesDir(null).getAbsolutePath();
        //  return "/storage/emulated/0/Android/data/com.iam360.sensorlog/";
        return getCacheDir().toString();
    }

    @Override
    public void onFlushCompleted(Sensor sensor) {

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    private void addEntry(SensorEvent evt) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

//            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 80) + 10f), 0);
            data.addEntry(new Entry(set.getEntryCount(), evt.values[0] + 5), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(150);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.BLUE);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;

    }

    private LineChart buildChart() {

        mChart = (LineChart) findViewById(R.id.chart1);
        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);


        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(10f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);

        return mChart;
    }

    private void feedMultiple() {

        if (thread != null){
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true){
                    plotData = true;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
        manager.unregisterListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }


    @Override
    protected void onDestroy() {
        manager.unregisterListener(_daq_simple.this);
        thread.interrupt();
        super.onDestroy();
    }




}
