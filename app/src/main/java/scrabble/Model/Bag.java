package scrabble.Model;

import scrabble.config.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * The bag class represents the bag of remaining stones which are available for distribution
 * The list of available tiles should be adapted to the selected language and is set on bag creation
 */
public class Bag {
    private final List<Tile> tiles;
    private final Random random = new Random();

    /**
     * Constructs a Bag object for a specified language.
     * @param language the language of the tile set to use
     */
    public Bag(Config.LANGUAGE language) {
        tiles = loadTilesByLanguage(language);
    }

    private List<Tile> loadTilesByLanguage(Config.LANGUAGE language) {
        switch (language) {
            case DE -> {
                return populateTileList(Config.DE_TILES);
            }
            case EN -> {
                return populateTileList(Config.EN_TILES);
            }
            default -> throw new IllegalArgumentException("No tile set for language " + language + " found");
        }
    }

    private List<Tile> populateTileList(Map<Tile, Integer> tileDistributionMap) {
        List<Tile> tiles = new ArrayList<>();
        for (Map.Entry<Tile, Integer> entry : tileDistributionMap.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                tiles.add(entry.getKey());
            }
        }
        return tiles;
    }

    public void addTile(Tile tile) {
        tiles.add(tile);
    }

    /**
     * This method randomly returns a specified amount of tiles from the bag
     * if the bag contains fewer tiles than are asked, all remaining tiles are returned
     * @param amount
     * @return list of tiles
     */
    public List<Tile> getNTiles(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount has to be greater than 0");
        }
        List<Tile> returnList = new ArrayList<>();
        amount = amount < tiles.size() ? amount : tiles.size();
        for (int i = 0; i < amount; i++) {
            int index = random.nextInt(tiles.size());
            returnList.add(tiles.get(index));
            tiles.remove(index);
        }
        return returnList;
    }

    public int getRemainingTiles() {
        return tiles.size();
    }
}
