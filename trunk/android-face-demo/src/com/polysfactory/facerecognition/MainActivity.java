package com.polysfactory.facerecognition;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.opencv.camera.NativePreviewer;
import com.opencv.camera.NativeProcessor;
import com.opencv.camera.NativeProcessor.PoolCallback;
import com.opencv.jni.Mat;
import com.opencv.jni.image_pool;
import com.opencv.jni.opencv;
import com.opencv.opengl.GL2CameraViewer;
import com.polysfactory.facerecognition.jni.BarBar;
import com.polysfactory.facerecognition.jni.BlinkDetector;
import com.polysfactory.facerecognition.jni.FooBarStruct;

public class MainActivity extends Activity {

    private static final String TAG = "Face";

    private final int FOOBARABOUT = 0;

    enum FaceDetectionMode {
        ViolaAndJones, BlinkDetection;
    }

    FaceDetectionMode faceDetectionMode = FaceDetectionMode.ViolaAndJones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Log.d("Face", "copy keypoints file");
            copy2Local();
        } catch (IOException e) {
            e.printStackTrace();
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FrameLayout frame = new FrameLayout(this);

        // Create our Preview view and set it as the content of our activity.
        mPreview = new NativePreviewer(getApplication(), 300, 300);

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.height = getWindowManager().getDefaultDisplay().getHeight();
        params.width = (int) (params.height * 4.0 / 2.88);

        LinearLayout vidlay = new LinearLayout(getApplication());

        vidlay.setGravity(Gravity.CENTER);
        vidlay.addView(mPreview, params);
        frame.addView(vidlay);

        // make the glview overlay ontop of video preview
        mPreview.setZOrderMediaOverlay(false);

        glview = new GL2CameraViewer(getApplication(), false, 0, 0);
        glview.setZOrderMediaOverlay(true);
        glview.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        frame.addView(glview);

        setContentView(frame);
    }

    /*
     * Handle the capture button as follows...
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        switch (keyCode) {
        case KeyEvent.KEYCODE_CAMERA:
        case KeyEvent.KEYCODE_SPACE:
        case KeyEvent.KEYCODE_DPAD_CENTER:
            // capture button pressed here
            return true;

        default:
            return super.onKeyUp(keyCode, event);
        }

    }

    /*
     * Handle the capture button as follows... On some phones there is no capture button, only trackball
     */
    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // capture button pressed
            return true;
        }
        return super.onTrackballEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(R.string.about_menu);
        menu.add("Viola&Jones");
        menu.add("BlinkDetection");
        return true;
    }

    private NativePreviewer mPreview;

    private GL2CameraViewer glview;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // example menu
        String title = item.getTitle().toString();
        if (title.equals(getString(R.string.about_menu))) {
            showDialog(FOOBARABOUT);
        } else if (title.equals("Viola&Jones")) {
            faceDetectionMode = FaceDetectionMode.ViolaAndJones;
        } else if (title.equals("BlinkDetection")) {
            faceDetectionMode = FaceDetectionMode.BlinkDetection;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        // IMPORTANT
        // must tell the NativePreviewer of a pause
        // and the glview - so that they can release resources and start back up
        // properly
        // failing to do this will cause the application to crash with no
        // warning
        // on restart
        // clears the callback stack
        mPreview.onPause();

        glview.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // resume the opengl viewer first
        glview.onResume();

        // add an initial callback stack to the preview on resume...
        // this one will just draw the frames to opengl
        LinkedList<NativeProcessor.PoolCallback> cbstack = new LinkedList<PoolCallback>();

        // SpamProcessor will be called first
        cbstack.add(new SpamProcessor());

        // then the same idx and pool will be passed to
        // the glview callback -
        // so operate on the image at idx, and modify, and then
        // it will be drawn in the glview
        // or remove this, and call glview manually in SpamProcessor
        // cbstack.add(glview.getDrawCallback());

        mPreview.addCallbackStack(cbstack);
        mPreview.onResume();

    }

    class SpamProcessor implements NativeProcessor.PoolCallback {

        FooBarStruct foo = new FooBarStruct();

        // Haar特徴量を利用したViola And Jonesのアルゴリズムによる顔検出器
        BarBar barbar = new BarBar();

        // 瞬き検出による顔検出器
        BlinkDetector blinkDetector = new BlinkDetector();

        @Override
        public void process(int idx, image_pool pool, long timestamp, NativeProcessor nativeProcessor) {

            // example of using the jni generated FoobarStruct;
            // int nImages = foo.pool_image_count(pool);
            // Log.i("foobar", "Number of images in pool: " + nImages);

            // Face[] faces = new Face[3];
            // Log.v(TAG, "getImage start");
            // Mat mat = pool.getImage(idx);
            // Log.v(TAG, "getImage end");
            // if (mat == null) {
            // Log.v(TAG, "pool.getImage is null");
            // return;
            // }
            // Bitmap bitmap = matToBitmap(mat);
            // FaceDetector faceDetector = new FaceDetector(bitmap.getWidth(), bitmap.getHeight(), faces.length);
            // int num = faceDetector.findFaces(bitmap, faces);
            // Log.v(TAG, num + " faces found.");

            if (FaceDetectionMode.ViolaAndJones == faceDetectionMode) {
                barbar.recognizeFace(idx, pool);
            } else if (FaceDetectionMode.BlinkDetection == faceDetectionMode) {
                blinkDetector.findFace(idx, pool);
            }

            // call a function - this function does absolutely nothing!
            // barbar.crazy();

            // sample processor
            // this gets called every frame in the order of the list
            // first add to the callback stack linked list will be the
            // first called
            // the idx and pool may be used to get the cv::Mat
            // that is the latest frame being passed.
            // pool.getClass(idx)

            // these are what the glview.getDrawCallback() calls
            glview.drawMatToGL(idx, pool);
            glview.requestRender();

        }
    }

    public static Bitmap matToBitmap(Mat mat) {
        Log.v(TAG, "cols=" + mat.getCols() + ", rows=" + mat.getRows() + ", channel=" + mat.channels());
        Bitmap bmap = Bitmap.createBitmap(mat.getCols(), mat.getRows(), Config.ARGB_8888);
        Log.v(TAG, "test1");
        ByteBuffer buffer = ByteBuffer.allocate(24 * bmap.getWidth() * bmap.getHeight());
        Log.v(TAG, buffer.remaining() + " remaining.");
        opencv.copyMatToBuffer(buffer, mat);
        Log.v(TAG, "test3");
        bmap.copyPixelsFromBuffer(buffer);
        Log.v(TAG, "test4");
        return bmap;
    }

    /**
     * assets以下のファイルをアプリのfilesディレクトリにコピーする<br>
     * @throws IOException IO例外
     */
    private void copy2Local() throws IOException {
        // assetsから読み込み、出力する
        String[] fileList = getResources().getAssets().list("haarcascades");
        if (fileList == null || fileList.length == 0) {
            return;
        }
        AssetManager as = getResources().getAssets();
        InputStream input = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

        for (String file : fileList) {
            String outFileName = "haarcascades" + "/" + file;
            Log.v("Face", "copy file:" + outFileName);
            input = as.open(outFileName);
            fos = openFileOutput(file, Context.MODE_WORLD_READABLE);
            bos = new BufferedOutputStream(fos);

            int DEFAULT_BUFFER_SIZE = 1024 * 4;

            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                fos.write(buffer, 0, n);
            }
            bos.close();
            fos.close();
            input.close();
        }
    }

}
