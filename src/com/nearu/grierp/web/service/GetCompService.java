package com.nearu.grierp.web.service;

import java.util.ArrayList;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.nearu.grierp.comm.Config;

public class GetCompService extends BaseService{
	public static final String TAG = "GetCompService";
	private static final String ACTION = "Getcompany";
	private static final String SOAP_ACTION = Config.targetNS + ACTION;
	private String[] urlTarget;
	public GetCompService(String urlTarget1, String urlTarget2, int[] serverStatus){
		urlTarget = new String[2];
		this.urlTarget[0] = urlTarget1;
		this.urlTarget[1] = urlTarget2;
		this.serverStatus =serverStatus;  
	}
	public boolean getComp()  {
		Log.d(TAG, "GetComp is invoked");
		SoapObject request = new SoapObject(Config.targetNS, ACTION);
		setHttpTransportAndCall(urlTarget,request, ACTION, SOAP_ACTION, TAG);
		//beginCall(request, ACTION, SOAP_ACTION, TAG);
		if(envelope == null) {
			Log.e(TAG, "envelope is null");
			return false;
		}
		SoapObject response = null;
		try {
			if(envelope.getResponse() != null) {
				response = (SoapObject) envelope.bodyIn;
				int count = response.getPropertyCount();
				if(count > 0) {
					String result = response.getProperty("GetcompanyResult").toString();
					Log.d(TAG, "result is " + result);
					SoapObject root = (SoapObject) response.getProperty(0);
					SoapObject diffgram = (SoapObject) root.getProperty(1);
					SoapObject document= (SoapObject) diffgram.getProperty(0);
					int compCount = document.getPropertyCount();
					ArrayList<String> comp = new ArrayList<String>();
					ArrayList<String> compNo = new ArrayList<String>();
					for(int i = 0; i < compCount; i++) {
						SoapObject compObj = (SoapObject) document.getProperty(i);
						compNo.add(compObj.getPropertyAsString("compno"));
						comp.add(compObj.getPropertyAsString("name"));
						Log.d(TAG, "comp name : " + comp.get(i) + ", compno : " + compNo.get(i) );
					}
					Config.comp = new String[compCount];
					Config.compNo = new String[compCount];
					for(int i = 0; i < compCount; ++i){
						Config.comp[i] = comp.get(i);
						Config.compNo[i] = compNo.get(i);
					}
					Config.compCount = compCount;
					return true;
				} else{
					Log.e(TAG, "count is 0");
					return false;
				}
			}else {
				Log.d(TAG, "response is null!!!");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}


}
