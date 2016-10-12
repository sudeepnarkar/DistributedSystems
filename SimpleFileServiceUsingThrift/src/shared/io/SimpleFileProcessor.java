package shared.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import shared.FileProcessorI;

public class SimpleFileProcessor implements FileProcessorI {

	@Override
	public String read(String fileName) {
		try (BufferedReader inputReader = new BufferedReader(new FileReader(fileName));) {
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = inputReader.readLine()) != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
			}
			sb.setLength(sb.length() - 1);
			return sb.toString();
		} catch (FileNotFoundException exception) {
			System.err.println("Could not locate file : " + fileName);
			exception.printStackTrace();
			System.exit(1);
		} catch (IOException exception) {
			System.err.println("Error occured while reader a file : " + fileName);
			exception.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	@Override
	public void write(String fileName, String contents) {
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));) {
			bufferedWriter.write(contents);
		} catch (IOException e) {
			System.err.println("Error occured while serving a request.");
			e.printStackTrace();
			System.exit(1);
		}
	}

}
