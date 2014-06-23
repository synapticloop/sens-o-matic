package sky.sensomatic.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyManager {
	private static final String PROPERTIES_LOCATION = "/sens-o-matic.properties";
	private static Properties properties = new Properties();

	private static String rabbitMQManagerHost = null;
	private static String rabbitMQManagerVHost = null;
	private static String rabbitMQManagerUsername = null;
	private static String rabbitMQManagerPassword = null;
	private static String rabbitMQManagerQueueName = null;

	private static String rabbitMQHost = null;
	private static String rabbitMQVHost = null;
	private static String rabbitMQUsername = null;
	private static String rabbitMQPassword = null;
	private static String rabbitMQQueueName = null;

	private static String sensomaticRabbitApiEndpoint = null;
	private static String sensomaticManagerEndpoint = null;

	public static void initialise() {
		try {
			InputStream resourceAsStream = Properties.class.getResourceAsStream(PROPERTIES_LOCATION);
			if(null != resourceAsStream) {
				properties.load(resourceAsStream);

				rabbitMQManagerHost = properties.getProperty("rabbitMQManagerHost", null);
				rabbitMQManagerVHost = properties.getProperty("rabbitMQManagerVHost", null);
				rabbitMQManagerUsername = properties.getProperty("rabbitMQManagerUsername", null);
				rabbitMQManagerPassword = properties.getProperty("rabbitMQManagerPassword", null);
				rabbitMQManagerQueueName = properties.getProperty("rabbitMQManagerQueueName", null);

				rabbitMQHost = properties.getProperty("rabbitMQHost", null);
				rabbitMQVHost = properties.getProperty("rabbitMQVHost", null);
				rabbitMQUsername = properties.getProperty("rabbitMQUsername", null);
				rabbitMQPassword = properties.getProperty("rabbitMQPassword", null);
				rabbitMQQueueName = properties.getProperty("rabbitMQQueueName", null);

				sensomaticRabbitApiEndpoint = properties.getProperty("sensomaticRabbitApiEndpoint", null);
				sensomaticManagerEndpoint = properties.getProperty("sensomaticManagerEndpoint", null);
			}
		} catch (IOException ignored) { /* couldn't find it - continue */
			ignored.printStackTrace();
		}
	}

	public static String getRabbitMQHost() { return rabbitMQHost; }
	public static String getRabbitMQVHost() { return rabbitMQVHost; }
	public static String getRabbitMQQueueName() { return rabbitMQQueueName; }
	public static String getRabbitMQUsername() { return rabbitMQUsername; }
	public static String getRabbitMQPassword() { return rabbitMQPassword; }

	public static String getRabbitMQManagerHost() { return rabbitMQManagerHost; }
	public static String getRabbitMQManagerVHost() { return rabbitMQManagerVHost; }
	public static String getRabbitMQManagerQueueName() { return rabbitMQManagerQueueName; }
	public static String getRabbitMQManagerUsername() { return rabbitMQManagerUsername; }
	public static String getRabbitMQManagerPassword() { return rabbitMQManagerPassword; }

	public static String getSensomaticRabbitApiEndpoint() { return sensomaticRabbitApiEndpoint; }

	public static String getSensomaticManagerEndpoint() { return sensomaticManagerEndpoint; }

}
