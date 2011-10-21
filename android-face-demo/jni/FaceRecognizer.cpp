#include "FaceRecognizer.h"
#include "log.h"

#define POINT_TL(r)  cvPoint(r.x, r.y)
#define POINT_BR(r)  cvPoint(r.x + r.width, r.y + r.height)
#define POINTS(r)  POINT_TL(r), POINT_BR(r)

/**
 * コンストラクタ
 */
FaceRecognizer::FaceRecognizer () {
  haarFaceDetector = new HaarFaceDetector(
    "/data/data/com.polysfactory.facerecognition/files/haarcascade_frontalface_default.xml",
    "/data/data/com.polysfactory.facerecognition/files/haarcascade_eye_tree_eyeglasses.xml",
    "/data/data/com.polysfactory.facerecognition/files/haarcascade_mcs_mouth.xml");  
  //eigen face recognizer
  eigenFace = new EigenFace("/data/data/com.polysfactory.facerecognition/files/facedata.xml");
  eigenFace->loadTrainingData();
  
  cvInitFont(&font, CV_FONT_HERSHEY_DUPLEX, 1.0, 1.0, 0, 3, 8);
}

/**
 * デコンストラクタ
 */
FaceRecognizer::~FaceRecognizer () {
  delete(haarFaceDetector);
}

void FaceRecognizer::learn(){
  eigenFace->loadTrainingData();
}

/**
 * 顔検出&顔認識
 */
int FaceRecognizer::recognize(int input_idx, image_pool* pool) {
  double scale = 3.0;
  double t = 0;
  LOGD("recognizeFace Start");
  
  IplImage srcImage = pool->getImage(input_idx);
  IplImage greyImage = pool->getGrey(input_idx);

  if (&greyImage == NULL) {
    LOGE("cannot get an image frame!\n");
    return -1;
  }
  
  CvRect faceRect;
  bool found = haarFaceDetector->detectFace(&greyImage, &faceRect);
  if (found) {
    cvRectangle(&srcImage, POINTS(faceRect), CV_RGB(255,255,0), 2, 8, 0);
    cvSetImageROI(&greyImage, faceRect);
    //int objId = objectMatcher->match(&greyImage);
    int objId = eigenFace->recognize(&greyImage);
    LOGD("objectId=%d", objId);
    char txt[256];
    sprintf(txt, "id=%d",objId);
    cvPutText(&srcImage, txt, POINT_TL(faceRect), &font, CV_RGB(0,255,255));
    return objId;
  } else {
    // 見つからない
    return -1;
  }
}
