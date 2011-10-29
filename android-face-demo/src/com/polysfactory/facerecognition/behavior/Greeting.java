package com.polysfactory.facerecognition.behavior;

import android.util.Log;

import com.polysfactory.facerecognition.App;
import com.polysfactory.facerecognition.CommandUtils;

/**
 * あいさつの振る舞い<br>
 * @author $Author$
 * @version $Revision$
 */
public class Greeting extends Behavior {

    @Override
    public void execute() {
        Log.d(App.TAG, "Greeting::action");
        CommandUtils.randomEye(mUsbCommander);
        sleep(100);
        mUsbCommander.rotateRightHand(50);
        sleep(1000);
        mUsbCommander.lightLed(0, 3, 0);
        sleep(100);
        mUsbCommander.rotateRightHand(32);
        sleep(1000);
        mUsbCommander.lightLed(0, 0, 0);
    }

}
