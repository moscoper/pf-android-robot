/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.4
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.polysfactory.robotaudio.jni;

public class RobotAudio {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  public RobotAudio(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(RobotAudio obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        NativeSoundTouchJNI.delete_RobotAudio(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public RobotAudio(String inFile, String outFie) {
    this(NativeSoundTouchJNI.new_RobotAudio(inFile, outFie), true);
  }

  public int pitchShift(float pitch) {
    return NativeSoundTouchJNI.RobotAudio_pitchShift(swigCPtr, this, pitch);
  }

}
