package com.howell.formuseum;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.opengl.GLSurfaceView;
import android.util.Log;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class JNIManager {
	
	static {
		System.loadLibrary("hwplay");
		System.loadLibrary("hwnet_jni");
        System.loadLibrary("talk_jni");
    }
	
	//语音对讲
	public native int register2service(String id,String local_phone,String name, String ip , short port);
	public native void unregister2service();
	public native int getRegisterState();	//0异常，1正常
	public native int setHeartBeat();
	public native int requestTalk();
	public native int getTalkState();		//1-正在等待服务器应答  2-服务器允许通话  3-服务器拒绝通话
	public native void stopTalk();
	public native int setData(byte[] buf, int len);
	
	public void disConnect(){
		Log.e("talk jni","talk disconnect with service");
	}
	
	//用于显示YUV数据
    public native void nativeInit();			//初始化
    public native void nativeOnSurfaceCreated();//开始显示YUV数据
    public native void nativeDeinit();			//释放内存
    public native void nativeRenderY();			//渲染Y数据
    public native void nativeRenderU();			//渲染U数据
    public native void nativeRenderV();			//渲染V数据
    
	private GLSurfaceView gl_surface_view_;
	
	public void setGl_surface_view_(GLSurfaceView gl_surface_view_) {
		this.gl_surface_view_ = gl_surface_view_;
	}
	
	//jni调用
	public void requestRender() {
		//Log.d("render","native invoke render ");
		gl_surface_view_.requestRender();
	}
    
    //用于播放音频
    public native void nativeAudioInit();		//初始化
    public native void nativeAudioStop();		//停止
    public native void nativeAudioDeinit();		//释放内存
	private static AudioTrack mAudioTrack;
	private byte[] mAudioData;
	private int mAudioDataLength;
    
	//jni调用
    public void audioWrite() {
		mAudioTrack.write(mAudioData,0,mAudioDataLength);
	}    
	
    public void audioPlay() {
		int buffer_size = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, buffer_size*8, AudioTrack.MODE_STREAM);
		mAudioData = new byte[buffer_size*8];
		nativeAudioInit();
		mAudioTrack.play();
	}
    
    public void audioStop(){
    	nativeAudioStop();
    	nativeAudioDeinit();
    	if(mAudioTrack != null){
    		mAudioTrack.stop();
    		mAudioTrack.release();
    	}
    }
    
    //登录nvr 1:成功 0:失败
    public native int register(String ip);
    
    //登出nvr
    public native void unregister();
    
    //关闭码流
    public native void stopPlay(int is_playback);
    
    //播放回放
    public native void display(int isPlayBack,short begYear,short begMonth,short begDay,short begHour
    		,short begMinute,short begSecond,short endYear,short endMonth,short endDay,short endHour,short endMinute
    		,short endSecond,int slot);
    
}
