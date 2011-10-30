package com.polysfactory.facerecognition.behavior;

import com.polysfactory.facerecognition.CommandUtils;

public class Dance extends Behavior {

    @Override
    public void execute() {
        while (true) {
            CommandUtils.randomEye(mUsbCommander);
            sleep(100);
            CommandUtils.randomMove(mUsbCommander);
            sleep(400);
        }
    }

}
