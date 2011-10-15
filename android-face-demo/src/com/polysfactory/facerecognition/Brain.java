package com.polysfactory.facerecognition;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

import com.opencv.jni.image_pool;
import com.polysfactory.facerecognition.behavior.BehaviorManager;
import com.polysfactory.facerecognition.behavior.IBehavior;
import com.polysfactory.facerecognition.jni.FaceRecognizer;

/**
 * ロボットの自律的な行動を制御するためのクラス<br>
 * @author $Author$
 * @version $Revision$
 */
public class Brain extends Thread {
    /** 左腕の角度 */
    int leftHandDegere = 0;

    /** 前回までの左腕の回転方向 */
    int leftRotateWay = 0;

    /** 右腕の角度 */
    int rightHandDegree = 0;

    /** 前回までの右腕の回転方向 */
    int rightRotateWay = 0;

    /** 首の角度 */
    int NeckDegree = 0;

    /** 前回までの首の回転方向 */
    int neckRotateWay = 0;

    /** 目の色 */
    int eyeColor = 0;

    /** コンテキスト */
    Context mContext;

    UsbCommander mUsbCommander;

    /** 顔認識器 */
    FaceRecognizer mFaceRecognizer;

    /** 音声 */
    TextToSpeech tts;

    /** 直前に検出した時間 */
    long prevTime = 0;

    /** 直前した検出したオブジェクトのID */
    int prevObjId = -1;

    BehaviorManager mBehaviorManager;

    SpeechRecognizer sr;

    Handler handler = new Handler();

    Intent intent = new Intent();

    /**
     * コンストラクタ<br>
     * @param usbCommander
     */
    public Brain(Context context, UsbCommander usbCommander) {
        mContext = context;
        mUsbCommander = usbCommander;
        mFaceRecognizer = new FaceRecognizer();
        mBehaviorManager = new BehaviorManager(usbCommander);
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
        initSpeechRecognizer();
        // this.start();
    }

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
                mUsbCommander.forward();
                break;
            } else if (text.contains("停止") || text.contains("止まれ") || text.contains("止まって") || text.contains("ストップ")) {
                mUsbCommander.stop();
                break;
            } else if (text.contains("後退") || text.contains("戻れ") || text.contains("戻って") || text.contains("バック")) {
                mUsbCommander.backward();
                break;
            } else if (text.contains("旋回") || text.contains("回れ") || text.contains("回って")) {
                mUsbCommander.spinTurnLeft();
                break;
            }
        }
    }

    void nextEye() {
        // if (eyeColor == 0 || eyeColor == 3) {
        // eyeColor = 3 << 4;
        // } else if (eyeColor == (3 << 4)) {
        // eyeColor = 3 << 2;
        // } else if (eyeColor == (3 << 2)) {
        // eyeColor = 3;
        // }
        mUsbCommander.lightLed(eyeColor);
        eyeColor++;
        if (eyeColor >= 64) {
            eyeColor = 0;
        }
    }

    void degreeAdd(int objectId, int addedDegree) {
        int originalDegree;
        int rotateWay;
        switch (objectId) {
        case 0:
            originalDegree = leftHandDegere;
            rotateWay = leftRotateWay;
            break;
        case 1:
            originalDegree = rightHandDegree;
            rotateWay = rightRotateWay;
            break;
        case 2:
            originalDegree = NeckDegree;
            rotateWay = neckRotateWay;
            break;
        default:
            throw new RuntimeException("unknown object Id for rotate");
        }
        int lowThre = 0;
        int highThre = 63;
        if (objectId == 1) {
            lowThre = 20;
        } else if (objectId == 0) {
            highThre = 43;
        }

        if (rotateWay == 0) {
            if (originalDegree + addedDegree > highThre) {
                originalDegree = highThre;
                rotateWay = 1;
            } else {
                originalDegree += addedDegree;
            }
        } else if (rotateWay == 1) {
            if (originalDegree - addedDegree < lowThre) {
                originalDegree = lowThre;
                rotateWay = 0;
            } else {
                originalDegree -= addedDegree;
            }
        }
        mUsbCommander.rotateServo(objectId, originalDegree);
        switch (objectId) {
        case 0:
            leftHandDegere = originalDegree;
            leftRotateWay = rotateWay;
            break;
        case 1:
            rightHandDegree = originalDegree;
            rightRotateWay = rotateWay;
            break;
        case 2:
            NeckDegree = originalDegree;
            neckRotateWay = rotateWay;
            break;
        default:
            throw new RuntimeException("unknown object Id for rotate");
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
            if (timestamp - prevTime < 2000 && prevObjId == objId && !tts.isSpeaking()) {
                // // for test
                if (turn % 2 == 0) {
                    mUsbCommander.rotateNeck(63);
                } else {
                    mUsbCommander.rotateLeftHand(0);
                }
                tts.speak("こんにちは" + names[objId] + "さん！", TextToSpeech.QUEUE_FLUSH, null);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO 自動生成された catch ブロック
                    e.printStackTrace();
                }
                if (turn % 2 == 0) {
                    mUsbCommander.rotateNeck(32);
                } else {
                    mUsbCommander.rotateLeftHand(43);
                }
                turn++;
                // mUsbCommander.rotateRightHand(60);
                // try {
                // Thread.sleep(1000);
                // } catch (InterruptedException e) {
                // // TODO 自動生成された catch ブロック
                // e.printStackTrace();
                // }
                // mUsbCommander.rotateNeck(60);
                // try {
                // Thread.sleep(1000);
                // } catch (InterruptedException e) {
                // // TODO 自動生成された catch ブロック
                // e.printStackTrace();
                // }
                // mUsbCommander.lightLed(3, 0, 0);
            }
        }
        prevObjId = objId;
        prevTime = timestamp;
    }

    @Override
    public void run() {
        while (true) {
            /*
             * int interval = 16; // double d = Math.random() * 2; // interval = (int) (((double) interval) * d); int commandId = (int) (Math.random() * 3); switch (commandId) { case 0: degreeAdd(0,
             * interval); break; case 1: degreeAdd(1, interval); break; case 2: degreeAdd(2, interval); break; // case 3: // nextEye(); // break; // } }
             */
            IBehavior behavior = mBehaviorManager.getBehavior();
            behavior.action();
            mySleep(450);
        }
    }

    public void mySleep(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
