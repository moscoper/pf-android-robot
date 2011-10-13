#include <EigenFace.h>

EigenFace::EigenFace(const char* trainingDataFile) {
  // load train data
  CvFileStorage * fileStorage;
  int i;

  // create a file-storage interface
  fileStorage = cvOpenFileStorage(trainingDataFile, 0, CV_STORAGE_READ );
  if( !fileStorage )
  {
    LOGE("Can't open facedata.xml\n");
    return;
  }

  nEigens = cvReadIntByName(fileStorage, 0, "nEigens", 0);
  nTrainFaces = cvReadIntByName(fileStorage, 0, "nTrainFaces", 0);
  trainPersonNumMat = (CvMat *)cvReadByName(fileStorage, 0, "trainPersonNumMat", 0);
  eigenValMat  = (CvMat *)cvReadByName(fileStorage, 0, "eigenValMat", 0);
  projectedTrainFaceMat = (CvMat *)cvReadByName(fileStorage, 0, "projectedTrainFaceMat", 0);
  pAvgTrainImg = (IplImage *)cvReadByName(fileStorage, 0, "avgTrainImg", 0);
  eigenVectArr = (IplImage **)cvAlloc(nTrainFaces*sizeof(IplImage *));
  for(i=0; i<nEigens; i++)
  {
    char varname[200];
    sprintf( varname, "eigenVect_%d", i );
    eigenVectArr[i] = (IplImage *)cvReadByName(fileStorage, 0, varname, 0);
  }

  // release the file-storage interface
  cvReleaseFileStorage( &fileStorage );

  return;

}

int EigenFace::recognize(IplImage* testFace)
{
  float * projectedTestFace = 0;

  IplImage* tmpImage = cvCreateImage(cvSize(FACE_WIDTH, FACE_HEIGHT), IPL_DEPTH_8U, 1);
  IplImage* resizedFaceImage = cvCloneImage(tmpImage);
  cvResize(testFace, tmpImage, CV_INTER_AREA);
  cvEqualizeHist(tmpImage, resizedFaceImage);
  
  // project the test images onto the PCA subspace
  projectedTestFace = (float *)cvAlloc( nEigens*sizeof(float) );
  int iNearest, nearest;

  // project the test image onto the PCA subspace
  cvEigenDecomposite(
    resizedFaceImage,
    nEigens,
    eigenVectArr,
    0, 0,
    pAvgTrainImg,
    projectedTestFace);

  iNearest = findNearestNeighbor(projectedTestFace);
  nearest  = trainPersonNumMat->data.i[iNearest];
  
  cvReleaseImage(&resizedFaceImage);

  return nearest;
}

//////////////////////////////////
// findNearestNeighbor()
//
int EigenFace::findNearestNeighbor(float * projectedTestFace)
{
  //double leastDistSq = 1e12;
  double leastDistSq = DBL_MAX;
  int i, iTrain, iNearest = 0;

  for(iTrain=0; iTrain<nTrainFaces; iTrain++)
  {
    double distSq=0;

    for(i=0; i<nEigens; i++)
    {
      float d_i =
        projectedTestFace[i] -
        projectedTrainFaceMat->data.fl[iTrain*nEigens + i];
      //distSq += d_i*d_i / eigenValMat->data.fl[i];  // Mahalanobis
      distSq += d_i*d_i; // Euclidean
    }

    if(distSq < leastDistSq)
    {
      leastDistSq = distSq;
      iNearest = iTrain;
    }
  }

  return iNearest;
}
