package scrabble.Controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import scrabble.Game;
import scrabble.Log.Logging;
import scrabble.Model.Board;
import scrabble.Model.Field;
import scrabble.Model.Player;
import scrabble.Model.Tile;
import scrabble.config.Config;

import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The GameViewController class is responsible for controlling the view of the game window, including
 * the game board, player hand, scoreboard, and buttons for interacting with the game. It contains
 * methods for setting up the initial state of the view, initializing the game board, updating the UI
 * when changes occur, and adding listeners for button clicks and other user interactions.
 */
public class GameViewController {
    private Player player;
    private Game game;
    private Stage stage;
    private ResourceBundle messages;
    private BooleanProperty isHelpWindowOpen;
    private Stage helpStage;
    private Pane source;
    private static final Logger logger = Logger.getLogger(Logging.class.getCanonicalName());

    @FXML
    public GridPane hand;

    @FXML
    public Button cancelSetting;

    @FXML
    private GridPane gridPane;

    @FXML
    private MenuItem buttonShowHelp;

    @FXML
    private Label currentTurn;

    @FXML
    private Button exchangeStonesButton;

    @FXML
    private Button finishTurnButton;

    @FXML
    private MenuItem menuQuitGame;

    @FXML
    private Menu quit;

    @FXML
    private Label scoreboard;

    @FXML
    private Label infoLabel;

    /**
     * Sets up the GameViewController with the given player, game, stage, and resource bundle.
     *
     * @param player   the player to set up the view for
     * @param game     the game to set up the view for
     * @param stage    the stage to set up the view on
     * @param messages the resource bundle containing the game's messages
     */
    public void setUpGameViewController(Player player, Game game, Stage stage, ResourceBundle messages) {
        this.player = player;
        this.game = game;
        this.stage = stage;
        this.messages = messages;
        this.isHelpWindowOpen = new SimpleBooleanProperty(false);
        changeButtonsUsability();
        addPanes();
        drawUIFromSource();
        addListeners();
        setText();
    }

    private void changeButtonsUsability() {
        cancelSetting.setDisable(true);
        if (player.equals(game.getCurrentPlayer())) {
            finishTurnButton.setDisable(false);
            exchangeStonesButton.setDisable(false);
        } else {
            finishTurnButton.setDisable(true);
            exchangeStonesButton.setDisable(true);
            if (game.getGameState() != Game.GAME_STATE.GAME_RUNNING) {
                for (Node tiles : hand.getChildren()) {
                    tiles.setDisable(true);
                }
            }
        }
    }

    private void addPanes() {
        for (int y = 0; y < Config.BOARD_ROW_SIZE; y++) {
            for (int x = 0; x < Config.BOARD_COL_SIZE; x++) {
                StackPane stackPane = createStackPane(18, 15, 150, 150);
                gridPane.add(stackPane, x, y);
                GridPane.setRowIndex(stackPane, y);
                GridPane.setColumnIndex(stackPane, x);
            }
        }
    }

    private StackPane createStackPane(int bigXOffset, int bigYOffset, int smallXOffset, int smallYOffset) {
        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(50, 50);
        stackPane.getChildren().add(addLabel(bigXOffset, bigYOffset));
        stackPane.getChildren().add(addSmallLabel(smallXOffset, smallYOffset));
        return stackPane;
    }

    private Label addLabel(int layoutX, int layoutY) {
        Label label = new Label();
        label.setContentDisplay(ContentDisplay.valueOf("CENTER"));
        label.setAlignment(Pos.valueOf("CENTER"));
        label.getStyleClass().add("bigLabel");
        label.setLayoutX(layoutX);
        label.setLayoutY(layoutY);
        return label;
    }

    private Label addSmallLabel(int layoutX, int layoutY) {
        Label smallLabel = new Label();
        smallLabel.getStyleClass().add("smallLabel");
        StackPane.setAlignment(smallLabel, Pos.valueOf("BOTTOM_RIGHT"));
        smallLabel.setLayoutX(layoutX);
        smallLabel.setLayoutY(layoutY);
        return smallLabel;
    }

    private void setEffectToPane(StackPane pane, int x, int y) {
        switch (game.getBoard().getField(y, x).getEffect()) {
            case DOUBLE_LETTER -> {
                pane.getStyleClass().add("doubleLetter");
                setFieldText(pane, "2x", messages.getString("letter"));
            }
            case DOUBLE_WORD -> {
                pane.getStyleClass().add("doubleWord");
                setFieldText(pane, "2x", messages.getString("word"));
            }
            case TRIPLE_LETTER -> {
                pane.getStyleClass().add("tripleLetter");
                setFieldText(pane, "3x", messages.getString("letter"));
            }
            case TRIPLE_WORD -> {
                pane.getStyleClass().add("tripleWord");
                setFieldText(pane, "3x", messages.getString("word"));
            }
            case NO_EFFECT -> setFieldText(pane, "", "");
            default -> throw new IllegalArgumentException("Unknown effect");
        }
    }

