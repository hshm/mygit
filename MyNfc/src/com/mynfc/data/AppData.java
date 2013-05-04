package com.mynfc.data;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

/**
 * @Description: 
 * @author samy  
 * @date 2012-9-18 下午02:52:36 
 * @version V1.1.0
 */
public class AppData {
	
	/**
     * activityList:所有activity对象，用于退出时全部finish; 
     * Activity跳转走下面发方法：
     * public static void forwardTarget(Activity srcClass, Class<?> descClass, String name, ArrayList<String> parameters)
     * 添加到该集合
	 */
    public static List<Activity> activityList = new ArrayList<Activity>();

}
