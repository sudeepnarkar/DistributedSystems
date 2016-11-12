package server;

import java.io.File;
import java.net.BindException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

import fileService.FileStore;
import fileService.FileStore.Processor;
import shared.io.SimpleFileProcessor;

/**
 * Driver code for Client. Contains the main method.
 * 
 * @author anandkulkarni
 *
 */
public class Server {

	public static FileStoreHandler handler;

	@SuppressWarnings("rawtypes")
	public static FileStore.Processor processor;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {
		try {
			if (args.length != 1) {
				System.err.println("Please enter valid number of arguments.");
				System.exit(1);
			}
			validateInputArgs(args);
			final Integer portNumber = Integer.parseInt(args[0]);
			handler = new FileStoreHandler(new SimpleFileProcessor());
			processor = new FileStore.Processor(handler);

			Runnable simple = new Runnable() {
				@Override
				public void run() {
					simple(processor, portNumber);
				}
			};
			new Thread(simple).start();
		} catch (NumberFormatException exception) {
			System.err.println("Please enter an argument in a valid format.");
			exception.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Creates the simple connection.
	 * 
	 * @param processor
	 *            FileStore processor instance
	 * @param portNumber
	 */
	public static void simple(@SuppressWarnings("rawtypes") Processor processor, int portNumber) {
		TServerTransport serverTransport = null;
		try {
			initializeRootDirectory();
			serverTransport = new TServerSocket(portNumber);
			TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
			System.out.println("Hostname: " + InetAddress.getLocalHost().getHostName());
			System.out.println("PortNumber: " + portNumber);
			server.serve();
		} catch (TTransportException exception) {
			if (exception.getCause() instanceof BindException) {
				System.out.println(portNumber + " is already in use. Please select a different port number.");
			} else {
				System.err.println("Error occured while starting a Server.");
			}
			exception.printStackTrace();
			System.exit(1);
		} catch (UnknownHostException exception) {
			System.err.println("Error occured while identifying a hostname");
			exception.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * This method creates a new empty directory with name "www".
	 */
	private static void initializeRootDirectory() {
		File rootDir = new File("www");
		rootDir.mkdir();
		Arrays.asList(rootDir.listFiles()).stream().forEach((fileName) -> {
			fileName.delete();
		});
	}

	/**
	 * Validate input arguments.
	 * 
	 * @param args
	 *            arguments to validated.
	 */
	private static void validateInputArgs(String[] args) {
		try {
			int portNumber = Integer.parseInt(args[0]);
			if (portNumber <= 0) {
				throw new NumberFormatException(" For input value: " + portNumber);
			}
		} catch (NumberFormatException exception) {
			System.err.println("Port number must be a positive integer value. Please enter a valid input.");
			exception.printStackTrace();
			System.exit(1);
		}
	}
}
