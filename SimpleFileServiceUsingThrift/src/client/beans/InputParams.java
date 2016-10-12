package client.beans;

public class InputParams {
	private String user = null;
	private String serverHostName = null;
	private int serverPortNumber = 0;
	private String fileName = null;
	private String operation = null;

	public String getServerHostName() {
		return serverHostName;
	}

	public void setServerHostName(String serverHostName) {
		this.serverHostName = serverHostName;
	}

	public int getServerPortNumber() {
		return serverPortNumber;
	}

	public void setServerPortNumber(int serverPortNumber) {
		this.serverPortNumber = serverPortNumber;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "InputParams [user=" + user + ", serverHostName=" + serverHostName + ", serverPortNumber="
				+ serverPortNumber + ", fileName=" + fileName + ", operation=" + operation + "]";
	}
}
