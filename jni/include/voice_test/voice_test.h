#ifndef voice_test_include_h
#define voice_test_include_h

#ifdef __cplusplus
#if	__cplusplus
extern "C"{
#endif
#endif

typedef int voice_fun(int voice_type,const char* buf,int len);

int voice_init(const char*local_phone,const char* name,const char* ip,short port,voice_fun* fun);

int voice_uninit();

int voice_request();

int voice_send(int voice_type,const char* buf,int len);

int voice_stop();

#ifdef __cplusplus
#if	__cplusplus
}
#endif
#endif

#endif

