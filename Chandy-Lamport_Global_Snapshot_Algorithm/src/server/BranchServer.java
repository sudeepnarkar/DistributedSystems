package server;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

import shared.logger.LogLevel;
import shared.logger.Logger;
import snapshotService.Branch;
import snapshotService.Branch.Processor;

/**
 * This class contains the logic to start a multi-threaded branch server at a
 * given port.
 * 
 * @author anandkulkarni
 *
 */
public class BranchServer {

	public static void main(String[] args) {
		validateInputArgs(args);
		try {
			BranchServer branchServer = new BranchServer();
			String ipAddr = InetAddress.getLocalHost().getHostAddress();
			String branchName = args[0];
			int port = Integer.valueOf(args[1]);
			branchServer.startBranchServer(ipAddr, branchName, port);
		} catch (UnknownHostException exception) {
			Logger.writeMessage("Encountered an error while starting a Branch Server.", LogLevel.ERROR);
			exception.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * This method is used to start a multi-threaded server using given
	 * parameters.
	 * 
	 * @param ipAddr
	 *            ipAddress of the branch server
	 * @param branchName
	 *            name of the branch
	 * @param port
	 *            port number on which a branch server will listen to requests.
	 */
	private void startBranchServer(String ipAddr, String branchName, int port) {
		BranchServerHandler handler = new BranchServerHandler(ipAddr, port, branchName);
		Processor<BranchServerHandler> processor = new Branch.Processor<BranchServerHandler>(handler);
		TServerTransport serverTransport = null;
		TServer server = null;
		try {
			serverTransport = new TServerSocket(port);
			server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
			Logger.writeMessage("starting '" + branchName + "' server...", LogLevel.OUTPUT);
			Logger.writeMessage("ip address: " + InetAddress.getLocalHost().getHostAddress(), LogLevel.OUTPUT);
			server.serve();
		} catch (TTransportException exception) {
			Logger.writeMessage("Encountered an error while starting a Branch Server.", LogLevel.ERROR);
			exception.printStackTrace();
			System.exit(1);
		} catch (UnknownHostException exception) {
			Logger.writeMessage("Encountered an error while starting a Branch Server.", LogLevel.DEBUG);
			exception.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * This method validates the input arguments.
	 * 
	 * @param args
	 *            input arguments read from the command line.
	 */
	private static void validateInputArgs(String[] args) {
		try {
			if (args.length != 2) {
				Logger.writeMessage("Please enter valid number of arguments.", LogLevel.ERROR);
				System.exit(1);
			}
			int portNumber = Integer.parseInt(args[1]);
			if (portNumber <= 0) {
				throw new NumberFormatException(" For input value: " + portNumber);
			}
		} catch (NumberFormatException exception) {
			Logger.writeMessage("Port number must be a positive integer value. Please enter a valid input.",
					LogLevel.ERROR);
			System.exit(1);
		}
	}
}
