NDK_MODULE_PATH := $(call my-dir)
APP_OPTIM := debug
APP_STL := stlport_static
#APP_ABI := armeabi armeabi-v7a
APP_ABI := armeabi
#APP_ABI := x86
NDK_TOOLCHAIN_VERSION := 4.9
APP_CPPFLAGS := -frtti -std=c++11
#NDK_TOOLCHAIN_VERSION := clang3.4-obfuscator