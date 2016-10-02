package com.nearu.grierp.entity;

public class RecordEntity {


	/*
	 * 条码
	 */
	private String TM;
	/*
	 * 品号
	 */
	private String PH;
	/*
	 * 品名
	 */
	private String PM;
	/*
	 *	批号
	 */
	private String LOT;
	/*
	 * 数量
	 */
	private String QTY;
	/*
	 * 制令单号
	 */
	private String MO_NO;
	/*
	 * 可入库数
	 */
	private String KRKS;
	/*
	 * 时间
	 */
	private String SYS_DATE;
	
	public RecordEntity(String TM, String PH ,String PM,String LOT,String QTY ,String MO_NO ,String KRKS,String SYS_DATE){
		this.TM = TM;
		this.LOT = LOT;
		this.MO_NO = MO_NO;
		this.PH = PH;
		this.PM = PM;
		this.QTY = QTY;
		this.KRKS = KRKS;
		this.SYS_DATE = SYS_DATE;
	}
	public String getTM() {
		return TM;
	}
	public void setTM(String tM) {
		TM = tM;
	}
	public String getPH() {
		return PH;
	}
	public void setPH(String pH) {
		PH = pH;
	}
	public String getPM() {
		return PM;
	}
	public void setPM(String pM) {
		PM = pM;
	}
	public String getLOT() {
		return LOT;
	}
	public void setLOT(String lOT) {
		LOT = lOT;
	}
	public String getQTY() {
		return QTY;
	}
	public void setQTY(String qTY) {
		QTY = qTY;
	}
	public String getMO_NO() {
		return MO_NO;
	}
	public void setMO_NO(String mO_NO) {
		MO_NO = mO_NO;
	}
	public String getSYS_DATE() {
		return SYS_DATE;
	}
	public void setSYS_DATE(String sYS_DATE) {
		SYS_DATE = sYS_DATE;
	}
	public String getKRKS() {
		return KRKS;
	}
	public void setKRKS(String kRKS) {
		KRKS = kRKS;
	}

}
