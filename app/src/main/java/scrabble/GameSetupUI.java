package scrabble;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import scrabble.Controller.SetupWindowController;
import scrabble.Log.Logging;

import java.util.logging.Level;


/**
 * The GameSetupUI class is responsible for launching the setup window for the Scrabble game.
 */
public class GameSetupUI extends Application {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Logging.class.getCanonicalName());
    /**
     * Launches the setup window for the Scrabble game.
     *
     * @param primaryStage The primary stage of the JavaFX application.
     */
    @Override
    public void start(Stage primaryStage) {
        createSetupWindow(primaryStage, new Game());
    }

    private void createSetupWindow(Stage primaryStage, Game game) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SetupWindow.fxml"));
            Pane rootPane = loader.load();
            SetupWindowController setupWindowController = loader.getController();
            setupWindowController.setUp(primaryStage, game);
            // fill in scene and stage setup
            Scene scene = new Scene(rootPane);
            // configure and show stage
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(420);
            primaryStage.setMinHeight(250);
            primaryStage.setTitle("Papi's Scrabble");
            primaryStage.show();
            logger.log(Level.INFO, "Game setup window created.");
        } catch (Exception e) {
           logger.log(Level.SEVERE, "Failed to create setup window.", e);
        }
    }
}
