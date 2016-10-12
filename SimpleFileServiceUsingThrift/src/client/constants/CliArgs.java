package client.constants;

public enum CliArgs {
	OPERATION("operation"), USER("user"), FILENAME("filename");
	private String value = null;

	private CliArgs(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static CliArgs fromValue(String value) {
		switch (value) {
		case "--operation":
			return CliArgs.OPERATION;
		case "--user":
			return CliArgs.USER;
		case "--filename":
			return CliArgs.FILENAME;
		default:
			throw new IllegalArgumentException(value + " is not supported by the application.");
		}
	}
}
