package com.polysfactory.facerecognition.behavior;

import android.net.Uri;
import android.util.Log;

import com.polysfactory.facerecognition.App;
import com.polysfactory.facerecognition.R;

public class Sing extends Behavior {

    @Override
    public void execute() {
        Log.d(App.TAG, "sing!");
        int n = (int) (2 * Math.random());

        switch (n) {
        case 0:
            mAudioCommander.playMusic(
                    Uri.parse("android.resource://com.polysfactory.facerecognition/" + R.raw.droidohatsuzuku)
                    );
            break;
        case 1:
            mAudioCommander.playMusic(
                    Uri.parse("android.resource://com.polysfactory.facerecognition/" + R.raw.polys_light_normal)
                    );
            break;
        default:
            break;
        }
    }

}
