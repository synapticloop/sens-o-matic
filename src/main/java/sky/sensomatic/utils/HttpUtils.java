package sky.sensomatic.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.json.JSONStringer;

import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class HttpUtils {
	private static final String MIME_TYPE_APPLICATION_JSON = "application/json";

	public static Response ok(String message) {
		return(new Response(Status.OK, MIME_TYPE_APPLICATION_JSON, getJsonMessage(200l, message)));
	}

	public static Response ok(String message, JSONObject jsonObject) {
		return(new Response(Status.OK, MIME_TYPE_APPLICATION_JSON, addStatus(200l, message, jsonObject)));
	}

	public static Response BadRequest(String message) {
		return(new Response(Status.BAD_REQUEST, MIME_TYPE_APPLICATION_JSON, getJsonMessage(400l, message)));
	}

	public static Response BadRequest(String message, JSONObject jsonObject) {
		return(new Response(Status.BAD_REQUEST, MIME_TYPE_APPLICATION_JSON, addStatus(400l, message, jsonObject)));
	}

	public static Response internalServerError(String message) {
		return(new Response(Status.BAD_REQUEST, MIME_TYPE_APPLICATION_JSON, getJsonMessage(500l, message)));
	}

	public static Response internalServerError(String message, JSONObject jsonObject) {
		return(new Response(Status.BAD_REQUEST, MIME_TYPE_APPLICATION_JSON, addStatus(500l, message, jsonObject)));
	}

	public static Response notFound(String message) {
		return(new Response(Status.NOT_FOUND, MIME_TYPE_APPLICATION_JSON, getJsonMessage(404l, message)));
	}

	public static Response notFound(String message, JSONObject jsonObject) {
		return(new Response(Status.NOT_FOUND, MIME_TYPE_APPLICATION_JSON, addStatus(404l, message, jsonObject)));
	}


	private static String getJsonMessage(long status, String message) {
		return(new JSONStringer()
		.object()
			.key("status")
			.value(status)
			.key("message")
			.value(message)
			.endObject()
			.toString());
	}

	private static String addStatus(long status, String message, JSONObject jsonObject) {
		jsonObject.put("status", status);
		jsonObject.put("message", message);
		return(jsonObject.toString());
	}

	public static boolean isNotEmptyRestParameter(String parameter) {
		return(null != parameter && parameter.trim().length() != 0);
	}

	public static String doGet(String endpoint, String payload) throws IOException {
		URL obj = new URL(endpoint);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		String urlParameters = "payload=" + payload;

		if(null != payload) {
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
		} else {
			con.setDoOutput(false);
		}

		int responseCode = con.getResponseCode();
		System.out.println("(" + responseCode + ") 'GET'ting " + endpoint + "?" + urlParameters);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		return(response.toString());
	}

	public static String doPost(String endpoint, String payload) throws IOException {
		URL obj = new URL(endpoint);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");

		String urlParameters = "payload=" + payload;

		// Send post request
		if(null != payload) {
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
		} else {
			con.setDoOutput(false);
		}

		int responseCode = con.getResponseCode();
		System.out.println("(" + responseCode + ") 'POST'ing " + endpoint + "?" + urlParameters);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		return(response.toString());

	}
}
