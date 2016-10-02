package com.nearu.grierp.web.service;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.nearu.grierp.comm.Config;
import com.nearu.grierp.comm.Constants;

public class GetPowerService extends BaseService{
	public static final String TAG = "GetPowerService";
	private static final String ACTION = "GetPower";
	private static final String SOAP_ACTION = Config.targetNS + ACTION;
	
	private String[] urlTarget;
	public GetPowerService(String urlTarget1, String urlTarget2, int[] serverStatus){
		urlTarget = new String[2];
		this.urlTarget[0] = urlTarget1;
		this.urlTarget[1] = urlTarget2;
		this.serverStatus =serverStatus; 
	}
	
	public int getPower(String billName, String username, String curCompNo){
		Log.d(TAG, "getPower is invoked");
		Log.d(TAG, "bill name is " + billName + " compcode is "+ curCompNo + " usercode is " + username);
		SoapObject request = new SoapObject(Config.targetNS, ACTION);
		request.addProperty("strCOMPCODE" , curCompNo.toLowerCase());
		request.addProperty("usercode"    , username);
		request.addProperty("strBill_name", billName.trim());
		setHttpTransportAndCall(urlTarget , request, ACTION, SOAP_ACTION, TAG);
		//beginCall(request,ACTION, SOAP_ACTION, TAG);
		try{
			if(envelope.getResponse() != null){
				response = (SoapObject) envelope.bodyIn;
				int count = response.getPropertyCount();
				Log.d(TAG, "result count is " + count);
				if(count > 0) {
					String result = response.getPropertyAsString("GetPowerResult");
					Log.d(TAG, result);
					SoapObject root = (SoapObject) response.getProperty(0);
					SoapObject diffgram = (SoapObject) root.getProperty(1);
					SoapObject document= (SoapObject) diffgram.getProperty(0);
					SoapObject handPower = (SoapObject) document.getProperty(0);
					String aselect = handPower.getPropertyAsString("aselect");
					String ainsert = handPower.getPropertyAsString("ainsert");
					Log.d(TAG, "aselect is " + aselect + " ainsert is " + ainsert);
					int power = 0;
					if(aselect.trim().equals("true")) 
						power += 1;
					if(ainsert.trim().equals("true"))
						power += 3;
					Log.d(TAG, "power is " + power);
					switch(power){
					case 0:
						return Constants.PNONE;
					case 1:
						return Constants.PSELECT;
					case 3:
						return Constants.PINSERT;
					case 4:
						return Constants.PINSERT_SELECT;
					}
					
				}else {
					Log.d(TAG, "count is 0");
					return 0;
				}
			}else {
				Log.d(TAG, "response is null + " + androidHttpTransport.responseDump);	
			}
		} catch(Exception e){
			Log.d(TAG, "exception !" , e);
			//Log.d(TAG, androidHttpTransport.responseDump);
			return 0;
		}
		return 0;
	}

}

