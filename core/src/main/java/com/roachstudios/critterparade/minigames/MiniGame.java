package com.roachstudios.critterparade.minigames;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.roachstudios.critterparade.CritterParade;
import com.roachstudios.critterparade.Player;

/**
 * Base type for all mini games. Extends {@link Screen} to integrate with LibGDX's
 * screen lifecycle and provides access to shared game resources like players,
 * sprites, and scoring.
 *
 * <p>Subclasses should call the constructor with the game instance to gain access
 * to shared players and resources. Override {@link #onGameComplete(Player[])} to
 * handle results when the minigame ends.</p>
 * 
 * <p>The ready-up phase and countdown are handled by {@link com.roachstudios.critterparade.menus.MiniGameInstructionScreen}
 * before the minigame starts.</p>
 */
public abstract class MiniGame implements Screen {
    
    /**
     * Gets the display name for this mini game.
     *
     * @return the display name for this mini game
     */
    public abstract String getName();
    
    /**
     * Gets the instructions explaining how to play this mini game.
     *
     * @return instructions explaining how to play this mini game
     */
    public abstract String getInstructions();
    
    /**
     * Reference to the main game instance for accessing shared resources.
     */
    protected final CritterParade game;
    
    /**
     * Constructs a MiniGame with access to shared game resources.
     *
     * @param game the main game instance providing viewport, batch, players, etc.
     */
    protected MiniGame(CritterParade game) {
        this.game = game;
    }
    
    // =========================================================================
    // Player Access
    // =========================================================================
    
    /**
     * Gets all active players in the current game session.
     *
     * @return all active players in the current game session
     */
    protected Player[] getPlayers() {
        return game.getPlayers();
    }
    
    /**
     * Gets a specific player by their 1-indexed ID.
     *
     * @param playerId player ID (1-6)
     * @return the Player instance, or null if invalid
     */
    protected Player getPlayer(int playerId) {
        return game.getPlayer(playerId);
    }
    
    /**
     * Gets the number of active players.
     *
     * @return the number of active players
     */
    protected int getPlayerCount() {
        return game.getNumPlayers();
    }
    
    // =========================================================================
    // Sprite Access
    // =========================================================================
    
    /**
     * Gets the sprite for a specific player.
     *
     * @param playerId player ID (1-6)
     * @return the player's Sprite, or null if player doesn't exist
     */
    protected Sprite getPlayerSprite(int playerId) {
        Player player = getPlayer(playerId);
        return player != null ? player.getSprite() : null;
    }
    
    /**
     * Collects all player sprites into an array.
     *
     * @return array of sprites for all active players
     */
    protected Sprite[] getAllPlayerSprites() {
        Player[] players = getPlayers();
        if (players == null) return new Sprite[0];
        
        Sprite[] sprites = new Sprite[players.length];
        for (int i = 0; i < players.length; i++) {
            sprites[i] = players[i].getSprite();
        }
        return sprites;
    }
    
    // =========================================================================
    // Score Access
    // =========================================================================
    
    /**
     * Gets a player's current fruit count.
     *
     * @param playerId player ID (1-6)
     * @return fruit count, or 0 if player doesn't exist
     */
    protected int getPlayerFruit(int playerId) {
        Player player = getPlayer(playerId);
        return player != null ? player.getFruit() : 0;
    }
    
    /**
     * Gets a player's current crumb count.
     *
     * @param playerId player ID (1-6)
     * @return crumb count, or 0 if player doesn't exist
     */
    protected int getPlayerCrumbs(int playerId) {
        Player player = getPlayer(playerId);
        return player != null ? player.getCrumbs() : 0;
    }
    
    /**
     * Gets a player's minigame win count.
     *
     * @param playerId player ID (1-6)
     * @return win count, or 0 if player doesn't exist
     */
    protected int getPlayerWins(int playerId) {
        Player player = getPlayer(playerId);
        return player != null ? player.getWins() : 0;
    }
    
    /**
     * Awards a win to the specified player.
     *
     * @param playerId player ID (1-6)
     */
    protected void awardWin(int playerId) {
        Player player = getPlayer(playerId);
        if (player != null) {
            player.addWin();
        }
    }
    
    /**
     * Awards crumbs to the specified player.
     *
     * @param playerId player ID (1-6)
     * @param amount number of crumbs to add
     */
    protected void awardCrumbs(int playerId, int amount) {
        Player player = getPlayer(playerId);
        if (player != null) {
            player.addCrumbs(amount);
        }
    }
    
    // =========================================================================
    // Input/Control Access
    // =========================================================================
    
    /**
     * Checks if a player is holding their up input.
     *
     * @param playerId player ID (1-6)
     * @return true if pressing up, false otherwise
     */
    protected boolean isPlayerPressingUp(int playerId) {
        Player player = getPlayer(playerId);
        return player != null && player.isPressingUp();
    }
    
    /**
     * Checks if a player just pressed their up input this frame.
     *
     * @param playerId player ID (1-6)
     * @return true if just pressed up, false otherwise
     */
    protected boolean playerJustPressedUp(int playerId) {
        Player player = getPlayer(playerId);
        return player != null && player.justPressedUp();
    }
    
