package org.mylearning.notification.handlers;

import java.util.concurrent.Callable;

import org.mylearning.notification.RabbitMQNotificationManager;
import org.mylearning.notification.event.NotificationEvent;
import org.mylearning.notification.event.NotificationEventStatus;
import org.mylearning.notification.event.NotificationEventType;
import org.mylearning.notification.sms.SMSSender;
import org.myleraning.notification.email.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationEventTask implements Callable<NotificationEvent> {

	protected final static Logger logger = LoggerFactory.getLogger(NotificationEventTask.class);

	private NotificationEvent eventData = null;

	private int retryCount;

	public NotificationEventTask(NotificationEvent eventData, int retryCount) {
		this.eventData = eventData;
		this.retryCount = retryCount;
	}

	@Override
	public NotificationEvent call() throws Exception {

		NotificationEventType notificationType = this.eventData.getNotificationType();

		logger.debug(String.format("NotificationEventTask: Processing %s", eventData.getNotificationId()));

		switch (notificationType) {

		case SMS:
			int smsRetryCount = 0;
			while (smsRetryCount < retryCount) {
				smsRetryCount++;
				SMSSender.sendSms(eventData);
				if (eventData.getEventStatus() == NotificationEventStatus.COMPLETED) {
					eventData.appendComments("Successfully " + NotificationEventStatus.COMPLETED.getName());
					break;
				}
			}
			eventData.setRetryCount(smsRetryCount);
			if (eventData.getEventStatus() == NotificationEventStatus.FAILED) {
				eventData.appendComments("Failed after configured re-tries: " + this.retryCount);
				logger.warn("Notification Event: {} failed after configured re-tries: {}",
						eventData.getNotificationId(), this.retryCount);
			}
			break;
		case EMAIL:
			int emailRetryCount = 0;
			while (emailRetryCount < retryCount) {
				emailRetryCount++;
				EmailSender.sendEmail(eventData);
				if (eventData.getEventStatus() == NotificationEventStatus.COMPLETED) {
					eventData.appendComments("Successfully " + NotificationEventStatus.COMPLETED.getName());
					break;
				}
			}
			eventData.setRetryCount(emailRetryCount);
			if (eventData.getEventStatus() == NotificationEventStatus.FAILED) {
				eventData.appendComments("Failed after configured re-tries: " + this.retryCount);
				logger.warn("Notification Event: {} failed after configured re-tries: {}",
						eventData.getNotificationId(), this.retryCount);
			}
			break;
		default:
			eventData.appendComments("BAD Notification Type registered for the Notification Event");
			logger.warn("BAD Notification Type registered for the Notification Event {}",
					eventData.getNotificationId());
		}

		// update DB about the status;
		RabbitMQNotificationManager.updateNotificationEventData(eventData);

		return this.eventData;
	}

}
