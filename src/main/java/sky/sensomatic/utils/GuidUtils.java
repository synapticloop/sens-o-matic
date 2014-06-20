package sky.sensomatic.utils;

import org.json.JSONObject;

public class GuidUtils {
	public static String getGuidForJson(JSONObject jsonObject) {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(jsonObject.getString("name"));
		stringBuilder.append(":");
		stringBuilder.append(jsonObject.getString("team"));
		stringBuilder.append(":");
		stringBuilder.append(jsonObject.getString("application"));

		return(stringBuilder.toString());
	}
}
