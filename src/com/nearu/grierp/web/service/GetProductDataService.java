package com.nearu.grierp.web.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.nearu.grierp.comm.Config;
import com.nearu.grierp.entity.JYDEntity;
import com.nearu.grierp.entity.JYDHeadEntity;

public class GetProductDataService extends BaseService {
	public  static final String TAG = "GetProductDataService";
	private static final String ACTION = "GetProduct_DATA";
	private static final String SOAP_ACTION = Config.targetNS + ACTION;
	private String[] urlTarget;
	public GetProductDataService(String urlTarget1, String urlTarget2, int[] serverStatus){
		urlTarget = new String[2];
		this.urlTarget[0] = urlTarget1;
		this.urlTarget[1] = urlTarget2;
		this.serverStatus =serverStatus;  
	}
	
	public HashMap<String,String> getProductData(String zldh, String comp){
		HashMap<String, String> data = new HashMap<String,String>();
		Log.d(TAG, "getproductData is invoked");
		SoapObject request = new SoapObject(Config.targetNS, ACTION);
		request.addProperty("strCOMPCODE", comp);
		request.addProperty("strZLDH", zldh);
		setHttpTransportAndCall(urlTarget,request, ACTION, SOAP_ACTION, TAG);
		//beginCall(request, ACTION, SOAP_ACTION, TAG);
		SoapObject doc = getResponse(TAG, "GetProduct_DATAResult");
		Log.d(TAG, androidHttpTransport.responseDump);
		if(doc == null) return null;
		try{
			if(doc.getAttributeCount() == 0) return null;
			String MRP_NO = doc.getPropertyAsString("MRP_NO");
			String PIH = doc.getPropertyAsString("BAT_NO");
			String QTY = doc.getPropertyAsString("QTY");
			String FIN = doc.getPropertyAsString("QTY_FIN");
			String DEP_NAME = doc.getPropertyAsString("DEP_NAME");
			Log.d(TAG, MRP_NO + " " + PIH + " " + QTY + " " + FIN);
			data.put("PH", MRP_NO);
			data.put("PIH", PIH);
			data.put("SCSL", QTY);
			data.put("YWGS", FIN);		
			data.put("DEP_NAME", DEP_NAME);
			return data;
		}catch(Exception e){
			Log.d(TAG, "Exception", e);
		}
		return null;
	}
	class JYD implements Serializable{
		public String name;
		public String sex;
		public String age;
		public JYD(){
			
		}
		public JYD(String name, String sex, String age){
			this.name= name;
			this.sex = sex;
			this.age = age;
		}
	}
	
	public void getObject(){
		SoapObject request = new SoapObject(Config.targetNS, "GetObject");
//		JYD [] man = new JYD[2];
//		man[0] = new JYD("a", "male", "10");
//		man[1] = new JYD("1", "male", "10");
		SoapObject man = new SoapObject(Config.targetNS, "man");
		SoapObject []JYD = new SoapObject[2];
		String [][]data = {
				{"a", "male", "10"},
				{"b", "female", "20"}
		};
		
		for(int i = 0; i < JYD.length; ++i){
			JYD[i] = new SoapObject(Config.targetNS, "JYD");
			JYD[i].addProperty("name", data[i][0]);
			JYD[i].addProperty("sex", data[i][1]);
			JYD[i].addProperty("age", data[i][2]);
			man.addProperty("JYD", JYD[i]);
		}
		request.addProperty("man",man);
		Log.d(TAG, request.toString());
		setHttpTransportAndCall(urlTarget,request, "GetObject",Config.targetNS+"GetObject" , TAG);
		SoapObject doc = getResponse(TAG, "GetObjectResult");
		
	}
	public String getEmployeeName(String comp, String ygdh){
		Log.d(TAG, "begin call getEmployeeName");
		SoapObject request = new SoapObject(Config.targetNS, "GetEmployee_Name");
		request.addProperty("strCOMPCODE", comp);
		request.addProperty("strYGNO", ygdh);
		Log.d(TAG, request.toString());
		setHttpTransportAndCall(urlTarget,request, "GetEmployee_Name",Config.targetNS+"GetEmployee_Name" , TAG);
		SoapObject doc = getResponse(TAG,"GetEmployee_NameResult");
		if(doc != null){
			Log.d(TAG, doc.toString());
		}else {
			return result;
		}
		return result;
	}
	
	public ArrayList<String> getJYMC(String comp, String ph){
		Log.d(TAG, "begin call get JYMC");
		SoapObject request = new SoapObject(Config.targetNS, "GetData_JYMC");
		request.addProperty("strCOMPCODE",comp);
		request.addProperty("strPH",ph);
		Log.d(TAG, request.toString());
		setHttpTransportAndCall(urlTarget,request, "GetData_JYMC",Config.targetNS+"GetData_JYMC" , TAG);
		SoapObject doc = getResponse(TAG, "GetData_JYMCResult");
		if(doc != null){
			Log.d(TAG, doc.toString() +  "%n" + this.androidHttpTransport.responseDump);
		}
		ArrayList<String> mcList = new ArrayList<String>();
		try{
			int count = document.getPropertyCount();
			for(int i = 0 ; i < count; ++i){
				Log.d(TAG, ((SoapObject)document.getProperty(i)).getPropertyAsString(0));
				mcList.add(((SoapObject)document.getProperty(i)).getPropertyAsString(0));
			}
		}catch(Exception e){
			Log.d(TAG, "Exception: " ,e);
		}
		return mcList;
	}
	
