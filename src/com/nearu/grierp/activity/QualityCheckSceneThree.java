package com.nearu.grierp.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.nearu.grierp.R;
import com.nearu.grierp.adapter.JYDAdapter;
import com.nearu.grierp.comm.Constants;
import com.nearu.grierp.comm.MyScanController;
import com.nearu.grierp.entity.JYDEntity;
import com.nearu.grierp.entity.JYDHeadEntity;
import com.nearu.grierp.web.service.GetProductDataService;

public class QualityCheckSceneThree extends QualityCheckBaseActivity {

	private Button btnBack = null;
	private Button btnSave = null;
	private Button btnCancel = null;
	private EditText etZLDH = null;
	private EditText etPH = null;
	private EditText etPIH = null;
	private EditText etYGDH = null;
	private EditText etYGXM = null;
	private ListView entityList = null;
	private Handler mHandler = null;
	private MyScanController scanController = null;
	private JYDHeadEntity headEntity = null;
	private ArrayList<JYDEntity> bodyList = null;
	private JYDAdapter entityAdapter = null;
	private GetProductDataService service = null;
	private JYDEntity h = null;
	private int bodyListSelected = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isGestureable = true;
		mHandler = new MyHandler();
		loadServer();
		if(locale == 0){
			h = new JYDEntity("姓名", "检验项目", "处置", "不合格量", "不合格原因", "备注");
		}
		else {
			h = new JYDEntity("Name", "check project", "disposal", "unqualified amount", "error cause", "remarks");
		}
		service = new GetProductDataService(
				server1, server2, serverStatus);
		bodyList = new ArrayList<JYDEntity>();
		
		bodyList.add(h);
		entityAdapter = new JYDAdapter(bodyList,this);
		initView();
		scanController = new MyScanController(this, mHandler, cookie);
		scanController.initScan();
		new Thread(){
			@Override
			public void run(){
				sendMsg(Constants.GET_CHECK_NAME);
				xmfa = service.getCheckName(cookie.getString("compNo", ""), ph);
				sendMsg(Constants.GET_CHECK_NAME_OK);
			}
		}.start();
		
	}

	@Override
	protected void onStop() {
		scanController.xiuxi();
		super.onStop();
		Log.d(TAG, "stoped");
	}

	@Override
	protected void onDestroy() {
		if (scanController != null) {
			scanController.xiadian();
		}
		super.onDestroy();
		Log.d(TAG, "this activity destoryed!!!!!!");
	}

	@Override
	protected void onPause() {
		super.onPause();
		scanController.xiuxi();
		Log.d(TAG, "PAUSE");
	}
