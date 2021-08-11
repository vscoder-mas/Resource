#include "utils.h"
#include "prebuilt/include/st_mobile_human_action.h"
#include <st_mobile_common.h>
#include <st_mobile_human_action.h>
#include <st_mobile_sticker.h>
#include <st_mobile_sticker_transition.h>
#include <st_mobile_effect.h>

#define  LOG_TAG    "utils"

long getCurrentTime() {
    struct timeval tv;
    gettimeofday(&tv,NULL);
    return tv.tv_sec * 1000 + tv.tv_usec / 1000;
}

int getImageStride(const st_pixel_format &pixel_format, const int &outputWidth) {
    int stride = 0;
    switch(pixel_format) {
        case ST_PIX_FMT_GRAY8:
        case ST_PIX_FMT_YUV420P:
        case ST_PIX_FMT_NV12:
        case ST_PIX_FMT_NV21:
            stride = outputWidth;
            break;
        case ST_PIX_FMT_BGRA8888:
        case ST_PIX_FMT_RGBA8888:
            stride = outputWidth * 4;
            break;
        case ST_PIX_FMT_BGR888:
            stride = outputWidth * 3;
            break;
        default:
            break;
    }

    return stride;
}

jobject convert2STRect(JNIEnv *env, const st_rect_t &object_rect){
    jclass STRectClass = env->FindClass("com/sensetime/stmobile/model/STRect");

    if (STRectClass == NULL) {
        return NULL;
    }

    jobject rectObject = env->AllocObject(STRectClass);

    jfieldID rect_left = env->GetFieldID(STRectClass, "left", "I");
    jfieldID rect_top = env->GetFieldID(STRectClass, "top", "I");
    jfieldID rect_right = env->GetFieldID(STRectClass, "right", "I");
    jfieldID rect_bottom = env->GetFieldID(STRectClass, "bottom", "I");

    env->SetIntField(rectObject, rect_left, object_rect.left);
    env->SetIntField(rectObject, rect_right, object_rect.right);
    env->SetIntField(rectObject, rect_top, object_rect.top);
    env->SetIntField(rectObject, rect_bottom, object_rect.bottom);

    if(STRectClass != NULL){
        env->DeleteLocalRef(STRectClass);
    }

    return rectObject;
}

