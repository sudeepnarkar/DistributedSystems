package cs557.httpServer.result;

import java.util.LinkedHashMap;
import java.util.Map;

import cs557.httpServer.driver.ResultInterface;
import cs557.httpServer.driver.StdOutInterface;

/**
 * This class contains the logic to maintain bookkeeping of requests and output
 * the result on console.
 * 
 * @author anandkulkarni
 *
 */
public class Results implements ResultInterface, StdOutInterface {

	private Map<String, Integer> resourceAccessRecord = new LinkedHashMap<String, Integer>();

	/**
	 * This method writes a given string on the console.
	 */
	@Override
	public synchronized void write(String result) {
		System.out.println(result);
	}

	/**
	 * This method contains an implementation to update map data structure used
	 * for bookkeeping, with information about new request.
	 */
	@Override
	public synchronized int update(String resourceURI) {
		if (resourceAccessRecord.containsKey(resourceURI)) {
			resourceAccessRecord.put(resourceURI, resourceAccessRecord.get(resourceURI) + 1);
		} else {
			resourceAccessRecord.put(resourceURI, 1);
		}
		return resourceAccessRecord.get(resourceURI);
	}

}
