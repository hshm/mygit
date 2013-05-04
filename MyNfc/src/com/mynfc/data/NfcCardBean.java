package com.mynfc.data;

import java.util.ArrayList;

import com.mynfc.tool.Config;

public class NfcCardBean extends BaseBean {
	public  String ITEM = "item";
	public  String CardUrl = "card_url";
	public  String CardType = "card_type";
	public  String Id = "id";
	public  String InfoCompleted = "card_logined";
	public  String CardId = "card_id";
	public  String AntiUrl = "anti_url";

	public String id;
    public String cardurl;
    public String cardtype;
    public String logined;
    public String cardid;
    public String antiurl;

	public static String checkNfcCardURL(String mCardId) {
		StringBuffer cardListURL = new StringBuffer();

		cardListURL.append(Config.ServerURL);
		cardListURL.append(Config.CardCheckFile);
		cardListURL.append(Config.CardID);
		cardListURL.append(mCardId);
		
		return cardListURL.toString();
	}
	

}
