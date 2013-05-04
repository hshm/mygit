package com.mynfc.tool;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.mynfc.data.AppData;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.widget.Toast;

/**
 * @Description: 常用方法
 * @author samy  
 * @date 2012-9-7 下午04:54:01 
 * @version V1.1.0
 */
public class Tools {

	/**
	 * 页面跳转
	 * @param srcClass
	 * @param descClass
	 * @param name
	 * @param parameters
	 *
	 * 2012-9-13下午03:38:32
	 */
    public static void forwardTarget(Activity srcClass, Class<?> descClass, String name, ArrayList<String> parameters) {
        Intent intent = new Intent();
        
        intent.setClass(srcClass, descClass);
        intent.putStringArrayListExtra(name, parameters);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
        srcClass.startActivity(intent);
    }
    
	/**
	 * 页面跳转
	 * @param srcClass
	 * @param descClass
	 * @param name
	 * @param parameters
	 *
	 * 2012-9-13下午03:38:32
	 */
    public static void forwardTarget(Activity srcClass, Class<?> descClass) {
        Intent intent = new Intent();
        intent.setClass(srcClass, descClass);
        
        srcClass.startActivity(intent);
    }
    
    public static void showToast(Context context, CharSequence chars) {
    	Toast.makeText(context, chars, Toast.LENGTH_SHORT).show();
	}
    
    /**
     * 格式数字,保留小数位
     * rul:0.00
     * mData:32.234
     */
    public static String getFormatData(String rul, String mData){
    	DecimalFormat df = new DecimalFormat(rul);
    	return df.format(Double.parseDouble(mData));
    } 
    
	/** 判断是否有网络 */
	public static boolean checkActionNet(Context context) {
		boolean ActionNetFlag = false;

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null) {
			ActionNetFlag = true;
		}

		return ActionNetFlag;
	}
	
	 /**
     * 销毁所有页面
     * 
     * @create_time 2011-9-6 上午11:16:41
     */
    public static void destroyAllActivity() {
        for (int i = 0; i < AppData.activityList.size(); i++) {
            if (null != AppData.activityList.get(i)) {
                AppData.activityList.get(i).finish();  
            }
        }
    }
    
	public static void DisplayToast(Context context, String displayText) {
        Toast.makeText(context, displayText, Toast.LENGTH_SHORT).show();
    }
	
    public static String Bytes2HexString(byte[] b) {
    	String ret = "";
    	for (int i = 0; i < b.length; i++) {
    	String hex = Integer.toHexString(b[i] & 0xFF);
    	if (hex.length() == 1) {
    	hex = '0' + hex;
    	}
    	ret += hex.toUpperCase();
    	} 
    	return ret;
    	}
    
    
    public static int byteToInt2(byte[] b){
    	  return (((int)b[0]) << 24) + (((int)b[1]) << 16) + (((int)b[2]) << 8) + b[3];
    	 }

    public static String checkStr(String mStr) {
		if (mStr != null) {
			return mStr;
		} else {
			return "";
		}
	}
}
