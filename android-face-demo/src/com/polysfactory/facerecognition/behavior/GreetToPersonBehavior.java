package com.polysfactory.facerecognition.behavior;

import android.util.Log;

import com.polysfactory.facerecognition.App;

public class GreetToPersonBehavior extends Behavior {
    String name;

    @Override
    public void execute() {
        Log.d(App.TAG, "Greeting::action");
        mUsbCommander.lightLed(0, 0, 3);
        sleep(100);
        mUsbCommander.rotateRightHand(50);
        mAudioCommander.speakByRobotVoie("こんにちわ、" + name + "さん！");
        sleep(300);
        mUsbCommander.lightLed(0, 3, 0);
        sleep(100);
        mUsbCommander.rotateRightHand(32);
        sleep(300);
        mUsbCommander.lightLed(0, 0, 0);
    }
}
