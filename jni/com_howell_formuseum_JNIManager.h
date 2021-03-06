/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_howell_formuseum_JNIManager */

#ifndef _Included_com_howell_formuseum_JNIManager
#define _Included_com_howell_formuseum_JNIManager
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    talkInit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkInit
  (JNIEnv *, jobject);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    talkDeInit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkDeInit
  (JNIEnv *, jobject);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    talkRegister2service
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;I)Z
 */
JNIEXPORT jboolean JNICALL Java_com_howell_formuseum_JNIManager_talkRegister2service
  (JNIEnv *, jobject, jstring, jstring, jstring, jint, jstring, jint);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    talkUnregister
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkUnregister
  (JNIEnv *, jobject);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    talkSetCallbackObject
 * Signature: (Ljava/lang/Object;I)V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkSetCallbackObject
  (JNIEnv *, jobject, jobject, jint);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    talkSetCallbackMethodName
 * Signature: (Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkSetCallbackMethodName
  (JNIEnv *, jobject, jstring, jint);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    talkSetHeartBeat
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkSetHeartBeat
  (JNIEnv *, jobject);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    talkGetRegisterState
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_howell_formuseum_JNIManager_talkGetRegisterState
  (JNIEnv *, jobject);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    talkGetDialogList
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkGetDialogList
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    talkSetNextTarget
 * Signature: ([Ljava/lang/String;[Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkSetNextTarget
  (JNIEnv *, jobject, jobjectArray, jobjectArray);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    setBoardcastData
 * Signature: ([BI)I
 */
JNIEXPORT jint JNICALL Java_com_howell_formuseum_JNIManager_setBoardcastData
  (JNIEnv *, jobject, jbyteArray, jint);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    talkSetSilent
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkSetSilent
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    talkCreateGroup
 * Signature: (Ljava/lang/String;[Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkCreateGroup
  (JNIEnv *, jobject, jstring, jobjectArray, jint);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    talkDeleteGroup
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkDeleteGroup
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    talkUpdataGroup
 * Signature: (Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkUpdataGroup
  (JNIEnv *, jobject, jstring, jstring, jobjectArray, jobjectArray);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    talkGetGroups
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkGetGroups
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    talkGetGroup
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkGetGroup
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    setGroupData
 * Signature: (Ljava/lang/String;[BI)I
 */
JNIEXPORT jint JNICALL Java_com_howell_formuseum_JNIManager_setGroupData
  (JNIEnv *, jobject, jstring, jbyteArray, jint);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    setReceiveRes
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_howell_formuseum_JNIManager_setReceiveRes
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    talkSilentGroup
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkSilentGroup
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    talkOnOfflineNotice
 * Signature: (Ljava/lang/String;Ljava/lang/String;Z)V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkOnOfflineNotice
  (JNIEnv *, jobject, jstring, jstring, jboolean);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    talkGetUsers
 * Signature: (Ljava/lang/String;II)V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkGetUsers
  (JNIEnv *, jobject, jstring, jint, jint);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    talkGetUser
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_talkGetUser
  (JNIEnv *, jobject, jstring, jstring);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    setReceiveGroup
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_howell_formuseum_JNIManager_setReceiveGroup
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    register2service
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;S)I
 */
JNIEXPORT jint JNICALL Java_com_howell_formuseum_JNIManager_register2service
  (JNIEnv *, jobject, jstring, jstring, jstring, jstring, jshort);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    unregister2service
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_unregister2service
  (JNIEnv *, jobject);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    getRegisterState
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_howell_formuseum_JNIManager_getRegisterState
  (JNIEnv *, jobject);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    setHeartBeat
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_howell_formuseum_JNIManager_setHeartBeat
  (JNIEnv *, jobject);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    requestTalk
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_howell_formuseum_JNIManager_requestTalk
  (JNIEnv *, jobject);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    getTalkState
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_howell_formuseum_JNIManager_getTalkState
  (JNIEnv *, jobject);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    stopTalk
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_stopTalk
  (JNIEnv *, jobject);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    setData
 * Signature: ([BI)I
 */
JNIEXPORT jint JNICALL Java_com_howell_formuseum_JNIManager_setData
  (JNIEnv *, jobject, jbyteArray, jint);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    setAudioData
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_com_howell_formuseum_JNIManager_setAudioData
  (JNIEnv *, jobject, jstring, jint);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    pcm2G711u
 * Signature: ([BI[B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_howell_formuseum_JNIManager_pcm2G711u
  (JNIEnv *, jobject, jbyteArray, jint, jbyteArray);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    g711u2Pcm
 * Signature: ([BI[B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_howell_formuseum_JNIManager_g711u2Pcm
  (JNIEnv *, jobject, jbyteArray, jint, jbyteArray);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    g711AudioPlay
 * Signature: ([BI)V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_g711AudioPlay
  (JNIEnv *, jobject, jbyteArray, jint);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    pcmAudioPlay
 * Signature: ([BI)V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_pcmAudioPlay
  (JNIEnv *, jobject, jbyteArray, jint);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    nativeInit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_nativeInit
  (JNIEnv *, jobject);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    YUVSetCallbackObject
 * Signature: (Ljava/lang/Object;I)V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_YUVSetCallbackObject
  (JNIEnv *, jobject, jobject, jint);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    nativeOnSurfaceCreated
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_nativeOnSurfaceCreated
  (JNIEnv *, jobject);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    nativeDeinit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_nativeDeinit
  (JNIEnv *, jobject);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    nativeRenderY
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_nativeRenderY
  (JNIEnv *, jobject);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    nativeRenderU
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_nativeRenderU
  (JNIEnv *, jobject);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    nativeRenderV
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_nativeRenderV
  (JNIEnv *, jobject);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    nativeAudioInit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_nativeAudioInit
  (JNIEnv *, jobject);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    nativeAudioSetCallbackObject
 * Signature: (Ljava/lang/Object;I)V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_nativeAudioSetCallbackObject
  (JNIEnv *, jobject, jobject, jint);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    nativeAudioSetCallbackMethodName
 * Signature: (Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_nativeAudioSetCallbackMethodName
  (JNIEnv *, jobject, jstring, jint);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    nativeAudioBPlayable
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_nativeAudioBPlayable
  (JNIEnv *, jobject);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    nativeAudioStop
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_nativeAudioStop
  (JNIEnv *, jobject);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    nativeAudioDeinit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_nativeAudioDeinit
  (JNIEnv *, jobject);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    register
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_howell_formuseum_JNIManager_register
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    unregister
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_unregister
  (JNIEnv *, jobject);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    stopPlay
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_stopPlay
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_howell_formuseum_JNIManager
 * Method:    display
 * Signature: (ISSSSSSSSSSSSI)I
 */
JNIEXPORT jint JNICALL Java_com_howell_formuseum_JNIManager_display
  (JNIEnv *, jobject, jint, jshort, jshort, jshort, jshort, jshort, jshort, jshort, jshort, jshort, jshort, jshort, jshort, jint);

#ifdef __cplusplus
}
#endif
#endif
