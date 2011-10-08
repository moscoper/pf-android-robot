#include "HaarFaceDetector.h"
#include "log.h"

//const String haarcascade_face = "/data/data/com.polysfactory.facerecognition/files/haarcascade_frontalface_alt.xml";
//const String haarcascade_face = "/data/data/com.polysfactory.facerecognition/files/haarcascade_frontalface_default.xml";
//const String haarcascade_eye = "/data/data/com.polysfactory.facerecognition/files/haarcascade_eye_tree_eyeglasses.xml";

HaarFaceDetector::HaarFaceDetector (const char* haarcascadeFaceFile, const char* haarcascadeEyeFile, const char* haarcascadeMouthFile)
{
  cascade_face = (CvHaarClassifierCascade*)cvLoad(haarcascadeFaceFile);
  if (cascade_face == NULL) {
    LOGE("ERROR: Could not load classifier cascade face\n");
    return;  
  }
  cascade_eye  = (CvHaarClassifierCascade*)cvLoad(haarcascadeEyeFile);
  if (cascade_eye == NULL) {
    LOGE("ERROR: Could not load classifier cascade eye\n");
    return;  
  }
  cascade_mouth = (CvHaarClassifierCascade*)cvLoad(haarcascadeMouthFile);
  if (cascade_mouth == NULL) {
    LOGE("ERROR: Could not load classifier cascade mouth\n");
    return;
  }
  //if(!cascade_face.load(haarcascadeFaceFile)){
    //__android_log_write(ANDROID_LOG_DEBUG, "Face", "ERROR: Could not load classifier cascade");
    //LOGE("ERROR: Could not load classifier cascade\n");
    //return;
  //}
  //if(!cascade_face.load(haarcascadeEyeFile)){
  //}
}

