package org.mylearning.notification.handlers;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.SerializationUtils;
import org.mylearning.notification.RabbitMQNotificationManager;
import org.mylearning.notification.event.NotificationEvent;
import org.mylearning.notification.event.NotificationEventStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

/**
 * Notification Event Handler would receive events from the queue, and process
 * them using Notification Event Task. This Handler is the consumer acting on
 * the Notification
 * 
 * @author sanjay
 *
 */

public class NotificationEventHandler implements DeliverCallback, CancelCallback {

	protected final static Logger logger = LoggerFactory.getLogger(NotificationEventHandler.class);

	private ExecutorService executorService;

	private Channel channel;

	private int retryCount;
	
	public NotificationEventHandler(Channel channel, int retryCount) {
		this.channel = channel;
		this.retryCount = retryCount;
		initializeCoreThreadPool();
	}

	private void initializeCoreThreadPool() {
		logger.debug("*********Initializing Core Thread Pool To process Notification Event:*********");
		// executorService = Executors.newCachedThreadPool();
		int cores = Runtime.getRuntime().availableProcessors();
		logger.debug("Available Cores: {}", cores);
		ThreadPoolExecutor tpe = new ThreadPoolExecutor((5 * cores), (15 * cores), 60L, TimeUnit.SECONDS,
				new ArrayBlockingQueue<>(100), new ThreadFactory() {
					private AtomicInteger count = new AtomicInteger();
					@Override
					public Thread newThread(Runnable r) {
						Thread thread = new Thread(r);
						thread.setName("NotificationEventHandler - " + count.incrementAndGet());
						thread.setDaemon(true);
						return thread;
					}
				});
		tpe.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executorService = tpe;
	}

	@Override
	public void handle(String consumerTag, Delivery delivery) throws IOException {
		logger.debug("*****************Received a Notification Event*****************");
		byte[] body = delivery.getBody();
		NotificationEvent notificationEvent = (NotificationEvent) SerializationUtils.deserialize(body);
		logger.debug("Processing Notification Event: " + notificationEvent);

		NotificationEventTask task = new NotificationEventTask(notificationEvent, this.retryCount);
		notificationEvent.setEventStatus(NotificationEventStatus.QUEUED);

		// Update the Event Status as QUEUED onto the DB;
		RabbitMQNotificationManager.updateNotificationEventData(notificationEvent);
		executorService.submit(task);
		logger.debug("Successfully added the event {} onto the processing queue",
				notificationEvent.getNotificationId());
		// Acknowledge the message as read.
		logger.debug("Message {} acknowledge as read to the queue", notificationEvent.getNotificationId());
		channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
	}

	@Override
	public void handle(String consumerTag) throws IOException {
		logger.warn("*****************Cancel Call Back called*****************");
	}

	public void tearDown() {
		executorService.shutdown();
	}
}
