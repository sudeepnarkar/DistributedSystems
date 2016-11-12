package controller.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import controller.FileReaderI;

/**
 * This class contains logic to read and write to a file using BufferedReader
 * and BufferedWriter.
 * 
 * @author anandkulkarni
 *
 */
public class SimpleFileReader implements FileReaderI {

	private String fileName = null;
	private BufferedReader inputReader;

	public SimpleFileReader(String fileNameIn) {
		fileName = fileNameIn;
		try {
			inputReader = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException exception) {
			System.err.println("Error occured while reading a file : " + fileName);
			exception.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public String read() {
		try {
			String line = null;
			while ((line = inputReader.readLine()) != null) {
				return line;
			}
		} catch (IOException exception) {
			System.err.println("Error occured while reader a file : " + fileName);
			exception.printStackTrace();
			System.exit(1);
		}
		return null;
	}

}