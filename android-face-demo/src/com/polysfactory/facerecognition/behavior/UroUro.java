package com.polysfactory.facerecognition.behavior;

import android.util.Log;

import com.polysfactory.facerecognition.App;

/**
 * ウロウロする振る舞い<br>
 * @author $Author$
 * @version $Revision$
 */
public class UroUro extends Behavior {

    @Override
    public void execute() {
        Log.d(App.TAG, "UroUro::action");
        mUsbCommander.forward();
        sleep(400);
        mUsbCommander.backward();
        sleep(400);
        mUsbCommander.stop();
        sleep(400);
    }
}
