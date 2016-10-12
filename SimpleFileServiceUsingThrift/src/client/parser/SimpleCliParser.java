package client.parser;

import client.CommandLineArgumentParserI;
import client.beans.InputParams;
import client.constants.CliArgs;

public class SimpleCliParser implements CommandLineArgumentParserI {

	private String[] commandLineArguments = null;

	private static final String ARG_PREFIX = "--";

	public SimpleCliParser(String[] commandLineArgumentsIn) {
		super();
		this.commandLineArguments = commandLineArgumentsIn;
	}

	@Override
	public InputParams parse() {
		InputParams inputParams = new InputParams();
		inputParams.setServerHostName(commandLineArguments[0]);
		try {
			int portNumber = Integer.parseInt(commandLineArguments[1]);
			if (portNumber < 0) {
				throw new NumberFormatException(" For input value: " + portNumber);
			}
			inputParams.setServerPortNumber(portNumber);
		} catch (NumberFormatException exception) {
			System.err.println("PortNumber must be a positive integer value. Please enter a valid input.");
			exception.printStackTrace();
			System.exit(1);
		}
		for (int i = 2; (i + 1) < commandLineArguments.length; i++) {
			if (commandLineArguments[i].equals(ARG_PREFIX + CliArgs.OPERATION.getValue())) {
				inputParams.setOperation(commandLineArguments[i + 1]);
			} else if (commandLineArguments[i].equals(ARG_PREFIX + CliArgs.FILENAME.getValue())) {
				inputParams.setFileName(commandLineArguments[i + 1]);
			} else if (commandLineArguments[i].equals(ARG_PREFIX + CliArgs.USER.getValue())) {
				inputParams.setUser(commandLineArguments[i + 1]);
			}
		}
		return inputParams;
	}
}
