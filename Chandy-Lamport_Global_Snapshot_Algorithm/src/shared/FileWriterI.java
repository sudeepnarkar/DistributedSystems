package shared;

/**
 * This interface is used to define methods related to writing contents to a
 * file.
 * 
 * @author anandkulkarni
 *
 */
public interface FileWriterI {

	/**
	 * This method should contain the logic to write contents to a file.
	 * 
	 * @param contents
	 *            contents to e written to a file.
	 */
	public void write(String contents);
}
