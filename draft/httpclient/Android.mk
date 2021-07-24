LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := httpclient
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_HOST_OS := linux
LOCAL_MULTILIB := first
LOCAL_CXX_STL := libc++_static

PROJECT_FILES := \
    http/HttpClient.cpp \
    http/HttpCookie.cpp \
    http/Thread.cpp \
    http/ThreadPool.cpp

LOCAL_SRC_FILES := \
		main.cpp \
		$(PROJECT_FILES)

LOCAL_C_INCLUDES = \
        $(LOCAL_PATH)/http

LOCAL_SHARED_LIBRARIES := \
        libcutils \
        libcurl \
        libssl \
        libcrypto \

LOCAL_STATIC_LIBRARIES := \
        libjsoncpp \
        libutils \
        libz \
        libext2_uuid

LOCAL_CFLAGS := -fno-exceptions -O2 -DANDROID_PIE=ON -DHAVE_SYS_UIO_H -fno-rtti -Wno-write-strings
LOCAL_CFLAGS += -Wno-error=format
LOCAL_CFLAGS += -Wno-error=unused-parameter
LOCAL_CFLAGS += -Wno-error=non-virtual-dtor
LOCAL_CFLAGS += -Wno-error=char-subscripts
LOCAL_CFLAGS += -Wno-error=unused-variable

LOCAL_CPPFLAGS += -fexceptions
LOCAL_CFLAGS_linux := -Wno-exit-time-destructors -Wthread-safety
LOCAL_LDLIBS_linux := -lrt -lz -lm

include $(BUILD_HOST_EXECUTABLE)