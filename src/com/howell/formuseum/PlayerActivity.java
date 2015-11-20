package com.howell.formuseum;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.howell.protocol.HttpProtocol;
import com.howell.protocol.entity.Device;
import com.howell.protocol.entity.EventLinkage;
import com.howell.protocol.entity.EventLinkageList;
import com.howell.protocol.entity.EventNotify;
import com.howell.protocol.entity.PlaybackTask;
import com.howell.protocol.entity.VideoPlaybackIdentifier;
import com.howell.render.YV12Renderer;
import com.howell.utils.JsonUtils;
import com.howell.utils.MD5;
import com.howell.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.widget.Toast;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class PlayerActivity extends Activity implements Callback{
	
	private EventNotify eventNotify;
	private String webserviceIp,session,cookieHalf,verify;
	private HttpProtocol hp;
	private JNIManager jni;
	
	private GLSurfaceView mGlView;
	private boolean mPausing;
	
	private String url;
	private int slot;
	private String time;
	
	private	boolean isPlayback;
	
	private static final int LOGIN_FAIL = 1;
	private static final int SHOW_INFO = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);
		isPlayback = init();
		Log.e("", "isPlayback:"+isPlayback);
		PlayerThread thread = new PlayerThread();
		thread.start();
		/*new AsyncTask<Void, Integer, Void>() {
			boolean ret = false;
			@Override
			protected Void doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				getEventLinkage();
				int r = isPlayback ? 1 : 0;
				Log.e("", "isplayback:"+r);
				if(url != null){
					ret = display(isPlayback ? 1 : 0);
				}else{
					Log.e("", "url is null");
				}
				
				return null;
			}
			
			protected void onPostExecute(Void result) {
				if(!ret){
					if(!isDestroyed()){
						new AlertDialog.Builder(PlayerActivity.this)   
				        .setTitle("登录失败")   
				        .setMessage("连接失败，请重新连接")                 
				        .setPositiveButton("确定", new OnClickListener() {
							
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								
								PlayerActivity.this.finish();
							}
						})   
				        .show();  
					}
				}
			};
			
		}.execute();*/
	}
	
	private class PlayerThread extends Thread{
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			boolean ret = false;
			getEventLinkage();
			if(url != null){
				ret = display(isPlayback ? 1 : 0);
			}else{
				Log.e("", "url is null");
			}
			
			if(!ret){
				handler.sendEmptyMessage(LOGIN_FAIL);
			}
		}
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case LOGIN_FAIL:
				if(!isDestroyed()){
					new AlertDialog.Builder(PlayerActivity.this)   
			        .setTitle("登录失败")   
			        .setMessage("连接失败，请重新连接")                 
			        .setPositiveButton("确定", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							
							PlayerActivity.this.finish();
						}
					})   
			        .show();  
				}
				break;
			case SHOW_INFO:
				Toast.makeText(PlayerActivity.this, "url:"+url+",slot:"+slot, 1000).show();
				break;
			}
		};
	};
	
	private boolean init(){
		Intent intent = getIntent();
		webserviceIp = intent.getStringExtra("webserviceIp");
		session = intent.getStringExtra("session");
		cookieHalf = intent.getStringExtra("cookieHalf");
		verify = intent.getStringExtra("verify");
		eventNotify = (EventNotify) intent.getSerializableExtra("eventNotify");
		time = eventNotify.getTime();
		
		hp = new HttpProtocol();
		jni = new JNIManager();
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    mGlView = (GLSurfaceView)findViewById(R.id.glsurface_view);
	 	mGlView.setEGLContextClientVersion(2);
	 	mGlView.setRenderer(new YV12Renderer(this,mGlView,jni));
	 	mGlView.getHolder().addCallback((Callback) this);
	 	mGlView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	 	
	 	return intent.getBooleanExtra("isPlayBack", true);
	}
	
