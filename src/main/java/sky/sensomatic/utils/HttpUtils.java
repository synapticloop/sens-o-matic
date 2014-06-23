package sky.sensomatic.utils;

import org.json.JSONStringer;
import org.json.JSONWriter;

import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class HttpUtils {
	private static final String MIME_TYPE_APPLICATION_JSON = "application/json";

	public static Response OKResponse(String message) {
		return(new Response(Status.OK, MIME_TYPE_APPLICATION_JSON, getJsonMessage(200l, message)));
	}

	public static Response OKResponse(JSONWriter message) {
		return(new Response(Status.OK, MIME_TYPE_APPLICATION_JSON, addStatus(200l, message)));
	}

	public static Response BadRequest(String message) {
		return(new Response(Status.BAD_REQUEST, MIME_TYPE_APPLICATION_JSON, getJsonMessage(400l, message)));
	}

	public static Response BadRequest(JSONWriter message) {
		return(new Response(Status.BAD_REQUEST, MIME_TYPE_APPLICATION_JSON, addStatus(400l, message)));
	}

	public static Response InternalServerErrorRequest(String message) {
		return(new Response(Status.BAD_REQUEST, MIME_TYPE_APPLICATION_JSON, getJsonMessage(500l, message)));
	}

	public static Response InternalServerErrorRequest(JSONWriter message) {
		return(new Response(Status.BAD_REQUEST, MIME_TYPE_APPLICATION_JSON, addStatus(500l, message)));
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

	private static String addStatus(long status, JSONWriter message) {
		return(message.object()
				.key("status")
				.value(status)
				.endObject()
				.toString());
	}

}
