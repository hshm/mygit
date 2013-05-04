package com.mynfc.xmlparser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.mynfc.data.BaseBean;



public class BeanParser extends BaseParser{
	
    private BaseBean bean;
    private StringBuilder builder;
   
    public BeanParser(){
    	bean = new BaseBean();
    }
    
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        builder.append(ch, start, length);
    }
    
    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        super.endElement(uri, localName, name);
        if (this.bean != null){
        	if(localName.equalsIgnoreCase(bean.RESPCODE)){
        		bean.setRespCode(builder.toString());
        	}
        	else if(localName.equalsIgnoreCase(bean.RESPMESG)){
        		bean.setRespMesg(builder.toString());
        	}
            builder.setLength(0);   
        }
    }
    
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        
        builder = new StringBuilder();
    }
    
    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
        super.startElement(uri, localName, name, attributes);

    }

    public BaseBean getDataBean(){
    	return this.bean;
    }
}
