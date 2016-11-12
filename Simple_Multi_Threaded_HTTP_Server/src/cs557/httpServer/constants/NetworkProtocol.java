package cs557.httpServer.constants;

/**
 * This enum lists different protocols that are supported by this application.
 * 
 * @author anandkulkarni
 *
 */
public enum NetworkProtocol {
	HTTP("HTTP/1.1");

	private String value;

	private NetworkProtocol(String valueIn) {
		value = valueIn;
	}

	public static NetworkProtocol fromValue(String value) {
		switch (value) {
		case "HTTP/1.1":
			return NetworkProtocol.HTTP;
		default:
			throw new IllegalArgumentException(value + " protocol is not supported");
		}
	}

	public String getValue() {
		return value;
	}
}
