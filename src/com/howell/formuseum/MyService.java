package com.howell.formuseum;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import java.util.logging.MemoryHandler;

import org.json.JSONException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.howell.db.DBManager;
import com.howell.formusemu.action.AlarmSound;
import com.howell.formusemu.action.AudioAction;
import com.howell.formusemu.action.OnAudioComing;
import com.howell.protocol.Const;
import com.howell.protocol.CseqManager;
import com.howell.protocol.HttpProtocol;
import com.howell.protocol.entity.EventHandleRes;
import com.howell.protocol.entity.EventNotify;
import com.howell.protocol.entity.EventNotifyRes;
import com.howell.protocol.entity.KeepAliveRes;
import com.howell.utils.DebugUtil;
import com.howell.utils.DialogUtils;
import com.howell.utils.MD5;
import com.howell.utils.SdCardUtil;
import com.howell.utils.SharedPreferencesUtils;
import com.howell.utils.SystemUpTimeUtils;
import com.howell.utils.TalkManager;
import com.howell.utils.WebSocketConnectionManager;
import com.howell.utils.WebSocketProtocolUtils;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;

public class MyService extends Service implements Const,OnAudioComing{
	
	private static final int MSG_SETTING_DEL_ALARM = 0xff00;
	
	private NotificationManager mNotificationManager;  
	private Notification mNotification; 
	private Intent my_intent = new Intent("com.howell.formuseum.RECEIVER");  
	private DBManager mgr; 
	private Timer timer;
	private final WebSocketConnection mConnection = new WebSocketConnection();
	private CseqManager mCseqManager;
	private SystemUpTimeUtils systemUpTimeUtil;
//	private WebSocketConnectionManager connectionManager;
	private String username,websocket_ip,session,webServiceIP,cookieHalf,verify;
	
	private Map<String, Integer> alarmSoundMap = null;
	private AlarmSound alarmSound = null;
	private HttpProtocol hp;
	
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_SETTING_DEL_ALARM:
				Bundle bundle = msg.getData();
				delAlarm(bundle.getString("eventID"));
				break;

