package com.example.myapplication65;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class page2 extends AppCompatActivity {
    Button keyboard;
    Animation middleAnimation;
    TextView a;
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
            socket=device.createRfcommSocketToServiceRecord(device_UUID);
            socket.connect();
            dos = new DataOutputStream(socket.getOutputStream());
//            while(true) {
//                dos.writeChar('x');
//                if(breaknow)
//                 break;
//            }// for example
//            socket.close();
        } catch (IOException e) {
            Log.e("BTtag",e.getMessage());
        }

        Toast.makeText(this, ""+device.getName(), Toast.LENGTH_SHORT).show();
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
        Log.d(TAG,"You pressed "+keyCode);
        try {
            if (keyCode >= 29 && keyCode <= 54) {
                if (event.isCapsLockOn()) {
                    dos.writeInt(keyCode + 36);
                    Log.d(TAG,"AAya finally");
                    dos.flush();
                }else{
                    dos.writeInt(keyCode+68);
                    dos.flush();
                }
            }else if(keyCode>=7 && keyCode<=16){
                dos.writeInt(keyCode+41);
                dos.flush();
            }
            else {
                switch (keyCode) {
                    case 62: //space
                        dos.writeInt(32);
                        break;
                    case 66:// enter
                        dos.writeInt(10);
                        break;
                    case 67: //backspace
                        dos.writeInt(8);
                        break;
                    default:
                     break;
                }
            }
        }catch (Exception e){

        }
        return super.onKeyDown(keyCode, event);
        }
}
