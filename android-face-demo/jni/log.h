#ifndef MY_LOG_H_
#define MY_LOG_H_

#include <android/log.h>
#define LOG(...) 
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "PFFaceDetector", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "PFFaceDetector", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "PFFaceDetector", __VA_ARGS__)

#endif /* MY_LOG_H_ */