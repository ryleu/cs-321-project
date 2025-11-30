package com.roachstudios.critterparade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
/**
 * Represents a player profile and input source. Tracks basic board resources
 * and exposes helper methods for reading input mapped by {@code playerID}.
 *
 * <p>Key mappings are intentionally hardcoded per ID to support same-keyboard
 * local play without a configuration UI. The layout spaces players across
 * the keyboard (WASD, TFGH, IJKL, bracket cluster, arrows, and numpad).</p>
 */
public class Player {
    
    private int fruit;
    private int crumbs;
    private int numMGWins; 
    
    private int playerID;
    private String name;
    private Texture playerTex;
    private Sprite playerSprite;
    private Rectangle bounds;
    
    // Board game state
    private int boardTileIndex = 0;
    private int previousTileIndex = -1;
   

    
    /**
     * Constructs a new player with the given ID, name, and sprite texture.
     *
     * @param id unique player ID in [1..6] used for input mapping
     * @param name display name for the critter (e.g., "Bumble Bee")
     * @param tex sprite texture representing the player
     */
    public Player(int id, String name, Texture tex) {
        this.fruit = 0;
        this.crumbs = 0;
        this.playerID = id;
        this.name = name;
        this.numMGWins = 0;  

        this.playerTex = tex;
        this.playerSprite = new Sprite(playerTex);
        this.playerSprite.setSize(1, 1);
        this.bounds = new Rectangle(this.playerSprite.getX(), this.playerSprite.getY(), 1, 1);
        
        
        
    }
    
    /**
     * Increments fruit count by one.
     */
    public void addFruit(){
        this.fruit++;
    }
    
