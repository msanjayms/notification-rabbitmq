package org.mylearning.notification.event;

public enum NotificationEventStatus {

	RAISED("Raised", 1), QUEUED("Queued", 2), COMPLETED("Completed", 3), FAILED("Failed",
			4), INTERMEDIATE("Intermediate", 5);

	private String name;
	private int value;

	private NotificationEventStatus(String displayName, int value) {
		this.name = displayName;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}
}