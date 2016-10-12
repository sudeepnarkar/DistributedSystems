package client.util;

import client.constants.CliArgs;

public class InputArgumentsValidator {

	public boolean validateIfNull(CliArgs arg, String value) {
		if (value == null || value.isEmpty() || value.equals("")) {
			System.err.println("'" + arg.getValue() + "' parameter is not provided.");
			System.exit(1);
		}
		return true;
	}

	public boolean validateNumberOfArgs(String[] args, int number) {
		return args.length != number ? false : true;
	}
}
