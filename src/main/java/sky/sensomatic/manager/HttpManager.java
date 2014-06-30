package sky.sensomatic.manager;

public class HttpManager {
	private static boolean postHttpMessage(String host, String endpoint, String message) {
		return(false);
	}

	public static boolean postManagerHttpMessage(String endpoint, String message) {
		return(postHttpMessage(PropertyManager.getSensomaticManagerEndpoint(), endpoint, message));
	}
}
