package com.polysfactory.facerecognition.behavior;

import android.util.Log;

import com.polysfactory.facerecognition.App;

/**
 * あいさつの振る舞い<br>
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 61 $
 */
public class Wondering extends Behavior {

    int eyeColor = 10;

    void nextEye() {
        // if (eyeColor == 0 || eyeColor == 3) {
        // eyeColor = 3 << 4;
        // } else if (eyeColor == (3 << 4)) {
        // eyeColor = 3 << 2;
        // } else if (eyeColor == (3 << 2)) {
        // eyeColor = 3;
        // }
        if (mUsbCommander != null) {
            mUsbCommander.lightLed(eyeColor);
        }
        eyeColor++;
        if (eyeColor >= 64) {
            eyeColor = 0;
        }
    }

    void randomHand() {
        int i = (int) (Math.random() * 7);
        switch (i) {
        case 0:
            mUsbCommander.rotateLeftHand(63);
            break;
        case 1:
            mUsbCommander.rotateRightHand(63);
            break;
        case 2:
            mUsbCommander.rotateNeck(63);
            break;
        case 3:
            mUsbCommander.rotateLeftHand(0);
            break;
        case 4:
            mUsbCommander.rotateRightHand(0);
            break;
        case 5:
            mUsbCommander.rotateNeck(32);
            break;
        case 6:
            mUsbCommander.rotateNeck(0);
            break;
        default:
            break;
        }
    }

    void randomMove() {
        int i = (int) (Math.random() * 6);
        switch (i) {
        case 0:
            mUsbCommander.forward();
            break;
        case 1:
            mUsbCommander.backward();
            break;
        case 2:
            mUsbCommander.spinTurnLeft();
            break;
        case 3:
            mUsbCommander.spinTurnRight();
            break;
        case 4:
            mUsbCommander.pivotTurnLeft();
            break;
        case 5:
            mUsbCommander.pivotTurnRight();
            break;
        default:
            break;
        }
    }

    @Override
    public void execute() {
        Log.d(App.TAG, "Greeting::action");
        for (int i = 0; i < 20; i++) {
            mUsbCommander.lightLed(eyeColor);
            sleep(100);
            randomHand();
            sleep(100);
            randomMove();
            sleep(5000);
        }
    }

}
