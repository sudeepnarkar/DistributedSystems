package cs557.httpServer.threadmgmt;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import cs557.httpServer.driver.ResultInterface;
import cs557.httpServer.driver.StdOutInterface;
import cs557.httpServer.util.PortScannerUtil;

/**
 * This class acts a server. It contains an implementation of server socket that
 * listens to a particular port for requests. It spawns a new thread for each
 * new request.
 * 
 * @author anandkulkarni
 *
 */
public class Server implements Runnable {

	ResultInterface resultInerface = null;
	StdOutInterface stdOutInterface = null;
	private static final String SERVER_INFORMATION_FORMAT = "Port Number: %s \nHost Name: %s \n";

	public Server(ResultInterface resultInerfaceIn, StdOutInterface stdOutInterfaceIn) {
		super();
		resultInerface = resultInerfaceIn;
		stdOutInterface = stdOutInterfaceIn;
	}

	/**
	 * This method contains an implementation of server thread. It contains the
	 * logic to spawn a new thread for each request.
	 */
	@Override
	public void run() {
		Integer portNumber = PortScannerUtil.findAFreePort();
		try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
			String hostName = serverSocket.getInetAddress().getLocalHost().getHostName();
			System.out.println(String.format(SERVER_INFORMATION_FORMAT, portNumber, hostName));
			while (true) {
				Socket connectionSocket = serverSocket.accept();
				Thread thread = new Worker(connectionSocket, resultInerface, stdOutInterface);
				thread.start();
			}
		} catch (IOException exception) {
			System.err.println("Error occuered while processing a request.");
			exception.printStackTrace();
			System.exit(1);
		}
	}
}
