package com.example.jeromecrocco.bluecube;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

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


import static java.lang.Float.parseFloat;


public class _daq_bluetooth extends AppCompatActivity {

    // TAG is used for informational messages
    private final static String TAG = _daq_bluetooth.class.getSimpleName();

    // Variables to access objects from the layout such as buttons, switches, values
    private TextView mCapsenseValue;
    private Button start_button;
    private Button search_button;
    private Button connect_button;
    private Button discover_button;
    private Button disconnect_button;
    private Switch led_switch;





    private Button led_Button;

    private Button plotdata_button;

    // Variables to manage BLE connection
    private static boolean mConnectState;
    private static boolean mServiceConnected;
    private static _daq_bluetooth_PSOC mPSoCCapSenseLedService;
    private static final int REQUEST_ENABLE_BLE = 1;

    //This is required for Android 6.0 (Marshmallow)
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    // Keep track of whether CapSense Notifications are on or off
    private static boolean CapSenseNotifyState = false;
    /* This manages the lifecycle of the BLE service.
     * When the service starts we get the service object and initialize the service. */

    private FileWriter writer;

    String filename = "sensors_" + System.currentTimeMillis() + ".csv";
    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        /**
         * This is called when the _daq_bluetooth_PSOC is connected
         * @param componentName the component name of the service that has been connected
         * @param service service being bound
         */

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.i(TAG, "onServiceConnected");
            mPSoCCapSenseLedService = ((_daq_bluetooth_PSOC.LocalBinder) service).getService();
            mServiceConnected = true;
            mPSoCCapSenseLedService.initialize();
        }

        /**
         * This is called when the PSoCCapSenseService is disconnected.
         * @param componentName the component name of the service that has been connected
         */
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG, "onServiceDisconnected");
            mPSoCCapSenseLedService = null;
        }
    };

    @TargetApi(Build.VERSION_CODES.M) // This is required for Android 6.0 (Marshmallow) to work

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_daq_ble);

        // Set up a variable to point to the CapSense value on the display
        mCapsenseValue = (TextView) findViewById(R.id.capsense_value);
        start_button = (Button) findViewById(R.id.start_button);
        search_button = (Button) findViewById(R.id.search_button);
        connect_button = (Button) findViewById(R.id.connect_button);
        discover_button = (Button) findViewById(R.id.discoverSvc_button);
        disconnect_button = (Button) findViewById(R.id.disconnect_button);
        feedMultiple();


        // Initialize service and connection state variable
        mServiceConnected = false;
        mConnectState = false;



        //This section required for Android 6.0 (Marshmallow)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check 
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access ");
                builder.setMessage("Please grant location access so this app can detect devices.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        } //End of section for Android 6.0 (Marshmallow)

    }     /* This will be called when the LED On/Off switch is touched */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permission for 6.0:", "Coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
            }
        }
    } //End of section for Android 6.0 (Marshmallow)

    @Override
    protected void onResume() {
        super.onResume();
        // Register the broadcast receiver. This specified the messages the main activity looks for from the _daq_bluetooth_PSOC
        final IntentFilter filter = new IntentFilter();
        filter.addAction(_daq_bluetooth_PSOC.ACTION_BLESCAN_CALLBACK);
        filter.addAction(_daq_bluetooth_PSOC.ACTION_CONNECTED);
        filter.addAction(_daq_bluetooth_PSOC.ACTION_DISCONNECTED);
        filter.addAction(_daq_bluetooth_PSOC.ACTION_SERVICES_DISCOVERED);
        filter.addAction(_daq_bluetooth_PSOC.ACTION_DATA_RECEIVED);
        registerReceiver(mBleUpdateReceiver, filter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BLE && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBleUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close and unbind the service when the activity goes away
        mPSoCCapSenseLedService.close();
        unbindService(mServiceConnection);
        mPSoCCapSenseLedService = null;
        mServiceConnected = false;
    }

    public void startBluetooth(View view) {

        // Find BLE service and adapter
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLE);
        }

        // Start the BLE Service
        Log.d(TAG, "Starting BLE Service");
        Intent gattServiceIntent = new Intent(this, _daq_bluetooth_PSOC.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        // Disable the start button and turn on the search  button
        start_button.setEnabled(false);
        search_button.setEnabled(true);
        Log.d(TAG, "Bluetooth is Enabled");

    }

    public void searchBluetooth(View view) {
        if (mServiceConnected) {
            mPSoCCapSenseLedService.scan();
        }

        /* After this we wait for the scan callback to detect that a device has been found */
        /* The callback broadcasts a message which is picked up by the mGattUpdateReceiver */
    }

    public void connectBluetooth(View view) {
        mPSoCCapSenseLedService.connect();
        /* After this we wait for the gatt callback to report the device is connected */
        /* That event broadcasts a message which is picked up by the mGattUpdateReceiver */
    }

    public void discoverServices(View view) {
        /* This will discover both services and characteristics */
        /* After this we wait for the gatt callback to report the services and characteristics */
        /* That event broadcasts a message which is picked up by the mGattUpdateReceiver */
        setContentView(R.layout.sensor_val);



        FloatingActionButton fab_Home  = (FloatingActionButton) findViewById(R.id.fab_Home);
        FloatingActionButton fab_Start = (FloatingActionButton) findViewById(R.id.fab_Start);
        FloatingActionButton fab_Pause = (FloatingActionButton) findViewById(R.id.fab_Pause);
        FloatingActionButton fab_Reset = (FloatingActionButton) findViewById(R.id.fab_Reset);
        FloatingActionButton fab_Share = (FloatingActionButton) findViewById(R.id.fab_Share);
        Button setMTU = (Button) findViewById(R.id.setMTU);


        mChart      = buildChart();

        setMTU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText mtuTxt = (EditText) findViewById(R.id.MTU_VAL);
                int mtuVal = Integer.parseInt(mtuTxt.getText().toString());

                Snackbar.make(view, "SET MTU to " + mtuTxt.getText().toString(), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

                mPSoCCapSenseLedService.negotiateSamples(mtuVal);



            }});



        fab_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "DAQ STARTED", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();


                mPSoCCapSenseLedService.writeCapSenseNotification(true);
                CapSenseNotifyState = true;  // Keep track of CapSense notification state
            }});

        fab_Pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "DAQ PAUSED", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();


                mPSoCCapSenseLedService.writeCapSenseNotification(false);
                CapSenseNotifyState = false;  // Keep track of CapSense notification state
            }});

        fab_Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "DAQ RESET", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

                mChart.setData(null);

                mPSoCCapSenseLedService.writeCapSenseNotification(false);
                CapSenseNotifyState = false;  // Keep track of CapSense notification state
            }});

        fab_Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.activity_main);

                mPSoCCapSenseLedService.disconnect();

                Intent intent = new Intent(_daq_bluetooth.this, MainActivity.class);
                startActivity(intent);
            }});

        fab_Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create writer to write file
                Log.d(TAG, "Writing to " + getStorageDir());
                try {
                    writer = new FileWriter(new File(getStorageDir(), filename));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                LineData data = mChart.getLineData();

                ILineDataSet val = data.getDataSetByIndex(0);

                int nvals = val.getEntryCount();

                for (int ii = 0; ii < nvals; ii++) {
                    Entry v = val.getEntryForIndex(ii);
                    float x = v.getX();
                    float y = v.getY();

                    try {
                        writer.write(String.format("SENSOR, %f, %f\n", x, y));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                File newFile = new File(getStorageDir(), filename);

                String[] emails = {"Jerome.Crocco@gmail.com"};

                Uri contentUri = FileProvider.getUriForFile(_daq_bluetooth.this,
                        "com.example.jeromecrocco.bluecube.fileprovider",
                        newFile);

                Intent shareIntent = ShareCompat.IntentBuilder.from(_daq_bluetooth.this)
                        .setStream(contentUri)
                        .setText("Attached is Android Sensor Data") // uri from FileProvider
                        .setSubject("Android Sensor Data")
                        .setEmailTo(emails)
                        .setType(getContentResolver().getType(contentUri))
                        .getIntent()
                        .setAction(Intent.ACTION_SEND) //Change if needed
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(shareIntent);

            }});

        LineData data = mChart.getData();

        led_switch = (Switch) findViewById(R.id.led_switch);

        mPSoCCapSenseLedService.discoverServices();

        mPSoCCapSenseLedService.negotiateMTU(20);
        mPSoCCapSenseLedService.negotiateSamples(20);

        // Set Values
        mCapsenseValue = (TextView) findViewById(R.id.capsense_value);


        /* This will be called when the CapSense Notify On/Off switch is touched */

                led_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // Turn CapSense Notifications on/off based on the state of the switch


                    }
                });

            }

    /** * This method handles the Disconnect button
     * @param view the view object  */
    public void Disconnect(View view) {
        mPSoCCapSenseLedService.disconnect();

        /* After this we wait for the gatt callback to report the device is disconnected */
        /* That event broadcasts a message which is picked up by the mGattUpdateReceiver */
    }

    private String getStorageDir() {
        return getCacheDir().toString();
    }

    private void addEntry(byte[] val) {

        LineData data = mChart.getData();


        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            int v;
            //val.length
            try {
                for (v = 0; v < val.length; v = v + 1) {

                    try {

                        float value = val[v];
                        //                    data.addEntry(new Entry(set.getEntryCount(), value + 5), 0);

                        data.addEntry(new Entry(set.getEntryCount(), value + 5), 0);

                    } catch (Exception e) {
                        Log.d(TAG, "Error with parseFloat data");
                        //TODO:  Throwing errors after about 20 seconds of data?  Why?
                    }
                    ;
                }
            }catch (Exception e) {
                Log.d(TAG, "Error with parseFloat data");
                }
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(1500);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(1f);
        set.setColor(Color.BLUE);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;

    }

    private LineChart buildChart() {

        mChart = (LineChart) findViewById(R.id.chart_plot);
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
        leftAxis.setAxisMaximum(255f);
        leftAxis.setAxisMinimum(-255f);
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


    /**  * Listener for BLE event broadcasts */
    private final BroadcastReceiver mBleUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            switch (action) {
                case _daq_bluetooth_PSOC.ACTION_BLESCAN_CALLBACK:
                    // Disable the search button and enable the connect button
                    search_button.setEnabled(false);
                    connect_button.setEnabled(true);

                    break;

                case _daq_bluetooth_PSOC.ACTION_CONNECTED:
                    /* This if statement is needed because we sometimes get a GATT_CONNECTED */
                    if (!mConnectState) {
                        connect_button.setEnabled(false);
                        discover_button.setEnabled(true);
                        disconnect_button.setEnabled(true);
                        mConnectState = true;
                        Log.d(TAG, "Connected to Device");
                    }
                    break;
                case _daq_bluetooth_PSOC.ACTION_DISCONNECTED:
                    // Disable the disconnect, discover svc, discover char button, and enable the search button
                    disconnect_button.setEnabled(false);
                    discover_button.setEnabled(false);
                    search_button.setEnabled(true);
                    mConnectState = false;
                    Log.d(TAG, "Disconnected");
                    break;

                case _daq_bluetooth_PSOC.ACTION_SERVICES_DISCOVERED:
                    // Disable the discover services button
                    Log.d(TAG, "Enabling Discover Button");
                    discover_button.setEnabled(false);
                    Log.d(TAG, "Services Discovered");
                    // now we are changing the view and updating the buttons / signals


                    break;


                case _daq_bluetooth_PSOC.ACTION_DATA_RECEIVED:
                    // This is called after a notify or a read completes
                    Log.d(TAG, "Receiving Data");

                    // Get CapSense Slider Value
                    Log.d(TAG, "Receiving CAP SENSE DATA");
                    //String CapSensePos = mPSoCCapSenseLedService.getCapSenseValue();
                    boolean getData  = led_switch.isChecked();

                    if (getData) {
                       byte[] AnalogInVal = mPSoCCapSenseLedService.getAnalogInValue();
                       addEntry(AnalogInVal);
                    }



                    //float val = parseFloat(CapSensePos);
                    //Long tsLong = System.currentTimeMillis()/1000;
                    //String ts = tsLong.toString();
                    //writer.write(String.format("ACC, %f, %f, %f, %f, %f, %f\n", 0.f, 0.f, 0.f, 0.f, 0.f, 0.f));
                    //writer.write("DOG");
                    //writer.write(System.lineSeparator()); //new line

/*                    if (AnalogInVal[0] == "-1") {  // No Touch returns 0xFFFF which is -1
                        if (!CapSenseNotifyState) { // Notifications are off
                            mCapsenseValue.setText(R.string.NotifyOff);
                        } else { // Notifications are on but there is no finger on the slider
                            mCapsenseValue.setText(R.string.NoTouch);
                        }
                    } else { // Valid CapSense value is returned
                        mCapsenseValue.setText(AnalogInVal[0]);

                    }*/


/*                  // Check LED switch Setting
                    Log.d(TAG, "Receiving LED SWITCH DATA");
                    if (!mPSoCCapSenseLedService.getLedSwitchState()) {
                        led_switch.setChecked(true);
                    } else {
                        led_switch.setChecked(false);
                    }*/

                default:
                    break;
            }
        }
    };
};

