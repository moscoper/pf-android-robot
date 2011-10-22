package com.polysfactory.facerecognition.behavior;

import com.polysfactory.facerecognition.AudioCommander;
import com.polysfactory.facerecognition.UsbCommander;

/**
 * 振る舞いパターンの基底インターフェース<br>
 * @author $Author$
 * @version $Revision$
 */
public abstract class Behavior implements Runnable {

    UsbCommander mUsbCommander;

    AudioCommander mAudioCommander;

    boolean isRunning;

    public boolean isRunning() {
        return isRunning;
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public abstract void execute();

    @Override
    public void run() {
        isRunning = true;
        execute();
        isRunning = false;
    }

}
