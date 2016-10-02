package com.nearu.grierp.web.service;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public class BaseService {
	protected SoapObject response = null;
	protected HttpTransportSE androidHttpTransport = null;
	protected SoapSerializationEnvelope  envelope = null;
	protected int [] serverStatus = null;
	protected String result = null;
	protected SoapObject document;
	
	protected boolean beginCall(SoapObject request, String ACTION, String SOAP_ACTION, String TAG) {
		envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.bodyOut = request;
		envelope.setOutputSoapObject(request);
		try {
			Log.d(TAG, "begin call");
			androidHttpTransport.call(SOAP_ACTION, envelope);
			return true;
		} catch(IOException e) {
			Log.d(TAG, "IOException",e);
			e.printStackTrace();
		} catch(XmlPullParserException e) {
			Log.d(TAG, "XmlPullParser Exception",e);
			e.printStackTrace();
		} catch(Exception e){
			Log.d(TAG, "Exception",e);
			e.printStackTrace();
		}
		return false;
	}
	
	protected void setHttpTransportAndCall(String[] url, SoapObject request, String ACTION, String SOAP_ACTION, String TAG){
		result = "anyType{}";
		androidHttpTransport = new HttpTransportSE(url[0]);
		androidHttpTransport.debug = true;
		serverStatus[0] = 1;
		serverStatus[1] = 1;
		if(!beginCall(request, ACTION, SOAP_ACTION, TAG)){
			Log.d(TAG, "server1 is not available");
			Log.d(TAG, "begin call server2");
			serverStatus[0] = 0;
			androidHttpTransport.setUrl(url[1]);
			if(!beginCall(request, ACTION, SOAP_ACTION, TAG)){
				serverStatus[1] = 0;
				Log.d(TAG, "server 2 is not available");
			}
		} else {
			Log.d(TAG, "call ok");
		}
	}
	
	protected void setHttpTransportAndCall(String url, SoapObject request, String ACTION, String SOAP_ACTION, String TAG){
		androidHttpTransport = new HttpTransportSE(url);
		androidHttpTransport.debug = true;
		if(!beginCall(request, ACTION, SOAP_ACTION, TAG)){
			Log.d(TAG, "call end");
		}
	}
	
	protected SoapObject getResponse(String TAG, String resultName){
		if(envelope == null) {
			Log.e(TAG, "envelope is null");
			return null;
		}
		
		try {
			if(envelope.getResponse() != null) {
				response = (SoapObject) envelope.bodyIn;
				int count = response.getPropertyCount();
				if(count > 0) {
					result = response.getProperty(resultName).toString();
					Log.d(TAG, "result is " + result);
					SoapObject root = (SoapObject) response.getProperty(0);
					SoapObject diffgram = (SoapObject) root.getProperty(1);
					document= (SoapObject) diffgram.getProperty(0);
					return (SoapObject) document.getProperty(0);
				}else {
					Log.d(TAG, "count is 0");
					return null;
				}
			}else {
				Log.d(TAG, "response is null + " + androidHttpTransport.responseDump);	
			}
		} catch(Exception e){
			Log.d(TAG, "exception !" , e);
			if(androidHttpTransport.responseDump!=null)
				Log.d(TAG, androidHttpTransport.responseDump);
			return null;
		}
		return null;
	}
}