/**
 *        <JYID>int</JYID>       0
          0<XMID>string</XMID>    xmid
          1<JYMC>string</JYMC>    jymc
          2<ERR_QTY>float</ERR_QTY> bhgsl
          3<MAKE>string</MAKE>      cz
          4<AMEMO>string</AMEMO>    bz
          5<ERR_CAUSE>string</ERR_CAUSE> bhgyy
          6<YG_NO>string</YG_NO>         ygdh
          7<YG_NAME>string</YG_NAME>     ygxm
          8<JY_QTY>float</JY_QTY>        jyl
 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, requestCode+"");
		if(requestCode == Constants.ZLJY  && resultCode == Constants.QM_RESULT_OK){
			int i = 0;
			String jyd = data.getStringExtra("jyd"+i);
			while(jyd != null){
				String []item = jyd.split(" ");
				String xmid  = item[0];
				String jymc  = item[1];
				String cz 		 = item[3];
				String ygdh  = item[6];
				String bhgyy = item[5];
				String ygxm  = item[7];
				String jyl   = item[8];
				String bhgsl = item[2];
				String bz    = item[4];
				//String ygxm, String xmmc, String cz, String bhgl, String bhgyy, String bz, String xmid, String ygdh
				JYDEntity entity = new JYDEntity(ygxm, jymc, cz, bhgsl,bhgyy, bz, xmid, ygdh, jyl);
				bodyList.add(entity);
				entityAdapter.notifyDataSetChanged();
				i++;
				jyd = data.getStringExtra("jyd"+i);
			}
		}else {
			return;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_F2) {
			Log.i("info", "TTTTTTTTTTT");
			scanController.doScan();
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			scanController.xiuxi();
			finish();
			return super.onKeyDown(keyCode, event);
		}
		return false;
	}

	void setSound() {
		if (cookie.getBoolean("sound", true)) {
			scanController.playSound();
		}
	}
	private String insertResult = null;
	private void save(){
		if (bodyList.size() == 1){
			return;
		}
		new Thread(){
			@Override 
			public void run(){
				sendMsg(Constants.JYD_SAVE);
				insertResult = service.insertJYD(headEntity, bodyList);
				sendMsg(Constants.JYD_SAVE_OK);
			}
		}.start();
	}
	private void clear(){
		bodyList.clear();
		bodyList.add(h);
		entityAdapter.notifyDataSetChanged();
		etYGDH.setText("");
		etYGXM.setText("");
		ygdh = "";
		ygxm = "";
	}
	private String zldh;
	private String ph;
	private String pih;
	private String ygdh;
	private String ygxm;
	private String depName;
	private String xmfa;
	private String jylx;
	private void initView() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.quality_scene_three);
		btnBack = (Button) findViewById(R.id.btn_back);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				scanController.xiuxi();
				QualityCheckSceneThree.this.finish();
			}
		});
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				scanController.xiuxi();
				finish();
			}
		});
		btnSave = (Button) findViewById(R.id.btn_save);
		btnSave.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				save();
			}
		});
		etZLDH = (EditText) findViewById(R.id.et_zldh);
		etPH = (EditText) findViewById(R.id.et_ph);
		etPIH = (EditText) findViewById(R.id.et_pih);
		etYGDH = (EditText) findViewById(R.id.et_ygdh);
		etYGXM = (EditText) findViewById(R.id.et_ygxm);
		entityList = (ListView) findViewById(R.id.lv_zljy);
		entityList.setAdapter(entityAdapter);
		entityList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				if (pos == 0) return;	
				bodyListSelected = pos;
				sendMsg(Constants.ZLJY_DEL_JYD);
			}
			
		});
		Intent intent = this.getIntent();
		zldh = intent.getStringExtra("zldh");
		/**
		 * for debug only
		 */
		ph = intent.getStringExtra("ph");
		//ph = "D13787478";
		pih = intent.getStringExtra("pih");
		jylx = intent.getStringExtra("jylx");
		depName = intent.getStringExtra("dep_name");
		etZLDH.setText(zldh);
		etPH.setText(ph);
		etPIH.setText(pih);
	}
	private void sendMsg(int what){
		Message msg = new Message();
		msg.what = what;
		mHandler.sendMessage(msg);
	}
	
	private ProgressDialog dialog = null;

	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.SCAN_OK:
				setSound();
				Bundle data = msg.getData();
				ygdh = data.getString("value");
				etYGDH.setText(ygdh);
				String[][] text = { { "tip", "tip" },
						{ "正在获取员工姓名", "get employee name..." } };
				dialog = ProgressDialog.show(QualityCheckSceneThree.this,
						text[0][locale], text[1][locale], true);
				ygxm = "";
				new Thread() {

					@Override
					public void run() {
						//for debug
						String name = service.getEmployeeName(
								cookie.getString("compNo", ""),
								ygdh); // "A1109003"
						Log.d(TAG, "ygdh is " + ygdh);
						Message msg = new Message();
						Bundle data = new Bundle();
						data.putString("name", name);
						msg.setData(data);
						msg.what = Constants.GET_YGXM_OK;
						mHandler.sendMessage(msg);
					}
				}.start();	

				break;
			case Constants.GET_YGXM_OK:
				dialog.dismiss();
				String name = msg.getData().getString("name");
				if (name.equals("anyType{}")) {
					String[][] text1 = { { "提示", "该员工代号不存在" },
							{ "tip", "The employee code does not exist" } };
					Toast.makeText(QualityCheckSceneThree.this,
							text1[locale][1], Toast.LENGTH_SHORT).show();
					return;
				}
				etYGXM.setText(name);
				ygxm = name;
				Intent intent = new Intent(QualityCheckSceneThree.this,
						QualityCheckSceneFour.class);
				intent.putExtra("ph", ph);
				intent.putExtra("ygdh", ygdh);
				intent.putExtra("ygxm", name);
				QualityCheckSceneThree.this.startActivityForResult(intent,
						Constants.ZLJY);
				break;
			case Constants.GET_CHECK_NAME:
				String text1[][] = {{"提示", "正在获取检验方案..."},{"tip","get check name..."}};
				dialog = ProgressDialog.show(QualityCheckSceneThree.this,
						text1[locale][0], text1[locale][1]);
				break;
			case Constants.GET_CHECK_NAME_OK:
				dialog.dismiss();
				if(xmfa.equals("anyType{}")){
					String text2[] = {"未设定检验方案!!","have not set a specific check name"};
					Toast.makeText(QualityCheckSceneThree.this, text2[locale], Toast.LENGTH_SHORT).show();
				} else {
					String text2[] = {"成功获取检验方案","get check name ok!"};
					Toast.makeText(QualityCheckSceneThree.this, text2[locale], Toast.LENGTH_SHORT).show();
				}
				//String comp,String zldh, String ph, String pih, String jylx, String username, String dep, String jyfa)
				headEntity = new JYDHeadEntity(
						cookie.getString("compNo",""), zldh, ph, pih, jylx, cookie.getString("username", ""), depName, xmfa);
				break;
			case Constants.JYD_SAVE:
				String text2[][] = {{"提示","正在保存"}, {"tip", "saving..,"}};
				dialog = ProgressDialog.show(QualityCheckSceneThree.this,
						text2[locale][0], text2[locale][1]);
				break;
			case Constants.JYD_SAVE_OK:
				dialog.dismiss();
				if(insertResult.equals("Success")){
					String text3[] = {"保存成功","save ok!"};
					Toast.makeText(QualityCheckSceneThree.this, text3[locale], Toast.LENGTH_SHORT).show();
					clear();
				}else{
					String text3[] = {"保存 失败","save failed!!"};
					Toast.makeText(QualityCheckSceneThree.this, text3[locale], Toast.LENGTH_SHORT).show();
				}
				break;
			case Constants.ZLJY_DEL_JYD:
				String [][]text4 = {{"确定","confirm"},{"取消","cancel"},{"确认删除这一项嘛？","Are U sure to delete this item?"}};
				AlertDialog.Builder builder = new Builder(QualityCheckSceneThree.this);
				builder.setTitle(text4[2][locale]);
				builder.setPositiveButton(text4[0][locale], new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						sendMsg(Constants.ZLJY_DEL_JYD_OK);
					}
				});
				builder.setNegativeButton(text4[1][locale], new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				});
				builder.create().show();
				break;
			case Constants.ZLJY_DEL_JYD_OK:
				bodyList.remove(bodyListSelected);
				entityAdapter.notifyDataSetChanged();
				break;
			}
		}

	}

	@Override
	protected void nextScene() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void preScene() {
		// TODO Auto-generated method stub
		
	}

}
