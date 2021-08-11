#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "st_mobile_effect.h"
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include "stmobile_sound_play_jni.h"
#include "utils.h"
#include "jvmutil.h"
#include<fcntl.h>
#include <st_mobile_beautify.h>

#define  LOG_TAG    "STMobileEffectNative"

extern "C" {
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_createInstanceNative(JNIEnv * env, jobject obj, jint config);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_destroyInstanceNative(JNIEnv * env, jobject obj);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_setParam(JNIEnv * env, jobject obj, jint param, jfloat value);
    JNIEXPORT jfloat JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_getParam(JNIEnv * env, jobject obj, jint param);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_addPackage(JNIEnv * env, jobject obj, jstring path);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_addPackageFromAssetsFile(JNIEnv * env, jobject obj, jstring file_path, jobject assetManager);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_changePackage(JNIEnv * env, jobject obj, jstring path);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_changePackageFromAssetsFile(JNIEnv * env, jobject obj, jstring file_path, jobject assetManager);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_removeEffect(JNIEnv * env, jobject obj, jint id);
    JNIEXPORT void JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_clear(JNIEnv * env, jobject obj);
    JNIEXPORT jobject JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_getPackageInfo(JNIEnv * env, jobject obj, jint packageId);
    JNIEXPORT jobjectArray JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_getModulesInPackage(JNIEnv * env, jobject obj, jint packageId, jint moduleCount);
    JNIEXPORT jobject JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_getModuleInfo(JNIEnv * env, jobject obj, jint moduleId);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_getOverlappedBeautyCount(JNIEnv *env, jobject obj);
    JNIEXPORT jobjectArray JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_getOverlappedBeauty(JNIEnv * env, jobject obj, jint beautyCount);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_setBeautyStrength(JNIEnv * env, jobject obj, jint type, jfloat value);
    JNIEXPORT jfloat JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_getBeautyStrength(JNIEnv *env, jobject obj, jint type);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_setBeauty(JNIEnv * env, jobject obj, jint param, jstring path);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_setBeautyMode(JNIEnv *env, jobject obj,jint param, jint value);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_getBeautyMode(JNIEnv *env, jobject obj,jint param);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_setBeautyFromAssetsFile(JNIEnv * env, jobject obj, jint param, jstring file_path, jobject assetManager);
    JNIEXPORT jlong JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_getHumanActionDetectConfig(JNIEnv *env, jobject obj);
    JNIEXPORT jlong JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_getAnimalDetectConfig(JNIEnv *env, jobject obj);
    JNIEXPORT jlong JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_getCustomParamConfig(JNIEnv *env, jobject obj);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_render(JNIEnv * env, jobject obj, jobject inputParams, jobject outputParams, jboolean needOutputHumanAction);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_replayPackage(JNIEnv * env, jobject obj, jint packageId);

};

static inline jfieldID getEffectHandleField(JNIEnv *env, jobject obj)
{
    jclass c = env->GetObjectClass(obj);
    // J is the type signature for long:
    return env->GetFieldID(c, "nativeEffectHandle", "J");
}

void setEffectHandle(JNIEnv *env, jobject obj, void *h)
{
    jlong handle = reinterpret_cast<jlong>(h);
    env->SetLongField(obj, getEffectHandleField(env, obj), handle);
}

