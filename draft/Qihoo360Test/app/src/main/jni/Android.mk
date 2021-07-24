# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#MY_ROOT_PATH := $(call my-dir)

################# 编译breakpad ####################
#include $(MY_ROOT_PATH)/googlebreakpad/Android.mk

#LOCAL_PATH := $(MY_ROOT_PATH)
#include $(CLEAR_VARS)

#LOCAL_MODULE    := crash-jni
#LOCAL_SRC_FILES := HelloBreakPad.cpp

################# 链接breakpad ####################
#LOCAL_STATIC_LIBRARIES += breakpad_client

#include $(BUILD_SHARED_LIBRARY)



LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := crash-jni
LOCAL_SRC_FILES := HelloBreakPad.c \
                    checksignature.c \
                    aes.c \
                    base64.c
LOCAL_LDLIBS :=-llog

include $(BUILD_SHARED_LIBRARY)