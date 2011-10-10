CFLAGS = -I/usr/local/opencv-2.3.0/include
LIBS = -L/usr/local/opencv-2.3.0/lib -lopencv_core -lopencv_highgui -lopencv_features2d -lopencv_imgproc -lopencv_objdetect
DEFS = -DSIMPLE

dump: HaarFaceDetector.o FaceFeatureDump.o
	g++ -o a.out $(LIBS) FaceFeatureDump.o HaarFaceDetector.o
match: FaceFeatureMatch.o HaarFaceDetector.o ObjectMatcher.o
	g++ -o a.out $(LIBS) FaceFeatureMatch.o HaarFaceDetector.o ObjectMatcher.o
FaceFeatureDump.o: FaceFeatureDump.cpp
	g++ $(CFLAGS) -c FaceFeatureDump.cpp -o FaceFeatureDump.o
FaceFeatureMatch.o: FaceFeatureMatch.cpp
	g++ $(CFLAGS) -c FaceFeatureMatch.cpp -o FaceFeatureMatch.o
HaarFaceDetector.o: HaarFaceDetector.cpp
	g++ $(CFLAGS) -c HaarFaceDetector.cpp -o HaarFaceDetector.o
ObjectMatcher.o: ObjectMatcher.cpp
	g++ $(CFLAGS) -c ObjectMatcher.cpp -o ObjectMatcher.o
clean:
	rm -f a.out FaceFeatureDump.o HaarFaceDetector.o FaceFeatureMatch.o ObjectMatcher.o