void HaarFaceDetector::recognizeFace(IplImage* srcImage)
{
  double scale = 3.0;
  double t = 0;
  //__android_log_write(ANDROID_LOG_DEBUG, "Face", "recognizeFace Start");
  LOGD("recognizeFace Start\n");
  
  IplImage* greyImage = cvCreateImage(cvGetSize(srcImage), IPL_DEPTH_8U, 1);
  cvCvtColor(srcImage, greyImage, CV_BGR2GRAY);
  //if (img.empty() || greyimage.empty()) {
  //  return NULL;
  //}
  
  // モナリザのマスク画像になるようにとりあえず固定
  IplImage *outImage = cvCreateImage (cvSize(386, 600), IPL_DEPTH_8U, 3);
  //Mat small_img = cvCreateImage( cvSize( cvRound (img.rows()/scale),
  //                           cvRound (img.cols()/scale)), 8, 1 );
  // Mat target ( cvRound (img.rows/scale), cvRound(img.cols/scale), CV_8UC1 );
  //Mat target = greyimage;
  
  int i=0;
  CvSeq* faces;
  t = (double)cvGetTickCount();
  
  LOGD("cascade face detect\n");
  
  CvMemStorage* storage1 = cvCreateMemStorage (0);
  faces = cvHaarDetectObjects(greyImage, cascade_face, storage1, 1.1, 2, 0
    |CV_HAAR_FIND_BIGGEST_OBJECT
    //|CV_HAAR_DO_ROUGH_SEARCH
    //|CV_HAAR_DO_CANNY_PRUNING
    //|CV_HAAR_SCALE_IMAGE);
    ,
    cvSize(20,20));
  
  t = (double)cvGetTickCount() - t;

  int facen = (faces ? faces->total : 0);
  LOGD("%d faces detected\n", facen);
  //__android_log_print(ANDROID_LOG_DEBUG, "Face", "detection time = %g ms\n", t/((double)cvGetTickFrequency()*1000.) );
  //__android_log_print(ANDROID_LOG_DEBUG, "Face", "%d faces detected", faces.size());
  for(int i = 0; i < facen; i++ )
  {
    CvRect* r = (CvRect*)cvGetSeqElem( faces, i );
    LOGD("face %d (x,y)=(%d,%d) (w,h)=(%d,%d)\n", i, r->x, r->y, r->width, r->height*2/3);
    
    // Scalar color = colors[i%8];
    Scalar color = CV_RGB(255,0,0);
    int radius;
    //center.x = cvRound((r->x + r->width*0.5)*scale);
    //center.y = cvRound((r->y + r->height*0.5)*scale);
    //radius = cvRound((r->width + r->height)*0.25*scale);
    Point center;
    center.x = cvRound((r->x + r->width*0.5));
    center.y = cvRound((r->y + r->height*0.5));
    Point pt1;
    pt1.x = cvRound(r->x);
    pt1.y = cvRound(r->y);
    Point pt2;
    pt2.x = cvRound(r->x + r->width);
    pt2.y = cvRound(r->y + r->height);
    radius = cvRound((r->width + r->height)*0.25);    
    //cvCircle(srcImage, center, radius, color, 3, 8, 0 );
    //cvRectangle(srcImage, pt1, pt2, color);
    
    bool eyeFound = false;
    bool monthFound = false;
    
    /*  Commenting eye detection part out to make it faster... */
    /* detecting eye */
    CvSeq* eyes;
    CvMemStorage* storage2 = cvCreateMemStorage (0);
    if(!cascade_eye)
      continue;
    cvSetImageROI(srcImage, cvRect(r->x, r->y, r->width, r->height));
    //smallImgROI = smallImg(*r);
    eyes = cvHaarDetectObjects(srcImage, cascade_eye, storage2,
      1.1, 2, 0
      //|CV_HAAR_FIND_BIGGEST_OBJECT
      //|CV_HAAR_DO_ROUGH_SEARCH
      //|CV_HAAR_DO_CANNY_PRUNING
      //|CV_HAAR_SCALE_IMAGE
      ,
      Size(30, 30) );
    cvResetImageROI(srcImage);
    int eyen = (eyes ? eyes->total : 0);
    if (eyen > 0) eyeFound = true;     
    LOGD("%d eyes detected\n", eyen);
    
    /* detecting mouth */
    CvSeq* mouthes;
    CvMemStorage* storage3 = cvCreateMemStorage (0);
    if(!cascade_mouth)
      continue;
    cvSetImageROI(srcImage, cvRect(r->x, r->y + (r->height * 2/3), r->width, r->height/3));
    //smallImgROI = smallImg(*r);
    mouthes = cvHaarDetectObjects(srcImage, cascade_mouth, storage3,
      1.1, 2, 0
      //|CV_HAAR_FIND_BIGGEST_OBJECT
      //|CV_HAAR_DO_ROUGH_SEARCH
      //|CV_HAAR_DO_CANNY_PRUNING
      //|CV_HAAR_SCALE_IMAGE
      ,
      Size(30, 30) );
    cvResetImageROI(srcImage);
    int mouthn = (mouthes ? mouthes->total : 0);
    if (mouthn > 0) {
      monthFound = true;
    }    
    LOGD("%d mouthes detected\n", mouthn);
    
    if (!eyeFound || !monthFound) {
      cvReleaseMemStorage(&storage2);    
      cvReleaseMemStorage(&storage3);
      continue;
    }
    
    // 解放処理
    cvReleaseMemStorage(&storage2);
    cvReleaseMemStorage(&storage3);
    
    //return outImage;
        
    /*
    for(int j = 0; j < eyen; j++ ) {
      CvRect* nr = (CvRect*)cvGetSeqElem( eyes, j );
      LOGD("eye %d (x,y)=(%d,%d) (w,h)=(%d,%d)\n", j, nr->x, nr->y, nr->width, nr->height);
      center.x = cvRound(r->x+nr->x + nr->width*0.5);
      center.y = cvRound(r->y+nr->y + nr->height*0.5);
      radius = cvRound((nr->width + nr->height)*0.25);
      //cvCircle( srcImage, center, radius, color, 3, 8, 0 );
      cvRectangle(srcImage, cvPoint(r->x+nr->x, r->y+nr->y), cvPoint(r->x+nr->x+nr->width, r->y+nr->y+nr->height), color);
    }

    for(int j = 0; j < mouthn; j++ )
    {
      CvRect* nr = (CvRect*)cvGetSeqElem( mouthes, j );
      LOGD("mouth %d (x,y)=(%d,%d) (w,h)=(%d,%d)\n", j, nr->x, nr->y, nr->width, nr->height);
      center.x = cvRound(r->x + nr->x + nr->width*0.5);
      center.y = cvRound(r->y + nr->y + (r->height * 2/3) + nr->height*0.5);
      radius = cvRound((nr->width + nr->height)*0.25);
      cvRectangle(srcImage, cvPoint(r->x+nr->x, r->y + nr->y + (r->height * 2/3)), cvPoint(r->x+nr->x+nr->width, r->y + nr->y + (r->height * 2/3) + nr->height), color);
      //cvCircle( srcImage, center, radius, color, 3, 8, 0 );
    }
    */

  }
   
  LOGD("face detection end\n");
  
  // 解放処理
  cvReleaseImage(&greyImage);
  //return outImage;
  return;
}
