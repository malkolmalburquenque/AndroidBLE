package com.group3.ecse426.speechble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class DeviceConnected extends AppCompatActivity {


    public static String EXTRAS_DEVICE_NAME;
    public static String EXTRAS_DEVICE_ADDRESS;
    String mDeviceName;
    String mDeviceAddress;
    public BluetoothAdapter mBluetoothAdapter;
    public BluetoothGatt mBluetoothGatt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_device_connected);
        setContentView(R.layout.activity_device_connected);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        ((TextView) findViewById(R.id.device_name)).setText("Device Name: " + mDeviceName);
        ((TextView) findViewById(R.id.device_address)).setText("Device Address: " + mDeviceAddress);

        Log.d("DeviceConnected: mDeviceName ", mDeviceName);
        Log.d("DeviceConnected: mDeviceAddress = ", mDeviceAddress);
    }

    private void connectDevice(String address) {
        //mListener.onShowProgress();
        BluetoothDevice device= mBluetoothAdapter.getRemoteDevice(address);
        //mBluetoothGatt=device.connectGatt(this, false, mGattCallback);
        Log.d("BLE", "connectDevice");
    }

}
