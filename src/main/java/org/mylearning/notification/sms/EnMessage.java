package org.mylearning.notification.sms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class EnMessage {

    public static final Logger logger = Logger.getLogger(EnMessage.class.getName());
    private static String dtdFilePath = null;
    private static boolean postBackMsgStat = false;
    private static int clientDebugLvl;
    private long refId = -1;
    private String messageType = "SMS";
    private String syncMode = "Async";
    private SMSMessage sMSMessage;
    private String successCode;
    private String responseCode;
    private String responseMessage;
    private String bankId;
    private String oldMessage;
    /**
     * where its stored in message server, internal use by message server only
     * */
    private String fileName;//
    /**
     * where its stored in message server, internal use by message server only
     * */
    private String clientId;//example ACS id - account id.
    private String senderId;//which provider sent (round robin/ fail over)
    private String providerId;//what provider returns (or what we give them to use)
    private String postBackUrl;
    
    /**
     * @return the clientId
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * @param clientId the clientId to set
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * @return the senderId
     */
    public String getSenderId() {
        return senderId;
    }

    /**
     * @param senderId the senderId to set
     */
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    /**
     * @return the bankId
     */
    public String getBankId() {
        return bankId;
    }

    /**
     * @param bankId the bankId to set
     */
    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    /**
     * @return the postBackUrl
     */
    public String getPostBackUrl() {
        return postBackUrl;
    }

    /**
     * @param postBackUrl the postBackUrl to set
     */
    public void setPostBackUrl(String postBackUrl) {
        this.postBackUrl = postBackUrl;
    }

    public static boolean getPostBackMsgStat() {
        return postBackMsgStat;
    }

    public static void setPostBackMsgStat(boolean b) {
        postBackMsgStat = b;
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(refId);
        sb.append(". syncMode :");
        sb.append(syncMode);
        sb.append(". successCode :");
        sb.append(successCode);
        sb.append(". bankId:");
        sb.append(bankId);
        sb.append(".");
        return sb.toString();
    }

    public String getProviderId() {
        return providerId;
    }

    /**
     * some providers return their own id.
     * need to use that id for delivery notification.
     * other providers use our id - then client and provider id can be set to same
     * or sent provider other unique id if you dont want to send account id for security.
     * */
    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getFileName() {
        return fileName;
    }

    /**
     * where its stored in message server, internal use by message server only
     * */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public long getRefId() {
        return refId;
    }

    public void setRefId(long refId) {
        this.refId = refId;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public SMSMessage getSMSMessage() {
        return sMSMessage;
    }

    public void setSMSMessage(SMSMessage message) {
        sMSMessage = message;
    }

    public String getSuccessCode() {
        return successCode;
    }

    public void setSuccessCode(String successCode) {
        this.successCode = successCode;
    }

    public String getSyncMode() {
        return syncMode;
    }

    public void setSyncMode(String syncMode) {
        this.syncMode = syncMode;
    }

    public String getOldMessage() {
        return oldMessage;
    }

    public void setOldMessage(String oldMessage) {
        this.oldMessage = oldMessage;
    }

    public String toXML() {
        StringBuffer sb = new StringBuffer();

        sb.append("<enMessage");

        if (getRefId() != -1) {
            sb.append(" RefId=\"");
            sb.append(getRefId());
            sb.append("\"");
        }

        sb.append(" MessageType=\"");
        sb.append(getMessageType());
        sb.append("\"");

        sb.append(" SyncMode=\"");
        sb.append(getSyncMode());
        sb.append("\"");
        sb.append('>');

        if (getSMSMessage() != null) {
            sb.append(getSMSMessage().toXML());
        }

        if (getMessageType() != null && getMessageType().equals("RESPONSE")) {
            if (getOldMessage() != null) {
                sb.append("<OldMessage>");
                sb.append(escapeSpecialCharacters(getOldMessage()));
                sb.append("</OldMessage>");
            }

            sb.append("<SuccessCode>");
            sb.append(getSuccessCode());
            sb.append("</SuccessCode>");

            if (getResponseCode() != null) {
                sb.append("<ResponseCode>");
                sb.append(escapeSpecialCharacters(getResponseCode()));
                sb.append("</ResponseCode>");
            }

            if (getResponseMessage() != null) {
                sb.append("<ResponseMessage>");
                sb.append(escapeSpecialCharacters(getResponseMessage()));
                sb.append("</ResponseMessage>");
            }
        }
        if (getPostBackMsgStat()) {
            logger.fine("post back true :" + new java.util.Date());
            sb.append("<ClientId>");
            sb.append(escapeSpecialCharacters(getClientId()));
            sb.append("</ClientId>");

            sb.append("<BankId>");
            sb.append(escapeSpecialCharacters(getBankId()));
            sb.append("</BankId>");

            sb.append("<SenderId>");
            sb.append(escapeSpecialCharacters(getSenderId()));
            sb.append("</SenderId>");

            sb.append("<ProviderId>");
            sb.append(escapeSpecialCharacters(getProviderId()));
            sb.append("</ProviderId>");

            sb.append("<PostBackUrl>");
            sb.append(escapeSpecialCharacters(getPostBackUrl()));
            sb.append("</PostBackUrl>");
        }

        sb.append("</enMessage>");
        if (clientDebugLvl > 10) {
            logger.log(Level.INFO, "XML :" + sb.toString() + ".");
        }
        return sb.toString();
    }

    public static String escapeSpecialCharacters(String xml){
		String escapedString = "";
		if (xml==null)  return "";
		if (xml.trim().equals("")) return "";
		escapedString = replaceString(xml,"&","&amp;");
		escapedString = replaceString(escapedString,"'","&apos;");
		escapedString = replaceString(escapedString,"\"","&quot;");
		escapedString = replaceString(escapedString,"<","&lt;");
		escapedString = replaceString(escapedString,">","&gt;");
		return escapedString;
	}
    
    private static String replaceString(String str,String str1,String str2){
		if (str == null)
		   return null;
		if (str1 == null)
		   return str;
		if (str2 == null)
		   return str;
		StringBuffer sbStr = new StringBuffer(str);
		int str1length = str1.length();
		int str2length = str2.length();
		int index = 0;
		int fromIndex = -1;
		index = sbStr.toString().indexOf(str1,fromIndex);
		while(index >= 0) {
		   sbStr.replace(index, index+str1length, str2);
		   fromIndex = index +str2length ;
		   if(fromIndex <= sbStr.length())
			   index = sbStr.toString().indexOf(str1,fromIndex);
		   else
			   index=-1;
		}
		return sbStr.toString();
   }
	
    public static EnMessage fromXML(String data) throws IOException {
        EnMessage enMessage = null;
        try {
          //  data = "<!DOCTYPE enMessage SYSTEM \"file://localhost/" + dtdFilePath + "\">" + data;

            if (clientDebugLvl > 10) {
                logger.log(Level.INFO, "XML :" + data + ".");
            }
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            //dbFactory.setValidating(true);
            DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();

            ErrorHandler eh = new MyErrorHandler();
            docBuilder.setErrorHandler(eh);

            Document doc =
                    docBuilder.parse(new ByteArrayInputStream(data.trim().getBytes("UTF-8")));
            enMessage = new EnMessage();

            Element element = doc.getDocumentElement();

            if (element.hasAttribute("RefId")) {
                enMessage.setRefId(Long.parseLong(element.getAttribute("RefId")));
            }
            if (element.hasAttribute("MessageType")) {
                enMessage.setMessageType(element.getAttribute("MessageType"));
            }
            if (element.hasAttribute("SyncMode")) {
                enMessage.setSyncMode(element.getAttribute("SyncMode"));
            }

            Node node = null;
            if (getPostBackMsgStat()) {
                //logger.fine("data :" + data + "|");
                NodeList clientNode = element.getElementsByTagName("ClientId");
                String s = getNodeValue2(element, "ClientId");
                logger.fine("c :" + s + "|");
                node = clientNode.item(0);
                if (node != null) {


                    enMessage.setClientId(node.getNodeValue());
                }
                if (enMessage.getClientId() == null) {
                    enMessage.setClientId(parseManual(data, "ClientId"));
                }
                NodeList senderNode = element.getElementsByTagName("SenderId");
                node = senderNode.item(0);
                if (node != null) {
                    enMessage.setSenderId(node.getNodeValue());
                }
                if (enMessage.getSenderId() == null) {
                    enMessage.setSenderId(parseManual(data, "SenderId"));
                }
                NodeList bankNode = element.getElementsByTagName("BankId");
                node = bankNode.item(0);
                if (node != null) {
                    enMessage.setBankId(node.getNodeValue());
                }
                if (enMessage.getBankId() == null) {
                    enMessage.setBankId(parseManual(data, "BankId"));
                }

                NodeList providerNode = element.getElementsByTagName("ProviderId");
                node = providerNode.item(0);
                if (node != null) {
                    enMessage.setProviderId(node.getNodeValue());
                }
                if (enMessage.getProviderId() == null) {
                    enMessage.setProviderId(parseManual(data, "ProviderId"));
                }

                NodeList urlNode = element.getElementsByTagName("PostBackUrl");
                node = urlNode.item(0);
                if (node != null) {
                    enMessage.setPostBackUrl(node.getNodeValue());
                }
                if (enMessage.getPostBackUrl() == null) {
                    enMessage.setPostBackUrl(parseManual(data, "PostBackUrl"));
                }
            }

            NodeList nodeList = element.getElementsByTagName("SMSMessage");

            if (nodeList != null && nodeList.getLength() > 0) {
                SMSMessage smsMessage = null;

                node = nodeList.item(0);
                smsMessage = new SMSMessage();
                NamedNodeMap map = node.getAttributes();
                if (map.getNamedItem("SMSType") != null) {
                    smsMessage.setSMSType(map.getNamedItem("SMSType").getNodeValue());
                }


                NodeList rcpPhoneList = element.getElementsByTagName("RcpPhone");
                if (rcpPhoneList.getLength() > 0) {
                    RcpPhone rcpPhone = new RcpPhone();
                    node = rcpPhoneList.item(0);

                    rcpPhone.setNumber(getNodeValue2(node, "Number"));
                    rcpPhone.setProvider(getNodeValue2(node, "Provider"));
                    rcpPhone.setMake(getNodeValue2(node, "Make"));
                    rcpPhone.setModel(getNodeValue2(node, "Model"));
                    smsMessage.setRcpPhone(rcpPhone);
                }

                NodeList bodyList = element.getElementsByTagName("Body");
                if (bodyList != null && bodyList.getLength() > 0) {
                    node = bodyList.item(0);
                    smsMessage.setBody(node.getFirstChild().getNodeValue());
                }

                NodeList from = element.getElementsByTagName("From");
                if (from != null && from.getLength() > 0) {
                    node = from.item(0);
                    smsMessage.setFrom(node.getFirstChild().getNodeValue());
                }

                enMessage.setSMSMessage(smsMessage);
            }
            node = null;

            if (enMessage.getMessageType() != null && enMessage.getMessageType().equals("RESPONSE")) {
                nodeList = element.getElementsByTagName("SuccessCode");
                if (nodeList != null && nodeList.getLength() > 0) {
                    node = nodeList.item(0);
                    enMessage.setSuccessCode(node.getFirstChild().getNodeValue());
                }
                nodeList = element.getElementsByTagName("ResponseCode");
                if (nodeList != null && nodeList.getLength() > 0) {
                    node = nodeList.item(0);
                    enMessage.setResponseCode(node.getFirstChild().getNodeValue());
                }
                nodeList = element.getElementsByTagName("ResponseMessage");
                if (nodeList != null && nodeList.getLength() > 0) {
                    node = nodeList.item(0);
                    enMessage.setResponseMessage(node.getFirstChild().getNodeValue());
                }
            }
        } catch (Throwable e) {
            logger.log(Level.WARNING, "Error for[" + data + "] : " + e, e);
            throw new IOException(e.getMessage());
        }
        return enMessage;
    }

    public static String parseManual(String data, String tag) {
        String s = null;
        try {
            int i = data.indexOf(tag);
            if (i > -1) {
                int j = data.indexOf("</" + tag, i);
                if (j > -1) {
                    int ln = tag.length() + 1;
                    s = data.substring(i + ln, j);
                }
            }
        } catch (Exception e) {
            logger.log(Level.FINER, "manul failed :tag " + tag + " " + e, e);

        }
        return s;
    }

    private static String getNodeValue2(Node element, String nodeName) throws Exception {
        try {
            NodeList nodeList = element.getChildNodes();
            Node node = null;
            for (int i = 0; i < nodeList.getLength(); i++) {
                node = nodeList.item(0);
                if (node.getNodeName().equals(nodeName)) {
                    return node.getFirstChild().getNodeValue();
                }
            }
            return null;
        } catch (Exception e) {
            throw new Exception(element.getNodeName() + "->" + nodeName + " : " + e.getMessage(), e);
        }
    }

  
}

class MyErrorHandler implements ErrorHandler {

    public void warning(SAXParseException e) throws SAXException {
    }

    public void error(SAXParseException e) throws SAXException {
        throw e;
    }

    public void fatalError(SAXParseException e) throws SAXException {
        throw e;
    }

}
