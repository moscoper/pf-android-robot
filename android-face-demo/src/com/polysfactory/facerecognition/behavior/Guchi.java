package com.polysfactory.facerecognition.behavior;

/**
 * 愚痴<br>
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 61 $
 */
public class Guchi extends Behavior {

    @Override
    public void execute() {
        // mAudioCommander.speakByRobotVoie("ハハハッ！！血迷ったか？");
        // mAudioCommander.speakByRobotVoie("俺を倒せば世界は救われるんだぞ");どうした？手も足もでんか？そうかそうか、まあ人を一人殺す事になるのだからなあ！！ハハハハハハハハーーーーッ！！！！！！");
        mUsbCommander.lightLed(3, 0, 0);
        mAudioCommander.speakByRobotVoie("ザクとは違うのだよ、ザクとは！");
    }

}
