package sky.sensomatic.utils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class PayloadUtils {
	public static String createRegisterPayload(String name, String team, String application, String component, String description, long interval, ArrayList<String> categories) {
		JSONObject message = new JSONObject();
		message.put("name", name);
		message.put("team", team);
		message.put("application", application);
		message.put("component", component);
		if(null != description) {
			message.put("description", description);
		}
		message.put("interval", interval);
		JSONArray categoryArray = new JSONArray();
		for (String category : categories) {
			categoryArray.put(category);
		}
		message.put("categories", categoryArray);
		return(message.toString());
	}

	public static String createPayload(String hash, String time, String payload) {
		JSONObject message = new JSONObject();
		message.put("hash", hash);
		message.put("time", time);
		if(null != payload) {
			message.put("payload", new JSONObject(payload));
		}
		return(message.toString());
	}

}
