package com.polysfactory.facerecognition.behavior;

import com.polysfactory.facerecognition.UsbCommander;

public class UroUro implements IBehavior {

    int mPhase;

    UsbCommander mUsbCommander;

    long time;

    public UroUro(UsbCommander usbCommander) {
        mUsbCommander = usbCommander;
        mPhase = 0;
    }

    @Override
    public void action() {
        long currentTime = System.currentTimeMillis();
        if (mPhase == 0) {
            mUsbCommander.rotateRightHand(50);
            time = currentTime;
            mPhase = 1;
        } else if (mPhase == 1) {
            if (currentTime - time > 500) {
                // 前回コマンドの動作終了を待つため、最低500ミリ秒は間をあける
                mUsbCommander.rotateRightHand(32);
                time = currentTime;
                mPhase = 2;
            }
        } else if (mPhase == 2) {
            if (currentTime - time > 500) {
                // 前回コマンドの動作終了を待つため、最低500ミリ秒は間をあける
                time = currentTime;
                mPhase = 3;
            }
        }
    }

    @Override
    public boolean isFinished() {
        return mPhase == 3;
    }

    @Override
    public void reset() {
        mPhase = 0;
    }

}
