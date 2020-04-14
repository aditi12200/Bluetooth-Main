package com.example.myapplication65;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    Button scanbutton;
    ListView scanlistview;
//    Button keyboard;
    public ArrayList<String> stringArrayList=new ArrayList<>();
    public ArrayList<BluetoothDevice> deviceInfoList=new ArrayList();
    ArrayAdapter<String> arrayAdapter;
    BluetoothAdapter myAdapter=BluetoothAdapter.getDefaultAdapter();

    private final int REQUEST_PERMISSION_ACCESS_COARSE_LOCATION =1;
    Animation middleAnimation;
    TextView a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        a = findViewById(R.id.a);
        middleAnimation = AnimationUtils.loadAnimation(this, R.anim.middle_animation);
        a.setAnimation(middleAnimation);

        scanbutton=(Button) findViewById(R.id.scanbutton);
        scanlistview=(ListView) findViewById(R.id.scanlistview);
        Button btnONOFF = (Button) findViewById(R.id.btnONOFF);
//        keyboard=(Button) findViewById(R.id.keyboard);
        scanlistview.setOnItemClickListener(MainActivity.this);

        scanbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAdapter.startDiscovery();
                enableOFF();


            }
        });

        btnONOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableDisableBT();
            }

        });
//        keyboard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
//            }
//
//        });


        IntentFilter intentFilter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(myReceiever,intentFilter);
        arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,stringArrayList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);

                return view;
            }
        };
        scanlistview.setAdapter(arrayAdapter);
        showExplanation("Warning", "ask for permission", Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_ACCESS_COARSE_LOCATION);
        IntentFilter filter=new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(myReceiever1,filter);

    }
    @Override
    protected  void  onDestroy() {

        super.onDestroy();
        unregisterReceiver(myReceiever);
        unregisterReceiver(myReceiever1);
    }
    public void enableOFF(){
        if(!myAdapter.isEnabled()){
            Toast.makeText(MainActivity.this, "Enabling BT!", Toast.LENGTH_SHORT).show();
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(myReceiever,BTIntent);
        }

    }
    public void enableDisableBT(){
        if(myAdapter == null){
            Toast.makeText(MainActivity.this, "Does not have capabilities ", Toast.LENGTH_SHORT).show();
        }
        if(!myAdapter.isEnabled()){
            Toast.makeText(MainActivity.this, "Enabling BT!", Toast.LENGTH_SHORT).show();
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(myReceiever,BTIntent);
        }
        if(myAdapter.isEnabled()){Toast.makeText(MainActivity.this, "Disable!", Toast.LENGTH_SHORT).show();
            myAdapter.disable();


            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(myReceiever,BTIntent);

        }

    }

    BroadcastReceiver myReceiever=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();

            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String devicename=device.getName();
                boolean newdevice=true;
                for(BluetoothDevice d: deviceInfoList){
                    if(device.equals(d)){
                        newdevice=false;
                        break;
                    }
                }
                if(newdevice)
                    stringArrayList.add(device.getName());
                deviceInfoList.add(device);
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };
    BroadcastReceiver myReceiever1=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();

            if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //case1: Already bonded
                if(device.getBondState()==BluetoothDevice.BOND_BONDED){
                    Toast.makeText(MainActivity.this, "BONDED!", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(getApplicationContext(),page2.class);
                    myIntent.putExtra("bluetoothDevice",device);
                    startActivity(myIntent);
                }
                //case2: Creating a bond
                if(device.getBondState()==BluetoothDevice.BOND_BONDING){
                    Log.d("BTtag","Bonding");
                    Toast.makeText(MainActivity.this, "BONDING!", Toast.LENGTH_LONG).show();

                    
                }
                //case3: breaking a bond
                if(device.getBondState()==BluetoothDevice.BOND_NONE){
                    Log.d("BTtag","Not bonded");
                    Toast.makeText(MainActivity.this, "NOT BONDED!", Toast.LENGTH_SHORT).show();
                }

            }
//            else{
//                Set<BluetoothDevice> pairedDevices = myAdapter.getBondedDevices();
//                if (pairedDevices.size() > 0) {
//                    // Loop through paired devices
//                    for (BluetoothDevice device : pairedDevices) {
//
//
//                        Log.e("Mac Addressess","are:  "+myAdapter.getRemoteDevice(device.getName()));
//                    }
//                }
//            }
        }
    };
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }
    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        myAdapter.cancelDiscovery();
        Log.d("BTtag","You clicked on a device");
        String deviceName=deviceInfoList.get(i).getName();
        String deviceAddress=deviceInfoList.get(i).getAddress();
        Log.d("BTtag","Size is: "+deviceInfoList.size());
        Log.d("BTtag","You clicked on "+deviceName+" "+i);
        boolean canconnect=false;
        try {
            Set<BluetoothDevice> pairedDevices = myAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                // Loop through paired devices
                for (BluetoothDevice device : pairedDevices) {
                    if(deviceAddress.equals(device.getAddress())){
                        canconnect=true;
                        break;
                    }
                }
            }
        }catch(Exception e){
            Log.d("BTtag",e.getMessage());
        }
        if(Build.VERSION.SDK_INT> Build.VERSION_CODES.JELLY_BEAN_MR2){
            if(!canconnect) {
                Log.d("BTtag", "Trying to pair with " + deviceName);
                Toast.makeText(MainActivity.this, "Trying to pair with " + deviceName, Toast.LENGTH_SHORT).show();
                deviceInfoList.get(i).createBond();
            }else{
                Toast.makeText(MainActivity.this, "Connecting "+deviceName, Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(getApplicationContext(),page2.class);
                myIntent.putExtra("bluetoothDevice",deviceInfoList.get(i));
                startActivity(myIntent);
            }
        }
    }
}
