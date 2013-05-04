package com.mynfc.handler;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.mynfc.R;
import com.mynfc.data.BaseBean;
import com.mynfc.tool.Config;
import com.mynfc.tool.Tools;

/**
 * 连接处理父类
 * 
 * @project ChickenLottery
 * @author zhw
 * @date 2011-12-30
 * Copyright (C) 2010-2012 www.2caipiao.com Inc. All rights reserved.
 */
public abstract class BaseNetHandler extends Handler{
	public Context context;
	
	/**
	 * 连接处理父类
	 */
	public BaseNetHandler(Context context){
		this.context = context;
	}
	
	public void handleMessage(Message msg) {
		handleFirst();
		switch (msg.what) {
			case Config.NET_SETERROR:				//网络配置有问题
//	            Tools.forwardNetSetDialog((Activity) context);
	            break;    
	        case Config.NET_ERROR:					//网络失败
	        	handleNetError();
	            break;
			default:								//有返回
				BaseBean baseBean = (BaseBean)msg.obj;
				if (baseBean != null) {
					handleBean(msg.what, baseBean);
				}else{
					handleBeanNull();
				}
				break;
		}
	}
	
	/**
	 * 有消息返回时,立即运行的操作<br/>
	 * 例如:进度条消失等
	 */
	public abstract void handleFirst();

	/**
	 * 成功返回后,自定义的操作<br/>
	 * 例如: 绑定显示数据等
	 * @param returnCode 成功返回的int值
	 * @param baseBean 返回的解析后的对象
	 */
	public abstract void handleBean(int returnCode, BaseBean baseBean);
	
	/**
	 * 网络异常的处理<br/>
	 * 例如:连接失败等<br/>
	 * 可覆盖此方法
	 */
	public abstract void handleNetError();
	
	/**
	 * 网络异常的处理<br/>
	 * 例如:连接失败等<br/>
	 * 可覆盖此方法
	 */
//	public void handleNetError(){
//		Tools.DisplayToast(context, context.getString(R.string.NET_ERROR));
//	}

	/**
	 * 返回bean为空的处理<br/>
	 * 可覆盖此方法
	 */
	public void handleBeanNull(){
		Tools.DisplayToast(context, context.getString(R.string.NET_ERROR));
	}
	
}
