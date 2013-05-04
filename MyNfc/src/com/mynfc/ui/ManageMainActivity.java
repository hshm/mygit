package com.mynfc.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.protocol.HTTP;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mynfc.R;
import com.mynfc.data.BaseBean;
import com.mynfc.data.CardListBean;
import com.mynfc.data.MemberBean;
import com.mynfc.data.MemberListBean;
import com.mynfc.data.NfcCardBean;
import com.mynfc.handler.BaseNetHandler;
import com.mynfc.net.Net;
import com.mynfc.tool.Config;
import com.mynfc.tool.Tools;
import com.mynfc.xmlparser.BeanParser;
import com.mynfc.xmlparser.CardListParser;
import com.mynfc.xmlparser.MemberListParser;
import com.mynfc.xmlparser.NfcCardParser;

/**
 * 会员管理
 * @author HSHM
 *
 */
public class ManageMainActivity extends BaseActivity {
	private ListView mMemberLV, mCardLV;
	private MemberListAdapter mMemberListAdapter;
	private CardListAdapter mCardListAdapter;
	private ArrayList<MemberListBean.MembBean> memberListBean = new ArrayList<MemberListBean.MembBean>();
	private ArrayList<CardListBean.CardBean> productListBean = new ArrayList<CardListBean.CardBean>();
	private LinearLayout nologin_ll,  login_ll,  product_member_ll, member_first_ll;
	/** 登录 */
	private boolean loginFlag = false;
	/** 管理员登录 */
	private boolean login_adminFlag = false;
	/** 管理员 */
	private final String adminFlag = "1";
	/** 会员 */
	private final String memberFlag = "2"; 
	/** 产品标签 */
	private final String productFlag = "3";
	/** 用户 */
	private final String consumerFlag = "4";
	/** 非法卡 */
	private final String fetalFlag = "0";
	
	/** 会员Id */
	private String userId;
	/** 防伪地址 */
	private String antiUrl;
	
	private TextView cardIdTV;
	private EditText firmNameET, firmUrlET, firmAddressET, telNumberET, userNameET, antiUrlET;
	
	private Button infoBtn;
	
	private ProgressDialog progressDialog;
	
	private String tempAntiUrl = "";
	
	/**新用户注册地址 */
	private String regsUrl = "";
	
