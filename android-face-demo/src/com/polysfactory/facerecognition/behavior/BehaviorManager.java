package com.polysfactory.facerecognition.behavior;

import java.util.Vector;

import android.R.bool;
import android.util.Log;

import com.polysfactory.facerecognition.App;
import com.polysfactory.facerecognition.AudioCommander;
import com.polysfactory.facerecognition.EmotionManager;
import com.polysfactory.facerecognition.UsbCommander;

/**
 * ロボットの振る舞いパターンを管理するクラス<br>
 * @author $Author$
 * @version $Revision$
 */
public class BehaviorManager {
    private Vector<Behavior> behaviorVector;

    private Behavior currentBehavior;

    private GreetToPersonBehavior greetToPersonBehavior;

    private Dance dance;

    private TalkBack talkBack;

    private Apologize apologize;

    private Shy shy;

    private Greeting greeting;

    private BehaviorInfo behaviorInfo;

    public class BehaviorInfo {
        /** 何らかのスレッドが実行中であることを示すフラグ */
        bool isRunning;

        /** スレッドを終了させるためのフラグ */
        bool stopFlag;

        /** 現在実行中のスレッドが割り込み可能かどうかを表すフラグ */
        bool canInterrupt;
    }

    public BehaviorManager(UsbCommander usbCommander, AudioCommander audioCommander, EmotionManager emotionManager) {
        // ランダムに能動的に行う振る舞いはVectorに突っ込む
        behaviorVector = new Vector<Behavior>();
        // behaviorVector.add(new Greeting());
        // behaviorVector.add(new UroUro());
        // behaviorVector.add(new KyoroKyoro());
        // behaviorVector.add(new Wondering());
        // behaviorVector.add(new Thinking());
        behaviorVector.add(new Guchi());
        // behaviorVector.add(new GuruGuru());
        // behaviorVector.add(new UroUro());
        behaviorVector.add(new Thinking());
        behaviorVector.add(new Sing());
        behaviorVector.add(new Sing2());

        // 外的要因によって受動的に行う振る舞いは個別のオブジェクトに持つ
        greetToPersonBehavior = new GreetToPersonBehavior();
        greetToPersonBehavior.mUsbCommander = usbCommander;
        greetToPersonBehavior.mAudioCommander = audioCommander;
        greetToPersonBehavior.mEmotionManager = emotionManager;

        dance = new Dance();
        dance.mUsbCommander = usbCommander;
        dance.mAudioCommander = audioCommander;
        dance.mEmotionManager = emotionManager;

        talkBack = new TalkBack();
        talkBack.mUsbCommander = usbCommander;
        talkBack.mAudioCommander = audioCommander;
        talkBack.mEmotionManager = emotionManager;

        apologize = new Apologize();
        apologize.mUsbCommander = usbCommander;
        apologize.mAudioCommander = audioCommander;
        apologize.mEmotionManager = emotionManager;

        shy = new Shy();
        shy.mUsbCommander = usbCommander;
        shy.mAudioCommander = audioCommander;
        shy.mEmotionManager = emotionManager;

        greeting = new Greeting();
        greeting.mUsbCommander = usbCommander;
        greeting.mAudioCommander = audioCommander;
        greeting.mEmotionManager = emotionManager;

        for (Behavior b : behaviorVector) {
            b.mUsbCommander = usbCommander;
            b.mAudioCommander = audioCommander;
            b.mEmotionManager = emotionManager;
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

    public void dance() {
        new Thread(dance).start();
    }

    private Behavior getNewBehavior() {
        int n = (int) (Math.random() * behaviorVector.size());
        return behaviorVector.get(n);
    }

    public void talkBack() {
        new Thread(talkBack).start();
    }

    public void apologize() {
        new Thread(apologize).start();
    }

    public void feelShy() {
        new Thread(shy).start();
    }

    public void greet() {
        new Thread(greeting).start();
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
