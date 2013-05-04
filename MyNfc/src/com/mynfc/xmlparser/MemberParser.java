package com.mynfc.xmlparser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.mynfc.data.BaseBean;
import com.mynfc.data.MemberBean;

public class MemberParser extends BaseHandler{
	private MemberBean memberBean;
	private StringBuilder builder;

	public MemberParser() {
		memberBean = new MemberBean();
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
		if (this.memberBean != null) {
			if (localName.equalsIgnoreCase(memberBean.RESPCODE)) {
				memberBean.setRespCode(builder.toString());
			} else if (localName.equalsIgnoreCase(memberBean.RESPMESG)) {
				memberBean.setRespMesg(builder.toString());
			} else if (localName.equalsIgnoreCase(memberBean.Type)) {
				memberBean.type = builder.toString();
            } else if (localName.equalsIgnoreCase(memberBean.ID)) {
				memberBean.id = builder.toString();
            }  else if (localName.equalsIgnoreCase(memberBean.USER_NAME)) {
				memberBean.userName = builder.toString();
            } else if (localName.equalsIgnoreCase(memberBean.Firm_Address)) {
				memberBean.address = builder.toString();
            } else if (localName.equalsIgnoreCase(memberBean.Firm_Name)) {
				memberBean.firmName = builder.toString();
            } else if (localName.equalsIgnoreCase(memberBean.Firm_Url)) {
				memberBean.url = builder.toString();
            } else if (localName.equalsIgnoreCase(memberBean.Tel_Number)) {
				memberBean.telnumber = builder.toString();
            } else if (localName.equalsIgnoreCase(memberBean.Card_id)) {
				memberBean.cardid = builder.toString();
            } else if (localName.equalsIgnoreCase(memberBean.Anti_Url)) {
				memberBean.antiUrl = builder.toString();
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

		return this.memberBean;
	}
}
