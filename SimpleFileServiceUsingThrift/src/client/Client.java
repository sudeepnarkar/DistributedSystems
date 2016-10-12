package client;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import client.beans.InputParams;
import client.hashing.MD5HashGenerator;
import client.parser.SimpleCliParser;
import client.util.InputArgumentsValidator;
import fileService.FileStore;
import shared.io.SimpleFileProcessor;

public class Client {

	private static ClientProcessor processor = null;
	private static InputArgumentsValidator validator = null;

	public static void main(String args[]) {
		try {
			processor = new ClientProcessor(new MD5HashGenerator(), new SimpleFileProcessor());
			validator = new InputArgumentsValidator();
			CommandLineArgumentParserI parser = new SimpleCliParser(args);
			if (!validator.validateNumberOfArgs(args, 6) && !validator.validateNumberOfArgs(args, 8)) {
				System.err.println("Please enter valid number of arguments:");
				System.err.println("Help: ");
				System.err.println(
						"8 Args : ./client <hostname> <portNumber> --operation <operation> --user <user> --filename <filename>");
				System.err.println("6 Args: ./client <hostname> <portNumber> --operation <operation> --user <user>");
				System.exit(1);
			}
			InputParams inputParams = parser.parse();
			TTransport transport = new TSocket(inputParams.getServerHostName(), inputParams.getServerPortNumber());
			transport.open();
			TProtocol protocol = new TBinaryProtocol(transport);
			FileStore.Client client = new FileStore.Client(protocol);
			processor.process(client, inputParams);
			transport.close();
		} catch (TException exception) {
			System.err.println("Error occured while sending a request. Please check input arguments and try again.");
			exception.printStackTrace();
			System.exit(1);
		}
	}
}
