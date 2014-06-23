package sky.sensomatic.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import fi.iki.elonen.NanoHTTPD.Response;

public class JsonUtils {
	private static final String OPTIONS_JSON_HASH = "hash";

	private static final String DATE_FORMAT_SECOND = "yyyy-mm-dd HH:mm:ss";
	private static final String DATE_FORMAT_MINUTE = "yyyy-mm-dd HH:mm";
	private static final String DATE_FORMAT_HOUR = "yyyy-mm-dd HH";
	private static final String DATE_FORMAT_DAY = "yyyy-mm-dd";
	private static final String DATE_FORMAT_MONTH = "yyyy-mm";
	private static final String DATE_FORMAT_YEAR = "yyyy";

	private static final String[] REQUIRED_KEYS = { "name", "team", "application", "interval", "categories" };
	private static final String[] REQUIRED_TIME_KEYS = { "time" };
	private static final String[] REQUIRED_OBJECT_KEYS = { "payload" };

	/**
	 * Return the MD5 hash for the JSON if possible, else null if it couldn't be
	 * done
	 *
	 * @param jsonObject the json object
	 *
	 * @return the MD5 hash - or null
	 */
	public static String getHashForJson(JSONObject jsonObject) {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(jsonObject.getString("name"));
		stringBuilder.append(":");
		stringBuilder.append(jsonObject.getString("team"));
		stringBuilder.append(":");
		stringBuilder.append(jsonObject.getString("application"));

		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] digest = messageDigest.digest(stringBuilder.toString().getBytes());
			StringBuilder sb = new StringBuilder(2*digest.length);
			for(byte b : digest) {
				sb.append(String.format("%02x", b&0xff));
			}
			return(sb.toString());
		} catch (NoSuchAlgorithmException nsaex) {
			// shouldn't happen
			nsaex.printStackTrace();
		}

		return(null);
	}

	public static String getHashForJson(String payload) {
		try {
			JSONObject jsonObject = new JSONObject(payload);
			return(getHashForJson(jsonObject));
		} catch(JSONException jsonex) {
			return(null);
		}
	}

	/**
	 * Validate that the JSON is correct.  The JSON comes in two formats, one is
	 * where the json feed has already been registered, in which case it will be
	 * in the form of
	 *   {
	 *     "hash": "<MD5_HASH>",
	 *     "time": "<some valid time format>",
	 *     "payload": {"json": [ "object", "payload" ]}
	 *   }
	 *
	 * or else if unregistered, will be in the long form and full checking will
	 * be done. the format is something along the lines of
	 *   {
	 *     "name": "Unique Name for the feed",
	 *     "team": "Team_Name",
	 *     "application": "application_name",
	 *     "time": "yyyy-MM-dd HH:mm:ss",
	 *     "interval": "#seconds per interval",
	 *     "categories": [
	 *       "category_one", "category_two", "category_three"
	 *     ],
	 *     "payload": {<any_valid_json_format>}
	 *   }
	 *
	 * @param json  The json to be validated
	 *
	 * @return
	 */

	public static Response validateJson(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			// there are one of two possible json payloads, one with the hash, and
			// the other with the full request
			String hashCheck = jsonObject.optString(OPTIONS_JSON_HASH);

			// validate keys that are needed for both versions
			for (int i = 0; i < REQUIRED_TIME_KEYS.length; i++) {
				String timeKey = jsonObject.optString(REQUIRED_TIME_KEYS[i]);
				if(null == timeKey || !isValidDateTimeFormat(timeKey)) {
					return(HttpUtils.BadRequest("MISSING OR INVALID DATE/TIME FIELD '" + timeKey + "'."));
				}
			}

			for (int i = 0; i < REQUIRED_OBJECT_KEYS.length; i++) {
				String requiredKey = REQUIRED_OBJECT_KEYS[i];
				// we need to ensure that the object is correctly formatted
				if(null == jsonObject.optJSONObject(requiredKey)) {
					return(HttpUtils.BadRequest("MISSING FIELD '" + requiredKey + "'."));
				}
			}

			if(null == hashCheck) {
				// must be a full registration
				return(validateFullJson(jsonObject));
			}

		} catch(JSONException jsonex) {
			return(HttpUtils.BadRequest("COULD NOT PARSE JSON '" + json + "'."));
		}
		return(HttpUtils.OKResponse("OK"));
	}

	private static Response validateFullJson(JSONObject jsonObject) {
		for (int i = 0; i < REQUIRED_KEYS.length; i++) {
			if(null == jsonObject.opt(REQUIRED_KEYS[i])) {
				return(HttpUtils.BadRequest("MISSING FIELD '" + REQUIRED_KEYS[i] + "'."));
			}
		}
		return(HttpUtils.OKResponse("OK"));
	}

	/**
	 * Determine whether this is a valid date/time format, this can be in any of:
	 * <ul>
	 *   <li>yyyy-mm-dd HH:mm:ss</li>
	 *   <li>yyyy-mm-dd HH:mm</li>
	 *   <li>yyyy-mm-dd HH</li>
	 *   <li>yyyy-mm-dd</li>
	 *   <li>yyyy-mm</li>
	 *   <li>yyyy</li>
	 * </ul>
	 *
	 * Any other formats fail.
	 *
	 * @param date the date as a string
	 *
	 * @return whether it is a valid date/time format
	 */
	private static boolean isValidDateTimeFormat(String date) {
		// 2014-01-01 13:45:23
		try {
			switch(date.length()) {
			case 4:
				// yearly stats
				new SimpleDateFormat(DATE_FORMAT_YEAR).parse(date);
				return(true);
			case 7:
				// monthly stats
				new SimpleDateFormat(DATE_FORMAT_MONTH).parse(date);
				return(true);
			case 10:
				// daily stats
				new SimpleDateFormat(DATE_FORMAT_DAY).parse(date);
				return(true);
			case 13:
				// hourly stats
				new SimpleDateFormat(DATE_FORMAT_HOUR).parse(date);
				return(true);
			case 16:
				// minutely stats
				new SimpleDateFormat(DATE_FORMAT_MINUTE).parse(date);
				return(true);
			case 19:
				// seconds stats
				new SimpleDateFormat(DATE_FORMAT_SECOND).parse(date);
				return(true);
			default:
				return(false);
			}
		} catch (ParseException pex) {
			return(false);
		} catch(NumberFormatException nfex) {
			return(false);
		}
	}
}
