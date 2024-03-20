package scrabble;

import javafx.application.Application;
import scrabble.Log.Logging;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class representing the entry point for the scrabble application.
 * The main method starts the JavaFX user interface by launching the scrabble.GameSetupUI class.
 */

public class Main {
    private static final Logger logger = Logger.getLogger(Logging.class.getCanonicalName());
    /**
     * The main method of the client application.
     * It starts the JavaFX user interface by launching the scrabble.GameSetupUI class.
     * @param args the command line arguments (not used)
     */
    public static void main(String[] args) throws IOException {
        // Start UI
        Logging.initialize();
        logger.log(Level.FINE, "Starting Client Application...");
        Application.launch(GameSetupUI.class, args);
        logger.log(Level.FINE, "Client Application ended.");
    }
}
