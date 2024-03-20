package scrabble.Controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import scrabble.Game;
import scrabble.Log.Logging;
import scrabble.Model.Player;
import scrabble.config.Config;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The SetupWindowControler class is responsible for controlling the setup window of the game.
 * It sets up the number of players and their usernames, and enables the creation of a new game.
 */
public class SetupWindowController {

    private Boolean languageIsSet = false;
    private static final Logger logger = Logger.getLogger(Logging.class.getName());
    private Stage primaryStage;
    private final Locale englishLocal = new Locale("en");
    private final Locale germanLocal = new Locale("de");
    private ResourceBundle messages;
    private Config.LANGUAGE chosenLanguage;
    private Game game;

    @FXML
    private MenuButton chooseLanguage;

    @FXML
    private Button createGameButton;

    @FXML
    private TextField numberOfPlayers;

    @FXML
    private HBox hboxPlayer1;

    @FXML
    private HBox hboxPlayer2;

    @FXML
    private HBox hboxPlayer3;

    @FXML
    private HBox hboxPlayer4;

    @FXML
    private Label labelNumberOfPlayer;
    @FXML
    private Label labelPlayer1;

    @FXML
    private Label language;

    @FXML
    private Label labelPlayer2;

    @FXML
    private Label labelPlayer3;

    @FXML
    private Label labelPlayer4;

    @FXML
    private Label welcome;

    @FXML
    private TextField player1;

    @FXML
    private TextField player2;

    @FXML
    private TextField player3;

    @FXML
    private TextField player4;

    /**
     * Creates a new game with the specified language and player list.
     * Open for each player the game window
     */
    @FXML
    private void createGame() {
        game.setUpGame(chosenLanguage, createPlayerList(Integer.parseInt(numberOfPlayers.getText())));
        game.getPlayers().forEach(player -> openGameWindow(player, game));
        primaryStage.close();
    }

