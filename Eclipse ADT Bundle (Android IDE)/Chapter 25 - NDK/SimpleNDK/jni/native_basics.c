#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <android/log.h>

#define DEBUG_TAG "NDK_NativeBasicsActivity"


void Java_com_advancedandroidbook_simplendk_NativeBasicsActivity_basicNativeCall
   (JNIEnv *env, jobject this)
{
	// do something interesting here
	
	__android_log_print(ANDROID_LOG_VERBOSE, DEBUG_TAG, "Basic call");
}

jstring Java_com_advancedandroidbook_simplendk_NativeBasicsActivity_formattedAddition
    (JNIEnv *env, jobject this, jint number1, jint number2, jstring formatString)
{
	// get a C string from a Java string object
	jboolean fCopy;
	const char * szFormat = (*env)->GetStringUTFChars(env, formatString, &fCopy);
    
	char * szResult;
	
	// add the two values
	jlong nSum = number1+number2;
	
	// make sure there's ample room for nSum
	szResult = malloc(sizeof(szFormat)+30);
	
	// make the call
	sprintf(szResult, szFormat, nSum);
	
	// get a Java string object
	jstring result = (*env)->NewStringUTF(env, szResult);
	
	// free the C strings
	free(szResult);
	(*env)->ReleaseStringUTFChars(env, formatString, szFormat);
	
	// return the Java string object
	return(result);
}

void Java_com_advancedandroidbook_simplendk_NativeBasicsActivity_throwsException
(JNIEnv * env, jobject this, jint number)
{
    if (number < 42 || number > 42) {
		// throw an exception
		jclass illegalArgumentException = 
            (*env)->FindClass(env, "java/lang/IllegalArgumentException");
		if (illegalArgumentException == NULL) {
			return;
		}
		(*env)->ThrowNew(env, illegalArgumentException, "What an exceptional number.");        
    } else {
        __android_log_print(ANDROID_LOG_VERBOSE, DEBUG_TAG, "Nothing exceptional here");
    }
}


void Java_com_advancedandroidbook_simplendk_NativeBasicsActivity_checksException
    (JNIEnv * env, jobject this, jint number)
{   
    jthrowable exception;
    jclass class = (*env)->GetObjectClass(env, this);    
    
    jmethodID fnJavaThrowsException =
        (*env)->GetMethodID(env, class, "javaThrowsException", "(I)V");
    
    if (fnJavaThrowsException != NULL) {
        (*env)->CallVoidMethod(env, this, fnJavaThrowsException, number);
        exception =	(*env)->ExceptionOccurred(env);
        if (exception) {
            (*env)->ExceptionDescribe(env);
            (*env)->ExceptionClear(env);
            __android_log_print(ANDROID_LOG_ERROR,
                DEBUG_TAG, "Exception occurred. Check LogCat.");
        }
    } else {
        __android_log_print(ANDROID_LOG_ERROR, DEBUG_TAG, "No method found");
    }
}

