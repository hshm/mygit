package com.mynfc.net;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mynfc.data.BaseBean;
import com.mynfc.tool.DebugLog;
import com.mynfc.xmlparser.BaseHandler;
import com.mynfc.xmlparser.BaseParser;
import com.mynfc.xmlparser.BeanParser;

public class ParserData {
	
	public BaseBean parser(InputStream is,BaseHandler handler){
		
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser parser = null;
        try {
			parser = spf.newSAXParser();
			parser.parse(is,handler);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return handler.getDataBean();
	}
	
	public BaseBean domParser(InputStream is) {
		BeanParser beanHandler = new BeanParser();
		
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document dom = null;
		try {
			db = dbfactory.newDocumentBuilder();
			dom = db.parse(is);
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		Element docEle = dom.getDocumentElement();
		NodeList nl = docEle.getElementsByTagName("rss");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element entry = (Element) nl.item(i);
				Element title = (Element) entry.getElementsByTagName("respCode").item(0);
				Element when = (Element) entry.getElementsByTagName("respMesg").item(0);
				DebugLog.log(Net.class.toString(),title.getNodeValue() + "|" + when.getNodeValue());
			}
		}
		
		return beanHandler.getDataBean();
	}
}
