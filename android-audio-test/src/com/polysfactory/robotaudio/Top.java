package com.polysfactory.robotaudio;

import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.polysfactory.robotaudio.jni.RobotAudio;

public class Top extends Activity {
    /** Called when the activity is first created. */
    MediaPlayer mp;

    EditText et;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button button1 = (Button) findViewById(R.id.Button1);
        Button button2 = (Button) findViewById(R.id.Button2);
        Button button3 = (Button) findViewById(R.id.Button3);
        et = (EditText) findViewById(R.id.pitchText);
        button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio("/sdcard/poly.wav");
            }
        });
        button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio("/sdcard/robot.wav");
            }
        });
        button3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                float shift = 0;
                try {
                    shift = Float.parseFloat(et.getText().toString());
                } catch (Exception e) {
                    Toast.makeText(Top.this, "数値を入力してください", Toast.LENGTH_LONG).show();
                    return;
                }
                if (shift > 60 || shift < -60) {
                    Toast.makeText(Top.this, "数値は -60以上 +60以下 の値を入力してください。", Toast.LENGTH_LONG).show();
                    return;
                }
                RobotAudio roboAudio = new RobotAudio();
                roboAudio.pitchShift(shift);
                Toast.makeText(Top.this, "変換しました", Toast.LENGTH_LONG).show();
            }
        });
    }

    void playAudio(String filePath) {
        if (mp != null) {
            mp.release();
            mp = null;
        }
        mp = new MediaPlayer();
        try {
            mp.setDataSource(filePath);
            mp.prepare();
            mp.start();
        } catch (IllegalArgumentException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
    }
}
