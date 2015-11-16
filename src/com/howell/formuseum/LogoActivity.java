package com.howell.formuseum;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.howell.protocol.HttpProtocol;
import com.howell.protocol.entity.ClientCredential;
import com.howell.protocol.entity.Fault;
import com.howell.protocol.entity.ServerNonce;
import com.howell.utils.DialogUtils;
import com.howell.utils.JsonUtils;
import com.howell.utils.MD5;
import com.howell.utils.SharedPreferencesUtils;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class LogoActivity extends Activity {
	private String account;
	private String password;
	private String webserviceIp;
	
	private String createClientNonce(int length){
		String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";  
        Random random = new Random();  
        StringBuffer sb = new StringBuffer();  
        for(int i=0;i<length;i++){  
            int number =random.nextInt(62);  
            sb.append(str.charAt(number));  
        }  
        return sb.toString();  
	}
	
	private boolean login(){
		HttpProtocol hp = new HttpProtocol();
		Fault fault = null;
		String cookieHalf = null;
		String password2 = null;
		
		try {
			//获取session
			ServerNonce sn= JsonUtils.parseNonceJsonObject(new JSONObject(hp.nonce(webserviceIp,account)));
			if(sn != null){
				String clientNonce = createClientNonce(32);
				String md5 = MD5.getMD5(password);
				//UserName@Domain:Nonce:ClientNonce:MD5(Password)
				password2 = account+"@"+sn.getDomain()+":"+sn.getNonce()+":"+clientNonce+":"+md5;
				ClientCredential clientCredential = new ClientCredential(account,sn.getNonce(),sn.getDomain(),clientNonce,MD5.getMD5(password2));
				fault = JsonUtils.parseAuthenticateJsonObject(new JSONObject(hp.authenticate(webserviceIp,clientCredential)));
				Log.e("fault", fault.toString());
				//Cookie: username =admin; sid=会话Id; domain=192.109.10.21;verifysession=MD5(METHOD:URL:Verifysession)
//				String cookie = "username="+account+";sid="+fault.getId()+";domain="+sn.getDomain()+";verifysession="+MD5.getMD5("GET:"+"/howell/ver10/data_service/management/System/Maps:"+MD5.getMD5(password2));
				cookieHalf = "username="+account+";sid="+fault.getId()+";domain="+sn.getDomain()+";";
			}
			
			if(fault != null && fault.getId() != null){
				Intent intent = new Intent(LogoActivity.this,MapListActivity.class);
				intent.putExtra("session", fault.getId());
				intent.putExtra("cookieHalf", cookieHalf);
				try {
					intent.putExtra("verify", MD5.getMD5(password2));
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				intent.putExtra("webserviceIp", webserviceIp);
				startActivity(intent);
				LogoActivity.this.finish();
			}else{
				return false;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logo);
		new AsyncTask<Void, Integer, Void>() {
			
			private static final int LOGIN_NOW = 1;
			private static final int LOGIN_AFTER = 2;
			private int loginMethod;
			
			@Override
			protected Void doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(1000);
					account = SharedPreferencesUtils.getAccount(LogoActivity.this);
					password = SharedPreferencesUtils.getPassword(LogoActivity.this);
					webserviceIp = SharedPreferencesUtils.getWebserviceIp(LogoActivity.this);
					Log.e("", "account:"+account+" password:"+password+ " webserviceIp:"+webserviceIp);
					if(account.equals("") && password.equals("") && webserviceIp.equals("")){
						//直接进入地图列表页面
						loginMethod = LOGIN_AFTER;
						
					}else{
						//进入登录页面
						loginMethod = LOGIN_NOW;
						if(!login()){
							loginMethod = LOGIN_AFTER;
						}
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(loginMethod == LOGIN_AFTER){
					Intent intent = new Intent(LogoActivity.this,LoginActivity.class);
					startActivity(intent);
					LogoActivity.this.finish();
				}
			}
		}.execute();
		
	}
}