	/** 是否新增用户 */
	private boolean add_consumer = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_main);
        initView();
        setClickListener();
        
        Log.i("intent","---oncreate---");
        processIntent(getIntent());  
    }
    
    @Override  
    protected void onResume() {  
        super.onResume();  
    }  
    
    protected void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);
    	Log.i("intent","---onNewIntent---");
    	processIntent(intent);
    }
    
    /** 
     * Parses the NDEF Message from the intent and prints to the TextView 
     * @throws  
     */  
	private void processIntent(Intent intent) {

		String intentActionStr = intent.getAction();// 获取到本次启动的action
		Log.i("intent","---" + intentActionStr);
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intentActionStr)// NDEF类型
				|| NfcAdapter.ACTION_TECH_DISCOVERED.equals(intentActionStr)// 其他类型
				|| NfcAdapter.ACTION_TAG_DISCOVERED.equals(intentActionStr)) {// 未知类型
			// 在intent中读取Tag id
			byte[] bytesId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);

			String strId = String.valueOf(Math.abs(Tools.byteToInt2(bytesId)));
			if (loginFlag == false) {//未登录则验证Id
				checkIdSubmit(strId);
			} else {
				if (login_adminFlag == true) {//增加会员
					memberAddSubmit(strId);
				} else {
					if(add_consumer == true){
						productAddSubmit(strId,  userId, antiUrl, consumerFlag);//增加用户
					}else{
						productAddSubmit(strId,  userId, antiUrl, productFlag);//增加商品
					}
				}
			}
		}
	}
	
	 private void initView(){
	    	mMemberLV = (ListView)findViewById(R.id.lv_memberlist);
	    	mMemberListAdapter = new MemberListAdapter(this) ;
	        mMemberLV.setAdapter(mMemberListAdapter);
//	        nologin_ll  = (LinearLayout)findViewById(R.id.manage_nologin);
	        login_ll  = (LinearLayout)findViewById(R.id.manage_login);
	        
	        mCardLV = (ListView)findViewById(R.id.lv_cardlist);
	        mCardListAdapter = new CardListAdapter(this) ;
	    	mCardLV.setAdapter(mCardListAdapter);
	    	product_member_ll  = (LinearLayout)findViewById(R.id.card_member_ll);
	    	
	    	member_first_ll  = (LinearLayout)findViewById(R.id.member_first_ll);
	    	firmNameET  = (EditText)findViewById(R.id.firm_name_et);
	    	firmUrlET  = (EditText)findViewById(R.id.firm_url_et);
	    	firmAddressET = (EditText)findViewById(R.id.firm_address_et);
	    	telNumberET = (EditText)findViewById(R.id.tel_number_et);
	    	userNameET = (EditText)findViewById(R.id.user_name_et);
	    	antiUrlET = (EditText)findViewById(R.id.anti_url_et);
	    	infoBtn = (Button)findViewById(R.id.info_btn);
	    	
	    	cardIdTV  = (TextView)findViewById(R.id.card_id_tv);
	    }
	    
	    private void setClickListener() {
	    
	    	infoBtn.setOnClickListener(new Button.OnClickListener() {
		        public void onClick(View v) {
		            String firmNameStr = "";
					try {
						firmNameStr = URLEncoder.encode(firmNameET.getText().toString(),  HTTP.UTF_8);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					
		            String firmUrlStr = firmUrlET.getText().toString();
		            
		            String firmaddressStr = "";
					try {
						firmaddressStr = URLEncoder.encode(firmAddressET.getText().toString(),  HTTP.UTF_8);
					} catch (UnsupportedEncodingException e) {
					}
					
		            String telNumberStr = telNumberET.getText().toString();
		            
		            String userNameStr = "";
					try {
						userNameStr = URLEncoder.encode(userNameET.getText().toString(),  HTTP.UTF_8);
					} catch (UnsupportedEncodingException e) {
					}
					
					String antiUrlStr = antiUrlET.getText().toString();
					antiUrl = antiUrlStr;//保存第一次完善资料中的防伪地址，用于接下来的新增产品
		            
		            memberInfoAddSubmit(userId, userNameStr, firmNameStr, firmUrlStr, firmaddressStr, telNumberStr,  antiUrlStr);
		        }
		    });
	    }
	    
		/** 会员项 */
	    class ViewHolder {
			TextView memberId;
			TextView memberTime;
			Button cardDelBtn;
		}
	    
	    /** 管理会员 */
	    private class MemberListAdapter extends BaseAdapter {
	    	
	    	private LayoutInflater mInflater;
	    	
	    	public MemberListAdapter(Context context) {
	            this.mInflater = LayoutInflater.from(context);
	        }

			public View getView(final int position, View convertView, ViewGroup parent) {
				ViewHolder holder = null;

	            if (convertView == null) {

	                holder = new ViewHolder();
	                convertView = mInflater.inflate(R.layout.carditem, null);
	                holder.memberId = (TextView) convertView.findViewById(R.id.card_id);
	                holder.memberTime = (TextView) convertView.findViewById(R.id.card_type);
	                holder.cardDelBtn = (Button) convertView.findViewById(R.id.btn_card_del);

	                convertView.setTag(holder);
	            } else {
	                holder = (ViewHolder) convertView.getTag();
	            }
	            
	            holder.memberId.setText(memberListBean.get(position).cardid);
	            holder.memberTime.setText(memberListBean.get(position).createTime);
	            
	            //删除
	            holder.cardDelBtn.setOnClickListener(new Button.OnClickListener() {
	    	        public void onClick(View v) {
//	    	        	memberDelSubmit(memberListBean.get(position).id);
	    	        	
	    	        	deleteMembConfirmDialog(memberListBean.get(position).id);
	    	        }
	    	    });
	            
	            convertView.setOnClickListener(new Button.OnClickListener() {
	    	        public void onClick(View v) {
	    	        	Intent intent = new Intent();        
	    				intent.setClass(context, MemberInfoActivity.class);
	    				intent.putExtra(Config.MembID,  memberListBean.get(position).id);
	    				intent.putExtra(Config.MembCardID,  memberListBean.get(position).cardid);
	    				startActivity(intent);
	    	        }
	    	    });
	            
				return convertView;
			}

			public Object getItem(int position){
				return memberListBean.get(position);
			}

			@Override
			public int getCount() {
				return memberListBean.size();
			}

			@Override
			public long getItemId(int position) {
				return position;
			}
			
		}
	    
	    /** 产品项 */
	    class ProductViewHolder {
			TextView productId;
			TextView productTime;
			Button productDelBtn;
		}
	    
	    /** 会员管理产品 */
	    private class CardListAdapter extends BaseAdapter {
	    	
	    	private LayoutInflater mInflater;
	    	
	    	public CardListAdapter(Context context) {
	            this.mInflater = LayoutInflater.from(context);
	        }

			public View getView(final int position, View convertView, ViewGroup parent) {
				ProductViewHolder holder = null;

	            if (convertView == null) {

	                holder = new ProductViewHolder();
	                convertView = mInflater.inflate(R.layout.productitem, null);
	                holder.productId = (TextView) convertView.findViewById(R.id.card_id);
	                holder.productTime = (TextView) convertView.findViewById(R.id.card_type);
	                holder.productDelBtn = (Button) convertView.findViewById(R.id.btn_card_del);

	                convertView.setTag(holder);
	            } else {
	                holder = (ProductViewHolder) convertView.getTag();
	            }
	            
	            holder.productId.setText(productListBean.get(position).cardid);
	            holder.productTime.setText(productListBean.get(position).createtime);
	          //删除
	            holder.productDelBtn.setOnClickListener(new Button.OnClickListener() {
	    	        public void onClick(View v) {
	    	        	productDelSubmit(productListBean.get(position).id);
	    	        }
	    	    });
	            
	            convertView.setOnClickListener(new Button.OnClickListener() {
	    	        public void onClick(View v) {
	    	        	Intent intent = new Intent();        
	    				intent.setClass(context, ProductInfoActivity.class);
	    				intent.putExtra(Config.ProductID,  productListBean.get(position).cardid);
	    				startActivity(intent);
	    	        }
	    	    });
	            
				return convertView;
			}

			public Object getItem(int position){
				return productListBean.get(position);
			}

			@Override
			public int getCount() {
				return productListBean.size();
			}

			@Override
			public long getItemId(int position) {
				return position;
			}
			
		}
	    
	    /** 增加产品 */
	    private void productAddSubmit(String mCardId, String mUserId, String mAntiUrl, String mType) {
	    	showDialog();
	    	
	    	String url = CardListBean.getAddCardURL(mCardId,  mUserId, mAntiUrl, mType);
	        BeanParser addProductXmlHandler = new BeanParser();
	        Net chaseListNet = new Net(this, url, addProductXmlHandler, productHandler, Config.CardAdd_OK);
	        chaseListNet.start();
	    }
	    
	    /** 产品列表 */
	    private void productListSubmit(String mUserId) {
	    	String url = CardListBean.getCardListURL(mUserId);
	    	CardListParser cardListXmlHandler = new CardListParser();
	        Net chaseListNet = new Net(this, url, cardListXmlHandler, productHandler, Config.CardList_OK);
	        chaseListNet.start();
	    }
	    
	    /** 删除产品 */
	    private void productDelSubmit(String mId) {
	    	showDialog();
	    	
	    	String url = CardListBean.getDelCardURL(mId);
	        BeanParser addProductXmlHandler = new BeanParser();
	        Net chaseListNet = new Net(this, url, addProductXmlHandler, productHandler, Config.CardDel_OK);
	        chaseListNet.start();
	    }
	    
	    /** 增加会员 */
	    private void memberAddSubmit(String mCardId) {
	    	showDialog();
	    	
	    	String url = MemberListBean.getAddMemberURL(mCardId);
	        BeanParser addCardXmlHandler = new BeanParser();
	        Net chaseListNet = new Net(this, url, addCardXmlHandler, memberHandler, Config.CardAdd_OK);
	        chaseListNet.start();
	    }
	    
	    /** 完善会员资料 */
	    private void memberInfoAddSubmit(String mId, String mName, String mFirmName, String mUrl, String mAddress, String mTelNumber, String mAntiUrl) {
	    	showDialog();
	    	  
	    	String url = MemberListBean.getMemberInfoAddURL(mId, mName, mFirmName, mUrl, mAddress, mTelNumber, mAntiUrl);
	        BeanParser addCardXmlHandler = new BeanParser();
	        Net chaseListNet = new Net(this, url, addCardXmlHandler, memberHandler, Config.CardInfoAdd_OK);
	        chaseListNet.start();
	    }
	    
	    /** 验证NFC卡登录 */
	    private void checkIdSubmit(String mCardId) {
	    	showDialog();
	    	
	    	String url = NfcCardBean.checkNfcCardURL(mCardId);
	    	NfcCardParser nfcCardXmlHandler = new NfcCardParser();
	        Net chaseListNet = new Net(this, url, nfcCardXmlHandler, memberHandler, Config.CardCheck_OK);
	        chaseListNet.start();
	    }
	    
	    /** 会员列表 */
	    private void memberListSubmit() {
	    	String url = MemberListBean.getMemberListURL();
	    	MemberListParser memberListXmlHandler = new MemberListParser();
	        Net chaseListNet = new Net(this, url, memberListXmlHandler, memberHandler, Config.CardList_OK);
	        chaseListNet.start();
	    }
	    
	    /** 修改防伪地址 */
	    private void memberAntiUrlSubmit(String mId, String mAntiUrl, String mRegsUrl) {
	    	String url = MemberBean.getAntiUrlModifyURL(mId, mAntiUrl, mRegsUrl);
	    	BeanParser memberListXmlHandler = new BeanParser();
	        Net chaseListNet = new Net(this, url, memberListXmlHandler, memberHandler, Config.AntiURL_OK);
	        chaseListNet.start();
	    }
	    
	    /** 修改未注册卡的默认跳转地址 */
	    private void fatalCardUrlSubmit(String mUrl) {
	    	String url = MemberBean.getFatalCardUrlModifyURL(mUrl);
	    	BeanParser memberListXmlHandler = new BeanParser();
	        Net chaseListNet = new Net(this, url, memberListXmlHandler, memberHandler, Config.AntiURL_OK);
	        chaseListNet.start();
	    }
	    
	    /** 删除会员 */
	    private void memberDelSubmit(String mId) {
	    	showDialog();
	    	
	    	String url = MemberListBean.getDelMemberURL(mId);
	        BeanParser delMembXmlHandler = new BeanParser();
	        Net chaseListNet = new Net(this, url, delMembXmlHandler, memberHandler, Config.CardDel_OK);
	        chaseListNet.start();
	    }
	    
	    private void memberAddResult(BaseBean mBaseBean) {
	        if (mBaseBean != null) {
	            String respCode = mBaseBean.getRespCode();
	            if (respCode.equalsIgnoreCase(Config.respCode_ok)) {
	            	Tools.DisplayToast(this, this.getString(R.string.ADD_OK));
	            	
	            	memberListSubmit();
	            } 

	            displayError(mBaseBean);
	        } else {
	            Tools.DisplayToast(this, this.getString(R.string.NET_ERROR));
	        }
	    }
	    
	    private void memberDelResult(BaseBean mBaseBean) {
	        if (mBaseBean != null) {
	            String respCode = mBaseBean.getRespCode();
	            if (respCode.equalsIgnoreCase(Config.respCode_ok)) {
	            	Tools.DisplayToast(this, this.getString(R.string.DEL_OK));
	            	
	            	memberListSubmit();
	            }

	            displayError(mBaseBean);
	        } else {
	            Tools.DisplayToast(this, this.getString(R.string.NET_ERROR));
	        }
	    }
	   
	    private void memberInfoAddResult(BaseBean mBaseBean) { 
	        if (mBaseBean != null) {
	            String respCode = mBaseBean.getRespCode();
	            if (respCode.equalsIgnoreCase(Config.respCode_ok)) {
	            	Tools.DisplayToast(this, this.getString(R.string.INFO_ADD_OK));
	            	
	            	productListSubmit(userId);
            		product_member_ll.setVisibility(View.VISIBLE);
            		member_first_ll.setVisibility(View.GONE);
	            } 
	            
	            displayError(mBaseBean);
	        } else {
	            Tools.DisplayToast(this, this.getString(R.string.NET_ERROR));
	        }
	    }
	    
	    private void memberListResult(MemberListBean mCardListBean) {
	        if (mCardListBean != null) {
	            String respCode = mCardListBean.getRespCode();
	            if (respCode.equalsIgnoreCase(Config.respCode_ok)) {
	            	memberListBean.clear();
	            	memberListBean.addAll(mCardListBean.MembList);
	            	mMemberListAdapter.notifyDataSetChanged();
	            } 

	            displayError(mCardListBean);
	        } else {
	            Tools.DisplayToast(this, this.getString(R.string.NET_ERROR));
	        }
	    }
	    
	    private void AntiUrlModifyResult(BaseBean mBaseBean) {
	        if (mBaseBean != null) {
	            String respCode = mBaseBean.getRespCode();
	            if (respCode.equalsIgnoreCase(Config.respCode_ok)) {
	            	Tools.DisplayToast(this, this.getString(R.string.ANTI_URL_OK));
	            	antiUrl = tempAntiUrl;
	            } 
	            
	            displayError(mBaseBean);
	        } else {
	            Tools.DisplayToast(this, this.getString(R.string.NET_ERROR));
	        }
	    }
	    
	    private Handler memberHandler = new BaseNetHandler(this){
	    	public void handleBean(int returnCode, BaseBean baseBean) {
	    		progressDialog.dismiss();
	    		
				switch (returnCode) {
		            case Config.CardList_OK:
		                memberListResult((MemberListBean) baseBean);
		                break;
		            case Config.CardAdd_OK:
		            	memberAddResult(baseBean);
		                break;
		            case Config.CardDel_OK:
		            	memberDelResult(baseBean);
		                break;
		            case Config.CardCheck_OK:
		            	checkCardResult((NfcCardBean) baseBean);
		                break;
		            case Config.CardInfoAdd_OK:
		            	memberInfoAddResult(baseBean);
		                break;
		            case Config.AntiURL_OK:
		            	AntiUrlModifyResult(baseBean);
		            	break;
		            }
			}

			public void handleFirst() {
//				progressBarLayout.setVisibility(View.GONE);
			}
			
			public void handleNetError(){
				progressDialog.dismiss();
				Tools.DisplayToast(context, context.getString(R.string.NET_ERROR));
			}
	    };
	    
	    /** 验证Id结果 */
	    private void checkCardResult(NfcCardBean mMemberBean) {
	        if (mMemberBean != null) {
	            String respCode = mMemberBean.getRespCode();
	            if (respCode.equalsIgnoreCase(Config.respCode_ok)) {
	            	if(mMemberBean.cardtype.equalsIgnoreCase(adminFlag)){
	            		login_adminFlag = true;
	            		memberListSubmit();
	            		login_ll.setVisibility(View.VISIBLE);
	            	}else if(mMemberBean.cardtype.equalsIgnoreCase(memberFlag)) {
	            		login_adminFlag = false;
	            		userId = mMemberBean.id;
	            		antiUrl = mMemberBean.antiurl;
	            		
	            		if(!mMemberBean.logined.equalsIgnoreCase("0")){
	            			productListSubmit(userId);
		            		product_member_ll.setVisibility(View.VISIBLE);
	            		}else{
	            			member_first_ll.setVisibility(View.VISIBLE);
	            			cardIdTV.setText(mMemberBean.cardid);
	            		}
	            		
	            	}else if(mMemberBean.cardtype.equalsIgnoreCase(productFlag) || 
	            					mMemberBean.cardtype.equalsIgnoreCase(consumerFlag) ||
	            					mMemberBean.cardtype.equalsIgnoreCase(fetalFlag)) {
	            		antiUrl = mMemberBean.cardurl;
	            		login_adminFlag = false;
	        			
	            		String targetUrl = antiUrl;
	        			//验证网址是不是http://开头
						if (!(antiUrl.startsWith("http://"))){
							targetUrl = "http://" + targetUrl;
						}

						try{
							Intent intent = new Intent();
							intent.setAction("android.intent.action.VIEW");
							Uri content_url = Uri.parse(targetUrl);
							intent.setData(content_url);
							startActivity(intent);

							this.finish();
						}catch(Exception et){
							Tools.showToast(context, antiUrl + "不是完整网址，请用http://开头");
						}
						
	            		
	            	}
	            	
//	            	nologin_ll.setVisibility(View.GONE);
	            	loginFlag = true;
//	            	Tools.DisplayToast(this, "登录成功!");
	            } 
	            
	            displayError(mMemberBean);
	        } else {
	            Tools.DisplayToast(this, this.getString(R.string.NET_ERROR));
	        }
	    }
	    
	    private Handler productHandler = new BaseNetHandler(this){
	    	public void handleBean(int returnCode, BaseBean baseBean) {
	    		progressDialog.dismiss();
	    		
				switch (returnCode) {
		            case Config.CardList_OK:
		            	productListResult((CardListBean) baseBean);
		                break;
		            case Config.CardAdd_OK:
		            	productAddResult(baseBean);
		                break;
		            case Config.CardDel_OK:
		            	productDelResult(baseBean);
		                break;
		            }
			}

			public void handleFirst() {
//				progressBarLayout.setVisibility(View.GONE);
			}
			
			public void handleNetError(){
				progressDialog.dismiss();
				Tools.DisplayToast(context, context.getString(R.string.NET_ERROR));
			}
	    };
	    
	    private void productListResult(CardListBean mCardListBean) {
	        if (mCardListBean != null) {
	            String respCode = mCardListBean.getRespCode();
	            if (respCode.equalsIgnoreCase(Config.respCode_ok)) {
	            	productListBean.clear();
	            	productListBean.addAll(mCardListBean.CardList);
	            	mCardListAdapter.notifyDataSetChanged();
	            } 
	            
	            displayError(mCardListBean);
	        } else {
	            Tools.DisplayToast(this, this.getString(R.string.NET_ERROR));
	        }
	    }
	    
	    private void productAddResult(BaseBean mBaseBean) {
	        if (mBaseBean != null) {
	            String respCode = mBaseBean.getRespCode();
	            if (respCode.equalsIgnoreCase(Config.respCode_ok)) {
	            	Tools.DisplayToast(this, this.getString(R.string.ADD_OK));
	            	
	            	productListSubmit(userId);
	            } 
	            
	            displayError(mBaseBean);
	        } else {
	            Tools.DisplayToast(this, this.getString(R.string.NET_ERROR));
	        }
	    }
	    
	    private void productDelResult(BaseBean mBaseBean) {
	        if (mBaseBean != null) {
	            String respCode = mBaseBean.getRespCode();
	            if (respCode.equalsIgnoreCase(Config.respCode_ok)) {
	            	Tools.DisplayToast(this, this.getString(R.string.DEL_OK));
	            	
	            	productListSubmit(userId);
	            } 

	            displayError(mBaseBean);
	        } else {
	            Tools.DisplayToast(this, this.getString(R.string.NET_ERROR));
	        }
	    }

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			Dialog alertDialog = new AlertDialog.Builder(this)
//					.setTitle("确定退出？")
//					.setMessage("您确定确定退出吗？")
//					.setIcon(R.drawable.ic_launcher)
//					.setPositiveButton("确定",
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int which) {
//									context.finish();
//								}
//							})
//					.setNegativeButton("取消",
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int which) {
//
//								}
//							}).create();
//			alertDialog.show();
//		}
//
//		return super.onKeyDown(keyCode, event);
//	}
	    
	/** 加载提示 */
	private void showDialog(){
		if(progressDialog != null){
			if(!(progressDialog.isShowing())){
				progressDialog.show();
			}
		}else{
			progressDialog = ProgressDialog.show(context, "", "正在加载中...",  true,  true);  
		}
	}
	
	/** 显示错误信息 */
	private void displayError(BaseBean mBaseBean){
		if (mBaseBean.getRespCode().equalsIgnoreCase(Config.respCode_fetal)) {
            Tools.DisplayToast(this, mBaseBean.getRespMesg());
        }  
        else if (mBaseBean.getRespCode().equalsIgnoreCase(Config.respCode_fail)) {
            Tools.DisplayToast(this, this.getString(R.string.NET_ERROR));
        } 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		if (loginFlag == true) {//未登录
			if (login_adminFlag == true) {
				menu.add("默认地址");
			} else {
				menu.add(Menu.NONE, Menu.FIRST + 1, 1, "防伪网址");
				menu.add(Menu.NONE, Menu.FIRST + 2, 2, "新增用户");
			}
		}
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final EditText antiUrlET = new EditText(context);
		antiUrlET.setText(antiUrl);// 设置已有网址

		if (login_adminFlag == true) {
			new AlertDialog.Builder(this)
					.setTitle("默认网址")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setView(antiUrlET)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									tempAntiUrl = antiUrlET.getText()
											.toString().trim();
									fatalCardUrlSubmit(tempAntiUrl);
								}
							}).show();
		} else {
			switch (item.getItemId()) {
			case Menu.FIRST + 1:// 产品标签防伪
				add_consumer = false;
				
				new AlertDialog.Builder(this)
						.setTitle("防伪网址")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(antiUrlET)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										tempAntiUrl = antiUrlET.getText().toString().trim();
										memberAntiUrlSubmit(userId, tempAntiUrl, regsUrl);
									}
								}).show();

				break;
			case Menu.FIRST + 2://新增用户
				add_consumer = true;//标志新增用户
				
				LayoutInflater inflater = LayoutInflater.from(context);
				View addUserView = inflater.inflate(R.layout.adduser, null);
				final EditText regET = (EditText)addUserView.findViewById(R.id.reg_et);
				final EditText fanweiET = (EditText)addUserView.findViewById(R.id.fanwei_et);
				
				new AlertDialog.Builder(this)
						.setTitle("新增用户")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(addUserView)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										tempAntiUrl = fanweiET.getText().toString().trim();
										regsUrl = regET.getText().toString().trim();
										memberAntiUrlSubmit(userId, tempAntiUrl, regsUrl);
									}
								}).show();
				break;
			}
		}

		return true;
	}
	
	/** 删除会员确认框 */
	private void deleteMembConfirmDialog(final String membId) {
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("删除会员")
				.setMessage("删除该会员,与其关联的标签都将删除，请确认?").setIcon(R.drawable.ic_launcher)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						memberDelSubmit(membId);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				}).create();
		alertDialog.show();
	}
    
    
}