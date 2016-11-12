package shared.logger;

import java.util.Date;

import shared.FileWriterI;
import shared.io.SimpleFileWriter;

/**
 * This class is used to log information about different events occured during
 * the execution of application.
 * 
 * @author Anand Kulkarni
 *
 */
public class Logger {

	private static LogLevel logLevel = LogLevel.INFO;

	private static final String LOG_MESSAGE_PATTERN = "[%s] %s: %s" + System.lineSeparator();

	private static FileWriterI fileProcessor = null;

	static {
		initialize();
	}

	private static void initialize() {
		fileProcessor = new SimpleFileWriter("Log.log");
	}

	public static void setDebugValue(int levelIn) {
		switch (levelIn) {
		case 1:
			logLevel = LogLevel.DEBUG;
			break;
		case 0:
			logLevel = LogLevel.INFO;
			break;
		default:
			throw new IllegalArgumentException("Log level: " + levelIn + " is not supporte by this application.");
		}
	}

	/**
	 * This method write the given message to a file or console based on the log
	 * level.
	 * 
	 * @param message
	 *            message to be logged.
	 * @param levelIn
	 *            loglevel
	 */
	public static synchronized void writeMessage(String message, LogLevel levelIn) {
		switch (levelIn) {
		case OUTPUT:
		case ERROR:
			System.out.println(
					String.format(LOG_MESSAGE_PATTERN, new Date(System.currentTimeMillis()), levelIn, message));
		case DEBUG:
		case INFO:
			fileProcessor
					.write(String.format(LOG_MESSAGE_PATTERN, new Date(System.currentTimeMillis()), levelIn, message));
			break;
		}
	}

	@Override
	public String toString() {
		return "Log Level is " + logLevel;
	}
}