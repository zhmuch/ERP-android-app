package com.nearu.grierp.activity;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.nearu.grierp.R;
import com.nearu.grierp.comm.Constants;
import com.nearu.grierp.entity.JYDEntity;
import com.nearu.grierp.web.service.GetProductDataService;

public class QualityCheckSceneFour extends QualityCheckBaseActivity{
	private String ygxm;
	private String ygdh;
	private String[][] cz = {{"","合格", "返工","报废"},{"","Qualified", "rework", "scrap"}};
	private String ph;
	private EditText etYGDH = null;
	private EditText etYGXM = null;
	private EditText etBHGSL = null;
	private EditText etJYL  = null;
	private EditText etBZ   = null;
	private Spinner spXMMC  = null;
	private Spinner spCZ    = null;
	private Spinner spBHGYY = null;
	private Button btnConfirm = null;
	private Button btnCancel = null;
	private Button btnCont = null;
	/**
	 * form items
	 */
	private String bhgyy, bhgsl, jyl, bz, jymc, xmid;   // xmdm = item code, from get_sample
	private int czIndex = 0;
	private int jymcIndex = 0;
	/**
	 * dialog for init
	 */
	private ProgressDialog dialog = null;
	private Handler handler = null;
	GetProductDataService service = null;
	private ArrayList<JYDEntity> jydList = new ArrayList<JYDEntity>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isGestureable = true;
		initView();
		handler = new MyHandler();
		dialog = ProgressDialog.show(this, "tip", "init...");
		loadServer();
		service = new GetProductDataService(server1, server2, serverStatus);
		new InitThread().start();
	}
	
	@Override
	protected void onDestroy() {
		Intent intent = new Intent();
		this.setResult(Constants.QM_RESULT_FL, intent);
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		Intent intent = new Intent();
		this.setResult(Constants.QM_RESULT_FL, intent);
		super.onPause();
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_F2) {
			Log.i("info", "TTTTTTTTTTT");
			if (!addJYD(true)){
				return true;
			}
			clear();
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
			this.setResult(Constants.QM_RESULT_FL, intent);
			finish();
			return super.onKeyDown(keyCode, event);
		}
		return false;
	}
	private void initView(){
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.quality_scene_four);
		Intent intent = this.getIntent();
		ygxm = intent.getStringExtra("ygxm");
		ygdh = intent.getStringExtra("ygdh");
		ph = intent.getStringExtra("ph");
		// debug
		//ph = "D13787478"; 
		etYGDH 	= (EditText) findViewById(R.id.et_ygdh);
		etYGXM 	= (EditText) findViewById(R.id.et_ygxm);
		etBHGSL 	= (EditText) findViewById(R.id.et_bhgsl);
		etJYL 		= (EditText) findViewById(R.id.et_jyl);
		etBZ 			= (EditText) findViewById(R.id.et_bz);
		spXMMC  = (Spinner) findViewById(R.id.sp_xmmc);
		spCZ    = (Spinner) findViewById(R.id.sp_cz);
		spBHGYY = (Spinner) findViewById(R.id.sp_bhgyy);
		spBHGYY.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long arg3) {
				bhgyy = bhgyyList.get(pos);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		btnConfirm = (Button) findViewById(R.id.btn_confirm);
		btnConfirm.setOnClickListener(new OnClickListener(){
			/**
			 * <XMID>string</XMID>
					<JYMC>string</JYMC>
					<ERR_QTY>float</ERR_QTY>
					<MAKE>string</MAKE>
					<AMEMO>string</AMEMO>
					<ERR_CAUSE>string</ERR_CAUSE>
					<YG_NO>string</YG_NO>
					<YG_NAME>string</YG_NAME>
					<JY_QTY>float</JY_QTY>
			 */
			@Override
			public void onClick(View arg0) {
				nextScene();
			}
		});
		btnCancel  = (Button) findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				preScene();
			}
			
		});
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, cz[locale]);
		spCZ.setAdapter(adapter);
		spCZ.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				czIndex = pos ;
				if(pos > 1){
					new Thread(){
						@Override
						public void run(){
							sendMsg(Constants.GET_ERR_CAUSE);
							bhgyyList = service.getErrorCause(cookie.getString("compNo", ""), ph, jymc);
							sendMsg(Constants.GET_ERR_CAUSE_OK);
						}
					}.start();
				}else if(pos == 1){
					sendMsg(Constants.ZLJY_CZ_HG);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

				
			}
		});
		spXMMC.setOnItemSelectedListener(new OnItemSelectedListener(){
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				if (pos == 0){
					jymc = "";
					return;
				}
				jymcIndex = pos;
				jymc = mcList.get(pos);
				new Thread(){
					@Override
					public void run(){
						sendMsg(Constants.GET_JYL);
						String comp = cookie.getString("compNo", "");
						ArrayList<String> data = service.getJYL(comp, ph, jymc);
						xmid = data.get(0);
						jyl = data.get(1);
						
						if(czIndex > 1){
							bhgyyList = service.getErrorCause(cookie.getString("compNo", ""), ph, jymc);
							sendMsg(Constants.GET_ERR_CAUSE_OK);
						}
						sendMsg(Constants.GET_JYL_OK);
					}
				}.start();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		etYGDH.setText(ygdh);
		etYGXM.setText(ygxm);
	}
	private void clear(){
		if (bhgyyList != null)
			bhgyyList.clear();
		spXMMC.setSelection(0);
		spCZ.setSelection(0);
		etJYL.setText("");
		String []tmp = {};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(QualityCheckSceneFour.this, android.R.layout.simple_dropdown_item_1line, tmp);
		spBHGYY.setAdapter(adapter);
		etBHGSL.setText("");
		etBZ.setText("");
		bhgyy = "";
		bz = "";
	}
	/**
	 * 
	 * @param flag true : send error info
	 * @return
	 */
	private boolean checkForm(boolean flag){
		if(bhgyy == null) bhgyy="0";
		if (jymcIndex == 0){
			//jymc can not be empty
			if (flag)
				sendMsg(Constants.ZLJY_ERR3);
			return false;
		}
		if (czIndex == 0){
			// cz can not be empty
			if (flag)
				sendMsg(Constants.ZLJY_ERR2);
			return false;
		}
		
		// check err cause
		if (czIndex > 1 && bhgyy.equals("")){
			if (flag)
				sendMsg(Constants.ZLJY_ERR4);
			return false;
		}
		
		// check err quality
		if(!bhgsl.equals("") || czIndex == 1){
			if(bhgsl.equals("")){
				bhgsl="0";
			}else if(Float.parseFloat(bhgsl) > Float.parseFloat(jyl)){
				if (flag)
					sendMsg(Constants.ZLJY_ERR0);
				return false;
			}
			if(czIndex == 1){
				bhgsl = "0";
			}
		}else {
			if (flag)
				sendMsg(Constants.ZLJY_ERR1);
			return false;
		}

		return true;
	}
	
	/**
	 * <XMID>string</XMID>
			<JYMC>string</JYMC>
			<ERR_QTY>float</ERR_QTY>
			<MAKE>string</MAKE>
			<AMEMO>string</AMEMO>
			<ERR_CAUSE>string</ERR_CAUSE>
			<YG_NO>string</YG_NO>
			<YG_NAME>string</YG_NAME>
			<JY_QTY>float</JY_QTY>
	 */
	private boolean addJYD(boolean flag){
		// (String ygxm, String xmmc, String cz, String bhgl, String bhgyy, String bz, String xmid,
		// String ygdh, String jyl)
		bhgsl = etBHGSL.getText().toString();
		bz = etBZ.getText().toString();
		if (!checkForm(flag)){
			return !flag;
		}
		JYDEntity jyd = new JYDEntity( ygxm, jymc, cz[locale][czIndex],bhgsl, bhgyy, bz, xmid, ygdh, jyl );
		jydList.add(jyd);
		
		return true;
	}
	
	private void sendMsg(int what){
		Message msg = new Message();
		msg.what = what;
		handler.sendMessage(msg);
	}
	
	class InitThread extends Thread{

		@Override
		public void run() {
			
			String comp = cookie.getString("compNo", "");
			mcList = service.getJYMC(comp, ph);
			mcList.add(0, "");
			sendMsg(Constants.ZLJY_INIT_OK);
		}
	}
	
	private ArrayList<String> mcList = null;
	private ArrayList<String> bhgyyList = null; // error cause
	class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case Constants.ZLJY_INIT_OK:
				dialog.dismiss();
				if (mcList.size() == 1){
					String []text7 = {"警告， 项目名称为空","warnning !! check name is empty"};
					Toast.makeText(QualityCheckSceneFour.this, text7[locale], Toast.LENGTH_SHORT).show();
				}
				String[] list = mcList.toArray(new String[mcList.size()]);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(QualityCheckSceneFour.this, android.R.layout.simple_dropdown_item_1line, list);
				spXMMC.setAdapter(adapter);
				break;
			case Constants.GET_JYL:
				if(czIndex <= 1){
					String text[][] = {{"提示","正在获取检验量..."},{"tip", "get sample num.."}};
					dialog = showDialog(text);
				}else{
					String text[][] = {{"提示","正在获取检验量和不合格原因..."},{"tip", "get sample num and error cause.."}};
					dialog = showDialog(text);
				}
				break;
			case Constants.GET_JYL_OK:
				dialog.dismiss();
				etJYL.setText(jyl);
				break;
			case Constants.GET_ERR_CAUSE:
				String text1[][] = {{"提示","正在获取不合格原因..."},{"tip", "get error cause.."}};
				dialog = showDialog(text1);
				break;
			case Constants.GET_ERR_CAUSE_OK:
				dialog.dismiss();
				bhgyyList.add(0, "");
				String [] bhgyyArray = bhgyyList.toArray(new String[bhgyyList.size()]);
				ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(QualityCheckSceneFour.this, android.R.layout.simple_dropdown_item_1line, bhgyyArray);
				spBHGYY.setAdapter(adapter1);
				etBHGSL.setText("");	
				break;
			case Constants.ZLJY_CZ_HG:
				if(bhgyyList != null){
					bhgyyList.clear();
					String []tmp = {};
					ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(QualityCheckSceneFour.this, android.R.layout.simple_dropdown_item_1line, tmp);
					spBHGYY.setAdapter(adapter2);
				}
				bhgyy = "";
				etBHGSL.setText("0");
				break;
			case Constants.ZLJY_ERR0:
				String text2[] = {"不合格量大于检验量", "error quality is bigger than check quality"};
				Toast.makeText(QualityCheckSceneFour.this, text2[locale], Toast.LENGTH_SHORT).show();
				break;
			case Constants.ZLJY_ERR1:
				String text3[] = {"不合格数量不能为空", "Please input error quality!"};
				Toast.makeText(QualityCheckSceneFour.this, text3[locale], Toast.LENGTH_SHORT).show();
				break;
			case Constants.ZLJY_ERR2:
				String text4[] = {"处置不能为空", "Please select make choice!"};
				Toast.makeText(QualityCheckSceneFour.this, text4[locale], Toast.LENGTH_SHORT).show();
				break;
			case Constants.ZLJY_ERR3:
				String text5[] = {"项目名称不能为空", "Please select check name!"};
				Toast.makeText(QualityCheckSceneFour.this, text5[locale], Toast.LENGTH_SHORT).show();
				break;
			case Constants.ZLJY_ERR4:
				String text6[] = {"不合格原因不能为空", "Please select error cause!"};
				Toast.makeText(QualityCheckSceneFour.this, text6[locale], Toast.LENGTH_SHORT).show();
				break;
			}
		}
		
	}
	private ProgressDialog showDialog(String [][]text){
		return ProgressDialog.show(QualityCheckSceneFour.this, text[locale][0], text[locale][1]);
	}		

	@Override
	protected void nextScene() {
		if (!addJYD(jydList.size() == 0)){
			return;
		}
		Intent intent = new Intent();
		int i = 0;
		for (JYDEntity entity : jydList){
			String jyd = entity.xmid + " " + entity.xmmc + " " + entity.bhgl + " " + entity.cz + " " +
					entity.bz + " " + entity.bhgyy + " " + entity.ygdh + " " + entity.ygxm + " " + entity.jyl;
			Log.d(TAG, "JYD :" + jyd);
			intent.putExtra("jyd"+i, jyd);
			i++;
		}
		QualityCheckSceneFour.this.setResult(Constants.QM_RESULT_OK, intent);
		QualityCheckSceneFour.this.finish();
	}

	@Override
	protected void preScene() {
		Intent intent = new Intent();
		QualityCheckSceneFour.this.setResult(Constants.QM_RESULT_FL,intent);
		QualityCheckSceneFour.this.finish();
	}
	
}
