package com.polysfactory.facerecognition.behavior;

public class Thinking extends Behavior {

    @Override
    public void execute() {
        mAudioCommander.ringThinkingSound(0, 1.5F);
        mUsbCommander.lightLed(0, 0, 3);
        sleep(100);
        mUsbCommander.rotateNeck(10);
        sleep(1000);
        mUsbCommander.lightLed(0, 3, 0);
        sleep(100);
        mUsbCommander.rotateNeck(60);
        sleep(1000);
        mUsbCommander.lightLed(0, 2, 2);
        sleep(100);
        mUsbCommander.rotateNeck(32);
    }

}
