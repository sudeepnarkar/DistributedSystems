package client.constants;

public enum Operation {
	READ("read"), WRITE("write"), LIST("list");
	String value;

	private Operation(String valueIn) {
		value = valueIn;
	}

	public String getValue() {
		return value;
	}

	public static Operation fromValue(String value) {
		switch (value) {
		case "read":
			return Operation.READ;
		case "write":
			return Operation.WRITE;
		case "list":
			return Operation.LIST;
		default:
			throw new IllegalArgumentException(value + " is not supported by the application.");
		}
	}
}
