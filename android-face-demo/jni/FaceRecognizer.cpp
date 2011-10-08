#include "FaceRecognizer.h"
#include "log.h"

FaceRecognizer::FaceRecognizer () {
  haarFaceDetector = new HaarFaceDetector("", "", "");
}

FaceRecognizer::~FaceRecognizer () {
  delete(haarFaceDetector);
}

void FaceRecognizer::recognize(int input_idx, image_pool* pool)
{
  double scale = 3.0;
  double t = 0;
  LOGD("recognizeFace Start");
  
  IplImage srcImage = pool->getImage(input_idx);
  IplImage greyImage = pool->getGrey(input_idx);

  if (&srcImage == NULL) {
    LOGE("cannot get an image frame!\n");
    return;
  }  
}
