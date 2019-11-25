package org.mylearning.notification.email;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.mylearning.notification.event.NotificationEvent;
import org.mylearning.notification.event.NotificationEventStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailSender {
	private final static Logger logger = LoggerFactory.getLogger(EmailSender.class);
	public static boolean isSimulationEnabled = false;
	
			
	public static boolean sendEmail(NotificationEvent event) {
		
		if(isSimulationEnabled){
			logger.debug("*****SIMULATION TURNED ON*****:\nDumping data {}", event);
			event.setEventStatus(NotificationEventStatus.COMPLETED);
			event.appendComments("*****EmailSender: SIMULATED ENVIRONMENT TURNED ON*****");
			return true;
		}
		
		String message				= event.getNotificationMsg();
		if(message == null) message = "";
		String subject				= event.getNotificationSubject();
		if(subject == null) subject = "";
		String email				= event.getNotificationTo();
		if(email == null) email = "";
		String fromEmailAddress				= event.getNotificationFrom();
		if(fromEmailAddress == null) fromEmailAddress = "";
		
		String emailType = "text/plain";
		String hostIPAddress = event.getEmailHost();
		String emailPort = event.getEmailPort();
		
		String toEmailAddress = email;
		try {
			Properties prop=new Properties();
			prop.setProperty("mail.smtp.host", hostIPAddress);
			prop.setProperty("mail.smtp.port", emailPort);
			prop.setProperty("mail.user", "root");

			Session mail_Session=Session.getDefaultInstance(prop, null);
			Message myMessage = new MimeMessage(mail_Session);
			
			Transport bus = mail_Session.getTransport("smtp");
	        //bus.connect();

			if(toEmailAddress !=  null && toEmailAddress.length() > 0) {
				InternetAddress toAddress = new InternetAddress(toEmailAddress);
				myMessage.addRecipient(Message.RecipientType.TO, toAddress);
			}
			
			//Added to support list of email addresses in to
			InternetAddress fromAddress = new InternetAddress(fromEmailAddress);
			myMessage.setFrom(fromAddress);

			
			Multipart multipart=null;
			multipart = new MimeMultipart("related");
			BodyPart messageBodyPart = new MimeBodyPart();
		
			messageBodyPart.setContent(message, emailType);
			multipart.addBodyPart(messageBodyPart);
			
			//Add all the attachments to the body
			myMessage.setContent(multipart);
			myMessage.setSentDate(new java.util.Date());

			if(subject != null)
				myMessage.setSubject(subject);
			myMessage.saveChanges();
			
			logger.debug("Sending mail.........");
			Transport.send(myMessage);
			logger.debug("end of Sending myMessage.....");
			event.setEventStatus(NotificationEventStatus.COMPLETED);
			return true;
		} catch(Exception msg_exc) {
			logger.debug("Error sending eMail : ",msg_exc);
			event.setEventStatus(NotificationEventStatus.FAILED);
			event.appendComments("EmailSender: Error while sending Email for the event: " + msg_exc.getMessage());
		}
		return true;
	}
}
