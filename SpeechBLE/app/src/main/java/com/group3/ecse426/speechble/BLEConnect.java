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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.*;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BLEConnect extends AppCompatActivity {


    public static String EXTRAS_DEVICE_NAME;
    public static String EXTRAS_DEVICE_ADDRESS;
    String mDeviceName;
    String mDeviceMode;
    String mDeviceAddress;
    public BluetoothAdapter mBluetoothAdapter;
    public BluetoothManager mBluetoothManager;
    public BluetoothGatt mBluetoothGatt;
    private static final String TAG = "BLEConnect: ";
    public BluetoothGattService mGattService;
    public BluetoothGattCharacteristic mEnable;
    boolean isXYZ;

    public short[] pitchvalues = new short[1000];
    public short[] rollvalues = new short[1000];
    public short[] micvalues = new short[16000];

    int pitch_index = 0;
    int roll_index = 0;
    int mic_index = 0;

    boolean pitchrollcomplete = false;
    boolean miccomplete = false;
    boolean readData = true;



    public FirebaseStorage storage = FirebaseStorage.getInstance();
    public StorageReference storageRef = storage.getReference();




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
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            // as soon as services are discovered, acquire characteristic and try enabling

            Log.d(TAG, "onServicesDiscovered CALLED! ");

            if(isXYZ) {
                Log.d(TAG, "Expecting PITCH/ROLL");
                mGattService = mBluetoothGatt.getService(UUID.fromString("02366E80-CF3A-11E1-9AB4-0002A5D5C51B"));
                mEnable = mGattService.getCharacteristic(UUID.fromString("340A1B80-CF4B-11E1-AC36-0002A5D5C51B"));
            }
            else{
                Log.d(TAG, "Expecting MIC");
                mGattService = mBluetoothGatt.getService(UUID.fromString("5E366E80-CF3A-11E1-9AB4-0002A5D5C51B"));
                mEnable = mGattService.getCharacteristic(UUID.fromString("1E366E80-CF3A-11E1-9AB4-0002A5D5C51B"));
            }
            if (mEnable == null) {
                Log.d(TAG, "Service not found");
                finish();
            }
            Log.d(TAG, "readCharacteristic");
            if(readData) {
                mBluetoothGatt.readCharacteristic(mEnable);
            }
        }

        /*
        This method is called after reading on characteristic successfully.

         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt , BluetoothGattCharacteristic characteristic, int status){
            super.onCharacteristicRead(gatt, characteristic, status);
            byte[] result = characteristic.getValue(); //data we receive from the characteristic is given to result

            //pitch/roll data
            if(isXYZ) {
                //packets are received in size 4, and we are to store 1000 values of both pitch and roll
                if(pitch_index < 1000 && roll_index < 1000) {
                    Log.d(TAG, "~~~~~~~~~~~~~~~~~~~~~~~ PITCH&ROLL ~~~~~~~~~~~~~~~~~~~ pitch_index ||| roll_index = " + pitch_index + " ||| " + roll_index);
                    int x = 0;
                    Log.d(TAG, "result.length = " + result.length);
                    int y = pitch_index;
                    int z = roll_index;
                    while(x < result.length){
                        if( x < (result.length)/2) {
                            pitchvalues[y] = (short) (result[x] << 8 | result[x + 1] & 0xFF); //convert byte to short
                            y = y + 1;
                        }
                        else{
                            rollvalues[z] = (short) (result[x] << 8 | result[x + 1] & 0xFF);
                            z = z + 1;
                        }
                        x = x + 2;
                    }
                    pitch_index = y;
                    roll_index = z;
                    //Display data
                    Log.d(TAG, "PITCH VALUES: " + Arrays.toString(pitchvalues));
                    Log.d(TAG, "ROLL VALUES : " + Arrays.toString(rollvalues));
                }
                else{
                    //sample received
                    pitchrollcomplete = true;
                    arrayFull();
                }
            }
            //mic data
            else{
                //packet size is 8, and we are to receive 16000 values
                if(mic_index < 16000) { //16000
                    Log.d(TAG, "~~~~~~~~~~~~~~~~~~~~~~~ MIC ~~~~~~~~~~~~~~~~~~~ mic_index = " + mic_index);
                    int i = 0;
                    int j = mic_index;
                    while(i < result.length){
                        micvalues[j] = (short) (result[i]<<8 | result[i+1] & 0xFF);
                        i = i + 2;
                        j++;
                    }
                    mic_index = j;
                    Log.d(TAG, "MIC VALUES: " + Arrays.toString(micvalues));
                }
                else{
                    //sample received
                    miccomplete = true;
                    arrayFull();
                }
            }
            onServicesDiscovered(gatt, status);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bleconnect);

        final Intent intent = getIntent();
        //mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME); //name not necessary
        mDeviceMode = "Not selected";
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        //initialize bluetooth manager and adapter
        boolean init_result = initialize();
        //check if manager & adapter are good
        if (!init_result){
            Toast.makeText(this, "BLE Adapter/Manager failure", Toast.LENGTH_SHORT).show();
            finish();
        }

        ((TextView) findViewById(R.id.device_mode)).setText("Device Mode: " + mDeviceMode);
        ((TextView) findViewById(R.id.device_address)).setText("Device Address: " + mDeviceAddress);

        Log.d(TAG, "mBluetoothManager = " + mBluetoothManager);
        Log.d(TAG, "mBluetoothAdapter = " + mBluetoothAdapter);

        Log.d(TAG, "mDeviceMode = " + mDeviceMode);
        Log.d(TAG, "mDeviceAddress = " + mDeviceAddress);

        //expecting to receive accelerometer data
        final Button xyz_button = findViewById(R.id.buttonxyz);
        xyz_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceMode = "Pitch/Roll";
                pitch_index = 0;
                roll_index = 0;
                readData = true;
                ((TextView) findViewById(R.id.device_mode)).setText("Device Mode: " + mDeviceMode);
                ((TextView) findViewById(R.id.pitchrollcomplete)).setText("Pitch/roll data required.");
                isXYZ = true;
            }
        });

        //expecting to receive microphone data
        final Button mic_button = findViewById(R.id.buttonmic);
        mic_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceMode = "MIC";
                mic_index = 0;
                readData = true;
                ((TextView) findViewById(R.id.device_mode)).setText("Device Mode: " + mDeviceMode);
                ((TextView) findViewById(R.id.miccomplete)).setText("Mic data required.");
                isXYZ = false;
            }
        });
        //connect to the selected device
        connectDevice(mDeviceAddress);
    }


    @Override
    protected void onPause() {
        super.onPause();
        closeGATT();
    }

    /*
    This method checks if both samples are full, and if so, calls firebase upload
     */
    private void arrayFull(){
        if(pitchrollcomplete && miccomplete){
            readData = false;
            Log.d(TAG, "readData = "+ readData);
            //closeGATT();
            try {
                startFirebase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(pitchrollcomplete){
                    ((TextView) findViewById(R.id.pitchrollcomplete)).setText("Pitch/roll data complete.");
                }
                if(miccomplete){
                    ((TextView) findViewById(R.id.miccomplete)).setText("Mic data complete.");
                }
            }
        });
    }

    //attempt to connect to selected device
    private void connectDevice(String address) {
        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "BLE Disabled :(", Toast.LENGTH_SHORT).show();
            this.finish();
        }
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mBluetoothGatt = device.connectGatt(this, false, mCallback);
        Log.d(TAG, "connectDevice");
    }

    //check if we can initialize our connection to Bluetooth Manger and Adapter
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



    /*
    call when GATT shutdown is desired -- transmission over
     */
    public void closeGATT() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void startFirebase() throws IOException {

        //store pitch values
        StorageReference pitchRef = storageRef.child("pitch_storage.txt");
        File pitchValuesFile = createTxt("pitchValuesFile", pitchvalues);
        uploadToFirebase(pitchValuesFile, pitchRef);

        //store roll values
        StorageReference rollRef = storageRef.child("roll_storage.txt");
        File rollValuesFile = createTxt("rollValuesFile", rollvalues);
        uploadToFirebase(rollValuesFile, rollRef);

        //store mic values
        StorageReference micRef = storageRef.child("mic_storage.txt");
        File micValuesFile = createTxt("micValuesFile", micvalues);
        uploadToFirebase(micValuesFile, micRef);

        //No errors, therefore great success
        Intent intent = new Intent(this, Success.class);
        startActivity(intent);
    }


    /*
    Create a .txt file to upload to Firebase
     */
    public File createTxt(String file_name, short[] data) throws IOException{
        String path = this.getFilesDir() + "/" + file_name;
        File curr = new File(path);
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(path));

        //display as a new line for each entry (for server side processing)
        for(int i = 0; i < data.length; i ++){
            outputWriter.write(data[i]+"\r\n");
        }
        outputWriter.flush();
        outputWriter.close();
        return curr;
    }

    public void uploadToFirebase(File currentFile, StorageReference currentRef){
        Uri file = Uri.fromFile(currentFile);
        Log.d("file.getPath()", "~~~~~~~~~~~~~~" + file.getPath());
        UploadTask uploadTask = currentRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("uploadFail", "" + exception);

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d(TAG, "Data successfully uploaded! " + downloadUrl);
            }
        });
    }

}

