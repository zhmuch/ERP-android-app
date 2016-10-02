package com.nearu.grierp.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.nearu.grierp.R;
import com.nearu.grierp.adapter.RecordListViewAdapter;
import com.nearu.grierp.comm.BeepManager;
import com.nearu.grierp.comm.Config;
import com.nearu.grierp.comm.ConsString;
import com.nearu.grierp.comm.Constants;
import com.nearu.grierp.controller.ScanControler;
import com.nearu.grierp.entity.RecordEntity;
import com.nearu.grierp.serialport.AbsSerialPort.OnReadSerialPortDataListener;
import com.nearu.grierp.serialport.AbsSerialPort.SerialPortData;
import com.nearu.grierp.web.service.GetReceiptInfoService;
import com.nearu.grierp.web.service.InsertSJDService;

public class CheckReceiptInActivity extends BaseActivity{

	public static final String TAG = "CheckReceiptInActivity";
	private Button   mReturnButton = null;
	private Button 	mStartButton = null;
	private BeepManager bManager   = null;
	private Context       mContext = null;
	private EditText et_tm   = null;
	private EditText et_zldh = null;
	private EditText et_num  = null;
	private EditText et_krk  = null;
	private EditText et_zrk  = null;
	private ScanControler    scanController = null;
	private ListView 	    mRecordListView   = null;
	private Handler mHandler     = null;
	private boolean isScanning   = false;
	private boolean goOnScanning = false;
	private boolean isSave = false;
	private RecordListViewAdapter recordAdapter = null; 
	private ArrayList<RecordEntity> recordList  = null;
	private RecordEntity mRecord = null;
	private String tm  = null;
	private int curZRK = 0;
	private ProgressDialog mDialog = null;
	private CheckReceiptInActivity This = null;
	/*
	 * must be unique within one procedure
	 */
	private String curZLDH = null;
	/*
	 * make sure that tm is unique	
	 */
	private Set<String> tmSet = new HashSet<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "is created");
		isGestureable = false;
		This = this;
		mContext = this.getApplicationContext();
		mHandler = new MyHandler();
		initView();
		initScan();
		
	}
	
	@Override
	protected void onStop() {
		
		isScanning = false;
		clearAll();
		if(mDialog != null)
			mDialog.dismiss();
		scanController.xiuxi();
		mStartButton.setText(CheckReceiptInActivity.this.getResources().getString(R.string.begin_scan));
		super.onStop();
		Log.d(TAG, "stoped");
	}

	@Override
	protected void onDestroy() {
	
		clearAll();
		if(mDialog != null)
			mDialog.dismiss();
		if(scanController != null) {
			scanController.trunVOFF();
			scanController.xiadian();
		}
		super.onDestroy();
		Log.d(TAG, "this activity destoryed!!!!!!");
	}
	@Override
	protected void onPause(){
		super.onPause();
		scanController.xiuxi();
		isScanning = false;
		mStartButton.setText(CheckReceiptInActivity.this.getResources().getString(R.string.begin_scan));
		Log.d(TAG, "PAUSE");
	}
	void initView(){
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.check_receipt_layout);
		mReturnButton = (Button)findViewById(R.id.btn_back_1);
		mStartButton  = (Button) findViewById(R.id.btn_begin_scan);
		mRecordListView = (ListView) findViewById(R.id.lv_record);
		recordList = new ArrayList<RecordEntity>();
		recordAdapter = new RecordListViewAdapter(this, recordList);
		mRecordListView.setAdapter(recordAdapter);
		mRecordListView.setOnTouchListener(new ReceiptOnTouchListener());
		mReturnButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				clearAll();
				isScanning = false;
				if(scanController != null) {
					scanController.trunVOFF();
					scanController.xiadian();
				}
				CheckReceiptInActivity.this.finish();
			}
			
		});
		et_tm = (EditText) findViewById(R.id.tm);
		et_zldh = (EditText) findViewById(R.id.zldh);
		et_num = (EditText) findViewById(R.id.num);
		et_krk = (EditText) findViewById(R.id.krk);
		et_zrk = (EditText) findViewById(R.id.zrk);
		mStartButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Log.d(TAG, "is scanning : " + isScanning);
				doScan();
			}
		});
	}
	void doScan(){
		if(!isScanning){
			scanController.doScan();
			isScanning = true;
			Message msg = new Message();
			msg.what = Constants.BEGIN_SCAN;
			mHandler.sendMessage(msg);
			new Thread(scanListener).start();
		} else {
			isScanning = false;
			Message msg = new Message();
			msg.what = Constants.STOP_SCAN;
			mHandler.sendMessage(msg);
		}
	}
	void initScan(){
		try{
			scanController = new ScanControler(this);
			scanController.initBM(this);
			//scanController.setPlaySound(true);
			scanController.read(oListener);
			scanController.kaidian();
		}catch(Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "can not find scanner", Toast.LENGTH_SHORT).show();
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_F2)
		{
			Log.i("info", "TTTTTTTTTTT");
			doScan();
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
		{
			isScanning = false;
			scanController.xiuxi();
			mStartButton.setText(CheckReceiptInActivity.this.getResources().getString(R.string.begin_scan));
			finish();
			return super.onKeyDown(keyCode, event);
		}
		return false;
	}
	void clearET() {
		et_tm.setText("");
		et_zldh.setText("");
		et_krk.setText("");
		et_num.setText("");
		et_zrk.setText("");
	}

	private void clearAll(){
		mRecord = null;
		curZLDH = null;
		recordList.clear();
		recordAdapter.notifyDataSetChanged();
		clearET();
		mRecord = null;
		curZRK = 0;
		tmSet.clear();
	}
	
	void setSound(String _value) {
		Log.d(TAG, "set value " + _value);
		if(cookie.getBoolean("sound", true)){
			scanController.playSound();	
		}
	}

	private static int hm = 0;
	
	Runnable scanListener = new Runnable() {
		@Override
		public void run() {
			try {
				hm = 0;
				while( isScanning )
				{					
					Thread.currentThread().sleep(10);
					hm += 10;
					if(hm > 2500)
					{
						scanController.cxhuanxing();
						hm = 0;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};
	private boolean isFirstScan = true;
	private OnReadSerialPortDataListener oListener = new OnReadSerialPortDataListener() {

		@Override
		public void onReadSerialPortData(SerialPortData serialPortData) {
			String val = new String(serialPortData.getDataByte(), 0, serialPortData.getSize());
			Log.d(TAG, "VAL = " + val);
			Message msg = new Message();
			msg.what = Constants.SCAN_OK;
			Bundle data = new Bundle();
			data.putString("value", val);
			msg.setData(data);
			if(isFirstScan){
				isFirstScan = false;
				return;
			}
			mHandler.sendMessage(msg);
			isScanning = false;
		}
	};
	

	private void dialog() {
		AlertDialog.Builder builder = new Builder(CheckReceiptInActivity.this);
		builder.setMessage(ConsString.getConfirmSave(locale));
		builder.setTitle(ConsString.getTip(locale));
		builder.setPositiveButton(ConsString.getConfirm(locale), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				isSave = true;
				Message msg = new Message();
				msg.what = Constants.SAVE;
				mHandler.sendMessage(msg);
				
			}
		});
		builder.setNegativeButton(ConsString.getCancel(locale), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				isSave = false;
			}
		});
		
		builder.create().show();
	}
	
	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case Constants.SCAN_OK:
					Bundle data = msg.getData();
					tm = data.getString("value");
					setSound(tm);
					if(tmSet.contains(tm)){
						Toast.makeText(mContext, ConsString.getRepeatTM(locale), Toast.LENGTH_SHORT).show();
						return;
					}
					mStartButton.setText(CheckReceiptInActivity.this.getResources().getString(R.string.begin_scan));
					if(This.isFinishing()){
						Log.d(TAG, "is finishing!");
						return;
					}
					
					if(locale == 0){
						mDialog = ProgressDialog.show(This, "tip", "正在请求单据信息...");
					}
					else if(!CheckReceiptInActivity.this.isFinishing()){
						mDialog = ProgressDialog.show(This, "tip", "asking for receipt info...");	
					}
					new Thread(){

						@Override
						public void run() {
							server1 = cookie.getString("server1", "");
							server2 = cookie.getString("server2", "");
							GetReceiptInfoService service = new GetReceiptInfoService(server1, server2,serverStatus);
							
							String compNo = cookie.getString("compNo", "");
							//"20131219023216258"
							mRecord = service.getReceiptInfo(compNo,tm);
							updateServerStatus(server1, server2);
							Message msg = new Message();
							if( mRecord != null){
								Log.d(TAG, "get receipt info ok!");
								msg.what = Constants.GET_RECORD_INFO_OK;
							}else {
								Log.d(TAG, "get receipt info fail!");
								msg.what = Constants.GET_RECORD_INFO_FL;
							}
							mHandler.sendMessage(msg);
						}
					}.start();
					break;
				case Constants.BEGIN_SCAN:
					mStartButton.setText(CheckReceiptInActivity.this.getResources().getString(R.string.stop_scan));
					break;
				case Constants.STOP_SCAN:
					mStartButton.setText(CheckReceiptInActivity.this.getResources().getString(R.string.begin_scan));
					break;
				case Constants.RIGHT_MOVE_SAVE:
					if(recordList.size() == 0) {
						return;
					}
					dialog();
					break;
				case Constants.SAVE:
					
					int power = cookie.getInt("power", -1);
					Config.power = power;
					if(Config.power != Constants.PINSERT && Config.power != Constants.PINSERT_SELECT) {
						Toast.makeText(CheckReceiptInActivity.this.getApplicationContext(), ConsString.getNoInsertPower(locale), Toast.LENGTH_SHORT).show();
						return;
					}
					String [][]text = {{"保存","保存中"},{"save", "saving..."}};
					
					if(This.isFinishing()){
						Log.d(TAG, "is finishing!");
						return;
					}
					mDialog = ProgressDialog.show(CheckReceiptInActivity.this, text[locale][0], text[locale][1]);
					new Thread(){
						@Override
						public void run() {
							server1 = cookie.getString("server1", "");
							server2 = cookie.getString("server2", "");
							InsertSJDService insertService = new InsertSJDService(server1, server2,serverStatus);
							String compNo = cookie.getString("compNo", "");
							String username = cookie.getString("username", "");
							Log.d(TAG, "compNo : " + compNo + " username : " + username );
							insertService.insertReceipt(compNo, username,curZLDH, curZRK);
							updateServerStatus(server1, server2);
							Message msg = new Message();
							msg.what = Constants.INSERT_OK;
							mHandler.sendMessage(msg);
						}
					}.start();
					break;
				case Constants.GET_RECORD_INFO_OK:
					Log.d(TAG, "in handler get receipt info ok!");
					if(mDialog != null)
						mDialog.dismiss();
					if(curZLDH == null) {
						curZLDH = mRecord.getMO_NO();
					}
					if(mRecord.getMO_NO().equals(curZLDH)){
						et_tm.setText(mRecord.getTM());
						et_zldh.setText(mRecord.getMO_NO());
						DecimalFormat format = new DecimalFormat("0.00");
						float krk =  Float.parseFloat(mRecord.getKRKS());
						et_krk.setText(format.format(krk));
						et_num.setText(mRecord.getQTY());
						curZRK += Double.parseDouble(mRecord.getQTY());
						et_zrk.setText(""+curZRK);
						recordList.add(mRecord);
						tmSet.add(tm);
						recordAdapter.notifyDataSetChanged();
					}else {
						Toast.makeText(CheckReceiptInActivity.this, ConsString.getDiffZLDH(locale), Toast.LENGTH_SHORT).show();
						return;
					}
					break;
				case Constants.GET_RECORD_INFO_FL:
					if(mDialog != null)
						mDialog.dismiss();
					Toast.makeText(CheckReceiptInActivity.this, ConsString.getGetReceiptFL(locale), Toast.LENGTH_SHORT).show();
					break;
				case Constants.INSERT_OK:
					if(mDialog != null)
						mDialog.dismiss();
					Toast.makeText(CheckReceiptInActivity.this.getApplicationContext(), ConsString.getSaveOK(locale), Toast.LENGTH_SHORT).show();
					clearAll();
					break;
			}
		}
	}
	class GetReceiptInfoTask extends AsyncTask<String, String, Integer>{
		public GetReceiptInfoTask(){
			if(locale == 0)
				mDialog = ProgressDialog.show(CheckReceiptInActivity.this, "tip", "正在请求单据信息...");
			else 
				mDialog = ProgressDialog.show(CheckReceiptInActivity.this, "tip", "asking for receipt info...");	
		}
		
		@Override
		protected void onPreExecute() {
		
			mDialog.show();
		}
		@Override
		protected void onPostExecute(Integer result) {
			mDialog.dismiss();
		}

		@Override
		protected Integer doInBackground(String... arg0) {
			server1 = cookie.getString("server1", "");
			server2 = cookie.getString("server2", "");
			GetReceiptInfoService service = new GetReceiptInfoService(server1, server2,serverStatus);
			String compNo = cookie.getString("compNo", "");
			mRecord = service.getReceiptInfo(compNo,"20131219023216258");
			updateServerStatus(server1, server2);
			Message msg = new Message();
			if( mRecord != null){
				Log.d(TAG, "get receipt info ok!");
				msg.what = Constants.GET_RECORD_INFO_OK;
			}else {
				Log.d(TAG, "get receipt info fail!");
				msg.what = Constants.GET_RECORD_INFO_FL;
			}
			mHandler.sendMessage(msg);
			return null;
		}
		
	}
	
	class ReceiptOnTouchListener implements OnTouchListener {
		int touchSlop = ViewConfiguration.get(CheckReceiptInActivity.this.getApplicationContext()).getScaledTouchSlop();
		boolean isLeftMove = false;
		float xDown = 0.0f;
		float yDown = 0.0f;
		float xMove = 0.0f;
		float yMove = 0.0f;
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			//Log.d(TAG, "ON TOUCH");
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				isLeftMove = false;
				Log.d(TAG, "down!!!!");
				xDown = event.getRawX();
				yDown = event.getRawY();
				Log.d(TAG, "xdown = " + xDown + " ydown = " + yDown);
				break;
			case MotionEvent.ACTION_MOVE:
				//Log.d(TAG, "moving !!");

				xMove = event.getRawX();
				yMove = event.getRawY();
				
				float xDiff = xMove - xDown;
				float yDiff = yMove - yDown;
				Log.d(TAG, "xmove = " + xMove + " ymove = " + yMove + " xDiff = "+ xDiff);
				if((-xDiff) > touchSlop*2 && !isLeftMove) {
					isLeftMove = true;
					Log.d(TAG, "left moving !!");
					Message msg = new Message();
					msg.what = Constants.RIGHT_MOVE_SAVE;
					mHandler.sendMessage(msg);
				}
				break;
			}
			return false;
		}
	}
}
