package com.mynfc.xmlparser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.mynfc.data.BaseBean;
import com.mynfc.data.MemberListBean;
import com.mynfc.data.MemberListBean.MembBean;

public class MemberListParser extends BaseHandler{
	private MemberListBean memberListBean;
	private MembBean  membBean;
	private StringBuilder builder;

	public MemberListParser() {
		memberListBean = new MemberListBean();
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
		if (this.memberListBean != null) {
			if (localName.equalsIgnoreCase(memberListBean.RESPCODE)) {
				memberListBean.setRespCode(builder.toString());
			} else if (localName.equalsIgnoreCase(memberListBean.RESPMESG)) {
				memberListBean.setRespMesg(builder.toString());
			} else if (localName.equalsIgnoreCase(memberListBean.ID)) {
				membBean.id = builder.toString();
            } else if (localName.equalsIgnoreCase(memberListBean.CardId)) {
				membBean.cardid = builder.toString();
            } else if (localName.equalsIgnoreCase(memberListBean.CreateTime)) {
            	membBean.createTime = builder.toString();
            } else if (localName.equalsIgnoreCase(memberListBean.ITEM)) {
            	memberListBean.MembList.add(membBean);
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
		
		if (localName.equalsIgnoreCase(memberListBean.ITEM)) {
			membBean = memberListBean.new MembBean();
        }
	}

	@Override
	public BaseBean getDataBean() {

		return this.memberListBean;
	}
}