			default:
				break;
			}
			
			
		}
		
	};
	
	private void delAlarm(String id){
		try {
			hp.process(websocket_ip, 
					id, 
					"del_event_id="+id, 
					cookieHalf+"verifysession="+MD5.getMD5("POST:"+"/howell/ver10/data_service/Business/Informations/IO/Inputs/Channels/"+id+"/Status/Process"+":"+verify));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void sendDelAlarmMsg(String eventId){
		if (SharedPreferencesUtils.getSettingIsAutoHandleAlarm(this)) {
			Message msg = new Message();
			msg.what = MSG_SETTING_DEL_ALARM;
			Bundle bundle = new Bundle();
			bundle.putString("eventID", eventId);
			msg.setData(bundle);
			mHandler.sendMessageDelayed(msg, SharedPreferencesUtils.getSettingAutoWaitTime(this)*1000);
		}
	}
	
	
	@SuppressWarnings("deprecation")
//	private void alarmStreamFun(String id,int slot,String ip,String name,int year,int month,int day,int hour,int min,int sec){
	private void alarmStreamFun(EventNotifyRes res){
		EventNotify eventNotify = ((EventNotifyRes) res).getEventNotify();
//		Log.e("alarmStreamFun",eventNotify.toString());
    	String componentId = eventNotify.getId();
    	String name = eventNotify.getName();
    	String eventState = ((EventNotifyRes) res).getEventNotify().getEventState();
    	String eventType = eventNotify.getEventType();
    	//设置内存里设备的id(notification id)号
    	int temp_id = mgr.selectEventNotifySqlKey(eventNotify);
    	
    	//isAlarmed 0：未查看警报 1：已查看警报
    	Log.e("123", "eventState:"+eventState);
    	if(eventState.equals("Active")){
    		Log.e("alarmStreamFun", "State is Active");
    		
    		/*
    		 * 语言警报
    		 */
    		
    		alarmSound = new AlarmSound();
    		alarmSound.playSound(this,name);
//    		alarmSound.playSound(this,1);
    		
    		if(!mgr.containsEventNotify(eventNotify)){
//    			Log.e("123", "mgr add alarm");
        		mgr.addAlarmList(eventNotify);
    		}else{
//    			Log.e("123", "mgr set alarmed 0");
    			eventNotify.setIsAlarmed(0);
    		}
    		// ② 初始化Notification  
    	    int icon = R.drawable.logo;  
    	    CharSequence tickerText = "入侵警报";  
    	    long when = System.currentTimeMillis();  
    	    mNotification = new Notification(icon,tickerText,when);  
    	    mNotification.flags = Notification.FLAG_AUTO_CANCEL;  
    	    mNotification.defaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;
    	    // ③ 定义notification的消息 和 PendingIntent  
    	    
    	    
    	/*    
    	    try {
				Field field = mNotification.getClass().getDeclaredField("extraNotification");
				Object extraNotification = field.get(mNotification);
				Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
				method.invoke(extraNotification, 10);
				Log.e("123", "set method ok");
			} catch (NoSuchFieldException e) {
				Log.i("123","notification   get field error");
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
    	  */  
    	    Context context = this;  
    	    CharSequence contentTitle = name + "入侵警报";  
    	    CharSequence contentText = name + "入侵警报";  
    	    Intent notificationIntent = new Intent(this,LogoActivity.class);
    	    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	    PendingIntent contentIntent = PendingIntent.getActivity(context, temp_id, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT );  
    	    mNotification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);  
    	    Log.e("123","alarm start notivication id="+temp_id);
    	    mNotificationManager.notify(temp_id,mNotification);  
    	    
    	    //报警之后自动消警
//    	    sendDelAlarmMsg(componentId);//We do not del alarm auto.user should click the alarm icon and decide if he would handle it 
    	    
    	}else{
    		Log.e("alarmStreamFun", "State is Inactive    type="+ eventType);

    		if (DebugUtil.isDebug()) {//FIXME
    			if (eventType.equals("VMD")) {
    				return ;//FIXME
				}
			}
    		eventNotify.setIsAlarmed(1);//FIXME
//    		if(!DebugUtil.isDebug()){
//    			eventNotify.setIsAlarmed(1);//FIXME
//    		}
    			
//    		if(!eventType.equals("VMD")){
//    			eventNotify.setIsAlarmed(1);//FIXME
//    		}
    		mNotificationManager.cancel(mgr.selectEventNotifySqlKey(eventNotify));
    	}
    	mgr.updateEventNotifyAlarmFlag(eventNotify);
//    	Log.e("alarmStreamFun2",eventNotify.toString());
		//发送Action为com.howell.formuseum.RECEIVER的广播  
		my_intent.putExtra("ret", 2); 
		my_intent.putExtra("alarmCamera", eventNotify);
	    sendBroadcast(my_intent);  
		Intent intent = new Intent("com.howell.receiver.ReceiveAlarm");
		intent.putExtra("alarmCamera", eventNotify);
		intent.putExtra("session", session);
		intent.putExtra("webServiceIP", webServiceIP);
		intent.putExtra("cookieHalf", cookieHalf);
		intent.putExtra("verify", verify);
		sendBroadcast(intent);
	}
	
	private void start(final String websocket_ip,final String session) {
		
		//ws://host:8803/howell/ver10/ADC
		final String wsuri = "ws://"+websocket_ip+":8803/howell/ver10/ADC";
		//ws://host:8803/howell/ver10/ADC
//		final String wsuri = "ws://"+websocket_ip+":8803/111";
//		Log.e("", "jzh??????????");
		try {
			mConnection.connect(wsuri, new WebSocketConnectionHandler() {
		    	@Override
		    	public void onOpen() {
		    		//登录成功
		    		systemUpTimeUtil = new SystemUpTimeUtils();
//		    		connectionManager = new WebSocketConnectionManager();
		    		mCseqManager = new CseqManager();
		    		Log.e("", "Status: Connected to " + wsuri);
//		    		my_intent.putExtra("ret", 1); 
//					sendBroadcast(my_intent);  
					mConnection.sendTextMessage(WebSocketProtocolUtils.createAlarmPushConnectJSONObject(mCseqManager.getCseq(),session,username).toString());
					timer = new Timer(true);
		    		timer.schedule(new TimerTask() {
						@Override
						public void run() {
							//发送心跳给服务器1分钟1次
							mConnection.sendTextMessage(WebSocketProtocolUtils.createKeepAliveJSONObject(mCseqManager.getCseq(),systemUpTimeUtil.getSystemUpTime()).toString());
						}
					} , 60 * 1000 , 60 * 1000);
		    	}

			    @Override
			    public void onTextMessage(String payload) {
			    	Log.i("log123", "收到报警   playload="+payload);
			    	
			    
			    	
			    	try {
			    		//if handle the alarm will receive message:4//FIXME
						Object res = WebSocketProtocolUtils.parseJSONString(payload);
						if(res != null){
							if(res instanceof EventNotifyRes){
//								System.out.println("name:"+((EventNotifyRes) res).getEventNotify().getName());
								Log.e("123","eventNotify name="+((EventNotifyRes)res).getEventNotify().getName());
								
								
								
								//System.out.println("ImageUrl:"+((EventNotifyRes) res).getEventNotify().getImageUrl());
//								alarmStreamFun(((EventNotifyRes) res).getEventNotify().getId(),1,Utils.parseUrl(((EventNotifyRes) res).getEventNotify().getImageUrl()),((EventNotifyRes) res).getEventNotify().getName()
//										,((EventNotifyRes) res).getEventNotify().getDateYear(),((EventNotifyRes) res).getEventNotify().getDateMonth(),((EventNotifyRes) res).getEventNotify().getDateDay()
//										,((EventNotifyRes) res).getEventNotify().getDateHour(),((EventNotifyRes) res).getEventNotify().getDateMin(),((EventNotifyRes) res).getEventNotify().getDateSec());
								//if(((EventNotifyRes) res).getEventNotify().getEventState().equals("Active")){
								
								
//								checkAndHandleAlarm((EventNotifyRes)res);
								final EventNotifyRes thisRes =(EventNotifyRes)res;
								new Thread(){
									public void run() {
										
										Intent intent = new Intent();
										intent.setAction("com.howell.formuseum.updataReceive");
										sendBroadcast(intent);
										try {
											sleep(500);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										alarmStreamFun(thisRes);
									};
								}.start();
								
							
								
								//}
								
								mConnection.sendTextMessage(WebSocketProtocolUtils.createADCResJSONObject(((EventNotifyRes) res).getcSeq()).toString());
							}else if(res instanceof KeepAliveRes){
//								Log.e("","KeepAliveRes");
//								connectionManager.keepAlive();
							}else if(res instanceof EventHandleRes){
								//TODO message = 4;
								Log.i("123", "message="+((EventHandleRes)res).getMessage());
								EventNotifyRes notifyRes  = new EventNotifyRes();
								EventNotify notify = new EventNotify();
								notify.setId(((EventHandleRes) res).getNotice().getComponentID());
								notify.setName(((EventHandleRes) res).getNotice().getComponentName());
								notify.setEventState("Inactive");
								notify.setEventType(((EventHandleRes) res).getNotice().getNoticeType());
								notifyRes.setEventNotify(notify);
//								alarmStreamFun(notifyRes);
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
			    }
	
			    @Override
			    public void onClose(int code, String reason) {
			    	//登录失败
			        //Log.e("", "Connection lost.");
//			        Log.e("","ws close, reason:"+reason + " code:"+code);
			        systemUpTimeUtil.stopTimer();
//		    		connectionManager.stopTimer();
		    		if(timer != null){
		    			timer.cancel();
		    		}
			        if(code == 2 || code == 3){
			    		start(websocket_ip,session);
			        }
//			        my_intent.putExtra("ret", 0); 
//					sendBroadcast(my_intent);  
//			        stopSelf();
			    }
			});
		} catch (WebSocketException e) {
			Log.d("", e.toString());
		}
	}
	

	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e("service", "start");
		startForeground(100, new Notification());
		
		
		AudioAction.getInstance().registerOnAudioComing(this);
		
		if (hp==null) {
			hp = new HttpProtocol();
		}
		
		
//		systemUpTimeUtil = new SystemUpTimeUtils();
//		connectionManager = new WebSocketConnectionManager();
//		connectionManager.setOnDisconncetListener(this);
//		mCseqManager = new CseqManager();
		SharedPreferences sharedPreferences = getSharedPreferences("set",Context.MODE_PRIVATE);
		websocket_ip = sharedPreferences.getString("webserviceIp", "");
		username = sharedPreferences.getString("account", "");
		session = intent.getStringExtra("session");
		webServiceIP = intent.getStringExtra("webserviceIp");
		cookieHalf = intent.getStringExtra("cookieHalf");
		verify = intent.getStringExtra("verify");		
		initNotification();
		//初始化DBManager  
		if(mgr == null)
			mgr = new DBManager(this);  
		System.out.println("register");
		start(websocket_ip,session);
		return START_REDELIVER_INTENT;
	}
	
	@Override
	public void onCreate() {
		AudioAction.getInstance().initAudioRecord();
		AudioAction.getInstance().initAudio();
		SdCardUtil.createAlarmSoundDir();
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("service onDestory");
//		quit();
		if(timer != null)
			timer.cancel();
		if ( mConnection.isConnected() )
        {
        	mConnection.disconnect();
        }
		if(mgr != null)
			mgr.closeDB();
		if(systemUpTimeUtil != null)
			systemUpTimeUtil.stopTimer();
		
	
	
		AudioAction.getInstance().unregisterOnAudioComing(this);
		AudioAction.getInstance().deInitAudio();
		AudioAction.getInstance().deInitAudioRecord();
//		connectionManager.stopTimer();
	}
	
	private void initNotification(){
		 // ① 获取NotificationManager的引用   
        String ns = Context.NOTIFICATION_SERVICE;  
        mNotificationManager = (NotificationManager)this.getSystemService(ns);  
	}

	@Override
	public void onAudioComing() {
		AudioAction.getInstance().audioPlay();
		//talk 界面
		TalkManager.getInstance().startTalkActivity(this);
	}
	
	
	
	
	private void initAlarmSoundMap(){
		if (alarmSoundMap!=null) {
			return;
		}
		alarmSoundMap = new HashMap<String, Integer>();
		alarmSoundMap.put("", 1);
		
		
		
	}
	
	private int getAlarmSoundNum(String alarmName){
		if (alarmName==null) {
			return -1;
		}
		if(alarmSoundMap==null){
			initAlarmSoundMap();
		}
		int alarmNum = -1;
		try {
			alarmNum = alarmSoundMap.get(alarmName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return alarmNum;
	}
	
}


