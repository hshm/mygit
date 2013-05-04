package com.mynfc.data;

import java.util.ArrayList;

import com.mynfc.tool.Config;

public class CardListBean extends BaseBean {
	public  String ITEM = "item";
	public  String ID = "id";
	public  String CardId = "cardid";
	public  String CARDTIME = "cardtime";

	public ArrayList<CardBean> CardList = new ArrayList<CardBean>();

	public class CardBean{
		public String id;
	    public String cardid;
	    public String typeid;
	    public String createtime;
	}

	/** 获取产品列表 */
	public static String getCardListURL(String mUserId) {
		StringBuffer cardListURL = new StringBuffer();

		cardListURL.append(Config.ServerURL);
		cardListURL.append(Config.CardListFile);
		cardListURL.append(Config.UserID);
		cardListURL.append(mUserId);
		
		return cardListURL.toString();
	}
	
	/** 增加产品 */
	public static String getAddCardURL(String mCardid, String mUserId, String mUrl, String mType){
		StringBuffer cardListURL = new StringBuffer();

		cardListURL.append(Config.ServerURL);
		cardListURL.append(Config.CardAddFile);
		cardListURL.append(Config.CardID);
		cardListURL.append(mCardid);
		cardListURL.append(Config.Sign);
		cardListURL.append(Config.UserID);
		cardListURL.append(mUserId);
		cardListURL.append(Config.Sign);
		cardListURL.append(Config.AntiUrl);
		cardListURL.append(mUrl);
		cardListURL.append(Config.Sign);
		cardListURL.append(Config.Type);
		cardListURL.append(mType);
		
		return cardListURL.toString();
	}
	
	/** 删除产品 */
	public static String getDelCardURL(String mId){
		StringBuffer cardListURL = new StringBuffer();

		cardListURL.append(Config.ServerURL);
		cardListURL.append(Config.CardDelFileStr);
		cardListURL.append(Config.ID);
		cardListURL.append(mId);
		
		return cardListURL.toString();
	}
	
	public static String getCardDetailURL(String mCardid){
		StringBuffer cardDetailURL = new StringBuffer();

		cardDetailURL.append(Config.ServerURL);
		cardDetailURL.append(Config.CardDetailFileStr);
		cardDetailURL.append(Config.CardID);
		cardDetailURL.append(mCardid);
		
		return cardDetailURL.toString();
	}

}
