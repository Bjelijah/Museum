package com.howell.formuseum;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.howell.protocol.HttpProtocol;
import com.howell.protocol.entity.EventNotify;
import com.howell.utils.CacheUtils;
import com.howell.utils.MD5;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class AlarmDetailActivity extends Activity implements OnTouchListener{
	private TextView mEventName/*,mEventState*/,mEventTime;
	private LinearLayout mPictures;
	private FrameLayout mPlayback,mPreview,mHandleAlarm;
	private EventNotify eventNotify;
	
	//private LinearLayout mTalk;
	
	private String webserviceIp,session,cookieHalf,verify;
	private HttpProtocol hp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_detail);
		init();
		
	}
	
	private void init(){
		mEventName = (TextView)findViewById(R.id.tv_alarm_detail_name);
		//mEventState = (TextView)findViewById(R.id.tv_alarm_detail_event_state);
		mEventTime = (TextView)findViewById(R.id.tv_alarm_detail_event_time);
		mPictures = (LinearLayout)findViewById(R.id.ll_alarm_detail_pictures);
		mPlayback = (FrameLayout)findViewById(R.id.fl_alarm_detail_playback);
		mPlayback.setOnTouchListener(this);
		//mTalk = (LinearLayout)findViewById(R.id.ll_alarm_detail_talk);
		//mTalk.setOnTouchListener(this);
		mPreview = (FrameLayout)findViewById(R.id.fl_alarm_detail_preview);
		mPreview.setOnTouchListener(this);
		mHandleAlarm = (FrameLayout)findViewById(R.id.fl_alarm_detail_handle_alarm);
		mHandleAlarm.setOnTouchListener(this);
		
		hp = new HttpProtocol();
		
		Intent intent = getIntent();
		webserviceIp = intent.getStringExtra("webserviceIp");
		session = intent.getStringExtra("session");
		cookieHalf = intent.getStringExtra("cookieHalf");
		verify = intent.getStringExtra("verify");
		eventNotify = (EventNotify) intent.getSerializableExtra("eventNotify");
		Log.e("eventNotify", eventNotify.toString());
		if(eventNotify.getName() == null){
			mEventName.setText("");
		}else{
			mEventName.setText(eventNotify.getName().toString());
		}
		/*if(eventNotify.getEventState() == null){
			mEventState.setText("");
		}else{
			mEventState.setText(eventNotify.getEventState().toString());
		}*/
		String time = eventNotify.getTime();
		mEventTime.setText(eventNotify.getDateYear(time)+"-"+eventNotify.getDateMonth(time)+"-"
				+eventNotify.getDateDay(time)+" "+eventNotify.getDateHour(time)+":"+eventNotify.getDateMin(time)
				+":"+eventNotify.getDateSec(time));
		
		if(eventNotify.getImageUrl() != null){
			String[] imgs = eventNotify.convertStringToArray(eventNotify.getImageUrl());
			for(String s : imgs){
				System.out.println("s:"+s);
			}
			addImgs(imgs);
		}
	}
	
	private void addImgs(String[] imgs){
		if(imgs == null){
			Log.e("addImgs", "addImgs is null");
			return;
		}
		CacheUtils.removeCache(CacheUtils.getPictureCachePath());
		//for(final String s : imgs){
		for(int i = 0 ; i < imgs.length ; i++){
			final ImageView imageView = new ImageView(this);
			imageView.setTag(i);
			imageView.setImageResource(R.drawable.empty_bg);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(128,128);
			layoutParams.setMargins(0, 0, 10, 0);
			imageView.setLayoutParams(layoutParams); 
			mPictures.addView(imageView);
			
			GetPictureTask task = new GetPictureTask(imgs[i],imageView,String.valueOf(i));
			task.execute();
			
			imageView.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(AlarmDetailActivity.this,PictureActivity.class);
					intent.putExtra("position", Integer.valueOf(imageView.getTag().toString()));
					startActivity(intent);
					return false;
				}
			});
			
			
			/*new AsyncTask<Void, Integer, Void>() {
				private InputStream is;
				private Bitmap bitmap;
				@Override
				protected Void doInBackground(Void... arg0) {
					// TODO Auto-generated method stub
					URL url;
					try {
						url = new URL(imgs[i]);
						is = url.openStream();
						bitmap = BitmapFactory.decodeStream(is);  
				        is.close();
				        
				        CacheUtils.cachePictures(bitmap, "");
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
					return null;
				}
				
				protected void onPostExecute(Void result) {
					imageView.setImageBitmap(bitmap);  
				};
				
			}.execute();*/
		}
	}
	
	class GetPictureTask extends AsyncTask<Void, Integer, Void>{

		private InputStream is;
		private Bitmap bitmap;
		
		private String img;
		private	ImageView imageView;
		private String fileName;
		
		public GetPictureTask(String img,ImageView imageView,String fileName) {
			// TODO Auto-generated constructor stub
			this.img = img;
			this.imageView = imageView;
			this.fileName = fileName;
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			URL url;
			try {
				Log.e(fileName, img);
				url = new URL(img);
				is = url.openStream();
				bitmap = BitmapFactory.decodeStream(is);  
		        is.close();
		        
		        CacheUtils.cachePictures(bitmap, fileName);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			return null;
		}
		
		protected void onPostExecute(Void result) {
			imageView.setImageBitmap(bitmap);  
		};
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.fl_alarm_detail_preview://预览
			Intent intent = new Intent(this,PlayerActivity.class);
			intent.putExtra("session", session);
			intent.putExtra("cookieHalf", cookieHalf);
			intent.putExtra("verify", verify);
			intent.putExtra("webserviceIp", webserviceIp);
			intent.putExtra("eventNotify", eventNotify);
			intent.putExtra("isPlayBack", false);
			startActivity(intent);
			break;
		case R.id.fl_alarm_detail_playback://回放
			System.out.println("playback");
			intent = new Intent(this,PlayerActivity.class);
			intent.putExtra("session", session);
			intent.putExtra("cookieHalf", cookieHalf);
			intent.putExtra("verify", verify);
			intent.putExtra("webserviceIp", webserviceIp);
			intent.putExtra("eventNotify", eventNotify);
			intent.putExtra("isPlayBack", true);
			startActivity(intent);
			break;
//		case R.id.ll_alarm_detail_talk:	//语音对讲
//			intent = new Intent(this,TalkActivity.class);
//			startActivity(intent);
//			break;
		case R.id.fl_alarm_detail_handle_alarm://处理警报
			LayoutInflater layoutInflater = LayoutInflater.from(this);
			final View view = layoutInflater.inflate(R.layout.process_dialog, null);
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("请输入处理结果");
			dialog.setView(view);
			dialog.setPositiveButton("确定", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					final EditText et = (EditText) view.findViewById(R.id.process_dialog_edittext);
					Log.e("process", et.getText().toString());
					//发送报警处理协议
					new Thread(){
						public void run() {
							try {
								hp.process(webserviceIp, eventNotify.getId(), et.getText().toString(), cookieHalf+"verifysession="+MD5.getMD5("POST:"+"/howell/ver10/data_service/Business/Informations/IO/Inputs/Channels/"+eventNotify.getId()+"/Status/Process"+":"+verify));
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
						};
					}.start();
				}
			});
			dialog.setNegativeButton("取消", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					arg0.dismiss();
				}
			});
			dialog.show();
			break;
		default:
			break;
		}
		return false;
	}
}
