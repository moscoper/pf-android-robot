package com.polysfactory.facerecognition.behavior;

import android.util.Log;

import com.polysfactory.facerecognition.App;
import com.polysfactory.facerecognition.UsbCommander;

/**
 * ウロウロする振る舞い<br>
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 49 $
 */
public class UroUro2 implements IBehavior {

    int mPhase;

    UsbCommander mUsbCommander;

    long time;

    public UroUro2(UsbCommander usbCommander) {
        mUsbCommander = usbCommander;
        mPhase = 0;
    }

    @Override
    public void action() {
        Log.d(App.TAG, "UroUro2::action:" + mPhase);
        long currentTime = System.currentTimeMillis();
        if (mPhase == 0) {
            mUsbCommander.spinTurnLeft();
            time = currentTime;
            mPhase = 1;
        } else if (mPhase == 1) {
            if (currentTime - time > 500) {
                // 前回コマンドの動作終了を待つため、最低500ミリ秒は間をあける
                mUsbCommander.spinTurnRight();
                time = currentTime;
                mPhase = 2;
            }
        } else if (mPhase == 2) {
            if (currentTime - time > 500) {
                // 前回コマンドの動作終了を待つため、最低500ミリ秒は間をあける
                mUsbCommander.stop();
                time = currentTime;
                mPhase = 3;
            }
        }
    }

    @Override
    public boolean isFinished() {
        return mPhase == 3;
    }

    @Override
    public void reset() {
        mPhase = 0;
    }

}
