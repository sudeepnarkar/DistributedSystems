package shared.logger;

/**
 * This enum is used to list log levels supported by this application.
 * 
 * @author anandkulkarni
 *
 */
public enum LogLevel {
	INFO(0), DEBUG(1), ERROR(2), OUTPUT(3);
	int code;

	private LogLevel(int codeIn) {
		code = codeIn;
	}

	public int getCode() {
		return code;
	}
}
