//
// Created by mashuai-s on 2017/6/2.
//

#include <jni.h>
#include <string.h>
#include "aes.h"
#include "com_qihoo360_test_JniSupport.h"
#include "checkSignature.h"

/* added to support googlebreakpad */
//#include "googlebreakpad/src/client/linux/handler/exception_handler.h"
//#include "googlebreakpad/src/client/linux/handler/minidump_descriptor.h"

const char *UNSIGNATURE = "UNSIGNATURE";

JNIEXPORT jint JNICALL Java_com_qihoo360_test_JniSupport_getCrashId(JNIEnv *env, jclass clazz) {
    //google_breakpad::MinidumpDescriptor descriptor("/mnt/sdcard/hellobreakpad");
    //google_breakpad::ExceptionHandler eh(descriptor, NULL, NULL, NULL, true, -1);

    int i = 10;
    int y = i / 0;
//    int y = 888;
    return y;
}

char *new_str(char *charBuffer) {
    char *str;
    if (strlen(charBuffer) == 0)
        str = charBuffer;
    else
        str = charBuffer + 1;
    return str;
}

char *getKey() {
    char *s = "NMTIzNDU2Nzg5MGFiY2RlZg==";
    const char *str_copy[strlen(s)];
    memcpy(str_copy, s, strlen(s));

    char *encode_str = new_str((char *) str_copy);
    return (char *) b64_decode(encode_str, strlen(encode_str));
}

JNIEXPORT jstring JNICALL
Java_com_qihoo360_test_JniSupport_encrypt(JNIEnv *env, jclass type, jobject context, jstring origin_) {
    if(checkSignature(env, context) != 1) {
        char *result = UNSIGNATURE;
        return (*env) ->NewStringUTF(env, result);
    }

    uint8_t *AES_KEY = (uint8_t *) getKey();
    const char *in = (*env)->GetStringUTFChars(env, origin_, JNI_FALSE);
    char *baseResult = AES_128_ECB_PKCS5Padding_Encrypt(in, AES_KEY);
    (*env)->ReleaseStringUTFChars(env, origin_, in);
    return (*env)->NewStringUTF(env, baseResult);
}

JNIEXPORT jstring JNICALL
Java_com_qihoo360_test_JniSupport_decrypt(JNIEnv *env, jclass type, jobject context, jstring cipher_) {
    if(checkSignature(env, context) != 1) {
        char *result = UNSIGNATURE;
        return (*env) ->NewStringUTF(env, result);
    }

    uint8_t *AES_KEY = (uint8_t *) getKey();
    const char *str = (*env)->GetStringUTFChars(env, cipher_, JNI_FALSE);
    char *desResult = AES_128_ECB_PKCS5Padding_Decrypt(str, AES_KEY);
    (*env)->ReleaseStringUTFChars(env, cipher_, str);
    return (*env)->NewStringUTF(env, desResult);
}

JNIEXPORT jstring JNICALL
Java_com_qihoo360_test_JniSupport_getBase64(JNIEnv *env, jclass type) {
    const char *s = "1234567890abcdef1234567890abcdef1234567890abcdef";
    char *r = b64_encode(s, strlen(s));
    return (*env)->NewStringUTF(env, r);
}