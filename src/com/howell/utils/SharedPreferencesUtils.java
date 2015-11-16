package com.howell.utils;

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
}
