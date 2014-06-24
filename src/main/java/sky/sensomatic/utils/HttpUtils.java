package sky.sensomatic.utils;

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
}
