package com.mynfc.xmlparser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.mynfc.data.BaseBean;
import com.mynfc.data.CardListBean;
import com.mynfc.data.CardListBean.CardBean;

public class CardListParser extends BaseHandler{
	private CardListBean cardListBean;
	private CardBean cardBean;
	private StringBuilder builder;

	public CardListParser() {
		cardListBean = new CardListBean();
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
		if (this.cardListBean != null) {
			if (localName.equalsIgnoreCase(cardListBean.RESPCODE)) {
				cardListBean.setRespCode(builder.toString());
			} else if (localName.equalsIgnoreCase(cardListBean.RESPMESG)) {
				cardListBean.setRespMesg(builder.toString());
			} else if (localName.equalsIgnoreCase(cardListBean.ID)) {
				cardBean.id= builder.toString();
            }else if (localName.equalsIgnoreCase(cardListBean.CardId)) {
				cardBean.cardid = builder.toString();
            } else if (localName.equalsIgnoreCase(cardListBean.CARDTIME)) {
            	cardBean.createtime = builder.toString();
            } else if (localName.equalsIgnoreCase(cardListBean.ITEM)) {
            	cardListBean.CardList.add(cardBean);
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
		
		if (localName.equalsIgnoreCase(cardListBean.ITEM)) {
			cardBean = cardListBean.new CardBean();
        }
	}

	@Override
	public BaseBean getDataBean() {

		return this.cardListBean;
	}
}
