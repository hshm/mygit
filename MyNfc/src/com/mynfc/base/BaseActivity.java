package com.mynfc.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.mynfc.data.AppData;

public class BaseActivity extends Activity{
	protected Activity context;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        context = this;
        
        AppData.activityList.add(context);
    }
}

