package com.mynfc.data;

import java.util.ArrayList;

import com.mynfc.tool.Config;

public class MemberListBean extends BaseBean {
	public  String ITEM = "item";
	public  String ID = "id";
	public  String CardId = "cardid";
	public  String Type = "type";
	public  String CreateTime = "createtime";

	public ArrayList<MembBean> MembList = new ArrayList<MembBean>();

	public class MembBean{
		public String id;
	    public String cardid;
	    public String typeid;
	    public String createTime;
	}

	public static String getMemberListURL() {
		StringBuffer cardListURL = new StringBuffer();

		cardListURL.append(Config.ServerURL);
		cardListURL.append(Config.MemberListFile);
		
		return cardListURL.toString();
	}
	
	public static String getAddMemberURL(String mCardid){
		StringBuffer cardListURL = new StringBuffer();

		cardListURL.append(Config.ServerURL);
		cardListURL.append(Config.MemberAddFile);
		cardListURL.append(Config.CardID);
		cardListURL.append(mCardid);
		
		return cardListURL.toString();
	}
	
	public static String getDelMemberURL(String mId){
		StringBuffer cardListURL = new StringBuffer();

		cardListURL.append(Config.ServerURL);
		cardListURL.append(Config.MemberDelFile);
		cardListURL.append(Config.ID);
		cardListURL.append(mId);
		
		return cardListURL.toString();
	}
	
	public static String getMemberInfoAddURL(String mId, String mName, String mFirmName, String mUrl, String mAddress, String mTelNumber, String mAntiUrl){
		StringBuffer cardListURL = new StringBuffer();

		cardListURL.append(Config.ServerURL);
		cardListURL.append(Config.MemberInfoAddFile);
		cardListURL.append(Config.ID);
		cardListURL.append(mId);
		cardListURL.append(Config.Sign);
		cardListURL.append(Config.UserName);
		cardListURL.append(mName);
		cardListURL.append(Config.Sign);
		cardListURL.append(Config.FirmAddress);
		cardListURL.append(mAddress);
		cardListURL.append(Config.Sign);
		cardListURL.append(Config.FirmName);
		cardListURL.append(mFirmName);
		cardListURL.append(Config.Sign);
		cardListURL.append(Config.FirmUrl);
		cardListURL.append(mUrl);
		cardListURL.append(Config.Sign);
		cardListURL.append(Config.TelPhone);
		cardListURL.append(mTelNumber);
		cardListURL.append(Config.Sign);
		cardListURL.append(Config.AntiUrl);
		cardListURL.append(mAntiUrl);
		
		
		return cardListURL.toString();
	}
	
}
