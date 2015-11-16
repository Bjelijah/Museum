package com.howell.utils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.howell.protocol.entity.Coordinate;
import com.howell.protocol.entity.Device;
import com.howell.protocol.entity.EventLinkage;
import com.howell.protocol.entity.EventLinkageList;
import com.howell.protocol.entity.ExceptionData;
import com.howell.protocol.entity.Fault;
import com.howell.protocol.entity.Map;
import com.howell.protocol.entity.MapItem;
import com.howell.protocol.entity.MapItemList;
import com.howell.protocol.entity.MapList;
import com.howell.protocol.entity.Page;
import com.howell.protocol.entity.PlaybackTask;
import com.howell.protocol.entity.ServerNonce;
import com.howell.protocol.entity.VideoPlaybackIdentifier;
import com.howell.protocol.entity.VideoPreviewIdentifier;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class JsonUtils {
	//Nonce
	public static ServerNonce parseNonceJsonObject(JSONObject param) throws JSONException{
		if(param == null){
			return null;
		}
		String nonce = param.getString("Nonce");
		String domain = param.getString("Domain");
		System.out.println("nonce:"+nonce+",domian:"+domain);
		return new ServerNonce(nonce,domain);
	}
	
	//Authenticate
	public static JSONObject createAuthenticateJsonObject(String userName,String nonce,String domain,String clientNonce,String verifySession) throws JSONException{
		JSONObject param = new JSONObject();  
		param.put("UserName", userName);  
		param.put("Nonce", nonce);  
		param.put("Domain", domain);
		param.put("ClientNonce", clientNonce); 
		param.put("VerifySession", verifySession); 
		return param;
	}
	
	public static Fault parseAuthenticateJsonObject(JSONObject param) throws JSONException{
		if(param == null){
			return null;
		}
		int faultCode = param.getInt("FaultCode");
		String faultReason = param.getString("FaultReason");
		String message = null,exceptionType = null;
		try{
			JSONObject exception = param.getJSONObject("Exception");
			message = exception.getString("Message");
			exceptionType = exception.getString("ExceptionType");
		}catch(Exception e){
			System.out.println("exception is null");
		}
		String id = null;
		try{
		id = param.getString("Id");
		}catch(Exception e){
			System.out.println("id is null");
		}
		return new Fault(faultCode,faultReason,new ExceptionData(message,exceptionType),id);
	}
	
	public static MapList parseMapsJsonObject(JSONObject param) throws JSONException{
		if(param == null){
			return null;
		}
		JSONObject page = param.getJSONObject("Page");
		int pageIndex = page.getInt("PageIndex");
		int pageSize = page.getInt("PageSize");
		int pageCount = page.getInt("PageCount");
		int recordCount = page.getInt("RecordCount");
		int totalRecordCount = page.getInt("TotalRecordCount");
		
		ArrayList<Map> maps = new ArrayList<Map>();
		JSONArray map = param.getJSONArray("Map");
		for (int i = 0; i < map.length(); i++) {  
			JSONObject item = map.getJSONObject(i); // 得到每个对象  
			String id = item.getString("Id"); 
			String name = item.getString("Name"); 
			String comment = "";
			try{
				comment = item.getString("Comment"); 
			}catch(Exception e){
				System.out.println("comment is null");
			}
			String mapFormat = item.getString("MapFormat"); 
			maps.add(new Map(id,name,comment,mapFormat));
		}
		return new MapList(new Page(pageIndex, pageSize,pageCount,recordCount,totalRecordCount),maps);
	}
	
	public static MapItemList parseMapsItemJsonObject(JSONObject param) throws JSONException{
		if(param == null){
			return null;
		}
		JSONObject page = param.getJSONObject("Page");
		int pageIndex = page.getInt("PageIndex");
		int pageSize = page.getInt("PageSize");
		int pageCount = page.getInt("PageCount");
		int recordCount = page.getInt("RecordCount");
		int totalRecordCount = page.getInt("TotalRecordCount");
		
		ArrayList<MapItem> maps = new ArrayList<MapItem>();
		JSONArray map = param.getJSONArray("MapItem");
		for (int i = 0; i < map.length(); i++) {  
			JSONObject item = map.getJSONObject(i); // 得到每个对象  
			String id = item.getString("Id"); 
			String itemType = item.getString("ItemType"); 
			String componentId = item.getString("ComponentId"); 
			JSONObject coordinate = item.getJSONObject("Coordinate");
			
			Double x = coordinate.getDouble("X");
			Double y = coordinate.getDouble("Y");
			Double angle = (double) 0;
			try{
				angle = item.getDouble("Angle");
			}catch(Exception e){
				System.out.println("angle is null");
			}
			maps.add(new MapItem(id,itemType,componentId,new Coordinate(x, y),angle));
		}
		return new MapItemList(new Page(pageIndex, pageSize,pageCount,recordCount,totalRecordCount),maps);
	}
	
	public static EventLinkageList parseEventLinkageListJsonObject(JSONObject param) throws JSONException{
		if(param == null){
			return null;
		}
		JSONObject page = param.getJSONObject("Page");
		int pageIndex = page.getInt("PageIndex");
		int pageSize = page.getInt("PageSize");
		int pageCount = page.getInt("PageCount");
		int recordCount = page.getInt("RecordCount");
		int totalRecordCount = page.getInt("TotalRecordCount");
		
		ArrayList<EventLinkage> eventLinkages = new ArrayList<EventLinkage>();
		JSONArray eventLinkage = param.getJSONArray("EventLinkage");
		for (int i = 0; i < eventLinkage.length(); i++) {  
			JSONObject item = eventLinkage.getJSONObject(i); // 得到每个对象  
			String componentId = item.getString("ComponentId"); 
			String eventType = item.getString("EventType"); 
			String eventState = item.getString("EventState"); 
			
			ArrayList<VideoPreviewIdentifier> videoPreviewIdentifiers = new ArrayList<VideoPreviewIdentifier>();
			try{
				JSONArray videoPreviewIdentifier = param.getJSONArray("VideoPreviewIdentifier");
				for (int j = 0; j < videoPreviewIdentifier.length(); j++) {  
					JSONObject item_videoPreviewIdentifier = videoPreviewIdentifier.getJSONObject(j); // 得到每个对象  
					String videoInputChannelId = item_videoPreviewIdentifier.getString("VideoInputChannelId");
					int streamNo = item_videoPreviewIdentifier.getInt("StreamNo");
					//String protocol = item_videoPreviewIdentifier.getString("Protocol");
					videoPreviewIdentifiers.add(new VideoPreviewIdentifier(videoInputChannelId,streamNo));
				}
			}catch(Exception e){
				Log.e("", "videoPreviewIdentifier is null");
			}
			ArrayList<VideoPlaybackIdentifier> videoPlaybackIdentifiers = new ArrayList<VideoPlaybackIdentifier>();
			try{
				JSONArray videoPlaybackIdentifier = param.getJSONArray("VideoPlaybackIdentifier");
				for (int j = 0; j < videoPlaybackIdentifier.length(); j++) {  
					JSONObject item_videoPlaybackIdentifier = videoPlaybackIdentifier.getJSONObject(j); // 得到每个对象  
					String videoInputChannelId = item_videoPlaybackIdentifier.getString("VideoInputChannelId");
					int streamNo = item_videoPlaybackIdentifier.getInt("StreamNo");
					String protocol = item_videoPlaybackIdentifier.getString("Protocol");
					int beginTime = item_videoPlaybackIdentifier.getInt("BeginTime");
					int endTime = item_videoPlaybackIdentifier.getInt("EndTime");
					videoPlaybackIdentifiers.add(new VideoPlaybackIdentifier(videoInputChannelId,streamNo,protocol,beginTime,endTime));
				}
			}catch(Exception e){
				Log.e("", "videoPlaybackIdentifier is null");
			}
			
			eventLinkages.add(new EventLinkage(componentId,eventType,eventState,videoPreviewIdentifiers));
		}
		
		return new EventLinkageList(new Page(pageIndex, pageSize,pageCount,recordCount,totalRecordCount),eventLinkages);
	}
	
	public static EventLinkage parseEventLinkageJsonObject(JSONObject param) throws JSONException{
		if(param == null){
			return null;
		}
		String componentId = param.getString("ComponentId"); 
		String eventType = param.getString("EventType"); 
		String eventState = param.getString("EventState"); 
		
		ArrayList<VideoPreviewIdentifier> videoPreviewIdentifiers = new ArrayList<VideoPreviewIdentifier>();
		try{
			JSONArray videoPreviewIdentifier = param.getJSONArray("VideoPreviewIdentifier");
			for (int j = 0; j < videoPreviewIdentifier.length(); j++) {  
				JSONObject item_videoPreviewIdentifier = videoPreviewIdentifier.getJSONObject(j); // 得到每个对象  
				String videoInputChannelId = item_videoPreviewIdentifier.getString("VideoInputChannelId");
				int streamNo = item_videoPreviewIdentifier.getInt("StreamNo");
				//String protocol = item_videoPreviewIdentifier.getString("Protocol");
				videoPreviewIdentifiers.add(new VideoPreviewIdentifier(videoInputChannelId,streamNo));
			}
		}catch(Exception e){
			Log.e("", "videoPreviewIdentifier is null");
		}
			
//		ArrayList<VideoPlaybackIdentifier> videoPlaybackIdentifiers = new ArrayList<VideoPlaybackIdentifier>();
//		JSONArray videoPlaybackIdentifier = param.getJSONArray("VideoPlaybackIdentifier");
//		for (int j = 0; j < videoPlaybackIdentifier.length(); j++) {  
//			JSONObject item_videoPlaybackIdentifier = videoPlaybackIdentifier.getJSONObject(j); // 得到每个对象  
//			String videoInputChannelId = item_videoPlaybackIdentifier.getString("VideoInputChannelId");
//			int streamNo = item_videoPlaybackIdentifier.getInt("StreamNo");
//			String protocol = item_videoPlaybackIdentifier.getString("Protocol");
//			int beginTime = item_videoPlaybackIdentifier.getInt("BeginTime");
//			int endTime = item_videoPlaybackIdentifier.getInt("EndTime");
//			videoPlaybackIdentifiers.add(new VideoPlaybackIdentifier(videoInputChannelId,streamNo,protocol,beginTime,endTime));
//		}
			
		return new EventLinkage(componentId,eventType,eventState,videoPreviewIdentifiers);
	}
	
	public static PlaybackTask parsePlaybackTaskJsonObject(JSONObject param) throws JSONException{
		if(param == null){
			return null;
		}
		
		String taskId = param.getString("TaskId"); 
		String videoInputChannelId = param.getString("VideoInputChannelId"); 
		String url = param.getString("Url"); 
		String protocol = param.getString("Protocol"); 
		String sDP = "";
		try{
			sDP = param.getString("SDP"); 
		}catch(Exception e){
			Log.e("PlaybackTask", "SDP is null");
		}
		
		return new PlaybackTask(taskId,videoInputChannelId,url,protocol,sDP);
	}
	
	public static Device parseDeviceJsonObject(JSONObject param) throws JSONException{
		if(param == null){
			return null;
		}
		
		String name = param.getString("Name"); 
		String url = param.getString("Uri"); 
		
		return new Device(name,url);
	}
	
	public static JSONObject createProcessJsonObject(String process) throws JSONException{
		JSONObject param = new JSONObject();  
		param.put("Description", process); 
		return param;
	}
}
