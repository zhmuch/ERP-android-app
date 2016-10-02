package com.nearu.grierp.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.nearu.grierp.R;
import com.nearu.grierp.adapter.tzAdapter;
import com.nearu.grierp.comm.Constants;
import com.nearu.grierp.web.service.DailyProdService;

public class DailyProdStepTwoActivity extends DailyProdBaseActivity {
	
	public static final String TAG = "DailyProdStepTwoActivity";
	private EditText et_scsl = null;
	private EditText et_zldh = null;
	private ListView lv_tz = null;
	private CheckBox rb_all = null;
	private String scsl;
	private String zldh;
	private String ph;
	private String pih;
	private String ywgs;
	private String hsgs;
	private boolean isAll;
	private ArrayList<String[]> tzList = new ArrayList<String[]>();
	private tzAdapter mAdapter = null;
	private Handler mHandler = null;
	private int tzIndex = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = this.getIntent();
		scsl = intent.getExtras().getString("scsl");
		zldh = intent.getExtras().getString("zldh");
		ph = intent.getExtras().getString("ph");
		pih = intent.getExtras().getString("pih");
		ywgs = intent.getExtras().getString("ywgs");
		hsgs = intent.getExtras().getString("hsgs");
		Log.d(TAG, "scsl is "+ scsl);
		mHandler = new MyHandler();
		loadServer();
		service = new DailyProdService(server1, server2, serverStatus);
		initView();
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		getTZList();
	}
	public static final String[][] head = 
		{{"TZ_NO", "制程", "说明", "未完工", "可根产"},
		{"TZ_NO","Process", "description", "unfinished", "may root production" }}; 
	private void initView() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.daily_prod_step_2);
		et_scsl = (EditText) findViewById(R.id.et_scsl);
		et_zldh = (EditText) findViewById(R.id.et_zldh);
		lv_tz = (ListView) findViewById(R.id.lv_tz);
		rb_all = (CheckBox) findViewById(R.id.rb_all);
		Button btnBack = (Button) findViewById(R.id.btn_back);
		btnBack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				DailyProdStepTwoActivity.this.finish();
			}
		});
		rb_all.setChecked(false);
		DecimalFormat format = new DecimalFormat("0.00");
		et_scsl.setText(format.format(Float.parseFloat(scsl)));
		et_zldh.setText(zldh);	
		tzList.add(head[locale]);
		mAdapter = new tzAdapter(this,tzList);
		lv_tz.setAdapter(mAdapter);
		isAll = false;
		rb_all.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {
				isAll = checked;	
				getTZList();
			}
			
		});
		lv_tz.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				if (pos == 0) {
					return;
				}
				tzIndex = pos;
				nextStep();
			}
			
		});
		getTZList();
	}
	private ArrayList<String[]> tempTZList = null;
	private void getTZList() {
		new Thread() {
			public void run() {
				sendMsg(Constants.SCRB_GET_TZLIST);
				String comp = cookie.getString("compNo", "");
				tempTZList = service.getTZList(comp, zldh, isAll);
				if (tempTZList == null) {
					sendMsg(Constants.SCRB_GET_TZLIST_FL);
					return;
				}
				
				sendMsg(Constants.SCRB_GET_TZLIST_OK);
			}
		}.start();
	}
	private void sendMsg(int what) {
		Message msg = new Message();
		msg.what = what;
		mHandler.sendMessage(msg);
	}
	
	private ProgressDialog dialog = null;
	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case Constants.SCRB_GET_TZLIST:
				String [][] text = {{"提示", "正在获取通知单"},{"Prompt", "Getting data list"}};
				dialog = ProgressDialog.show(DailyProdStepTwoActivity.this, text[locale][0], text[locale][1], true);
				break;
			case Constants.SCRB_GET_TZLIST_OK:
				dialog.dismiss();
				tzList.clear();
				tzList.add(head[locale]);
				for (String[] s : tempTZList) {
					tzList.add(s);
				}
				mAdapter.notifyDataSetChanged();
				break;
			case Constants.SCRB_GET_TZLIST_FL:
				dialog.dismiss();
				String []text1 = {"获取通知列表失败", "failed to get data list"};
				Toast.makeText(DailyProdStepTwoActivity.this, text1[locale], Toast.LENGTH_SHORT).show();
				break;
			}
		}
		
	}
	
	@Override
	protected void preStep() {
		
	}
	
	@Override
	protected void nextStep() {
		Intent intent = new Intent(DailyProdStepTwoActivity.this, DailyProdStepThreeActivity.class);
		intent.putExtra("zldh", zldh);
		intent.putExtra("ph", ph);
		intent.putExtra("pih", pih);
		intent.putExtra("scsl", scsl);
		intent.putExtra("ywgs", ywgs);
		String[] item = tzList.get(tzIndex);
		intent.putExtra("tz", item[0]);
		intent.putExtra("zc", item[1]);
		intent.putExtra("sm", item[2]);
		intent.putExtra("wwgs", item[3]);
		intent.putExtra("kgs", item[4]);
		intent.putExtra("hsgs", hsgs);
		this.startActivityForResult(intent, Constants.SCRB_REQUEST);
	}

}
