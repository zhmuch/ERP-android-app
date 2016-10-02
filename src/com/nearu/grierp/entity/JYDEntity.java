package com.nearu.grierp.entity;

public class JYDEntity {
	//姓名，检验项目，处置，不合格量，不合格原因，备注
	public String ygxm;
	public	String xmmc;
	public	String cz;
	public	String bhgl;
	public	String bhgyy;
	public	String bz;
	public String xmid;
	public String ygdh;
	public String jyl;
	public JYDEntity(String ygxm, String xmmc, String cz, String bhgl, String bhgyy, String bz ){
		this.ygxm = ygxm;
		this.xmmc = xmmc;
		this.cz = cz;
		this.bhgl = bhgl;
		this.bhgyy = bhgyy;
		this.bz = bz;
	}
	public JYDEntity(String ygxm, String xmmc, String cz, String bhgl, String bhgyy, String bz, String xmid,
			String ygdh, String jyl){
		this.ygxm = ygxm;
		this.xmmc = xmmc;
		this.cz   = cz;
		this.bhgl = bhgl;
		this.bhgyy = bhgyy;
		this.bz    = bz;
		this.xmid  = xmid;
		this.ygdh  = ygdh;
		this.jyl = jyl;
	}

}
