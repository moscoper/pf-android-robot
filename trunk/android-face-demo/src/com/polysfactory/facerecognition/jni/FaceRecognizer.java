/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.4
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.polysfactory.facerecognition.jni;

import com.opencv.jni.image_pool;// import the image_pool interface for playing nice with android-opencv

public class FaceRecognizer {
  private long swigCPtr;
  protected boolean swigCMemOwn;
  public FaceRecognizer(long cPtr, boolean cMemoryOwn) {
	swigCMemOwn = cMemoryOwn;
	swigCPtr = cPtr;
  }
  public static long getCPtr(FaceRecognizer obj) {
	return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        NativeFaceRecognizerJNI.delete_FaceRecognizer(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public FaceRecognizer() {
    this(NativeFaceRecognizerJNI.new_FaceRecognizer(), true);
  }

  public int recognize(int idx, image_pool pool) {
    return NativeFaceRecognizerJNI.FaceRecognizer_recognize(swigCPtr, this, idx, image_pool.getCPtr(pool), pool);
  }

}