	public ArrayList<String> getJYL(String comp, String ph, String jymc){
		Log.d(TAG, "begin call getJYL");
		SoapObject request = new SoapObject(Config.targetNS, "GetData_Sample");
		request.addProperty("strCOMPCODE", comp);
		request.addProperty("strPH", ph);
		request.addProperty("strJYMC", jymc);
		Log.d(TAG, request.toString());
		setHttpTransportAndCall(urlTarget,request, "GetData_Sample",Config.targetNS+"GetData_Sample" , TAG);
		SoapObject doc = getResponse(TAG, "GetData_SampleResult");
		if(doc != null){
			Log.d(TAG, doc.toString() +  "%n" + this.androidHttpTransport.responseDump);
		}
		ArrayList<String> data = new ArrayList<String>();
		try{
			String xmCode = doc.getPropertyAsString(0);
			String cyl = doc.getPropertyAsString(1);
			data.add(xmCode);
			data.add(cyl);
		}catch(Exception e){
			Log.d(TAG, "Exception: " ,e);
		}
		
		return data;
	}
	
	public ArrayList<String> getErrorCause(String comp, String ph, String jymc){
		Log.d(TAG, "begin call get error cause");
		SoapObject request = new SoapObject(Config.targetNS, "GetData_Fail_Cause");
		request.addProperty("strCOMPCODE", comp);
		request.addProperty("strPH", ph);
		request.addProperty("strJYMC", jymc);
		Log.d(TAG, request.toString());
		setHttpTransportAndCall(urlTarget,request, "GetData_Fail_Cause",Config.targetNS+"GetData_Fail_Cause" , TAG);
		SoapObject doc = getResponse(TAG, "GetData_Fail_CauseResult");
		if(doc != null){
			Log.d(TAG, doc.toString() +  "%n" + this.androidHttpTransport.responseDump);
		}
		ArrayList<String> data = new ArrayList<String>();
		try{
			int count = document.getPropertyCount();
			for(int i = 0 ;i < count; ++i){
				data.add(((SoapObject)document.getProperty(i)).getPropertyAsString(0));
			}
		}catch(Exception e){
			Log.d(TAG, "Exception: " ,e);
		}
		return data;
	}
	
	public String getCheckName(String comp, String ph){
		Log.d(TAG, "begin get check name");
		SoapObject request = new SoapObject(Config.targetNS, "GetCheck_Name");
		request.addProperty("strCOMPCODE", comp);
		request.addProperty("strPH", ph);
		Log.d(TAG, request.toString());
		setHttpTransportAndCall(urlTarget,request, "GetCheck_Name",Config.targetNS+"GetCheck_Name" , TAG);
		getResponse(TAG, "GetCheck_NameResult");
		return result;
	}
	public String insertJYD(JYDHeadEntity head, ArrayList<JYDEntity> bodyList){
		Log.d(TAG, "begin insert JYD");
		SoapObject request = new SoapObject(Config.targetNS, "EXEC_Insert_ZL_JY");
		request.addProperty("strCOMPCODE",head.comp);
		request.addProperty("strZLDH",head.zldh);
		request.addProperty("strPH",head.ph);
		request.addProperty("strBAT",head.pih);
		request.addProperty("strJYLX",head.jylx);
		request.addProperty("strUserName",head.username);
		request.addProperty("strDEP",head.dep);
		request.addProperty("strSample_plan",head.jyfa);
		SoapObject ajyd = new SoapObject(Config.targetNS, "ajyd");
		request.addProperty("ajyd", ajyd);
		for(int i = 1; i < bodyList.size(); ++i ){
			SoapObject jyd = new SoapObject(Config.targetNS, "JYD");
			JYDEntity entity = bodyList.get(i);
			jyd.addProperty("JYID",0);
			jyd.addProperty("XMID",entity.xmid);
			jyd.addProperty("JYMC",entity.xmmc);
			jyd.addProperty("ERR_QTY",entity.bhgl);
			jyd.addProperty("MAKE",entity.cz);
			jyd.addProperty("AMEMO",entity.bz);
			jyd.addProperty("ERR_CAUSE",entity.bhgyy);
			jyd.addProperty("YG_NO",entity.ygdh);
			jyd.addProperty("YG_NAME",entity.ygxm);
			jyd.addProperty("JY_QTY",entity.jyl);
			Log.d(TAG, jyd.toString());
			ajyd.addProperty("JYD", jyd);
		}
		Log.d(TAG, request.toString());
		setHttpTransportAndCall(urlTarget,request, "EXEC_Insert_ZL_JY",Config.targetNS+"EXEC_Insert_ZL_JY" , TAG);
		SoapObject doc = getResponse(TAG, "EXEC_Insert_ZL_JYResult");
		if(doc != null){
			Log.d(TAG, doc.toString());
		}
		return result;
		
	}
	public String updateBat(String compNo, String zldh, String pih){
		Log.d(TAG, "begin insert updateBat");
		SoapObject request = new SoapObject(Config.targetNS, "Update_ZLDH_Bat");
		request.addProperty("strCOMPCODE",compNo);
		request.addProperty("strZLDH",zldh);
		request.addProperty("strBAT", pih);
		setHttpTransportAndCall(urlTarget,request, "Update_ZLDH_Bat",Config.targetNS+"Update_ZLDH_Bat" , TAG);
		SoapObject doc = getResponse(TAG, "Update_ZLDH_BatResult");
		if(doc != null){
			Log.d(TAG, doc.toString());
		}
		return result;
	}
	
}

