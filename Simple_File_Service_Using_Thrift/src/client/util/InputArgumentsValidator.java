package client.util;

import client.constants.CliArgs;

/**
 * This class contains the logic to validate input arguments.
 * 
 * @author anandkulkarni
 *
 */
public class InputArgumentsValidator {

	/**
	 * This method contains the logic to validate if a given value is null.
	 * 
	 * @param arg
	 *            argument name
	 * @param value
	 *            argument value
	 * @return true if value is not not null. otherwise throws an error.
	 */
	public boolean validateIfNull(CliArgs arg, String value) {
		if (value == null || value.isEmpty() || value.equals("")) {
			System.err.println("'" + arg.getValue() + "' parameter is not provided.");
			System.exit(1);
		}
		return true;
	}

	/**
	 * This method contains the logic to validates number of command line
	 * arguments.
	 * 
	 * @param args
	 *            command line arguments array.
	 * @param number
	 *            number to validate against.
	 * @return returns true if the number of arguments is equal to expected
	 *         number otherwise throws an error.
	 */
	public boolean validateNumberOfArgs(String[] args, int number) {
		return args.length != number ? false : true;
	}
}
