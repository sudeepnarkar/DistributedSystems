package shared;

/**
 * This interface defines all methods related to file processing.
 * 
 * @author anandkulkarni
 *
 */
public interface FileProcessorI {

	/**
	 * This method should contain the logic to read contents of the file.
	 * 
	 * @param fileName
	 *            name of the file to read.
	 * @return contents of the file.
	 */
	public String read(String fileName);

	/**
	 * This method should contain the logic to read contents of the file.
	 * 
	 * @param fileName
	 *            name of the file to write to.
	 * @param contents
	 *            contents to be written to a file.
	 */
	public void write(String fileName, String contents);
}
