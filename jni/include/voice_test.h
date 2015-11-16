#ifndef voice_test_include_h
#define voice_test_include_h

#ifdef __cplusplus
#if	__cplusplus
extern "C"{
#endif
#endif

typedef int voice_fun(int voice_type,const char* buf,int len);

/*
 * msg_type: 0- disconnect  1-connect
 * buf:     当前为NULL
 * len:     当前为0
 */
typedef int msg_fun(int msg_type,const char* buf,int len);

int voice_init(const char* id,
        const char*local_phone,
        const char* name,
        const char* ip,
        short port,
        voice_fun* fun,
        msg_fun* mfun);

int voice_uninit();

int voice_request();

int voice_send(int voice_type,const char* buf,int len);

int voice_stop();

int voice_is_connected();

#ifdef __cplusplus
#if	__cplusplus
}
#endif
#endif

#endif

