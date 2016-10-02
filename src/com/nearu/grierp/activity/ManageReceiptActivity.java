package com.nearu.grierp.activity;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.nearu.grierp.R;
import com.nearu.grierp.adapter.ReceiptAdapter;
import com.nearu.grierp.comm.Config;
import com.nearu.grierp.comm.Constants;
import com.nearu.grierp.web.service.GetPowerService;

public class ManageReceiptActivity extends BaseActivity{
	public static String TAG = "ManageReceiptActivity";
	private GridView mReceiptView = null;
	private Button 	  mBackButton = null; 
	private Context mContext = null;
	private TextView tvHeadBar = null;
	private int moduleIndex;
	private int power = -1;
	private int selectReceipt = 0;
	private Handler mHandler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this.getApplicationContext();
		isGestureable = true;
		moduleIndex = this.getIntent().getIntExtra("module", -1); 
		Log.d(TAG, "module index is " + moduleIndex);
		mHandler = new MyHandler();
		initView();
	}
	void initView(){
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.manage_receipt);
		tvHeadBar = (TextView) findViewById(R.id.tv_manage_receipt_bar);
		String title;
		if(locale == 0){
			title = Config.module[moduleIndex];
		}else{
			title = Config.moduleEN[moduleIndex];
		}
		tvHeadBar.setText(title);
		mBackButton = (Button) findViewById(R.id.btn_back);
		mReceiptView = (GridView) findViewById(R.id.gv_receipt);
		ArrayList<String> list = getReceiptList()	;
		ReceiptAdapter adapter = new ReceiptAdapter(this.getApplicationContext(), list);
		mReceiptView.setAdapter(adapter);
		mReceiptView.setOnItemClickListener(new OnItemClickListener(){
		
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				selectReceipt = pos;
				Message msg = new Message();
				msg.what = Constants.BEGIN_GET_POWER;
				mHandler.sendMessage(msg);
			}
		});
		mBackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ManageReceiptActivity.this.finish();
			}
			
		});
	}
	
		
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	private Thread checkPowerThread = new Thread(){

		@Override
		public void run() {
			Message msg = new Message();
			server1 = cookie.getString("server1", "");
			server2 = cookie.getString("server2", "");
			GetPowerService service = new GetPowerService(server1, server2,serverStatus);
			String compNo = cookie.getString("compNo","");
			String username = cookie.getString("username", "");
			String receipt = Constants.receiptList[moduleIndex][selectReceipt * 2];
			power = service.getPower(receipt, username, compNo);
			updateServerStatus(server1, server2);
			editor.putInt("power", power);
			editor.commit();
			Config.power = power;
			msg.what = Constants.GET_POWER_OK;
			mHandler.sendMessage(msg);
			Log.d(TAG, "power is " + power );
		}
	};
	
	private ArrayList<String> getReceiptList(){
		ArrayList<String> rList = new ArrayList<String>();
		String[] receiptArray = Constants.receiptList[moduleIndex];
		for (int i = 0; i < receiptArray.length / 2; ++i) {
			rList.add(receiptArray[i*2 + locale]);
		}
		return rList;
	}
	
	private ProgressDialog dialog;
	
	class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case Constants.BEGIN_GET_POWER:
				String [][]text = {
						{" 提示", "tip"},
						{"正在获取权限...", "Geting power info..."},
				};
				
				if(power == -1){
					checkPowerThread.start();
					dialog = ProgressDialog.show(ManageReceiptActivity.this,text[0][locale], text[1][locale]);
				}else {
					Message msg1 = new Message();
					msg1.what = Constants.GET_POWER_OK;
					mHandler.sendMessage(msg1);
				}
				break;
			case Constants.GET_POWER_OK:
				dialog.dismiss();
				int power = cookie.getInt("power", -1);
				Config.power = power;
				if(Config.power == Constants.PNONE || Config.power == Constants.PSELECT){
					String []text0 = {"您木有权限查看此单据","sorry, you have no power to check this receipt!"};
					Toast.makeText(ManageReceiptActivity.this, text0[locale], Toast.LENGTH_SHORT).show();
					return;
				}else if(Config.power == 0) {
					String []text0 = {"获取用户权限失败","failed to get user power"};
					Toast.makeText(ManageReceiptActivity.this, text0[locale], Toast.LENGTH_SHORT).show();
					// only for debug
					return;
				}
				Intent intent = new Intent(mContext, Config.receiptClass[moduleIndex][selectReceipt]);
				ManageReceiptActivity.this.startActivity(intent);
				break;
			}
		}
		
	}
	
}
