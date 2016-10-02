package com.nearu.grierp.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.nearu.grierp.R;
import com.nearu.grierp.adapter.DailyAdapter;
import com.nearu.grierp.comm.Constants;
import com.nearu.grierp.comm.MyScanController;
import com.nearu.grierp.web.service.DailyProdService;

public class DailyProdStepThreeActivity extends DailyProdBaseActivity {

	public static final String TAG = "DailyProdStepThreeActivity";
	private Handler mHandler 	= null;
	private EditText etTZ 			= null;
	private EditText etWWGS 	= null;
	private EditText etZC 			= null;
	private EditText etBCWGS 		= null;
	private ListView lvRecord	= null;
	private String tz, wwgs, zc, ygdh, ygxm, bscsl,bscslf, hsgs;
	private String scsl;
	private String zldh;
	private String ph;
	private String pih;
	private String ywgs;
	private float wwcs; // quantity need to finish
	private float bcwgs = 0.0f; // quantity finished this time
	private ArrayList<String[]> recordList = new ArrayList<String[]>();
	private HashMap<String, Integer> nameIndexMap = new HashMap<String, Integer>();
	private DailyAdapter mAdapter = null;
	private MyScanController scanController = null;
	private int recordIndex;
	public static final String[][] head = { {"员工代号","员工姓名","生产数量","数量（副)"},
		{"Staff Code", "Employee Name", "production number", "number (deputy)"}};  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle data = this.getIntent().getExtras();
		tz = data.getString("tz");
		zc = data.getString("zc");
		wwgs = data.getString("wwgs");
		wwcs = Float.parseFloat(wwgs);
		scsl = data.getString("scsl");
		zldh = data.getString("zldh");
		ph = data.getString("ph");
		pih = data.getString("pih");
		ywgs = data.getString("ywgs");
		hsgs = data.getString("hsgs");
		mHandler = new MyHandler();
		initView();
		scanController = new MyScanController(this, mHandler, cookie);
		scanController.initScan();
		loadServer();
		service = new DailyProdService(server1, server2, serverStatus);
		isGestureable = true;
	}
	
	@Override
	protected void onStop() {
		scanController.xiuxi();
		super.onStop();
		Log.d(TAG, "stoped");
		//this.setResult(Constants.SCRB_RESULT_OK);
	}

	@Override
	protected void onDestroy() {
		if (scanController != null) {
			scanController.xiadian();
		}
		super.onDestroy();
		Log.d(TAG, "this activity destoryed!!!!!!");
		this.setResult(Constants.SCRB_RESULT_OK);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_F2) {
			sendMsg(Constants.SCRB_NEW_RECORD);
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			scanController.xiuxi();
			finish();
			return super.onKeyDown(keyCode, event);
		}
		return false;
	}
	
	private void initView() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.daily_prod_step_3);
		etTZ = (EditText) findViewById(R.id.et_tz);
		etWWGS = (EditText) findViewById(R.id.et_wwgs);
		etZC = (EditText) findViewById(R.id.et_zc);
		etBCWGS = (EditText) findViewById(R.id.et_wgsl);
		etTZ.setText(tz);
		etWWGS.setText(wwgs);
		etZC.setText(zc);
		etBCWGS.setText(bcwgs+"");
		lvRecord = (ListView) findViewById(R.id.lv_record);
		recordList.add(head[locale]);
		mAdapter = new DailyAdapter(this, recordList);
		lvRecord.setAdapter(mAdapter);
		lvRecord.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				if (pos == 0) {
					return;
				}
				recordIndex = pos;
				sendMsg(Constants.SCRB_DEL_RECORD);
			}
			
		});
		lvRecord.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				if (pos == 0) {
					return false;
				}
				detSCSL.setText("");
				detSCSLF.setText("");
				recordIndex = pos;
				sendMsg(Constants.SCRB_MOD_RECORD);
				return false;
			}


			
		});
		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DailyProdStepThreeActivity.this.finish();
			}
			
		});
		findViewById(R.id.btn_new).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMsg(Constants.SCRB_NEW_RECORD);
			}
		});
		sendMsg(Constants.SCRB_NEW_RECORD);
		findViewById(R.id.btn_save).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				nextStep();
			}
			
		});
	}
	
	private void sendMsg(int what) {
		Message msg = new Message();
		msg.what = what;
		mHandler.sendMessage(msg);
	}
	
	void setSound() {
		if (cookie.getBoolean("sound", true)) {
			scanController.playSound();
		}
	}
	
	private void clear() {
		recordList.clear();
		recordList.add(head[locale]);
		mAdapter.notifyDataSetChanged();
		etBCWGS.setText("0.0");
		bcwgs = 0.0f;
		nameIndexMap.clear();
	}
	private EditText detSCSL, detSCSLF, detYGDH, detYGXM;
	private ProgressDialog dialog = null;
	private boolean isModify = false;
	private boolean isMerge = false;
	View dialogView = null;
	class MyHandler extends Handler {
		String[] tempRecord = null;
		AlertDialog alertDialog;
		boolean isNewRecordConfirmed = false;
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case Constants.SCRB_NEW_RECORD:
				String [][] text = {{"作业人员", "确定","取消"}, {"Workers", "OK", "Cancel"}};
				AlertDialog.Builder builder = new Builder(DailyProdStepThreeActivity.this);
				
				builder.setTitle(text[locale][0]);
				dialogView = LayoutInflater.from(DailyProdStepThreeActivity.this).inflate(R.layout.daily_prod_new_record, null);
				((EditText)dialogView.findViewById(R.id.det_zc)).setText(zc);
				detSCSL = (EditText) dialogView.findViewById(R.id.det_scsl);
				detSCSLF = (EditText) dialogView.findViewById(R.id.det_scslf);
				detYGDH = (EditText) dialogView.findViewById(R.id.det_ygdh);
				detYGXM = (EditText) dialogView.findViewById(R.id.det_ygxm);
				builder.setView(dialogView);
				if (isModify) {
					detYGDH.setText(ygdh);
					detYGXM.setText(ygxm);
					
					//detSCSL.setText(bscsl);
					//detSCSLF.setText(bscslf);
				}
				isNewRecordConfirmed = false;
				builder.setPositiveButton(text[locale][1], new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if(isNewRecordConfirmed) {
							return;
						}
						if (detYGDH.getText().toString().equals("") || detYGXM.equals("")) {
							return;
						}
						bscsl = detSCSL.getText().toString();
						bscslf = detSCSLF.getText().toString();
						isNewRecordConfirmed = true;
						sendMsg(Constants.SCRB_CAL_QTY);
					}
					
				});
				builder.setNegativeButton(text[locale][2], new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int keyCode) {
						if (tempRecord != null && isModify) {
							recordList.add(tempRecord);
							mAdapter.notifyDataSetChanged();
							bcwgs += Float.parseFloat(tempRecord[2]);
							tempRecord = null;
						}
					}
				});
				
				builder.setOnKeyListener(new OnKeyListener(){

					@Override
					public boolean onKey(DialogInterface arg0, int keyCode,
							KeyEvent arg2) {
						if(isNewRecordConfirmed) {
							return false;
						}
						if (keyCode == KeyEvent.KEYCODE_F2) {	
							Log.i("info", "TTTTTTTTTTT");
							scanController.doScan();
						} else if(keyCode == KeyEvent.KEYCODE_ENTER) {
							if (detYGDH.getText().toString().equals("") || detYGXM.equals("")) {
								return false;
							}
							bscsl = detSCSL.getText().toString();
							bscslf = detSCSLF.getText().toString();
							sendMsg(Constants.SCRB_CAL_QTY);
							alertDialog.dismiss();
							isNewRecordConfirmed = true;
						}
						Log.d(TAG, "key code is " + keyCode);
						
						return false;
					}
					
				});
				alertDialog = builder.create();
				alertDialog.show();
				break;
			case Constants.SCRB_NEW_RECORD_OK:
				dialog.dismiss();
				String[] item = {
						detYGDH.getText().toString(),
						detYGXM.getText().toString(),
						detSCSL.getText().toString(),
						detSCSLF.getText().toString()};
				String curYGXM = detYGXM.getText().toString();
				if(isMerge) {
					String[] temp 			= recordList.get(nameIndexMap.get(curYGXM).intValue());
					float tempSCSL 			= Float.parseFloat(temp[2]);
					float tempSCSLF 		= Float.parseFloat(temp[3]);
					float tempCurSCSLF = Float.parseFloat(detSCSLF.getText().toString());
					float tempCurSCSL 	= Float.parseFloat(detSCSL.getText().toString());
					tempSCSL 	+= tempCurSCSL;
					tempSCSLF += tempCurSCSLF;
					temp[2] = "" + tempSCSL;
					temp[3] = "" + tempSCSLF;
					mAdapter.notifyDataSetChanged();
					bcwgs += tempCurSCSL;
					etBCWGS.setText(bcwgs+"");
					isMerge = false;
					isModify = false;
					return;
				}
				if(nameIndexMap.containsKey(curYGXM) && !isModify) {
					//sendMsg(Constants.SCRB_MERGE_RECORD);
					
					String [][]text4 = {{"确定","confirm"},{"取消","cancel"},{"该员工的记录已经存在, 确认合并这一项嘛？","This employee is already exists! Are U sure to merge this item?"}};
					AlertDialog.Builder builder1 = new Builder(DailyProdStepThreeActivity.this);
					builder1.setTitle(text4[2][locale]);
					builder1.setPositiveButton(text4[0][locale], new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							isMerge = true;
							sendMsg(Constants.SCRB_NEW_RECORD_OK);
						}
					});
					builder1.setNegativeButton(text4[1][locale], new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							return;
						}
					});
					builder1.create().show();
				} else {
					recordList.add(item);
					mAdapter.notifyDataSetChanged();
					bcwgs += Float.parseFloat(detSCSL.getText().toString());
					etBCWGS.setText(bcwgs + "");
					nameIndexMap.put(curYGXM, recordList.size() - 1);
					isModify = false;
				}
				
				break;
			case Constants.SCRB_NEW_RECORD_FL:
				break;
			case Constants.SCAN_OK:
				setSound();
				Bundle data = msg.getData();
				ygdh = data.getString("value");
				// only for debug
				// ygdh = "A0205001";
				detYGDH.setText(ygdh);
				String[][] text1 = { { "tip", "tip" },
						{ "正在获取员工姓名", "get employee name..." } };
				dialog = ProgressDialog.show(DailyProdStepThreeActivity.this,
						text1[0][locale], text1[1][locale], true,true);
				ygxm = "";
				new Thread() {
					@Override
					public void run() {
						//for debug
						String name = service.getYGXM(cookie.getString("classID", ""), ygdh);
						// "ZC130617"
						Message msg = new Message();
						Bundle data = new Bundle();
						if (name == null) {
							Log.e(TAG, "name is null");
							data.putString("name", "null");
						}
						else {
							data.putString("name", name);
						}
						msg.setData(data);
						msg.what = Constants.GET_YGXM_OK;
						mHandler.sendMessage(msg);
					}
				}.start();	

				break;
			case Constants.GET_YGXM_OK:
				dialog.dismiss();
				String name = msg.getData().getString("name");
				if (name.equals("anyType{}") || name.equals("null")) {
					String[][] text2 = { { "提示", "该员工代号不存在" },
							{ "tip", "The employee code does not exist" } };
					Toast.makeText(DailyProdStepThreeActivity.this,
							text2[locale][1], Toast.LENGTH_SHORT).show();
					detYGDH.setText("");
					return;
				}
				detYGXM.setText(name);
				ygxm = name;
				break;
			case Constants.SCRB_DEL_RECORD:
				String [][]text4 = {{"确定","confirm"},{"取消","cancel"},{"确认删除这一项嘛？","Are U sure to delete this item?"}};
				AlertDialog.Builder builder1 = new Builder(DailyProdStepThreeActivity.this);
				builder1.setTitle(text4[2][locale]);
				builder1.setPositiveButton(text4[0][locale], new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						sendMsg(Constants.SCRB_DEL_RECORD_OK);
					}
				});
				builder1.setNegativeButton(text4[1][locale], new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						isModify = false;
						return;
					}
				});
				builder1.create().show();
				break;
			case Constants.SCRB_DEL_RECORD_OK:
				bcwgs -= Float.parseFloat(recordList.get(recordIndex)[2]);
				nameIndexMap.remove((recordList.get(recordIndex))[1]);
				etBCWGS.setText(bcwgs + "");
				recordList.remove(recordIndex);
				mAdapter.notifyDataSetChanged();
				
				break;
			case Constants.SCRB_MOD_RECORD:
				String []item1 = recordList.get(recordIndex);
				ygdh = item1[0];
				ygxm = item1[1];
				bscsl = item1[2];
				bscslf = item1[3];
				bcwgs -= Float.parseFloat(bscsl);
				etBCWGS.setText(bcwgs+"");
				tempRecord = recordList.get(recordIndex);
				recordList.remove(recordIndex);
				mAdapter.notifyDataSetInvalidated();
				isModify = true;
				sendMsg(Constants.SCRB_NEW_RECORD);
				break;
			case Constants.SCRB_CAL_QTY:
				if (bscsl.equals("") && bscslf.equals("")) {
					String []text2 = {"生产数量和生产数量副不能均为空","Production quantities(vice) can not both be empty" }; 
					Toast.makeText(DailyProdStepThreeActivity.this, text2[locale], Toast.LENGTH_SHORT).show();
					return;
				}	
				String [][]text5 = {{"提示","正在换算生产数量"},{"Prompt", "production quantities are translated"}};
				dialog = ProgressDialog.show(DailyProdStepThreeActivity.this, text5[locale][0], text5[locale][1], true, true);
				new Thread() {
					public void run() {
						boolean flag;
						String qty;
						String calQty;
						loadServer();
						if (detSCSL.getText().toString().equals("")) {
							flag = true;
							qty = detSCSLF.getText().toString();
							calQty = service.calQty(hsgs, qty, flag);
							detSCSL.setText(calQty);
							bscsl = calQty;
						} else {
							flag = false;
							qty = detSCSL.getText().toString();
							calQty = service.calQty(hsgs, qty, flag);
							bscslf = calQty;
							detSCSLF.setText(calQty);
						}
						if (calQty.equals("anyType{}")) {
							sendMsg(Constants.SCRB_CAL_QTY_FL);
						} else {
							sendMsg(Constants.SCRB_NEW_RECORD_OK);
						}
					}
				}.start();
				break;
			case Constants.SCRB_CAL_QTY_FL:
				String []text6 = {	"换算生产数量失败", "translate qty failed"};
				Toast.makeText(DailyProdStepThreeActivity.this, text6[locale], Toast.LENGTH_SHORT).show();
				break;
			case Constants.SCRB_SAVE_RECORD:
				if(bcwgs > wwcs) {
					String []text10 = {	"本次完工数大于未完工数量", "quantity finished is larger than expected!"};
					Toast.makeText(DailyProdStepThreeActivity.this, text10[locale], Toast.LENGTH_SHORT).show();
					return;
				}
				String [][]text7 = { {"保存", "正在保存..."}, {"save" , "saving..."}};
				dialog = ProgressDialog.show(DailyProdStepThreeActivity.this, text7[locale][0], text7[locale][1], true, true);
				break;
			case Constants.SCRB_SAVE_RECORD_OK:
				dialog.dismiss();
				wwcs -= bcwgs;
				etWWGS.setText("" + wwcs);
				clear();
				String []text9 = {	"成功保存", "save ok!"};
				Toast.makeText(DailyProdStepThreeActivity.this, text9[locale], Toast.LENGTH_SHORT).show();
				setResult(Constants.SCRB_RESULT_OK);
				finish();
				break;
			case Constants.SCRB_SAVE_RECORD_FL:
				dialog.dismiss();
				String []text8 = {	"保存失败! 请重试!", "sorry! failed to save! please try again"};
				Toast.makeText(DailyProdStepThreeActivity.this, text8[locale], Toast.LENGTH_SHORT).show();
				break;
			}
			
		}
		
	}
	
	@Override
	protected void preStep() {
		
	}
	@Override
	protected void nextStep() {
		if(bcwgs == 0.0f) {
			return;
		}
		sendMsg(Constants.SCRB_SAVE_RECORD);
		if(bcwgs > wwcs) {
			return;
		}
		new Thread() {
			public void run() {
				loadServer();
				boolean result = service.insertSCRB(cookie.getString("classID", ""), tz, 
						recordList.subList(1, recordList.size()));
				if (result) {
					sendMsg(Constants.SCRB_SAVE_RECORD_OK);
					return;
				}
				sendMsg(Constants.SCRB_SAVE_RECORD_FL);
			}
		}.start();
		
	}
	

}
