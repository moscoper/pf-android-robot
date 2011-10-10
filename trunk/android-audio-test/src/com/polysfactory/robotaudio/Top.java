package com.polysfactory.robotaudio;

import android.app.Activity;
import android.os.Bundle;

import com.polysfactory.robotaudio.jni.RobotAudio;

public class Top extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        RobotAudio robotAudio = new RobotAudio();
        robotAudio.execute();
    }
}
