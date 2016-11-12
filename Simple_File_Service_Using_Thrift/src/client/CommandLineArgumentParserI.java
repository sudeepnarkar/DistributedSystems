package client;

import client.beans.InputParams;

/**
 * This interface defines methods related to parsing command line arguments.
 * 
 * @author anandkulkarni
 *
 */
public interface CommandLineArgumentParserI {

	/**
	 * This method should parse command line arguments.
	 * 
	 * @return an object of type InputParams that contains all input parameters.
	 */
	public InputParams parse();
}
