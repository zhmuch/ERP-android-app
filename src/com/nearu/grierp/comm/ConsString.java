package com.nearu.grierp.comm;

public class ConsString {
	public static final String REPEAT_TM_CH = "条码号重复";
	public static final String REPEAT_TM_EN = "repeat barcode";
	public static final String NO_INSERT_POWER_CH = "抱歉，您木有插入单据的权限!";
	public static final String NO_INSERT_POWER_EN = "sorry, you have not insert power!";
	public static final String[] DIFF_ZLDH = {"不同的制令单号!","different order number"};
	public static final String[] GET_RECEIPT_INFO_FL = {"获取单据信息失败","failed to get receipt info"};
	public static final String[] SAVE_OK = {"保存成功","save ok"};
	public static final String[] CONFIRM_SAVE = {"确认保存么？","really save?"};
	public static final String[] TIP = {"提示","tip"};
	public static final String[] CANCEL = {"取消","cancel"};
	public static final String[] CONFIRM = {"确认","confirm"};
	public static String getRepeatTM(int locale){
		if(locale == 0)
			return REPEAT_TM_CH;
		else return REPEAT_TM_EN;
	}
	public static String getNoInsertPower(int locale){
		if(locale == 0)
			return NO_INSERT_POWER_CH;
		else return NO_INSERT_POWER_EN;
	}
	public static String getDiffZLDH(int locale){
		if(locale == 0)
			return DIFF_ZLDH[0];
		else return DIFF_ZLDH[1];
	}
	public static String getGetReceiptFL(int locale){
		if(locale == 0)
			return GET_RECEIPT_INFO_FL[0];
		else return GET_RECEIPT_INFO_FL[1];
	}
	public static String getConfirmSave(int locale){
		if(locale == 0)
			return CONFIRM_SAVE[0];
		else return CONFIRM_SAVE[1];
	}
	public static String getTip(int locale){
		if(locale == 0)
			return TIP[0];
		else return TIP[1];
	}
	public static String getSaveOK(int locale){
		if(locale == 0)
			return SAVE_OK[0];
		else return SAVE_OK[1];
	}
	public static String getCancel(int locale){
		if(locale == 0)
			return CANCEL[0];
		else return CANCEL[1];
	}
	public static String getConfirm(int locale){
		if(locale == 0)
			return CONFIRM[0];
		else return CONFIRM[1];
	}
}
