package com.group3.ecse426.bletofirebase;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    public ScanCallback mScanCallback;
    int REQUEST_ENABLE_BT;
    public BluetoothLeScanner mBluetoothLeScanner;

    public RecyclerView mRecyclerView;
    //public RecyclerView.Adapter = mAdapter;
    public MyItemRecyclerViewAdapter mRecycleViewAdapter;

    //private DeviceScanActivity mDeviceScanActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mDeviceScanActivity = new DeviceScanActivity(this);



        /*
            test if BLE support available on this device
            if not supported, TOAST
         */
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            // request bluetooth intent
            // This system activity will return once Bluetooth has completed turning on, or the user has decided not to turn Bluetooth on.
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT); //REQUEST_ENABLE_BT is int showing if they select BT on or BT off
        }

        //ble_search button initializes search for transmitter device
        final Button ble_search_button = findViewById(R.id.ble_search);
        ble_search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, R.string.ble_searching, Toast.LENGTH_SHORT).show();
               // beginBLEScanning();
               // startScan();
                //mBluetoothAdapter.getBluetoothLeScanner().startScan((ScanCallback) mLeScanCallback);
            }
        });

        if(Build.VERSION.SDK_INT >= 22){
            mScanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    Log.d("addDevice ", "add.!");


                }
            };
        }
    }

    //called from startActivityForResult()
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if(requestCode == ScanFragement.REQUEST_ENABLE_BT) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode== Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Bluetooth Disabled",Toast.LENGTH_SHORT).show();
                finish();
            }
            //else we are good, proceed
        }
    }

//    public void addDevice(BluetoothDevice device, int new_rssi) {
//
//        String address = device.getAddress();
//
//
//    }

//    public void startScan(){
//       // mBluetoothLeScanner.start();
//        mDeviceScanActivity.start();
//    }

//    private BluetoothAdapter.LeScanCallback mLeScanCallback =
//            new BluetoothAdapter.LeScanCallback() {
//                @Override
//                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord){
//                    final int new_rssi = rssi;
//                    if(rssi > -75) {
//                        addDevice(device, new_rssi);
//                    }
//                }
//            };

//
//    public void beginBLEScanning(){
//        Log.d("beginBLEScanning: ", "beginBLEScanning");
//        Toast.makeText(MainActivity.this, "beginBLEScanning called!", Toast.LENGTH_SHORT).show();
//        mScanCallback = new ScanCallback() {
//            @Override
//            public void onScanResult(int callbackType, ScanResult result) { //Callback when a BLE advertisement has been found
//                super.onScanResult(callbackType, result);
//                Log.d("beginBLEScanning: ", "onScanResult result: " + result.toString());
//                Toast.makeText(MainActivity.this, "onScanResult called!", Toast.LENGTH_SHORT).show();
//            }
//        };
//        Log.d("beginBLEScanning: ", "onScanResult mScanCallback: " + mScanCallback.toString());
//        //Go search for BLE Device
//        Intent intent = new Intent(this, DeviceScanActivity.class);
//        startActivity(intent);
//    }
}
