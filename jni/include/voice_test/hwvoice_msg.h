#ifndef hwvoice_msg_include_h
#define hwvoice_msg_include_h

namespace hwvoice{

	typedef struct  
	{
		int tag;
		int len;
		int id;
		char type;		
	}msg_t;
	
	typedef struct  
	{
		char name[64];
		char region[64];
		char phone[32];
		char reserve[128];
	}msg_detail_t;

	typedef struct  
	{
		char reserve[256];
	}msg_heartbeat_t;

#define VOICE_TAG 0x12345678
#define HWVOICE_MSG_VOICE_DATA							0x0
#define HWVOICE_MSG_VIOCE_HEARTBEAT				0x1
#define HWVOICE_MSG_VOICE_DETAIL						0x2

}

#endif
