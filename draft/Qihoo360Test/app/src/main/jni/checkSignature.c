//
// Created by mac on 2017/7/8.
//

#include <string.h>
#include <jni.h>
#include<Android/log.h>
#include<stdlib.h>
#include "checksignature.h"

#define TAG "TINYSOFT" // 这个是自定义的LOG的标识
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOGD类型
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__) // 定义LOGI类型
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__) // 定义LOGW类型
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__) // 定义LOGE类型
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__) // 定义LOGF类型

/**
 * 字节流转换为十六进制字符串 http://blog.csdn.net/pingd/article/details/41945417
 * @param source
 * @param dest
 * @param sourceLen
 */
void ByteToHexStr(const unsigned char *source, char *dest, int sourceLen) {
    short i;
    unsigned char highByte, lowByte;

    for (i = 0; i < sourceLen; i++) {
        highByte = source[i] >> 4;
        lowByte = source[i] & 0x0f;
        highByte += 0x30;
        if (highByte > 0x39)
            dest[i * 2] = highByte + 0x07;
        else
            dest[i * 2] = highByte;

        lowByte += 0x30;
        if (lowByte > 0x39)
            dest[i * 2 + 1] = lowByte + 0x07;
        else
            dest[i * 2 + 1] = lowByte;
    }
    return;
}

jstring toHexString(JNIEnv *env, jbyteArray jbArr) {
    jsize barr_length = (*env)->GetArrayLength(env, jbArr);
    jbyte *barr_elements = (*env)->GetByteArrayElements(env, jbArr, JNI_FALSE);
    char *char_result = (char *) malloc(barr_length * 2 + 1);// 开始没有+1，在有的情况下会越界产生问题，还是在后面补上\0比较好
    // 将byte数组转换成16进制字符串，jbyte(signed char，强转为unsigned char)
    ByteToHexStr((unsigned char *) barr_elements, char_result, barr_length);
    *(char_result + barr_length * 2) = '\0';// 在末尾补\0
    jstring string_result = (*env)->NewStringUTF(env, char_result);
    // release
    (*env)->ReleaseByteArrayElements(env, jbArr, barr_elements, JNI_ABORT);
    // 释放指针使用free
    free(char_result);
    return string_result;
}

jbyteArray md5(JNIEnv *env, jobject obj_sign_byte_array) {
    //MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
    jmethodID tem_method;
    jclass class_MessageDigest = (*env)->FindClass(env, "java/security/MessageDigest");
    tem_method = (*env)->GetStaticMethodID(env, class_MessageDigest, "getInstance",
                                           "(Ljava/lang/String;)Ljava/security/MessageDigest;");
    jobject obj_md5 = (*env)->CallStaticObjectMethod(env, class_MessageDigest, tem_method,
                                                     (*env)->NewStringUTF(env, "md5"));
    //localMessageDigest.update(localSignature.toByteArray());
    //tem_class = (*env)->GetObjectClass(env, obj_md5);
    tem_method = (*env)->GetMethodID(env, class_MessageDigest, "update", "([B)V");
    (*env)->CallVoidMethod(env, obj_md5, tem_method, obj_sign_byte_array);
    //localMessageDigest.digest()
    tem_method = (*env)->GetMethodID(env, class_MessageDigest, "digest", "()[B");
    // 这个是md5以后的byte数组，现在只要将它转换成16进制字符串，就可以和之前的比较了
    jbyteArray byte_array_sign = (jbyteArray) (*env)->CallObjectMethod(env, obj_md5, tem_method);
    return byte_array_sign;
}

jint checkSignature(JNIEnv *env, jobject context) {
    jclass tem_class;
    jmethodID tem_method;
    jclass class_context = (*env)->GetObjectClass(env, context);

    //PackageInfo localPackageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 64);
    tem_method = (*env)->GetMethodID(env, class_context, "getPackageManager",
                                     "()Landroid/content/pm/PackageManager;");
    jobject obj_package_manager = (*env)->CallObjectMethod(env, context, tem_method);
    // getPackageName
    tem_method = (*env)->GetMethodID(env, class_context, "getPackageName", "()Ljava/lang/String;");
    jstring obj_package_name = (jstring) (*env)->CallObjectMethod(env, context, tem_method);

    // getPackageInfo
    tem_class = (*env)->GetObjectClass(env, obj_package_manager);
    tem_method = (*env)->GetMethodID(env, tem_class, "getPackageInfo",
                                     "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    jobject obj_package_info = (*env)->CallObjectMethod(env, obj_package_manager, tem_method,
                                                        obj_package_name, 64);

    // Signature[] arrayOfSignature = localPackageInfo.signatures;
    // Signature localSignature = arrayOfSignature[0];
    tem_class = (*env)->GetObjectClass(env, obj_package_info);
    jfieldID fieldID_signatures = (*env)->GetFieldID(env, tem_class, "signatures",
                                                     "[Landroid/content/pm/Signature;");
    jobjectArray signatures = (jobjectArray) (*env)->GetObjectField(env, obj_package_info,
                                                                    fieldID_signatures);
    jobject signature = (*env)->GetObjectArrayElement(env, signatures, 0);
    // localSignature.toByteArray()
    tem_class = (*env)->GetObjectClass(env, signature);
    tem_method = (*env)->GetMethodID(env, tem_class, "toByteArray", "()[B");
    jobject signArr = (*env)->CallObjectMethod(env, signature, tem_method);// 这个就是拿到的签名byte数组

    //证书签名md5
    jbyteArray signMd5Arr = md5(env, signArr);
    jstring signMd5Str = toHexString(env, signMd5Arr);
    const char *md5 = (*env)->GetStringUTFChars(env, signMd5Str, JNI_FALSE);
    const char *pkg_name = (*env)->GetStringUTFChars(env, obj_package_name, 0);
    LOGE("package name %s", pkg_name);
    LOGE("package md5 %s", md5);

    int result1 = strcmp(app_pkgName, pkg_name);
    if (result1 != 0) {
        return -1;
    }

    int result2 = strcmp(app_md5, md5);
    if (result2 != 0) {
        return -2;
    }

    return 1;
}