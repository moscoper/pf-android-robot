/*
 * TestBar.h
 *
 *  Created on: Jul 17, 2010
 *      Author: ethan
 */

#ifndef HAAR_FACE_DETECTOR_H_
#define HAAR_FACE_DETECTOR_H_

#include <opencv2/opencv.hpp>

#include "image_pool.h"

using namespace std;
using namespace cv;

class HaarFaceDetector{
public:
	HaarFaceDetector(const char* haarcascadeFaceFile, const char* haarcascadeEyeFile, const char* haarcascadeMouthFile);
	CvRect* detectFace(IplImage* srcImage);
private:
	CvHaarClassifierCascade* cascade_face;
  CvHaarClassifierCascade* cascade_eye;
  CvHaarClassifierCascade* cascade_mouth;
};

#endif /* HAAR_FACE_DETECTOR_H_ */
