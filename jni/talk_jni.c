#include <jni.h>
#include <pthread.h>
#include <android/log.h>
#include <stdio.h>
#include <string.h>
#include "include/net_sdk.h"

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "yv12", __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, "yv12", __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "yv12", __VA_ARGS__))

struct StreamResource
{
	JavaVM * jvm;
	JNIEnv * env;
	jobject obj;
	jmethodID mid;
	int method_ready;
	USER_HANDLE user_handle;
	VOICE_STREAM_HANDLE voice_stream_handle;
};
static struct StreamResource * res = NULL;

int on_voice_fun(int voice_type,const char* buf,int len){
	__android_log_print(ANDROID_LOG_INFO, "jni", "on_voice_fun voice_type:%d len:%d",voice_type,len);
	audio_play(buf,len);
}

/*
 * msg_type: 0- disconnect  1-connect
 * buf:     当前为NULL
 * len:     当前为0
 */
int on_msg_fun(int msg_type,const char* buf,int len){
	__android_log_print(ANDROID_LOG_INFO, "jni", " msg_type:%d",msg_type);
	if(msg_type == 0){
		if((*res->jvm)->AttachCurrentThread(res->jvm, &res->env, NULL) != JNI_OK) {
			    LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
			    return;
		}

		/* get JAVA method first */
		if (!res->method_ready) {
			jclass cls = (*res->env)->GetObjectClass(res->env,res->obj);
			if (cls == NULL) {
				LOGE("FindClass() Error.....");
			    goto error;
			}
			res->mid = (*res->env)->GetMethodID(res->env, cls, "disConnect", "()V");
			if (res->mid == NULL ) {
				LOGE("GetMethodID() Error.....");
			    goto error;
			}
			res->method_ready=1;
		}

		/* notify the JAVA */
		(*res->env)->CallVoidMethod(res->env, res->obj, res->mid, NULL);
		//LOGE("555555555");

		if ((*res->jvm)->DetachCurrentThread(res->jvm) != JNI_OK) {
			LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
		}
		return;

		error:
		if ((*res->jvm)->DetachCurrentThread(res->jvm) != JNI_OK) {
			LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
		}
		return;
	}
}

//static int register_talk_callback(const char* id,const char*local_phone,const char* name, const char* ip , short port){
	//int ret = voice_init(local_phone,name,ip,port,on_voice_fun);
//	int ret = voice_init(id,local_phone,name,ip,port,on_voice_fun,on_msg_fun);
//	return ret;
//}

int registerService(const char* id,const char*local_phone,const char* name,const char* ip,short port){
	/**
	 * 注册到服务器
	 * id: 必须唯一
	 * local_phone: 电话号码
	 * name: 姓名(UTF-8格式)
	 * ip: 服务器IP
	 * port:服务器端口
	 * fun:回调函数
	 * return: 0-成功,-1-失败
	 */
	return voice_register2svr(id,local_phone,name,ip,port,on_voice_fun);
}

static int create_resource(JNIEnv *env, jobject obj,jstring j_id, jstring j_local_phone,jstring j_name,jstring j_ip ,short port)
{
  res = (struct StreamResource *)calloc(1,sizeof(*res));
  if (res == NULL) return;
  (*env)->GetJavaVM(env,&res->jvm);
  res->obj = (*env)->NewGlobalRef(env,obj);
  res->method_ready = 0;
  const char* id = (*env)-> GetStringUTFChars(env,j_id,NULL);
  const char* ip = (*env)-> GetStringUTFChars(env,j_ip,NULL);
  const char* local_phone = (*env)-> GetStringUTFChars(env,j_local_phone,NULL);
  const char* name = (*env)-> GetStringUTFChars(env,j_name,NULL);
  int ret = registerService(id,local_phone,name,ip,port);
  __android_log_print(ANDROID_LOG_INFO, "registerService", "ret %d,ip %s,port:%d",ret,ip,port);
  (*env)->ReleaseStringUTFChars(env,j_id,id);
  (*env)->ReleaseStringUTFChars(env,j_ip,ip);
  (*env)->ReleaseStringUTFChars(env,j_local_phone,local_phone);
  (*env)->ReleaseStringUTFChars(env,j_name,name);
  return ret;
}

JNIEXPORT int JNICALL Java_com_howell_formuseum_JNIManager_register2service
(JNIEnv *env, jobject obj, jstring j_id,jstring j_local_phone,jstring j_name, jstring j_ip , short port){
	return create_resource(env,obj,j_id,j_local_phone,j_name,j_ip , port);
}

JNIEXPORT void JNICALL Java_com_howell_formuseum_JNIManager_unregister2service
(JNIEnv *env, jclass cls){
	voice_unregister2svr();
}

JNIEXPORT int JNICALL Java_com_howell_formuseum_JNIManager_getRegisterState
(JNIEnv *env, jclass cls){
	/*
	 * 获取注册状态
	 * state: 返回 0-异常  1-正常
	 * return:0-成功 -1-失败
	 */
	int state;
	int ret = voice_get_register_state(&state);
	return state;
}

JNIEXPORT int JNICALL Java_com_howell_formuseum_JNIManager_setHeartBeat
(JNIEnv *env, jclass cls){
	return voice_send_register_heartbeat();
}

/*
 * 请求与服务器说话
 * return: 0-成功(因为服务器不是立刻回复，所以还需要调用voice_get_talk_state) -1-失败
 */
JNIEXPORT int JNICALL Java_com_howell_formuseum_JNIManager_requestTalk
(JNIEnv *env, jclass cls){
	return voice_start_talk2svr();
}

JNIEXPORT int JNICALL Java_com_howell_formuseum_JNIManager_getTalkState
(JNIEnv *env, jclass cls){
	/*
	 * 获取与服务器的说话状态
	 * state: 返回 1-正在等待服务器应答  2-服务器允许通话  3-服务器拒绝通话
	 * return: 0-成功 -1-失败
	 */
	int state;
	int ret = voice_get_talk_state(&state);
	return state;
}

JNIEXPORT int JNICALL Java_com_howell_formuseum_JNIManager_stopTalk
(JNIEnv *env, jclass cls){
	return voice_stop_talk2svr();
}

JNIEXPORT int JNICALL Java_com_howell_formuseum_JNIManager_setData
(JNIEnv *env, jclass cls,jbyteArray bytes ,int len){
	int voice_type = 1;
	char *temp = (*env)->GetByteArrayElements(env,bytes,NULL);
	if(temp == NULL){
		__android_log_print(ANDROID_LOG_INFO, "inputVoice", "temp == NULL");
		return 0;
	}
	int ret = voice_input_voice_data(voice_type,temp,len);
	(*env)->ReleaseByteArrayElements(env,bytes,temp,0);
	return ret;
}