    private StackPane getStackPane(int x, int y) {
        for (Node node : gridPane.getChildren()) {
            if (Objects.nonNull(GridPane.getRowIndex(node)) && (GridPane.getRowIndex(node) == y)
                    && (GridPane.getColumnIndex(node) == x) && (node instanceof StackPane stackPane)) {
                return stackPane;
            }
        }
        throw new IllegalArgumentException("GridPane doesnt contain coordinates");
    }

    private void setFieldText(Pane pane, String big, String small) {
        Label bigLbl = (Label) pane.getChildren().get(0);
        Label smallLbl = (Label) pane.getChildren().get(1);
        bigLbl.setText(big);
        smallLbl.setText(small);
    }

    private void drawUIFromSource() {
        infoLabel.setVisible(false);
        drawEmptyField();
        updateUI();
    }

    private void drawEmptyField() {
        for (int y = 0; y < Config.BOARD_ROW_SIZE; y++) {
            for (int x = 0; x < Config.BOARD_COL_SIZE; x++) {
                StackPane pane = getStackPane(x, y);
                pane.getStyleClass().remove("tile");
                pane.getStyleClass().add("field");
                setEffectToPane(pane, x, y);
            }
        }
    }

    private void updateUI() {
        updateBoard(game.getBoard());
        updateHand(player.getTileList());
    }

    private void updateBoard(Board board) {
        for (int y = 0; y < Config.BOARD_ROW_SIZE; y++) {
            for (int x = 0; x < Config.BOARD_COL_SIZE; x++) {
                Field field = board.getField(y, x);
                StackPane pane = getStackPane(x, y);
                if (field.isOccupied()) {
                    setFieldText(pane, String.valueOf(field.getOccupant().letter()), String.valueOf(field.getOccupant().value()));
                    enableDragging(pane);
                    pane.getStyleClass().add("tile");
                } else {
                    addDropListener(pane);
                }
            }
        }
    }

    private void enableDragging(Pane pane) {
        pane.setOnDragDetected(event -> {
            if (game.getCurrentPlayer().equals(player)) {
                Dragboard dragboard = pane.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString("pane");
                pane.setOpacity(0.5);
                dragboard.setDragView(pane.snapshot(null, null));
                dragboard.setDragViewOffsetX(event.getX());
                dragboard.setDragViewOffsetY(event.getY());
                dragboard.setContent(content);
                source = pane;
                event.consume();
            }
        });

        pane.setOnDragDone(event -> pane.setOpacity(1));
    }

    private void addDropListener(Pane target) {
        target.setOnDragDropped(event -> {
            if (game.getCurrentPlayer().equals(player)) {
                Dragboard db = event.getDragboard();
                if (db.hasString()) {
                    int x = GridPane.getColumnIndex(target);
                    int y = GridPane.getRowIndex(target);
                    source.setOpacity(1);
                    cancelSetting.setDisable(false);
                    if (gridPane.equals(source.getParent())) restoreGameField();
                    gridPane.getChildren().remove(target);
                    gridPane.getChildren().remove(source);
                    gridPane.add(source, x, y);
                    event.setDropCompleted(true);
                } else {
                    event.setDropCompleted(false);
                }
                event.consume();
            }
        });
    }

    private void updateHand(List<Tile> tileList) {
        hand.getChildren().clear();
        for (int i = 0; i < tileList.size(); i++) {
            StackPane handElement = createStackPane(12, 10, 30, 30);
            handElement.getStyleClass().add("tile");
            setFieldText(handElement, String.valueOf(tileList.get(i).letter()), String.valueOf(tileList.get(i).value()));
            enableDragging(handElement);
            hand.add(handElement, i, 0);
        }
    }

    private void setText() {
        buttonShowHelp.setText(messages.getString("showHelp"));
        quit.setText(messages.getString("quit"));
        menuQuitGame.setText(messages.getString("quitGame"));
        currentTurn.setText(messages.getString("currentTurn") + ": " + game.getCurrentPlayer().getPlayerName());
        finishTurnButton.setText(messages.getString("finishTurn"));
        exchangeStonesButton.setText(messages.getString("exchangeStones"));
        cancelSetting.setText(messages.getString("cancelSetting"));
        scoreboard.setText(createScoreboardString());
    }

