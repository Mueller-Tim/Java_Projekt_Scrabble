package scrabble.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import scrabble.Log.Logging;
import scrabble.config.Config;

/**
 * This class represents a Scrabble player, and is responsible for storing the player's name, tile list, and score.
 */
public class Player {

    private static final Logger logger = Logger.getLogger(Logging.class.getCanonicalName());
    /**
     * The player's name. Must be a string of 4 to 10 characters, consisting of only letters, digits, and underscores.
     */

    private final String playerName;
    /**
     * The list of tiles in the player's hand.
     */
    private final List<Tile> tileList;

    /**
     * The player's score in points.
     */
    private final IntegerProperty points;

    /**
     * Creates a new player with the given name and an empty hand.
     *
     * @param playerName the name of the player, which must match the regular expression
     * @throws IllegalArgumentException if the player name does not match the regular expression
     */
    public Player(String playerName) {
        if (playerName.matches(Config.REGEX_USERNAME)) {
            this.playerName = playerName;
            points = new SimpleIntegerProperty(0);
            tileList = new ArrayList<>();
        } else {
            throw new IllegalArgumentException("Username has an invalid format. The username must be between 4 and 15 characters");
        }
    }

    /**
     * Returns the name of the player.
     *
     * @return the player's name
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Returns the list of tiles in the player's hand.
     *
     * @return the list of tiles in the player's hand
     */
    public List<Tile> getTileList() {
        return tileList;
    }

    /**
     * Returns the player's score in points.
     *
     * @return the player's score in points
     */
    public IntegerProperty getPoints() {
        return points;
    }

    /**
     * Adds points to the player's score
     *
     * @param points the value to be added to the player's score
     */
    public void addPoints(int points) {
        this.points.set(getPoints().get()+points);
    }

    /**
     * Removes the given tile from the player's hand.
     *
     * @param  tile to remove from the player's hand
     * @throws IllegalArgumentException if the player does not have the given tile in their hand
     */
    public void dropTileFromTileList(Tile tile) {
        try {
            if (!tileList.remove(tile)) {
                throw new IllegalArgumentException("User does not contain the tile in the tile list");
            }else {
                logger.log(Level.FINE, "Player {0} dropped tile {1} from their tile list.", new Object[]{playerName, tile});
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Player cannot drop tile from tile list.", e);
            throw e;
        }
    }

    /**
     * Adds the given tile to the player's hand.
     *
     * @param tile to add to the player's hand
     */
    public void addTileToTileList(Tile tile) {
        logger.log(Level.FINE, "Player {0} added tile {1} to their tile list.", new Object[]{playerName, tile});
        tileList.add(tile);
    }

    /**
     * Removes all tiles from the player's hand.
     */
    public void clearTileList() {
        logger.log(Level.INFO, "Player {0} cleared their tile list.", playerName);
        tileList.clear();
    }

    @Override
    public String toString() {
        return " [playerName=" + playerName + ", tileList=" + tileList + ", points=" + points + "]";
    }
}
