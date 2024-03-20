package scrabble.config;


import scrabble.Model.Tile;
import java.io.File;
import java.util.*;

/**
 * This class contains configuration values for the application.
 */
public class Config {
    /**
     * A regular expression that defines the valid format for a player's username.
     */
    public static final String REGEX_USERNAME = "^[A-Za-z][A-Za-z0-9_]{2,13}$";

    public static final File enWordList = new File("./src/main/resources/wordlists/wordlist-en.csv");
    public static final File deWordList = new File("./src/main/resources/wordlists/wordlist-de.txt");

    /**
     * This enum stores the different languages which are supported
     */
    public enum LANGUAGE {
        DE("Deutsch"),
        EN("English");

        private final String language;
        LANGUAGE(String language) {
            this.language = language;
        }

        @Override
        public String toString() {
            return language;
        }
    }

    public enum Direction {
        RIGHT,
        DOWN;
    }

    /**
     * The size of the board row and column
     */
    public static final int BOARD_ROW_SIZE = 15;
    public static final int BOARD_COL_SIZE = 15;

    /**
     * The amount of tiles a player can have in his hand
     */
    public static final int INITIAL_TILE_COUNT = 7;

    /**
     * This map contains all tiles and amount of each tile for the language german
     */
    public static final Map<Tile, Integer> DE_TILES = new HashMap<Tile, Integer>() {
        {
            put(new Tile(' ', 0), 2);
            put(new Tile('A', 1), 5);
            put(new Tile('B', 3), 2);
            put(new Tile('C', 4), 2);
            put(new Tile('D', 1), 4);
            put(new Tile('E', 1), 15);
            put(new Tile('F', 4), 2);
            put(new Tile('G', 2), 3);
            put(new Tile('H', 2), 4);
            put(new Tile('I', 1), 6);
            put(new Tile('J', 6), 1);
            put(new Tile('K', 4), 2);
            put(new Tile('L', 2), 3);
            put(new Tile('M', 3), 4);
            put(new Tile('N', 1), 9);
            put(new Tile('O', 2), 3);
            put(new Tile('P', 4), 1);
            put(new Tile('Q', 10), 1);
            put(new Tile('R', 1), 6);
            put(new Tile('S', 1), 7);
            put(new Tile('T', 1), 6);
            put(new Tile('U', 1), 6);
            put(new Tile('V', 6), 1);
            put(new Tile('W', 3), 1);
            put(new Tile('X', 8), 1);
            put(new Tile('Y', 10), 1);
            put(new Tile('Z', 3), 1);
            put(new Tile('Ä', 6), 1);
            put(new Tile('Ü', 6), 1);
            put(new Tile('Ö', 8), 1);
        }
    };

    /**
     * This map contains all tiles and amount of each tile for the language english
     */
    public static final Map<Tile, Integer> EN_TILES = new HashMap<Tile, Integer>() {
        {
            put(new Tile(' ', 0), 2);
            put(new Tile('A', 1), 9);
            put(new Tile('B', 3), 2);
            put(new Tile('C', 3), 2);
            put(new Tile('D', 2), 4);
            put(new Tile('E', 1), 12);
            put(new Tile('F', 4), 2);
            put(new Tile('G', 2), 3);
            put(new Tile('H', 4), 2);
            put(new Tile('I', 1), 9);
            put(new Tile('J', 8), 1);
            put(new Tile('K', 5), 1);
            put(new Tile('L', 1), 4);
            put(new Tile('M', 3), 2);
            put(new Tile('N', 1), 6);
            put(new Tile('O', 1), 8);
            put(new Tile('P', 3), 2);
            put(new Tile('Q', 10), 1);
            put(new Tile('R', 1), 6);
            put(new Tile('S', 1), 4);
            put(new Tile('T', 1), 6);
            put(new Tile('U', 1), 4);
            put(new Tile('V', 4), 2);
            put(new Tile('W', 4), 2);
            put(new Tile('X', 8), 1);
            put(new Tile('Y', 4), 2);
            put(new Tile('Z', 10), 1);
        }
    };

    /**
     * This enum stores the different effects which can be applied to a scrabble field
     */
    public enum EFFECT {
        NO_EFFECT,
        DOUBLE_LETTER,
        DOUBLE_WORD,
        TRIPLE_LETTER,
        TRIPLE_WORD;
    }
}