    private void openGameWindow(Player player, Game game) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/GameView.fxml"));
            Pane rootPane = loader.load();
            Scene scene = new Scene(rootPane);
            Stage gameWindow = new Stage();
            gameWindow.setScene(scene);
            gameWindow.setMinHeight(900);
            gameWindow.setMinWidth(900);
            GameViewController gameViewController = loader.getController();
            gameViewController.setUpGameViewController(player, game, gameWindow, messages);
            gameWindow.setTitle(messages.getString("gameViewName") + " " + messages.getString("player")  + ": " + player.getPlayerName());
            gameWindow.show();
            logger.log(Level.INFO, "Game window created for: {0}.", player.getPlayerName());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error starting up UI. ", e.getMessage());
        }
    }

    /**
     * Sets up the game creation controller by adding listeners.
     *
     * @param primaryStage the primary stage
     * @param game          the game
     */
    public void setUp(Stage primaryStage, Game game) {
        this.primaryStage = primaryStage;
        this.game = game;
        List<MenuItem> languages = new ArrayList<>();
        for (Config.LANGUAGE language : Config.LANGUAGE.values()) {
            MenuItem menuItem = new MenuItem(language.toString());
            menuItem.setOnAction(e -> setChooseLanguage(language));
            languages.add(menuItem);
        }
        chooseLanguage.getItems().addAll(languages);
        messages = ResourceBundle.getBundle("messages", englishLocal);
        updateText();
        addListener();
        createGameButton.setDisable(true);
        hboxPlayer1.setVisible(false);
        hboxPlayer2.setVisible(false);
        hboxPlayer3.setVisible(false);
        hboxPlayer4.setVisible(false);
    }

    /**
     * Adds listeners to the number of players and player name fields.
     */
    private void addListener() {
        numberOfPlayers.textProperty().addListener((observable, oldValue, newValue) -> validateNumberOfPlayers(newValue));
        player1.textProperty().addListener((observable, oldValue, newValue) -> validateUsername(newValue, 1));
        player2.textProperty().addListener((observable, oldValue, newValue) -> validateUsername(newValue, 2));
        player3.textProperty().addListener((observable, oldValue, newValue) -> validateUsername(newValue, 3));
        player4.textProperty().addListener((observable, oldValue, newValue) -> validateUsername(newValue, 4));
    }

    /**
     * Sets the language chosen by the user and enables the create game button if all necessary fields are filled.
     *
     * @param language the language chosen by the user
     */
    private void setChooseLanguage(Config.LANGUAGE language) {
        languageIsSet = true;
        chooseLanguage.setText(language.toString());
        chosenLanguage = language;
        checkIfCreateGameButtonShouldBeEnabled();
        switch (language) {
            case DE -> messages = ResourceBundle.getBundle("messages", germanLocal);
            case EN -> messages = ResourceBundle.getBundle("messages", englishLocal);
            default -> throw new IllegalStateException("Unexpected value: " + language);
        }
        updateText();
    }

    private void updateText(){
        welcome.setText(messages.getString("welcome"));
        labelNumberOfPlayer.setText(messages.getString("numberOfPlayer"));
        labelPlayer1.setText(messages.getString("player") + "1");
        labelPlayer2.setText(messages.getString("player") + "2");
        labelPlayer3.setText(messages.getString("player") + "3");
        labelPlayer4.setText(messages.getString("player") + "4");
        language.setText(messages.getString("language"));
        createGameButton.setText(messages.getString("createScrabble"));
        numberOfPlayers.setPromptText(messages.getString("between2And4"));
    }

    /**
     * Checks if the create game button should be enabled based on whether all player names have been validated and the language has been set.
     */
    private void checkIfCreateGameButtonShouldBeEnabled() {
        boolean allPlayersValidated = true;
        try {
            for (int i = 1; i <= Integer.parseInt(this.numberOfPlayers.getText()); i++) {
                if (!getHTextFieldForPlayer(i).getStyle().equals("-fx-border-color: green") || !numberOfPlayers.getStyle().equals("-fx-border-color: green")) {
                    allPlayersValidated = false;
                    break;
                }
            }
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Error parsing number of players. ", e.getMessage());
            allPlayersValidated = false;
        }
        if (languageIsSet) {
            createGameButton.setDisable(!allPlayersValidated);
        }
    }

    /**
     * Returns the horizontal text field associated with the specified player number.
     *
     * @param playerNumber the player number (1-4)
     * @return the horizontal text field associated with the specified player number
     */
    private TextField getHTextFieldForPlayer(int playerNumber) {
        return switch (playerNumber) {
            case 1 -> player1;
            case 2 -> player2;
            case 3 -> player3;
            case 4 -> player4;
            default -> null;
        };
    }

    /**
     * Validates the number of players entered by the user and changes the visibility of the username input fields accordingly.
     * Also sets the style of the number of players input field to green if the input is valid and to red if the input is invalid.
     *
     * @param numberOfPlayers the number of players entered by the user
     */
    private void validateNumberOfPlayers(String numberOfPlayers) {
        try {
            int numberOfPlayer = Integer.parseInt(numberOfPlayers);
            if (numberOfPlayer < 2 || numberOfPlayer > 4) {
                throw new NumberFormatException();
            }
            this.numberOfPlayers.setStyle("-fx-border-color: green");
            changeVisibilityForUsernameInput(numberOfPlayer);
            checkIfCreateGameButtonShouldBeEnabled();
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Invalid number of players entered. ", e.getMessage());
            changeVisibilityForUsernameInput(0);
            this.numberOfPlayers.setStyle("-fx-border-color: red");
            checkIfCreateGameButtonShouldBeEnabled();
        }

    }

    /**
     * Checks if the given username already exists in any of the other player fields.
     *
     * @param username     the username to be checked
     * @param playerNumber the number of the player whose username is being checked
     */
    private boolean checkIfUsernameAlreadyExists(String username, int playerNumber) {
        boolean usernameAlreadyExists = false;
        TextField[] players = {player1, player2, player3, player4};
        for (int i = 0; i < players.length; i++) {
            if (i + 1 != playerNumber && username.toLowerCase().equals(players[i].getText().toLowerCase())) usernameAlreadyExists = true;
        }
        return usernameAlreadyExists;
    }

    /**
     * Validates the given username for the player identified by playerNumber.
     * The validation includes checking if the username matches the REGEX_USERNAME from the Config class,
     * and if it doesn't exist in any of the other player fields.
     *
     * @param username     the username to be validated
     * @param playerNumber the number of the player whose username is being validated
     * @throws IllegalArgumentException if an invalid player number is provided
     */
    private void validateUsername(String username, int playerNumber) {
        TextField player = switch (playerNumber) {
            case 1 -> player1;
            case 2 -> player2;
            case 3 -> player3;
            case 4 -> player4;
            default -> throw new IllegalArgumentException("Invalid player number: " + playerNumber);
        };

        if (!username.matches(Config.REGEX_USERNAME) || checkIfUsernameAlreadyExists(username, playerNumber)) {
            player.setStyle("-fx-border-color: red");
        } else {
            player.setStyle("-fx-border-color: green");
        }
        checkIfCreateGameButtonShouldBeEnabled();
    }

    /**
     * Changes the visibility of the input fields for the players based on the number of players.
     *
     * @param numberOfPlayers the number of players in the game
     */
    private void changeVisibilityForUsernameInput(int numberOfPlayers) {
        hboxPlayer1.setVisible(numberOfPlayers >= 1);
        hboxPlayer2.setVisible(numberOfPlayers >= 2);
        hboxPlayer3.setVisible(numberOfPlayers >= 3);
        hboxPlayer4.setVisible(numberOfPlayers >= 4);
    }

    /**
     * Creates a list of Player objects based on the number of players.
     *
     * @param numberOfPlayers the number of players in the game
     * @return a list of Player objects
     */
    private List<Player> createPlayerList(int numberOfPlayers) {
        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= numberOfPlayers; i++) {
            players.add(new Player(getHTextFieldForPlayer(i).getText()));
        }
        logger.log(Level.INFO, "Created player list with following usernames: {0}", players.stream().map(Player::getPlayerName).collect(Collectors.toList()));
        return players;
    }
}