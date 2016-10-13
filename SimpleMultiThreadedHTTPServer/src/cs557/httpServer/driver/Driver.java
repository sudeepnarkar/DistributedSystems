package cs557.httpServer.driver;

import cs557.httpServer.result.Results;
import cs557.httpServer.threadmgmt.Server;

/**
 * This class contains the main method.
 * 
 * @author anandkulkarni
 *
 */
public class Driver {

	/**
	 * Main method. Starting point of execution of a server. This method spawns
	 * a thread of Type Server to start a server.
	 * 
	 * @param args
	 *            Command line arguments if any.
	 */
	public static void main(String[] args) {
		try {
			ResultInterface result = new Results();
			Thread server = new Thread(new Server(result, (StdOutInterface) result));
			server.start();
			server.join();
		} catch (InterruptedException exception) {
			System.err.println("Error occured while running a server thread.");
			exception.printStackTrace();
			System.exit(1);
		}
	}
}