#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <vector>

using namespace std;
using namespace cv;

extern "C" {
JNIEXPORT jobjectArray JNICALL Java_org_opencv_samples_tutorial4_Sample4View_FindFeatures(JNIEnv* env, jobject, jlong addrRgba, jint pts)
{
	//declarations
    Mat* pMatRgb=(Mat*)addrRgba;

    jobjectArray joaMyArray;
    jobject newObject;
    jclass KeyPtsClass = (*env).FindClass("org/opencv/features2d/KeyPoint");

    vector<KeyPoint> v;

    //body
    FastFeatureDetector detector(pts);

    detector.detect(*pMatRgb, v);

    // converting to the output (KeyPoint[] in java)
    if (KeyPtsClass==NULL)
    	return NULL;

    jmethodID mInit = (*env).GetMethodID(KeyPtsClass, "<init>", "(FFF)V");
    if (mInit==NULL)
    	return NULL;

    joaMyArray = (*env).NewObjectArray(v.size(), KeyPtsClass, NULL);
    //for( size_t i = 0; i < v.size(); i++ )
      //     circle(*pMatRgb, Point(v[i].pt.x, v[i].pt.y), 10, Scalar(255,0,0,255));

    for(size_t i = 0; i < v.size(); i++)
    {
        newObject = (*env).NewObject(KeyPtsClass, mInit, v[i].pt.x, v[i].pt.y, v[i].size);
        (*env).SetObjectArrayElement(joaMyArray, i, newObject);
        (*env).DeleteLocalRef(newObject);
    }
    return joaMyArray;
}

}


/*
#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <vector>

using namespace std;
using namespace cv;

extern "C" {
JNIEXPORT void JNICALL Java_org_opencv_samples_tutorial4_Sample4View_FindFeatures(JNIEnv* env, jobject, jlong addrGray, jlong addrRgba)
{
    Mat* pMatGr=(Mat*)addrGray;
    Mat* pMatRgb=(Mat*)addrRgba;
    vector<KeyPoint> v;
    FastFeatureDetector detector(50);
    detector.detect(*pMatGr, v);
    for( size_t i = 0; i < v.size(); i++ )
        circle(*pMatRgb, Point(v[i].pt.x, v[i].pt.y), 10, Scalar(255,0,0,255));
}

}
*/
