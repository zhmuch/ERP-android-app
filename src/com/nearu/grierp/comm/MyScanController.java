package com.nearu.grierp.comm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.nearu.grierp.controller.ScanControler;
import com.nearu.grierp.serialport.AbsSerialPort.OnReadSerialPortDataListener;
import com.nearu.grierp.serialport.AbsSerialPort.SerialPortData;

public class MyScanController {
	
	public static final String TAG = "MyScanController";
	private Context context;
	private Handler mHandler;
	private boolean isFirstScan = true;
	private boolean isScanning = false;
	private ScanControler scanController;
	private SharedPreferences cookie;
	
	public MyScanController(Context context, Handler handler, SharedPreferences cookie){
		this.context = context;
		this.mHandler = handler;
		this.cookie = cookie;
	}

	public void doScan(){
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
	public void initScan(){
		
		try {
			scanController = new ScanControler(context);
			scanController.initBM((Activity)context);
			//scanController.setPlaySound(true);
			scanController.read(oListener);
			scanController.kaidian();
		} catch (Exception e) {
			e.printStackTrace();
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
	public void xiadian(){
		if(scanController != null){
			scanController.trunVOFF();
			scanController.xiadian();
		}
	}
	public void xiuxi(){
		if(scanController != null){
			scanController.xiuxi();
			isScanning = false;
		}
	}
	public void playSound(){
		scanController.playSound();
	}
}
