
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := VideoPlayer
LOCAL_SRC_FILES := interface.c
LOCAL_LDLIBS += -llog -lz -landroid
LOCAL_SHARED_LIBRARIES:= libavcodec libavformat libavutil libswresample libswscale
include $(BUILD_SHARED_LIBRARY)
$(call import-module, ffmpeg/android/arm)



