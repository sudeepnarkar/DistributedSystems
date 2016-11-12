package shared.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import shared.FileWriterI;
import shared.logger.LogLevel;
import shared.logger.Logger;

public class SimpleFileWriter implements FileWriterI {

	private PrintWriter writer = null;

	public SimpleFileWriter(String fileNameIn) {
		File file = null;
		try {
			file = new File(fileNameIn);
			if (!file.isDirectory() && file.exists()) {
				file.delete();
				file.createNewFile();
			}
			writer = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
		} catch (FileNotFoundException exception) {
			Logger.writeMessage("Error occured while reading a file : " + file.getName(), LogLevel.ERROR);
			exception.printStackTrace();
			System.exit(1);
		} catch (IOException exception) {
			Logger.writeMessage("Error occured while reading/writing a file.", LogLevel.ERROR);
			exception.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public synchronized void write(String contents) {
		writer.println(contents);
		writer.flush();
	}
}
