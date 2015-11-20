package com.howell.formuseum;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.howell.db.DBManager;
import com.howell.ehlib.MyListView;
import com.howell.ehlib.MyListView.OnRefreshListener;
import com.howell.protocol.HttpProtocol;
import com.howell.protocol.entity.Map;
import com.howell.protocol.entity.MapItem;
import com.howell.protocol.entity.MapItemList;
import com.howell.protocol.entity.MapList;
import com.howell.utils.CacheUtils;
import com.howell.utils.JsonUtils;
import com.howell.utils.MD5;
import com.howell.utils.Utils;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class MapListActivity extends Activity implements OnRefreshListener,OnItemClickListener,OnClickListener{
	
	private MyListView listview;
	//private LinearLayout mTalk;
	private HttpProtocol hp;
	private DBManager mgr;
	
	private ArrayList<Map> mapList;
	private MapListAdapter adapter;
	
	//协议相关
	private String webserviceIp,session,cookieHalf,verify;
	
	private MsgReceiver msgReceiver; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_list);
		init();
		registerReceiver();
		initService();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		updateAlarm();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mgr != null){
			mgr.closeDB();
		}
		unregisterReceiver(msgReceiver);
	}
	
	private void registerReceiver(){
		msgReceiver = new MsgReceiver();  
	    IntentFilter intentFilter = new IntentFilter();  
	    intentFilter.addAction("com.howell.formuseum.RECEIVER");  
	    registerReceiver(msgReceiver, intentFilter);
	}
	
	private void initService(){
		Intent intent = new Intent(this, MyService.class);
		if(isServiceRun(this)){
	      	Log.e("", "stop service");
	      	stopService(intent);
		}
		intent.putExtra("session", session);
		startService(intent); 
	}
	
	//判断Service是否在运行
	public boolean isServiceRun(Context context){
		int serviceCount = 100;
		int addCount = 100;
		@SuppressWarnings("static-access")
		ActivityManager am = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> list = am.getRunningServices(serviceCount);
		while(list.size() == serviceCount){
			System.out.println("list.size():"+list.size());
			serviceCount += addCount;
			list = null;
			list = am.getRunningServices(serviceCount);
		}
		System.out.println("service count:"+list.size());
		for(RunningServiceInfo info : list){
			System.out.println(info.service.getClassName());
			if(info.service.getClassName().equals("com.howell.formuseum.MyService")){
				System.out.println("isServiceRun true");
			    return true;
			}
		}
		System.out.println("isServiceRun false");
		return false;
	}
	
	private void init(){
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		listview = (MyListView)findViewById(R.id.map_list_listview);
		listview.setonRefreshListener(this);
		listview.setOnItemClickListener(this);
		//mTalk = (LinearLayout)findViewById(R.id.ll_map_list_talk);
		//mTalk.setOnClickListener(this);
		hp = new HttpProtocol();
		
		Intent intent = getIntent();
		webserviceIp = intent.getStringExtra("webserviceIp");
		session = intent.getStringExtra("session");
		cookieHalf = intent.getStringExtra("cookieHalf");
		verify = intent.getStringExtra("verify");
		
		mapList = new ArrayList<Map>();
		adapter = new MapListAdapter(this,mapList);
		listview.setAdapter(adapter);
	}
	
	private boolean compareString(String s1,String s2){
		return s1.equals(s2);
	}
	
	private boolean isMapUpdate(ArrayList<Map> newMaps,ArrayList<Map> oldMaps){
		if(newMaps.size() != oldMaps.size()){
			//平台传入的地图列表个数与之前不一样，重新更新
			Log.e("","有更新");
			return true;
		}else{
			//平台传入的地图列表个数与之前一样，检查每个map的md5码和子模块更新时间
			for(int i = 0 ; i < newMaps.size() ; i ++){
				if(!compareString(newMaps.get(i).getMD5Code(),oldMaps.get(i).getMD5Code())
						|| !compareString(newMaps.get(i).getLastModificationTime(),oldMaps.get(i).getLastModificationTime())){
					Log.e("","有更新");
					return true;
				}
			}
		}
		Log.e("","没有更新");
		return false;
	}
	
	private void getMaps(){
//		int width = PhoneConfigUtils.getPhoneWidth(this);
//		int height = PhoneConfigUtils.getPhoneHeight(this);
		try {
			//获取地图
			MapList maps = JsonUtils.parseMapsJsonObject(new JSONObject(hp.maps(webserviceIp, 1, 10,cookieHalf+"verifysession="+MD5.getMD5("GET:"+"/howell/ver10/data_service/management/System/Maps:"+verify))));
			for(Map map : maps.getMap()){
//				byte[] data = hp.mapsData(webserviceIp, map.getId(), cookieHalf+"verifysession="+MD5.getMD5("GET:"+"/howell/ver10/data_service/management/System/Maps/"+map.getId()+"/Data:"+verify));
				//存于文件中
//				CacheUtils.saveBmpToSd(ScaleImageUtils.decodeByteArray(width, height, data), map.getId());
				Map m = new Map(map.getId(),map.getName(),map.getComment(),map.getMapFormat(),CacheUtils.getBitmapCachePath()+map.getId(),map.getMD5Code(),map.getLastModificationTime());
				mapList.add(m);
				//存于数据库中
				mgr.addMap(m);
				
				//获取地图子模块
				MapItemList mapItems = JsonUtils.parseMapsItemJsonObject(new JSONObject(hp.items(webserviceIp, map.getId(), 1,10,cookieHalf+"verifysession="+MD5.getMD5("GET:"+"/howell/ver10/data_service/management/System/Maps/"+map.getId()+"/Items:"+verify))));
				for(MapItem item : mapItems.getMapItem()){
					item.setMapId(map.getId());
//					Log.e("debug", "debug:"+item.toString());
					mgr.addMapItem(item);
				}
			}
			
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
	
	private void updateMaps(){
		mgr.deleteTable("map");
		mgr.deleteTable("map_item");
		mgr.deleteTable("alarm_list");
		mapList.clear();
		CacheUtils.removeCache(CacheUtils.getBitmapCachePath());
		getMaps();
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		new AsyncTask<Void, Integer, Void>() {

			@Override
			protected Void doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				updateMaps();
				return null;
			}
			
			protected void onPostExecute(Void result) {
				//adapter.notifyDataSetChanged();
				adapter.setMapList(mapList);
				updateAlarm();
				listview.onRefreshComplete();
			};
			
		}.execute();
	}

	@Override
	public void onFirstRefresh() {
		// TODO Auto-generated method stub
		mgr = new DBManager(this);
		mapList = mgr.queryMap();
		MapList maps;
		try {
			maps = JsonUtils.parseMapsJsonObject(new JSONObject(hp.maps(webserviceIp, 1, 10,cookieHalf+"verifysession="+MD5.getMD5("GET:"+"/howell/ver10/data_service/management/System/Maps:"+verify))));
			//判断是否有更新
			if(isMapUpdate(maps.getMap(), mapList)){
				updateMaps();
			}
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
		
		
		//if(mapList.size() == 0){
			//Toast.makeText(MapListActivity.this, "第一次加载可能需要几分钟时间，请耐心等待", 1000).show();
			//从平台获取地图信息
		//	getMaps();
		//}
	}

	@Override
	public void onFirstRefreshDown() {
		// TODO Auto-generated method stub
		//adapter.notifyDataSetChanged();
		adapter.setMapList(mapList);
		updateAlarm();
		listview.onRefreshComplete();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add("退出登录");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this,LoginActivity.class);
		startActivity(intent);
		finish();
		return super.onOptionsItemSelected(item);
	}
	
	class MapListAdapter extends BaseAdapter {

		private Context mContext;
		private ArrayList<Map> mapList;
		
		public MapListAdapter(Context context,ArrayList<Map> mapList) {
	        this.mContext = context;
	        this.mapList = mapList;
	    }
		
		public void setMapList(ArrayList<Map> mapList) {
			this.mapList = mapList;
		}

		@Override
	    public int getCount() {
	        // TODO Auto-generated method stub
	        return mapList.size() ;
	    }

	    @Override
	    public Object getItem(int position) {
	        // TODO Auto-generated method stub
	        return mapList.get(position) ;
	    }

	    @Override
	    public long getItemId(int position) {
	        // TODO Auto-generated method stub
	        return position;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        // TODO Auto-generated method stub
	    	System.out.println("getView:"+position);
	    	ViewHolder holder = null;
	    	if (convertView == null) {
	    		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
	    		convertView = layoutInflater.inflate(R.layout.map_list_item, null);
				holder = new ViewHolder();
					
				holder.mapName = (TextView)convertView.findViewById(R.id.tv_map_list_item);
				holder.alarmIcon = (ImageView)convertView.findViewById(R.id.map_list_item_alarm_icon);
	            convertView.setTag(holder);
	    	}else{
	         	holder = (ViewHolder)convertView.getTag();
	        }
	    	holder.mapName.setText(Utils.utf8Togb2312(new String(Base64.decode(mapList.get(position).getName(),0))));
	    	if(mapList.get(position).isHasAlarm()){
	    		holder.alarmIcon.setVisibility(View.VISIBLE);
	    	}else{
	    		holder.alarmIcon.setVisibility(View.GONE);
	    	}
			return convertView;
	    }
	    
	    class ViewHolder {
	        public TextView mapName;
	        public ImageView alarmIcon;
	    }
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
		// TODO Auto-generated method stub
		mapList.get((int)arg3).setHasAlarm(false);
		adapter.notifyDataSetChanged();
		Intent intent = new Intent(this,MapActivity.class);
		intent.putExtra("map", mapList.get((int)arg3));
		intent.putExtra("session", session);
		intent.putExtra("cookieHalf", cookieHalf);
		intent.putExtra("verify", verify);
		intent.putExtra("webserviceIp", webserviceIp);
		startActivity(intent);
	}

	private void updateAlarm(){
		for(Map map : mapList){
			if(mgr.hasAlarmWithMapId(map.getId())){
				map.setHasAlarm(true);
			}else{
				map.setHasAlarm(false);
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	/** 
	   * 广播接收器 
	   * @author huo 
	   * 
	   */  
	public class MsgReceiver extends BroadcastReceiver{  
		@Override  
		public void onReceive(Context context, Intent intent) {  
			System.out.println("收到广播！！！");
			//ret:0 登录失败 1 登录成功 2 有报警  -2 其它
			int ret = intent.getIntExtra("ret", -2); 
			System.out.println("login ret :"+ret);
			if(ret == 2){
				//获取数据库数据
				updateAlarm();
			}
		}  
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
//		case R.id.ll_map_list_talk:
//			Intent intent = new Intent(this,TalkActivity.class);
//			startActivity(intent);
//			break;

		default:
			break;
		}
	}
}
