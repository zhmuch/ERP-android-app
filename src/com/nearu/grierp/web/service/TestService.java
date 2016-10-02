package com.nearu.grierp.web.service;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.nearu.grierp.comm.Config;


public class TestService extends BaseService{
	public static  final String TAG    = "TestService";
	private static final String ACTION = "Test";
	private static final String SOAP_ACTION = Config.targetNS + ACTION;
	
	public TestService() {
		
	}
	public boolean test() {
		Log.d(TAG, TAG + " is invoked");
		SoapObject request = new SoapObject(Config.targetNS, ACTION);		
		setHttpTransportAndCall(Config.URLTarget,request, ACTION, SOAP_ACTION, TAG);
		//beginCall(request, ACTION, SOAP_ACTION, TAG);
		try {
			if(envelope.getResponse()!= null) {
				response = (SoapObject)envelope.bodyIn;
				int count = response.getPropertyCount();
				Log.d(TAG, "response count is " + count);
				if(count > 0) {
					String result = response.getProperty("TestResult").toString();
					Log.d(TAG, "Test result is " + result);
					if(result.equals("T")){
						Log.d(TAG, "service is avaiable");
						return true;
					}
					else{ 
						Log.d(TAG, "service is not avaiable");
						return false;
					}
				}
			} else {
				Log.d(TAG, "response is null!!!");
			}
		}
		catch(Exception e) {
			Log.e(TAG, "exception", e);
			e.printStackTrace();
		}
		return false;
	}

}
