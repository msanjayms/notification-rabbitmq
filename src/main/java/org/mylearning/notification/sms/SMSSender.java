package org.mylearning.notification.sms;

import org.mylearning.notification.event.NotificationEvent;
import org.mylearning.notification.event.NotificationEventStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SMSSender {
	private static final String SMS_SUCCESS_CODE = "00";

	private final static Logger logger = LoggerFactory.getLogger(SMSSender.class);
	
	public static boolean isSimulationEnabled = false;

	public static boolean sendSms(NotificationEvent event) {
		logger.debug("SMSSender: processing event {} ", event.getNotificationId());

		if (isSimulationEnabled) {
			logger.debug("*****SIMULATION TURNED ON*****:\nDumping data {}", event);
			event.setEventStatus(NotificationEventStatus.COMPLETED);
			event.appendComments("*****SMSSender: SIMULATED ENVIRONMENT TURNED ON*****");
			return true;
		}
		
		// Construct request for SMS
		if (event.getNotificationTo() != null) {
			final String smsHost = event.getSmsHost();
			final int smsPort = event.getSmsPort();
			String smsText = event.getNotificationMsg();

			try {
				EnMessage enM = new EnMessage();
				RcpPhone rcpPhone = new RcpPhone();
				rcpPhone.setNumber(event.getNotificationTo());
				SMSMessage smsMessage = new SMSMessage();
				smsMessage.setSMSType("SMS");
				smsMessage.setRcpPhone(rcpPhone);
				smsMessage.setFrom(event.getNotificationFrom());
				smsMessage.setBody(smsText);
				enM.setSMSMessage(smsMessage);
				enM.setRefId(event.getEpfTxnId());
				enM.setClientId(event.getNotificationId());
				EnMessage.setPostBackMsgStat(true);
				EnMessage enMessageRes = MessageServerAPI.sendMessage(enM, smsHost, smsPort);
				String successCode = enMessageRes.getSuccessCode();
				String responseMessage = enMessageRes.getResponseMessage();
				logger.debug("SMSSender: SMS for the event {} was sent successfully, with the SuccessCode : {} "
						+ "and Response Message: {}", event.getNotificationId(), successCode, responseMessage);
				if (successCode.equals(SMS_SUCCESS_CODE))
					event.setEventStatus(NotificationEventStatus.COMPLETED);
				else
					event.setEventStatus(NotificationEventStatus.FAILED);
				event.appendComments("SMS Success Code: " + successCode);
				event.appendComments("SMS Response Message: " + responseMessage);
			} catch (Exception io) {
				logger.error("SMSSender: Error while sending SMS for the event: {} ", event.getNotificationId(), io);
				event.setEventStatus(NotificationEventStatus.FAILED);
				event.appendComments("SMSSender: Error while sending SMS for the event: " + io.getMessage());
			}
		} else {
			logger.warn("SMSSender: couldn't find mobile number for the event {} !", event.getNotificationId());
			event.setEventStatus(NotificationEventStatus.INTERMEDIATE);
			event.appendComments("SMSSender: couldn't find mobile number for the event");
			return false;
		}
		return true;
	}

}
