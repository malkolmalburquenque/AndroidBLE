package com.group3.ecse426.speechble;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    int gpsRequestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
            test if BLE support available on this device
            if not supported, TOAST
         */
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }


        /*
        ~~~~~~~~~~~~~~~~~~~~~~~~THE FOLLOWING LINE IS REQUIRED IN API 24+~~~~~~~~~~~~~~~~~~~~~~~~~~~
         */

        //requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, gpsRequestCode);



        //ble_search button initializes search for transmitter device
        final Button ble_search_button = findViewById(R.id.ble_search);
        ble_search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DeviceScanActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        if(requestCode != gpsRequestCode)
        {
            Toast.makeText(this, "Location services required", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}
