package com.roachstudios.critterparade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

/**
 * Represents a player profile and input source. Tracks basic board resources
 * and exposes helper methods for reading input mapped by player ID.
 *
 * <p>Key mappings are intentionally hardcoded per ID to support same-keyboard
 * local play without a configuration UI. The layout spaces players across
 * the keyboard (WASD, TFGH, IJKL, bracket cluster, arrows, and numpad).</p>
 */
public class Player {
    
    /**
     * Input action types for player controls.
     */
    private enum InputAction {
        UP, DOWN, LEFT, RIGHT, ACTION
    }
    
    /**
     * Key mappings for each player (indexed by player ID - 1).
     * Each sub-array contains keys for [UP, DOWN, LEFT, RIGHT, ACTION].
     */
    private static final int[][] KEY_MAPPINGS = {
        // Player 1: WASD + E
        {Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D, Input.Keys.E},
        // Player 2: TFGH + Y
        {Input.Keys.T, Input.Keys.G, Input.Keys.F, Input.Keys.H, Input.Keys.Y},
        // Player 3: IJKL + O
        {Input.Keys.I, Input.Keys.K, Input.Keys.J, Input.Keys.L, Input.Keys.O},
        // Player 4: Bracket cluster
        {Input.Keys.LEFT_BRACKET, Input.Keys.APOSTROPHE, Input.Keys.SEMICOLON, Input.Keys.ENTER, Input.Keys.RIGHT_BRACKET},
        // Player 5: Arrow keys + Space
        {Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.SPACE},
        // Player 6: Numpad
        {Input.Keys.NUMPAD_8, Input.Keys.NUMPAD_5, Input.Keys.NUMPAD_4, Input.Keys.NUMPAD_6, Input.Keys.NUMPAD_9}
    };
    
    private int fruit;
    private int crumbs;
    private int numMGWins; 
    
    private final int playerID;
    private final String name;
    private final Texture playerTex;
    private final Sprite playerSprite;
    private final Rectangle bounds;
    
    /** Key codes for this player's controls, indexed by InputAction ordinal. */
    private final int[] keyBindings;
    
    // Board game state
    private int boardTileIndex = 0;
    private int previousTileIndex = -1;

    /**
     * Constructs a new player with the given ID, name, and sprite texture.
     *
     * @param id unique player ID in [1..6] used for input mapping
     * @param name display name for the critter (e.g., "Bumble Bee")
     * @param tex sprite texture representing the player
     * @throws IllegalArgumentException if id is not in range [1..6]
     */
    public Player(int id, String name, Texture tex) {
        if (id < 1 || id > 6) {
            throw new IllegalArgumentException("Player ID must be between 1 and 6, got: " + id);
        }
        
        this.fruit = 0;
        this.crumbs = 0;
        this.playerID = id;
        this.name = name;
        this.numMGWins = 0;
        
        // Copy key bindings for this player
        this.keyBindings = KEY_MAPPINGS[id - 1].clone();

        this.playerTex = tex;
        this.playerSprite = new Sprite(playerTex);
        this.playerSprite.setSize(1, 1);
        this.bounds = new Rectangle(this.playerSprite.getX(), this.playerSprite.getY(), 1, 1);
    }
    
    // =========================================================================
    // Score Management
    // =========================================================================
    
    /**
     * Increments fruit count by one.
     */
    public void addFruit() {
        this.fruit++;
    }
    
    /**
     * Decrements fruit count and clamps at zero.
     */
    public void subFruit() {
        this.fruit--;
        if (this.fruit < 0) {
            this.fruit = 0;
        }
    }
    
    /**
     * Gets the current fruit count.
     *
     * @return current fruit count
     */
    public int getFruit() {
        return this.fruit;
    }
    
    /**
     * Adds crumbs to the player's total.
     *
     * @param numToAdd number of crumbs to add
     */
    public void addCrumbs(int numToAdd) {
        this.crumbs += numToAdd;
    }
    
    /**
     * Subtracts crumbs and clamps at zero.
     *
     * @param numToSub number of crumbs to subtract
     */
    public void subCrumbs(int numToSub) {
        this.crumbs -= numToSub;
        if (this.crumbs < 0) {
            this.crumbs = 0;
        }
    }
    
    /**
     * Gets the current crumb total.
     *
     * @return current crumb total
     */
    public int getCrumbs() {
        return this.crumbs;
    }
    
    /**
     * Increments mini-game win counter.
     */
    public void addWin() {
        this.numMGWins++;
    }
    
    /**
     * Gets the number of mini-game wins.
     *
     * @return number of mini-game wins
     */
    public int getWins() {
        return this.numMGWins;
    }
    
    /**
     * Resets all score-related fields (fruit, crumbs, wins) to zero.
     * Useful when starting a new game session.
     */
    public void resetScores() {
        this.fruit = 0;
        this.crumbs = 0;
        this.numMGWins = 0;
    }
    
