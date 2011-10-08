#CFLAGS = `pkg-config --cflags opencv` -O
CFLAGS = 
LIBS = `pkg-config --libs opencv`
DEFS = -DSIMPLE

feature: HaarFaceDetector.o FaceFeatureDump.o
	g++ -o a.out $(LIBS) FaceFeatureDump.o HaarFaceDetector.o
FaceFeatureDump.o: FaceFeatureDump.cpp
	g++ -c FaceFeatureDump.cpp -o FaceFeatureDump.o
HaarFaceDetector.o: HaarFaceDetector.cpp
	g++ -c HaarFaceDetector.cpp -o HaarFaceDetector.o
clean:
	rm -f a.out FaceFeatureDump.o HaarFaceDetector.o
