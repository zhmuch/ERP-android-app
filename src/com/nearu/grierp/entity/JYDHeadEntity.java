package com.nearu.grierp.entity;
/**
 * 
 *    <strCOMPCODE>string</strCOMPCODE>
      <strZLDH>string</strZLDH>
      <strPH>string</strPH>
      <strBAT>string</strBAT>
      <strJYLX>string</strJYLX>
      <strUserName>string</strUserName>
      <strDEP>string</strDEP>
      <strSample_plan>string</strSample_plan>
 *
 */
public class JYDHeadEntity {
	public String comp, zldh, ph, pih, jylx, username, dep, jyfa;
	public JYDHeadEntity(
			String comp,String zldh, String ph, String pih, String jylx, String username, String dep, String jyfa)
	{
		this.zldh = zldh;
		this.comp = comp;
		this.ph = ph;
		this.pih = pih;
		this.jylx = jylx;
		this.username = username;
		this.dep = dep;
		this.jyfa = jyfa;
	}
}