    /**
     * Checks if a player is holding their down input.
     *
     * @param playerId player ID (1-6)
     * @return true if pressing down, false otherwise
     */
    protected boolean isPlayerPressingDown(int playerId) {
        Player player = getPlayer(playerId);
        return player != null && player.isPressingDown();
    }
    
    /**
     * Checks if a player just pressed their down input this frame.
     *
     * @param playerId player ID (1-6)
     * @return true if just pressed down, false otherwise
     */
    protected boolean playerJustPressedDown(int playerId) {
        Player player = getPlayer(playerId);
        return player != null && player.justPressedDown();
    }
    
    /**
     * Checks if a player is holding their left input.
     *
     * @param playerId player ID (1-6)
     * @return true if pressing left, false otherwise
     */
    protected boolean isPlayerPressingLeft(int playerId) {
        Player player = getPlayer(playerId);
        return player != null && player.isPressingLeft();
    }
    
    /**
     * Checks if a player just pressed their left input this frame.
     *
     * @param playerId player ID (1-6)
     * @return true if just pressed left, false otherwise
     */
    protected boolean playerJustPressedLeft(int playerId) {
        Player player = getPlayer(playerId);
        return player != null && player.justPressedLeft();
    }
    
    /**
     * Checks if a player is holding their right input.
     *
     * @param playerId player ID (1-6)
     * @return true if pressing right, false otherwise
     */
    protected boolean isPlayerPressingRight(int playerId) {
        Player player = getPlayer(playerId);
        return player != null && player.isPressingRight();
    }
    
    /**
     * Checks if a player just pressed their right input this frame.
     *
     * @param playerId player ID (1-6)
     * @return true if just pressed right, false otherwise
     */
    protected boolean playerJustPressedRight(int playerId) {
        Player player = getPlayer(playerId);
        return player != null && player.justPressedRight();
    }
    
    /**
     * Checks if a player is holding their action input.
     *
     * @param playerId player ID (1-6)
     * @return true if pressing action, false otherwise
     */
    protected boolean isPlayerPressingAction(int playerId) {
        Player player = getPlayer(playerId);
        return player != null && player.isPressingAction();
    }
    
    /**
     * Checks if a player just pressed their action input this frame.
     *
     * @param playerId player ID (1-6)
     * @return true if just pressed action, false otherwise
     */
    protected boolean playerJustPressedAction(int playerId) {
        Player player = getPlayer(playerId);
        return player != null && player.justPressedAction();
    }
    
    // =========================================================================
    // Game Completion
    // =========================================================================
    
    /**
     * Called by subclasses when the minigame ends to report final placements.
     * Override this to customize result handling, or use the default which
     * navigates to the results screen.
     *
     * @param placements players ordered from 1st to last place
     */
    protected void onGameComplete(Player[] placements) {
        // Default implementation: award a win to first place and show results
        if (placements != null && placements.length > 0 && placements[0] != null) {
            placements[0].addWin();
        }
        
        // Award crumbs based on placement (5 for 1st, scaling down to 0 for last)
        int[] crumbsAwarded = awardPlacementCrumbs(placements);
        
        // Log minigame completion
        game.log("Minigame '%s' completed", getName());
        if (placements != null) {
            String[] names = new String[placements.length];
            for (int i = 0; i < placements.length; i++) {
                names[i] = placements[i] != null ? placements[i].getName() : "Unknown";
            }
            game.logMinigameEnd(getName(), names, crumbsAwarded);
        }
        
        game.setScreen(new com.roachstudios.critterparade.menus.MiniGameResultScreen(game, placements));
    }
    
    /**
     * Awards crumbs to players based on their placement in the minigame.
     * Points scale linearly from 5 (1st place) to 0 (last place), rounded down.
     * Supports 2-6 players.
     * 
     * Examples:
     * - 6 players: 5, 4, 3, 2, 1, 0
     * - 4 players: 5, 3, 1, 0
     * - 2 players: 5, 0
     *
     * @param placements players ordered from 1st to last place
     * @return array of crumbs awarded to each player, or empty array if invalid
     */
    protected int[] awardPlacementCrumbs(Player[] placements) {
        if (placements == null || placements.length < 2) {
            return new int[0];
        }
        
        int numPlayers = placements.length;
        int[] crumbsAwarded = new int[numPlayers];
        
        // Award points based on placement: floor(5 * (numPlayers - placement) / (numPlayers - 1))
        // placement is 1-indexed (1 = first place)
        for (int i = 0; i < numPlayers; i++) {
            if (placements[i] != null) {
                int placement = i + 1; // Convert to 1-indexed
                int crumbs = (5 * (numPlayers - placement)) / (numPlayers - 1);
                placements[i].addCrumbs(crumbs);
                crumbsAwarded[i] = crumbs;
            }
        }
        
        return crumbsAwarded;
    }
    
    // =========================================================================
    // Default Screen Lifecycle (can be overridden)
    // =========================================================================
    
    @Override
    public void show() {
        // Override in subclass if needed
    }
    
    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }
    
    @Override
    public void pause() {
        // Override in subclass if needed
    }
    
    @Override
    public void resume() {
        // Override in subclass if needed
    }
    
    @Override
    public void hide() {
        // Override in subclass if needed
    }
    
    @Override
    public void dispose() {
        // Override in subclass if needed for minigame-specific resources
    }
}
