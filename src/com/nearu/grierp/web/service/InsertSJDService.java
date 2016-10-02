package com.nearu.grierp.web.service;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.nearu.grierp.comm.Config;

public class InsertSJDService extends BaseService{
	public static final String TAG = "InsertSJDService";
	private final static String ACTION = "Insert_SJD";
	private static final String SOAP_ACTION = Config.targetNS + ACTION;
	
	private String[] urlTarget;
	public InsertSJDService(String urlTarget1, String urlTarget2 ,int[] serverStatus){
		urlTarget = new String[2];
		this.urlTarget[0] = urlTarget1;
		this.urlTarget[1] = urlTarget2;
		this.serverStatus =serverStatus;
	}
	public boolean insertReceipt(String compNo, String username, String zldh, int num){
		Log.d(TAG, "insertReceipt is invoked");
		SoapObject request = new SoapObject(Config.targetNS, ACTION);
		
		request.addProperty("strCOMPCODE",compNo);
		request.addProperty("strusername", username);
		request.addProperty("strZLDH", zldh);
		request.addProperty("qty", num);
		Log.d(TAG, "compNo : " + compNo + " username : " +username + " zldh : " + zldh  + " qty : " + num);
		setHttpTransportAndCall(urlTarget,request, ACTION, SOAP_ACTION, TAG);
		//beginCall(request, ACTION, SOAP_ACTION, TAG);
		SoapObject response = null;
		try{
			if(envelope.getResponse() != null) {
				response = (SoapObject) envelope.bodyIn;
				int count = response.getPropertyCount();
				Log.d(TAG, "count is " + count);
				if(count > 0) {
					Log.d(TAG, "response is " + this.androidHttpTransport.responseDump);
					String result = response.getPropertyAsString(0);
					Log.d(TAG, "result is " + result);
				}else {
					Log.d(TAG, "count = 0");
				}
			}
		}catch (Exception e){
			Log.d(TAG, "exception", e);
		}
		return false;
	}


}
