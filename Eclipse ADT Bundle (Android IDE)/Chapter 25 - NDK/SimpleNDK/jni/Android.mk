LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_LDLIBS := -llog -lGLESv2

LOCAL_MODULE    := simplendk
LOCAL_SRC_FILES := native_basics.c native_opengl2.c

include $(BUILD_SHARED_LIBRARY)