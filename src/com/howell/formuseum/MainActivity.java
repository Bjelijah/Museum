//package com.howell.formuseum;
//
//import java.io.File;
//import java.io.UnsupportedEncodingException;
//import java.security.NoSuchAlgorithmException;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.annotation.SuppressLint;
//import android.app.ActivityManager;
//import android.app.ActivityManager.RunningServiceInfo;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.Bitmap;
//import android.graphics.Color;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.media.AudioFormat;
//import android.media.AudioRecord;
//import android.media.MediaRecorder;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.util.TypedValue;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.astuetz.PagerSlidingTabStrip;
//import com.howell.db.Camera;
//import com.howell.db.DBManager;
//import com.howell.protocol.HttpProtocol;
//import com.howell.protocol.entity.Floor;
//import com.howell.protocol.entity.Map;
//import com.howell.protocol.entity.MapItem;
//import com.howell.protocol.entity.MapItemList;
//import com.howell.protocol.entity.MapList;
//import com.howell.utils.CacheUtils;
//import com.howell.utils.JsonUtils;
//import com.howell.utils.MD5;
//import com.howell.utils.ScaleImageUtils;
//
///**
// * @author 霍之昊 
// *
// * 类说明
// */
//@SuppressWarnings("deprecation")
//public class MainActivity extends FragmentActivity implements OnClickListener, DialogInterface.OnKeyListener{
//	
//	//语音对讲按钮
//	private LinearLayout mTalk,mGetMaps;	
//	//google录音类
//	private AudioRecord audioRecord;  
//	private int recBufSize;  
//	//语音对讲各个参数
//	private static final int frequency = 8000;  
//	private static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;  
//	private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT; 
//	
//	//是否录放的标记  
//	boolean isRecording = false;
//	
//	//手机端发送语音数据线程
//	private RecordPlayThread thread;
//	private JNIManager jni;
//	private DBManager mgr;
//	
//	// 获取当前屏幕的密度
//	private DisplayMetrics dm;
////	int phoneWidth,phoneHeight;
//	private MyPagerAdapter adapter;
//	//楼层界面
//	private ArrayList<FloorFragment> floorFragmentList;
//	//PagerSlidingTabStrip的实例
//	private PagerSlidingTabStrip tabs;
//	private ViewPager pager;
//	private ProgressBar progressBar;
//	
//	private Dialog talkDialog;
//	private Button talkDialogBtn;
//	private TextView talkDialogTextView;
//	private Drawable online,offline;
//	
//	private List<Camera> list;
//	
//	private int isTalkConnect;
//	private String cookieHalf,verify;
//	private String webserviceIp;
//	
//	private HttpProtocol hp;
//    private MsgReceiver msgReceiver;  
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.main_activity);
//		init();
//		initAudio();
//		
//		//floorFragmentList.get(0).alarmPointStartShining("1楼A");
//	}
//	
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//		jni.audioStop();
//		jni.uninit();
//		isRecording = false;  
//		if(thread != null){
//			try {
//				thread.join();
//				thread = null;
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		if(audioRecord != null){
//			audioRecord.release();
//			audioRecord = null;
//		}
//		if(mgr != null){
//			mgr.closeDB();
//		}
//		unregisterReceiver(msgReceiver);
//	}
//	
//	//对PagerSlidingTabStrip的各项属性进行赋值。
//	private void setTabsValue() {
//		// 设置Tab是自动填充满屏幕的
//		tabs.setShouldExpand(true);
//		// 设置Tab的分割线是透明的
//		tabs.setDividerColor(Color.TRANSPARENT);
//		// 设置Tab底部线的高度
//		tabs.setUnderlineHeight((int) TypedValue.applyDimension(
//				TypedValue.COMPLEX_UNIT_DIP, 1, dm));
//		// 设置Tab Indicator的高度
//		tabs.setIndicatorHeight((int) TypedValue.applyDimension(
//				TypedValue.COMPLEX_UNIT_DIP, 4, dm));
//		// 设置Tab标题文字的大小
//		tabs.setTextSize((int) TypedValue.applyDimension(
//				TypedValue.COMPLEX_UNIT_SP, 16, dm));
//		// 设置Tab Indicator的颜色
//		tabs.setIndicatorColor(Color.parseColor("#45c01a"));
//		// 设置选中Tab文字的颜色 (这是我自定义的一个方法)
//		tabs.setSelectedTextColor(Color.parseColor("#45c01a"));
//		// 取消点击Tab时的背景色
//		tabs.setTabBackground(0);
//	}
//	
//	//初始化界面
//	private void init(){
//		Intent intent = getIntent();
//		isTalkConnect = intent.getIntExtra("isTalkConnect", 0);
//		webserviceIp = intent.getStringExtra("webserviceIp");
//		cookieHalf = intent.getStringExtra("cookieHalf");
//		verify = intent.getStringExtra("verify");
//		String session = intent.getStringExtra("session");
//		hp = new HttpProtocol();
//		online = this.getResources().getDrawable(R.drawable.online);
//		offline = this.getResources().getDrawable(R.drawable.offline);
//		initTalkDialog();
//		mTalk = (LinearLayout)findViewById(R.id.btn_talk);
//		mTalk.setOnClickListener(this);
//		mGetMaps = (LinearLayout)findViewById(R.id.btn_get_maps);
//		mGetMaps.setOnClickListener(this);
//		progressBar = (ProgressBar)findViewById(R.id.progress_get_maps);
//		jni = new JNIManager();
//		
//		//初始化DBManager  
//        mgr = new DBManager(this); 
//		
//		pager = (ViewPager) findViewById(R.id.pager);
//		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
//		
//		//创建地图文件夹
//		CacheUtils.createBitmapDir();
//		
////		phoneWidth = PhoneConfigUtils.getPhoneWidth(this);
////		phoneHeight = PhoneConfigUtils.getPhoneHeight(this);
//		
//		dm = getResources().getDisplayMetrics();
//		floorFragmentList = new ArrayList<FloorFragment>();
//		
////		ViewPager pager = (ViewPager) findViewById(R.id.pager);
////		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
//		//如果缓存文件夹里有地图文件，则直接从本地读取
//		if(CacheUtils.getMapPathFromSD().size() > 0){
//			setMaps();
//		}
//		progressBar.setVisibility(View.GONE);
//		
//		//动态注册广播接收器  
//        msgReceiver = new MsgReceiver();  
//        IntentFilter intentFilter = new IntentFilter();  
//        intentFilter.addAction("com.howell.formuseum.RECEIVER");  
//        registerReceiver(msgReceiver, intentFilter);
//        
//        //如果Service没有被开启则startService并传入nvr_ip
//        intent = new Intent(this, MyService.class);
//        if(isServiceRun(this)){
//        	Log.e("", "stop service");
//        	stopService(intent);
//		}
//    	intent.putExtra("session", session);
//		startService(intent); 
//		
//		updateAlarmSignal();
//	}
//	
//	//判断Service是否在运行
//	public boolean isServiceRun(Context context){
//		int serviceCount = 100;
//		int addCount = 100;
//		ActivityManager am = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);
//		List<RunningServiceInfo> list = am.getRunningServices(serviceCount);
//		while(list.size() == serviceCount){
//			System.out.println("list.size():"+list.size());
//			serviceCount += addCount;
//			list = null;
//			list = am.getRunningServices(serviceCount);
//		}
//		System.out.println("service count:"+list.size());
//		for(RunningServiceInfo info : list){
//			System.out.println(info.service.getClassName());
//			if(info.service.getClassName().equals("com.howell.formuseum.MyService")){
//				System.out.println("isServiceRun true");
//			    return true;
//			}
//		}
//		System.out.println("isServiceRun false");
//		return false;
//	}
//	
//	private void initTalkDialog(){
//		talkDialog = new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar);
//		talkDialog.setContentView(R.layout.talk_dialog);
//		talkDialogBtn = ((Button)talkDialog.findViewById(R.id.talk_dialog_btn));
//		talkDialogTextView = ((TextView)talkDialog.findViewById(R.id.talk_dialog_textview));
////      ((TextView) lDialog.findViewById(R.id.dialog_title)).setText(pTitle);
//		talkDialog.findViewById(R.id.talk_dialog_btn).setOnClickListener(MainActivity.this);
//		talkDialog.setOnKeyListener(MainActivity.this);
//		
//		if(isTalkConnect == 1){
//			//语音对讲登陆成功
//			talkDialogTextView.setText("状态：在线");
//			talkDialogTextView.setCompoundDrawablesWithIntrinsicBounds (null,null,online,null);
//		}else{
//			//语音对讲登录失败
//			talkDialogTextView.setText("状态：离线");
//			talkDialogTextView.setCompoundDrawablesWithIntrinsicBounds (null,null,offline,null);
//		}
//		//lDialog.show();
//	}
//	
//	private void initViewPager(){
////		adapter = new MyPagerAdapter(getSupportFragmentManager(),floorFragmentList);
////		pager.setAdapter(adapter);
////		tabs.setViewPager(pager);
////		setTabsValue();
////		adapter.setFragments(floorFragmentList);
//		adapter = new MyPagerAdapter(getSupportFragmentManager());
//		pager.setAdapter(adapter);
//		tabs.setViewPager(pager);
//		setTabsValue();
//	}
//	
//	//18.149
//	private void setMaps(ArrayList<Floor> list ){
//		System.out.println("setMaps");
//		floorFragmentList.clear();
//		for(int i = 0 ; i < list.size() ; i++){
//			FloorFragment f = new FloorFragment();
//			f.setContext(this);
//			Drawable drawable = new BitmapDrawable(ScaleImageUtils.decodeByteArray(704, 382, list.get(i).getMapBitmap()));
//			f.setFloorMap(drawable);
//			f.setFloorName(list.get(i).getName());
//			f.setFloorId(list.get(i).getId());
//			
//			for(MapItem item : list.get(i).getItemList()){
//				double x = item.getCoordinate().getX();
//				double y = item.getCoordinate().getY();
//				f.createAlarmPoint(this,getResources().getDrawable(R.drawable.ic_launcher),item.getComponentId(), (float)x, (float)y);
//			}
//			floorFragmentList.add(f);
//		}
//		
////		
////		FloorFragment f = new FloorFragment();
////		Drawable drawable = new BitmapDrawable(b);
////		f.setFloorMap(drawable);
////		f.setFloorTag(map.getMap().get(0).getName());
//////		f.createNewAlarmThreadList();
//////		f.createNewAlarmPointList();
////		
////		x =  0.801;
////		y = 0.36;
////		f.createAlarmPoint(this,getResources().getDrawable(R.drawable.ic_launcher),"1楼B",(float)x, (float)y);
////		x = 0.678;
////		y = 0.704;
////		f.createAlarmPoint(this,getResources().getDrawable(R.drawable.ic_launcher),"1楼C",(float)x, (float)y);
////		
////		FloorFragment f2 = new FloorFragment();
////		f2.setFloorMap(getResources().getDrawable(R.drawable.second_floor));
////		f2.setFloorTag("2楼");
//////		f2.createNewAlarmThreadList();
//////		f2.createNewAlarmPointList();
////		x = 0.278;
////		y = 0.215;
////		f2.createAlarmPoint(this,getResources().getDrawable(R.drawable.ic_launcher),"2楼A", (float)x, (float)y);
////		x = 0.735;
////		y = 0.208;
////		f2.createAlarmPoint(this,getResources().getDrawable(R.drawable.ic_launcher),"2楼B", (float)x, (float)y);
////		
////		floorFragmentList.add(f);
////		floorFragmentList.add(f2);
////		
////		if(i == 1){
////			FloorFragment f3 = new FloorFragment();
////			f3.setFloorMap(getResources().getDrawable(R.drawable.second_floor));
////			f3.setFloorTag("3楼");
//////			f3.createNewAlarmThreadList();
//////			f3.createNewAlarmPointList();
////			x = 0.278;
////			y = 0.215;
////			f3.createAlarmPoint(this,getResources().getDrawable(R.drawable.ic_launcher),"3楼A", (float)x, (float)y);
////			x = 0.735;
////			y = 0.208;
////			f3.createAlarmPoint(this,getResources().getDrawable(R.drawable.ic_launcher),"3楼B", (float)x, (float)y);
////			
////			floorFragmentList.add(f3);
////		}
////		
////		i++;
//		
//		initViewPager();
//	}
//	
//	private void setMaps(){
//		System.out.println("setMaps");
//		floorFragmentList.clear();
//		ArrayList<Camera> mapItems = (ArrayList<Camera>) mgr.query();
//		
//		for(String s : CacheUtils.getMapIdFromSD()){
//			System.out.println(s);
//			FloorFragment f = new FloorFragment();
//			f.setContext(this);
//			Drawable drawable = new BitmapDrawable(ScaleImageUtils.decodeFile(704, 382, new File(CacheUtils.getMapPathFromSD(s))));
//			f.setFloorMap(drawable);
//			for(Camera c : mapItems){
//				System.out.println(c.toString());
//				if(c.mapId.equals(s)){
//					f.setFloorName(c.mapName);
//					f.setFloorId(c.mapId);
//					double x = c.xPosition;
//					double y = c.yPosition;
//					f.createAlarmPoint(this,getResources().getDrawable(R.drawable.ic_launcher),c.componentId, (float)x, (float)y);
//				}
//			}
//			floorFragmentList.add(f);
//		}
//		initViewPager();
//	}
//	
//	//获取数据库里有报警的子模块
//	private void updateAlarmSignal(){
//		list = mgr.query();  
//		for(Camera c : list){
//			System.out.println("摄像机名："+c.name);
//			System.out.println("c.isShown:"+c.isShown);
//			if(c.isShown == 1){//有报警
//				hasAlarmSignal(c);
//			}
//		}
//	}
//	
//	//查询子模块属于那张map并闪烁
//	public void hasAlarmSignal(Camera c){
//		for(FloorFragment f : floorFragmentList){
//			if(f.getFloorId().equals(c.mapId)){
//				System.out.println("f:"+f.getFloorId());
//				f.alarmPointStartShining(c.componentId);
//				return;
//			}
//		}
//	}
//	
//	public class MyPagerAdapter extends FragmentPagerAdapter {
//
//		private FragmentManager fm;
//		public MyPagerAdapter(FragmentManager fm) {
//			super(fm);
//			this.fm = fm;
//		}
//		
//		@Override
//		public CharSequence getPageTitle(int position) {
//			System.out.println("getPageTitle position:"+position);
//			System.out.println(floorFragmentList.get(position).getFloorName());
//			return floorFragmentList.size() == 0 ? "" : floorFragmentList.get(position).getFloorName();
//		}
//
//		@Override
//		public int getCount() {
//			return floorFragmentList.size();
//		}
//
//		@Override
//		public Fragment getItem(int position) {
//			System.out.println("getItem");
//			return floorFragmentList.get(position);
//		}
//		
//	}
//	
//	//初始化语音对讲
//	private void initAudio(){
//		recBufSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);  
//		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,  
//              channelConfiguration, audioEncoding, recBufSize);  
//		jni.audioPlay();
//	}
//	
//	class RecordPlayThread extends Thread {  
//        public void run() {  
//            try {  
//                byte[] buffer = new byte[recBufSize];  
//                audioRecord.startRecording();//开始录制  
////                audioTrack.play();//开始播放  
//                System.out.println("isRecording:"+isRecording);
//                while (isRecording) { 
//                    //从MIC保存数据到缓冲区  
//                    int bufferReadResult = audioRecord.read(buffer, 0, recBufSize);  
//                    System.out.println("bufferReadResult:"+bufferReadResult);
//  
//                    byte[] tmpBuf = new byte[bufferReadResult];  
//                    System.arraycopy(buffer, 0, tmpBuf, 0, bufferReadResult);  
//                    //写入数据即播放  
//                    int ret = jni.startTalk(tmpBuf, bufferReadResult);
//                    System.out.println("startTalk ret :"+ret);
//                }  
//                audioRecord.stop();  
//                jni.stopTalk();
//            } catch (Throwable t) {  
//                //Toast.makeText(Talk.this, t.getMessage(), 1000);  
//            }  
//        }  
//    };  
//	
//	@SuppressLint("ShowToast")
//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		switch(v.getId()){
//		case R.id.btn_talk:
//			talkDialog.show();
//			
//			break;
//		case R.id.talk_dialog_btn:
//			Button btn = (Button)v;
//			btn.setBackgroundResource(R.drawable.reject_button_sel);
//			btn.setText("停止对讲");
//			if(!isRecording){
//				jni.requestTalk();
//				isRecording = true;  
//				if(thread == null){
//					thread = new RecordPlayThread();
//					thread.start();
//				} 
//			}else{
//				btn.setBackgroundResource(R.drawable.reject_button);
//				btn.setText("按下对讲");
//				isRecording = false;
//				if(thread != null){
//					try {
//						thread.join();
//						thread = null;
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//			break;
//		case R.id.btn_get_maps:
//			if(CacheUtils.getMapPathFromSD().size() > 0){
//				Toast.makeText(MainActivity.this, "地图信息已是最新", 1000).show();
//				return;
//			}
//			progressBar.setVisibility(View.VISIBLE);
//			new AsyncTask<Void, Integer, Void>() {
//				Bitmap bmp;
//				MapList mapList = null;
//				ArrayList<Floor> list = new ArrayList<Floor>();
//				MapItemList mapItemList = new MapItemList();
//				
//				@Override
//				protected Void doInBackground(Void... arg0) {
//					// TODO Auto-generated method stub
//					try {
//						//从平台获取各楼地图
//						mapList = JsonUtils.parseMapsJsonObject(new JSONObject(hp.maps(webserviceIp, 1, 10,cookieHalf+"verifysession="+MD5.getMD5("GET:"+"/howell/ver10/data_service/management/System/Maps:"+verify))));
//						for(Map map : mapList.getMap()){
//							byte[] bitmap = hp.mapsData(webserviceIp, map.getId(), cookieHalf+"verifysession="+MD5.getMD5("GET:"+"/howell/ver10/data_service/management/System/Maps/"+map.getId()+"/Data:"+verify));
//							bmp = ScaleImageUtils.decodeByteArray(704, 382, bitmap); 
//							//储存各地图到本地
//							CacheUtils.saveBmpToSd(bmp, map.getId());
//							//从平台获取各楼摄像机位置
//							mapItemList = JsonUtils.parseMapsItemJsonObject(new JSONObject(hp.items(webserviceIp, map.getId(), 1,10,cookieHalf+"verifysession="+MD5.getMD5("GET:"+"/howell/ver10/data_service/management/System/Maps/"+map.getId()+"/Items:"+verify))));
//							//储存各子地图到本地
//							for(MapItem item : mapItemList.getMapItem()){
//								mgr.add(new Camera(item.getId(),item.getCoordinate().getX(),item.getCoordinate().getY(),map.getId(),map.getName(),item.getComponentId()));
//							}
//							list.add(new Floor(map.getId(), map.getName(), bitmap,mapItemList.getMapItem()));
//							bmp.recycle();
//							bmp = null;
//						}
//					} catch (NoSuchAlgorithmException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (UnsupportedEncodingException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					return null;
//				}
//				
//				@Override
//				protected void onPostExecute(Void result) {
//					// TODO Auto-generated method stub
//					super.onPostExecute(result);
//					//设置各楼地图
//					//设置各楼摄像机位置
//					setMaps(list);
//					progressBar.setVisibility(View.GONE);
//				}
//				
//			}.execute();
//			break;
//		}
//	}
//
//	@Override
//	public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
//			if(event.getAction() == event.ACTION_UP){
//				System.out.println("ACTION_UP");
//			}else if(event.getAction() == event.ACTION_DOWN){
//				System.out.println("ACTION_DOWN");
//				talkDialogBtn.setText("按下对讲");
//				talkDialogBtn.setBackgroundResource(R.drawable.reject_button);
//				isRecording = false;
//				if(thread != null){
//					thread = null;
//				}
//			}
//		}
//		return false;
//	}
//	
//	/** 
//     * 广播接收器 
//     * @author huo 
//     * 
//     */  
//    public class MsgReceiver extends BroadcastReceiver{  
//        @Override  
//        public void onReceive(Context context, Intent intent) {  
//        	System.out.println("收到广播！！！");
//        	//ret:0 登录失败 1 登录成功 2 有报警  -2 其它
//        	int ret = intent.getIntExtra("ret", -2); 
//        	System.out.println("login ret :"+ret);
//        	if(ret == 0){
//        		Dialog alertDialog = new AlertDialog.Builder(MainActivity.this).   
//        	            setTitle("登录失败").   
//        	            setMessage("登录失败，请重新登录").   
//        	            setIcon(R.drawable.expander_ic_minimized).   
//        	            setPositiveButton("确定", new DialogInterface.OnClickListener() {   
//        	                @Override   
//        	                public void onClick(DialogInterface dialog, int which) {   
//        	                    // TODO Auto-generated method stub  
//        	                	finish();
//        	                }   
//        	            }).   
//        	    create();   
//        		alertDialog.show();   
//        	}else if(ret == 2){
//        		//获取数据库数据
//        		updateAlarmSignal();
//        	}else if(ret == -1){
//        		
//        	}
//        }  
//          
//    }
//
//}
