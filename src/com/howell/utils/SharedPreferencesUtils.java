package com.howell.utils;

import com.howell.formuseum.bean.SettingParamInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class SharedPreferencesUtils {
	private static final String spName = "login_set";
	private static final String spAlarm = "alarm_set";
	private static final String spLoginTalk = "login_talk";
	private static final String spSetting = "setting_set";
	public static void saveLoginInfo(Context mContext,String account,String password,String webserviceIp){
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(spName, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("account", account);
        editor.putString("password", password);
        editor.putString("webserviceIp", webserviceIp);
        editor.commit();
	}
	
	public static String getAccount(Context mContext){
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(spName,Context.MODE_PRIVATE);
		return sharedPreferences.getString("account", "");
	}
	
	public static String getPassword(Context mContext){
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(spName,Context.MODE_PRIVATE);
		return sharedPreferences.getString("password", "");
	}
	
	public static String getWebserviceIp(Context mContext){
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(spName,Context.MODE_PRIVATE);
		return sharedPreferences.getString("webserviceIp", "");
	}
	
	public static void setAlarm(Context context,boolean bAlarm){
		SharedPreferences sharedPreferences = context.getSharedPreferences(spAlarm, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("isalarm", bAlarm);
		editor.commit();
	}
	public static boolean getIsAlarm(Context context){
		SharedPreferences sharedPreferences=context.getSharedPreferences(spAlarm, context.MODE_PRIVATE);
		return sharedPreferences.getBoolean("isalarm", false);
	}
	
	public static void saveLoginTalkInfo(Context context,String userName,String userPwd,String ip,String uuid,String mac){
		SharedPreferences sharedPreferences = context.getSharedPreferences(spLoginTalk, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("username", userName);
        editor.putString("userpwd", userPwd);
        editor.putString("webserviceIp", ip);
        editor.putString("uuid", uuid);
        editor.putString("mac", mac);
        editor.commit();
	}
	
	public static String getTalkUserName(Context context){
		SharedPreferences sharedPreferences=context.getSharedPreferences(spLoginTalk, Context.MODE_PRIVATE);
		return sharedPreferences.getString("username", "");
	}
	
	public static String getTalkUserPwd(Context context){
		SharedPreferences sharedPreferences=context.getSharedPreferences(spLoginTalk, Context.MODE_PRIVATE);
		return sharedPreferences.getString("userpwd", "");
	}
	
	public static String getTalkIp(Context context){
		SharedPreferences sharedPreferences=context.getSharedPreferences(spLoginTalk, Context.MODE_PRIVATE);
		return sharedPreferences.getString("webserviceIp", "");
	}
	
	public static String getTalkUuid(Context context){
		SharedPreferences sharedPreferences=context.getSharedPreferences(spLoginTalk, Context.MODE_PRIVATE);
		return sharedPreferences.getString("uuid", "");
	}

	
	public static String getTalkMac(Context context){
		SharedPreferences sharedPreferences=context.getSharedPreferences(spLoginTalk, Context.MODE_PRIVATE);
		return sharedPreferences.getString("mac", "");
	}
	
	public static void saveSettingAutoHandleAlarm(Context context, boolean bAuto){
		SharedPreferences sp = context.getSharedPreferences(spSetting, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean("autoHandleAlarm", bAuto);
		editor.commit();	
	}
	
	public static boolean getSettingIsAutoHandleAlarm(Context c){
		SharedPreferences sp = c.getSharedPreferences(spSetting, Context.MODE_PRIVATE);
		return sp.getBoolean("autoHandleAlarm", false);
	}
	
	public static void saveSettingAutoWaitTime(Context context, int second){
		SharedPreferences sp = context.getSharedPreferences(spSetting, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt("autoWaitTime", second);
		editor.commit();
	}
	
	public static int getSettingAutoWaitTime(Context c){
		SharedPreferences sp = c.getSharedPreferences(spSetting, Context.MODE_PRIVATE);
		return sp.getInt("autoWaitTime", 60);
	}
	
	public static void saveSettingInfo(Context c,SettingParamInfo info){
		SharedPreferences sp = c.getSharedPreferences(spSetting, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean("autoHandleAlarm", info.isbAuto());
		editor.putInt("autoWaitTime", info.getWaitTime());
		editor.commit();
	}
	
}
