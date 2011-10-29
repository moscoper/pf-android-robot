package com.polysfactory.facerecognition;

import java.util.ArrayList;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Audio.Media;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.opencv.jni.image_pool;
import com.polysfactory.facerecognition.behavior.BehaviorManager;
import com.polysfactory.facerecognition.jni.FaceRecognizer;

/**
 * ロボットの自律的な行動を制御するためのクラス<br>
 * @author $Author$
 * @version $Revision$
 */
public class Brain {

    /** コンテキスト */
    Context mContext;

    UsbCommander mUsbCommander;

    /** 顔認識器 */
    FaceRecognizer mFaceRecognizer;

    /** 直前に検出した時間 */
    long prevTime = 0;

    /** 直前した検出したオブジェクトのID */
    int prevObjId = -1;

    BehaviorManager mBehaviorManager;

    SpeechRecognizer sr;

    Handler handler = new Handler();

    Intent intent = new Intent();

    volatile boolean stopThread = false;

    private AudioCommander mAudioCommander;

    private static final int INTERVAL = 20000;

    /**
     * コンストラクタ<br>
     * @param usbCommander
     */
    public Brain(Context context, UsbCommander usbCommander) {
        mContext = context;
        mUsbCommander = usbCommander;
        mAudioCommander = new AudioCommander(context);
        mFaceRecognizer = new FaceRecognizer();
        mBehaviorManager = new BehaviorManager(usbCommander, mAudioCommander);
        initSpeechRecognizer();
        // this.start();
        activeHandler.sendEmptyMessageDelayed(100, INTERVAL);
    }

    /**
     * 音声認識オブジェクトを初期化する<br>
     */
    void initSpeechRecognizer() {
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        sr = SpeechRecognizer.createSpeechRecognizer(mContext);
        sr.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onRmsChanged(float rmsdB) {
                // TODO Auto-generated method stub
                // Log.d(App.TAG, "onRmsChanged");
            }

            @Override
            public void onResults(Bundle results) {
                // TODO Auto-generated method stub
                Log.d(App.TAG, "onResults");
                ArrayList<String> array = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                for (String s : array) {
                    Log.d(App.TAG, s);
                }

                if (array.size() > 0) {
                    actionOnListening(array);
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sr.startListening(intent);
                    }
                }, 0);
            }

            @Override
            public void onReadyForSpeech(Bundle params) {
                // TODO Auto-generated method stub
                Log.d(App.TAG, "onReadyForSpeech");
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                // TODO Auto-generated method stub
                Log.d(App.TAG, "onPartialResults");
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // TODO Auto-generated method stub
                Log.d(App.TAG, "onEvent");
            }

            @Override
            public void onError(int error) {
                // TODO Auto-generated method stub
                Log.d(App.TAG, "onError:" + error);
                if (error != SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {
                    int delayed = 0;
                    if (error == SpeechRecognizer.ERROR_NETWORK) {
                        delayed = 10000;
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sr.startListening(intent);
                        }
                    }, delayed);
                }
            }

            @Override
            public void onEndOfSpeech() {
                // TODO Auto-generated method stub
                Log.d(App.TAG, "onEndOfSpeech");
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // TODO Auto-generated method stub
                // Log.d(App.TAG, "onBurfferReceived");
            }

            @Override
            public void onBeginningOfSpeech() {
                // TODO Auto-generated method stub
                Log.d(App.TAG, "onBeginningOfSpeech");
            }
        });
        sr.startListening(intent);
    }

    /**
     * 言葉の入力に対するアクションを行う<br>
     * @param text 入力文字列
     */
    void actionOnListening(ArrayList<String> array) {
        int len = Math.min(3, array.size());
        for (int i = 0; i < len; i++) {
            String text = array.get(i);
            Log.d(App.TAG, "actionOnListening:" + text);
            if (text.contains("前進") || text.contains("進め") || text.contains("進んで") || text.contains("ゴー")) {
                stopThread = true;
                mAudioCommander.stopMusic();
                if (mUsbCommander != null) {
                    mUsbCommander.forward();
                }
                break;
            } else if (text.contains("停止") || text.contains("止まれ") || text.contains("とまれ") || text.contains("止まって") || text.contains("ストップ")) {
                stopThread = true;
                mAudioCommander.stopMusic();
                if (mUsbCommander != null) {
                    mUsbCommander.stop();
                }
                break;
            } else if (text.contains("後退") || text.contains("戻れ") || text.contains("戻って") || text.contains("バック")) {
                stopThread = true;
                mAudioCommander.stopMusic();
                if (mUsbCommander != null) {
                    mUsbCommander.backward();
                }
                break;
            } else if (text.contains("旋回") || text.contains("回れ") || text.contains("回って") || text.contains("まわれ")) {
                stopThread = true;
                mAudioCommander.stopMusic();
                if (mUsbCommander != null) {
                    mUsbCommander.spinTurnLeft();
                    // mUsbCommander.pivotTurnLeft();
                }
                break;
            } else if (text.contains("こんにちわ") || text.contains("こんにちは")) {
                stopThread = true;
                mAudioCommander.speakByRobotVoie("こんにちは");
                break;
            } else if (text.contains("踊れ") || text.contains("踊って") || text.contains("おどれ")) {
                mAudioCommander.playMusic(ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, 29));
                stopThread = true;
                new OdoruThread().start();
            } else if (text.contains("ありがとう")) {
                stopThread = true;
                mAudioCommander.speakByRobotVoie("ありがとうございました！");
            } else if (text.contains("元気")) {
                stopThread = true;
                mAudioCommander.speakByRobotVoie("元気です！");
            }
        }
    }

    int turn = 0;

    /**
     * 入力された情報から次の動作を行う<br>
     * @param idx
     * @param pool
     * @param timestamp
     */
    public void process(int idx, image_pool pool, long timestamp) {
        Log.d(App.TAG, "process:" + timestamp);
        timestamp = timestamp / (1000 * 1000);
        int objId = mFaceRecognizer.recognize(idx, pool);
        String[] names = {"不明", "秋田", "ホソミ", "イガリ", "伊藤", "中澤", "古賀", "ナミカワ", "ハン", "佐藤", "ポリー", "山田", "ショウコ" };
        if (objId >= 0) {
            Log.d(App.TAG, "objId=" + objId + ",prevObjId=" + prevObjId + ",timediff=" + (timestamp - prevTime));
            if (timestamp - prevTime < 2000 && prevObjId == objId) {
                mBehaviorManager.greetToPerson(names[objId]);
            }
        }
        prevObjId = objId;
        prevTime = timestamp;
    }

    ActiveHandler activeHandler = new ActiveHandler();

    public class ActiveHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            mBehaviorManager.next();
            activeHandler.sendEmptyMessageDelayed(100, INTERVAL);
        }

    }

    public class OdoruThread extends Thread {
        public void run() {
            stopThread = false;
            while (true) {
                if (stopThread) {
                    break;
                }
                /*
                 * int interval = 16; // double d = Math.random() * 2; // interval = (int) (((double) interval) * d); int commandId = (int) (Math.random() * 3); switch (commandId) { case 0:
                 * degreeAdd(0, interval); break; case 1: degreeAdd(1, interval); break; case 2: degreeAdd(2, interval); break; // case 3: // nextEye(); // break; // } }
                 */
                mBehaviorManager.next();
                mySleep(100);
            }
        }
    }

    public void mySleep(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        stopThread = true;
        activeHandler.removeMessages(100);
        mAudioCommander.stopMusic();
    }
}
