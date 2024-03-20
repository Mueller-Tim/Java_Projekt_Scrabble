package scrabble;


import javafx.beans.property.*;
import scrabble.config.Config;

import java.io.IOException;
import java.util.*;

import scrabble.Model.Player;
import scrabble.Model.Bag;
import scrabble.Model.Board;
import scrabble.Model.Field;
import scrabble.Model.Tile;
import scrabble.Log.Logging;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static scrabble.Game.GAME_STATE.GAME_ABORTED;
import static scrabble.Game.GAME_STATE.GAME_OVER;
import static scrabble.config.Config.Direction.*;

/**
 * The scrabble.Game class is responsible for handling the flow of a game of scrabble
 */
public class Game {

    private static final Logger logger = java.util.logging.Logger.getLogger(Logging.class.getCanonicalName());
    private Bag bag;
    private Board existingBoard;
    private List<Player> players;
    private final IntegerProperty currentPlayerIndex;
    private int skippedTurns;
    private boolean middleFieldIsUsed;
    private BooleanProperty invalidWord;
    private Config.LANGUAGE language;
    private WordValidator wordValidator;
    private ObjectProperty<GAME_STATE> gameState;


    /**
     * This enum represents the possible states of a game
     */
    public enum GAME_STATE {
        GAME_OVER, GAME_RUNNING, GAME_ABORTED
    }

    /**
     * Constructor for a new Game object. Initializes the Board and sets up the initial
     * currentPlayerIndex and gameOverByClosing properties.
     */
    public Game() {
        invalidWord = new SimpleBooleanProperty(false);
        gameState = new SimpleObjectProperty<>(GAME_STATE.GAME_RUNNING);
        currentPlayerIndex = new SimpleIntegerProperty(0);
        skippedTurns = 0;
        existingBoard = new Board();
        logger.log(Level.INFO, "Game Object created.");
    }

    /**
     * Sets up a new game by initializing the Bag and distributing the initial tiles
     * to the players.
     *
     * @param language The language for the game
     * @param players  The list of players in the game
     */
    public void setUpGame(Config.LANGUAGE language, List<Player> players) {
        if (players.size() < 2 || players.size() > 4) throw new IllegalArgumentException("Invalid number of players");
        this.players = players;
        this.language = language;
        setWordValidator();
        bag = new Bag(language);
        distributeInitialTiles();
        logger.log(Level.FINE, "Game is set up -> {0}", this.toString());
    }

    public Board getBoard() {
        return existingBoard;
    }

    private void setWordValidator() {
        try {
            switch (language) {
                case EN -> wordValidator = new WordValidator(Config.enWordList, Config.EN_TILES);
                case DE -> wordValidator = new WordValidator(Config.deWordList, Config.DE_TILES);
                default -> throw new
                        IllegalArgumentException(String.format("No wordlist available for the language %s", language));
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "The wordlist could not be set");
            throw new RuntimeException(e);
        }
        logger.log(Level.INFO, "The wordlist for the language \"{0}\" was set successfully", language);
    }


