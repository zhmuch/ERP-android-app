package com.nearu.grierp.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;



public class BaseActivity extends Activity {
	public static final String TAG = "BaseActivity";
	protected SharedPreferences  cookie = null;
	protected SharedPreferences.Editor editor = null;
	protected String server1;
	protected String server2;
	/**
	 *  0: chinese
	 *  1: english
	 */
	protected int locale = 0;
	protected int []serverStatus;
	protected boolean isGestureable = false; 
	
	public BaseActivity(){
		serverStatus = new int[2];
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cookie = this.getSharedPreferences("user_cookie", 0);
		if (cookie == null) {
			Log.e(TAG, "user_cookie is null");
		}
		editor = cookie.edit();
		locale = cookie.getString("lang", "").equals("CH")?0:1;
		server1 = cookie.getString("server1", "");
		server2 = cookie.getString("server2", "");
		
	}

	
	public void updateServerStatus(String server1, String server2){
		Log.d(TAG, "serverStatus[0] is " + serverStatus[0] + " serverStatus[1] is " + serverStatus[1] );
		if(serverStatus[0] == 0 && serverStatus[1] == 1) {
			editor.putString("server1", server2);
			editor.putString("server2", server1);
		}
		editor.commit();
	}
	protected void loadServer(){
		if (cookie != null) {
			server1 = cookie.getString("server1", "");
			server2 = cookie.getString("server2", "");
		} else {
			Log.e(TAG, "user_cookie is null");
		}
	}
	
}
