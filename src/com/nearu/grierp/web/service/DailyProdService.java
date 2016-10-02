package com.nearu.grierp.web.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.nearu.grierp.comm.Config;

public class DailyProdService extends BaseService {
	
	public static final String TAG = "DailyProdService";
	private String[] urlTarget;
	
	public DailyProdService(String urlTarget1, String urlTarget2, int[] serverStatus) {
		urlTarget = new String[2];
		this.urlTarget[0] = urlTarget1;
		this.urlTarget[1] = urlTarget2;
		this.serverStatus =serverStatus;  
	}
	
	public ArrayList<String[]> getTZList(String comp, String zldh, boolean isAll) {
		Log.d(TAG, "begin call get tz list : " + comp + " " + zldh + " " + isAll);
		
		SoapObject request = new SoapObject(Config.targetNS, "GetData_TZ_List");
		request.addProperty("strCOMPCODE",comp);
		request.addProperty("strZLDH",zldh);
		request.addProperty("bool_All",isAll);
		urlTarget[0] = Config.URLTarget2;
		urlTarget[1] = Config.URLTarget2;
		setHttpTransportAndCall(urlTarget,request, "GetData_TZ_List",Config.targetNS+"GetData_TZ_List" , TAG);
		SoapObject doc = getResponse(TAG, "GetData_TZ_ListResult");
		if (doc != null) {
			Log.d(TAG, doc.toString());
			ArrayList<String[]> data = new ArrayList<String[]>();
			if (isAll == false) {
				String []item = {doc.getPropertyAsString("TZ_NO"), 
						doc.getPropertyAsString("制程"),
						doc.getPropertyAsString("说明"),
						doc.getPropertyAsString("未完工数"),
						doc.getPropertyAsString("可报产量数"),};
				data.add(item);
			} else {
				int count = document.getPropertyCount();
				for (int i = 0 ;i < count ;++i) {
					Log.d(TAG, "result property 0" + document.getPropertyAsString(0));
					SoapObject pro = (SoapObject) document.getProperty(i);
					String []item = {pro.getPropertyAsString("TZ_NO"), 
							pro.getPropertyAsString("制程"),
							pro.getPropertyAsString("说明"),
							pro.getPropertyAsString("未完工数"),
							pro.getPropertyAsString("可报产量数"),};
					data.add(item);
				}
			}
			return data;
		}
		return null;
	}
	
	public HashMap<String, String> getDataZLDH(String comp, String zldh) {
		Log.d(TAG, "begin call get data zldh : " + comp + " " + zldh);
		
		SoapObject request = new SoapObject(Config.targetNS, "GetData_ZLDH");
		request.addProperty("strCOMPCODE", comp);
		request.addProperty("strZLDH",zldh);
		urlTarget[0] = Config.URLTarget2;
		urlTarget[1] = Config.URLTarget2;
		setHttpTransportAndCall(urlTarget, request, "GetData_ZLDH", Config.targetNS + "GetData_ZLDH", TAG);
		
		SoapObject doc = getResponse(TAG, "GetData_ZLDHResult");
		try {
		if (doc != null) {
			Log.d(TAG, doc.toString());
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("zldh",	doc.getPropertyAsString("制令单号"));
			data.put("ph",		doc.getPropertyAsString("品号"));
			data.put("pih",		doc.getPropertyAsString("批号"));
			data.put("scsl",	doc.getPropertyAsString("数量"));
			data.put("ywgs",	doc.getPropertyAsString("已完工数"));
			data.put("hsgs", doc.getPropertyAsString("换算公式"));
			return data;
		} else {
			return null;
		}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
	public String updateZLDHAndBat(String comp, String zldh, String pih) {
		Log.d(TAG, "begin call update zldh and bat : " + comp + " " + zldh + " " + pih );
		
		SoapObject request = new SoapObject(Config.targetNS, "Update_ZLDH_Bat");
		request.addProperty("strCOMPCODE", comp);
		request.addProperty("strZLDH", zldh);
		request.addProperty("strBAT", pih);
		urlTarget[0] = Config.URLTarget2;
		urlTarget[1] = Config.URLTarget2;
		setHttpTransportAndCall(urlTarget, request, "Update_ZLDH_Bat", Config.targetNS + "Update_ZLDH_Bat", TAG);
		
		SoapObject doc = getResponse(TAG, "Update_ZLDH_BatResult");
		if (doc != null) {
			Log.d(TAG, doc.toString());
		}
		return null;
	}
	
	public String getYGXM(String classID, String ygdh) {
		Log.d(TAG, "begin call get ygdh : " + classID + " " + ygdh);
		
		SoapObject request = new SoapObject(Config.targetNS, "GetYgName");
		request.addProperty("classID", classID);
		request.addProperty("ygNo", ygdh);
		urlTarget[0] = Config.URLTarget;
		urlTarget[1] = Config.URLTarget;
		setHttpTransportAndCall(urlTarget, request, "GetYgName", Config.targetNS + "GetYgName", TAG);
		//<Root><Flag>T</Flag><Name>仇桂香</Name></Root>
		SoapObject doc = getResponse(TAG, "GetYgNameResult");
		try {
				Document d = DocumentHelper.parseText(result);
				Element r = d.getRootElement();
				return r.elementText("Name");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/*
	 * flag  true: vice->qty    false qty->false
	 */
	public String calQty(String formula, String qty, boolean flag) {
		Log.d(TAG, "begin call calQty : " + formula + " " + qty + " " + flag);
		
		SoapObject request = new SoapObject(Config.targetNS, "CalculatorQty");
		request.addProperty("formula", formula);
		request.addProperty("QTY", qty);
		request.addProperty("isGetQty", flag);
		urlTarget[0] = Config.URLTarget2;
		urlTarget[1] = Config.URLTarget2;
		setHttpTransportAndCall(urlTarget, request, "CalculatorQty", Config.targetNS + "CalculatorQty", TAG);
		
		SoapObject doc = getResponse(TAG, "CalculatorQtyResult");
		if (doc != null) {
			Log.d(TAG, doc.toString());
		}
		return result;
	}
	

	
	
	public boolean insertSCRB(String classID, String tzdh, List<String[]> data) {
		Log.d(TAG, "begin call insert SCRB : " + classID + " " + tzdh + " ");
		SoapObject request = new SoapObject(Config.targetNS, "insert_SCRB");
		request.addProperty("classid", classID);
		request.addProperty("TZDH", tzdh);
		SoapObject arb = new SoapObject(Config.targetNS, "arb");
		request.addProperty("arb", arb);
		for(String[] e : data) {
			SoapObject scrb = new SoapObject(Config.targetNS, "SCRB");
			scrb.addProperty("YG_NO", e[0]);
			scrb.addProperty("YG_NAME", e[1]);
			scrb.addProperty("QTY", e[2]);
			scrb.addProperty("QTY1", e[3]);
			Log.d(TAG, "scrb : " + scrb.toString());
			arb.addProperty("SCRB", scrb);
		}
		urlTarget[0] = Config.URLTarget2;
		urlTarget[1] = Config.URLTarget2;
		setHttpTransportAndCall(urlTarget, request, "insert_SCRB", Config.targetNS + "insert_SCRB", TAG);
		SoapObject doc = getResponse(TAG, "insert_SCRBResult");
		if (doc != null) {
			Log.d(TAG, doc.toString());
		}
		if (result != null) {
			Log.d(TAG, "result is " + result);
		}
		if (result != null && result.equals("成功保存")) {
			return true;
		} else {
			return false;
		}
		
	}
}
