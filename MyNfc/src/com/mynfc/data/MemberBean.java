package com.mynfc.data;

import com.mynfc.tool.Config;

public class MemberBean extends BaseBean {
	public  String Type = "user_type";
	public  String ID = "id";
	public  String USER_NAME = "user_name";
	public  String Firm_Name = "firm_name";
	public  String Firm_Address = "address";
	public  String Firm_Url = "firm_url";
	public  String Tel_Number = "telnumber";
	public  String Card_id = "card_id";
	public  String Anti_Url = "anti_url";

    public String type;
    public String id;
    public String userName;
    public String firmName;
    public String address;
    public String url;
    public String telnumber;
    public String cardid;
    public String antiUrl;

	public static String checkMemberURL(String mCardid){
		StringBuffer cardDetailURL = new StringBuffer();

		cardDetailURL.append(Config.ServerURL);
		cardDetailURL.append(Config.MemberCheckFileStr);
		cardDetailURL.append(Config.CardID);
		cardDetailURL.append(mCardid);
		
		return cardDetailURL.toString();
	}
	
	/** 获取会员详细信息 */
	public static String getMemberDetailURL(String mId){
		StringBuffer cardDetailURL = new StringBuffer();

		cardDetailURL.append(Config.ServerURL);
		cardDetailURL.append(Config.MemberDetailFile);
		cardDetailURL.append(Config.ID);
		cardDetailURL.append(mId);
		
		return cardDetailURL.toString();
	}
	
	/** 修改会员的产品防伪地址 */
	public static String getAntiUrlModifyURL(String mId, String mAntiUrl, String mRegsUrl){
		StringBuffer cardDetailURL = new StringBuffer();

		cardDetailURL.append(Config.ServerURL);
		cardDetailURL.append(Config.MemberAntiUrlFile);
		cardDetailURL.append(Config.ID);
		cardDetailURL.append(mId);
		cardDetailURL.append(Config.Sign);
		cardDetailURL.append(Config.AntiUrl);
		cardDetailURL.append(mAntiUrl);
		cardDetailURL.append(Config.Sign);
		cardDetailURL.append(Config.RegsUrl);
		cardDetailURL.append(mRegsUrl);
		
		return cardDetailURL.toString();
	}
	
	/** 修改非法卡的跳转地址 */
	public static String getFatalCardUrlModifyURL(String mAntiUrl){
		StringBuffer cardDetailURL = new StringBuffer();

		cardDetailURL.append(Config.ServerURL);
		cardDetailURL.append(Config.FatalMemberUrlFile);
		cardDetailURL.append(Config.AntiUrl);
		cardDetailURL.append(mAntiUrl);
		
		return cardDetailURL.toString();
	}
}
