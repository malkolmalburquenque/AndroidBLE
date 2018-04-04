//package com.group3.ecse426.bletofirebase;
//
//import android.app.ListActivity;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothManager;
//import android.bluetooth.le.BluetoothLeScanner;
//import android.bluetooth.le.ScanCallback;
//import android.bluetooth.le.ScanSettings;
//import android.content.Context;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//
///**
// * Created by MalkolmAlburquenque on 2018-04-02.
// */
//
//public class DeviceScanActivity {
//
//    private MainActivity mainActivity;
//
//    private BluetoothAdapter mBluetoothAdapter;
//    private boolean mScanning;
//    //private Handler mHandler;
//
//    private long scanPeriod;
//    private int signalStrength;
//
//
//    public DeviceScanActivity(MainActivity mainActivity){
//        this.mainActivity = mainActivity;
//        //mHandler = new Handler();
//        this.scanPeriod = 7500;
//        this.signalStrength = -75;
//
//        final BluetoothManager bluetoothManager = (BluetoothManager) mainActivity.getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = bluetoothManager.getAdapter();
//    }
//
//    public boolean isScanning() {
//        return mScanning;
//    }
//
//    public void start(){
//        scanLeDevice(true);
//    }
//
//    public void stop(){
//        scanLeDevice(false);
//    }
//
//
//    private void scanLeDevice(final boolean enable){
//        //enable determines if we should enable or disable scan
//
//        if(enable && !mScanning) {
//            Log.d("scanLeDevice ", "starting BLE SCAN !! ");
//            //Toast.makeText(DeviceScanActivity.this,  "Starting BLE Scan :) " , Toast.LENGTH_SHORT).show();
//            Toast.makeText(mainActivity, "STARTING BLE SCAN !! :)", Toast.LENGTH_SHORT).show();
//
//
//            mScanning = true;
//            //mBluetoothAdapter.startLeScan(mLeScanCallback);
//            mBluetoothAdapter.getBluetoothLeScanner().startScan((ScanCallback) mLeScanCallback);
//        }
//    }
//
//    private BluetoothAdapter.LeScanCallback mLeScanCallback =
//            new BluetoothAdapter.LeScanCallback() {
//                @Override
//                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord){
//                    final int new_rssi = rssi;
//                    if(rssi > signalStrength) {
//                        mainActivity.addDevice(device, new_rssi);
//                    }
//                }
//            };
//
//
//
//
//
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_device_scan);
////
////
////        if (mLeScanner == null)
////            mLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
////        // start scan in low latency mode
////        mLeScanner.startScan(mScanCallback);
////        Log.d("DeviceScanActivity", "DeviceScanActivity onCreate()! ");
////    }
//
//}