package com.howell.utils;

import android.util.Log;

import com.howell.formuseum.JNIManager;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class TalkManager {
    private static TalkManager sInstance = new TalkManager();
    
	private TalkManager() {

    }
    public static TalkManager getInstance() {
        return sInstance;
    }
    
	private boolean exit = false;//程序退出标志位
	
    public boolean isExit() {
		return exit;
	}
	public void setExit(boolean exit) {
		this.exit = exit;
	}

	private JNIManager jni = new JNIManager();
	
	public void test(){
		Log.e("", "jni:"+jni.toString());
	}
	
	private int register_state;//是否与语音平台联通
	
	public int get_register_state() {
		return register_state;
	}
	public void set_register_state(int register_state) {
		this.register_state = register_state;
	}
	
	//is_voice_start 表示是否启动了对讲
//    int is_voice_start = 0;
//    
//	public int getIs_voice_start() {
//		return is_voice_start;
//	}
//	public void setIs_voice_start(int is_voice_start) {
//		this.is_voice_start = is_voice_start;
//	}

	class MyCheckingThread extends Thread{
		private int is_register;
    	private String id;
    	private String local_phone;
    	private String name; 
    	private String ip;
    	private short port;
    	
		public MyCheckingThread(int is_register, String id, String local_phone,
				String name, String ip, short port) {
			super();
			this.is_register = is_register;
			this.id = id;
			this.local_phone = local_phone;
			this.name = name;
			this.ip = ip;
			this.port = port;
			exit = false;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
	        while(!exit){
	        	Log.v("doInBackground", "exit:"+exit);
	        	if(is_register == 0){	//成功
		    		register_state = jni.getRegisterState();
		    		Log.v("doInBackground", "register_state:"+register_state);
		    		if(register_state == 0){
		                //先关闭对讲
		                jni.stopTalk();
//		                is_voice_start = 0;

		                jni.unregister2service();
		                is_register = -1;
		                
		            }else{
		                //正常，发送心跳
		                jni.setHeartBeat();
		            }
		    	}else{					//失败
		    		is_register = jni.register2service(id,local_phone,name,ip,port);
		    	}
	        	try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        unregisterService();
		}
	}
	
    private void startCheckRegisterState(int is_register,String id,String local_phone,String name, String ip , short port){
    	MyCheckingThread thread = new MyCheckingThread(is_register,id,local_phone,name, ip , port);
    	thread.start();
    }
    
    public void registerService(String id,String local_phone,String name, String ip , short port){
    	int is_register = jni.register2service(id,local_phone,name, ip , port);
    	startCheckRegisterState(is_register,id,local_phone,name, ip , port);
    	jni.audioPlay();
    }
    
    public void unregisterService(){
    	jni.unregister2service();
    }
    
    public int requestTalk(){
    	return jni.requestTalk();
    }
    
    public int getTalkState(){
    	return jni.getTalkState();
    }

    public void stopTalk(){
    	jni.stopTalk();
    }
    
    public void audioPlay(){
    	jni.audioPlay();
    }
    
    public void audioStop(){
    	jni.audioStop();
    }
    
    public int setData(byte[] buf,int len){
    	return jni.setData(buf, len);
    }
    
}
