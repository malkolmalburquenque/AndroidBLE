package com.group3.ecse426.speechble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

public class BLEConnect extends AppCompatActivity {


    public static String EXTRAS_DEVICE_NAME;
    public static String EXTRAS_DEVICE_ADDRESS;
    String mDeviceName;
    String mDeviceAddress;
    public BluetoothAdapter mBluetoothAdapter;
    public BluetoothManager mBluetoothManager;
    public BluetoothGatt mBluetoothGatt;
    private static final String TAG = "BLEConnect: ";
    private boolean mConnected = false;
    private TextView mConnectionState;
    public BluetoothGattService mGattService;
    public BluetoothGattCharacteristic mEnable;

    private BluetoothGattCallback mCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            switch (newState) {
                case BluetoothGatt.STATE_CONNECTED:
                    // as soon as we're connected, discover services
                    mBluetoothGatt.discoverServices();
                    Log.d(TAG, "onConnectionStateChange");
                    break;
            }
        }
    };

    //private BLEService mBluetoothLeService = new BLEService();

//    // Code to manage Service lifecycle.
//    private final ServiceConnection mServiceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder service) {
//            mBluetoothLeService = ((BLEService.LocalBinder) service).getService();
//            if (!mBluetoothLeService.initialize()) {
//                Log.e(TAG, "Unable to initialize Bluetooth");
//                finish();
//            }
//            // Automatically connects to the device upon successful start-up initialization.
//            mBluetoothLeService.connect(mDeviceAddress);
//            Log.d(TAG, "CONNECT CALLED! ");
//        }
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            mBluetoothLeService = null;
//        }
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bleconnect);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        boolean init_result = initialize();

        ((TextView) findViewById(R.id.device_name)).setText("Device Name: " + mDeviceName);
        ((TextView) findViewById(R.id.device_address)).setText("Device Address: " + mDeviceAddress);

        // Log.d(TAG, "init_result = " + init_result);

        Log.d(TAG, "mBluetoothManager = " + mBluetoothManager);
        Log.d(TAG, "mBluetoothAdapter = " + mBluetoothAdapter);

        Log.d(TAG, "mDeviceName = " + mDeviceName);
        Log.d(TAG, "mDeviceAddress = " + mDeviceAddress);

        connectDevice(mDeviceAddress);

//        Intent gattServiceIntent = new Intent(this, BLEService.class);
//        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
//        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
//
//        Log.d(TAG, "mBluetoothLeService = " + mBluetoothLeService);
//        mBluetoothLeService.connect(mDeviceAddress);
//
//        if (mBluetoothLeService != null) {
//            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
//            Log.d(TAG, "Connect request result = " + result);
//        }

    }


    private void connectDevice(String address) {
        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "BLE Disabled :(", Toast.LENGTH_SHORT).show();
            this.finish();
        }
        //mListener.onShowProgress();
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mBluetoothGatt = device.connectGatt(this, false, mCallback);
        Log.d(TAG, "connectDevice");
    }

//    @Override
//    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//        super.onServicesDiscovered(gatt, status);
//        // as soon as services are discovered, acquire characteristic and try enabling
//
//        Log.d(TAG, "onServicesDiscovered CALLED! ");
//
//        //// Why these values? ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~`
//        mGattService = mBluetoothGatt.getService(UUID.fromString("02366E80-CF3A-11E1-9AB4-0002A5D5C51B"));
//        mEnable = mGattService.getCharacteristic(UUID.fromString("340A1B80-CF4B-11E1-AC36-0002A5D5C51B"));
//        if (mEnable == null) {
//            Toast.makeText(this, "Service is not found",Toast.LENGTH_LONG).show();
//            this.finish();
//        }
//        mBluetoothGatt.readCharacteristic(mEnable);
//        //deviceConnected();        }
//    }

    //THIS IS WHERE WE RECEIVE OUR DATA
//    @Override
//    void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){
//        characteristic.getValue(); //set to our recieved data variable
//    }

    // Handles various events fired by the Service.
// ACTION_GATT_CONNECTED: connected to a GATT server.
// ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
// ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
// ACTION_DATA_AVAILABLE: received data from the device. This can be a
// result of read or notification operations.
//    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
//            if (BLEService.ACTION_GATT_CONNECTED.equals(action)) {
//                mConnected = true;
//                //updateConnectionState(R.string.connected);
//                invalidateOptionsMenu();
//            } else if (BLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
//                mConnected = false;
//                //updateConnectionState(R.string.disconnected);
//                invalidateOptionsMenu();
//                //clearUI();
//            } else if (BLEService.
//                    ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
//                // Show all the supported services and characteristics on the
//                // user interface.
//                //displayGattServices(mBluetoothLeService.getSupportedGattServices());
//            } else if (BLEService.ACTION_DATA_AVAILABLE.equals(action)) {
//                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
//            }
//        }
//    };
//
//    private void updateConnectionState(final int resourceId) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mConnectionState.setText(resourceId);
//            }
//        });
//    }
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
//        if (mBluetoothLeService != null) {
//            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
//            Log.d(TAG, "Connect request result=" + result);
//        }
//    }
//
//    private static IntentFilter makeGattUpdateIntentFilter() {
//        final IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(BLEService.ACTION_GATT_CONNECTED);
//        intentFilter.addAction(BLEService.ACTION_GATT_DISCONNECTED);
//        intentFilter.addAction(BLEService.ACTION_GATT_SERVICES_DISCOVERED);
//        intentFilter.addAction(BLEService.ACTION_DATA_AVAILABLE);
//        return intentFilter;
//    }
//
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }


//    private void clearUI() {
//        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
//        mDataField.setText(R.string.no_data);
//    }

}

