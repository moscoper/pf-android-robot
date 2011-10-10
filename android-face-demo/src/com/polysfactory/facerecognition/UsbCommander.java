package com.polysfactory.facerecognition;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;
import com.polysfactory.facerecognition.usbprotocol.LedCommand;
import com.polysfactory.facerecognition.usbprotocol.ServoCommand;

/**
 * USBに対してロボット制御命令を発行するためのクラス<br>
 * @author $Author$
 * @version $Revision$
 */
public class UsbCommander extends BroadcastReceiver {
    /** タグ */
    private static final String TAG = "PFFaceDetector_Java";

    private static final String ACTION_USB_PERMISSION = "com.polysfactory.androidkun.USB_PERMISSION";

    private UsbManager mUsbManager;

    private UsbAccessory mAccessory;

    private boolean mPermissionRequestPending;

    private PendingIntent mPermissionIntent;

    private ParcelFileDescriptor mFileDescriptor;

    /** USBアクセサリからの入力ストリーム */
    private FileInputStream mInputStream;

    /** USBアクセサリへの出力 */
    private FileOutputStream mOutputStream;

    private Context mContext;

    public UsbCommander(Context context) {
        mContext = context;
        mUsbManager = UsbManager.getInstance(context);
        mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        context.registerReceiver(this, filter);

        // if (getLastNonConfigurationInstance() != null) {
        // mAccessory = (UsbAccessory) context.getLastNonConfigurationInstance();
        // this.openAccessory(mAccessory);
        // }
    }

    public void unregisterReceiver() {
        mContext.unregisterReceiver(this);
    }

    public void reopen() {
        // USBアクセサリ関連
        if (mInputStream == null || mOutputStream == null) {
            Log.d(TAG, "reopen");
            UsbAccessory[] accessories = mUsbManager.getAccessoryList();
            UsbAccessory accessory = (accessories == null ? null : accessories[0]);
            if (accessory != null) {
                Log.d(TAG, "accessory is not null");
                if (mUsbManager.hasPermission(accessory)) {
                    this.openAccessory(accessory);
                } else {
                    synchronized (this) {
                        if (!mPermissionRequestPending) {
                            mUsbManager.requestPermission(accessory, mPermissionIntent);
                            mPermissionRequestPending = true;
                        }
                    }
                }
            } else {
                Log.d(TAG, "mAccessory is null");
            }
        }
    }

    /**
     * USBアクセサリをオープンする<br>
     * @param accessory
     */
    public void openAccessory(UsbAccessory accessory) {
        Log.d(TAG, "open USBAccessory");
        mFileDescriptor = mUsbManager.openAccessory(accessory);
        if (mFileDescriptor != null) {
            mAccessory = accessory;
            FileDescriptor fd = mFileDescriptor.getFileDescriptor();
            mInputStream = new FileInputStream(fd);
            mOutputStream = new FileOutputStream(fd);
            // Thread thread = new Thread(null, this, "DemoKit");
            // thread.start();
            Log.d(TAG, "accessory opened");
            enableControls(true);
        } else {
            Log.d(TAG, "accessory open fail");
        }
    }

    /**
     * USBアクセサリをクローズする<br>
     */
    public void closeAccessory() {
        Log.d(TAG, "close USBAccessory");
        enableControls(false);

        try {
            if (mFileDescriptor != null) {
                mFileDescriptor.close();
            }
        } catch (IOException e) {
        } finally {
            mFileDescriptor = null;
            mAccessory = null;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_USB_PERMISSION.equals(action)) {
            synchronized (this) {
                UsbAccessory accessory = UsbManager.getAccessory(intent);
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    this.openAccessory(accessory);
                } else {
                    Log.d(TAG, "permission denied for accessory " + accessory);
                }
                mPermissionRequestPending = false;
            }
        } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
            UsbAccessory accessory = UsbManager.getAccessory(intent);
            if (accessory != null && accessory.equals(mAccessory)) {
                this.closeAccessory();
            }
        }
    }

    protected void enableControls(boolean enable) {
    }

    /**
     * 左腕を動かす<br>
     * @param degree
     */
    public void rotateLeftHand(int degree) {
        Log.d(TAG, "rotateLeftHand:" + degree);
        rotateServo(0, degree);
    }

    /**
     * 右腕を動かす<br>
     * @param degree
     */
    public void rotateRightHand(int degree) {
        Log.d(TAG, "rotateRightHand:" + degree);
        rotateServo(1, degree);
    }

    /**
     * 首を動かす<br>
     * @param degree
     */
    public void rotateNeck(int degree) {
        Log.d(TAG, "rotateNeck:" + degree);
        rotateServo(2, degree);
    }

    /**
     * サーボ回転命令を発行する<br>
     * @param servoId
     * @param degree
     */
    public void rotateServo(int servoId, int degree) {
        if (mOutputStream != null) {
            ServoCommand servoCommand = new ServoCommand();
            servoCommand.setServoDegree(servoId, degree);
            try {
                mOutputStream.write(servoCommand.toBytes());
            } catch (IOException e) {
                Log.e(TAG, "rotateLeftHandError", e);
            }
        }
    }

    public void lightLed(int color) {
        if (mOutputStream != null) {
            LedCommand ledCommand = new LedCommand();
            ledCommand.setLedScale(0, color);
            ledCommand.setLedScale(1, color);
            try {
                mOutputStream.write(ledCommand.toBytes());
            } catch (IOException e) {
                Log.e(TAG, "rotateLeftHandError", e);
            }
        }
    }
    
    public void lightLed(int red, int green, int blue) {
        Log.d(TAG, "lightLed:(" + red + "," + green + "," + blue + ")");
        if (mOutputStream != null) {
            LedCommand ledCommand = new LedCommand();
            ledCommand.setLedScale(0, red, green, blue);
            ledCommand.setLedScale(1, red, green, blue);
            try {
                mOutputStream.write(ledCommand.toBytes());
            } catch (IOException e) {
                Log.e(TAG, "rotateLeftHandError", e);
            }
        }
    }
}
