package com.howell.formuseum;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.howell.protocol.Const;
import com.howell.utils.TalkManager;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class TalkActivity extends Activity implements OnClickListener,Const{
	private ImageView mImgState,mTalkBtn;
	private TextView mTvState,mTvState2;
	
	private int talkState;	//语音对讲当前状态
	private int registerState; //与语音平台连接状态
	
	private TalkManager talkManager;
	private static final int STATE_CHANGE_TO_TALKING = 1;
	private static final int SET_REGISTER_UI = 2;
	
	//google录音类
	private AudioRecord audioRecord;  
	private int recBufSize;  
	//语音对讲各个参数
	private static final int frequency = 8000;  
	private static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;  
	private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	
	//是否录放的标记  
	//boolean isRecording = false;
	private RecordPlayThread recordPlayThread;
	private DetectRegisterStateThread detectRegisterStateThread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.talk);
		init();
		initAudioRecord();
		checkConnect();
		setState();
		detectRegisterState();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(talkState == REQUEST){
			talkManager.stopTalk();
		}
		talkState = STOP;  
		if(recordPlayThread != null){
			//recordPlayThread.join();
			recordPlayThread = null;
		}
		if(audioRecord != null){
			audioRecord.release();
			audioRecord = null;
		}
		if(detectRegisterStateThread != null){
			detectRegisterStateThread = null;
		}
	}
	
	private void init(){
		mImgState = (ImageView)findViewById(R.id.iv_talk_state);
		mTalkBtn = (ImageView)findViewById(R.id.iv_talk_btn);
		mTalkBtn.setOnClickListener(this);
		mTvState = (TextView)findViewById(R.id.tv_talk_state);
		mTvState2 = (TextView)findViewById(R.id.tv_talk_state2);
		talkManager = TalkManager.getInstance();
		talkManager.test();
	}
	
	private void checkConnect(){
		if(talkManager.get_register_state() == 1){	//连接状态
			registerState = CONNECT_SERVICE;
		}else{
			registerState = DISCONNECT_SERVICE;
		}
		
		//talkState = STOP;
	}
	
	private void setState(){
		if(registerState == CONNECT_SERVICE){
			mImgState.setImageResource(R.drawable.online_b);
			mTvState.setText(getResources().getString(R.string.online));
			mTalkBtn.setImageResource(R.drawable.talk_red);
			mTvState2.setText(getResources().getString(R.string.press_talk_btn_state_1));
		}else if(registerState == DISCONNECT_SERVICE){
			mImgState.setImageResource(R.drawable.offline_b);
			mTvState.setText(getResources().getString(R.string.offline));
			mTalkBtn.setImageResource(R.drawable.talk_disconnect);
			mTvState2.setText(getResources().getString(R.string.disconnect_2_talk_service));
			
		}
		talkState = STOP;
	}
	
	private void detectRegisterState(){
		if(detectRegisterStateThread == null){
			detectRegisterStateThread = new DetectRegisterStateThread();
			detectRegisterStateThread.start();
		}
	}
	
	class DetectRegisterStateThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			while(!isFinishing()){
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int tempState = 0;
				if(talkManager.get_register_state() == 1){	//连接状态
					tempState = CONNECT_SERVICE;
				}else{
					tempState = DISCONNECT_SERVICE;
				}
				Log.v("DetectRegisterStateThread", "tempState："+tempState+",registerState:"+registerState);
				if(tempState != registerState){//状态改变
					registerState = tempState;
					handler.sendEmptyMessage(SET_REGISTER_UI);
				}
			}
		}
	}
	
	class CheckTalkStateThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			int voice_state = 0;
			while(talkState == REQUEST && voice_state != 2){
				voice_state = talkManager.getTalkState();
				if(voice_state == 2){	//成功
					talkState = TALKING;
					handler.sendEmptyMessage(STATE_CHANGE_TO_TALKING);
					
					//setData
					if(recordPlayThread == null){
						recordPlayThread = new RecordPlayThread();
						recordPlayThread.start();
					}
					
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case STATE_CHANGE_TO_TALKING:
				mTalkBtn.setImageResource(R.drawable.talk_green);
				mTvState2.setText(getResources().getString(R.string.press_talk_btn_state_3));
				break;
			case SET_REGISTER_UI:
				setState();
				break;
			default:
				break;
			}
		}
	};
	
	//初始化语音对讲
	public void initAudioRecord(){
		recBufSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);  
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,  
              channelConfiguration, audioEncoding, recBufSize);  
	}
	
	class RecordPlayThread extends Thread {  
		public void run() {  
			try {  
				byte[] buffer = new byte[recBufSize];  
				audioRecord.startRecording();//开始录制  
//          audioTrack.play();//开始播放  
//				System.out.println("isRecording:"+isRecording);
				while (talkState == TALKING) { 
	              //从MIC保存数据到缓冲区  
	              int bufferReadResult = audioRecord.read(buffer, 0, recBufSize);  
	              System.out.println("bufferReadResult:"+bufferReadResult);
	
	              byte[] tmpBuf = new byte[bufferReadResult];  
	              System.arraycopy(buffer, 0, tmpBuf, 0, bufferReadResult);  
	              //写入数据即播放  
	              int ret = talkManager.setData(tmpBuf, bufferReadResult);
	              System.out.println("startTalk ret :"+ret);
				}  
				audioRecord.stop();  
				talkManager.stopTalk();
			} catch (Throwable t) {  
				//Toast.makeText(Talk.this, t.getMessage(), 1000);  
      		}  
		}  
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_talk_btn:
			if(registerState == CONNECT_SERVICE){
				if(talkState == STOP){
					talkState = REQUEST;
					mTalkBtn.setImageResource(R.drawable.talk_yellow);
					mTvState2.setText(getResources().getString(R.string.press_talk_btn_state_2));
					//请求连接
//					talkManager.setIs_voice_start();
					talkManager.requestTalk();
					CheckTalkStateThread thread = new CheckTalkStateThread();
					thread.start();
					
				}else if(talkState == TALKING){
					talkState = STOP;
					mTalkBtn.setImageResource(R.drawable.talk_red);
					mTvState2.setText(getResources().getString(R.string.press_talk_btn_state_1));
//					talkManager.setIs_voice_start(0);
					if(recordPlayThread != null){
						try {
							recordPlayThread.join();
							recordPlayThread = null;
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}else{
				Toast.makeText(this, "与语音平台断开连接，正在重连", 1000).show();
			}
			
			break;

		default:
			break;
		}
	}
}
