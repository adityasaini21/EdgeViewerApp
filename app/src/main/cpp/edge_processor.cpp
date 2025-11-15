#include <jni.h>
#include <opencv2/imgproc.hpp>
#include <opencv2/core.hpp>
#include <android/log.h>

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,"EdgeProc",__VA_ARGS__)
using namespace cv;

extern "C" JNIEXPORT jboolean JNICALL
Java_com_adityasaini_edgeviewer_NativeLib_processEdge(JNIEnv *env, jobject /* this */,
                                                   jbyteArray yuvArray,
                                                   jint width, jint height,
                                                   jbyteArray outArray) {
    // Convert YUV (NV21) to Mat, then to Gray, then Canny, then copy to outArray
    jbyte *yuv = env->GetByteArrayElements(yuvArray, NULL);
    jbyte *out = env->GetByteArrayElements(outArray, NULL);

    Mat yuvMat(height + height/2, width, CV_8UC1, (unsigned char *)yuv);
    Mat rgb;
    cvtColor(yuvMat, rgb, COLOR_YUV2RGB_NV21);
    Mat gray, edges;
    cvtColor(rgb, gray, COLOR_RGB2GRAY);
    Canny(gray, edges, 50, 150);

    // Convert edges (single channel) to RGBA byte array (so Android can show easily)
    Mat rgba;
    cvtColor(edges, rgba, COLOR_GRAY2RGBA);

    int size = width * height * 4;
    memcpy(out, rgba.data, size);

    env->ReleaseByteArrayElements(yuvArray, yuv, JNI_ABORT);
    env->ReleaseByteArrayElements(outArray, out, 0);

    return JNI_TRUE;
}