/*	private void getPlaybackUrl(){
		try {
			EventLinkageList eventLinkageList = JsonUtils.parseEventLinkageListJsonObject(new JSONObject(hp.linkages(webserviceIp, eventNotify.getId(), eventNotify.getEventType(), eventNotify.getEventState(), 1, 10,cookieHalf+"verifysession="+MD5.getMD5("GET:"+"/howell/ver10/data_service/management/System/Events/Linkages:"+verify))));
			EventLinkage eventLinkage = eventLinkageList.getEventLinkage().get(0);
			VideoPlaybackIdentifier videoPlaybackIdentifier = eventLinkage.getVideoPlaybackIdentifier().get(0);
			
			PlaybackTask playbackTask = JsonUtils.parsePlaybackTaskJsonObject(new JSONObject(hp.playback(webserviceIp, videoPlaybackIdentifier.getVideoInputChannelId(), videoPlaybackIdentifier.getStreamNo(), videoPlaybackIdentifier.getBeginTime(), videoPlaybackIdentifier.getEndTime(), cookieHalf+"verifysession="+MD5.getMD5("GET:"+"/howell/ver10/data_service/business/informations/Business/Clients/Tasks/Playback:"+verify))));
			String url = playbackTask.getUrl();
			
			//播放视频
			//jni.displayPlayBack(begYear, begMonth, begDay, begHour, begMinute, begSecond, endYear, endMonth, endDay, endHour, endMinute, endSecond, videoPlaybackIdentifier.getStreamNo() - 1);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	private void getEventLinkage(){
		try {
			Log.e("", "getEventLinkage");
			String deviceId = "";
			EventLinkage eventLinkage = JsonUtils.parseEventLinkageJsonObject(new JSONObject(hp.linkage(webserviceIp, eventNotify.getId(), eventNotify.getEventType(), eventNotify.getEventState(),cookieHalf+"verifysession="+MD5.getMD5("GET:"+"/howell/ver10/data_service/management/System/Events/Linkages/Components/"+eventNotify.getId()+"/"+eventNotify.getEventType()+"/"+eventNotify.getEventState()+":"+verify))));
			if(eventLinkage.getVideoPlaybackIdentifier() != null){
				deviceId = setDeviceId(eventLinkage.getVideoPlaybackIdentifier().get(0).getVideoInputChannelId());
				slot = getSlot(eventLinkage.getVideoPlaybackIdentifier().get(0).getVideoInputChannelId()) - 1;
			}else{
				deviceId = setDeviceId(eventLinkage.getVideoPreviewIdentifier().get(0).getVideoInputChannelId());
				slot = getSlot(eventLinkage.getVideoPreviewIdentifier().get(0).getVideoInputChannelId()) - 1;
			}
			Log.e("", "slot:"+slot);
			Device device = JsonUtils.parseDeviceJsonObject(new JSONObject(hp.device(webserviceIp, deviceId, cookieHalf+"verifysession="+MD5.getMD5("GET:"+"/howell/ver10/data_service/management/System/Devices/"+deviceId+":"+verify))));
			Log.e("1", device.getUri());
			url = device.getUri().split(":")[1].substring(2);
			//url = "192.168.3.99";
			Log.e("2", url);
			//handler.sendEmptyMessage(SHOW_INFO);
			//int beg = eventLinkage.getVideoPlaybackIdentifier().get(0).getBeginTime();
			//int end = eventLinkage.getVideoPlaybackIdentifier().get(0).getEndTime();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean display(int isplayback){
		if(jni.register(url) == 0){
			//失败
			Log.e("", "login失败");
			return false;
		}else{
			//成功
			Date date = Utils.StringToDate(time);
			/*short year = (short)(date.getYear() + 1900);
			short month = (short)(date.getMonth() + 1);
			short day = (short)date.getDate();
			short hour = (short)date.getHours();
			short minute = (short)date.getMinutes();
			short second = (short)date.getSeconds();*/
			short beg[] = Utils.getFiveSecondsBeforeDate(date);
			short end[] = Utils.getTwoSecondsAfterDate(date);
			
			jni.display(isplayback, beg[0], beg[1], beg[2], beg[3], beg[4], beg[5], end[0], end[1], end[2], end[3], end[4], end[5], slot);
			return true;
		}
	}
	
	private String setDeviceId(String videoInputChannelId){
		char[] list = videoInputChannelId.toCharArray();
		for(int i = 24; i < 32 ; i++){
			list[i] = '0';
		}
		return String.valueOf(list);
	}
	
	private int getSlot(String videoInputChannelId){
		String slot = videoInputChannelId.substring(28);
		return Integer.valueOf(slot);
	}
	
	@Override
	protected void onPause() {
		Log.e("", "onPause");
		mPausing = true;
		this.mGlView.onPause();
		super.onPause();
		
		//finish();
	}

	@Override
	protected void onDestroy() {
		//Log.e("", "onDestroy");
		super.onDestroy();
		System.runFinalization();
		if(url != null){
			jni.stopPlay(isPlayback ? 1 : 0);
			jni.unregister();
		}
	}

	@Override
	protected void onResume() {
		Log.e("", "onResume");
		mPausing = false;
		mGlView.onResume();
		super.onResume();
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}
}
