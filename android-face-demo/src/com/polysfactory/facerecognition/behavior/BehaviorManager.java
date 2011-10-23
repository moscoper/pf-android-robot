package com.polysfactory.facerecognition.behavior;

import java.util.Vector;

import android.util.Log;

import com.polysfactory.facerecognition.App;
import com.polysfactory.facerecognition.AudioCommander;
import com.polysfactory.facerecognition.UsbCommander;

/**
 * ロボットの振る舞いパターンを管理するクラス<br>
 * @author $Author$
 * @version $Revision$
 */
public class BehaviorManager {
    Vector<Behavior> behaviorVector;

    UsbCommander mUsbCommander;

    Behavior currentBehavior;

    GreetToPersonBehavior greetToPersonBehavior;

    public BehaviorManager(UsbCommander usbCommander, AudioCommander audioCommander) {
        mUsbCommander = usbCommander;
        // ランダムに能動的に行う振る舞いはVectorに突っ込む
        behaviorVector = new Vector<Behavior>();
        // behaviorVector.add(new Greeting());
        // behaviorVector.add(new UroUro());
        // behaviorVector.add(new KyoroKyoro());
        // behaviorVector.add(new Wondering());
        behaviorVector.add(new Thinking());
        behaviorVector.add(new Guchi());

        // 外的要因によって受動的に行う振る舞いは個別のオブジェクトに持つ
        greetToPersonBehavior = new GreetToPersonBehavior();
        greetToPersonBehavior.mUsbCommander = usbCommander;
        greetToPersonBehavior.mAudioCommander = audioCommander;

        for (Behavior b : behaviorVector) {
            b.mUsbCommander = usbCommander;
            b.mAudioCommander = audioCommander;
        }
    }

    public void greetToPerson(String name) {
        greetToPersonBehavior.name = name;
        if (!greetToPersonBehavior.isRunning()) {
            new Thread(greetToPersonBehavior).start();
        } else {
            Log.w(App.TAG, "thread is running");
        }
    }

    private Behavior getNewBehavior() {
        int n = (int) (Math.random() * behaviorVector.size());
        return behaviorVector.get(n);
    }

    public void next() {
        currentBehavior = getNewBehavior();
        if (!currentBehavior.isRunning()) {
            new Thread(currentBehavior).start();
        } else {
            Log.w(App.TAG, "thread is running");
        }
    }
}
