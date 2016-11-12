package controller;

/**
 * This interface defines all methods related to file processing.
 * 
 * @author anandkulkarni
 *
 */
public interface FileReaderI {

	/**
	 * This method should contain the logic to read contents of the file line by
	 * line. So each call to this method should return next line from the file.
	 * 
	 * @return next line or null if EOF is reached.
	 */
	public String read();

}
