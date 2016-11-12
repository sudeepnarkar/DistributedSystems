package cs557.httpServer.util;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.activation.MimetypesFileTypeMap;

/**
 * This class contains the utility methods to read and retrieved various
 * properties configured via .properties file or other files from a file system.
 * 
 * @author anandkulkarni
 *
 */
public class ResourceHelper {
	private static final String MIME_TYPES_SOURCE_FILE = "/etc/mime.types";
	private static MimetypesFileTypeMap mimeTypes = null;

	static {
		initialize();
	}

	/**
	 * This method is used initialize all the resources.
	 */
	private static void initialize() {
		try {
			mimeTypes = new MimetypesFileTypeMap(MIME_TYPES_SOURCE_FILE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is used to retrieve mime type of a resource based on the name
	 * of the resource.
	 * 
	 * @param resourceName
	 *            name of the resource whose mime type is to be retrieved.
	 * @return mime type of a requested resourceName.
	 */
	public static String getMimeType(String resourceName) {
		return mimeTypes.getContentType(resourceName);
	}
}
