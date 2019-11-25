package org.mylearning.notification;

import org.mylearning.notification.event.NotificationEvent;

public interface NotificationManager {

	public void publishEvent(NotificationEvent event);
	
	public void publishWibmoAnalyticsEvent(NotificationEvent event);

}
