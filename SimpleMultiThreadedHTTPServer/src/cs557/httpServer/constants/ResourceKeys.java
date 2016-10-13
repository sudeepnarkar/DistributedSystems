package cs557.httpServer.constants;

/**
 * This enum lists different keys that map to the corresponding keys in a
 * configuration file.
 * 
 * @author anandkulkarni
 *
 */
public enum ResourceKeys {
	ROOTDIRNAME("RootDirectoryName"), SERVERNAME("ServerName"), ERR_RESPONSE("404Response");

	private String value;

	private ResourceKeys(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
