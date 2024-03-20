package scrabble.Model;

import scrabble.config.Config;

/**
 * This class represents a single Field on a scrabble board and is responsible for storing a possible occupant as well as the effect to be applied to its occupant
 */
public class Field {
    private Tile occupant;
    private final Config.EFFECT effect;

    /**
     * Constructor for the field class
     *
     * @param effect the effect to be applied to the field's occupant
     */
    public Field(Config.EFFECT effect) {
        this.effect = effect;
    }

    public Tile getOccupant() {
        return occupant;
    }

    /**
     * Setter method for the field's occupant
     *
     * @param occupant the tile to be placed on the field
     * @throws IllegalArgumentException if the field is already occupied
     */
    public void setOccupant(Tile occupant) throws IllegalArgumentException {
        if (this.occupant == null){
            this.occupant = occupant;
        }else{
            throw new IllegalArgumentException("Field is already occupied");
        }

    }

    public Config.EFFECT getEffect() {
        return effect;
    }

    /**
     * checks whether the field as an occupant or not
     * @return Boolean
     */
    public boolean isOccupied() {
        return occupant != null;
    }

    @Override
    public String toString() {
        return "Field{" +
                "occupant=" + occupant +
                ", effect=" + effect +
                '}';
    }
}