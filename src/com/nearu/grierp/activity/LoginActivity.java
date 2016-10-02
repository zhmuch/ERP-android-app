package com.nearu.grierp.activity;

import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nearu.grierp.R;
import com.nearu.grierp.comm.Config;
import com.nearu.grierp.comm.Constants;
import com.nearu.grierp.util.NetworkDetector;
import com.nearu.grierp.web.service.GetCompService;
import com.nearu.grierp.web.service.LogonService;
import com.nearu.grierp.web.service.TestService;

public class LoginActivity extends BaseActivity {
	public static String TAG = "LoginActivity";
	private static final String LOGIN_OK_CH="登录成功";
	private static final String LOGIN_FL_CH="用户名或密码错误";
	private static final String LOGIN_OK_EN="Successfully logon";
	private static final String LOGIN_FL_EN="undefined username or wrong password";
	private EditText etUsername = null;
	private EditText etPassword = null;
	private Spinner  spComp     = null;
	private Button   mButton    = null;
	private ProgressBar pbLogining = null;
	private TextView tvLogining    = null;
	private Button btnChangeLang   = null;
	private ArrayAdapter<String> adapter;
	private Context mContext = null;
	private Dialog initDialog = null;
	private Button btnResetServer = null;
	private ProgressDialog logonDialog = null;
	private StringBuilder classId = new StringBuilder();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this.getApplicationContext();
		isGestureable = false;
		initView();
		init();
	}
	void init() {
		if(!cookie.contains("lang")){
			
			editor.putString("lang", "CH");
			editor.commit();
		}
		if(!cookie.contains("server1")){
			editor.putString("server1", Config.URLTarget2);
			editor.putString("server2", Config.URLTarget2);
			editor.commit();
		}
		locale = cookie.getString("lang", "").equals("CH")?0:1;
		boolean networkState = NetworkDetector.detect(this);  
		if(!networkState) {
			dialog();
		}else {
			if(!checkServerURL(false)){
				return;
			}
			initDialog = ProgressDialog.show(this, "初始化", "正在初始化...");
			new Thread() {
				@Override
				public void run() {
					Log.d(TAG, "BEGIN test whether service is avaiable ");
					TestService ts = new TestService();
					if(ts.test()){
						SharedPreferences  cookie = LoginActivity.this.getSharedPreferences("user_cookie", 0);
						server1 = cookie.getString("server1", "");
						server2 = cookie.getString("server2", "");
						GetCompService getComp = new GetCompService(server1,server2,serverStatus);			
						if (getComp.getComp()) {
							Message msg = new Message();
							msg.what = Constants.GET_COMP_OK;
							mHandler.sendMessage(msg);
						} else {
							Message msg = new Message();
							msg.what = Constants.GET_COMP_FL;
							mHandler.sendMessage(msg);
						}
						updateServerStatus(server1, server2);
						Message msg = new Message();
						msg.what = Constants.TEST_SERCICE_OK;
						mHandler.sendMessage(msg);
					}else{
						Message msg = new Message();
						msg.what = Constants.TEST_SERCICE_FL;
						mHandler.sendMessage(msg);
					}
					
				}
			}.start();
		}
		locale = cookie.getString("lang", "").equals("CH")?0:1;
	}

	private EditText etServer1 = null;
	private EditText etServer2 = null;
	
	private boolean checkServerURL(boolean isReset){
		final boolean isreset = isReset; 
		SharedPreferences  cookie = LoginActivity.this.getSharedPreferences("user_cookie", 0);
		if(cookie.contains("server1") && cookie.contains("server2") && !isReset){
			Log.d(TAG, "cookie contains server1,2 " + cookie.getString("server1", "") + "|" +cookie.getString("server2", ""));
			return true;
		}else {
			String [][]text = {
					{"请输入服务器的url", "确认","取消" },
					{"please input server url", "ok", "cancel"}
			};
			
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle(text[locale][0]);
			View dialogView = LayoutInflater.from(mContext).inflate(R.layout.input_server, null);
			etServer1 = (EditText)dialogView.findViewById(R.id.et_server1);
			etServer2 = (EditText)dialogView.findViewById(R.id.et_server2);
			builder.setView(dialogView);
			
			builder.setPositiveButton(text[locale][1], new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					editor.putString("server1", etServer1.getText().toString());
					editor.putString("server2", etServer2.getText().toString());
					editor.commit();
					Message msg = new Message();
					msg.what = Constants.INIT_AGAIN;
					mHandler.sendMessage(msg);
				}
			});
			
			builder.setNegativeButton(text[locale][2], new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(!isreset)
						LoginActivity.this.finish();
				}
			});
			builder.create().show();
			return false;
		}
		
	}
	protected void dialog() {
		  AlertDialog.Builder builder = new Builder(this);
			String [][]text = {
					{"网络不可用，请确认网络状态后重新启动软件", "提示","确认", "取消"},
					{"network is not available, please check it and restart this app", "tip", "ok","cancel"}
			};
			
		  builder.setMessage(text[locale][0]);
		  builder.setTitle(text[locale][1]);
		  builder.setPositiveButton(text[locale][2], new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {	
				LoginActivity.this.finish();
			}
		  });

		  builder.setNegativeButton(text[locale][3], null);
		  builder.create().show();
	}
	private void showInitDialog() {
		
	}
	private Handler mHandler = null;
	private String username;
	private String password;
	private int comp;
	void initView() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		etUsername = (EditText) findViewById(R.id.et_user);
		etPassword = (EditText) findViewById(R.id.et_pwd);
		pbLogining = (ProgressBar) findViewById(R.id.pb_logining);
		tvLogining = (TextView) findViewById(R.id.tv_logining);
		btnChangeLang = (Button) findViewById(R.id.btn_change_language);
		btnResetServer = (Button) findViewById(R.id.btn_reset_server);
		spComp = (Spinner) findViewById(R.id.sp_comp);
		mButton = (Button) findViewById(R.id.btn_login);
		spComp.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> praent, View view,
					int pos, long id) {
				comp = pos;
				//Toast.makeText(MainActivity.this, Config.zt[pos] + " is selected", 1000).show();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
			
		});
		btnResetServer.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Message msg = new Message();
				msg.what = Constants.RESET_SERVER;
				mHandler.sendMessage(msg);
			}
			
		});
		mHandler = new MyHandler();
		
		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				username = etUsername.getText().toString();
				password = etPassword.getText().toString();
				if(Config.compNo.length>0){
					new LogonTask().execute(Config.compNo[comp], username, password);
				}
			}
			
		});
		
		btnChangeLang.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Resources resource = LoginActivity.this.getResources();
				Configuration config = resource.getConfiguration();
				DisplayMetrics dm = resource.getDisplayMetrics();
				String curLocale = cookie.getString("lang", "");
				if(curLocale.equals("EN")){
					Log.d(TAG, "change to simplified chinese");
					editor.putString("lang", "CH");
					config.locale = Locale.SIMPLIFIED_CHINESE;
				}
				else{ 
					Log.d(TAG, "change to english");
					editor.putString("lang", "EN");
					config.locale = Locale.ENGLISH;
				}
				editor.commit();
				resource.updateConfiguration(config, dm);
				resource.flushLayoutCache();
				Intent intent = new Intent("change_lang");
				LoginActivity.this.finish();
				LoginActivity.this.sendBroadcast(intent);
			}
			
		});
	}
	
	void setLoginingVisiable(int v) {
		pbLogining.setVisibility(v);
		tvLogining.setVisibility(v);
	}
	
	@Override
	public void onPause(){
		super.onPause();
		this.finish();
	}
	class LogonTask extends AsyncTask<String, Boolean, Boolean> {
		
		@Override
		protected void onPreExecute() {
			String[][] text = {
					{"提示", "tip"},
					{"正在登录" ,"logoning"}
					
			};
			
			logonDialog = ProgressDialog.show(LoginActivity.this,text[0][locale] ,text[1][locale] );
		}

		@Override
		protected Boolean doInBackground(String... params) {
			String	   compNo = params[0];
			String username = params[1];
			String password = params[2];
			
			Message msg = new Message();
			msg.what = View.VISIBLE;
			mHandler.sendMessage(msg );
			LogonService logonService = new LogonService();
			return logonService.logon(compNo, username, password, classId);

		}
		
		@Override 
		protected void onPostExecute(Boolean result) {
			logonDialog.dismiss();
			Message msg = new Message();
			msg.what = View.INVISIBLE ;
			mHandler.sendMessage(msg);
			msg = new Message();
			msg.what = result ? Constants.LOGON_OK : Constants.LOGON_FL;
			mHandler.sendMessage(msg);
		}
		
	}
	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case View.VISIBLE:
					//setLoginingVisiable(msg.what);
					break;
				case View.INVISIBLE:
					//setLoginingVisiable(msg.what);
					break;
				case Constants.TEST_SERCICE_OK:
					initDialog.dismiss();
					if(locale == 0)
						Toast.makeText(mContext, "当前登录服务器可用", Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(mContext, "current login server is ok", Toast.LENGTH_SHORT).show();
					break;
				case Constants.TEST_SERCICE_FL:
					initDialog.dismiss();
					if(locale == 0)
						Toast.makeText(mContext, "登录服务器不可用", Toast.LENGTH_SHORT).show();
					else 
					Toast.makeText(mContext, "access current login server failed", Toast.LENGTH_SHORT).show();
					
					break;
				case Constants.LOGON_OK:
					if(locale == 0)
						Toast.makeText(mContext, LOGIN_OK_CH, Toast.LENGTH_SHORT).show();
					else 
						Toast.makeText(mContext, LOGIN_OK_EN, Toast.LENGTH_SHORT).show();
					editor.putString("username", username);
					editor.putString("password", password);
					editor.putString("compNo", Config.compNo[comp]);
					editor.putString("compName", Config.comp[comp]);
					Log.d(TAG, classId.toString());
					editor.putString("classID", classId.toString());
					editor.putInt("compIndex", comp);
					editor.commit();
					Config.curCompNo = Config.compNo[comp];
					Config.username = username;
					Config.curCompIndex = comp;
					Intent intent = new Intent(mContext, ModuleActivity.class);
					LoginActivity.this.startActivity(intent);
					break;
				case Constants.LOGON_FL:
					if(locale == 0)
						Toast.makeText(mContext, LOGIN_FL_CH, Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(mContext, LOGIN_FL_EN, Toast.LENGTH_SHORT).show();
					// debug
//					
//					Message msg1 = new Message();
//					msg1.what = Constants.LOGON_OK;
//					mHandler.sendMessage(msg1);
					break;
				case Constants.GET_COMP_OK:
					adapter = new ArrayAdapter<String>(mContext, R.layout.spin_item, Config.comp);
					adapter.setDropDownViewResource(R.layout.spin_item);
					spComp.setAdapter(adapter);
					SharedPreferences  cookie_get = LoginActivity.this.getSharedPreferences("user_cookie", 0);
					username = cookie_get.getString("username", "");
					password = cookie_get.getString("password", "");
					comp = cookie_get.getInt("compIndex", 0);
					etUsername.setText(username);
					etPassword.setText(password);
					if(comp <= Config.compCount) 
						spComp.setSelection(comp);
					break;
				case Constants.GET_COMP_FL:
					if(locale == 0)
						Toast.makeText(mContext, "无法获得公司名单", Toast.LENGTH_SHORT).show();
					else 
						Toast.makeText(mContext, "can't fetch company name list", Toast.LENGTH_SHORT).show();
					break;
				case Constants.INIT_AGAIN:
					init();
					break;
				case Constants.RESET_SERVER:
					checkServerURL(true);
					break;
			}
		}
	}
}
