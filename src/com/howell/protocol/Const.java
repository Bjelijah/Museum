package com.howell.protocol;
/**
 * @author 霍之昊 
 *
 * 类说明
 */
public interface Const {
	static final int POST = 1;
	static final int GET = 2;
	static final int STRING = 3;
	static final int BYTEARRAY = 4;
	
	static final int CONNECT_SERVICE = 5;		//已经连上语音平台
	static final int DISCONNECT_SERVICE = 6;	//断开与语音平台连接
	static final int REQUEST = 7;				//请求对讲状态
	static final int TALKING = 8;				//正在对讲状态
	static final int STOP = 9;					//停止对讲状态
}
