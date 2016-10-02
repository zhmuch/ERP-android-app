package com.nearu.grierp.comm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.nearu.grierp.R;
import com.nearu.grierp.activity.CheckReceiptInActivity;
import com.nearu.grierp.activity.DailyProdStepOneActivity;
import com.nearu.grierp.activity.QualityCheckActivity;
public class Config {
	public static String username = null;
	public static boolean isLogin = false;
	public static int power = 0;
	public static int curCompIndex = 0;
	public static String curCompNo = null;
	public static String []comp = {};
	public static String []compNo = {};
	public static String curModule = null;
	public static int compCount = 0;
	public static String curSelectedCompNo = null;
	public static String []module = {"审核管理", "质量管理", "生产管理", "仓库管理","人事管理" , "计划管理", "客服管理"};
	public static String []moduleEN = {"Audit Management","Quality Management","Production management", "Warehouse Management", "Personnel Management","Plan Management","Service Management"};
	public static Class [][] receiptClass = {
		{null},
		{QualityCheckActivity.class, },
		{CheckReceiptInActivity.class, DailyProdStepOneActivity.class}
		};
	public static int[] module_icon = {R.drawable.a,R.drawable.b,R.drawable.c, R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.g, R.drawable.h }; 
	public static String [][]menu = {
		{"扫描声音","退出"},
		{"sound of scanning","Exit"}
	};
	
	public static Map<String, Integer> receiptNumMap = new HashMap<String, Integer>();
	public static String URLTarget = "http://122.225.92.54:6060/erpweb/public/webservice1.asmx";
	public static String URLTarget2 = "http://122.225.92.54:6060/erp_webserver/erpservice.asmx";
	public static String targetNS = "http://tempuri.org/";
	public static ArrayList<String> serverList = new ArrayList<String>();
}
