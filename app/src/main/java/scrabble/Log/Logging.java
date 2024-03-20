package scrabble.Log;

import java.io.IOException;
import java.util.Locale;
import java.util.logging.LogManager;
import java.util.logging.Logger;


/**
 * The Logging class provides methods for configuring and initializing the Java Logging API.
 */
public class Logging {
	private static final Logger logger = Logger.getLogger(Logging.class.getCanonicalName());

	/**
	 * Initializes the Java Logging API with the configuration specified in the log.properties file.
	 *
	 * @throws IOException if there is an error reading the configuration file.
	 */
	public static void initialize() throws IOException {
		Locale.setDefault(Locale.ENGLISH);
		LogManager.getLogManager().readConfiguration(Logging.class.getResourceAsStream("/config/log.properties"));
		logger.info("Logging initialized.");
	}
}