package com.polysfactory.facerecognition.behavior;

import android.net.Uri;
import android.util.Log;

import com.polysfactory.facerecognition.App;
import com.polysfactory.facerecognition.R;

public class Sing2 extends Behavior {

    @Override
    public void execute() {
        Log.d(App.TAG, "sing2!");
        mAudioCommander.playMusic(
                Uri.parse("android.resource://com.polysfactory.facerecognition/" + R.raw.polys_light_normal)
                );
    }

}
