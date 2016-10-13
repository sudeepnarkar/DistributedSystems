package cs557.httpServer.constants;

/**
 * This enum lists various header entities that are used in this application.
 * 
 * @author anandkulkarni
 *
 */
public enum ResponseHeaders {
	DATE("Date"), CONTENTTYPE("Content-Type"), CONTENTLENGTH("Content-Length"), RESOURCELMDATE("Last-Modified"), SERVER(
			"Server");
	String value;

	private ResponseHeaders(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	};

	@Override
	public String toString() {
		return value;
	}

}