    /**
     * Returns the current player whose turn it is.
     *
     * @return The current player object
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex.get());
    }

    public IntegerProperty getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    private void switchToNextPlayer() {
        currentPlayerIndex.set(currentPlayerIndex.get() < players.size() - 1 ? currentPlayerIndex.get() + 1 : 0);
        logger.log(Level.INFO, "Switched to next player: {0}, number of skipped turns: {1}", new Object[]{getCurrentPlayer().getPlayerName(), skippedTurns});
    }

    /**
     * Method to finish the turn of the current player and update the game board and player score accordingly.
     *
     * @param newBoard the current game board
     * @return true if the turn was successfully finished, false otherwise
     */
    public boolean playerFinishTurn(Board newBoard) {
        logger.log(Level.INFO, "Entering playerFinishTurn method.");
        invalidWord.set(false);
        List<Field> resultFields;
        String word;
        int beginningRow = 0;
        int beginningColumn = 0;
        boolean foundBeginning = false;
        Map<String, List<Field>> newFields = new HashMap<>();

        for (int i = 0; i < newBoard.getBoard().length; i++) {
            for (int j = 0; j < newBoard.getBoard()[i].length; j++) {
                if (newBoard.getField(i, j).isOccupied() && !existingBoard.getField(i, j).isOccupied()) {
                    logger.log(Level.INFO, "Found new {2} on: x={1}, y={0}", new Object[]{i, j, newBoard.getField(i, j).getOccupant()});
                    if (!foundBeginning) {
                        beginningRow = i;
                        beginningColumn = j;
                        foundBeginning = true;
                        if (i == 7 && j == 7) {
                            middleFieldIsUsed = true;
                        }
                        logger.log(Level.INFO, "Found beginning of word on: x={1}, y={0}", new Object[]{i, j});
                    }
                }
            }
        }
        if (!middleFieldIsUsed) {
            logger.log(Level.INFO, "Middle field is not used.");
            return false;
        }
        resultFields = getFieldsInWord(newBoard, beginningRow, beginningColumn);
        word = getWordFromFields(resultFields);
        if (word.isEmpty()) {
            logger.log(Level.INFO, "Turn was skipped.");
            playerSkipTurn();
            return true;
        }
        logger.log(Level.INFO, "Found new word: {0}", word);
        newFields.put(word, resultFields);

        if (validateWords(newFields.keySet()) && middleFieldIsUsed) {
            newFields.values().forEach(fields -> {
                givePlayerPoints(fields);
                fields.forEach(field -> {
                    if (getCurrentPlayer().getTileList().contains(field.getOccupant())) {
                        getCurrentPlayer().dropTileFromTileList(field.getOccupant());
                    }
                });
            });
            skippedTurns = 0;
            this.existingBoard = newBoard;
            bag.getNTiles(Config.INITIAL_TILE_COUNT - getCurrentPlayer().getTileList().size()).forEach(getCurrentPlayer()::addTileToTileList);
            logger.log(Level.INFO, "Turn finished successfully with word: {0}.", word);
            switchToNextPlayer();
            return true;
        }
        logger.log(Level.INFO, "Turn could not be finished. Invalid word(s) found.");
        return false;
    }

    private List<Field> getFieldsInWord(Board board, int i, int j) {
        List<Field> fields = new ArrayList<>();
        int row = i;
        int column = j;
        if ((row + 1 <= Config.BOARD_ROW_SIZE - 1 && board.getField(row + 1, column).isOccupied()) || (row - 1 >= 0 && board.getField(row - 1, column).isOccupied())) {
            row--;
            while (row >= 0 && board.getField(row, column).isOccupied()) {
                fields.add(board.getField(row, column));
                row--;
            }
            row = i;
            fields.add(board.getField(i, j));
            row++;
            while (row <= Config.BOARD_ROW_SIZE - 1 && board.getField(row, column).isOccupied()) {
                fields.add(board.getField(row, column));
                row++;
            }
        } else if ((column + 1 <= Config.BOARD_COL_SIZE - 1 && board.getField(row, column + 1).isOccupied()) || (column - 1 >= 0 && board.getField(row, column - 1).isOccupied())) {
            column--;
            while (column >= 0 && board.getField(row, column).isOccupied()) {
                fields.add(board.getField(row, column));
                column--;
            }
            column = j;
            fields.add(board.getField(i, j));
            column++;
            while (column <= Config.BOARD_COL_SIZE - 1 && board.getField(row, column).isOccupied()) {
                fields.add(board.getField(row, column));
                column++;
            }
        }
        return fields;
    }

    private String getWordFromFields(List<Field> fields) {
        StringBuilder word = new StringBuilder();
        for (Field field : fields) {
            word.append(field.getOccupant().letter());
        }
        return word.toString();
    }

    private boolean validateWords(Set<String> words) {
        for (String word : words) {
            if (!wordValidator.containsWord(word)) {
                return false;
            }
        }
        return true;
    }

    private String extractWord(List<Field> fields) {
        String word = "";
        for (Field field : fields) {
            if (!field.isOccupied()) throw new IllegalArgumentException("Fields don't contain a word");
            word += field.getOccupant().letter();
        }
        return word;
    }

