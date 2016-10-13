package cs557.httpServer.constants;

/**
 * This enum contains different types of HTTP requests that are supported by
 * this application.
 * 
 * @author anandkulkarni
 *
 */
public enum RequestType {
	GET("GET");
	String value;

	private RequestType(String valueIn) {
		value = valueIn;
	}

	public static RequestType fromValue(String value) {
		switch (value) {
		case "GET":
			return RequestType.GET;
		default:
			throw new IllegalArgumentException("Request type: " + value + " is not supported");
		}
	}

	public String getValue() {
		return value;
	}
}
