package cs557.httpServer.constants;

/**
 * This enum lists various HTTP response types that are supported by this
 * application.
 * 
 * @author anandkulkarni
 *
 */
public enum ResponseStatus {
	OK(200, "OK"), NOT_FOUND(404, "Not Found");
	Integer code;
	String value;

	private ResponseStatus(Integer codeIn, String valueIn) {
		code = codeIn;
		value = valueIn;
	}

	public Integer getCode() {
		return code;
	}

	public String getValue() {
		return value;
	}

	public static ResponseStatus fromCode(Integer code) {
		switch (code) {
		case 200:
			return ResponseStatus.OK;
		case 404:
			return ResponseStatus.NOT_FOUND;
		default:
			throw new IllegalArgumentException("Status code: " + code + " is not supported");
		}
	}
}
