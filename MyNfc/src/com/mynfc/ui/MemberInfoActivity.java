package com.mynfc.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.mynfc.R;
import com.mynfc.data.BaseBean;
import com.mynfc.data.MemberBean;
import com.mynfc.handler.BaseNetHandler;
import com.mynfc.net.Net;
import com.mynfc.tool.Config;
import com.mynfc.tool.Tools;
import com.mynfc.xmlparser.MemberParser;

public class MemberInfoActivity extends BaseActivity {
	protected Context context;
	private TextView firmNameET, firmUrlET, firmAddressET, telNumberET, userNameET, cardIdTV, antiUrlTV;
	/** 会员卡标签id */
	private String cardId;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memberinfo);
        
        initView();
        memberInfoSubmit(this.getIntent().getStringExtra(Config.MembID));
//        cardId = this.getIntent().getStringExtra(Config.MembCardID);
    }
    
    private void initView(){
    	firmNameET  = (TextView)findViewById(R.id.firm_name_et);
    	firmUrlET  = (TextView)findViewById(R.id.firm_url_et);
    	firmAddressET = (TextView)findViewById(R.id.firm_address_et);
    	telNumberET = (TextView)findViewById(R.id.tel_number_et);
    	userNameET = (TextView)findViewById(R.id.user_name_et);
    	cardIdTV = (TextView)findViewById(R.id.card_id_tv);
    	antiUrlTV = (TextView)findViewById(R.id.anti_url_et);
    }
    
    private void memberInfoSubmit(String mId) {
    	String url = MemberBean.getMemberDetailURL(mId );
    	MemberParser addProductXmlHandler = new MemberParser();
        Net chaseListNet = new Net(this, url, addProductXmlHandler, MemberHandler, Config.CardDetailInfo_OK);
        chaseListNet.start();
    }
    
    private Handler MemberHandler = new BaseNetHandler(this){
    	public void handleBean(int returnCode, BaseBean baseBean) {
			switch (returnCode) {
	            case Config.CardDetailInfo_OK:
	            	memberDetailResult((MemberBean) baseBean);
	                break;
	            }
		}

		public void handleFirst() {
//			progressBarLayout.setVisibility(View.GONE);
		}
		
		public void handleNetError(){
			Tools.DisplayToast(context, context.getString(R.string.NET_ERROR));
		}
    };
    
    private void memberDetailResult(MemberBean mMemberBean) {
        if (mMemberBean != null) {
            String respCode = mMemberBean.getRespCode();
            if (respCode.equalsIgnoreCase(Config.respCode_ok)) {
            	firmNameET.setText(Tools.checkStr(mMemberBean.firmName));
            	firmUrlET.setText(Tools.checkStr(mMemberBean.url));
            	firmAddressET.setText(Tools.checkStr(mMemberBean.address));
            	telNumberET.setText(Tools.checkStr(mMemberBean.telnumber));
            	userNameET.setText(Tools.checkStr(mMemberBean.userName));
            	cardIdTV.setText(Tools.checkStr(mMemberBean.cardid));
            	antiUrlTV.setText(Tools.checkStr(mMemberBean.antiUrl));
            } 
            else if (respCode.equalsIgnoreCase(Config.respCode_fail)) {
                Tools.DisplayToast(this, this.getString(R.string.NET_ERROR));
            } 
        } else {
            Tools.DisplayToast(this, this.getString(R.string.NET_ERROR));
        }
    }
    

    
}