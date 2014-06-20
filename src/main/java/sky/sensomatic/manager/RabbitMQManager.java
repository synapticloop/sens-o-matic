package sky.sensomatic.manager;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


public class RabbitMQManager {
	private static String host = null;
	private static String virtualHost = null;
	private static String username = null;
	private static String password = null;
	private static String queueName = null;

	public RabbitMQManager(String host, String virtualHost, String queueName, String username, String password) {
		RabbitMQManager.host = host;
		RabbitMQManager.virtualHost = virtualHost;
		RabbitMQManager.username = username;
		RabbitMQManager.password = password;
		RabbitMQManager.queueName = queueName;
	}

	public static ConnectionFactory getConnectionFactory() {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		factory.setVirtualHost(virtualHost);
		factory.setUsername(username);
		factory.setPassword(password);
		return(factory);
	}

	public static boolean pushMessage(String payload) {
		// pump the payload onto the rabbit MQ
		ConnectionFactory factory = getConnectionFactory();

		Connection connection = null;
		Channel channel = null;
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.queueDeclare(queueName, false, false, false, null);
			String message = payload;
			channel.basicPublish("", queueName, null, message.getBytes());
		} catch (IOException ioex) {
			return(false);
		} finally {
			if(null != connection) {
				try { channel.close(); } catch (IOException ioex) { channel = null; }
				try { connection.close(); } catch (IOException ioex) { connection = null; }
			}
		}
		return(true);
	}

	public static String getQueueName() {
		return(queueName);
	}
}
