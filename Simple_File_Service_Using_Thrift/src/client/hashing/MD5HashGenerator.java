package client.hashing;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import client.HashGeneratorI;

/**
 * This class contains the logic to generate hash value suing MD5 hashing
 * method.
 * 
 * @author anandkulkarni
 *
 */
public class MD5HashGenerator implements HashGeneratorI {

	private MessageDigest md = null;

	public MD5HashGenerator() {
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Error occured while generating Hash.");
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public String generate(String contents) {
		// TODO: Need to double check logic to
		md.update(contents.getBytes());
		byte[] hashBytes = md.digest();
		StringBuilder hashString = new StringBuilder("");
		for (int i = 0; i < hashBytes.length; i++) {
			if ((0xff & hashBytes[i]) < 0x10) {
				hashString.append("0" + Integer.toHexString((0xFF & hashBytes[i])));
			} else {
				hashString.append(Integer.toHexString(0xFF & hashBytes[i]));
			}
		}
		return hashString.toString();
	}
}
