LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_USE_AAPT2 := true

LOCAL_PACKAGE_NAME := CobraSystemUI

LOCAL_JAVA_LIBRARIES := SystemUIPluginLib

LOCAL_STATIC_ANDROID_LIBRARIES := \
    androidx.recyclerview_recyclerview

LOCAL_PRIVATE_PLATFORM_APIS := true
LOCAL_CERTIFICATE := platform
LOCAL_PROGUARD_ENABLED := disabled

LOCAL_SRC_FILES := $(call all-java-files-under, src)

include $(BUILD_PACKAGE)
