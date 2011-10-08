#include "FaceRecognizer.h"
#include "log.h"

FaceRecognizer::FaceRecognizer () {
  haarFaceDetector = new HaarFaceDetector("", "", "");
  objectMatcher = new ObjectMatcher();
  // load feature vector
  objectMatcher->loadDescription("");
}

FaceRecognizer::~FaceRecognizer () {
  delete(haarFaceDetector);
  delete(objectMatcher);
}

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
  
  CvRect* faceRect = haarFaceDetector->detectFace(&srcImage);
  cvSetImageROI(&srcImage, *faceRect);
  int objId = objectMatcher->match(&srcImage);
}
