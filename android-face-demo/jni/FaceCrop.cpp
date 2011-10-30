/*
顔を指定サイズで切り抜くためのプログラム
*/

#include "FaceCrop.h"

/**
 * コンストラクタ
 */
FaceCrop::FaceCrop () {
  haarFaceDetector = new HaarFaceDetector(
    "/data/data/com.polysfactory.facerecognition/files/haarcascade_frontalface_default.xml",
    "/data/data/com.polysfactory.facerecognition/files/haarcascade_eye_tree_eyeglasses.xml",
    "/data/data/com.polysfactory.facerecognition/files/haarcascade_mcs_mouth.xml");  
}

/**
 * デストラクタ
 */
FaceCrop::~FaceCrop () {
  delete(haarFaceDetector);
}

/**
 * 顔の部分だけ指定されたサイズで顔を切り抜く
 */
bool FaceCrop::crop (char* inFile) {
  IplImage* srcImage = cvLoadImage(inFile, CV_LOAD_IMAGE_ANYCOLOR);
  if (!srcImage) {
    LOGE("cannot find image file: %s", inFile);
    return -1;
  }
  CvRect faceRect;
  bool find = haarFaceDetector->detectFace(srcImage, &faceRect);
  if (find) {
    cvSetImageROI(srcImage, faceRect);
    IplImage* resizedFaceImage = cvCreateImage(cvSize(FACE_WIDTH, FACE_HEIGHT), IPL_DEPTH_8U, 3);
    cvResize(srcImage, resizedFaceImage, CV_INTER_AREA);
    int r = cvSaveImage(inFile, resizedFaceImage);
    if (r != 1) {
      LOGW("save error:%d", r);
    } else {
      LOGI("save success");
    }
    cvReleaseImage(&resizedFaceImage);
  } else {
    LOGW("can't find faces");
  }
  cvReleaseImage(&srcImage);  
  return find;
}