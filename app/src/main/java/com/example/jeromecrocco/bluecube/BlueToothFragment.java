package com.example.jeromecrocco.bluecube;

/**
 * Created by jerome.crocco on 3/15/2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/** * A simple {@link Fragment} subclass.  */
public class BlueToothFragment extends Fragment {


    public BlueToothFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_connectbluetooth, container, false);
    }

}
