package com.mynfc.data;

import com.mynfc.tool.Config;

public class ProcutBean extends BaseBean {
	public String FW_URL = "card_url";
	public String fw_url;
	
	public static String getProductDetailURL(String mCardid){
		StringBuffer cardDetailURL = new StringBuffer();

		cardDetailURL.append(Config.ServerURL);
		cardDetailURL.append(Config.CardDetailFile);
		cardDetailURL.append(Config.CardID);
		cardDetailURL.append(mCardid);
		
		return cardDetailURL.toString();
	}

}