    /**
     * Swaps the current player's hand with new tiles from the bag
     */
    public void playerSwapHand() {
        Player currentPlayer = getCurrentPlayer();
        List<Tile> playerTiles = new ArrayList<>(currentPlayer.getTileList());
        currentPlayer.clearTileList();
        playerTiles.forEach(bag::addTile);
        bag.getNTiles(Config.INITIAL_TILE_COUNT).forEach(currentPlayer::addTileToTileList);
        skippedTurns = 0;
        switchToNextPlayer();
    }

    private void playerSkipTurn() {
        skippedTurns++;
        if (gameOverBySkipping()) {
            gameState.setValue(GAME_OVER);
            logger.log(Level.INFO, "Game is over by skipping turns.");
        }
        switchToNextPlayer();
    }

    private boolean gameOverBySkipping() {
        return skippedTurns == 2 * players.size();
    }

    /**
     * Sets the gameOverByClosing flag to true to indicate the game was ended by closing the window
     */
    public void handleEndingForCloseWindow() {
        gameState.setValue(GAME_ABORTED);
        logger.log(Level.INFO, "Game is over by closing the window.");
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setLanguage(Config.LANGUAGE language) {
        this.language = language;
        logger.log(Level.INFO, "Language is set to \"{0}\".", language);
    }

    private void distributeInitialTiles() {
        for (Player player : players) {
            List<Tile> tiles = bag.getNTiles(Config.INITIAL_TILE_COUNT);
            for (Tile tile : tiles) {
                player.addTileToTileList(tile);
            }
        }
        logger.log(Level.INFO, "Initial tiles are distributed.");
    }

    private void givePlayerPoints(List<Field> fields) {
        String word = extractWord(fields);
        int points = 0;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            points += calculatePointsForChar(c, fields.get(i).getEffect());
        }
        players.get(currentPlayerIndex.get()).addPoints(calculatePointsForWord(points, fields));
    }

    private int calculatePointsForWord(int points, List<Field> fields) {
        for (Field field : fields) {
            if (field.getEffect().equals(Config.EFFECT.DOUBLE_WORD)) return 2 * points;
            if (field.getEffect().equals(Config.EFFECT.TRIPLE_WORD)) return 3 * points;
        }
        return points;
    }

    private int calculatePointsForChar(char c, Config.EFFECT effect) {
        switch (effect) {
            case DOUBLE_LETTER -> {
                return 2 * getPointsForChar(c);
            }
            case TRIPLE_LETTER -> {
                return 3 * getPointsForChar(c);
            }
            default -> {
                return getPointsForChar(c);
            }
        }
    }

    private int getPointsForChar(char c) {
        for (Map.Entry<Tile, Integer> entry : getPointReference().entrySet()) {
            if (entry.getKey().letter() == c) {
                logger.log(Level.FINE, "Points for char \"{0}\" are \"{1}\".", new Object[]{c, entry.getKey().value()});
                return entry.getKey().value();
            }
        }
        throw new IllegalArgumentException("Character is not in tile set");
    }

    private Map<Tile, Integer> getPointReference() {
        switch (language) {
            case DE -> {
                return Config.DE_TILES;
            }
            case EN -> {
                return Config.EN_TILES;
            }
            default -> throw new IllegalArgumentException("No tile set for language " + language + " found");
        }
    }

    /**
     * Returns the player with the highest number of points among all players.
     * If there are multiple players with the same highest number of points,
     * the method returns null to indicate a tie.
     *
     * @return the winning player or null if there is a tie
     */
    public Player getWinner() {
        int maxPoints = 0;
        Player winningPlayer = null;
        for (Player player : players) {
            if (maxPoints < player.getPoints().get()) {
                maxPoints = player.getPoints().get();
                winningPlayer = player;
            } else if (maxPoints == player.getPoints().get()) {
                winningPlayer = null;
            }
        }
        return winningPlayer;
    }

    public GAME_STATE getGameState() {
        return gameState.get();
    }

    public ObjectProperty<GAME_STATE> gameStateProperty() {
        return gameState;
    }

    public BooleanProperty invalidWordProperty() {
        return invalidWord;
    }

    @Override
    public String toString() {
        return "Game{" +
                "players=" + players.toString() +
                ", currentPlayerIndex=" + currentPlayerIndex +
                ", gameState=" + gameState +
                ", bag=" + bag +
                ", skippedTurns=" + skippedTurns +
                ", language=" + language +
                '}';
    }
}
