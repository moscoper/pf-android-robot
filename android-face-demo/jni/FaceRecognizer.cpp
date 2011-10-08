#include "FaceRecognizer.h"
#include "log.h"

/**
 * コンストラクタ
 */
FaceRecognizer::FaceRecognizer () {
  haarFaceDetector = new HaarFaceDetector(
    "/data/data/com.polysfactory.facerecognition/files/haarcascade_frontalface_default.xml",
    "/data/data/com.polysfactory.facerecognition/files/haarcascade_eye_tree_eyeglasses.xml",
    "/data/data/com.polysfactory.facerecognition/files/haarcascade_mcs_mouth.xml");
  objectMatcher = new ObjectMatcher();
  // load feature vector
  objectMatcher->loadDescription("");
}

/**
 * デコンストラクタ
 */
FaceRecognizer::~FaceRecognizer () {
  delete(haarFaceDetector);
  delete(objectMatcher);
}

/**
 * 顔検出&顔認識
 */
void FaceRecognizer::recognize(int input_idx, image_pool* pool)
{
  double scale = 3.0;
  double t = 0;
  LOGD("recognizeFace Start");
  
  IplImage srcImage = pool->getImage(input_idx);
  //IplImage greyImage = pool->getGrey(input_idx);

  if (&srcImage == NULL) {
    LOGE("cannot get an image frame!\n");
    return;
  }
  
  CvRect faceRect = haarFaceDetector->detectFace(&srcImage);
  cvSetImageROI(&srcImage, faceRect);
  int objId = objectMatcher->match(&srcImage);
}
