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

	private static String managerHost = null;
	private static String managerVirtualHost = null;
	private static String managerUsername = null;
	private static String managerPassword = null;
	private static String managerQueueName = null;

	public static void initialise() {
		RabbitMQManager.host = PropertyManager.getRabbitMQHost();
		RabbitMQManager.virtualHost = PropertyManager.getRabbitMQVHost();
		RabbitMQManager.username = PropertyManager.getRabbitMQUsername();
		RabbitMQManager.password = PropertyManager.getRabbitMQPassword();
		RabbitMQManager.queueName = PropertyManager.getRabbitMQQueueName();

		RabbitMQManager.managerHost = PropertyManager.getRabbitMQManagerHost();
		RabbitMQManager.managerVirtualHost = PropertyManager.getRabbitMQManagerVHost();
		RabbitMQManager.managerUsername = PropertyManager.getRabbitMQManagerUsername();
		RabbitMQManager.managerPassword = PropertyManager.getRabbitMQManagerPassword();
		RabbitMQManager.managerQueueName = PropertyManager.getRabbitMQManagerQueueName();
	}

	public static ConnectionFactory getConnectionFactory() {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		factory.setVirtualHost(virtualHost);
		factory.setUsername(username);
		factory.setPassword(password);
		return(factory);
	}

	public static ConnectionFactory getManagerConnectionFactory() {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(managerHost);
		factory.setVirtualHost(managerVirtualHost);
		factory.setUsername(managerUsername);
		factory.setPassword(managerPassword);
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

	public static boolean pushManagerMessage(String payload) {
		// pump the payload onto the manager rabbit MQ
		ConnectionFactory factory = getManagerConnectionFactory();

		Connection connection = null;
		Channel channel = null;
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.queueDeclare(managerQueueName, false, false, false, null);
			String message = payload;
			channel.basicPublish("", managerQueueName, null, message.getBytes());
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

	public static String getManagerQueueName() {
		return(managerQueueName);
	}
}
