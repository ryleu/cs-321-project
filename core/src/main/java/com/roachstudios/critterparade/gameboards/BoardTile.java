package com.roachstudios.critterparade.gameboards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a tile on a {@link GameBoard} with position, type, and connections
 * to neighboring tiles. Tiles form a graph that players traverse during board mode.
 */
public class BoardTile {
    private final int id;
    private final float posX;
    private final float posY;
    private final Type type;
    private final List<BoardTile> neighbors;
    
    /**
     * Tile types determine gameplay effects when a player lands on them.
     */
    public enum Type {
        /** Awards crumbs to the player */
        GREEN,
        /** No effect */
        BLUE,
        /** Triggers a minigame */
        RED
    }

    /**
     * Constructs a new board tile with the specified properties.
     *
     * @param id unique tile identifier
     * @param posX screen X-position (percentage of screen width, 0-1)
     * @param posY screen Y-position (percentage of screen height, 0-1)
     * @param type determines gameplay behavior when landed on
     */
    public BoardTile(int id, float posX, float posY, Type type) {
        this.id = id;
        this.posX = posX;
        this.posY = posY;
        this.type = type;
        this.neighbors = new ArrayList<>();
    }

    /**
     * Gets the unique tile identifier.
     *
     * @return unique tile identifier
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the X position as percentage of screen width.
     *
     * @return X position as percentage of screen width (0-1)
     */
    public float getPosX() {
        return posX;
    }

    /**
     * Gets the Y position as percentage of screen height.
     *
     * @return Y position as percentage of screen height (0-1)
     */
    public float getPosY() {
        return posY;
    }

    /**
     * Gets the tile type.
     *
     * @return the tile type
     */
    public Type getType() {
        return type;
    }
    
    /**
     * Adds a neighbor tile (bidirectional connection).
     * @param neighbor tile to connect to
     */
    public void addNeighbor(BoardTile neighbor) {
        if (!neighbors.contains(neighbor)) {
            neighbors.add(neighbor);
        }
    }
    
    /**
     * Gets the neighboring tiles connected to this tile.
     *
     * @return unmodifiable list of connected tiles
     */
    public List<BoardTile> getNeighbors() {
        return Collections.unmodifiableList(neighbors);
    }
    
    /**
     * Gets the next tiles available to move to, excluding the tile the player came from.
     * @param cameFrom the tile the player moved from (null if starting)
     * @return list of possible next tiles
     */
    public List<BoardTile> getNextTiles(BoardTile cameFrom) {
        if (cameFrom == null || neighbors.size() <= 1) {
            return Collections.unmodifiableList(neighbors);
        }
        
        List<BoardTile> options = new ArrayList<>();
        for (BoardTile neighbor : neighbors) {
            if (neighbor != cameFrom) {
                options.add(neighbor);
            }
        }
        return options;
    }
    
    /**
     * Checks if this tile is a junction (crossroads with multiple paths).
     *
     * @return true if this tile has more than 2 neighbors (a junction/crossroads)
     */
    public boolean isJunction() {
        return neighbors.size() > 2;
    }
    
    /**
     * Checks if this tile is a dead end.
     *
     * @return true if this tile is a dead end (only 1 neighbor)
     */
    public boolean isDeadEnd() {
        return neighbors.size() == 1;
    }
}
