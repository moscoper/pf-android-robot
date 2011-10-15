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
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.opencv.camera.NativePreviewer;
import com.opencv.camera.NativeProcessor;
import com.opencv.camera.NativeProcessor.PoolCallback;
import com.opencv.jni.Mat;
import com.opencv.jni.image_pool;
import com.opencv.jni.opencv;
import com.opencv.opengl.GL2CameraViewer;

/**
 * Androidくんメインアクティビティ<br>
 * @author $Author$
 * @version $Revision$
 */
public class MainActivity extends Activity {

    private final int FOOBARABOUT = 0;

    UsbCommander mUsbCommander;

    Brain brain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Log.d(App.TAG, "copy keypoints file");
            copy2Local("haarcascades");
            copy2Local("features");
        } catch (IOException e) {
            e.printStackTrace();
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

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

        // set Auto Focus
        mPreview.postautofocus(0);

        glview = new GL2CameraViewer(getApplication(), false, 0, 0);
        glview.setZOrderMediaOverlay(true);
        glview.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        frame.addView(glview);

        setContentView(frame);

        mUsbCommander = new UsbCommander(this);
        brain = new Brain(this, mUsbCommander);
    }

    @Override
    protected void onDestroy() {
        mUsbCommander.unregisterReceiver();
        super.onDestroy();
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

        // USBアクセサリ関連
        mUsbCommander.closeAccessory();
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
        cbstack.add(new Processor());

        // then the same idx and pool will be passed to
        // the glview callback -
        // so operate on the image at idx, and modify, and then
        // it will be drawn in the glview
        // or remove this, and call glview manually in SpamProcessor
        // cbstack.add(glview.getDrawCallback());

        mPreview.addCallbackStack(cbstack);
        mPreview.onResume();
        
        mUsbCommander.reopen();
    }

    class Processor implements NativeProcessor.PoolCallback {

        @Override
        public void process(int idx, image_pool pool, long timestamp, NativeProcessor nativeProcessor) {

            // example of using the jni generated FoobarStruct;
            /*
             * int nImages = foo.pool_image_count(pool); Log.i("foobar", "Number of images in pool: " + nImages); Face[] faces = new Face[3]; Log.v(TAG, "getImage start"); Mat mat =
             * pool.getImage(idx); Log.v(TAG, "getImage end"); if (mat == null) { Log.v(TAG, "pool.getImage is null"); return; } Bitmap bitmap = matToBitmap(mat); FaceDetector faceDetector = new
             * FaceDetector(bitmap.getWidth(), bitmap.getHeight(), faces.length); int num = faceDetector.findFaces(bitmap, faces); Log.v(TAG, num + " faces found.");
             */
            brain.process(idx, pool, timestamp);

            // these are what the glview.getDrawCallback() calls
            glview.drawMatToGL(idx, pool);
            glview.requestRender();

        }
    }

    public static Bitmap matToBitmap(Mat mat) {
        Bitmap bmap = Bitmap.createBitmap(mat.getCols(), mat.getRows(), Config.ARGB_8888);
        ByteBuffer buffer = ByteBuffer.allocate(24 * bmap.getWidth() * bmap.getHeight());
        opencv.copyMatToBuffer(buffer, mat);
        bmap.copyPixelsFromBuffer(buffer);
        return bmap;
    }

    /**
     * assets以下のファイルをアプリのfilesディレクトリにコピーする<br>
     * @throws IOException IO例外
     */
    private void copy2Local(String target) throws IOException {
        // assetsから読み込み、出力する
        String[] fileList = getResources().getAssets().list(target);
        if (fileList == null || fileList.length == 0) {
            return;
        }
        AssetManager as = getResources().getAssets();
        InputStream input = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

        for (String file : fileList) {
            String outFileName = target + "/" + file;
            Log.v(App.TAG, "copy file:" + outFileName);
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
