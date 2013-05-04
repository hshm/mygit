package com.mynfc.xmlparser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.mynfc.data.BaseBean;
import com.mynfc.data.CardListBean;
import com.mynfc.data.CardListBean.CardBean;
import com.mynfc.data.NfcCardBean;

public class NfcCardParser extends BaseHandler{
	private NfcCardBean cardBean;
	private StringBuilder builder;

	public NfcCardParser() {
		cardBean = new NfcCardBean();
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
		if (this.cardBean != null) {
			if (localName.equalsIgnoreCase(cardBean.RESPCODE)) {
				cardBean.setRespCode(builder.toString());
			} else if (localName.equalsIgnoreCase(cardBean.RESPMESG)) {
				cardBean.setRespMesg(builder.toString());
			} else if (localName.equalsIgnoreCase(cardBean.CardType)) {
				cardBean.cardtype= builder.toString();
            }else if (localName.equalsIgnoreCase(cardBean.CardUrl)) {
				cardBean.cardurl = builder.toString();
            } else if (localName.equalsIgnoreCase(cardBean.InfoCompleted)) {
				cardBean.logined = builder.toString();
            } else if (localName.equalsIgnoreCase(cardBean.Id)) {
				cardBean.id = builder.toString();
            } else if (localName.equalsIgnoreCase(cardBean.CardId)) {
				cardBean.cardid = builder.toString();
            } else if (localName.equalsIgnoreCase(cardBean.AntiUrl)) {
				cardBean.antiurl = builder.toString();
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

		return this.cardBean;
	}
}
