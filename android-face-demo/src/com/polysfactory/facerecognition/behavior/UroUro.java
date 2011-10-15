package com.polysfactory.facerecognition.behavior;

import android.util.Log;

import com.polysfactory.facerecognition.App;
import com.polysfactory.facerecognition.UsbCommander;

/**
 * ウロウロする振る舞い<br>
 * @author $Author$
 * @version $Revision$
 */
public class UroUro implements IBehavior {

    int mPhase;

    UsbCommander mUsbCommander;

    long time;

    public UroUro(UsbCommander usbCommander) {
        mUsbCommander = usbCommander;
        mPhase = 0;
    }

    @Override
    public void action() {
        Log.d(App.TAG, "UroUro::action:" + mPhase);
        long currentTime = System.currentTimeMillis();
        if (mPhase == 0) {
            mUsbCommander.forward();
            time = currentTime;
            mPhase = 1;
        } else if (mPhase == 1) {
            if (currentTime - time > 500) {
                // 前回コマンドの動作終了を待つため、最低500ミリ秒は間をあける
                mUsbCommander.backward();
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
