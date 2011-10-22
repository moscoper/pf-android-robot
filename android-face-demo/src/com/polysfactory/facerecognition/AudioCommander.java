package com.polysfactory.facerecognition;

import java.io.IOException;
import java.util.Locale;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

import com.polysfactory.robotaudio.jni.RobotAudio;

/**
 * 音声関連の命令を実行するクラス<br>
 * @author $Author$
 * @version $Revision$
 */
public class AudioCommander {
    /** ロボット声加工機 */
    RobotAudio robotAudio = new RobotAudio("/sdcard/poly.wav", "/sdcard/robot.wav");

    /** メディアプレイヤー */
    MediaPlayer mp = null;

    /** 音声 */
    TextToSpeech tts;

    Context mContext;

    public AudioCommander(Context context) {
        mContext = context;
        // setup TextToSpeech object
        tts = new TextToSpeech(context, new OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // ロケールの指定
                    // Locale locale = Locale.ENGLISH;
                    Locale locale = Locale.JAPANESE;
                    if (tts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
                        tts.setLanguage(locale);
                        tts.setPitch(0.5F);
                        tts.setSpeechRate(0.7F);
                    } else {
                        Log.e(App.TAG, "Error SetLocale");
                    }
                }
            }
        });
    }

    public void speakByRobotVoie(String text) {
        tts.synthesizeToFile(text, null, "/sdcard/poly.wav");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            // TODO 自動生成された catch ブロック
            e1.printStackTrace();
        }
        robotAudio.pitchShift(0);
        if (mp != null) {
            mp.release();
            mp = null;
        }
        mp = new MediaPlayer();
        try {
            mp.setDataSource("/sdcard/robot.wav");
            mp.prepare();
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
        mp.start();
    }

    public void playMusic(Uri audioUri) {
        if (mp != null) {
            mp.release();
            mp = null;
        }
        mp = new MediaPlayer();
        try {
            mp.setDataSource(mContext, audioUri);
            mp.prepare();
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
        mp.start();
    }

    public void stopMusic() {
        if (mp != null) {
            mp.release();
            mp = null;
        }
    }

}
