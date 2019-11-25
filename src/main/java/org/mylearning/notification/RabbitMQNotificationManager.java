package org.mylearning.notification;

import org.apache.commons.lang.SerializationUtils;
import org.mylearning.notification.email.EmailSender;
import org.mylearning.notification.event.NotificationEvent;
import org.mylearning.notification.event.NotificationEventStatus;
import org.mylearning.notification.handlers.NotificationEventHandler;
import org.mylearning.notification.sms.SMSSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class RabbitMQNotificationManager {

	protected final static Logger logger = LoggerFactory.getLogger(RabbitMQNotificationManager.class);

	private static String QUEUE_NAME = "notification_queue";
	private static NotificationEventHandler notificationEventHandler;
	private static ConnectionFactory factory;
	private static Connection connection;
	private static Channel channel;

	private static NotificationQueueConfig notificationQueueConfig;

	public static void initializeNotificationMgr(NotificationQueueConfig queueConfig) throws Exception {
		logger.info("Starting Notification Event Manager");
		notificationQueueConfig = queueConfig;
		// Initialize Queue implementation (RabbitMQ)
		initializeQueueMgr(notificationQueueConfig);
		// Start the consumer
		startNotificationProcessor();
		logger.info("Queue initialization is successfull and ready for event processing");
		
		/*
		 * This is only for the load testing purpose.
		 */
		
		SMSSender.isSimulationEnabled = notificationQueueConfig.isSimulationEnable();
		EmailSender.isSimulationEnabled = notificationQueueConfig.isSimulationEnable();
	}

	private static void startNotificationProcessor() throws NotificationQueueException {
		try {
			if (!channel.isOpen())
				channel = connection.createChannel();
			notificationEventHandler = new NotificationEventHandler(channel, notificationQueueConfig.getRetryCount());
			channel.basicConsume(QUEUE_NAME, false, notificationEventHandler, notificationEventHandler);
		} catch (Exception e) {
			logger.error("Failed to start the Notification Processor (Queue Consumer) for the Queue {}", QUEUE_NAME);
			throw new NotificationQueueException(
					String.format("Failed to start the Notification Processor for the Queue %s", QUEUE_NAME), e);
		}
	}

	public static void publishEvent(NotificationEvent eventData) throws NotificationQueueException {

		try {
			logger.info(String.format("Trying to push Notification Event with the ID: %s, " + "  onto the queue: %s",
					eventData.getNotificationId(), notificationQueueConfig.getQueueName()));
			if (!channel.isOpen())
				channel = connection.createChannel();
			channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN,
					SerializationUtils.serialize(eventData));
		} catch (Exception e) {
			String message = String.format("Error while publishing Notification Event: %s, on the queue: %s",
					eventData.getNotificationId(), QUEUE_NAME);
			logger.error("Error while publishing Notification Event {}, onto the queue: {}", eventData, QUEUE_NAME);
			eventData.setEventStatus(NotificationEventStatus.FAILED);
			eventData.appendComments("Error while pushing Event on to the queue: " + QUEUE_NAME);
			eventData.setRetryCount(1);
			RabbitMQNotificationManager.updateNotificationEventData(eventData);
			throw new NotificationQueueException(message, e);
		}
	}

	public static void tearDownNotificationManager() throws Exception {
		logger.info("Stopping Notification Manager");
		if (notificationEventHandler != null)
			notificationEventHandler.tearDown();
		logger.warn("Stoping Wibmo Analytics Rest Client Helper");
		logger.warn("Closing Queue related connections");
		channel.close();
		connection.close();
	}

	public static void insertNotificationEventData(NotificationEvent eventData) throws NotificationDBException {
				
		// Source code to persist the Data onto the DB.
	}

	public static void updateNotificationEventData(NotificationEvent eventData) {
		
		//Source code to update the Event Data on the DB.
	}

	private static void initializeQueueMgr(NotificationQueueConfig queueConfig) throws NotificationQueueException {
		QUEUE_NAME = notificationQueueConfig.getQueueName();
		factory = new ConnectionFactory();
		factory.setHost(queueConfig.getHost());
		factory.setPort(queueConfig.getPort());
		factory.setUsername(queueConfig.getUsername());
		factory.setPassword(queueConfig.getPassword());
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.queueDeclare(QUEUE_NAME, queueConfig.isQueueDurable(), false, false, null);
		} catch (Exception e) {
			logger.error("******FATAL ERROR: Failed to initialize connections to RabbitMQ Server {}: ******\n", notificationQueueConfig);
			throw new NotificationQueueException("Failed to initialize connections to RabbitMQ Server", e);
		}
	}
}
