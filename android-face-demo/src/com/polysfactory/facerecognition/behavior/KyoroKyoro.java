package com.polysfactory.facerecognition.behavior;

import android.util.Log;

import com.polysfactory.facerecognition.App;

/**
 * ウロウロする振る舞い<br>
 * @author $Author$
 * @version $Revision$
 */
public class KyoroKyoro extends Behavior {

    @Override
    public void execute() {
        Log.d(App.TAG, "KyoroKyoro::action");
        mUsbCommander.lightLed(3, 0, 0);
        sleep(100);
        mUsbCommander.rotateNeck(63);
        sleep(300);
        mUsbCommander.lightLed(2, 2, 0);
        sleep(100);
        mUsbCommander.rotateNeck(32);
        sleep(300);
        mUsbCommander.stop();
        sleep(400);
        mUsbCommander.lightLed(0, 0, 0);
    }

}