    // =========================================================================
    // Board Position State
    // =========================================================================
    
    /**
     * Gets the index of the tile this player is currently on.
     *
     * @return the index of the tile this player is currently on
     */
    public int getBoardTileIndex() {
        return boardTileIndex;
    }
    
    /**
     * Sets the player's current tile index on the board.
     *
     * @param index the tile index
     */
    public void setBoardTileIndex(int index) {
        this.boardTileIndex = index;
    }
    
    /**
     * Gets the index of the tile the player came from.
     *
     * @return the index of the tile the player came from (-1 if starting)
     */
    public int getPreviousTileIndex() {
        return previousTileIndex;
    }
    
    /**
     * Sets the previous tile index (for junction direction tracking).
     *
     * @param index the previous tile index
     */
    public void setPreviousTileIndex(int index) {
        this.previousTileIndex = index;
    }
    
    /**
     * Resets board position to starting state.
     *
     * @param startTileIndex the starting tile index
     */
    public void resetBoardPosition(int startTileIndex) {
        this.boardTileIndex = startTileIndex;
        this.previousTileIndex = -1;
    }
    
    // =========================================================================
    // Identity and Sprite Access
    // =========================================================================
    
    /**
     * Gets the player's ID used for input mapping.
     *
     * @return the player's ID used for input mapping
     */
    public int getID() {
        return this.playerID;
    }
    
    /**
     * Gets the critter's display name.
     *
     * @return the critter's display name
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Gets the player's sprite.
     *
     * @return the player's sprite
     */
    public Sprite getSprite() {
        return this.playerSprite;
    }
    
    /**
     * Sets sprite size uniformly in world units.
     *
     * @param size the size in world units for both width and height
     */
    public void setSpriteSize(float size) {
        this.playerSprite.setSize(size, size);
    }
    
    /**
     * Gets the bounds rectangle for collision detection.
     *
     * @return the bounds rectangle
     */
    public Rectangle getBounds() {
        return this.bounds;
    }
    
    // =========================================================================
    // Input Handling
    // =========================================================================
    
    /**
     * Checks if a specific key for this player is currently pressed.
     *
     * @param action the input action to check
     * @return true if the mapped key is held
     */
    private boolean isKeyPressed(InputAction action) {
        return Gdx.input.isKeyPressed(keyBindings[action.ordinal()]);
    }
    
    /**
     * Checks if a specific key for this player was just pressed this frame.
     *
     * @param action the input action to check
     * @return true if the mapped key was just pressed
     */
    private boolean isKeyJustPressed(InputAction action) {
        return Gdx.input.isKeyJustPressed(keyBindings[action.ordinal()]);
    }
    
    /**
     * Checks if the player is pressing up.
     *
     * @return true while the mapped "Up" key is held for this player
     */
    public boolean isPressingUp() {
        return isKeyPressed(InputAction.UP);
    }
    
    /**
     * Checks if the player just pressed up this frame.
     *
     * @return true on the frame the mapped "Up" key is pressed
     */
    public boolean justPressedUp() {
        return isKeyJustPressed(InputAction.UP);
    }
    
    /**
     * Checks if the player is pressing left.
     *
     * @return true while the mapped "Left" key is held for this player
     */
    public boolean isPressingLeft() {
        return isKeyPressed(InputAction.LEFT);
    }
    
    /**
     * Checks if the player just pressed left this frame.
     *
     * @return true on the frame the mapped "Left" key is pressed
     */
    public boolean justPressedLeft() {
        return isKeyJustPressed(InputAction.LEFT);
    }
    
    /**
     * Checks if the player is pressing down.
     *
     * @return true while the mapped "Down" key is held for this player
     */
    public boolean isPressingDown() {
        return isKeyPressed(InputAction.DOWN);
    }
    
    /**
     * Checks if the player just pressed down this frame.
     *
     * @return true on the frame the mapped "Down" key is pressed
     */
    public boolean justPressedDown() {
        return isKeyJustPressed(InputAction.DOWN);
    }
    
    /**
     * Checks if the player is pressing right.
     *
     * @return true while the mapped "Right" key is held for this player
     */
    public boolean isPressingRight() {
        return isKeyPressed(InputAction.RIGHT);
    }
    
    /**
     * Checks if the player just pressed right this frame.
     *
     * @return true on the frame the mapped "Right" key is pressed
     */
    public boolean justPressedRight() {
        return isKeyJustPressed(InputAction.RIGHT);
    }
    
    /**
     * Checks if the player is pressing action.
     *
     * @return true while the mapped "Action" key is held for this player
     */
    public boolean isPressingAction() {
        return isKeyPressed(InputAction.ACTION);
    }
    
    /**
     * Checks if the player just pressed action this frame.
     *
     * @return true on the frame the mapped "Action" key is pressed
     */
    public boolean justPressedAction() {
        return isKeyJustPressed(InputAction.ACTION);
    }
}
