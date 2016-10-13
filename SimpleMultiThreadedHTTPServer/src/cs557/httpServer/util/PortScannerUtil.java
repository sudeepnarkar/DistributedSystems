package cs557.httpServer.util;

import java.io.IOException;
import java.net.ServerSocket;

public class PortScannerUtil {

	/*
	 * MIN_PORT_NO is 1024 because port numbers 0-1023 are priviledged and needs
	 * to be used through root user.
	 */
	private static final Integer MIN_PORT_NO = 1024;
	private static final Integer MAX_PORT_NO = 65535;

	/**
	 * This method is used to search an empty port.
	 * 
	 * @return returns an empty port number;
	 */
	public static Integer findAFreePort() {
		for (Integer portNo = MIN_PORT_NO; portNo <= MAX_PORT_NO; portNo++) {
			if (isAvailable(portNo)) {
				return portNo;
			}
		}
		System.err.println("No ports are availale.");
		System.exit(1);
		return -1;
	}

	/**
	 * This method contain the logic to check if the given port is free.
	 * 
	 * @param portNo
	 * @return
	 */
	private static boolean isAvailable(Integer portNo) {
		boolean isAvailable = false;
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(portNo);
			serverSocket.close();
			isAvailable = true;
		} catch (IOException e) {
			isAvailable = false;
		}
		return isAvailable;
	}
}