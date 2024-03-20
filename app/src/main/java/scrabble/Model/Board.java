package scrabble.Model;


import scrabble.Log.Logging;
import scrabble.config.Config;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The board class is a simple data class for representing a scrabble board.
 * It should contain a 15x15 2 dimensional array for holding Field objects
 * There should be methods for adding and removing Field objects to the array
 */
public class Board {

    private final Field[][] board;
    private static Logger logger = Logger.getLogger(Logging.class.getCanonicalName());


    /**
     * Constructor for the Board. Creates a 15x15 grid of empty tiles.
     */
    public Board() {
        board = new Field[Config.BOARD_ROW_SIZE][Config.BOARD_COL_SIZE];
        populateBoard();
    }

    private void populateBoard() {
        for (int y = 0; y < Config.BOARD_ROW_SIZE; y++) {
            for (int x = 0; x < Config.BOARD_COL_SIZE; x++) {
                if (isDoubleWord(x, y)) {
                    board[y][x] = new Field(Config.EFFECT.DOUBLE_WORD);
                } else if (isTripleWord(x, y)) {
                    board[y][x] = new Field(Config.EFFECT.TRIPLE_WORD);
                } else if (isDoubleLetter(x, y)) {
                    board[y][x] = new Field(Config.EFFECT.DOUBLE_LETTER);
                } else if (isTripleLetter(x, y)) {
                    board[y][x] = new Field(Config.EFFECT.TRIPLE_LETTER);
                } else {
                    board[y][x] = new Field(Config.EFFECT.NO_EFFECT);
                }
            }
        }
    }

    private boolean isTripleLetter(int x, int y) {
        return (x == 1 || x == 13) && (y == 5 || y == 9) ||
                (x == 5 || x == 9) && (y == 1 || y == 5 || y == 9 || y == 13);
    }

    private boolean isDoubleLetter(int x, int y) {
        return (x == 0 || x == 14) && (y == 3 || y == 11) ||
                (x == 2 || x == 12) && (y == 6 || y == 8) ||
                (x == 3 || x == 11) && (y == 0 || y == 7 || y == 14) ||
                (x == 6 || x == 8) && (y == 2 || y == 6 || y == 8 || y == 12) ||
                x == 7 && (y == 3 || y == 11);
    }

    private boolean isTripleWord(int x, int y) {
        return ((x == 0 || x == 14) && (y == 0 || y == 7 || y == 14) ||
                x == 7 && (y == 0 || y == 14));
    }

    private boolean isDoubleWord(int x, int y) {
        if ((x == 1 || x == 13) && (y == 1 || y == 13) ||
                (x == 2 || x == 12) && (y == 2 || y == 12) ||
                (x == 3 || x == 11) && (y == 3 || y == 11) ||
                (x == 4 || x == 10) && (y == 4 || y == 10)) {
            return true;
        }
        return x == 7 && y == 7;
    }

    /**
     * Sets the given tile at the specified row and column of the game board.
     *
     * @param tile the tile to be set
     * @param row  the row number where the tile is to be set
     * @param col  the column number where the tile is to be set
     * @throws ArrayIndexOutOfBoundsException if row or column number is out of bounds of the game board
     * @throws IllegalArgumentException       if the specified field is already occupied or the coordinates are invalid
     */
    public void setTile(Tile tile, int row, int col) {
        try {
            getField(row, col).setOccupant(tile);
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Gets the Field object at the specified row and column from the board.
     *
     * @param row the row to get the Field from
     * @param col the column to get the Field from
     * @return the Field object at the specified row and column, or null if no Field is present
     * @throws IllegalArgumentException if the specified field is already occupied or the coordinates are invalid
     */
    public Field getField(int row, int col) throws IllegalArgumentException {
        try {
            if (isCoordinateValid(row, col)){
                return board[row][col];
            }else{
                throw new IllegalArgumentException("Invalid Coordinates: (" + row + ", " + col + ")");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Invalid Coordinates: (" + row + ", " + col + ")", e);
            throw e;
        }
    }

    /**
     * Gets the entire board.
     *
     * @return the board
     */
    public Field[][] getBoard() {
        return board;
    }

    private boolean isCoordinateValid(int row, int col) {
        return row >= 0 && row < board.length && col >= 0 && col < board[row].length;
    }

}
