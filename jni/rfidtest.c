#include <string.h>
#include <jni.h>
#include <android/log.h>
#include "Card.h"
#include "Rc522.h"

JNIEnv *jniEnv;

jint Java_com_ge_test_NdkarrayActivity_getDoubleNumber(JNIEnv *env, jobject thiz, jint num)
{
	if (jniEnv == NULL) {
		jniEnv = env;
	}
	__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "Java ---> C JNI :num=%d", num);
	return num*2;
}

jbyteArray Java_com_ge_test_NdkarrayActivity_getArrayDoubleNumber(JNIEnv *env, jobject thiz, jbyteArray nums)
{
	if(jniEnv == NULL) {
		jniEnv = env;
	}

	if (nums == NULL) {
		return NULL;
	}

	jsize len = (*jniEnv)->GetArrayLength(jniEnv, nums);

	__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "Java --> C JNI : len=%d\n", len);

	if (len <= 0) {
		return NULL;
	}

	jbyteArray array = (*jniEnv)->NewByteArray(jniEnv, len);
	if (array == NULL) {
		return NULL;
	}

	jbyte *body = (*env)->GetByteArrayElements(env, nums, 0);

	jint i = 0;
	jbyte num[len];

	/*
	for (; i < len; i++) {
		num[i] = 'a' + i;
		__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "Java --> C JNI	: nums[%d]=%c", i, num[i]);
	}
	*/

	if (num == NULL) {
		return NULL;
	}

	(*jniEnv)->SetByteArrayRegion(jniEnv, array, 0, len, num);
	return array;
}

jint Java_com_ge_test_NdkarrayActivity_Rc522Init(JNIEnv *env, jobject thiz)
{
	if (jniEnv == NULL) {
		jniEnv = env;
	}
	Rc522Init();
	return 0;
}

jint Java_com_ge_test_NdkarrayActivity_ChangeType(JNIEnv *env, jobject thiz, jint type)
{
	if (jniEnv == NULL) {
		jniEnv = env;
	}
	ChangeType(type);	
	return 0;
}

jbyteArray Java_com_ge_test_NdkarrayActivity_RequestA(JNIEnv *env, jobject thiz, jint code)
{
	if (jniEnv == NULL) {
		jniEnv = env;
	}
	jbyte uid[16];

	RequestA(code, uid); 

	jbyteArray array = (*jniEnv)->NewByteArray(jniEnv, 16);
	if (array == NULL) {
		return NULL;
	}
	jint i = 0;
	for (; i < 16; i++) {
		__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "RequestA uid[%d]=%02x\n", i, uid[i]);
	}

	(*jniEnv)->SetByteArrayRegion(jniEnv, array, 0, 16, uid);

	return array;
}

jbyteArray Java_com_ge_test_NdkarrayActivity_SelectA(JNIEnv *env, jobject thiz, jbyteArray uid)
{
	if (jniEnv == NULL) {
		jniEnv = env;
	}
	
	jsize blen = (*jniEnv)->GetArrayLength(jniEnv, uid);
	__android_log_print(ANDROID_LOG_INFO, "JNIMSG", "SelectA len=%d", blen);
	if (blen <= 0) {
		return NULL;
	}


	jbyte *body = (*env)->GetByteArrayElements(env, uid, 0);
	
	jbyte num[blen];
	jint i = 0;
	for (i = 0; i < blen; i++) {
		num[i] = body[i];
	}
	unsigned short status;
	unsigned char len;

	status = AnticollSelectA(0, num, &len);
	if (status && status != STATUS_COLLISION_ERROR) {
		__android_log_print(ANDROID_LOG_INFO, "JNIMSG", "Not Found Card\n");
		return NULL;
	}
	for (i = 0; i < len ; i++) {
		__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "SelectA %02x\n", num[i]);
	}

	jbyteArray oldarray = (*jniEnv)->NewByteArray(jniEnv, len); 
	(*jniEnv)->SetByteArrayRegion(jniEnv, oldarray, 0, len, num);

	return oldarray;
}

jint Java_com_ge_test_NdkarrayActivity_R522Close(JNIEnv *env, jobject thiz)
{
	if (jniEnv == NULL) {
		jniEnv = env;
	}
	Rc522Close();
	__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "RC522 Close\n");
	return 0;	
}

//认证
//key 6 byte
//snr 4 byte
jint Java_com_ge_test_NdkarrayActivity_AuthenticationA(JNIEnv *env, jobject thiz, jint auth_mode, jbyteArray key, jbyteArray uid, jint block)
{
	jint status;

	if (jniEnv == NULL) {
		jniEnv = env;
	}
	jsize len;

	len = (*jniEnv)->GetArrayLength(jniEnv, key);
	if (len < 0) return -1;

	len = (*jniEnv)->GetArrayLength(jniEnv, uid);
	if (len < 0) return -1;

	jbyte *lkey = (*env)->GetByteArrayElements(env, key, 0);
	jbyte *luid = (*env)->GetByteArrayElements(env, uid, 0);

	status = AuthenticationA(auth_mode, lkey, luid, block);
	if (status) {
		return -1;
	} else {
		return 0;
	}
}

jbyteArray Java_com_ge_test_NdkarrayActivity_ReadA(JNIEnv *env,  jobject thiz, jint block)
{
	if (jniEnv == NULL) {
		jniEnv = env;
	}
	jbyte rdata[16];
	jint status;

	status = ReadA(block, rdata);
	if (status) {
		__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "ReadA block %d error\n", block);
		return NULL;
	}
	
	jbyteArray array = (*jniEnv)->NewByteArray(jniEnv, 16);
	if (array == NULL) {
		__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "ReadA new bytes error\n");
		return NULL;
	}
	
	jint i;
	for (; i < 16; i++) {
		__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "ReadA[%d]=%02x\n", i, rdata[i]);
	}

	(*jniEnv)->SetByteArrayRegion(jniEnv, array, 0, 16, rdata);
	return array;
}

jint Java_com_ge_test_NdkarrayActivity_WriteA(JNIEnv *env, jobject thiz, jint block, jbyteArray wbuf)
{
	if (jniEnv == NULL) {
		jniEnv = env;
	}
		
	jsize len = (*jniEnv)->GetArrayLength(jniEnv, wbuf);
	if (len < 16) {
		__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "length low 16byte\n");
		return -1;
	}
	
	jbyte lwdata[16];
	jint i;
	jbyte *body = (*env)->GetByteArrayElements(env, wbuf, 0);

	for (i =0; i < 16; i++) {
		lwdata[i] = body[i];
	}
	jint status;	

	status = WriteA(block, lwdata);
	if (status) {
		__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "WriteA data error\n");
		return -1;
	}
	return 0;
}
