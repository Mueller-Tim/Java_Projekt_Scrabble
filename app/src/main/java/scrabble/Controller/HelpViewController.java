package scrabble.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.ResourceBundle;

/**
 * This class is responsible for managing the HelpWindow in a JavaFX application. It contains FXML elements representing
 * labels for displaying text related to game rules and instructions, as well as a GameViewController instance and a Stage
 * instance representing the HelpWindow.
 */
public class HelpViewController {

    public Label helpMenuTitle;
    @FXML
    private Label gameExplanationDescription;

    @FXML
    private Label gameExplanationLabel;

    @FXML
    private Label gameExplanationTitle;

    @FXML
    private Label playRulesTitle;

    private GameViewController gameViewController;
    private Stage helpStage;
    private ResourceBundle messages;

    /**
     * Sets up the HelpWindowController with the given parameters.
     *
     * @param messages           the ResourceBundle containing the language-specific text to display in the HelpWindow
     * @param helpStage          the Stage for the HelpWindow
     * @param gameViewController the GameViewController associated with the HelpWindow
     */
    public void setUp(ResourceBundle messages, Stage helpStage, GameViewController gameViewController) {
        this.helpStage = helpStage;
        this.messages = messages;
        this.gameViewController = gameViewController;
        setUpLanguage();
        addListener();
    }


    /**
     * Sets up the language for the help window based on the resource bundle passed to the help window controller.
     * It retrieves the appropriate language strings from the resource bundle and sets them as text for the help window labels.
     */
    private void setUpLanguage() {
        helpMenuTitle.setText(messages.getString("helpMenuTitle"));
        gameExplanationLabel.setText(messages.getString("gameExplanation"));
        gameExplanationDescription.setText(messages.getString("gameExplanationDescription"));
        playRulesTitle.setText(messages.getString("playRulesTitle"));
    }

    /**
     * Adds a listener to the help window stage. When the help window is closed, the listener calls the closeHelpWindow() method
     * in the gameViewController.
     */
    private void addListener() {
        helpStage.setOnCloseRequest(event -> gameViewController.closeHelpWindow());
    }


}