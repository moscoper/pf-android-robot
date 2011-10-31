package com.polysfactory.facerecognition.behavior;

import com.polysfactory.facerecognition.AudioCommander;
import com.polysfactory.facerecognition.EmotionManager;
import com.polysfactory.facerecognition.UsbCommander;

/**
 * 振る舞いパターンの基底インターフェース<br>
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 60 $
 */
public abstract class Playing implements Runnable {

    UsbCommander mUsbCommander;

    AudioCommander mAudioCommander;

    EmotionManager mEmotionManager;

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