    /**
     * Decrements fruit count and clamps at zero.
     */
    public void subFruit(){
        this.fruit--;
        
        if(this.fruit < 0){
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
        
        if(this.crumbs < 0){
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
    public void addWin(){
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
     * @param index the previous tile index
     */
    public void setPreviousTileIndex(int index) {
        this.previousTileIndex = index;
    }
    
    /**
     * Resets board position to starting state.
     * @param startTileIndex the starting tile index
     */
    public void resetBoardPosition(int startTileIndex) {
        this.boardTileIndex = startTileIndex;
        this.previousTileIndex = -1;
    }
    
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
     * Checks if the player is pressing up.
     *
     * @return true while the mapped "Up" key is held for this player
     */
    public boolean isPressingUp() {
        switch(this.playerID){
            case 1:
                if(Gdx.input.isKeyPressed(Input.Keys.W)){
                   return true; 
                }
                break;
            case 2:
                if(Gdx.input.isKeyPressed(Input.Keys.T)){
                    return true;
                }
                break;
            case 3:
                if(Gdx.input.isKeyPressed(Input.Keys.I)){
                    return true;
                }
                break;
            case 4:
                if(Gdx.input.isKeyPressed(Input.Keys.LEFT_BRACKET)){
                    return true;
                }
                break;
            case 5:
                if(Gdx.input.isKeyPressed(Input.Keys.UP)){
                    return true;
                }
                break;
            case 6:
                if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_8)){
                    return true;
                }
                break;
            default:
                System.out.println("INPUT ERROR: THE ID FOR THIS CHARACTER IS SET WRONG!!!");
                
        }
        return false;
    }
    
    /**
     * Checks if the player just pressed up this frame.
     *
     * @return true on the frame the mapped "Up" key is pressed
     */
    public boolean justPressedUp() {
        switch(this.playerID){
            case 1:
                if(Gdx.input.isKeyJustPressed(Input.Keys.W)){
                   return true; 
                }
                break;
            case 2:
                if(Gdx.input.isKeyJustPressed(Input.Keys.T)){
                    return true;
                }
                break;
            case 3:
                if(Gdx.input.isKeyJustPressed(Input.Keys.I)){
                    return true;
                }
                break;
            case 4:
                if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT_BRACKET)){
                    return true;
                }
                break;
            case 5:
                if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
                    return true;
                }
                break;
            case 6:
                if(Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_8)){
                    return true;
                }
                break;
            default:
                System.out.println("INPUT ERROR: THE ID FOR THIS CHARACTER IS SET WRONG!!!");
                
        }
        return false;
    }
    
    /**
     * Checks if the player is pressing left.
     *
     * @return true while the mapped "Left" key is held for this player
     */
    public boolean isPressingLeft() {
        switch(this.playerID){
            case 1:
                if(Gdx.input.isKeyPressed(Input.Keys.A)){
                   return true; 
                }
                break;
            case 2:
                if(Gdx.input.isKeyPressed(Input.Keys.F)){
                    return true;
                }
                break;
            case 3:
                if(Gdx.input.isKeyPressed(Input.Keys.J)){
                    return true;
                }
                break;
            case 4:
                if(Gdx.input.isKeyPressed(Input.Keys.SEMICOLON)){
                    return true;
                }
                break;
            case 5:
                if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
                    return true;
                }
                break;
            case 6:
                if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_4)){
                    return true;
                }
                break;
            default:
                System.out.println("INPUT ERROR: THE ID FOR THIS CHARACTER IS SET WRONG!!!");
                
        }
        return false;
    }
    
    /**
     * Checks if the player just pressed left this frame.
     *
     * @return true on the frame the mapped "Left" key is pressed
     */
    public boolean justPressedLeft() {
        switch(this.playerID){
            case 1:
                if(Gdx.input.isKeyJustPressed(Input.Keys.A)){
                   return true; 
                }
                break;
            case 2:
                if(Gdx.input.isKeyJustPressed(Input.Keys.F)){
                    return true;
                }
                break;
            case 3:
                if(Gdx.input.isKeyJustPressed(Input.Keys.J)){
                    return true;
                }
                break;
            case 4:
                if(Gdx.input.isKeyJustPressed(Input.Keys.SEMICOLON)){
                    return true;
                }
                break;
            case 5:
                if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)){
                    return true;
                }
                break;
            case 6:
                if(Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_4)){
                    return true;
                }
                break;
            default:
                System.out.println("INPUT ERROR: THE ID FOR THIS CHARACTER IS SET WRONG!!!");
                
        }
        return false;
    }
     
    /**
     * Checks if the player is pressing down.
     *
     * @return true while the mapped "Down" key is held for this player
     */
    public boolean isPressingDown() {
        switch(this.playerID){
            case 1:
                if(Gdx.input.isKeyPressed(Input.Keys.S)){
                   return true; 
                }
                break;
            case 2:
                if(Gdx.input.isKeyPressed(Input.Keys.G)){
                    return true;
                }
                break;
            case 3:
                if(Gdx.input.isKeyPressed(Input.Keys.K)){
                    return true;
                }
                break;
            case 4:
                if(Gdx.input.isKeyPressed(Input.Keys.APOSTROPHE)){
                    return true;
                }
                break;
            case 5:
                if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
                    return true;
                }
                break;
            case 6:
                if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_5)){
                    return true;
                }
                break;
            default:
                System.out.println("INPUT ERROR: THE ID FOR THIS CHARACTER IS SET WRONG!!!");
                
        }
        return false;
    }
    
    /**
     * Checks if the player just pressed down this frame.
     *
     * @return true on the frame the mapped "Down" key is pressed
     */
    public boolean justPressedDown() {
        switch(this.playerID){
            case 1:
                if(Gdx.input.isKeyJustPressed(Input.Keys.S)){
                   return true; 
                }
                break;
            case 2:
                if(Gdx.input.isKeyJustPressed(Input.Keys.G)){
                    return true;
                }
                break;
            case 3:
                if(Gdx.input.isKeyJustPressed(Input.Keys.K)){
                    return true;
                }
                break;
            case 4:
                if(Gdx.input.isKeyJustPressed(Input.Keys.APOSTROPHE)){
                    return true;
                }
                break;
            case 5:
                if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
                    return true;
                }
                break;
            case 6:
                if(Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_5)){
                    return true;
                }
                break;
            default:
                System.out.println("INPUT ERROR: THE ID FOR THIS CHARACTER IS SET WRONG!!!");
                
        }
        return false;
    }
    
    /**
     * Checks if the player is pressing right.
     *
     * @return true while the mapped "Right" key is held for this player
     */
    public boolean isPressingRight() {
        switch(this.playerID){
            case 1:
                if(Gdx.input.isKeyPressed(Input.Keys.D)){
                    return true; 
                }
                break;
            case 2:
                if(Gdx.input.isKeyPressed(Input.Keys.H)){
                    return true;
                }
                break;
            case 3:
                if(Gdx.input.isKeyPressed(Input.Keys.L)){
                    return true;
                }
                break;
            case 4:
                if(Gdx.input.isKeyPressed(Input.Keys.ENTER)){
                    return true;
                }
                break;
            case 5:
                if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
                    return true;
                }
                break;
            case 6:
                if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_6)){
                    return true;
                }
                break;
            default:
                System.out.println("INPUT ERROR: THE ID FOR THIS CHARACTER IS SET WRONG!!!");
                
        }
        return false;
    }
    
    /**
     * Checks if the player just pressed right this frame.
     *
     * @return true on the frame the mapped "Right" key is pressed
     */
    public boolean justPressedRight() {
        switch(this.playerID){
            case 1:
                if(Gdx.input.isKeyJustPressed(Input.Keys.D)){
                    return true; 
                }
                break;
            case 2:
                if(Gdx.input.isKeyJustPressed(Input.Keys.H)){
                    return true;
                }
                break;
            case 3:
                if(Gdx.input.isKeyJustPressed(Input.Keys.L)){
                    return true;
                }
                break;
            case 4:
                if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
                    return true;
                }
                break;
            case 5:
                if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)){
                    return true;
                }
                break;
            case 6:
                if(Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_6)){
                    return true;
                }
                break;
            default:
                System.out.println("INPUT ERROR: THE ID FOR THIS CHARACTER IS SET WRONG!!!");
                
        }
        return false;
    }
       
    /**
     * Checks if the player is pressing action.
     *
     * @return true while the mapped "Action" key is held for this player
     */
    public boolean isPressingAction() {
        switch(this.playerID){
            case 1:
                if(Gdx.input.isKeyPressed(Input.Keys.E)){
                   return true; 
                }
                break;
            case 2:
                if(Gdx.input.isKeyPressed(Input.Keys.Y)){
                    return true;
                }
                break;
            case 3:
                if(Gdx.input.isKeyPressed(Input.Keys.O)){
                    return true;
                }
                break;
            case 4:
                if(Gdx.input.isKeyPressed(Input.Keys.RIGHT_BRACKET)){
                    return true;
                }
                break;
            case 5:
                if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
                    return true;
                }
                break;
            case 6:
                if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_9)){
                    return true;
                }
                break;
            default:
                System.out.println("INPUT ERROR: THE ID FOR THIS CHARACTER IS SET WRONG!!!");
                
        }
        return false;
    }
    
    /**
     * Checks if the player just pressed action this frame.
     *
     * @return true on the frame the mapped "Action" key is pressed
     */
    public boolean justPressedAction() {
        switch(this.playerID){
            case 1:
                if(Gdx.input.isKeyJustPressed(Input.Keys.E)){
                   return true; 
                }
                break;
            case 2:
                if(Gdx.input.isKeyJustPressed(Input.Keys.Y)){
                    return true;
                }
                break;
            case 3:
                if(Gdx.input.isKeyJustPressed(Input.Keys.O)){
                    return true;
                }
                break;
            case 4:
                if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT_BRACKET)){
                    return true;
                }
                break;
            case 5:
                if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                    return true;
                }
                break;
            case 6:
                if(Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_9)){
                    return true;
                }
                break;
            default:
                System.out.println("INPUT ERROR: THE ID FOR THIS CHARACTER IS SET WRONG!!!");
                
        }
        return false;
    }
    
    /**
     * Gets the bounds rectangle.
     *
     * @return the bounds rectangle.
     */
    public Rectangle getBounds(){
        return this.bounds;
    }
}