    private void addListeners() {
        stage.setOnCloseRequest(event -> game.handleEndingForCloseWindow());
        player.getPoints().addListener((observable, oldValue, newValue) -> this.scoreboard.setText(createScoreboardString()));
        game.gameStateProperty().addListener((observable, oldValue, newValue) -> handleGameEnding(newValue));
        game.invalidWordProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                infoLabel.setVisible(true);
                infoLabel.setText(messages.getString("finishTurnError"));
            } else {
                infoLabel.setVisible(false);
            }
        });
        game.getCurrentPlayerIndex().addListener((observable, oldValue, newValue) -> {
            setText();
            changeButtonsUsability();
            updateUI();
        });

        gridPane.setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
    }


    private String createScoreboardString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Scoreboard: ");
        int counter = 1;
        for (Player player : game.getPlayers()) {
            if (counter == game.getPlayers().size())
                stringBuilder.append(player.getPlayerName()).append(": ").append(player.getPoints().get());
            else if (counter < game.getPlayers().size()) {
                stringBuilder.append(player.getPlayerName()).append(": ").append(player.getPoints().get()).append(" | ");
            }
            counter++;
        }
        return stringBuilder.toString();
    }

    private void restoreGameField() {
        int x = GridPane.getColumnIndex(source);
        int y = GridPane.getRowIndex(source);
        StackPane stackPane = createStackPane(18, 15, 150, 150);
        stackPane.getStyleClass().add("field");
        setEffectToPane(stackPane, x, y);
        addDropListener(stackPane);
        gridPane.add(stackPane, x, y);
    }

    @FXML
    void exchangeStones() {
        game.playerSwapHand();
        drawUIFromSource();
    }

    @FXML
    void finishTurn() {
        Board currentBoard = new Board();
        gridPane.getChildren().forEach(node -> {
            if (node instanceof Pane) {
                Pane pane = (Pane) node;
                int x = GridPane.getColumnIndex(pane);
                int y = GridPane.getRowIndex(pane);
                if (pane.getStyleClass().contains("tile")) {
                    char letter = ((Label) pane.getChildren().get(0)).getText().charAt(0);
                    int value = Integer.parseInt(((Label) pane.getChildren().get(1)).getText());
                    currentBoard.setTile(new Tile(letter, value), y, x);
                }
            }
        });

        if(!game.playerFinishTurn(currentBoard)) {
            game.invalidWordProperty().set(true);
        } else {
            game.invalidWordProperty().set(false);
        }
    }

    private void handleGameEnding(Game.GAME_STATE newValue) {
        String closeMessage;
        switch (newValue) {
            case GAME_OVER -> closeMessage = buildEndingMessage();
            case GAME_ABORTED -> closeMessage = buildCloseMessage();
            default -> throw new IllegalArgumentException("Invalid game state");
        }
        closeGameWindow();
        closeHelpWindow();
        openEndWindow(closeMessage);
    }


    private String buildCloseMessage() {
        StringBuilder message = new StringBuilder();
        message.append("Game aborted");
        return message.toString();
    }

    private String buildEndingMessage() {
        if (hasWinner()) {
            return createScoreboardString();
        } else {
            StringBuilder message = new StringBuilder();
            message.append("No winner it's a draw").
                    append(System.lineSeparator()).
                    append(createScoreboardString());
            return message.toString();
        }
    }

    private void openEndWindow(String closeMessage) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle(messages.getString("gameOver"));
        Label label = new Label(closeMessage);
        Button closeButton = new Button(messages.getString("close"));
        closeButton.setOnAction(event -> popupStage.close());
        VBox vbox = new VBox(10, label, closeButton);
        vbox.setStyle("-fx-padding: 10px");
        Scene popupScene = new Scene(vbox, 300, 100);
        popupStage.setScene(popupScene);
        popupStage.show();
    }

    private boolean hasWinner() {
        return Objects.nonNull(game.getWinner());
    }

    @FXML
    void quitGame() {
        game.handleEndingForCloseWindow();
    }

    @FXML
    void showHelp(ActionEvent event) {
        if (isHelpWindowOpen.get()) {
            helpStage.requestFocus();
        } else {
            openHelpWindow();
            isHelpWindowOpen.set(true);
        }

    }

    private void openHelpWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/HelpView.fxml"));
            this.helpStage = new Stage();
            Pane rootPane = loader.load();
            HelpViewController helpViewController = loader.getController();
            helpViewController.setUp(messages, helpStage, this);
            Scene scene = new Scene(rootPane);
            helpStage.setScene(scene);
            helpStage.setMinWidth(420);
            helpStage.setMinHeight(250);
            helpStage.setTitle("Papi's Scrabble");
            helpStage.show();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while opening help window", e);
        }
    }

    /**
     * Closes the help window if it is open and sets the isHelpWindowOpen flag to false.
     * If the help window is not open, nothing happens.
     */
    public void closeHelpWindow() {
        if (isHelpWindowOpen.get()) {
            helpStage.close();
            isHelpWindowOpen.set(false);
        }
    }

    @FXML
    void cancelSetting() {
        resetBoard();
    }

    private void resetBoard() {
        drawUIFromSource();
    }

    private void closeGameWindow() {
        stage.close();
    }

}
