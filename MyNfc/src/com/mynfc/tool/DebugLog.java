package com.mynfc.tool;

import java.text.SimpleDateFormat;

import android.util.Log;

public class DebugLog {
	public static void log(String className,String logInfo){
		Log.i("", "----------------------");
		Log.i(className, logInfo);
		
	}
	
	public static void logOnFile(String TAG, String format, Object... args){
		
		String logstr = String.format(format, args);
		
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd   hh:mm:ss");      
		String date   =   sDateFormat.format(new java.util.Date());  
		logstr = "\r\n-------------------------\r\n" + date + "\r\n" + logstr;
		
		Log.i(TAG, logstr);
		
	}
	
}
