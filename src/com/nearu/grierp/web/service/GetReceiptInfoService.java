package com.nearu.grierp.web.service;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.nearu.grierp.comm.Config;
import com.nearu.grierp.entity.RecordEntity;
import com.nearu.grierp.util.Tools;

public class GetReceiptInfoService  extends BaseService{
	public static final String TAG = "GetReceiptInfoService";
	private final static String ACTION = "GetData_TZ";
	private static final String SOAP_ACTION = Config.targetNS + ACTION;
	
	private String[] urlTarget;
	public GetReceiptInfoService(String urlTarget1, String urlTarget2,int[] serverStatus ){
		urlTarget = new String[2];
		this.urlTarget[0] = urlTarget1;
		this.urlTarget[1] = urlTarget2;
		this.serverStatus =serverStatus;
	}
	public RecordEntity getReceiptInfo(String compNo, String tm) {
		Log.d(TAG, "getReceiptInfo is invoked");
		SoapObject request = new SoapObject(Config.targetNS, ACTION);
		
		request.addProperty("strCOMPCODE",compNo);
		request.addProperty("strTM", tm);
		setHttpTransportAndCall(urlTarget, request, ACTION, SOAP_ACTION, TAG);
		//beginCall(request, ACTION, SOAP_ACTION, TAG);
		SoapObject response = null;
		try {
			if(envelope.getResponse() != null) {
				response = (SoapObject)envelope.bodyIn;
				Log.d(TAG,"response is " + this.androidHttpTransport.responseDump);
				String result = response.getPropertyAsString("GetData_TZResult");
				//Log.d(TAG,"result is " + result);
				SoapObject root = (SoapObject) response.getProperty(0);
				SoapObject diffgram = (SoapObject) root.getProperty(1);
				if(diffgram.getPropertyCount() == 0){
					return null;
				}
				SoapObject dElement = (SoapObject) diffgram.getProperty(0);
				SoapObject data = (SoapObject) dElement.getProperty(0);
				Log.d(TAG, "data " + data.getPropertyCount());
				String TM = data.getPropertyAsString("条码");
				String PH = data.getPropertyAsString("品号");
				String PM = data.getPropertyAsString("品名");
				String PIH = data.getPropertyAsString("批号");
				String num = data.getPropertyAsString("数量");
				String ZLDH = data.getPropertyAsString("制令单号");
				String KRKS = data.getPropertyAsString("KRKS");
				Log.d(TAG, "TM : " + TM +  " PH : " + PH + " PM : " + PM + " PIH : " + PIH + " NUM : " + num + " ZLDH : " + ZLDH + " KRKS: " + KRKS);
				return new RecordEntity(TM, PH ,PM, PIH, num, ZLDH, KRKS, Tools.getNowTimeString());
			}else {
				Log.d(TAG, "response is null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	


}
