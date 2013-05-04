package com.mynfc.xmlparser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.mynfc.data.BaseBean;
import com.mynfc.data.ProcutBean;

public class ProductParser extends BaseHandler{
	private ProcutBean productBean;
	private StringBuilder builder;

	public ProductParser() {
		productBean = new ProcutBean();
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
		if (this.productBean != null) {
			if (localName.equalsIgnoreCase(productBean.RESPCODE)) {
				productBean.setRespCode(builder.toString());
			} else if (localName.equalsIgnoreCase(productBean.RESPMESG)) {
				productBean.setRespMesg(builder.toString());
			} else if (localName.equalsIgnoreCase(productBean.FW_URL)) {
				productBean.fw_url = builder.toString();
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

	@Override
	public BaseBean getDataBean() {

		return this.productBean;
	}
}
