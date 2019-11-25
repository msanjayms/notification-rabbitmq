package org.mylearning.notification.sms;

import java.io.StringWriter;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class SMSMessage {
	private String sMSType="SMS";
	private RcpPhone rcpPhone; 
	private String body;
	private String from="MServer";
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public RcpPhone getRcpPhone() {
		return rcpPhone;
	}

	public void setRcpPhone(RcpPhone rcpPhone) {
		this.rcpPhone = rcpPhone;
	}

	public String getSMSType() {
		return sMSType;
	}

	public void setSMSType(String type) {
		sMSType = type;
	}

	public String toXML() {
		StringBuffer sb = new StringBuffer();
		sb.append("<SMSMessage");
		if(getSMSType()!=null) {
			sb.append(" SMSType=\"");
			sb.append(getSMSType());
			sb.append("\"");
		}
		sb.append(">");
		sb.append(getRcpPhone().toXML());
		sb.append("<From>");
		sb.append(getFrom());
		sb.append("</From>");
		
		try {
			sb.append("\n");
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element bodyEle = doc.createElement("Body");
			doc.appendChild(bodyEle);
			Text xBodyTxt = doc.createTextNode(getBody());
			bodyEle.appendChild(xBodyTxt);
			
			//set up a transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");

			//create string from xml tree
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(doc);
			trans.transform(source, result);
			sb.append(sw.toString());			
		} catch (Exception e) {
			EnMessage.logger.log(Level.SEVERE , "XML parse err :" + e, e);
		} 
		sb.append("</SMSMessage>");
		return sb.toString();
	}
}

