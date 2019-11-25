package org.mylearning.notification.event;

import java.io.Serializable;
import java.sql.Timestamp;

public class NotificationEvent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3110377697245607617L;

	private long id;
	private int bankId;
	private String notificationId;
	private long iccId;
	private NotificationEventType notificationType;
	private String notificationFrom;
	private String notificationTo;
	private String notificationMsg;
	private String emailHost;
	private String comments = "";
	private int retryCount = 3;
	private Timestamp createdTime;
	private Timestamp updatedTime;
	private long epfTxnId;

	private NotificationEventStatus eventStatus;
	// @Transient
	private String notificationSubject;
	// @Transient
	private String smsHost;
	// @Transient
	private int smsPort;

	private long eventId;

	private String emailPort;

	private StringBuilder appendStatus = new StringBuilder();

	private StringBuilder appendComments = new StringBuilder();

	// @Transient
	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public long getIccId() {
		return iccId;
	}

	public void setIccId(long iccId) {
		this.iccId = iccId;
	}

	public NotificationEventType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationEventType notificationType) {
		this.notificationType = notificationType;
	}

	public String getNotificationFrom() {
		return notificationFrom;
	}

	public void setNotificationFrom(String notificationFrom) {
		this.notificationFrom = notificationFrom;
	}

	public String getNotificationTo() {
		return notificationTo;
	}

	public void setNotificationTo(String notificationTo) {
		this.notificationTo = notificationTo;
	}

	public String getNotificationSubject() {
		return notificationSubject;
	}

	public void setNotificationSubject(String notificationSubject) {
		this.notificationSubject = notificationSubject;
	}

	public String getNotificationMsg() {
		return notificationMsg;
	}

	public void setNotificationMsg(String notificationMsg) {
		this.notificationMsg = notificationMsg;
	}

	public String getSmsHost() {
		return smsHost;
	}

	public void setSmsHost(String smsHost) {
		this.smsHost = smsHost;
	}

	public int getSmsPort() {
		return smsPort;
	}

	public void setSmsPort(int smsPort) {
		this.smsPort = smsPort;
	}

	public String getEmailHost() {
		return emailHost;
	}

	public void setEmailHost(String emailHost) {
		this.emailHost = emailHost;
	}

	public String getComments() {
		buildMoreDetailedComments();
		return comments;
	}

	public NotificationEventStatus getEventStatus() {
		return eventStatus;
	}

	public void setEventStatus(NotificationEventStatus eventStatus) {
		this.eventStatus = eventStatus;
		this.appendStatus.append(eventStatus).append(" + ");
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	public Timestamp getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Timestamp updatedTime) {
		this.updatedTime = updatedTime;
	}

	public long getEpfTxnId() {
		return epfTxnId;
	}

	public void setEpfTxnId(long epfTxnId) {
		this.epfTxnId = epfTxnId;
	}

	public long getEventId() {
		return eventId;
	}

	public void appendComments(String comments) {

		this.appendComments.append("\n").append(comments);
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		String desc = String.format(
				"Notification Event: id: %d notificationId: %s iccId %d eventId: %d bankId: %d notificationType: %s notificationFrom: %s notificationTo: %s"
						+ "notificationMsg: %s" + "emailHost: %s comment: %s" + "retryCount: %d createdTime: %s"
						+ "updatedTime: %s epfTxnId: %d",
				id, notificationId, iccId, eventId, bankId, notificationType, notificationFrom, notificationTo,
				notificationMsg, emailHost, comments, retryCount, createdTime, updatedTime, epfTxnId);
		return desc;
	}

	public void setEmailPort(String emailPort) {
		this.emailPort = emailPort;
	}

	public String getEmailPort() {
		return emailPort;
	}

	public int getBankId() {
		return bankId;
	}

	public void setBankId(int bankId) {
		this.bankId = bankId;
	}

	private void buildMoreDetailedComments() {
		comments = appendStatus.append(appendComments.toString()).toString();
	}
}
