package com.mynfc.tool;


public class Config {
    
	public static String ServerURL = "http://hshm20517.109.5ghl.cn/";
	public static String CardListFileStr = "cardlist_interface.ashx?";
	public static String CardAddFileStr = "cardadd_interface.ashx?";
	public static String CardDetailFileStr = "card_interface.ashx?";
	public static String CardDelFileStr = "carddel_interface.ashx?";
	
	public static String MemberCheckFileStr = "membercheck_interface.ashx?";
	public static String MemberListFileStr = "memberlist_interface.ashx?";
	public static String MemberAddFileStr = "memberadd_interface.ashx?";
	public static String MemberDelFileStr = "memberdel_interface.ashx?";
	public static String MemberDetailFileStr = "memberdetailinfo_interface.ashx?";
	public static String MemberInfoAddFileStr = "memberInfoAdd_interface.ashx?";
	
	public static String CardCheckFile = "CardIdCheck.ashx?";
	public static String MemberListFile = "MemberList.ashx?";
	public static String MemberAddFile = "MemberAdd.ashx?";
	public static String MemberDelFile = "MemberDel.ashx?";
	public static String CardAddFile = "CardAdd.ashx?";
	public static String MemberInfoAddFile = "MemberInfoAdd.ashx?";
	public static String CardListFile = "CardList.ashx?";
	public static String MemberDetailFile = "MemberDetail.ashx?";
	public static String MemberAntiUrlFile = "MemberAntiUrl.ashx?";
	public static String FatalMemberUrlFile = "FatalMemberUrl.ashx?";
	public static String CardDetailFile = "CardDetail.ashx?";
	
	public static String respCode_ok = "0000";
	public static String respCode_fetal = "1111";
    public static String respCode_fail = "9999";
    
    public static final int CardList_OK = 10;
    public static final int CardAdd_OK = 11;
    public static final int CardCheck_OK = 12;
    public static final int CardDel_OK = 13;
    public static final int CardInfoAdd_OK = 14;
    public static final int CardDetailInfo_OK = 15;
    public static final int AntiURL_OK = 16;
	
    public static final String CardID = "cardid=";
    public static final String CardType = "typeid=";
    public static final String Sign = "&";
    public static final String UserID = "userid=";
    public static final String ID = "cid=";
    public static final String UserName = "username=";
    public static final String FirmName = "firmname=";
    public static final String FirmAddress = "firmaddress=";
    public static final String FirmUrl = "firmurl=";
    public static final String TelPhone = "telphone=";
    public static final String AntiUrl = "antiurl=";
    public static final String RegsUrl = "regsurl=";
    public static final String Type = "type=";
    
  	public static final int NET_SETERROR = 998;
	public static final int NET_ERROR = 999;
	public static final int DownLoadIMG_OK = 2;
	public static final int Loading_OK = 7;
	
	public static final int PageNUM = 30;
	
	public static final String MembID = "mId";
	public static final String MembCardID = "membcardId";
	public static final String ProductID = "pId";
	
}
