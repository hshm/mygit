package com.mynfc.data;

public class BaseBean {
	public String RESPCODE = "respCode";
	public String RESPMESG = "respMesg";
	private String respCode;
	private String respMesg;
	
	public void setRespCode(String iRespCode){
		respCode = iRespCode.trim();
	}
	
	public String getRespCode(){
		return respCode;
	}
	
	public void setRespMesg(String iRespMesg){
		respMesg = iRespMesg.trim();
	}
	
	public String getRespMesg(){
		return respMesg;
	}
}
