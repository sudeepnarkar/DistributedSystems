package cs557.httpServer.threadmgmt;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cs557.httpServer.beans.HttpResponseHeaders;
import cs557.httpServer.constants.Constants;
import cs557.httpServer.constants.NetworkProtocol;
import cs557.httpServer.constants.RequestType;
import cs557.httpServer.constants.ResponseStatus;
import cs557.httpServer.driver.ResultInterface;
import cs557.httpServer.driver.StdOutInterface;

public class Worker extends Thread {
	private Socket connectionSocket;

	private ResultInterface resultInterface;
	private StdOutInterface stdOutInterface;

	private static final Pattern REQUEST_PATTERN = Pattern.compile("(.+) /(.+) (.+)");

	public Worker(Socket connectionSocketIn, ResultInterface resultInterfaceIn, StdOutInterface stdOutInterfaceIn) {
		super();
		connectionSocket = connectionSocketIn;
		resultInterface = resultInterfaceIn;
		stdOutInterface = stdOutInterfaceIn;
	}

	/**
	 * This method is called by each thread that is spawned by the server. This
	 * method contains the logic to process a HTTP GET request and to write an
	 * HTTP response along with appropriate headers.
	 */
	@Override
	public synchronized void run() {
		validateRootDirectory();
		try (DataOutputStream dataOutPutStream = new DataOutputStream(connectionSocket.getOutputStream());
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(connectionSocket.getInputStream()));) {
			String requestString = bufferedReader.readLine();
			Matcher requestPatternMatcher = REQUEST_PATTERN.matcher(requestString);
			requestPatternMatcher.find();
			RequestType requestType = RequestType.fromValue(requestPatternMatcher.group(1));
			String resourceURI = requestPatternMatcher.group(2);
			NetworkProtocol protocol = NetworkProtocol.fromValue(requestPatternMatcher.group(3));
			File file = new File(Constants.ROOT_DIRECTORY_NAME + File.separator + resourceURI);
			HttpResponseHeaders httpResponseHeaders = null;
			if (file.exists()) {
				// Generate 200 response.getProtocol
				httpResponseHeaders = new HttpResponseHeaders().withProtocol(protocol).withResource(file)
						.build(ResponseStatus.OK);
				dataOutPutStream.write(httpResponseHeaders.getMessage().getBytes());
				InputStream inputFileStream = new FileInputStream(file);
				byte[] buffer = new byte[8192];
				int size = 0;
				while ((size = inputFileStream.read(buffer, 0, buffer.length)) > 0) {
					dataOutPutStream.write(buffer, 0, size);
					dataOutPutStream.flush();
				}
				inputFileStream.close();
				stdOutInterface
						.write(File.separator + resourceURI + "|" + connectionSocket.getInetAddress().getHostAddress()
								+ "|" + resultInterface.update(resourceURI));
			} else {
				// Generate 404 response.
				httpResponseHeaders = new HttpResponseHeaders().withProtocol(protocol).build(ResponseStatus.NOT_FOUND);
				dataOutPutStream.write(httpResponseHeaders.getMessage().getBytes());
				dataOutPutStream.write(Constants.ERR_RESPONSE.getBytes());
			}
		} catch (IllegalStateException exception) {
			System.err.println("Unexpected Request format encountered. Server is shutting down...");
			System.exit(1);
		} catch (SocketException exception) {
			if (exception.getMessage().equals("Connection reset")) {
				// swallow this exception since one of the clients may have
				// terminated the connection. Nothing we need to do here.
			} else {
				System.err.println("Error occured while generating a response");
				System.exit(1);
			}
		} catch (IOException exception) {
			System.err.println("Error occured while generating a response");
			System.exit(1);
		} finally {
			try {
				connectionSocket.close();
			} catch (IOException e) {
				System.err.println("Error occured while closing a socket connection.");
				System.exit(1);
			}
		}
	}

	private synchronized void validateRootDirectory() {
		File file = new File(Constants.ROOT_DIRECTORY_NAME);
		if (!(Boolean.logicalAnd(file.exists(), file.isDirectory()))) {
			System.err.println("Could not find root directory(directory name : " + file.getName()
					+ ") on server. Server is shutting down...");
			System.exit(1);
		}
	}
}