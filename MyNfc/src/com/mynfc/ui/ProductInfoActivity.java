package com.mynfc.ui;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.mynfc.R;
import com.mynfc.data.BaseBean;
import com.mynfc.data.ProcutBean;
import com.mynfc.handler.BaseNetHandler;
import com.mynfc.net.Net;
import com.mynfc.tool.Config;
import com.mynfc.tool.Tools;
import com.mynfc.xmlparser.ProductParser;

public class ProductInfoActivity extends BaseActivity {
	private TextView cardUrlTV, cardIdTV;
	private String cardId;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productinfo);
        
        cardId = this.getIntent().getStringExtra(Config.ProductID);
        productInfoSubmit(cardId);
        initView();
    }
    
    private void initView(){
    	cardIdTV  = (TextView)findViewById(R.id.card_id_tv);
    	cardUrlTV  = (TextView)findViewById(R.id.card_url_tv);

    }
    
    private void productInfoSubmit(String mCardId) {
    	String url = ProcutBean.getProductDetailURL(mCardId );
    	ProductParser productXmlHandler = new ProductParser();
        Net chaseListNet = new Net(this, url, productXmlHandler, productHandler, Config.CardDetailInfo_OK);
        chaseListNet.start();
    }
    
    private Handler productHandler = new BaseNetHandler(this){
    	public void handleBean(int returnCode, BaseBean baseBean) {
			switch (returnCode) {
	            case Config.CardDetailInfo_OK:
	            	productDetailResult((ProcutBean) baseBean);
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
    
    private void productDetailResult(ProcutBean mProductBean) {
        if (mProductBean != null) {
            String respCode = mProductBean.getRespCode();
            if (respCode.equalsIgnoreCase(Config.respCode_ok)) {
            	cardUrlTV.setText(mProductBean.fw_url);
            	cardIdTV.setText(cardId);
            } 
            else if (respCode.equalsIgnoreCase(Config.respCode_fail)) {
                Tools.DisplayToast(this, this.getString(R.string.NET_ERROR));
            } 
        } else {
            Tools.DisplayToast(this, this.getString(R.string.NET_ERROR));
        }
    }
    

    
}