jobject convert2MobileFace106(JNIEnv *env, const st_mobile_106_t &mobile_106){
    jclass st_mobile_106_class = env->FindClass("com/sensetime/stmobile/model/STMobile106");
    jfieldID frect = env->GetFieldID(st_mobile_106_class, "rect", "Lcom/sensetime/stmobile/model/STRect;");
    jfieldID fscore = env->GetFieldID(st_mobile_106_class, "score", "F");
    jfieldID fpoints_array = env->GetFieldID(st_mobile_106_class, "points_array", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fvisibility_array = env->GetFieldID(st_mobile_106_class, "visibility_array", "[F");
    jfieldID fyaw = env->GetFieldID(st_mobile_106_class, "yaw", "F");
    jfieldID fpitch = env->GetFieldID(st_mobile_106_class, "pitch", "F");
    jfieldID froll = env->GetFieldID(st_mobile_106_class, "roll", "F");
    jfieldID feye_dist = env->GetFieldID(st_mobile_106_class, "eye_dist", "F");
    jfieldID fID = env->GetFieldID(st_mobile_106_class, "ID", "I");

    jclass st_mobile_point_class = env->FindClass("com/sensetime/stmobile/model/STPoint");
    jfieldID fpoint_x = env->GetFieldID(st_mobile_point_class, "x", "F");
    jfieldID fpoint_y = env->GetFieldID(st_mobile_point_class, "y", "F");

    jclass st_face_rect_class = env->FindClass("com/sensetime/stmobile/model/STRect");
    jfieldID frect_left = env->GetFieldID(st_face_rect_class, "left", "I");
    jfieldID frect_top = env->GetFieldID(st_face_rect_class, "top", "I");
    jfieldID frect_right = env->GetFieldID(st_face_rect_class, "right", "I");
    jfieldID frect_bottom = env->GetFieldID(st_face_rect_class, "bottom", "I");

    jobject st_106_object = env->AllocObject(st_mobile_106_class);

    //继续获取rect
    jobject face_rect = env->AllocObject(st_face_rect_class);

    env->SetIntField(face_rect, frect_left, mobile_106.rect.left);
    env->SetIntField(face_rect, frect_right, mobile_106.rect.right);
    env->SetIntField(face_rect, frect_top, mobile_106.rect.top);
    env->SetIntField(face_rect, frect_bottom, mobile_106.rect.bottom);

    //继续获取points_array
    jobjectArray face_point_array = env->NewObjectArray(106, st_mobile_point_class, 0);
    jfloatArray face_visibility_array = env->NewFloatArray(106);
    jfloat visibility_array[106];

    for(int j=0; j<106; j++) {
        jobject st_point_object = env->AllocObject(st_mobile_point_class);

        env->SetFloatField(st_point_object, fpoint_x, mobile_106.points_array[j].x);
        env->SetFloatField(st_point_object, fpoint_y, mobile_106.points_array[j].y);

        env->SetObjectArrayElement(face_point_array, j, st_point_object);
        env->DeleteLocalRef(st_point_object);

        visibility_array[j] = mobile_106.visibility_array[j];
    }
    env->SetFloatArrayRegion(face_visibility_array, 0, 106, visibility_array);
    env->SetObjectField(st_106_object, frect, face_rect);
    env->SetFloatField(st_106_object, fscore, mobile_106.score);
    env->SetObjectField(st_106_object, fpoints_array, face_point_array);
    env->SetObjectField(st_106_object, fvisibility_array, face_visibility_array);
    env->SetFloatField(st_106_object, fyaw, mobile_106.yaw);
    env->SetFloatField(st_106_object, fpitch, mobile_106.pitch);
    env->SetFloatField(st_106_object, froll, mobile_106.roll);
    env->SetFloatField(st_106_object, feye_dist, mobile_106.eye_dist);
    env->SetIntField(st_106_object, fID, mobile_106.ID);

    env->DeleteLocalRef(face_rect);
    env->DeleteLocalRef(face_point_array);
    env->DeleteLocalRef(face_visibility_array);

    if (st_mobile_point_class != NULL) {
        env->DeleteLocalRef(st_mobile_point_class);
    }

    if (st_face_rect_class != NULL) {
        env->DeleteLocalRef(st_face_rect_class);
    }

    if (st_mobile_106_class != NULL) {
        env->DeleteLocalRef(st_mobile_106_class);
    }

    return st_106_object;
}

jobject convert2FaceAttribute(JNIEnv *env, const st_mobile_attributes_t *faceAttribute){
    jclass face_attribute_cls = env->FindClass("com/sensetime/stmobile/model/STFaceAttribute");

    jfieldID fieldAttribute_count = env->GetFieldID(face_attribute_cls, "attribute_count", "I");
    jfieldID fieldAttribute = env->GetFieldID(face_attribute_cls, "arrayAttribute", "[Lcom/sensetime/stmobile/model/STFaceAttribute$Attribute;");

    jobject faceAttributeObj = env->AllocObject(face_attribute_cls);

    env->SetIntField(faceAttributeObj, fieldAttribute_count, faceAttribute->attribute_count);

    jclass attribute_cls = env->FindClass("com/sensetime/stmobile/model/STFaceAttribute$Attribute");
    jfieldID fieldCategory = env->GetFieldID(attribute_cls, "category", "Ljava/lang/String;");
    jfieldID fieldLabel = env->GetFieldID(attribute_cls, "label", "Ljava/lang/String;");
    jfieldID fieldScore = env->GetFieldID(attribute_cls, "score", "F");

    if (faceAttribute->attribute_count > 0) {
        LOGE("attribute_count: %d", faceAttribute->attribute_count);
        jobjectArray arrayAttrObj = env->NewObjectArray(faceAttribute->attribute_count, attribute_cls, 0);
        for (int i = 0; i < faceAttribute->attribute_count; ++i) {
            st_mobile_attribute_t one = faceAttribute->p_attributes[i];
            jobject attrObj = env->AllocObject(attribute_cls);
            jstring cateObj = env->NewStringUTF(one.category);
            jstring labelObj = env->NewStringUTF(one.label);
            env->SetObjectField(attrObj, fieldCategory, cateObj);
            env->SetObjectField(attrObj, fieldLabel, labelObj);
            env->SetFloatField(attrObj, fieldScore, one.score);

            env->SetObjectArrayElement(arrayAttrObj, i, attrObj);
            env->DeleteLocalRef(cateObj);
            env->DeleteLocalRef(labelObj);
            env->DeleteLocalRef(attrObj);
        }

        env->SetObjectField(faceAttributeObj, fieldAttribute, arrayAttrObj);
        env->DeleteLocalRef(arrayAttrObj);
    }

    env->DeleteLocalRef(attribute_cls);
    env->DeleteLocalRef(face_attribute_cls);
    return faceAttributeObj;
}

jobject convert2Image(JNIEnv *env, const st_image_t *image){
    jclass image_cls = env->FindClass("com/sensetime/stmobile/model/STImage");

    jfieldID fieldImageData = env->GetFieldID(image_cls, "imageData", "[B");
    jfieldID fieldPixelFormat = env->GetFieldID(image_cls, "pixelFormat", "I");
    jfieldID fieldWidth = env->GetFieldID(image_cls, "width", "I");
    jfieldID fieldHeight = env->GetFieldID(image_cls, "height", "I");
    jfieldID fieldStride = env->GetFieldID(image_cls, "stride", "I");
    jfieldID fieldTime = env->GetFieldID(image_cls, "timeStamp", "D");

    jobject imageObj = env->AllocObject(image_cls);

    jbyteArray arrayImageData;
    arrayImageData = (env)->NewByteArray(image->width*image->height);
    jbyte* data = (jbyte*)(image->data);
    if(data == NULL){
        return NULL;
    }
    env->SetByteArrayRegion(arrayImageData, 0, image->width*image->height, data);
    env->SetObjectField(imageObj, fieldImageData, arrayImageData);

    env->SetIntField(imageObj, fieldPixelFormat, (int)image->pixel_format);
    env->SetIntField(imageObj, fieldWidth, image->width);
    env->SetIntField(imageObj, fieldHeight, image->height);
    env->SetIntField(imageObj, fieldStride, image->stride);

    env->SetDoubleField(imageObj, fieldTime, image->time_stamp);

    env->DeleteLocalRef(arrayImageData);
    env->DeleteLocalRef(image_cls);

    return imageObj;
}

jobject convert2Segment(JNIEnv *env, const st_mobile_segment_t *segment){
    jclass segment_cls = env->FindClass("com/sensetime/stmobile/model/STSegment");

    jfieldID fieldImage = env->GetFieldID(segment_cls, "image", "Lcom/sensetime/stmobile/model/STImage;");
    jfieldID fieldScore = env->GetFieldID(segment_cls, "score", "F");
    jfieldID fieldMinThrehold = env->GetFieldID(segment_cls, "minThrehold", "F");
    jfieldID fieldMaxThrehold = env->GetFieldID(segment_cls, "maxThrehold", "F");
    jfieldID fieldOffset = env->GetFieldID(segment_cls, "offset", "Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldScale = env->GetFieldID(segment_cls, "scale", "Lcom/sensetime/stmobile/model/STPoint;");

    jobject segmentObj = env->AllocObject(segment_cls);

    jobject imageObj;

    if(segment->p_segment != NULL){
        imageObj = convert2Image(env, segment->p_segment);
        env->SetObjectField(segmentObj, fieldImage, imageObj);
    }

    env->SetFloatField(segmentObj, fieldScore, segment->score);
    env->SetFloatField(segmentObj, fieldMinThrehold, segment->min_threshold);
    env->SetFloatField(segmentObj, fieldMaxThrehold, segment->max_threshold);

    jclass st_points_class = env->FindClass("com/sensetime/stmobile/model/STPoint");
    jfieldID fpoint_x = env->GetFieldID(st_points_class, "x", "F");
    jfieldID fpoint_y = env->GetFieldID(st_points_class, "y", "F");

    jobject offsetObj = env->AllocObject(st_points_class);
    env->SetFloatField(offsetObj, fpoint_x, segment->offset.x);
    env->SetFloatField(offsetObj, fpoint_y, segment->offset.y);
    env->SetObjectField(segmentObj, fieldOffset, offsetObj);

    jobject scaleObj = env->AllocObject(st_points_class);
    env->SetFloatField(scaleObj, fpoint_x, segment->scale.x);
    env->SetFloatField(scaleObj, fpoint_y, segment->scale.y);
    env->SetObjectField(segmentObj, fieldScale, scaleObj);

    env->DeleteLocalRef(st_points_class);

    env->DeleteLocalRef(segment_cls);

    return segmentObj;
}

jobject convert2HandInfo(JNIEnv *env, const st_mobile_hand_t *hand_info){
    jclass hand_info_cls = env->FindClass("com/sensetime/stmobile/model/STMobileHandInfo");

    jfieldID fieldHandId = env->GetFieldID(hand_info_cls, "handId", "I");
    jfieldID fieldHandRect = env->GetFieldID(hand_info_cls, "handRect", "Lcom/sensetime/stmobile/model/STRect;");
    jfieldID fieldKeyPoints = env->GetFieldID(hand_info_cls, "keyPoints", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldKeyPointsCount = env->GetFieldID(hand_info_cls, "keyPointsCount", "I");
    jfieldID fieldHandAction = env->GetFieldID(hand_info_cls, "handAction", "J");
    jfieldID fieldHandActionScore = env->GetFieldID(hand_info_cls, "handActionScore", "F");

    jobject handInfoObj = env->AllocObject(hand_info_cls);

    jclass hand_rect_class = env->FindClass("com/sensetime/stmobile/model/STRect");
    jfieldID hrect_left = env->GetFieldID(hand_rect_class, "left", "I");
    jfieldID hrect_top = env->GetFieldID(hand_rect_class, "top", "I");
    jfieldID hrect_right = env->GetFieldID(hand_rect_class, "right", "I");
    jfieldID hrect_bottom = env->GetFieldID(hand_rect_class, "bottom", "I");

    jobject handRectObj = env->AllocObject(hand_rect_class);
    env->SetIntField(handRectObj, hrect_left, hand_info->rect.left);
    env->SetIntField(handRectObj, hrect_top, hand_info->rect.top);
    env->SetIntField(handRectObj, hrect_right, hand_info->rect.right);
    env->SetIntField(handRectObj, hrect_bottom, hand_info->rect.bottom);

    env->SetObjectField(handInfoObj, fieldHandRect, handRectObj);

    jclass st_points_class = env->FindClass("com/sensetime/stmobile/model/STPoint");
    jfieldID fpoint_x = env->GetFieldID(st_points_class, "x", "F");
    jfieldID fpoint_y = env->GetFieldID(st_points_class, "y", "F");

    //extra_face_points
    jobjectArray key_points_array = env->NewObjectArray(hand_info->key_points_count, st_points_class, 0);
    for(int i = 0; i < hand_info->key_points_count; i++){
        jobject keyPointsObj = env->AllocObject(st_points_class);

        env->SetFloatField(keyPointsObj, fpoint_x, (hand_info->p_key_points+i)->x);
        env->SetFloatField(keyPointsObj, fpoint_y, (hand_info->p_key_points+i)->y);

        env->SetObjectArrayElement(key_points_array, i, keyPointsObj);
        env->DeleteLocalRef(keyPointsObj);
    }

    env->SetObjectField(handInfoObj, fieldKeyPoints, key_points_array);
    env->DeleteLocalRef(key_points_array);

    env->SetIntField(handInfoObj, fieldHandId, hand_info->id);
    env->SetIntField(handInfoObj, fieldKeyPointsCount, hand_info->key_points_count);
    env->SetLongField(handInfoObj, fieldHandAction, hand_info->hand_action);
    env->SetFloatField(handInfoObj, fieldHandActionScore, hand_info->score);

    env->DeleteLocalRef(st_points_class);
    env->DeleteLocalRef(hand_rect_class);
    env->DeleteLocalRef(handRectObj);
    env->DeleteLocalRef(hand_info_cls);

    return handInfoObj;
}

jobject convert2BodyInfo(JNIEnv *env, const st_mobile_body_t *body_info){
    jclass body_info_cls = env->FindClass("com/sensetime/stmobile/model/STMobileBodyInfo");

    jfieldID fieldId = env->GetFieldID(body_info_cls, "id", "I");
    jfieldID fieldKeyPoints = env->GetFieldID(body_info_cls, "keyPoints", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldKeyPointsScore = env->GetFieldID(body_info_cls, "keyPointsScore", "[F");
    jfieldID fieldKeyPointsCount = env->GetFieldID(body_info_cls, "keyPointsCount", "I");
    jfieldID fieldContourPoints = env->GetFieldID(body_info_cls, "contourPoints", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldContourPointsScore = env->GetFieldID(body_info_cls, "contourPointsScore", "[F");
    jfieldID fieldContourPointsCount = env->GetFieldID(body_info_cls, "contourPointsCount", "I");
    jfieldID fieldBodyAction = env->GetFieldID(body_info_cls, "bodyAction", "J");
    jfieldID fieldBodyActionScore = env->GetFieldID(body_info_cls, "bodyActionScore", "F");

    jobject bodyInfoObj = env->AllocObject(body_info_cls);

    jclass st_points_class = env->FindClass("com/sensetime/stmobile/model/STPoint");
    jfieldID fpoint_x = env->GetFieldID(st_points_class, "x", "F");
    jfieldID fpoint_y = env->GetFieldID(st_points_class, "y", "F");

    //key_points
    jobjectArray key_points_array = env->NewObjectArray(body_info->key_points_count, st_points_class, 0);
    jfloatArray key_points_score = env->NewFloatArray(body_info->key_points_count);
    jfloat key_points_score_array[body_info->key_points_count];

    for(int i = 0; i < body_info->key_points_count; i++){
        jobject keyPointsObj = env->AllocObject(st_points_class);

        env->SetFloatField(keyPointsObj, fpoint_x, (body_info->p_key_points+i)->x);
        env->SetFloatField(keyPointsObj, fpoint_y, (body_info->p_key_points+i)->y);

        env->SetObjectArrayElement(key_points_array, i, keyPointsObj);
        env->DeleteLocalRef(keyPointsObj);

        key_points_score_array[i] = body_info->p_key_points_score[i];
    }

    env->SetFloatArrayRegion(key_points_score, 0, body_info->key_points_count, key_points_score_array);
    env->SetObjectField(bodyInfoObj, fieldKeyPointsScore, key_points_score);

    env->SetObjectField(bodyInfoObj, fieldKeyPoints, key_points_array);
    env->DeleteLocalRef(key_points_score);
    env->DeleteLocalRef(key_points_array);

    //contour_points
    jobjectArray contour_points_array = env->NewObjectArray(body_info->contour_points_count, st_points_class, 0);
    jfloatArray contour_points_score = env->NewFloatArray(body_info->contour_points_count);
    jfloat contour_points_score_array[body_info->contour_points_count];

    for(int i = 0; i < body_info->contour_points_count; i++){
        jobject contourPointsObj = env->AllocObject(st_points_class);

        env->SetFloatField(contourPointsObj, fpoint_x, (body_info->p_contour_points+i)->x);
        env->SetFloatField(contourPointsObj, fpoint_y, (body_info->p_contour_points+i)->y);

        env->SetObjectArrayElement(contour_points_array, i, contourPointsObj);
        env->DeleteLocalRef(contourPointsObj);

        contour_points_score_array[i] = body_info->p_contour_points_score[i];
    }

    env->SetFloatArrayRegion(contour_points_score, 0, body_info->contour_points_count, contour_points_score_array);
    env->SetObjectField(bodyInfoObj, fieldContourPointsScore, contour_points_score);

    env->SetObjectField(bodyInfoObj, fieldContourPoints, contour_points_array);
    env->DeleteLocalRef(contour_points_score);
    env->DeleteLocalRef(contour_points_array);

    env->SetIntField(bodyInfoObj, fieldId, body_info->id);
    env->SetIntField(bodyInfoObj, fieldKeyPointsCount, body_info->key_points_count);
    env->SetIntField(bodyInfoObj, fieldContourPointsCount, body_info->contour_points_count);
    env->SetLongField(bodyInfoObj, fieldBodyAction, body_info->body_action);
    env->SetFloatField(bodyInfoObj, fieldBodyActionScore, body_info->body_action_score);

    env->DeleteLocalRef(st_points_class);
    env->DeleteLocalRef(body_info_cls);

    return bodyInfoObj;
}

jobject convert2FaceInfo(JNIEnv *env, const st_mobile_face_t *face_info){
    jclass face_info_cls = env->FindClass("com/sensetime/stmobile/model/STMobileFaceInfo");

    jfieldID fieldFace = env->GetFieldID(face_info_cls, "face106", "Lcom/sensetime/stmobile/model/STMobile106;");

    jfieldID fieldExtraFacePoints = env->GetFieldID(face_info_cls, "extraFacePoints", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldExtraFacePointsCount = env->GetFieldID(face_info_cls, "extraFacePointsCount", "I");

    jfieldID fieldTonguePoints = env->GetFieldID(face_info_cls, "tonguePoints", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldTonguePointsScore = env->GetFieldID(face_info_cls, "tonguePointsScore", "[F");
    jfieldID fieldTonguePointsCount = env->GetFieldID(face_info_cls, "tonguePointsCount", "I");

    jfieldID fieldEyeballCenter = env->GetFieldID(face_info_cls, "eyeballCenter", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldEyeballCenterPointsCount = env->GetFieldID(face_info_cls, "eyeballCenterPointsCount", "I");

    jfieldID fieldEyeballContour = env->GetFieldID(face_info_cls, "eyeballContour", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldEyeballContourPointsCount = env->GetFieldID(face_info_cls, "eyeballContourPointsCount", "I");

    jfieldID fieldLeftEyeballScore = env->GetFieldID(face_info_cls, "leftEyeballScore", "F");
    jfieldID fieldRightEyeballScore = env->GetFieldID(face_info_cls, "rightEyeballScore", "F");

    jfieldID fieldFaceAction = env->GetFieldID(face_info_cls, "faceAction", "J");
    jfieldID fieldFaceActionScore = env->GetFieldID(face_info_cls, "faceActionScore", "[F");
    jfieldID fieldFaceActionScoreCount = env->GetFieldID(face_info_cls, "faceActionScoreCount", "I");

    jfieldID fieldFaceExtraInfo = env->GetFieldID(face_info_cls, "faceExtraInfo", "Lcom/sensetime/stmobile/model/STFaceExtraInfo;");

    jfieldID fieldAvatarHelpInfo = env->GetFieldID(face_info_cls, "avatarHelpInfo", "[B");
    jfieldID fieldAvatarHelpInfoLength = env->GetFieldID(face_info_cls, "avatarHelpInfoLength", "I");

    jobject faceInfoObj = env->AllocObject(face_info_cls);

    //face106
    jclass face106Class = env->FindClass("com/sensetime/stmobile/model/STMobile106");

    jobject face106_object = env->AllocObject(face106Class);
    face106_object = convert2MobileFace106(env, face_info->face106);

    env->SetObjectField(faceInfoObj, fieldFace, face106_object);
    env->DeleteLocalRef(face106_object);

    jclass st_points_class = env->FindClass("com/sensetime/stmobile/model/STPoint");
    jfieldID fpoint_x = env->GetFieldID(st_points_class, "x", "F");
    jfieldID fpoint_y = env->GetFieldID(st_points_class, "y", "F");
    env->DeleteLocalRef(face106Class);

    //extra_face_points
    jobjectArray extra_face_points_array = env->NewObjectArray(face_info->extra_face_points_count, st_points_class, 0);
    for(int i = 0; i < face_info->extra_face_points_count; i++){
        jobject extraFacePointsObj = env->AllocObject(st_points_class);

        env->SetFloatField(extraFacePointsObj, fpoint_x, face_info->p_extra_face_points[i].x);
        env->SetFloatField(extraFacePointsObj, fpoint_y, face_info->p_extra_face_points[i].y);

        env->SetObjectArrayElement(extra_face_points_array, i, extraFacePointsObj);
        env->DeleteLocalRef(extraFacePointsObj);
    }

    env->SetObjectField(faceInfoObj, fieldExtraFacePoints, extra_face_points_array);
    env->DeleteLocalRef(extra_face_points_array);

    env->SetIntField(faceInfoObj, fieldExtraFacePointsCount, face_info->extra_face_points_count);

    env->SetFloatField(faceInfoObj, fieldLeftEyeballScore, face_info->left_eyeball_score);
    env->SetFloatField(faceInfoObj, fieldRightEyeballScore, face_info->right_eyeball_score);

    //tongue_points
    jobjectArray tongue_points_array = env->NewObjectArray(face_info->tongue_points_count, st_points_class, 0);
    jfloatArray tongue_points_score = env->NewFloatArray(face_info->tongue_points_count);
    jfloat tongue_points_score_array[face_info->tongue_points_count];

    for(int i = 0; i < face_info->tongue_points_count; i++){
        jobject tonguePointsObj = env->AllocObject(st_points_class);

        env->SetFloatField(tonguePointsObj, fpoint_x, (face_info->p_tongue_points+i)->x);
        env->SetFloatField(tonguePointsObj, fpoint_y, (face_info->p_tongue_points+i)->y);

        env->SetObjectArrayElement(tongue_points_array, i, tonguePointsObj);
        env->DeleteLocalRef(tonguePointsObj);

        tongue_points_score_array[i] = face_info->p_tongue_points_score[i];
    }

    env->SetFloatArrayRegion(tongue_points_score, 0, face_info->tongue_points_count, tongue_points_score_array);
    env->SetObjectField(faceInfoObj, fieldTonguePointsScore, tongue_points_score);

    env->SetObjectField(faceInfoObj, fieldTonguePoints, tongue_points_array);
    env->DeleteLocalRef(tongue_points_score);
    env->DeleteLocalRef(tongue_points_array);
    env->SetIntField(faceInfoObj, fieldTonguePointsCount, face_info->tongue_points_count);

    //eyeball_center
    jobjectArray eyeball_center_array = env->NewObjectArray(face_info->eyeball_center_points_count, st_points_class, 0);
    for(int i = 0; i < face_info->eyeball_center_points_count; i++){
        jobject eyeballCenterObj = env->AllocObject(st_points_class);

        env->SetFloatField(eyeballCenterObj, fpoint_x, face_info->p_eyeball_center[i].x);
        env->SetFloatField(eyeballCenterObj, fpoint_y, face_info->p_eyeball_center[i].y);

        env->SetObjectArrayElement(eyeball_center_array, i, eyeballCenterObj);
        env->DeleteLocalRef(eyeballCenterObj);
    }

    env->SetObjectField(faceInfoObj, fieldEyeballCenter, eyeball_center_array);
    env->DeleteLocalRef(eyeball_center_array);

    env->SetIntField(faceInfoObj, fieldEyeballCenterPointsCount, face_info->eyeball_center_points_count);

    //eyeball_contour
    jobjectArray eyeball_contour_array = env->NewObjectArray(face_info->eyeball_contour_points_count, st_points_class, 0);
    for(int i = 0; i < face_info->eyeball_contour_points_count; i++){
        jobject eyeballContourObj = env->AllocObject(st_points_class);

        env->SetFloatField(eyeballContourObj, fpoint_x, face_info->p_eyeball_contour[i].x);
        env->SetFloatField(eyeballContourObj, fpoint_y, face_info->p_eyeball_contour[i].y);

        env->SetObjectArrayElement(eyeball_contour_array, i, eyeballContourObj);
        env->DeleteLocalRef(eyeballContourObj);
    }

    env->SetObjectField(faceInfoObj, fieldEyeballContour, eyeball_contour_array);
    env->DeleteLocalRef(eyeball_contour_array);

    env->SetIntField(faceInfoObj, fieldEyeballContourPointsCount, face_info->eyeball_contour_points_count);

    //face Action score
    env->SetLongField(faceInfoObj, fieldFaceAction, face_info->face_action);

    env->SetIntField(faceInfoObj, fieldFaceActionScoreCount, face_info->face_action_score_count);
    jfloatArray face_action_score_array = env->NewFloatArray(face_info->face_action_score_count);
    env->SetFloatArrayRegion(face_action_score_array, 0, face_info->face_action_score_count, face_info->p_face_action_score);
    env->SetObjectField(faceInfoObj, fieldFaceActionScore, face_action_score_array);
    env->DeleteLocalRef(face_action_score_array);

    //avatar extra info
    jclass extraClass = env->FindClass("com/sensetime/stmobile/model/STFaceExtraInfo");
    jobject extra_object = env->AllocObject(extraClass);

    extra_object = convert2FaceExtraInfo(env, face_info->face_extra_info );

    env->SetObjectField(faceInfoObj, fieldFaceExtraInfo, extra_object);
    env->DeleteLocalRef(extraClass);


    //avatar help info
    env->SetIntField(faceInfoObj, fieldAvatarHelpInfoLength, face_info->avatar_help_info_length);
    jbyteArray arrayImageData;
    arrayImageData = (env)->NewByteArray(face_info->avatar_help_info_length);
    jbyte* data = (jbyte*)(face_info->p_avatar_help_info);

    env->SetByteArrayRegion(arrayImageData, 0, face_info->avatar_help_info_length, data);
    env->SetObjectField(faceInfoObj, fieldAvatarHelpInfo, arrayImageData);
    env->DeleteLocalRef(arrayImageData);

    env->DeleteLocalRef(st_points_class);
    env->DeleteLocalRef(face_info_cls);

    return faceInfoObj;
}

jobject convert2HumanAction(JNIEnv *env, const st_mobile_human_action_t *human_action){
    if(human_action == nullptr) return NULL;

    jclass human_action_cls = env->FindClass("com/sensetime/stmobile/model/STHumanAction");

    jfieldID fieldFaces = env->GetFieldID(human_action_cls, "faces", "[Lcom/sensetime/stmobile/model/STMobileFaceInfo;");
    jfieldID fieldFaceCount = env->GetFieldID(human_action_cls, "faceCount", "I");

    jfieldID fieldHands = env->GetFieldID(human_action_cls, "hands", "[Lcom/sensetime/stmobile/model/STMobileHandInfo;");
    jfieldID fieldHandCount = env->GetFieldID(human_action_cls, "handCount", "I");

    jfieldID fieldBodys = env->GetFieldID(human_action_cls, "bodys", "[Lcom/sensetime/stmobile/model/STMobileBodyInfo;");
    jfieldID fieldBodyCount = env->GetFieldID(human_action_cls, "bodyCount", "I");

    jfieldID fieldImage = env->GetFieldID(human_action_cls, "image", "Lcom/sensetime/stmobile/model/STSegment;");

    jfieldID fieldHair = env->GetFieldID(human_action_cls, "hair", "Lcom/sensetime/stmobile/model/STSegment;");
    jfieldID fieldHead = env->GetFieldID(human_action_cls, "head", "Lcom/sensetime/stmobile/model/STSegment;");

    jfieldID fieldMultiSegment = env->GetFieldID(human_action_cls, "multiSegment", "Lcom/sensetime/stmobile/model/STSegment;");
    jfieldID fieldMouthParse = env->GetFieldID(human_action_cls, "mouthParses", "[Lcom/sensetime/stmobile/model/STSegment;");
    jfieldID fieldMouthParseCount = env->GetFieldID(human_action_cls, "mouthParseCount", "I");

    jobject humanActionObj = env->AllocObject(human_action_cls);

    //faces
    env->SetIntField(humanActionObj, fieldFaceCount, human_action->face_count);

    jclass face_info_cls = env->FindClass("com/sensetime/stmobile/model/STMobileFaceInfo");
    jobjectArray face_info_array = env->NewObjectArray(human_action->face_count, face_info_cls, 0);
    for(int i = 0; i < human_action->face_count; i++){
        jobject faceInfoObj = env->AllocObject(face_info_cls);

        faceInfoObj = convert2FaceInfo(env, human_action->p_faces+i);

        env->SetObjectArrayElement(face_info_array, i, faceInfoObj);
        env->DeleteLocalRef(faceInfoObj);
    }

    env->SetObjectField(humanActionObj, fieldFaces, face_info_array);
    env->DeleteLocalRef(face_info_array);
    env->DeleteLocalRef(face_info_cls);

    //hands
    env->SetIntField(humanActionObj, fieldHandCount, human_action->hand_count);

    jclass hand_info_cls = env->FindClass("com/sensetime/stmobile/model/STMobileHandInfo");
    jobjectArray hand_info_array = env->NewObjectArray(human_action->hand_count, hand_info_cls, 0);
    for(int i = 0; i < human_action->hand_count; i++){
        jobject handInfoObj = env->AllocObject(hand_info_cls);

        handInfoObj = convert2HandInfo(env, human_action->p_hands+i);

        env->SetObjectArrayElement(hand_info_array, i, handInfoObj);
        env->DeleteLocalRef(handInfoObj);
    }

    env->SetObjectField(humanActionObj, fieldHands, hand_info_array);
    env->DeleteLocalRef(hand_info_array);
    env->DeleteLocalRef(hand_info_cls);

    //bodys
    env->SetIntField(humanActionObj, fieldBodyCount, human_action->body_count);

    jclass body_info_cls = env->FindClass("com/sensetime/stmobile/model/STMobileBodyInfo");
    jobjectArray body_info_array = env->NewObjectArray(human_action->body_count, body_info_cls, 0);
    for(int i = 0; i < human_action->body_count; i++){
        jobject bodyInfoObj = env->AllocObject(body_info_cls);

        bodyInfoObj = convert2BodyInfo(env, human_action->p_bodys+i);

        env->SetObjectArrayElement(body_info_array, i, bodyInfoObj);
        env->DeleteLocalRef(bodyInfoObj);
    }

    env->SetObjectField(humanActionObj, fieldBodys, body_info_array);
    env->DeleteLocalRef(body_info_array);
    env->DeleteLocalRef(body_info_cls);

    //image
    if(human_action->p_figure != NULL){
        jclass segmentClass = env->FindClass("com/sensetime/stmobile/model/STSegment");
        jobject segment_object = env->AllocObject(segmentClass);
        segment_object = convert2Segment(env, human_action->p_figure);

        env->SetObjectField(humanActionObj, fieldImage, segment_object);
        env->DeleteLocalRef(segmentClass);
    }

    //hair
    if(human_action->p_hair != NULL){
        jclass segmentClass = env->FindClass("com/sensetime/stmobile/model/STSegment");
        jobject segment_object = env->AllocObject(segmentClass);
        segment_object = convert2Segment(env, human_action->p_hair);

        env->SetObjectField(humanActionObj, fieldHair, segment_object);
        env->DeleteLocalRef(segmentClass);
    }

    //head
    if(human_action->p_head != NULL){
        jclass segmentClass = env->FindClass("com/sensetime/stmobile/model/STSegment");
        jobject segment_object = env->AllocObject(segmentClass);
        segment_object = convert2Segment(env, human_action->p_head);

        env->SetObjectField(humanActionObj, fieldHead, segment_object);
        env->DeleteLocalRef(segmentClass);
    }

    //mouth parse
    env->SetIntField(humanActionObj, fieldMouthParseCount, human_action->mouth_parse_count);

    jclass mouthParseClass = env->FindClass("com/sensetime/stmobile/model/STSegment");
    jobjectArray mouth_parse_array = env->NewObjectArray(human_action->mouth_parse_count, mouthParseClass, 0);
    for(int i = 0; i < human_action->mouth_parse_count; i++){
        jobject mouthParseObj = env->AllocObject(mouthParseClass);
        mouthParseObj = convert2Segment(env, human_action->p_mouth_parse+i);

        env->SetObjectArrayElement(mouth_parse_array, i, mouthParseObj);
        env->DeleteLocalRef(mouthParseObj);
    }
    env->SetObjectField(humanActionObj, fieldMouthParse, mouth_parse_array);
    env->DeleteLocalRef(mouth_parse_array);
    env->DeleteLocalRef(mouthParseClass);

    //MultiSegment
    if(human_action->p_multi != NULL){
        jclass segmentClass = env->FindClass("com/sensetime/stmobile/model/STSegment");
        jobject segment_object = env->AllocObject(segmentClass);
        segment_object = convert2Segment(env, human_action->p_multi);

        env->SetObjectField(humanActionObj, fieldMultiSegment, segment_object);
        env->DeleteLocalRef(segmentClass);
    }

    env->DeleteLocalRef(human_action_cls);

    return humanActionObj;
}

void convert2HumanAction(JNIEnv *env, const st_mobile_human_action_t *human_action, jobject humanActionObj){
    if(human_action == nullptr) return;

    jclass human_action_cls = env->FindClass("com/sensetime/stmobile/model/STHumanAction");

    jfieldID fieldFaces = env->GetFieldID(human_action_cls, "faces", "[Lcom/sensetime/stmobile/model/STMobileFaceInfo;");
    jfieldID fieldFaceCount = env->GetFieldID(human_action_cls, "faceCount", "I");

    jfieldID fieldHands = env->GetFieldID(human_action_cls, "hands", "[Lcom/sensetime/stmobile/model/STMobileHandInfo;");
    jfieldID fieldHandCount = env->GetFieldID(human_action_cls, "handCount", "I");

    jfieldID fieldBodys = env->GetFieldID(human_action_cls, "bodys", "[Lcom/sensetime/stmobile/model/STMobileBodyInfo;");
    jfieldID fieldBodyCount = env->GetFieldID(human_action_cls, "bodyCount", "I");

    jfieldID fieldImage = env->GetFieldID(human_action_cls, "image", "Lcom/sensetime/stmobile/model/STSegment;");

    jfieldID fieldHair = env->GetFieldID(human_action_cls, "hair", "Lcom/sensetime/stmobile/model/STSegment;");
    jfieldID fieldSkin = env->GetFieldID(human_action_cls, "skin", "Lcom/sensetime/stmobile/model/STSegment;");
    jfieldID fieldHead = env->GetFieldID(human_action_cls, "head", "Lcom/sensetime/stmobile/model/STSegment;");
    jfieldID fieldMouthParse = env->GetFieldID(human_action_cls, "mouthParses", "[Lcom/sensetime/stmobile/model/STSegment;");
    jfieldID fieldMouthParseCount = env->GetFieldID(human_action_cls, "mouthParseCount", "I");

    jfieldID fieldMultiSegment = env->GetFieldID(human_action_cls, "multiSegment", "Lcom/sensetime/stmobile/model/STSegment;");

    //faces
    env->SetIntField(humanActionObj, fieldFaceCount, human_action->face_count);

    jclass face_info_cls = env->FindClass("com/sensetime/stmobile/model/STMobileFaceInfo");
    jobjectArray face_info_array = env->NewObjectArray(human_action->face_count, face_info_cls, 0);
    for(int i = 0; i < human_action->face_count; i++){
        jobject faceInfoObj = env->AllocObject(face_info_cls);

        faceInfoObj = convert2FaceInfo(env, human_action->p_faces+i);

        env->SetObjectArrayElement(face_info_array, i, faceInfoObj);
        env->DeleteLocalRef(faceInfoObj);
    }

    env->SetObjectField(humanActionObj, fieldFaces, face_info_array);
    env->DeleteLocalRef(face_info_array);
    env->DeleteLocalRef(face_info_cls);

    //hands
    env->SetIntField(humanActionObj, fieldHandCount, human_action->hand_count);

    jclass hand_info_cls = env->FindClass("com/sensetime/stmobile/model/STMobileHandInfo");
    jobjectArray hand_info_array = env->NewObjectArray(human_action->hand_count, hand_info_cls, 0);
    for(int i = 0; i < human_action->hand_count; i++){
        jobject handInfoObj = env->AllocObject(hand_info_cls);

        handInfoObj = convert2HandInfo(env, human_action->p_hands+i);

        env->SetObjectArrayElement(hand_info_array, i, handInfoObj);
        env->DeleteLocalRef(handInfoObj);
    }

    env->SetObjectField(humanActionObj, fieldHands, hand_info_array);
    env->DeleteLocalRef(hand_info_array);
    env->DeleteLocalRef(hand_info_cls);

    //bodys
    env->SetIntField(humanActionObj, fieldBodyCount, human_action->body_count);

    jclass body_info_cls = env->FindClass("com/sensetime/stmobile/model/STMobileBodyInfo");
    jobjectArray body_info_array = env->NewObjectArray(human_action->body_count, body_info_cls, 0);
    for(int i = 0; i < human_action->body_count; i++){
        jobject bodyInfoObj = env->AllocObject(body_info_cls);

        bodyInfoObj = convert2BodyInfo(env, human_action->p_bodys+i);

        env->SetObjectArrayElement(body_info_array, i, bodyInfoObj);
        env->DeleteLocalRef(bodyInfoObj);
    }

    env->SetObjectField(humanActionObj, fieldBodys, body_info_array);
    env->DeleteLocalRef(body_info_array);
    env->DeleteLocalRef(body_info_cls);

    //image
    if(human_action->p_figure != NULL){
        jclass segmentClass = env->FindClass("com/sensetime/stmobile/model/STSegment");
        jobject segment_object = env->AllocObject(segmentClass);
        segment_object = convert2Segment(env, human_action->p_figure);

        env->SetObjectField(humanActionObj, fieldImage, segment_object);
        env->DeleteLocalRef(segmentClass);
    }

    //hair
    if(human_action->p_hair != NULL){
        jclass segmentClass = env->FindClass("com/sensetime/stmobile/model/STSegment");
        jobject segment_object = env->AllocObject(segmentClass);
        segment_object = convert2Segment(env, human_action->p_hair);

        env->SetObjectField(humanActionObj, fieldHair, segment_object);
        env->DeleteLocalRef(segmentClass);
    }

    //skin
    if(human_action->p_skin != NULL){
        jclass segmentClass = env->FindClass("com/sensetime/stmobile/model/STSegment");
        jobject segment_object = env->AllocObject(segmentClass);
        segment_object = convert2Segment(env, human_action->p_skin);

        env->SetObjectField(humanActionObj, fieldSkin, segment_object);
        env->DeleteLocalRef(segmentClass);
    }

    //head
    if(human_action->p_head != NULL){
        jclass segmentClass = env->FindClass("com/sensetime/stmobile/model/STSegment");
        jobject segment_object = env->AllocObject(segmentClass);
        segment_object = convert2Segment(env, human_action->p_head);

        env->SetObjectField(humanActionObj, fieldHead, segment_object);
        env->DeleteLocalRef(segmentClass);
    }

    //mouth parse
    env->SetIntField(humanActionObj, fieldMouthParseCount, human_action->mouth_parse_count);

    jclass mouthParseClass = env->FindClass("com/sensetime/stmobile/model/STSegment");
    jobjectArray mouth_parse_array = env->NewObjectArray(human_action->mouth_parse_count, mouthParseClass, 0);
    for(int i = 0; i < human_action->mouth_parse_count; i++){
        jobject mouthParseObj = env->AllocObject(mouthParseClass);
        mouthParseObj = convert2Segment(env, human_action->p_mouth_parse+i);

        env->SetObjectArrayElement(mouth_parse_array, i, mouthParseObj);
        env->DeleteLocalRef(mouthParseObj);
    }
    env->SetObjectField(humanActionObj, fieldMouthParse, mouth_parse_array);
    env->DeleteLocalRef(mouth_parse_array);
    env->DeleteLocalRef(mouthParseClass);

    //MultiSegment
    if(human_action->p_multi != NULL){
        jclass segmentClass = env->FindClass("com/sensetime/stmobile/model/STSegment");
        jobject segment_object = env->AllocObject(segmentClass);
        segment_object = convert2Segment(env, human_action->p_multi);

        env->SetObjectField(humanActionObj, fieldMultiSegment, segment_object);
        env->DeleteLocalRef(segmentClass);
    }

    env->DeleteLocalRef(human_action_cls);
}
jobject convert2ModuleInfo(JNIEnv *env, const st_module_info *module_info){
    jclass module_info_cls = env->FindClass("com/sensetime/stmobile/sticker_module_types/STModuleInfo");

    jfieldID fieldId = env->GetFieldID(module_info_cls, "id", "I");
    jfieldID fieldPackageId = env->GetFieldID(module_info_cls, "packageId", "I");

    jfieldID fieldModuleType = env->GetFieldID(module_info_cls, "moduleType", "I");
    jfieldID fieldEnabled = env->GetFieldID(module_info_cls, "enabled", "Z");

    jfieldID fieldName = env->GetFieldID(module_info_cls, "name", "[B");

    jobject moduleInfoObj = env->AllocObject(module_info_cls);

    env->SetIntField(moduleInfoObj, fieldId, module_info->id);
    env->SetIntField(moduleInfoObj, fieldPackageId, module_info->package_id);
    env->SetIntField(moduleInfoObj, fieldModuleType, (int)module_info->type);
    env->SetBooleanField(moduleInfoObj, fieldEnabled, module_info->enabled);

    jbyteArray arrayName;
    jbyte* name = (jbyte*)(module_info->name);
    int len = strlen(module_info->name);
    arrayName = (env)->NewByteArray(len + 1);

    if(name == NULL){
        return NULL;
    }
    env->SetByteArrayRegion(arrayName, 0, len + 1, name);
    env->SetObjectField(moduleInfoObj, fieldName, arrayName);

    env->DeleteLocalRef(arrayName);
    env->DeleteLocalRef(module_info_cls);

    return moduleInfoObj;
}

jobject convert2AnimalFace(JNIEnv *env, const st_mobile_animal_face_t *animal_face){
    jclass animal_face_cls = env->FindClass("com/sensetime/stmobile/model/STAnimalFace");

    jfieldID fieldId = env->GetFieldID(animal_face_cls, "id", "I");
    jfieldID fieldRect = env->GetFieldID(animal_face_cls, "rect", "Lcom/sensetime/stmobile/model/STRect;");
    jfieldID fieldScore = env->GetFieldID(animal_face_cls, "score", "F");
    jfieldID fieldKeyPoints = env->GetFieldID(animal_face_cls, "p_key_points", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldKeyPointsCount = env->GetFieldID(animal_face_cls, "key_points_count", "I");
    jfieldID fieldYaw = env->GetFieldID(animal_face_cls, "yaw", "F");
    jfieldID fieldPitch = env->GetFieldID(animal_face_cls, "pitch", "F");
    jfieldID fieldRoll = env->GetFieldID(animal_face_cls, "roll", "F");

    jobject animalfaceObj = env->AllocObject(animal_face_cls);

    env->SetIntField(animalfaceObj, fieldId, animal_face->id);
    jobject strectObj = convert2STRect(env, animal_face->rect);
    env->SetObjectField(animalfaceObj, fieldRect, strectObj);
    env->SetFloatField(animalfaceObj, fieldScore, animal_face->score);
    env->SetIntField(animalfaceObj, fieldKeyPointsCount, animal_face->key_points_count);
    env->SetFloatField(animalfaceObj, fieldYaw, animal_face->yaw);
    env->SetFloatField(animalfaceObj, fieldPitch, animal_face->pitch);
    env->SetFloatField(animalfaceObj, fieldRoll, animal_face->roll);

    jclass st_point_class = env->FindClass("com/sensetime/stmobile/model/STPoint");
    jfieldID fpoint_x = env->GetFieldID(st_point_class, "x", "F");
    jfieldID fpoint_y = env->GetFieldID(st_point_class, "y", "F");

    jobjectArray key_points_array = env->NewObjectArray(animal_face->key_points_count, st_point_class, 0);

    for(int i = 0; i < animal_face->key_points_count; i++){
        jobject keyPointsObj = env->AllocObject(st_point_class);

        env->SetFloatField(keyPointsObj, fpoint_x, (animal_face->p_key_points+i)->x);
        env->SetFloatField(keyPointsObj, fpoint_y, (animal_face->p_key_points+i)->y);

        env->SetObjectArrayElement(key_points_array, i, keyPointsObj);
        env->DeleteLocalRef(keyPointsObj);
    }

    env->SetObjectField(animalfaceObj, fieldKeyPoints, key_points_array);
    env->DeleteLocalRef(key_points_array);

    env->DeleteLocalRef(st_point_class);
    env->DeleteLocalRef(animal_face_cls);

    return animalfaceObj;
}

bool convert2st_rect_t(JNIEnv *env, jobject rectObject, st_rect_t &rect){
    if(rectObject == NULL){
        return false;
    }

    jclass STRectClass = env->GetObjectClass(rectObject);

    if (STRectClass == NULL) {
        return false;
    }

    jfieldID rect_left = env->GetFieldID(STRectClass, "left", "I");
    jfieldID rect_top = env->GetFieldID(STRectClass, "top", "I");
    jfieldID rect_right = env->GetFieldID(STRectClass, "right", "I");
    jfieldID rect_bottom = env->GetFieldID(STRectClass, "bottom", "I");

    rect.left = env->GetIntField(rectObject, rect_left);
    rect.top = env->GetIntField(rectObject, rect_top);
    rect.right = env->GetIntField(rectObject, rect_right);
    rect.bottom = env->GetIntField(rectObject, rect_bottom);

    if(STRectClass != NULL){
        env->DeleteLocalRef(STRectClass);
    }

    return true;
}

bool convert2mobile_106(JNIEnv *env, jobject face106, st_mobile_106_t &mobile_106)
{
    if (face106 == NULL) {
        return false;
    }

    jclass st_mobile_106_class = env->FindClass("com/sensetime/stmobile/model/STMobile106");
    jfieldID frect = env->GetFieldID(st_mobile_106_class, "rect", "Lcom/sensetime/stmobile/model/STRect;");
    jfieldID fscore = env->GetFieldID(st_mobile_106_class, "score", "F");
    jfieldID fpoints_array = env->GetFieldID(st_mobile_106_class, "points_array", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fvisibility_array = env->GetFieldID(st_mobile_106_class, "visibility_array", "[F");
    jfieldID fyaw = env->GetFieldID(st_mobile_106_class, "yaw", "F");
    jfieldID fpitch = env->GetFieldID(st_mobile_106_class, "pitch", "F");
    jfieldID froll = env->GetFieldID(st_mobile_106_class, "roll", "F");
    jfieldID feye_dist = env->GetFieldID(st_mobile_106_class, "eye_dist", "F");
    jfieldID fID = env->GetFieldID(st_mobile_106_class, "ID", "I");

    jclass st_mobile_point_class = env->FindClass("com/sensetime/stmobile/model/STPoint");
    jfieldID fpoint_x = env->GetFieldID(st_mobile_point_class, "x", "F");
    jfieldID fpoint_y = env->GetFieldID(st_mobile_point_class, "y", "F");

    jclass st_face_rect_class = env->FindClass("com/sensetime/stmobile/model/STRect");
    jfieldID frect_left = env->GetFieldID(st_face_rect_class, "left", "I");
    jfieldID frect_top = env->GetFieldID(st_face_rect_class, "top", "I");
    jfieldID frect_right = env->GetFieldID(st_face_rect_class, "right", "I");
    jfieldID frect_bottom = env->GetFieldID(st_face_rect_class, "bottom", "I");

    mobile_106.score = env->GetFloatField(face106, fscore);
    mobile_106.yaw = env->GetFloatField(face106, fyaw);
    mobile_106.pitch = env->GetFloatField(face106, fpitch);
    mobile_106.roll = env->GetFloatField(face106, froll);
    mobile_106.eye_dist = env->GetFloatField(face106, feye_dist);
    mobile_106.ID = env->GetIntField(face106, fID);

    jobject faceRect = env->GetObjectField(face106, frect);
    mobile_106.rect.left = env->GetIntField(faceRect, frect_left);
    mobile_106.rect.right = env->GetIntField(faceRect, frect_right);
    mobile_106.rect.top = env->GetIntField(faceRect, frect_top);
    mobile_106.rect.bottom = env->GetIntField(faceRect, frect_bottom);

    jobjectArray points_array = (jobjectArray)env->GetObjectField(face106, fpoints_array);
    jfloatArray face_visibility_array = (jfloatArray)env->GetObjectField(face106, fvisibility_array);
    jfloat* visibility_array = env->GetFloatArrayElements(face_visibility_array, 0);

    for (int j = 0; j < 106; ++j)
    {
        jobject point = env->GetObjectArrayElement(points_array, j);

        mobile_106.points_array[j].x = env->GetFloatField(point, fpoint_x);
        mobile_106.points_array[j].y = env->GetFloatField(point, fpoint_y);
        env->DeleteLocalRef(point);

        mobile_106.visibility_array[j] = visibility_array[j];
    }

    env->ReleaseFloatArrayElements(face_visibility_array, visibility_array, JNI_FALSE);
    env->DeleteLocalRef(face_visibility_array);
    env->DeleteLocalRef(points_array);
    env->DeleteLocalRef(faceRect);
    env->DeleteLocalRef(st_mobile_106_class);
    env->DeleteLocalRef(st_face_rect_class);
    env->DeleteLocalRef(st_mobile_point_class);

    return true;
}

bool convert2Image(JNIEnv *env, jobject image, st_image_t *background){
    if (image == NULL) {
        return false;
    }

    jclass image_cls = env->FindClass("com/sensetime/stmobile/model/STImage");

    jfieldID fieldImageData = env->GetFieldID(image_cls, "imageData", "[B");
    jfieldID fieldPixelFormat = env->GetFieldID(image_cls, "pixelFormat", "I");
    jfieldID fieldWidth = env->GetFieldID(image_cls, "width", "I");
    jfieldID fieldHeight = env->GetFieldID(image_cls, "height", "I");
    jfieldID fieldStride = env->GetFieldID(image_cls, "stride", "I");
    jfieldID fieldTime = env->GetFieldID(image_cls, "timeStamp", "D");

    jobject imageData = env->GetObjectField(image, fieldImageData);
    jbyteArray *arr = reinterpret_cast<jbyteArray*>(&imageData);
    jboolean isCopy = 0;
    jbyte* data = env->GetByteArrayElements(*arr, &isCopy);
    background->data = (unsigned char*)data;

    background->pixel_format = (st_pixel_format)env->GetIntField(image, fieldPixelFormat);
    background->width = env->GetIntField(image, fieldWidth);
    background->height = env->GetIntField(image, fieldHeight);
    background->stride = env->GetIntField(image, fieldStride);
//    background->time_stamp = env->GetDoubleField(image, fieldTime);
    double time_stamp = 0.0f;
    if(isCopy)time_stamp = 1.0f;
    background->time_stamp = time_stamp;

    env->ReleaseByteArrayElements(*arr, data, JNI_COMMIT);
    env->DeleteLocalRef(imageData);
    env->DeleteLocalRef(image_cls);

    //test for jni memory leak
    //jclass vm_class = env->FindClass("dalvik/system/VMDebug");
    //jmethodID dump_mid = env->GetStaticMethodID( vm_class, "dumpReferenceTables", "()V" );
    //env->CallStaticVoidMethod( vm_class, dump_mid );

    return true;
}

bool convert2Segment(JNIEnv *env, jobject segmentObj, st_mobile_segment_t *segment){
    if (segmentObj == NULL) {
        return false;
    }

    jclass segment_cls = env->FindClass("com/sensetime/stmobile/model/STSegment");

    jfieldID fieldImage = env->GetFieldID(segment_cls, "image", "Lcom/sensetime/stmobile/model/STImage;");
    jfieldID fieldScore = env->GetFieldID(segment_cls, "score", "F");
    jfieldID fieldMinThrehold = env->GetFieldID(segment_cls, "minThrehold", "F");
    jfieldID fieldMaxThrehold = env->GetFieldID(segment_cls, "maxThrehold", "F");
    jfieldID fieldOffset = env->GetFieldID(segment_cls, "offset", "Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldScale = env->GetFieldID(segment_cls, "scale", "Lcom/sensetime/stmobile/model/STPoint;");

    jobject imageObj = env->GetObjectField(segmentObj, fieldImage);

    if(imageObj != NULL){
        segment->p_segment = new st_image_t;
        memset(segment->p_segment, 0, sizeof(st_image_t));

        convert2Image(env, imageObj, segment->p_segment);
    } else{
        segment->p_segment = NULL;
    }

    segment->score = env->GetFloatField(segmentObj, fieldScore);
    segment->min_threshold = env->GetFloatField(segmentObj, fieldMinThrehold);
    segment->max_threshold = env->GetFloatField(segmentObj, fieldMaxThrehold);

    jclass point_class = env->FindClass("com/sensetime/stmobile/model/STPoint");
    jfieldID fpoint_x = env->GetFieldID(point_class, "x", "F");
    jfieldID fpoint_y = env->GetFieldID(point_class, "y", "F");

    jobject point = env->GetObjectField(segmentObj, fieldOffset);
    segment->offset.x = env->GetFloatField(point, fpoint_x);
    segment->offset.y = env->GetFloatField(point, fpoint_y);

    jobject scale = env->GetObjectField(segmentObj, fieldScale);
    segment->scale.x = env->GetFloatField(scale, fpoint_x);
    segment->scale.y = env->GetFloatField(scale, fpoint_y);

    env->DeleteLocalRef(point_class);

    env->DeleteLocalRef(segment_cls);

    return true;
}

bool convert2HandInfo(JNIEnv *env, jobject handInfoObject, st_mobile_hand_t *hand_info){
    if (handInfoObject == NULL) {
        return false;
    }

    jclass hand_info_cls = env->FindClass("com/sensetime/stmobile/model/STMobileHandInfo");

    jfieldID fieldHandId = env->GetFieldID(hand_info_cls, "handId", "I");
    jfieldID fieldHandRect = env->GetFieldID(hand_info_cls, "handRect", "Lcom/sensetime/stmobile/model/STRect;");
    jfieldID fieldKeyPoints = env->GetFieldID(hand_info_cls, "keyPoints", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldKeyPointsCount = env->GetFieldID(hand_info_cls, "keyPointsCount", "I");
    jfieldID fieldHandAction = env->GetFieldID(hand_info_cls, "handAction", "J");
    jfieldID fieldHandActionScore = env->GetFieldID(hand_info_cls, "handActionScore", "F");

    jclass hand_rect_class = env->FindClass("com/sensetime/stmobile/model/STRect");
    jfieldID hrect_left = env->GetFieldID(hand_rect_class, "left", "I");
    jfieldID hrect_top = env->GetFieldID(hand_rect_class, "top", "I");
    jfieldID hrect_right = env->GetFieldID(hand_rect_class, "right", "I");
    jfieldID hrect_bottom = env->GetFieldID(hand_rect_class, "bottom", "I");

    jobject handRectObj = env->GetObjectField(handInfoObject, fieldHandRect);
    hand_info->rect.left = env->GetIntField(handRectObj, hrect_left);
    hand_info->rect.top = env->GetIntField(handRectObj, hrect_top);
    hand_info->rect.right = env->GetIntField(handRectObj, hrect_right);
    hand_info->rect.bottom = env->GetIntField(handRectObj, hrect_bottom);

    hand_info->key_points_count = env->GetIntField(handInfoObject, fieldKeyPointsCount);

    //key_points
    hand_info->key_points_count = env->GetIntField(handInfoObject, fieldKeyPointsCount);

    if(hand_info->key_points_count > 0){
        jclass point_class = env->FindClass("com/sensetime/stmobile/model/STPoint");
        jfieldID fpoint_x = env->GetFieldID(point_class, "x", "F");
        jfieldID fpoint_y = env->GetFieldID(point_class, "y", "F");

        jobjectArray key_points_array = (jobjectArray)env->GetObjectField(handInfoObject, fieldKeyPoints);
        hand_info->p_key_points = new st_pointf_t[hand_info->key_points_count];
        memset(hand_info->p_key_points, 0, sizeof(st_pointf_t)*hand_info->key_points_count);
        for (int i = 0; i < hand_info->key_points_count; ++i) {
            jobject point = env->GetObjectArrayElement(key_points_array, i);

            (hand_info->p_key_points+i)->x = env->GetFloatField(point, fpoint_x);
            (hand_info->p_key_points+i)->y = env->GetFloatField(point, fpoint_y);
            env->DeleteLocalRef(point);
        }

        env->DeleteLocalRef(key_points_array);
        env->DeleteLocalRef(point_class);
    } else{
        hand_info->p_key_points = NULL;
    }

    hand_info->id = env->GetIntField(handInfoObject, fieldHandId);
    hand_info->hand_action = env->GetLongField(handInfoObject, fieldHandAction);
    hand_info->score = env->GetFloatField(handInfoObject, fieldHandActionScore);

    env->DeleteLocalRef(hand_info_cls);
    env->DeleteLocalRef(hand_rect_class);
    env->DeleteLocalRef(handRectObj);

    return true;
}

bool convert2FaceInfo(JNIEnv *env, jobject faceInfoObject, st_mobile_face_t *face_info){
    if (faceInfoObject == NULL) {
        return false;
    }

    jclass face_info_cls = env->FindClass("com/sensetime/stmobile/model/STMobileFaceInfo");

    jfieldID fieldFace = env->GetFieldID(face_info_cls, "face106", "Lcom/sensetime/stmobile/model/STMobile106;");

    jfieldID fieldExtraFacePoints = env->GetFieldID(face_info_cls, "extraFacePoints", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldExtraFacePointsCount = env->GetFieldID(face_info_cls, "extraFacePointsCount", "I");

    jfieldID fieldTonguePoints = env->GetFieldID(face_info_cls, "tonguePoints", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldTonguePointsScore = env->GetFieldID(face_info_cls, "tonguePointsScore", "[F");
    jfieldID fieldTonguePointsCount = env->GetFieldID(face_info_cls, "tonguePointsCount", "I");

    jfieldID fieldEyeballCenter = env->GetFieldID(face_info_cls, "eyeballCenter", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldEyeballCenterPointsCount = env->GetFieldID(face_info_cls, "eyeballCenterPointsCount", "I");

    jfieldID fieldEyeballContour = env->GetFieldID(face_info_cls, "eyeballContour", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldEyeballContourPointsCount = env->GetFieldID(face_info_cls, "eyeballContourPointsCount", "I");

    jfieldID fieldLeftEyeballScore = env->GetFieldID(face_info_cls, "leftEyeballScore", "F");
    jfieldID fieldRightEyeballScore = env->GetFieldID(face_info_cls, "rightEyeballScore", "F");

    jfieldID fieldFaceAction = env->GetFieldID(face_info_cls, "faceAction", "J");
    jfieldID fieldFaceActionScore = env->GetFieldID(face_info_cls, "faceActionScore", "[F");
    jfieldID fieldFaceActionScoreCount = env->GetFieldID(face_info_cls, "faceActionScoreCount", "I");

    jfieldID fieldFaceExtraInfo = env->GetFieldID(face_info_cls, "faceExtraInfo", "Lcom/sensetime/stmobile/model/STFaceExtraInfo;");

    jfieldID fieldAvatarHelpInfo = env->GetFieldID(face_info_cls, "avatarHelpInfo", "[B");
    jfieldID fieldAvatarHelpInfoLength = env->GetFieldID(face_info_cls, "avatarHelpInfoLength", "I");

    //face106
    jobject face106Obj = env->GetObjectField(faceInfoObject, fieldFace);
    convert2mobile_106(env, face106Obj, face_info->face106);
    env->DeleteLocalRef(face106Obj);

    jclass point_class = env->FindClass("com/sensetime/stmobile/model/STPoint");
    jfieldID fpoint_x = env->GetFieldID(point_class, "x", "F");
    jfieldID fpoint_y = env->GetFieldID(point_class, "y", "F");

    //extra_face_points
    face_info->extra_face_points_count = env->GetIntField(faceInfoObject, fieldExtraFacePointsCount);

    if(face_info->extra_face_points_count > 0){
        jobjectArray extra_face_points_array = (jobjectArray)env->GetObjectField(faceInfoObject, fieldExtraFacePoints);
        face_info->p_extra_face_points = new st_pointf_t[face_info->extra_face_points_count];
        memset(face_info->p_extra_face_points, 0, sizeof(st_pointf_t)*face_info->extra_face_points_count);
        for (int i = 0; i < face_info->extra_face_points_count; ++i) {
            jobject point = env->GetObjectArrayElement(extra_face_points_array, i);

            face_info->p_extra_face_points[i].x = env->GetFloatField(point, fpoint_x);
            face_info->p_extra_face_points[i].y = env->GetFloatField(point, fpoint_y);
            env->DeleteLocalRef(point);
        }

        env->DeleteLocalRef(extra_face_points_array);
    }else{
        face_info->p_extra_face_points = NULL;
    }

    face_info->left_eyeball_score = env->GetFloatField(faceInfoObject, fieldLeftEyeballScore);
    face_info->right_eyeball_score = env->GetFloatField(faceInfoObject, fieldRightEyeballScore);

    //TonguePoints
    face_info->tongue_points_count = env->GetIntField(faceInfoObject, fieldTonguePointsCount);

    if(face_info->tongue_points_count > 0){
        jfloatArray score_array= (jfloatArray)env->GetObjectField(faceInfoObject, fieldTonguePointsScore);
        float* scores = env->GetFloatArrayElements(score_array, 0);

        face_info->p_tongue_points_score = new float[face_info->tongue_points_count];
        memset(face_info->p_tongue_points_score, 0, sizeof(float)*face_info->tongue_points_count);
        memcpy(face_info->p_tongue_points_score, scores, sizeof(float)*face_info->tongue_points_count);

        env->ReleaseFloatArrayElements(score_array, scores, JNI_FALSE);
        env->DeleteLocalRef(score_array);

        jclass point_class = env->FindClass("com/sensetime/stmobile/model/STPoint");
        jfieldID fpoint_x = env->GetFieldID(point_class, "x", "F");
        jfieldID fpoint_y = env->GetFieldID(point_class, "y", "F");

        jobjectArray key_points_array = (jobjectArray)env->GetObjectField(faceInfoObject, fieldTonguePoints);
        face_info->p_tongue_points = new st_pointf_t[face_info->tongue_points_count];
        memset(face_info->p_tongue_points, 0, sizeof(st_pointf_t)*face_info->tongue_points_count);
        for (int i = 0; i < face_info->tongue_points_count; ++i) {
            jobject point = env->GetObjectArrayElement(key_points_array, i);
            (face_info->p_tongue_points+i)->x = env->GetFloatField(point, fpoint_x);
            (face_info->p_tongue_points+i)->y = env->GetFloatField(point, fpoint_y);
            env->DeleteLocalRef(point);
        }

        env->DeleteLocalRef(key_points_array);
        env->DeleteLocalRef(point_class);
    } else{
        face_info->p_tongue_points = NULL;
        face_info->p_tongue_points_score = NULL;
    }


    //eyeball_center
    face_info->eyeball_center_points_count = env->GetIntField(faceInfoObject, fieldEyeballCenterPointsCount);

    if(face_info->eyeball_center_points_count > 0){
        jobjectArray eyeball_center_array = (jobjectArray)env->GetObjectField(faceInfoObject, fieldEyeballCenter);

        face_info->p_eyeball_center = new st_pointf_t[face_info->eyeball_center_points_count];
        memset(face_info->p_eyeball_center, 0, sizeof(st_pointf_t)*face_info->eyeball_center_points_count);
        for (int i = 0; i < face_info->eyeball_center_points_count; ++i) {
            jobject point = env->GetObjectArrayElement(eyeball_center_array, i);

            face_info->p_eyeball_center[i].x = env->GetFloatField(point, fpoint_x);
            face_info->p_eyeball_center[i].y = env->GetFloatField(point, fpoint_y);
            env->DeleteLocalRef(point);
        }

        env->DeleteLocalRef(eyeball_center_array);
    }else{
        face_info->p_eyeball_center = NULL;
    }

    //eyeball_contour
    face_info->eyeball_contour_points_count = env->GetIntField(faceInfoObject, fieldEyeballContourPointsCount);

    if(face_info->eyeball_contour_points_count > 0){
        jobjectArray eyeball_contour_array = (jobjectArray)env->GetObjectField(faceInfoObject, fieldEyeballContour);

        face_info->p_eyeball_contour = new st_pointf_t[face_info->eyeball_contour_points_count];
        memset(face_info->p_eyeball_contour, 0, sizeof(st_pointf_t)*face_info->eyeball_contour_points_count);
        for (int i = 0; i < face_info->eyeball_contour_points_count; ++i) {
            jobject point = env->GetObjectArrayElement(eyeball_contour_array, i);

            face_info->p_eyeball_contour[i].x = env->GetFloatField(point, fpoint_x);
            face_info->p_eyeball_contour[i].y = env->GetFloatField(point, fpoint_y);
            env->DeleteLocalRef(point);
        }

        env->DeleteLocalRef(eyeball_contour_array);
    } else{
        face_info->p_eyeball_contour = NULL;
    }

    //face Action score
    face_info->face_action = env->GetLongField(faceInfoObject, fieldFaceAction);

    face_info->face_action_score_count = env->GetIntField(faceInfoObject, fieldFaceActionScoreCount);
    if(face_info->face_action_score_count > 0){
        jfloatArray score_array= (jfloatArray)env->GetObjectField(faceInfoObject, fieldFaceActionScore);
        float* scores = env->GetFloatArrayElements(score_array, 0);

        face_info->p_face_action_score = new float[face_info->face_action_score_count];
        memset(face_info->p_face_action_score, 0, sizeof(float)*face_info->face_action_score_count);
        memcpy(face_info->p_face_action_score, scores, sizeof(float)*face_info->face_action_score_count);

        env->ReleaseFloatArrayElements(score_array, scores, JNI_FALSE);
        env->DeleteLocalRef(score_array);
    } else{
        face_info->p_face_action_score = NULL;
    }

    //avatar extra info
    jobject faceExtraInfoObj = env->GetObjectField(faceInfoObject, fieldFaceExtraInfo);
    if(faceExtraInfoObj != NULL){
        convert2FaceExtraInfo(env, faceExtraInfoObj, &face_info->face_extra_info);
    }


//    //avatar help info
    face_info->avatar_help_info_length = env->GetIntField(faceInfoObject, fieldAvatarHelpInfoLength);
    if(face_info->avatar_help_info_length > 0){
        jobject avatar_help_info = env->GetObjectField(faceInfoObject, fieldAvatarHelpInfo);
        jbyteArray *arr = reinterpret_cast<jbyteArray*>(&avatar_help_info);
        jbyte* data = env->GetByteArrayElements(*arr, NULL);

        face_info->p_avatar_help_info = new unsigned char[face_info->avatar_help_info_length];
        memset(face_info->p_avatar_help_info, 0, sizeof(unsigned char)*face_info->avatar_help_info_length);
        for (int i = 0; i < face_info->avatar_help_info_length; ++i) {
            face_info->p_avatar_help_info[i] = data[i];
        }

        env->ReleaseByteArrayElements(*arr, data, JNI_FALSE);
        env->DeleteLocalRef(avatar_help_info);
    } else{
        face_info->p_avatar_help_info = NULL;
    }

    env->DeleteLocalRef(point_class);
    env->DeleteLocalRef(face_info_cls);

    return true;
}

bool convert2BodyInfo(JNIEnv *env, jobject bodyInfoObject, st_mobile_body_t *body_info){
    if (bodyInfoObject == NULL) {
        return false;
    }

    jclass body_info_cls = env->FindClass("com/sensetime/stmobile/model/STMobileBodyInfo");

    jfieldID fieldBodyId = env->GetFieldID(body_info_cls, "id", "I");
    jfieldID fieldKeyPoints = env->GetFieldID(body_info_cls, "keyPoints", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldKeyPointsScore = env->GetFieldID(body_info_cls, "keyPointsScore", "[F");
    jfieldID fieldKeyPointsCount = env->GetFieldID(body_info_cls, "keyPointsCount", "I");
    jfieldID fieldContourPoints = env->GetFieldID(body_info_cls, "contourPoints", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldContourPointsScore = env->GetFieldID(body_info_cls, "contourPointsScore", "[F");
    jfieldID fieldContourPointsCount = env->GetFieldID(body_info_cls, "contourPointsCount", "I");
    jfieldID fieldBodyAction = env->GetFieldID(body_info_cls, "bodyAction", "J");
    jfieldID fieldBodyActionScore = env->GetFieldID(body_info_cls, "bodyActionScore", "F");

    //key_points
    body_info->key_points_count = env->GetIntField(bodyInfoObject, fieldKeyPointsCount);

    if(body_info->key_points_count > 0){
        jfloatArray score_array= (jfloatArray)env->GetObjectField(bodyInfoObject, fieldKeyPointsScore);
        float* scores = env->GetFloatArrayElements(score_array, 0);

        body_info->p_key_points_score = new float[body_info->key_points_count];
        memset(body_info->p_key_points_score, 0, sizeof(float)*body_info->key_points_count);
        memcpy(body_info->p_key_points_score, scores, sizeof(float)*body_info->key_points_count);

        env->ReleaseFloatArrayElements(score_array, scores, JNI_FALSE);
        env->DeleteLocalRef(score_array);

        jclass point_class = env->FindClass("com/sensetime/stmobile/model/STPoint");
        jfieldID fpoint_x = env->GetFieldID(point_class, "x", "F");
        jfieldID fpoint_y = env->GetFieldID(point_class, "y", "F");

        jobjectArray key_points_array = (jobjectArray)env->GetObjectField(bodyInfoObject, fieldKeyPoints);
        body_info->p_key_points = new st_pointf_t[body_info->key_points_count];
        memset(body_info->p_key_points, 0, sizeof(st_pointf_t)*body_info->key_points_count);
        for (int i = 0; i < body_info->key_points_count; ++i) {
            jobject point = env->GetObjectArrayElement(key_points_array, i);
            (body_info->p_key_points+i)->x = env->GetFloatField(point, fpoint_x);
            (body_info->p_key_points+i)->y = env->GetFloatField(point, fpoint_y);
            env->DeleteLocalRef(point);
        }

        env->DeleteLocalRef(key_points_array);
        env->DeleteLocalRef(point_class);
    } else{
        body_info->p_key_points = NULL;
        body_info->p_key_points_score = NULL;
    }

    //contour_points
    body_info->contour_points_count = env->GetIntField(bodyInfoObject, fieldContourPointsCount);

    if(body_info->contour_points_count > 0){
        jfloatArray score_array= (jfloatArray)env->GetObjectField(bodyInfoObject, fieldContourPointsScore);
        float* scores = env->GetFloatArrayElements(score_array, 0);

        body_info->p_contour_points_score = new float[body_info->contour_points_count];
        memset(body_info->p_contour_points_score, 0, sizeof(float)*body_info->contour_points_count);
        memcpy(body_info->p_contour_points_score, scores, sizeof(float)*body_info->contour_points_count);

        env->ReleaseFloatArrayElements(score_array, scores, JNI_FALSE);
        env->DeleteLocalRef(score_array);

        jclass point_class = env->FindClass("com/sensetime/stmobile/model/STPoint");
        jfieldID fpoint_x = env->GetFieldID(point_class, "x", "F");
        jfieldID fpoint_y = env->GetFieldID(point_class, "y", "F");

        jobjectArray contour_points_array = (jobjectArray)env->GetObjectField(bodyInfoObject, fieldContourPoints);
        body_info->p_contour_points = new st_pointf_t[body_info->contour_points_count];
        memset(body_info->p_contour_points, 0, sizeof(st_pointf_t)*body_info->contour_points_count);
        for (int i = 0; i < body_info->contour_points_count; ++i) {
            jobject point = env->GetObjectArrayElement(contour_points_array, i);
            (body_info->p_contour_points+i)->x = env->GetFloatField(point, fpoint_x);
            (body_info->p_contour_points+i)->y = env->GetFloatField(point, fpoint_y);
            env->DeleteLocalRef(point);
        }

        env->DeleteLocalRef(contour_points_array);
        env->DeleteLocalRef(point_class);
    } else{
        body_info->p_contour_points = NULL;
        body_info->p_contour_points_score = NULL;
    }

    body_info->id = env->GetIntField(bodyInfoObject, fieldBodyId);
    body_info->body_action = env->GetLongField(bodyInfoObject, fieldBodyAction);
    body_info->body_action_score = env->GetFloatField(bodyInfoObject, fieldBodyActionScore);

    env->DeleteLocalRef(body_info_cls);

    return true;
}

bool convert2HumanAction(JNIEnv *env, jobject humanActionObject, st_mobile_human_action_t *human_action){
    if (humanActionObject == NULL) {
        return false;
    }

    jclass human_action_cls = env->FindClass("com/sensetime/stmobile/model/STHumanAction");

    jfieldID fieldFaces = env->GetFieldID(human_action_cls, "faces", "[Lcom/sensetime/stmobile/model/STMobileFaceInfo;");
    jfieldID fieldFaceCount = env->GetFieldID(human_action_cls, "faceCount", "I");

    jfieldID fieldHands = env->GetFieldID(human_action_cls, "hands", "[Lcom/sensetime/stmobile/model/STMobileHandInfo;");
    jfieldID fieldHandCount = env->GetFieldID(human_action_cls, "handCount", "I");

    jfieldID fieldBodys = env->GetFieldID(human_action_cls, "bodys", "[Lcom/sensetime/stmobile/model/STMobileBodyInfo;");
    jfieldID fieldBodyCount = env->GetFieldID(human_action_cls, "bodyCount", "I");

    jfieldID fieldImage = env->GetFieldID(human_action_cls, "image", "Lcom/sensetime/stmobile/model/STSegment;");

    jfieldID fieldHair = env->GetFieldID(human_action_cls, "hair", "Lcom/sensetime/stmobile/model/STSegment;");
    jfieldID fieldHead = env->GetFieldID(human_action_cls, "head", "Lcom/sensetime/stmobile/model/STSegment;");
    jfieldID fieldSkin = env->GetFieldID(human_action_cls, "skin", "Lcom/sensetime/stmobile/model/STSegment;");
    jfieldID fieldMouthParse = env->GetFieldID(human_action_cls, "mouthParses", "[Lcom/sensetime/stmobile/model/STSegment;");
    jfieldID fieldMouthParseCount = env->GetFieldID(human_action_cls, "mouthParseCount", "I");

    jfieldID fieldMultiSegment = env->GetFieldID(human_action_cls, "multiSegment", "Lcom/sensetime/stmobile/model/STSegment;");

    //faces
    human_action->face_count = env->GetIntField(humanActionObject, fieldFaceCount);

    if(human_action->face_count > 0){
        jobjectArray faces_obj_array = (jobjectArray)env->GetObjectField(humanActionObject, fieldFaces);

        human_action->p_faces = new st_mobile_face_t[human_action->face_count];
        memset(human_action->p_faces, 0, sizeof(st_mobile_face_t)*human_action->face_count);
        for(int i = 0; i < human_action->face_count; i++){
            jobject facesObj = env->GetObjectArrayElement(faces_obj_array, i);
            convert2FaceInfo(env, facesObj, human_action->p_faces+i);

            env->DeleteLocalRef(facesObj);
        }
        env->DeleteLocalRef(faces_obj_array);
    } else {
        human_action->p_faces = NULL;
    }

    //hands
    human_action->hand_count = env->GetIntField(humanActionObject, fieldHandCount);

    if(human_action->hand_count > 0){
        jobjectArray hands_obj_array = (jobjectArray)env->GetObjectField(humanActionObject, fieldHands);

        human_action->p_hands = new st_mobile_hand_t[human_action->hand_count];
        memset(human_action->p_hands, 0, sizeof(st_mobile_hand_t)*human_action->hand_count);
        for(int i = 0; i < human_action->hand_count; i++){
            jobject handsObj = env->GetObjectArrayElement(hands_obj_array, i);
            convert2HandInfo(env, handsObj, human_action->p_hands+i);

            env->DeleteLocalRef(handsObj);
        }

        env->DeleteLocalRef(hands_obj_array);
    } else{
        human_action->p_hands = NULL;
    }

    //bodys
    human_action->body_count = env->GetIntField(humanActionObject, fieldBodyCount);

    if(human_action->body_count > 0){
        jobjectArray bodys_obj_array = (jobjectArray)env->GetObjectField(humanActionObject, fieldBodys);

        human_action->p_bodys = new st_mobile_body_t[human_action->body_count];
        memset(human_action->p_bodys, 0, sizeof(st_mobile_body_t)*human_action->body_count);
        for(int i = 0; i < human_action->body_count; i++){
            jobject bodysObj = env->GetObjectArrayElement(bodys_obj_array, i);
            convert2BodyInfo(env, bodysObj, human_action->p_bodys+i);

            env->DeleteLocalRef(bodysObj);
        }

        env->DeleteLocalRef(bodys_obj_array);
    } else{
        human_action->p_bodys = NULL;
    }

    //image
    jobject imageObj = env->GetObjectField(humanActionObject, fieldImage);

    if(imageObj != NULL){
        human_action->p_figure = new st_mobile_segment_t;
        memset(human_action->p_figure, 0, sizeof(st_mobile_segment_t));

        convert2Segment(env, imageObj, human_action->p_figure);
    } else{
        human_action->p_figure = NULL;
    }

    env->DeleteLocalRef(imageObj);

    //hair
    jobject hairObj = env->GetObjectField(humanActionObject, fieldHair);

    if(hairObj != NULL){
        human_action->p_hair = new st_mobile_segment_t;
        memset(human_action->p_hair, 0, sizeof(st_mobile_segment_t));

        convert2Segment(env, hairObj, human_action->p_hair);
    } else{
        human_action->p_hair = NULL;
    }

    env->DeleteLocalRef(hairObj);

    //skin
    jobject skinObj = env->GetObjectField(humanActionObject, fieldSkin);

    if(hairObj != NULL){
        human_action->p_skin = new st_mobile_segment_t;
        memset(human_action->p_skin, 0, sizeof(st_mobile_segment_t));

        convert2Segment(env, skinObj, human_action->p_skin);
    } else{
        human_action->p_skin = NULL;
    }

    env->DeleteLocalRef(skinObj);

    //head
    jobject headObj = env->GetObjectField(humanActionObject, fieldHead);

    if(headObj != NULL){
        human_action->p_head = new st_mobile_segment_t;
        memset(human_action->p_head, 0, sizeof(st_mobile_segment_t));

        convert2Segment(env, headObj, human_action->p_head);
    } else{
        human_action->p_head = NULL;
    }

    //mouth parse
    human_action->mouth_parse_count = env->GetIntField(humanActionObject, fieldMouthParseCount);
    if(human_action->mouth_parse_count > 0){
        jobjectArray mouth_parse_obj_array = (jobjectArray)env->GetObjectField(humanActionObject, fieldMouthParse);

        human_action->p_mouth_parse = new st_mobile_segment_t[human_action->mouth_parse_count];
        memset(human_action->p_mouth_parse, 0, sizeof(st_mobile_segment_t)*human_action->mouth_parse_count);
        for(int i = 0; i < human_action->mouth_parse_count; i++){
            jobject mouth_parseObj = env->GetObjectArrayElement(mouth_parse_obj_array, i);
            convert2Segment(env, mouth_parseObj, human_action->p_mouth_parse+i);

            env->DeleteLocalRef(mouth_parseObj);
        }

        env->DeleteLocalRef(mouth_parse_obj_array);
    } else{
        human_action->p_mouth_parse = NULL;
    }

    //MultiSegment
    jobject multiSegmentObj = env->GetObjectField(humanActionObject, fieldMultiSegment);

    if(multiSegmentObj != NULL){
        human_action->p_multi = new st_mobile_segment_t;
        memset(human_action->p_multi, 0, sizeof(st_mobile_segment_t));

        convert2Segment(env, multiSegmentObj, human_action->p_multi);
    } else{
        human_action->p_multi = NULL;
    }

    env->DeleteLocalRef(multiSegmentObj);

    env->DeleteLocalRef(human_action_cls);

    return true;
}

jobject convert2FaceExtraInfo(JNIEnv *env, const st_mobile_face_extra_info &face_extra_info){
    jclass face_extra_info_cls = env->FindClass("com/sensetime/stmobile/model/STFaceExtraInfo");

    jfieldID fieldAffineMat = env->GetFieldID(face_extra_info_cls, "affineMat", "[[F");
    jfieldID fieldModelInputSize = env->GetFieldID(face_extra_info_cls, "modelInputSize", "I");

    jobject faceInfoObj = env->AllocObject(face_extra_info_cls);

    //Avatar
    jobjectArray arrayAffineMats;
    jclass floatArr = env->FindClass("[F");
    arrayAffineMats = env->NewObjectArray(3, floatArr, nullptr);
    for(int i = 0; i < 3; i++){
        jfloatArray arrayAffineMat = env->NewFloatArray(3);
        env->SetFloatArrayRegion(arrayAffineMat, 0, 3, face_extra_info.affine_mat[i]);
        env->SetObjectArrayElement(arrayAffineMats, i, arrayAffineMat);
        env->DeleteLocalRef(arrayAffineMat);
    }
    env->DeleteLocalRef(floatArr);
    env->SetObjectField(faceInfoObj, fieldAffineMat, arrayAffineMats);
    env->DeleteLocalRef(arrayAffineMats);

    env->SetIntField(faceInfoObj, fieldModelInputSize, face_extra_info.model_input_size);

    env->DeleteLocalRef(face_extra_info_cls);

    return faceInfoObj;
}

bool convert2FaceExtraInfo(JNIEnv *env, jobject faceExtraInfoObject, st_mobile_face_extra_info *face_extra_info){
    if (faceExtraInfoObject == NULL) {
        return false;
    }

    jclass face_info_cls = env->FindClass("com/sensetime/stmobile/model/STFaceExtraInfo");
    jfieldID fieldAffineMat = env->GetFieldID(face_info_cls, "affineMat", "[[F");
    jfieldID fieldModelInputSize = env->GetFieldID(face_info_cls, "modelInputSize", "I");

    //Avatar
    jobjectArray objAvatarAffineMat = (jobjectArray)env->GetObjectField(faceExtraInfoObject, fieldAffineMat);
    for(int i = 0; i < 3; i++){
        jfloatArray arrayAvatarAffineMat = (jfloatArray)env->GetObjectArrayElement(objAvatarAffineMat, i);
        jfloat* jfarr = env->GetFloatArrayElements(arrayAvatarAffineMat, JNI_FALSE);
        for(int j = 0; j < 3; j++){
            face_extra_info->affine_mat[i][j] = jfarr[j];
        }
        env->ReleaseFloatArrayElements(arrayAvatarAffineMat, jfarr, JNI_FALSE);
        env->DeleteLocalRef(arrayAvatarAffineMat);
    }

    face_extra_info->model_input_size = env->GetIntField(faceExtraInfoObject, fieldModelInputSize);
    env->DeleteLocalRef(face_info_cls);

    return true;
}


//bool convert2YuvImage(JNIEnv *env, jobject yuvImageObj, st_yuv_image_t *yuv_image){
//    if (yuvImageObj == NULL) {
//        return false;
//    }
//
//    jclass yuv_image_cls = env->FindClass("com/sensetime/stmobile/model/STYuvImage");
//
//    jfieldID fieldPlanes0 = env->GetFieldID(yuv_image_cls, "planes0", "[B");
//    jfieldID fieldPlanes1 = env->GetFieldID(yuv_image_cls, "planes1", "[B");
//    jfieldID fieldPlanes2 = env->GetFieldID(yuv_image_cls, "planes2", "[B");
//    jfieldID fieldStrides = env->GetFieldID(yuv_image_cls, "strides", "[I");
//    jfieldID fieldWidth = env->GetFieldID(yuv_image_cls, "width", "I");
//    jfieldID fieldHeight = env->GetFieldID(yuv_image_cls, "height", "I");
//    jfieldID fieldFormat = env->GetFieldID(yuv_image_cls, "format", "I");
//
//    yuv_image->width = env->GetIntField(yuvImageObj, fieldWidth);
//    yuv_image->height = env->GetIntField(yuvImageObj, fieldHeight);
//    yuv_image->format = (st_pixel_format)env->GetIntField(yuvImageObj, fieldFormat);
//
//    jintArray strides_array = (jintArray)env->GetObjectField(yuvImageObj, fieldStrides);
//    int* values = env->GetIntArrayElements(strides_array, 0);
//    for(int i = 0; i < 3; i++){
//        yuv_image->strides[i] = values[i];
//    }
//
//    jobject imagePlane0 = env->GetObjectField(yuvImageObj, fieldPlanes0);
//    if(imagePlane0 != NULL){
//        jbyteArray *arrayPlane0 = reinterpret_cast<jbyteArray*>(&imagePlane0);
//        jbyte* dataPlane0 = env->GetByteArrayElements(*arrayPlane0, NULL);
//        yuv_image->planes[0] = (uint8_t*)dataPlane0;
//    } else{
//        yuv_image->planes[0] = NULL;
//    }
//
//    jobject imagePlane1 = env->GetObjectField(yuvImageObj, fieldPlanes1);
//    if(imagePlane1 != NULL){
//        jbyteArray *arrayPlane1 = reinterpret_cast<jbyteArray*>(&imagePlane1);
//        jbyte* dataPlane1 = env->GetByteArrayElements(*arrayPlane1, NULL);
//        yuv_image->planes[1] = (uint8_t*)dataPlane1;
//    } else{
//        yuv_image->planes[1] = NULL;
//    }
//
//    jobject imagePlane2 = env->GetObjectField(yuvImageObj, fieldPlanes2);
//    if(imagePlane2 != NULL){
//        jbyteArray *arrayPlane2 = reinterpret_cast<jbyteArray*>(&imagePlane2);
//        jbyte* dataPlane2 = env->GetByteArrayElements(*arrayPlane2, NULL);
//        yuv_image->planes[2] = (uint8_t*)dataPlane2;
//    } else{
//        yuv_image->planes[2] = NULL;
//    }
//
//    return true;
//}

void releaseEffectRenderInputParams(st_effect_render_in_param_t *param){
    if(!param) return;
    releaseHumanAction(param->p_human);
    releaseAnimal(param->p_animal_face, param->animal_face_count);
    safe_delete(param->p_custom_param);
    safe_delete(param->p_image);
    safe_delete(param->p_tex);
}

void releaseEffectRenderOutputParams(st_effect_render_out_param_t *param){
    if(!param) return;
    releaseHumanAction(param->p_human);
    safe_delete(param->p_image);
    safe_delete(param->p_tex);
}

void releaseHumanAction(st_mobile_human_action_t * p_human) {
    if(!p_human) return;
    DeleteObject(p_human->p_faces, p_human->face_count);
    DeleteObject(p_human->p_hands, p_human->hand_count);
    DeleteObject(p_human->p_bodys, p_human->body_count);

    releaseSegment(p_human->p_figure, 1);
    releaseSegment(p_human->p_hair, 1);
    releaseSegment(p_human->p_multi, 1);
    releaseSegment(p_human->p_head, 1);
    releaseSegment(p_human->p_skin, 1);
    releaseSegment(p_human->p_mouth_parse, p_human->mouth_parse_count);

    memset(p_human, 0x0, sizeof(st_mobile_human_action_t));
}

void releaseSegment(st_mobile_segment_t*& p_dst, int count){
    if (nullptr != p_dst) {
        for (int i = 0; i < count; ++i)
        {
            DeleteImage(p_dst[i].p_segment);
        }
        delete[] p_dst;
        p_dst = nullptr;
    }
}

void DeleteImage(st_image_t*& image) {
    if (image) {
        if(image->time_stamp == 1.0f){
            safe_delete_array(image->data);
        }
        safe_delete(image);
    }
}

void DeleteObject(st_mobile_face_t*& p_faces, int& face_count) {
    for (int i = 0; i < face_count; i++) {
        safe_delete_array(p_faces[i].p_extra_face_points);
        safe_delete_array(p_faces[i].p_tongue_points);
        safe_delete_array(p_faces[i].p_tongue_points_score);
        safe_delete_array(p_faces[i].p_eyeball_center);
        safe_delete_array(p_faces[i].p_eyeball_contour);
        safe_delete_array(p_faces[i].p_gaze_direction);
        safe_delete_array(p_faces[i].p_gaze_score);
        safe_delete_array(p_faces[i].p_avatar_help_info);
        if (p_faces[i].p_face_ear)
        safe_delete_array(p_faces[i].p_face_ear->p_ear_points);
        safe_delete(p_faces[i].p_face_ear);
        if (p_faces[i].p_face_forehead)
        safe_delete_array(p_faces[i].p_face_forehead->p_forehead_points);
        safe_delete(p_faces[i].p_face_forehead);
        if (p_faces[i].p_face_mesh)
        safe_delete_array(p_faces[i].p_face_mesh->p_face_mesh_points);
        safe_delete(p_faces[i].p_face_mesh);
    }
    safe_delete_array(p_faces);
    face_count = 0;
}
void DeleteObject(st_mobile_hand_t*& p_hands, int& hand_count) {
    for (int i = 0; i < hand_count; i++) {
        safe_delete_array(p_hands[i].p_key_points);
        safe_delete_array(p_hands[i].p_skeleton_keypoints);
        safe_delete_array(p_hands[i].p_skeleton_3d_keypoints);
        safe_delete_array(p_hands[i].p_dynamic_gesture_keypoints);
    }
    safe_delete_array(p_hands);
    hand_count = 0;
}
void DeleteObject(st_mobile_body_t*& p_bodys, int& body_count) {
    for (int i = 0; i < body_count; i++) {
        safe_delete_array(p_bodys[i].p_key_points);
        safe_delete_array(p_bodys[i].p_key_points_score);
        safe_delete_array(p_bodys[i].p_contour_points);
        safe_delete_array(p_bodys[i].p_contour_points_score);
        safe_delete_array(p_bodys[i].p_key_points_3d);
    }
    safe_delete_array(p_bodys);
    body_count = 0;
}

bool convert2Condition(JNIEnv *env, jobject conditionObject, st_condition &condition){
    if(conditionObject == NULL){
        return false;
    }
    jclass st_condition_class = env->FindClass("com/sensetime/stmobile/model/STCondition");
    jfieldID fpreStateModuleId = env->GetFieldID(st_condition_class, "preStateModuleId", "I");
    jfieldID fpreState = env->GetFieldID(st_condition_class, "preState", "I");
    jfieldID ftriggers = env->GetFieldID(st_condition_class, "triggers", "[Lcom/sensetime/stmobile/model/STTriggerEvent;");
    jfieldID ftriggerCount = env->GetFieldID(st_condition_class, "triggerCount", "I");

    condition.pre_state_module_id = env->GetIntField(conditionObject, fpreStateModuleId);
    condition.pre_state = (st_animation_state_type)env->GetIntField(conditionObject, fpreState);
    condition.trigger_count = env->GetIntField(conditionObject, ftriggerCount);

    if(condition.trigger_count > 0){
        jobjectArray trigger_obj_array = (jobjectArray)env->GetObjectField(conditionObject, ftriggers);

        condition.triggers = new st_trigger_event[condition.trigger_count];
        memset(condition.triggers, 0, sizeof(st_trigger_event)*condition.trigger_count);
        for(int i = 0; i < condition.trigger_count; i++){
            jobject triggersObj = env->GetObjectArrayElement(trigger_obj_array, i);
            convert2TriggerEvent(env, triggersObj, condition.triggers[i]);

            env->DeleteLocalRef(triggersObj);
        }

        env->DeleteLocalRef(trigger_obj_array);
    } else{
        condition.triggers = NULL;
    }

    env->DeleteLocalRef(st_condition_class);

    return true;
}

bool convert2TransParam(JNIEnv *env, jobject paramObject, st_trans_param &param){
    if(paramObject == NULL){
        return false;
    }
    jclass st_param_class = env->FindClass("com/sensetime/stmobile/model/STTransParam");
    jfieldID fadeFrame = env->GetFieldID(st_param_class, "fadeFrame", "I");
    jfieldID fdelayFrame = env->GetFieldID(st_param_class, "delayFrame", "I");
    jfieldID flastingFrame = env->GetFieldID(st_param_class, "lastingFrame", "I");
    jfieldID fplayloop = env->GetFieldID(st_param_class, "playloop", "I");

    param.fade_frame = env->GetIntField(paramObject, fadeFrame);
    param.delay_frame = env->GetIntField(paramObject, fdelayFrame);
    param.lasting_frame = env->GetIntField(paramObject, flastingFrame);
    param.play_loop = env->GetIntField(paramObject, fplayloop);

    env->DeleteLocalRef(st_param_class);

    return true;
}

bool convert2TriggerEvent(JNIEnv *env, jobject triggerEventObject, st_trigger_event &trigger_event){
    if(triggerEventObject == NULL){
        return false;
    }

    jclass st_trigger_event_class = env->FindClass("com/sensetime/stmobile/model/STTriggerEvent");
    jfieldID ftriggerType = env->GetFieldID(st_trigger_event_class, "triggerType", "I");
    jfieldID ftrigger = env->GetFieldID(st_trigger_event_class, "trigger", "J");
    jfieldID fmoduleId = env->GetFieldID(st_trigger_event_class, "moduleId", "I");
    jfieldID fisAppear = env->GetFieldID(st_trigger_event_class, "isAppear", "Z");

    trigger_event.triggerType = (st_trigger_event_type) env->GetIntField(triggerEventObject, ftriggerType);
    trigger_event.trigger = env->GetLongField(triggerEventObject, ftrigger);
    trigger_event.module_id = env->GetIntField(triggerEventObject, fmoduleId);
    trigger_event.is_appear = env->GetBooleanField(triggerEventObject, fisAppear);

    env->DeleteLocalRef(st_trigger_event_class);
}

bool convert2StickerInputParams(JNIEnv *env, jobject eventObject, st_mobile_input_params &input_params){
    if(eventObject == NULL){
        return false;
    }
    jclass st_input_event_class = env->FindClass("com/sensetime/stmobile/model/STStickerInputParams");
    jfieldID fieldQuaternion = env->GetFieldID(st_input_event_class, "cameraQuaternion", "[F");
    jfieldID fieldLength = env->GetFieldID(st_input_event_class, "quaternionLength", "I");
    jfieldID fieldCameraId = env->GetFieldID(st_input_event_class, "isFrontCamera", "Z");

    jfieldID fieldEvent = env->GetFieldID(st_input_event_class, "customEvent", "I");

    int length = env->GetIntField(eventObject, fieldLength);
    if(length >= 4){
        jfloatArray quaternion_array= (jfloatArray)env->GetObjectField(eventObject, fieldQuaternion);
        float* values = env->GetFloatArrayElements(quaternion_array, 0);

        for(int i = 0; i< 4; i++){
            input_params.camera_quaternion[i] = values[i];
        }
        env->ReleaseFloatArrayElements(quaternion_array, values, 0);
        env->DeleteLocalRef(quaternion_array);
    }

    bool isFrontCamera = env->GetBooleanField(eventObject, fieldCameraId);
    input_params.is_front_camera = isFrontCamera;

    input_params.custom_event = env->GetIntField(eventObject, fieldEvent);

    env->DeleteLocalRef(st_input_event_class);

    return true;
}

bool convert2AnimalFace(JNIEnv *env, jobject animalFaceObject, st_mobile_animal_face_t *animal_face){
    if (animalFaceObject == NULL) {
        return false;
    }

    jclass animal_face_cls = env->FindClass("com/sensetime/stmobile/model/STAnimalFace");

    jfieldID fieldId = env->GetFieldID(animal_face_cls, "id", "I");
    jfieldID fieldRect = env->GetFieldID(animal_face_cls, "rect", "Lcom/sensetime/stmobile/model/STRect;");
    jfieldID fieldScore = env->GetFieldID(animal_face_cls, "score", "F");
    jfieldID fieldKeyPoints = env->GetFieldID(animal_face_cls, "p_key_points", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldKeyPointsCount = env->GetFieldID(animal_face_cls, "key_points_count", "I");
    jfieldID fieldYaw = env->GetFieldID(animal_face_cls, "yaw", "F");
    jfieldID fieldPitch = env->GetFieldID(animal_face_cls, "pitch", "F");
    jfieldID fieldRoll = env->GetFieldID(animal_face_cls, "roll", "F");

    animal_face->id = env->GetIntField(animalFaceObject, fieldId);
    jobject animalFaceRect = env->GetObjectField(animalFaceObject, fieldRect);

    if(!convert2st_rect_t(env, animalFaceRect, animal_face->rect)){
        return false;
    }
    animal_face->score = env->GetFloatField(animalFaceObject, fieldScore);
    animal_face->key_points_count = env->GetIntField(animalFaceObject, fieldKeyPointsCount);
    animal_face->yaw = env->GetFloatField(animalFaceObject, fieldYaw);
    animal_face->pitch = env->GetFloatField(animalFaceObject, fieldPitch);
    animal_face->roll = env->GetFloatField(animalFaceObject, fieldRoll);

    if(animal_face->key_points_count > 0){
        jclass point_class = env->FindClass("com/sensetime/stmobile/model/STPoint");
        jfieldID fpoint_x = env->GetFieldID(point_class, "x", "F");
        jfieldID fpoint_y = env->GetFieldID(point_class, "y", "F");

        jobjectArray key_points_array = (jobjectArray)env->GetObjectField(animalFaceObject, fieldKeyPoints);
        animal_face->p_key_points = new st_pointf_t[animal_face->key_points_count];
        memset(animal_face->p_key_points, 0, sizeof(st_pointf_t)*animal_face->key_points_count);
        for (int i = 0; i < animal_face->key_points_count; ++i) {
            jobject point = env->GetObjectArrayElement(key_points_array, i);
            (animal_face->p_key_points+i)->x = env->GetFloatField(point, fpoint_x);
            (animal_face->p_key_points+i)->y = env->GetFloatField(point, fpoint_y);
            env->DeleteLocalRef(point);
        }
        env->DeleteLocalRef(key_points_array);
        env->DeleteLocalRef(point_class);
    } else{
        animal_face->p_key_points = NULL;
    }
    env->DeleteLocalRef(animal_face_cls);

    return true;
}

void releaseAnimal(st_mobile_animal_face_t *animal_face, int faceCount){
    if(animal_face == NULL){
        return;
    }
    for(int i = 0; i < faceCount; i++){
        safe_delete_array(animal_face[i].p_key_points);
    }
}



jobject convert2EffectTexture(JNIEnv *env, const st_effect_texture_t *effect_texture){
    jclass effect_texture_cls = env->FindClass("com/sensetime/stmobile/model/STEffectTexture");

    jfieldID fieldId = env->GetFieldID(effect_texture_cls, "id", "I");
    jfieldID fieldWidth = env->GetFieldID(effect_texture_cls, "width", "I");
    jfieldID fieldHeight = env->GetFieldID(effect_texture_cls, "height", "I");
    jfieldID fieldFormat = env->GetFieldID(effect_texture_cls, "format", "I");

    jobject effectTextureObj = env->AllocObject(effect_texture_cls);

    env->SetIntField(effectTextureObj, fieldId, effect_texture->id);
    env->SetIntField(effectTextureObj, fieldWidth, effect_texture->width);
    env->SetIntField(effectTextureObj, fieldHeight, effect_texture->height);
    env->SetIntField(effectTextureObj, fieldFormat, (int)effect_texture->format);

    env->DeleteLocalRef(effect_texture_cls);

    return effectTextureObj;
}

bool convert2st_effect_texture(JNIEnv *env, jobject effectTextureObject, st_effect_texture_t *effect_texture){
    if(effectTextureObject == NULL){
        return JNI_FALSE;
    }

    jclass effect_texture_cls = env->FindClass("com/sensetime/stmobile/model/STEffectTexture");

    jfieldID fieldId = env->GetFieldID(effect_texture_cls, "id", "I");
    jfieldID fieldWidth = env->GetFieldID(effect_texture_cls, "width", "I");
    jfieldID fieldHeight = env->GetFieldID(effect_texture_cls, "height", "I");
    jfieldID fieldFormat = env->GetFieldID(effect_texture_cls, "format", "I");

    effect_texture->id = env->GetIntField(effectTextureObject, fieldId);
    effect_texture->width = env->GetIntField(effectTextureObject, fieldWidth);
    effect_texture->height = env->GetIntField(effectTextureObject, fieldHeight);
    effect_texture->format = (st_pixel_format)env->GetIntField(effectTextureObject, fieldFormat);

    env->DeleteLocalRef(effect_texture_cls);

    return JNI_TRUE;
}

jobject convert2Quaternion(JNIEnv *env, const st_quaternion_t &quaternion){
    jclass STQuaternionClass = env->FindClass("com/sensetime/stmobile/model/STQuaternion");

    if (STQuaternionClass == NULL) {
        return NULL;
    }

    jobject quaternionObject = env->AllocObject(STQuaternionClass);

    jfieldID quaternion_x = env->GetFieldID(STQuaternionClass, "x", "F");
    jfieldID quaternion_y = env->GetFieldID(STQuaternionClass, "y", "F");
    jfieldID quaternion_z = env->GetFieldID(STQuaternionClass, "z", "F");
    jfieldID quaternion_w = env->GetFieldID(STQuaternionClass, "w", "F");

    env->SetFloatField(quaternionObject, quaternion_x, quaternion.x);
    env->SetFloatField(quaternionObject, quaternion_y, quaternion.y);
    env->SetFloatField(quaternionObject, quaternion_z, quaternion.z);
    env->SetFloatField(quaternionObject, quaternion_w, quaternion.w);

    if(STQuaternionClass != NULL){
        env->DeleteLocalRef(STQuaternionClass);
    }

    return quaternionObject;
}

bool convert2st_quaternion(JNIEnv *env, jobject quaternionObject, st_quaternion_t *quaternion){
    if(quaternionObject == NULL){
        return false;
    }

    jclass STQuaternionClass = env->GetObjectClass(quaternionObject);

    if (STQuaternionClass == NULL) {
        return false;
    }

    jfieldID quaternion_x = env->GetFieldID(STQuaternionClass, "x", "F");
    jfieldID quaternion_y = env->GetFieldID(STQuaternionClass, "y", "F");
    jfieldID quaternion_z = env->GetFieldID(STQuaternionClass, "z", "F");
    jfieldID quaternion_w = env->GetFieldID(STQuaternionClass, "w", "F");

    quaternion->x = env->GetFloatField(quaternionObject, quaternion_x);
    quaternion->y = env->GetFloatField(quaternionObject, quaternion_y);
    quaternion->z = env->GetFloatField(quaternionObject, quaternion_z);
    quaternion->w = env->GetFloatField(quaternionObject, quaternion_w);

    if(STQuaternionClass != NULL){
        env->DeleteLocalRef(STQuaternionClass);
    }

    return true;
}

bool convert2st_effect_custom_param(JNIEnv *env, jobject eventObject, st_effect_custom_param_t *input_params){
    if(eventObject == NULL){
        return false;
    }
    jclass st_input_event_class = env->FindClass("com/sensetime/stmobile/model/STEffectCustomParam");
    jfieldID fieldQuaternion = env->GetFieldID(st_input_event_class, "cameraQuaternion", "Lcom/sensetime/stmobile/model/STQuaternion;");
    jfieldID fieldCameraId = env->GetFieldID(st_input_event_class, "isFrontCamera", "Z");

    jfieldID fieldEvent = env->GetFieldID(st_input_event_class, "event", "I");

    input_params->front_camera = env->GetBooleanField(eventObject, fieldCameraId);
    input_params->event = env->GetIntField(eventObject, fieldEvent);

    jobject quaternionObj = env->GetObjectField(eventObject, fieldQuaternion);
    if(!convert2st_quaternion(env, quaternionObj, &input_params->camera_quat)){
        return false;
    }

    env->DeleteLocalRef(st_input_event_class);

    return true;
}

bool convert2st_effect_render_in_param(JNIEnv *env, jobject inputObject, st_effect_render_in_param_t *render_in_param){
    if(inputObject == NULL){
        return false;
    }

    jclass render_in_param_class = env->FindClass("com/sensetime/stmobile/model/STEffectRenderInParam");
    jfieldID fieldNativeHumanAction = env->GetFieldID(render_in_param_class, "nativeHumanActionResult", "J");
    jfieldID fieldHumanAction = env->GetFieldID(render_in_param_class, "humanAction", "Lcom/sensetime/stmobile/model/STHumanAction;");
    jfieldID fieldCustomParam = env->GetFieldID(render_in_param_class, "customParam", "Lcom/sensetime/stmobile/model/STEffectCustomParam;");
    jfieldID fieldNeedMirror = env->GetFieldID(render_in_param_class, "needMirror", "Z");
    jfieldID fieldRotate = env->GetFieldID(render_in_param_class, "rotate", "I");
    jfieldID fieldFrontRotate = env->GetFieldID(render_in_param_class, "frontRotate", "I");
    jfieldID fieldImage = env->GetFieldID(render_in_param_class, "image", "Lcom/sensetime/stmobile/model/STImage;");
    jfieldID fieldTexture = env->GetFieldID(render_in_param_class, "texture", "Lcom/sensetime/stmobile/model/STEffectTexture;");
    jfieldID fieldAnimalCount = env->GetFieldID(render_in_param_class, "animalFaceCount", "I");
    jfieldID fieldAnimalFaces = env->GetFieldID(render_in_param_class, "animalFaces", "[Lcom/sensetime/stmobile/model/STAnimalFace;");

    render_in_param->need_mirror = env->GetBooleanField(inputObject, fieldNeedMirror);
    render_in_param->rotate = (st_rotate_type)env->GetIntField(inputObject, fieldRotate);
    render_in_param->front_rotate = (st_rotate_type)env->GetIntField(inputObject, fieldFrontRotate);

    jobject humanActionObj = env->GetObjectField(inputObject, fieldHumanAction);
    if(humanActionObj != NULL){
        render_in_param->p_human = new st_mobile_human_action_t;
        memset(render_in_param->p_human, 0, sizeof(st_mobile_human_action_t));
        convert2HumanAction(env, humanActionObj, render_in_param->p_human);
    } else {
        jlong native_human_action_result = env->GetLongField(inputObject, fieldNativeHumanAction);

        if(native_human_action_result != 0){
            st_mobile_human_action_t *human_action = new st_mobile_human_action_t;
            memset(human_action, 0, sizeof(st_mobile_human_action_t));
            st_mobile_human_action_copy(reinterpret_cast<st_mobile_human_action_t *>(native_human_action_result), human_action);
            render_in_param->p_human = human_action;

            if(render_in_param->p_human != nullptr && render_in_param->p_human->mouth_parse_count > 0 && render_in_param->p_human->p_mouth_parse != nullptr){
                for (int i = 0; i < render_in_param->p_human->mouth_parse_count; ++i) {
                    if(render_in_param->p_human->p_mouth_parse[i].p_segment != nullptr){
                        render_in_param->p_human->p_mouth_parse[i].p_segment->time_stamp = 1.0f;
                    }
                }
            }

            if(render_in_param->p_human->p_figure != nullptr && render_in_param->p_human->p_figure->p_segment){
                render_in_param->p_human->p_figure->p_segment->time_stamp = 1.0f;
            }

            if(render_in_param->p_human->p_hair != nullptr && render_in_param->p_human->p_hair->p_segment){
                render_in_param->p_human->p_hair->p_segment->time_stamp = 1.0f;
            }

            if(render_in_param->p_human->p_multi != nullptr && render_in_param->p_human->p_figure->p_segment){
                render_in_param->p_human->p_multi->p_segment->time_stamp = 1.0f;
            }

            if(render_in_param->p_human->p_head != nullptr && render_in_param->p_human->p_figure->p_segment){
                render_in_param->p_human->p_head->p_segment->time_stamp = 1.0f;
            }

            if(render_in_param->p_human->p_skin != nullptr && render_in_param->p_human->p_figure->p_segment){
                render_in_param->p_human->p_skin->p_segment->time_stamp = 1.0f;
            }

            if(render_in_param->p_human->p_sky != nullptr && render_in_param->p_human->p_figure->p_segment){
                render_in_param->p_human->p_sky->p_segment->time_stamp = 1.0f;
            }
        } else{
            //LOGE("sensetime jni error: input human_action is null");
            render_in_param->p_human = NULL;
        }
    }
    env->DeleteLocalRef(humanActionObj);

    jobject customParamObj = env->GetObjectField(inputObject, fieldCustomParam);
    if(customParamObj != NULL){
        render_in_param->p_custom_param = new st_effect_custom_param_t ;
        memset(render_in_param->p_custom_param, 0, sizeof(st_effect_custom_param_t));
        convert2st_effect_custom_param(env, customParamObj, render_in_param->p_custom_param);
    } else{
//        LOGE("sensetime jni error: input custom_param is null");
        render_in_param->p_custom_param = NULL;
    }
    env->DeleteLocalRef(customParamObj);

    jobject imageObj = env->GetObjectField(inputObject, fieldImage);
    if(imageObj != NULL){
        render_in_param->p_image = new st_image_t ;
        memset(render_in_param->p_image, 0, sizeof(st_image_t));

        convert2Image(env, imageObj, render_in_param->p_image);
    } else{
//        LOGE("sensetime jni error: input image is null");
        render_in_param->p_image = NULL;
    }
    env->DeleteLocalRef(imageObj);

    jobject textureObj = env->GetObjectField(inputObject, fieldTexture);
    if(textureObj != NULL){
        render_in_param->p_tex = new st_effect_texture_t ;
        memset(render_in_param->p_tex, 0, sizeof(st_effect_texture_t));

        convert2st_effect_texture(env, textureObj, render_in_param->p_tex);
    } else{
        LOGE("sensetime jni error: input texture is null");
        render_in_param->p_tex = NULL;
    }
    env->DeleteLocalRef(textureObj);

    //animal faces
    render_in_param->animal_face_count = env->GetIntField(inputObject, fieldAnimalCount);

    if(render_in_param->animal_face_count > 0){
        jobjectArray faces_obj_array = (jobjectArray)env->GetObjectField(inputObject, fieldAnimalFaces);

        render_in_param->p_animal_face = new st_mobile_animal_face_t[render_in_param->animal_face_count];
        memset(render_in_param->p_animal_face, 0, sizeof(st_mobile_animal_face_t)*render_in_param->animal_face_count);
        for(int i = 0; i < render_in_param->animal_face_count; i++){
            jobject facesObj = env->GetObjectArrayElement(faces_obj_array, i);
            convert2AnimalFace(env, facesObj, render_in_param->p_animal_face+i);

            env->DeleteLocalRef(facesObj);
        }
        env->DeleteLocalRef(faces_obj_array);
    } else {
        render_in_param->p_animal_face = NULL;
    }

    env->DeleteLocalRef(render_in_param_class);

    return true;
}

bool convert2st_effect_render_out_param(JNIEnv *env, jobject outputObject, st_effect_render_out_param_t *render_out_param){
    if(outputObject == NULL){
        return false;
    }

    jclass render_out_param_class = env->FindClass("com/sensetime/stmobile/model/STEffectRenderOutParam");
    jfieldID fieldHumanAction = env->GetFieldID(render_out_param_class, "humanAction", "Lcom/sensetime/stmobile/model/STHumanAction;");
    jfieldID fieldImage = env->GetFieldID(render_out_param_class, "image", "Lcom/sensetime/stmobile/model/STImage;");
    jfieldID fieldTexture = env->GetFieldID(render_out_param_class, "texture", "Lcom/sensetime/stmobile/model/STEffectTexture;");

    jobject humanActionObj = env->GetObjectField(outputObject, fieldHumanAction);
    if(humanActionObj != NULL){
        render_out_param->p_human = new st_mobile_human_action_t;
        memset(render_out_param->p_human, 0, sizeof(st_mobile_human_action_t));
        convert2HumanAction(env, humanActionObj, render_out_param->p_human);
    } else{
        //LOGE("sensetime jni error: input human_action is null");
        render_out_param->p_human = NULL;
    }
    env->DeleteLocalRef(humanActionObj);

    jobject imageObj = env->GetObjectField(outputObject, fieldImage);
    if(imageObj != NULL){
        render_out_param->p_image = new st_image_t ;
        memset(render_out_param->p_image, 0, sizeof(st_image_t));

        convert2Image(env, imageObj, render_out_param->p_image);
    } else{
        //LOGE("sensetime jni error: input image is null");
        render_out_param->p_image = NULL;
    }
    env->DeleteLocalRef(imageObj);

    jobject textureObj = env->GetObjectField(outputObject, fieldTexture);
    if(textureObj != NULL){
        render_out_param->p_tex = new st_effect_texture_t ;
        memset(render_out_param->p_tex, 0, sizeof(st_effect_texture_t));

        convert2st_effect_texture(env, textureObj, render_out_param->p_tex);
    } else{
        LOGE("sensetime jni error: input texture is null");
        render_out_param->p_tex = NULL;
    }
    env->DeleteLocalRef(textureObj);

    env->DeleteLocalRef(render_out_param_class);

    return true;
}

jobject convert2STEffectRenderOutParam(JNIEnv *env, const st_effect_render_out_param_t *render_out_param){
    jclass STEffectRenderOutParamClass = env->FindClass("com/sensetime/stmobile/model/STEffectRenderOutParam");
    jfieldID fieldHumanAction = env->GetFieldID(STEffectRenderOutParamClass, "humanAction", "Lcom/sensetime/stmobile/model/STHumanAction;");
    jfieldID fieldImage = env->GetFieldID(STEffectRenderOutParamClass, "image", "Lcom/sensetime/stmobile/model/STImage;");
    jfieldID fieldTexture = env->GetFieldID(STEffectRenderOutParamClass, "texture", "Lcom/sensetime/stmobile/model/STEffectTexture;");

    jobject STEffectRenderOutParamObject = env->AllocObject(STEffectRenderOutParamClass);

    //human action
    jclass humanActionClass = env->FindClass("com/sensetime/stmobile/model/STHumanAction");
    jobject humanActionObject = env->AllocObject(humanActionClass);

    if(render_out_param->p_human != NULL){
        humanActionObject = convert2HumanAction(env, render_out_param->p_human);
    }
    env->SetObjectField(STEffectRenderOutParamObject, fieldHumanAction, humanActionObject);
    env->DeleteLocalRef(humanActionClass);

    //image
    jclass imageClass = env->FindClass("com/sensetime/stmobile/model/STImage");
    jobject imageObject = env->AllocObject(imageClass);

    if(render_out_param->p_image != NULL){
        imageObject = convert2Image(env, render_out_param->p_image);
    }
    env->SetObjectField(STEffectRenderOutParamObject, fieldImage, imageObject);
    env->DeleteLocalRef(imageClass);

    //texture
    jclass textureClass = env->FindClass("com/sensetime/stmobile/model/STEffectTexture");
    jobject textureObject = env->AllocObject(textureClass);

    textureObject = convert2EffectTexture(env, render_out_param->p_tex);

    env->SetObjectField(STEffectRenderOutParamObject, fieldTexture, textureObject);
    env->DeleteLocalRef(textureClass);

    env->DeleteLocalRef(STEffectRenderOutParamClass);
    return STEffectRenderOutParamObject;
}

jobject convert2EffectPackageInfo(JNIEnv *env, const st_effect_package_info_t *package_info){
    jclass package_info_cls = env->FindClass("com/sensetime/stmobile/model/STEffectPackageInfo");

    jfieldID fieldPackageId = env->GetFieldID(package_info_cls, "packageId", "I");
    jfieldID fieldModuleCount = env->GetFieldID(package_info_cls, "moduleCount", "I");
    jfieldID fieldState = env->GetFieldID(package_info_cls, "state", "I");
    jfieldID fieldName = env->GetFieldID(package_info_cls, "name", "[B");

    jobject packageInfoObj = env->AllocObject(package_info_cls);

    env->SetIntField(packageInfoObj, fieldState, (int)package_info->state);
    env->SetIntField(packageInfoObj, fieldPackageId, package_info->package_id);
    env->SetIntField(packageInfoObj, fieldModuleCount, (int)package_info->module_count);

    jbyteArray arrayName;
    jbyte* name = (jbyte*)(package_info->name);
    int len = strlen(package_info->name);
    arrayName = (env)->NewByteArray(len);

    if(name == NULL){
        return NULL;
    }
    env->SetByteArrayRegion(arrayName, 0, len, name);
    env->SetObjectField(packageInfoObj, fieldName, arrayName);

    env->DeleteLocalRef(arrayName);
    env->DeleteLocalRef(package_info_cls);

    return packageInfoObj;
}

jobject convert2EffectModuleInfo(JNIEnv *env, const st_effect_module_info_t *module_info){
    jclass module_info_cls = env->FindClass("com/sensetime/stmobile/model/STEffectModuleInfo");

    jfieldID fieldPackageId = env->GetFieldID(module_info_cls, "packageId", "I");
    jfieldID fieldModuleId = env->GetFieldID(module_info_cls, "moduleId", "I");
    jfieldID fieldState = env->GetFieldID(module_info_cls, "state", "I");
    jfieldID fieldName = env->GetFieldID(module_info_cls, "name", "[B");
    jfieldID fieldModuleType = env->GetFieldID(module_info_cls, "moduleType", "I");
    jfieldID fieldStrength = env->GetFieldID(module_info_cls, "strength", "F");
    jfieldID fieldInstanceId = env->GetFieldID(module_info_cls, "instanceId", "I");

    jobject packageInfoObj = env->AllocObject(module_info_cls);

    env->SetIntField(packageInfoObj, fieldState, (int)module_info->state);
    env->SetIntField(packageInfoObj, fieldPackageId, module_info->package_id);
    env->SetIntField(packageInfoObj, fieldModuleId, module_info->module_id);
    env->SetIntField(packageInfoObj, fieldModuleType, (int)module_info->type);
    env->SetIntField(packageInfoObj, fieldInstanceId, module_info->instance_id);
    env->SetFloatField(packageInfoObj, fieldStrength, module_info->strength);

    jbyteArray arrayName;
    jbyte* name = (jbyte*)(module_info->name);
    int len = strlen(module_info->name);
    arrayName = (env)->NewByteArray(len + 1);

    if(name == NULL){
        return NULL;
    }
    env->SetByteArrayRegion(arrayName, 0, len + 1, name);
    env->SetObjectField(packageInfoObj, fieldName, arrayName);

    env->DeleteLocalRef(arrayName);
    env->DeleteLocalRef(module_info_cls);

    return packageInfoObj;
}

jobject convert2EffectBeautyInfo(JNIEnv *env, const st_effect_beauty_info_t *beauty_info){
    jclass beauty_info_cls = env->FindClass("com/sensetime/stmobile/model/STEffectBeautyInfo");


    jfieldID fieldName = env->GetFieldID(beauty_info_cls, "name", "[B");
    jfieldID fieldType = env->GetFieldID(beauty_info_cls, "type", "I");
    jfieldID fieldMode = env->GetFieldID(beauty_info_cls, "mode", "I");
    jfieldID fieldStrength = env->GetFieldID(beauty_info_cls, "strength", "F");

    jobject beautyInfoObj = env->AllocObject(beauty_info_cls);

    env->SetIntField(beautyInfoObj, fieldType, (int)beauty_info->type);
    env->SetIntField(beautyInfoObj, fieldMode, (int)beauty_info->mode);
    env->SetFloatField(beautyInfoObj, fieldStrength, beauty_info->strength);

    jbyteArray arrayName;
    jbyte* name = (jbyte*)(beauty_info->name);
    int len = strlen(beauty_info->name);
    arrayName = (env)->NewByteArray(len);

    if(name == NULL){
        return NULL;
    }
    env->SetByteArrayRegion(arrayName, 0, len, name);
    env->SetObjectField(beautyInfoObj, fieldName, arrayName);

    env->DeleteLocalRef(arrayName);
    env->DeleteLocalRef(beauty_info_cls);

    return beautyInfoObj;
}