package com.roachstudios.critterparade.gameboards;

/**
 * Immutable data object describing a tile on a {@link GameBoard}.
 */
public class BoardTile {
    private final int posX;
    private final int posY;
    private final Type type;

    /**
     * @param posX logical grid X-position
     * @param posY logical grid Y-position
     * @param type semantic type used to drive gameplay behavior
     */
    public BoardTile(int posX, int posY, Type type) {
        this.posX = posX;
        this.posY = posY;
        this.type = type;
    }

    /**
     * @return grid X-position
     */
    public int getPosX() {
        return posX;
    }

    /**
     * @return grid Y-position
     */
    public int getPosY() {
        return posY;
    }

    /**
     * @return the tile type
     */
    public Type getType() {
        return type;
    }

    /**
     * Types of board tiles. Colors in comments correspond to UI styling.
     */
    public enum Type {
        SHOP, // green
        MINIGAME, // red
        REGULAR; // blue
    }
}
