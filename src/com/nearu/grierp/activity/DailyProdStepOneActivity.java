package com.nearu.grierp.activity;

import java.text.DecimalFormat;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nearu.grierp.R;
import com.nearu.grierp.comm.Constants;
import com.nearu.grierp.web.service.DailyProdService;
import com.nearu.grierp.web.service.GetProductDataService;

public class DailyProdStepOneActivity extends DailyProdBaseActivity {
	
	public static final String TAG = "DailyProdStepOneActivity";
	private EditText etZLDH = null;
	private EditText etPH 		= null;
	private EditText etPIH 	= null;
	private EditText etSCSL = null;
	private EditText etYWGS = null;
	private Button btnBack 	= null;
	private Button btnNext = null;
	private String zldh;
	private String ph  ;
	private String pih ;
	private String scsl;
	private String ywgs;
	private String depName;
	private String hsgs;
	private Handler mHandler = null;
	private ProgressDialog dialog = null;
	
	private HashMap<String, String> data = new HashMap<String,String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new MyHandler();
		loadServer();
		isGestureable = true;
		service = new DailyProdService(server1, server2, serverStatus);
		initView();
	}
	
	private void initView() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.daily_prod_step_1);
		btnBack = (Button) findViewById(R.id.btn_back);
		btnBack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				DailyProdStepOneActivity.this.finish();
			}
		});
		btnNext = (Button) findViewById(R.id.btn_next);
		btnNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				nextStep();
			}
		});
		etYWGS = (EditText) findViewById(R.id.et_ywgs);
		etSCSL = (EditText) findViewById(R.id.et_scsl);
		etPIH 	= (EditText) findViewById(R.id.et_pih);
		etPH 		= (EditText) findViewById(R.id.et_ph);
		etZLDH = (EditText) findViewById(R.id.et_zldh);
		MyTextWatcher watcher = new MyTextWatcher();
		etZLDH.addTextChangedListener( watcher);

	}
	class MyTextWatcher implements TextWatcher{

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if(s.length() == 10){
				zldh = s.toString();
				Message msg = new Message();
				msg.what = Constants.GET_PRODUCT_DATA;
				mHandler.sendMessage(msg);
				new Thread(){

					@Override
					public void run() {
						Log.d(TAG, "begin call get product data service");
						loadServer();
						String comp = cookie.getString("compNo", "");
						data  = service.getDataZLDH(comp, zldh);
						if(data != null){
							Message msg = new Message();
							msg.what = Constants.GET_PRODUCT_DATA_OK;
							mHandler.sendMessage(msg);
						}else {
							Message msg = new Message();
							msg.what = Constants.GET_PRODUCT_DATA_FL;
							mHandler.sendMessage(msg);
						}
					}
					
				}.start();
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			
		}
		
	}
	private void clear(){
		etPH.setText("");
		etPIH.setText("");
		etSCSL.setText("");
		etYWGS.setText("");
		ph = "";
		pih = "";
		scsl="";
		ywgs="";
	}
	
	@Override
	protected void preStep() {
		
	}

	@Override
	protected void nextStep() {
		if(etPIH.getText().toString().equals("") || etPH.getText().toString().equals("")){
			Message msg = new Message();
			msg.what = Constants.ZLJY_PIH_EMPTY;
			mHandler.sendMessage(msg);
			return;
		}
		if(pih.equals("anyType{}") || !pih.equals(etPIH.getText().toString())){
			pih = etPIH.getText().toString();
			new Thread(){
				@Override
				public void run(){
					Log.d(TAG, "update pih " + pih);
					loadServer();
					GetProductDataService service = new GetProductDataService(server1, server2, serverStatus);
					String comp = cookie.getString("compNo", "");
					String result = service.updateBat(comp, zldh, pih);
					Log.d(TAG, "update bat result " + result);
					if (result != null && !result.equals("Success")){
						Message msg = new Message();
						msg.what = Constants.ZLJY_UPDATE_PIH_FL;
						mHandler.sendMessage(msg);
					}
				}
			}.start();
		}
		Intent intent = new Intent(DailyProdStepOneActivity.this, DailyProdStepTwoActivity.class);
		intent.putExtra("zldh", zldh);
		intent.putExtra("ph", ph);
		intent.putExtra("pih", pih);
		intent.putExtra("scsl", scsl);
		intent.putExtra("ywgs", ywgs);
		intent.putExtra("hsgs", hsgs);
		DailyProdStepOneActivity.this.startActivity(intent);
	}
	
	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case Constants.GET_PRODUCT_DATA:
				String [][]text = {{"提示","tip"},{"正在查询产品信息...","get product info..."}};
				dialog = ProgressDialog.show(DailyProdStepOneActivity.this, text[0][locale], text[1][locale]);
				break;
			case Constants.GET_PRODUCT_DATA_OK:
				dialog.dismiss();
				String text1[] = {"成功获取产品信息","get product info ok"};
				Toast.makeText(DailyProdStepOneActivity.this, text1[locale], Toast.LENGTH_SHORT).show();
				ph 		= data.get("ph");
				pih 	= data.get("pih");
				scsl = data.get("scsl");
				ywgs = data.get("ywgs");
				hsgs = data.get("hsgs");
				etPH.setText(ph);
				//etPIH.setText(pih);
				DecimalFormat format = new DecimalFormat("0.00");
				Float fSCSL = Float.parseFloat(scsl);
				Float fYWGS = Float.parseFloat(ywgs);
				etSCSL.setText(format.format(fSCSL));
				etYWGS.setText(format.format(fYWGS));
				if (!pih.equals("anyType{}"))
					etPIH.setText(pih);
				break;
			case Constants.GET_PRODUCT_DATA_FL:
				dialog.dismiss();
				String text4[] = {"获取产品信息失败", "fail to get product info"};
				Toast.makeText(DailyProdStepOneActivity.this, text4[locale], Toast.LENGTH_SHORT).show();	
				clear();
				break;
			case Constants.ZLJY_PIH_EMPTY:
				String text2[] = {"品号或批号不能为空呀", "item num or lot num can not be empty!"};
				Toast.makeText(DailyProdStepOneActivity.this, text2[locale], Toast.LENGTH_SHORT).show();
				break;
			case Constants.ZLJY_UPDATE_PIH_FL:
				String text3[] = {"更新批号失败", "update lot num failed"};
				Toast.makeText(DailyProdStepOneActivity.this, text3[locale], Toast.LENGTH_SHORT).show();
				break;
			}
		}
		
	}
	
}
