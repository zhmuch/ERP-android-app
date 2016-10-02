package com.nearu.grierp.web.service;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.nearu.grierp.comm.Config;

public class LogonService extends BaseService{
	public static final String TAG = "LogonService";
	private static final String ACTION = "Logon";
	private static final String SOAP_ACTION = Config.targetNS + ACTION;
	
	public LogonService(){
		
	}
	public boolean logon(String comp, String username, String password, StringBuilder classId){	
		Log.d(TAG, "logon is invoked");
		Log.d(TAG, "try to logon using username :" + username + ", password:" + password+", compNo:" + comp + ".");
		
		SoapObject request = new SoapObject(Config.targetNS, ACTION);
		request.addProperty("compNo", comp.trim());
		request.addProperty("usr", username.trim());
		request.addProperty("pswd", password.trim());
		setHttpTransportAndCall(Config.URLTarget,request, ACTION, SOAP_ACTION, TAG);
		//beginCall(request, ACTION, SOAP_ACTION, TAG);
		try {
			if(envelope.getResponse()!= null) {
				response = (SoapObject)envelope.bodyIn;
				int count = response.getPropertyCount();
				Log.d(TAG, "response count is " + count);
				if(count > 0) {
					String result = response.getProperty("LogonResult").toString();
					Log.d(TAG, "logon result is " + result);
					try {
						Document document = DocumentHelper.parseText(result);
						Element rootElem = (Element) document.getRootElement();
						String  flag = rootElem.elementText("Flag");
						if(flag.equals("T")){
							String classID = rootElem.elementText("ClassID");
							String mrpEG = rootElem.elementText("MrpEG");
							String mrpJY = rootElem.elementText("MrpJY");
							classId.append(classID);
							Log.d(TAG, "logon flag is " + flag + ", MrpEG is " + mrpEG + ", MrpJY is " + mrpJY + ", classID is " + classID );
							return true;
						}else {
							String  msg = rootElem.elementText("Msg");
							Log.d(TAG, "logon flag is " + flag + ", msg is " + msg);
							return false;
						}
					} catch (DocumentException e) {
						e.printStackTrace();
					}
					
				}
			} else {
				Log.d(TAG, "response is null!!!");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
