package client;

/**
 * This interface defines all methods related to HashCode generation.
 * 
 * @author anandkulkarni
 *
 */
public interface HashGeneratorI {

	/**
	 * This method should generate hashcode for the given contents.
	 * 
	 * @param contents
	 *            input contents whose hashcode is to be generated.
	 * @return hashcode.
	 */
	public String generate(String contents);

}
