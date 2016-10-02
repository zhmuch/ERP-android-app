package com.nearu.grierp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.nearu.grierp.R;

public class QualityCheckActivity extends QualityCheckBaseActivity{
	
	private Button btnConfirm = null;
	private Button btnBack = null;
	private EditText etZLDHLen = null;
	private RadioButton rbFirstCheck = null;
	private RadioButton rbProcessChcek = null;
	/**
	 *  0: first check
	 *   1: process check
	 */
	private int which; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isGestureable = true;
		initView();
	}
	public void initView(){
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.qulity_scene_one);
		btnConfirm = (Button) findViewById(R.id.btn_sceneone_confirm);
		btnBack = (Button) findViewById(R.id.btn_back2);
		etZLDHLen = (EditText) findViewById(R.id.et_zldh_len);
		etZLDHLen.setText("10");
		btnBack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				QualityCheckActivity.this.finish();
				return;
			}
			
		});
		btnConfirm.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				nextScene();
			}
			
		});
	}
	public void onRadioButtonClicked(View view) {
		boolean checked = ((RadioButton) view).isChecked();
		switch(view.getId()){
		case R.id.rb_first_check:
			if(checked)
				which = 0;
			break;
		case R.id.rb_process_check:
			if(checked)
				which = 1;
			break;
		}
	}
	@Override
	protected void nextScene() {
		int len = Integer.parseInt(etZLDHLen.getText().toString());
		Intent intent = new Intent(QualityCheckActivity.this, QualityCheckSceneTwo.class);
		intent.putExtra("len", len);
		intent.putExtra("mode", which);
		QualityCheckActivity.this.startActivity(intent);
		
	}
	@Override
	protected void preScene() {
		// TODO Auto-generated method stub
		
	}
}
