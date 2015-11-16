package com.howell.formuseum;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.howell.db.DBManager;
import com.howell.protocol.CseqManager;
import com.howell.protocol.entity.EventNotify;
import com.howell.protocol.entity.EventNotifyRes;
import com.howell.protocol.entity.KeepAliveRes;
import com.howell.utils.SystemUpTimeUtils;
import com.howell.utils.WebSocketConnectionManager;
import com.howell.utils.WebSocketProtocolUtils;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;

public class MyService extends Service{
	
	private NotificationManager mNotificationManager;  
	private Notification mNotification; 
	private Intent my_intent = new Intent("com.howell.formuseum.RECEIVER");  
	private DBManager mgr; 
	private Timer timer;
	private final WebSocketConnection mConnection = new WebSocketConnection();
	private CseqManager mCseqManager;
	private SystemUpTimeUtils systemUpTimeUtil;
//	private WebSocketConnectionManager connectionManager;
	private String username,websocket_ip,session;
	
	@SuppressWarnings("deprecation")
//	private void alarmStreamFun(String id,int slot,String ip,String name,int year,int month,int day,int hour,int min,int sec){
	private void alarmStreamFun(EventNotifyRes res){
		EventNotify eventNotify = ((EventNotifyRes) res).getEventNotify();
		Log.e("alarmStreamFun",eventNotify.toString());
    	String componentId = eventNotify.getId();
    	String name = eventNotify.getName();
//    	String time = ((EventNotifyRes) res).getEventNotify().getTime();
//    	int year = ((EventNotifyRes) res).getEventNotify().getDateYear(time);
//    	int month = ((EventNotifyRes) res).getEventNotify().getDateMonth(time);
//    	int day = ((EventNotifyRes) res).getEventNotify().getDateDay(time);
//    	int hour = ((EventNotifyRes) res).getEventNotify().getDateHour(time);
//    	int min = ((EventNotifyRes) res).getEventNotify().getDateMin(time);
//    	int sec = ((EventNotifyRes) res).getEventNotify().getDateSec(time);
//    	String eventType = ((EventNotifyRes) res).getEventNotify().getEventType();
//    	String eventState = ((EventNotifyRes) res).getEventNotify().getEventState();
//    	String pictureUrl = "";
//    	if(((EventNotifyRes) res).getEventNotify().getImageUrl() != null)
//    		pictureUrl = ((EventNotifyRes) res).getEventNotify().getImageUrl();
//    	
//		System.out.println("alarmStreamFun componentId:" + componentId + ",name:" + name );
    	//isAlarmed 0：未查看警报 1：已查看警报
    	if(!mgr.containsEventNotify(eventNotify)){
    		mgr.addAlarmList(eventNotify);
		}else{
			eventNotify.setIsAlarmed(0);
			mgr.updateEventNotifyAlarmFlag(eventNotify);
		}
		
//		final Camera camera = new Camera(componentId,name,pictureUrl,eventType,eventState,year,month,day,hour,min,sec);
//		if(!mgr.containsElement(camera)){
//			mgr.add(camera);  
//		}else{
//		camera.isShown = 1;
//		mgr.updateCameraIsShownFlag(camera);
//		}
		//设置内存里设备的id(notification id)号
		int temp_id = mgr.selectEventNotifySqlKey(eventNotify);
//		if(temp_id != -1){
//			camera._id = tempC._id;
//			System.out.println("camera id:"+camera._id);
//			tempC = null;
//		}
//		System.out.println("camera:"+camera.toString());
			
		//发送Action为com.howell.formuseum.RECEIVER的广播  
		my_intent.putExtra("ret", 2); 
		my_intent.putExtra("alarmCamera", eventNotify);
	    sendBroadcast(my_intent);  
	        
	    // ② 初始化Notification  
	    int icon = R.drawable.logo;  
	    CharSequence tickerText = "入侵警报";  
	    long when = System.currentTimeMillis();  
	    mNotification = new Notification(icon,tickerText,when);  
	    mNotification.flags = Notification.FLAG_AUTO_CANCEL;  
	    mNotification.defaults = Notification.DEFAULT_VIBRATE| Notification.DEFAULT_SOUND;
			
	    // ③ 定义notification的消息 和 PendingIntent  
	    Context context = this;  
	    CharSequence contentTitle = name + "入侵警报";  
	    CharSequence contentText = name + "入侵警报";  
	    Intent notificationIntent = new Intent(this,LogoActivity.class);
	    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//	        notificationIntent.putExtra("ip", ip);
//	        notificationIntent.putExtra("name", name);
//	        System.out.println("alarmStreamFun2 slot:"+slot + " ip:"+ip + " name:"+name);
	    PendingIntent contentIntent = PendingIntent.getActivity(context, temp_id, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT );  
	    mNotification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);  
	    mNotificationManager.notify(temp_id,mNotification);  
//		System.out.println("alarmStreamFun camera._id:"+camera._id + " ip:"+ip + " name:"+name);
//		}
	}
	
	private void start(final String websocket_ip,final String session) {
		
		//ws://host:8803/howell/ver10/ADC
		final String wsuri = "ws://"+websocket_ip+":8803/howell/ver10/ADC";
		//ws://host:8803/howell/ver10/ADC
//		final String wsuri = "ws://"+websocket_ip+":8803/111";
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
							// TODO Auto-generated method stub
							//发送心跳给服务器3分钟1次
							mConnection.sendTextMessage(WebSocketProtocolUtils.createKeepAliveJSONObject(mCseqManager.getCseq(),systemUpTimeUtil.getSystemUpTime()).toString());
						}
					} , 60 * 1000 , 60 * 1000);
		    	}

			    @Override
			    public void onTextMessage(String payload) {
			    	Log.e("", "Got echo: " + payload);
			    	try {
						Object res = WebSocketProtocolUtils.parseJSONString(payload);
						if(res != null){
							if(res instanceof EventNotifyRes){
								System.out.println("name:"+((EventNotifyRes) res).getEventNotify().getName());
								//System.out.println("ImageUrl:"+((EventNotifyRes) res).getEventNotify().getImageUrl());
//								alarmStreamFun(((EventNotifyRes) res).getEventNotify().getId(),1,Utils.parseUrl(((EventNotifyRes) res).getEventNotify().getImageUrl()),((EventNotifyRes) res).getEventNotify().getName()
//										,((EventNotifyRes) res).getEventNotify().getDateYear(),((EventNotifyRes) res).getEventNotify().getDateMonth(),((EventNotifyRes) res).getEventNotify().getDateDay()
//										,((EventNotifyRes) res).getEventNotify().getDateHour(),((EventNotifyRes) res).getEventNotify().getDateMin(),((EventNotifyRes) res).getEventNotify().getDateSec());
								alarmStreamFun((EventNotifyRes)res);
								
								mConnection.sendTextMessage(WebSocketProtocolUtils.createADCResJSONObject(((EventNotifyRes) res).getcSeq()).toString());
							}else if(res instanceof KeepAliveRes){
//								Log.e("","KeepAliveRes");
//								connectionManager.keepAlive();
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
	
			    @Override
			    public void onClose(int code, String reason) {
			    	//登录失败
			        Log.e("", "Connection lost.");
			        Log.e("","reason:"+reason + " code:"+code);
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
//		// TODO Auto-generated method stub
		Log.e("service", "start");
		startForeground(100, new Notification());
//		systemUpTimeUtil = new SystemUpTimeUtils();
//		connectionManager = new WebSocketConnectionManager();
//		connectionManager.setOnDisconncetListener(this);
//		mCseqManager = new CseqManager();
		SharedPreferences sharedPreferences = getSharedPreferences("set",Context.MODE_PRIVATE);
		websocket_ip = sharedPreferences.getString("webserviceIp", "");
		username = sharedPreferences.getString("account", "");
		session = intent.getStringExtra("session");
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
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
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
//		connectionManager.stopTimer();
	}
	
	private void initNotification(){
		 // ① 获取NotificationManager的引用   
        String ns = Context.NOTIFICATION_SERVICE;  
        mNotificationManager = (NotificationManager)this.getSystemService(ns);  
	}

}


