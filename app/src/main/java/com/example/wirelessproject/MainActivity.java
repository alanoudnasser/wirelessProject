package com.example.wirelessproject;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;
    RecyclerView.Adapter adapter;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        linearLayout = findViewById(R.id.scrollViewChild);
      TextView textView = findViewById(R.id.myTextView);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, mainactivity2.class);
                startActivity(intent);
            }
        });
    }

    public void bluetoothOn(View view) {
        if (!mBluetoothAdapter.isEnabled()) {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
            startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
        }
    }

    public void bluetoothOff(View view) {
        if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
        mBluetoothAdapter.disable();
        startActivity(new Intent("android.bluetooth.adapter.action.REQUEST_DISABLE"));
    }

    public void bluetoothVisible(View view) {
        if (checkSelfPermission(Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_ADVERTISE}, 2);
        startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300));
    }

    public void pairDevice(View view) {
        if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices(); //GET THE NEAR DEVICES
        linearLayout.removeAllViews();

        TextView textView;
        textView = new TextView(MainActivity.this);
        textView.setText("hjhgjhg");
        linearLayout.addView(textView);
        for (BluetoothDevice device : pairedDevices) {
            textView = new TextView(MainActivity.this);
            textView.setText(device.getName() + " : " + device.getAddress());
            linearLayout.addView(textView);
        }
    }

    public void discoverDevices(View view) { //SEARCH FOR DEVICES
        Toast.makeText(getApplicationContext(), "Message to display", Toast.LENGTH_SHORT).show();
        if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN}, 2);

        if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);

        linearLayout.removeAllViews();
        mBluetoothAdapter.startDiscovery();
        registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    private final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                }
                TextView textView = new TextView(MainActivity.this);
                textView.setText(device.getName() + " : " + device.getAddress());
                linearLayout.addView(textView);
            }
        }
    };

    public void Send (View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,1);
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = null;
        uri = data.getData();
        //get path for image file
        Intent intent = new Intent(); // create new intent
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");  // add type of content that will be send.
        intent.putExtra(Intent.EXTRA_STREAM, uri); //add path of image
        //startActivity(Intent.createChooser(intent, null));            // to open messages or whatsapp to share
        PackageManager pm = getPackageManager();    // get a list of all applications that are capable of sending image file.
        List<ResolveInfo> appsList = pm.queryIntentActivities(intent, 0);
        if (appsList.size() > 0) {
            String packageName = null;
            String className = null;
            boolean found = false;
            for (ResolveInfo info : appsList) {
                packageName = info.activityInfo.packageName;
                if (packageName.equals("com.android.bluetooth")) {
                    className = info.activityInfo.name;
                    found = true;
                    break;// found
                }
            }
            if (found) {
                intent.setClassName(packageName, className);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(bReceiver);
        super.onDestroy();
    }
}