package com.google.android.DemoKit;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

public class Top extends Activity implements Runnable {
    private static final String TAG = "DemoKit";

    private static final String ACTION_USB_PERMISSION = "com.google.android.DemoKit.action.USB_PERMISSION";

    UsbAccessory mAccessory;

    int servo1Level = 0;

    UsbCommander usbCommander;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        usbCommander = new UsbCommander(this);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);

        if (getLastNonConfigurationInstance() != null) {
            mAccessory = (UsbAccessory) getLastNonConfigurationInstance();
            usbCommander.openAccessory(mAccessory);
        }

        setContentView(R.layout.top);
        Button stopMotor1Button = (Button) findViewById(R.id.StopMotor1);
        Button forwardMotor1Button = (Button) findViewById(R.id.ForwardMotor1);
        Button backwardMotor1Button = (Button) findViewById(R.id.BackwardMotor1);
        stopMotor1Button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                usbCommander.forward();
            }
        });
        forwardMotor1Button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                usbCommander.backward();
            }
        });
        backwardMotor1Button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                usbCommander.spinTurnLeft();
            }
        });
    }

    @Override
    protected void onDestroy() {
        usbCommander.unregisterReceiver();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        usbCommander.closeAccessory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        usbCommander.reopen();
    }

    protected void enableControls(boolean enable) {
    }

    public void run() {
        // TODO アクセサリからのデータの読み取り
    }

}
