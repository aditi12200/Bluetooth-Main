package com.example.myapplication65;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class page2 extends AppCompatActivity {
    Button keyboard;
    Animation middleAnimation;
    TextView a;
    EditText Input;
    BluetoothDevice device;
    BluetoothSocket socket;
    DataOutputStream dos;
    boolean isCaps=false;
    private String TAG="BTtag";
    private static UUID device_UUID=UUID.fromString("d7d5d184-7e5f-11ea-bc55-0242ac130003");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_page2);
        a = findViewById(R.id.a);
        middleAnimation = AnimationUtils.loadAnimation(this, R.anim.middle_animation);
        a.setAnimation(middleAnimation);
        keyboard=(Button) findViewById(R.id.keyboard);
        Input=(EditText) findViewById(R.id.input) ;
        Input.setTextColor(Color.WHITE);
//        Input.setOnKeyListener(new EditText.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction()==KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
//                    onKeyDown(keyCode,event);
//                    Log.d(TAG,"Here");
//                    return true;
//                }
//                return false;
//            }
//        });
        Input.setEnabled(false);
        keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            }

        });
        device=getIntent().getParcelableExtra("bluetoothDevice");
        boolean breaknow=false;
        try {
            createConnection();
        } catch (IOException e) {
            Log.e("BTtag",e.getMessage());
            showErrorScreen();
        }

        Toast.makeText(this, ""+device.getName(), Toast.LENGTH_SHORT).show();
    }

    private void showErrorScreen() {
        AlertDialog.Builder a=new AlertDialog.Builder(page2.this);
        a.setMessage("Connection lost with the device! Please try again")
                .setNegativeButton("Back ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    createConnection();
                }catch (Exception e){
                    showErrorScreen();
                }
            }
        }).setCancelable(false);
        final AlertDialog p=a.create();
        p.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                p.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#ff0000"));
                p.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#008000"));
            }
        });

        p.setTitle("PC not responding");
        p.show();
    }

    @Override
    protected  void  onDestroy(){
        super.onDestroy();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.d(TAG,"You pressed "+
        String text;
        try {
            Log.d(TAG,"Unicode: "+event.getUnicodeChar());
            if(keyCode==67){
                dos.writeInt(8);
                text = Input.getText().toString();
                Input.setText(text.substring(0,text.length()-1));
                dos.flush();
            }else if(keyCode!=59){
                dos.writeInt(event.getUnicodeChar());
                text = Input.getText().toString();
                Input.setText(text+(char)event.getUnicodeChar());
                dos.flush();
            }

        }catch (Exception e){
           showErrorScreen();
        }
        return super.onKeyDown(keyCode, event);
        }

    private void createConnection() throws  IOException{
        socket=device.createRfcommSocketToServiceRecord(device_UUID);
        socket.connect();
        dos = new DataOutputStream(socket.getOutputStream());
    }

}
