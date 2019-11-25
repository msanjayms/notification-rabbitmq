package org.mylearning.notification.event;
public enum NotificationEventType {
	SMS("SMS", 1), EMAIL("Email", 2);

	private int value;
	private String name;

	private NotificationEventType(String name, int value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}
}