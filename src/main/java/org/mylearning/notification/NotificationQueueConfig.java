package org.mylearning.notification;

public class NotificationQueueConfig {

	private String host;
	private String queueName;
	private long timeOut;
	private int retryCount;
	private boolean isQueueDurable;
	private int port;
	private String exchangeName;
	private String username;
	private String password;
	private boolean isSimulationEnable;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public long getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(long timeOut) {
		this.timeOut = timeOut;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public boolean isQueueDurable() {
		return isQueueDurable;
	}

	public void setQueueDurable(boolean isQueueDurable) {
		this.isQueueDurable = isQueueDurable;
	}

	@Override
	public String toString() {
		return String.format(
				"Queue configuration details:\nHost: %s, Port: %d, Queue Name: %s, Exchange Name: %s, isQueueDurable: %s, Timeout: %d, "
						+ "retryCount: %d, isSimulationEnabled: %s",
				host, port, queueName, exchangeName, isQueueDurable, timeOut, retryCount, isSimulationEnable);
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public String getExchangeName() {
		// TODO Auto-generated method stub
		return exchangeName;
	}

	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isSimulationEnable() {
		return isSimulationEnable;
	}

	public void setSimulationEnable(boolean isSimulationEnable) {
		this.isSimulationEnable = isSimulationEnable;
	}	
}