void *getEffectHandle(JNIEnv *env, jobject obj)
{
    jlong handle = env->GetLongField(obj, getEffectHandleField(env, obj));
    return reinterpret_cast<void *>(handle);
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_createInstanceNative(JNIEnv * env, jobject obj, jint config)
{
    gStickerObject = env->NewGlobalRef(obj);

    st_handle_t handle;
    int result = st_mobile_effect_create_handle(config, &handle);
    if(result != 0){
        if(gStickerObject != NULL) {
            env->DeleteGlobalRef(gStickerObject);
            gStickerObject = NULL;
        }

        LOGE("create handle failed");
        return result;
    }

    int ret = st_mobile_effect_set_module_state_change_callback(handle, sound_state_changed);

    setEffectHandle(env, obj, handle);
    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_destroyInstanceNative(JNIEnv * env, jobject obj)
{
    st_handle_t handle = getEffectHandle(env, obj);
    if(handle == NULL){
        return ST_E_HANDLE;
    }
    setEffectHandle(env, obj, NULL);
    st_mobile_effect_destroy_handle(handle);
    return ST_OK;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_setParam(JNIEnv * env, jobject obj, jint param, jfloat value)
{
    int result = ST_OK;
    st_handle_t handle = getEffectHandle(env, obj);
    if(handle != NULL){
        result = st_mobile_effect_set_param(handle, (st_effect_param_t)param, value);
    }
    return result;
}

JNIEXPORT jfloat JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_getParam(JNIEnv * env, jobject obj, jint param)
{
    float value = 0.0f;
    st_handle_t handle = getEffectHandle(env, obj);
    if(handle != NULL){
        st_mobile_effect_set_param(handle, (st_effect_param_t)param, value);
    }

    return value;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_setBeautyMode(JNIEnv *env, jobject obj,
                                                               jint param, jint value) {
    //LOGE("setBeautyMode: %d param", param);
    //LOGE("setBeautyMode: %d value", value);
    int result = ST_OK;
    st_handle_t handle = getEffectHandle(env, obj);
    if(handle != NULL){
        result = st_mobile_effect_set_beauty_mode(handle, (st_effect_beauty_type_t)param, value);
        LOGI("Java_com_sensetime_stmobile_STMobileEffectNative_setBeautyMode:%d", result);
    }
    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_getBeautyMode(JNIEnv *env, jobject obj,
                                                               jint param) {
    //int result = ST_OK;
    st_handle_t handle = getEffectHandle(env, obj);
    jint mode = -1;
    if(handle != NULL){
        st_mobile_effect_get_beauty_mode(handle, (st_effect_beauty_type_t)param, &mode);
    }
    return mode;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_addPackage(JNIEnv * env, jobject obj, jstring path)
{
//    LOGE("Java_com_sensetime_stmobile_STMobileEffectNative_addPackage");
    int result = ST_OK;
    st_handle_t handle = getEffectHandle(env, obj);

    if (handle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    const char *pathChars = NULL;
    if (path != NULL) {
        pathChars = env->GetStringUTFChars(path, 0);
    }

    int packageId = 0;
    if(handle != NULL) {
        result = st_mobile_effect_add_package(handle,pathChars, &packageId);
        //LOGE("add_package result: %d", result);
        //LOGE("add_package path: %s", pathChars);
    }
    if (pathChars != NULL) {
        env->ReleaseStringUTFChars(path, pathChars);
    }

    if(result == ST_OK){
        return packageId;
    } else{
        return result;
    }
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_addPackageFromAssetsFile(JNIEnv * env, jobject obj, jstring file_path, jobject assetManager)
{
    int result = ST_OK;
    st_handle_t handle = getEffectHandle(env, obj);

    if (handle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    int packageId = 0;

    if(NULL == assetManager){
        LOGE("assetManager is null");
        return ST_E_INVALIDARG;
    }

    if(file_path == NULL){
        LOGE("add package null");
        return ST_E_INVALIDARG;
    }

    const char* file_name_str = env->GetStringUTFChars(file_path, 0);
    if(NULL == file_name_str) {
        LOGE("file_name to c_str failed, add effect to null");
        return ST_E_INVALIDARG;
    }

    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
    if(NULL == mgr) {
        LOGE("native assetManager is null");
        return ST_E_INVALIDARG;
    }

    AAsset* asset = AAssetManager_open(mgr, file_name_str, AASSET_MODE_UNKNOWN);
    env->ReleaseStringUTFChars(file_path, file_name_str);
    if (NULL == asset) {
        LOGE("open asset file failed");
        return ST_E_FILE_NOT_FOUND;
    }

    unsigned char* buffer = NULL;
    long size = 0;
    size = AAsset_getLength(asset);
    buffer = new unsigned char[size];
    memset(buffer, '\0', size);

    long readSize = AAsset_read(asset, buffer, size);
    if (readSize != size) {
        AAsset_close(asset);
        if(buffer){
            delete[] buffer;
            buffer = NULL;
        }

        return ST_E_INVALID_FILE_FORMAT;
    }

    AAsset_close(asset);

    if (size < 100) {
        LOGE("file is too short");
        if (buffer) {
            delete[] buffer;
            buffer = NULL;
        }
        return ST_E_INVALID_FILE_FORMAT;
    }

    st_effect_buffer_t *effect_buffer = new st_effect_buffer_t;
    memset(effect_buffer, 0, sizeof(st_effect_buffer_t));
    effect_buffer->data_ptr = (char*)buffer;
    effect_buffer->data_len = size;

    if(handle != NULL) {
        result = st_mobile_effect_add_package_from_buffer(handle, effect_buffer, &packageId);
    }

    delete(effect_buffer);

    if(buffer){
        delete[] buffer;
        buffer = NULL;
    }

    if(result == ST_OK){
        return packageId;
    } else{
        LOGE("add_package_from_buffer failed, %d",result);
        return result;
    }
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_changePackage(JNIEnv * env, jobject obj, jstring path)
{
    int result = ST_OK;
    st_handle_t handle = getEffectHandle(env, obj);

    if (handle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    const char *pathChars = NULL;
    if (path != NULL) {
        pathChars = env->GetStringUTFChars(path, 0);
    }

    int packageId = 0;
    result = st_mobile_effect_change_package(handle,pathChars, &packageId);

    if (pathChars != NULL) {
        env->ReleaseStringUTFChars(path, pathChars);
    }

    if(result == ST_OK){
        return packageId;
    } else{
        return result;
    }
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_changePackageFromAssetsFile(JNIEnv * env, jobject obj, jstring file_path, jobject assetManager)
{
    int result = ST_OK;
    st_handle_t handle = getEffectHandle(env, obj);

    if (handle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    int packageId = 0;

    if(NULL == assetManager){
        LOGE("assetManager is null");
        return ST_E_INVALIDARG;
    }

    if(file_path == NULL){
        LOGE("change package null");
        return ST_E_INVALIDARG;
    }

    const char* file_name_str = env->GetStringUTFChars(file_path, 0);
    if(NULL == file_name_str) {
        LOGE("file_name to c_str failed, add effect to null");
        return ST_E_INVALIDARG;
    }

    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
    if(NULL == mgr) {
        LOGE("native assetManager is null");
        return ST_E_INVALIDARG;
    }

    AAsset* asset = AAssetManager_open(mgr, file_name_str, AASSET_MODE_UNKNOWN);
    env->ReleaseStringUTFChars(file_path, file_name_str);
    if (NULL == asset) {
        LOGE("open asset file failed");
        return ST_E_FILE_NOT_FOUND;
    }

    unsigned char* buffer = NULL;
    long size = 0;
    size = AAsset_getLength(asset);
    buffer = new unsigned char[size];
    memset(buffer, '\0', size);

    long readSize = AAsset_read(asset, buffer, size);
    if (readSize != size) {
        AAsset_close(asset);
        if(buffer){
            delete[] buffer;
            buffer = NULL;
        }

        return ST_E_INVALID_FILE_FORMAT;
    }

    AAsset_close(asset);

    if (size < 100) {
        LOGE("file is too short");
        if (buffer) {
            delete[] buffer;
            buffer = NULL;
        }
        return ST_E_INVALID_FILE_FORMAT;
    }

    st_effect_buffer_t *effect_buffer = new st_effect_buffer_t;
    memset(effect_buffer, 0, sizeof(st_effect_buffer_t));
    effect_buffer->data_ptr = (char*)buffer;
    effect_buffer->data_len = size;

    if(handle != NULL) {
        result = st_mobile_effect_change_package_from_buffer(handle, effect_buffer, &packageId);
    }

    delete(effect_buffer);

    if(buffer){
        delete[] buffer;
        buffer = NULL;
    }

    if(result == ST_OK){
        return packageId;
    } else{
        LOGE("add_package_from_buffer failed, %d",result);
        return result;
    }
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_removeEffect(JNIEnv * env, jobject obj, jint id){
    LOGI("enter removeEffect");
    int result = ST_OK;
    st_handle_t handle = getEffectHandle(env, obj);

    if (handle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    if(handle != NULL) {
        result = st_mobile_effect_remove_package(handle, id);
    }

    return result;
}

JNIEXPORT void JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_clear(JNIEnv * env, jobject obj){
    st_handle_t handle = getEffectHandle(env, obj);

    if (handle == NULL) {
        LOGE("handle is null");
    }

    if(handle != NULL) {
        int ret = st_mobile_effect_clear_packages(handle);
    }
}

JNIEXPORT jobject JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_getPackageInfo(JNIEnv * env, jobject obj, jint packageId)
{
    st_handle_t handle = getEffectHandle(env, obj);

    if(handle == NULL){
        return NULL;
    }

    st_effect_package_info_t *package_info = new st_effect_package_info_t;
    memset(package_info, 0, sizeof(st_effect_package_info_t));

    int result = ST_OK;
    result = st_mobile_effect_get_package_info(handle, packageId, package_info);

    if(result == ST_OK){
        jobject packageInfoObj = convert2EffectPackageInfo(env, package_info);
        return packageInfoObj;
    }

    return NULL;
}

JNIEXPORT jobjectArray JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_getModulesInPackage(JNIEnv * env, jobject obj, jint packageId, jint moduleCount)
{
    int result = ST_OK;
    st_handle_t handle = getEffectHandle(env, obj);

    if (handle == NULL) {
        LOGE("handle is null");
        return NULL;
    }

    st_effect_module_info_t* pModuleInfos = NULL;
    if (moduleCount > 0) {
        pModuleInfos = (st_effect_module_info_t *)malloc(moduleCount * sizeof(st_effect_module_info_t));
    }
    result = st_mobile_effect_get_modules_in_package(handle, packageId, pModuleInfos, moduleCount);
    jclass module_info_cls = env->FindClass("com/sensetime/stmobile/model/STEffectModuleInfo");
    jobjectArray moduleInfos = (env)->NewObjectArray(moduleCount, module_info_cls, 0);

    if (result == ST_OK) {
        for (int i = 0; i < moduleCount; ++i) {
            jobject moduleInfoObj = convert2EffectModuleInfo(env, &pModuleInfos[i]);
            if (moduleInfoObj != NULL) {
                env->SetObjectArrayElement(moduleInfos, i, moduleInfoObj);
            }
            env->DeleteLocalRef(moduleInfoObj);
        }
    }

    env->DeleteLocalRef(module_info_cls);
    safe_delete_array(pModuleInfos);

    return moduleInfos;
}

JNIEXPORT jobject JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_getModuleInfo(JNIEnv * env, jobject obj, jint moduleId)
{
    st_handle_t handle = getEffectHandle(env, obj);

    if(handle == NULL){
        return NULL;
    }

    st_effect_module_info_t *module_info = new st_effect_module_info_t;
    memset(module_info, 0, sizeof(st_effect_module_info_t));

    int result = ST_OK;
    result = st_mobile_effect_get_module_info(handle, moduleId, module_info);

    if(result == ST_OK){
        jobject moduleInfoObj = convert2EffectModuleInfo(env, module_info);
        return moduleInfoObj;
    }

    return NULL;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_getOverlappedBeautyCount(JNIEnv *env, jobject obj) {
    int result = ST_OK;
    st_handle_t handle = getEffectHandle(env, obj);

    if(handle == NULL){
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    int count = 0;
    result =  st_mobile_effect_get_overlapped_beauty_count(handle, &count);

    if(result == ST_OK){
        return count;
    }

    return result;
}

JNIEXPORT jobjectArray JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_getOverlappedBeauty(JNIEnv * env, jobject obj, jint beautyCount)
{
    int result = ST_OK;
    st_handle_t handle = getEffectHandle(env, obj);

    if (handle == NULL) {
        LOGE("handle is null");
        return NULL;
    }

    st_effect_beauty_info_t* pBeautyInfos = NULL;
    if (beautyCount > 0) {
        pBeautyInfos = (st_effect_beauty_info_t *)malloc(beautyCount * sizeof(st_effect_beauty_info_t));
    }
    result = st_mobile_effect_get_overlapped_beauty(handle, pBeautyInfos, beautyCount);
    jclass beauty_info_cls = env->FindClass("com/sensetime/stmobile/model/STEffectBeautyInfo");
    jobjectArray beautyInfos = (env)->NewObjectArray(beautyCount, beauty_info_cls, 0);

    if (result == ST_OK) {
        for (int i = 0; i < beautyCount; ++i) {
            jobject beautyInfoObj = convert2EffectBeautyInfo(env, &pBeautyInfos[i]);
            if (beautyInfoObj != NULL) {
                env->SetObjectArrayElement(beautyInfos, i, beautyInfoObj);
            }
            env->DeleteLocalRef(beautyInfoObj);
        }
    }

    env->DeleteLocalRef(beauty_info_cls);
    safe_delete_array(pBeautyInfos);

    return beautyInfos;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_setBeautyStrength(JNIEnv * env, jobject obj, jint type, jfloat value)
{
    //LOGE("setBeautyStrength type: %d", type);
    //LOGE("setBeautyStrength value: %f", value);
    st_handle_t handle = getEffectHandle(env, obj);
    if(handle == NULL){
        return ST_E_HANDLE;
    }
    int result = st_mobile_effect_set_beauty_strength(handle,(st_effect_beauty_type_t)type, value);

    return result;
}

JNIEXPORT jfloat JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_getBeautyStrength(JNIEnv *env, jobject obj, jint type) {
    int result = ST_OK;
    st_handle_t handle = getEffectHandle(env, obj);

    if(handle == NULL){
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    float value = 0.0f;
    result =  st_mobile_effect_get_beauty_strength(handle, (st_effect_beauty_type_t)type, &value);

    if(result == ST_OK){
        return value;
    }

    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_setBeauty(JNIEnv * env, jobject obj, jint param, jstring path)
{
    int result = ST_OK;
    st_handle_t handle = getEffectHandle(env, obj);

    if (handle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    const char *pathChars = NULL;
    if (path != NULL) {
        pathChars = env->GetStringUTFChars(path, 0);
    }
    LOGI("setBeauty param %d", param);
    LOGI("setBeauty path %s", pathChars);

    int packageId = 0;
    result = st_mobile_effect_set_beauty(handle, (st_effect_beauty_type_t)param, pathChars);

    if (pathChars != NULL) {
        env->ReleaseStringUTFChars(path, pathChars);
    }

    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_setBeautyFromAssetsFile(JNIEnv * env, jobject obj, jint param, jstring file_path, jobject assetManager)
{
    int result = ST_OK;
    st_handle_t handle = getEffectHandle(env, obj);

    if (handle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    int packageId = 0;

    if(NULL == assetManager){
        LOGE("assetManager is null");
        return ST_E_INVALIDARG;
    }

    if(file_path == NULL){
        LOGE("set beauty null");
        return ST_E_INVALIDARG;
    }

    const char* file_name_str = env->GetStringUTFChars(file_path, 0);
    if(NULL == file_name_str) {
        LOGE("file_name to c_str failed, add effect to null");
        return ST_E_INVALIDARG;
    }

    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
    if(NULL == mgr) {
        LOGE("native assetManager is null");
        return ST_E_INVALIDARG;
    }

    AAsset* asset = AAssetManager_open(mgr, file_name_str, AASSET_MODE_UNKNOWN);
    env->ReleaseStringUTFChars(file_path, file_name_str);
    if (NULL == asset) {
        LOGE("open asset file failed");
        return ST_E_FILE_NOT_FOUND;
    }

    unsigned char* buffer = NULL;
    long size = 0;
    size = AAsset_getLength(asset);
    buffer = new unsigned char[size];
    memset(buffer, '\0', size);

    long readSize = AAsset_read(asset, buffer, size);
    if (readSize != size) {
        AAsset_close(asset);
        if(buffer){
            delete[] buffer;
            buffer = NULL;
        }

        return ST_E_INVALID_FILE_FORMAT;
    }

    AAsset_close(asset);

    if (size < 100) {
        LOGE("file is too short");
        if (buffer) {
            delete[] buffer;
            buffer = NULL;
        }
        return ST_E_INVALID_FILE_FORMAT;
    }

    st_effect_buffer_t *effect_buffer = new st_effect_buffer_t;
    memset(effect_buffer, 0, sizeof(st_effect_buffer_t));
    effect_buffer->data_ptr = (char*)buffer;
    effect_buffer->data_len = size;

    if(handle != NULL) {
        result = st_mobile_effect_set_beauty_from_buffer(handle, (st_effect_beauty_type_t)param, effect_buffer);
    }

    delete(effect_buffer);

    if(buffer){
        delete[] buffer;
        buffer = NULL;
    }

    return result;
}

JNIEXPORT jlong JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_getHumanActionDetectConfig(JNIEnv *env, jobject obj) {
    int result = ST_OK;
    st_handle_t handle = getEffectHandle(env, obj);

    if(handle == NULL){
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    uint64_t value = 0;
    result =  st_mobile_effect_get_detect_config(handle, (uint64_t*)&value);

    if(result == ST_OK){
        return value;
    }

    return result;
}

JNIEXPORT jlong JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_getAnimalDetectConfig(JNIEnv *env, jobject obj) {
    int result = ST_OK;
    st_handle_t handle = getEffectHandle(env, obj);

    if(handle == NULL){
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    uint64_t value = 0;
    result =  st_mobile_effect_get_animal_detect_config(handle, (uint64_t*)&value);

    if(result == ST_OK){
        return value;
    }

    return result;
}

JNIEXPORT jlong JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_getCustomParamConfig(JNIEnv *env, jobject obj) {
    int result = ST_OK;
    st_handle_t handle = getEffectHandle(env, obj);

    if(handle == NULL){
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    uint64_t value = 0;
    result =  st_mobile_effect_get_custom_param_config(handle, (uint64_t*)&value);

    if(result == ST_OK){
        return value;
    }

    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_render(JNIEnv * env, jobject obj, jobject inputParams, jobject outputParams, jboolean needOutputHumanAction)
{
    st_handle_t handle = getEffectHandle(env, obj);

    if(handle == NULL){
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    st_effect_render_in_param_t *in_param = new st_effect_render_in_param_t;
    if (!convert2st_effect_render_in_param(env, inputParams, in_param)) {
        memset(in_param, 0, sizeof(st_effect_render_in_param_t));
    }

    st_effect_render_out_param_t *out_param = new st_effect_render_out_param_t;
    if (!convert2st_effect_render_out_param(env, outputParams, out_param)) {
        memset(out_param, 0, sizeof(st_effect_render_out_param_t));
    }

    if(needOutputHumanAction && in_param->p_human != nullptr && out_param->p_human == nullptr){
        out_param->p_human = new st_mobile_human_action_t;
        memset(out_param->p_human, 0, sizeof(st_mobile_human_action_t));
        st_mobile_human_action_copy(in_param->p_human, out_param->p_human);
    }

    int result = st_mobile_effect_render(handle, in_param, out_param);

    if(result == ST_OK){
        outputParams = convert2STEffectRenderOutParam(env, out_param);
    }

    releaseEffectRenderInputParams(in_param);
    releaseEffectRenderOutputParams(out_param);

    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileEffectNative_replayPackage(JNIEnv * env, jobject obj, jint packageId){
    int result = ST_OK;
    st_handle_t handle = getEffectHandle(env, obj);

    if (handle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    if(handle != NULL) {
        result = st_mobile_effect_replay_package(handle, packageId);
    }

    return result;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_sensetime_stmobile_STMobileEffectNative_setSoundPlayDone(JNIEnv *env, jobject obj,
                                                                  jstring name) {
    st_handle_t handle = getEffectHandle(env, obj);
    if(handle == NULL) {
        LOGE("effectHandle is null");
        return ST_E_HANDLE;
    }
    if (name != NULL) {
        const char *nameCstr = NULL;
        nameCstr = env->GetStringUTFChars(name, 0);
        if (nameCstr != NULL) {
            st_effect_module_info_t moduleInfo;
            memset(&moduleInfo, 0, sizeof(moduleInfo));
            moduleInfo.type = EFFECT_MODULE_SOUND;
            strcpy(moduleInfo.name, nameCstr);
            moduleInfo.state = EFFECT_MODULE_PAUSED_LAST_FRAME;

            int ret = st_mobile_effect_set_module_info(handle, &moduleInfo);
            LOGI("st_mobile_effect_set_module_info ret: %d", ret);
            env->ReleaseStringUTFChars(name, nameCstr);
        } else {
            LOGE("Sound name is NULL");
            return ST_E_INVALIDARG;
        }

        LOGI("Set play done success");
        return ST_OK;
    }

}
