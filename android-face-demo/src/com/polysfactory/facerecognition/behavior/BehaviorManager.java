package com.polysfactory.facerecognition.behavior;

import java.util.Vector;

import com.polysfactory.facerecognition.UsbCommander;

/**
 * ロボットの振る舞いパターンを管理するクラス<br>
 * @author $Author$
 * @version $Revision$
 */
public class BehaviorManager {
    Vector<IBehavior> behaviorVector;

    UsbCommander mUsbCommander;

    IBehavior currentBehavior;

    public BehaviorManager(UsbCommander usbCommander) {
        mUsbCommander = usbCommander;
        behaviorVector = new Vector<IBehavior>();
        behaviorVector.add(new UroUro(usbCommander));
    }

    private IBehavior getNewBehavior() {
        int n = (int) (Math.random() * behaviorVector.size());
        return behaviorVector.get(n);
    }

    public IBehavior getBehavior() {
        if (currentBehavior == null) {
            currentBehavior = getNewBehavior();
        } else if (currentBehavior.isFinished()) {
            currentBehavior.reset();
            currentBehavior = getNewBehavior();
        }
        return currentBehavior;
    }
}
