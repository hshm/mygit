package com.mynfc.xmlparser;

import org.xml.sax.helpers.DefaultHandler;

import com.mynfc.data.BaseBean;

public abstract class BaseHandler extends DefaultHandler{
	public abstract BaseBean getDataBean();
}
