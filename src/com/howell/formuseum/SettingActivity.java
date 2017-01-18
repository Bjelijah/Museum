package com.howell.formuseum;

import com.howell.formuseum.bean.SettingParamInfo;
import com.howell.utils.SharedPreferencesUtils;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ToggleButton;

public class SettingActivity extends Activity {
	
	ToggleButton mAutoTb;
	EditText mAutoEt;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		mAutoTb = (ToggleButton) findViewById(R.id.setting_auto_tb);
		mAutoTb.setChecked(SharedPreferencesUtils.getSettingIsAutoHandleAlarm(this));
		mAutoEt = (EditText) findViewById(R.id.setting_waittime_et);
		mAutoEt.setText(SharedPreferencesUtils.getSettingAutoWaitTime(this)+"");
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		SettingParamInfo info = new SettingParamInfo();
		info.setbAuto(mAutoTb.isChecked());
		info.setWaitTime( Integer.valueOf(mAutoEt.getText().toString()));
		SharedPreferencesUtils.saveSettingInfo(this, info);
		super.onStop();
	}
	
}
