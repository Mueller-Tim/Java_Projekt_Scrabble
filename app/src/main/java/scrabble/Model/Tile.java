package scrabble.Model;

/**
 * This class represents a tile. A tile has the properties {@link #letter} and {@link #value}.
 * The properties are specified when a token is created and cannot be changed afterwards.
 */
public record Tile(char letter, int value) {

	/**
	 * Compares two tile objects if they are equal
	 * @param tile
	 * @return true if it's equal, false if it's not equal
	 */
	public boolean equals(Tile tile) {
		return tile.letter == this.letter && tile.value == this.value;
	}
}
