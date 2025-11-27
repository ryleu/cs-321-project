package com.roachstudios.critterparade;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.roachstudios.critterparade.gameboards.GameBoard;
import com.roachstudios.critterparade.gameboards.PicnicPondBoard;
import com.roachstudios.critterparade.menus.MainMenu;
import com.roachstudios.critterparade.minigames.MiniGame;
import com.roachstudios.critterparade.minigames.SimpleRacerMiniGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Root {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 *
 * <p>Responsible for bootstrapping shared resources, registering available boards and
 * mini games, and managing high-level screen navigation.</p>
 */
public class CritterParade extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    public Skin skin;
    public FitViewport viewport;

    private int numPlayers = 6;
    
    /**
     * Shared player instances persisting across all screens. Initialized via
     * {@link #initializePlayers(int)} and accessed through {@link #getPlayers()}.
     */
    private Player[] players;
    
    /**
     * Sprite texture paths for each player slot. Used during player initialization.
     */
    private static final String[] PLAYER_SPRITE_PATHS = {
        "PlayerSprites/bumble_bee.png",
        "PlayerSprites/lady_bug.png",
        "PlayerSprites/pond_frog.png",
        "PlayerSprites/red_squirrel.png",
        "PlayerSprites/field_mouse.png",
        "PlayerSprites/solider_ant.png"
    };
    
    /**
     * Display names for each critter, indexed by player slot.
     */
    private static final String[] CRITTER_NAMES = {
        "Bumble Bee",
        "Lady Bug",
        "Pond Frog",
        "Red Squirrel",
        "Field Mouse",
        "Soldier Ant"
    };
    
    /**
     * Textures for player sprites, kept alive for the game lifetime to avoid
     * reloading and to allow proper disposal.
     */
    private Texture[] playerTextures;
    
    /**
     * High-level mode affects control flow between screens (e.g., where to go after
     * mini games). We keep it coarse-grained to simplify navigation decisions.
     */
    public enum Mode{
        BOARD_MODE, PRACTICE_MODE;
    }
    
    public Mode mode;

    private final ArrayList<NamedSupplier<MiniGame>> minigameRegistry = new ArrayList<>();
    private final ArrayList<NamedSupplier<GameBoard>> gameBoardRegistry = new ArrayList<>();
    
    /**
     * Current player's turn index (0-based). Persists across board recreations.
     */
    private int currentPlayerTurn = 0;
    
    /**
     * Flag to advance to next player's turn when returning from minigame.
     */
    private boolean advanceTurnOnBoardReturn = false;

    private boolean debugMode = true;

    /**
     * Initializes shared resources and registers boards/mini games.
     */
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        skin = new CritterParadeSkin();
        // Use a small 16x9 virtual world for UI scaling; scene2d widgets are laid out
        // in this space and scaled to the actual window while preserving aspect ratio.
        viewport = new FitViewport(16,9);

        font.setUseIntegerPositions(false);
        // Scale font so that it renders with consistent perceived size across resolutions.
        font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

        // register game boards
        registerGameBoard(PicnicPondBoard.NAME, () -> new PicnicPondBoard(this));

        // register mini games
        registerMiniGame(SimpleRacerMiniGame.NAME, () -> new SimpleRacerMiniGame(this));

        this.setScreen(new MainMenu(this));
    }

    /**
     * Delegates to the active screen.
     */
    public void render() {
        super.render();
    }

    /**
     * Disposes shared resources created in {@link #create()}.
     */
    public void dispose() {
        batch.dispose();
        font.dispose();
        disposePlayerTextures();
    }

    /**
     * @return true if debug visuals should be drawn for UI layout
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Registers a lazy supplier for a mini game with a display name.
     * Suppliers are used so screens can create fresh instances on demand
     * instead of reusing stateful objects.
     *
     * @param name display name for the mini game
     * @param miniGameSupplier supplier that creates a new mini game instance
     */
    public void registerMiniGame(String name, Supplier<MiniGame> miniGameSupplier) {
        minigameRegistry.add(new NamedSupplier<>(name, miniGameSupplier));
    }

    /**
     * @return immutable view of all registered mini games with their names
     */
    public List<NamedSupplier<MiniGame>> getMiniGames() {
        return Collections.unmodifiableList(minigameRegistry);
    }

    /**
     * Registers a lazy supplier for a game board with a display name.
     *
     * @param name display name for the game board
     * @param gameBoardSupplier supplier that creates a new game board instance
     */
    public void registerGameBoard(String name, Supplier<GameBoard> gameBoardSupplier) {
        gameBoardRegistry.add(new NamedSupplier<>(name, gameBoardSupplier));
    }

    /**
     * @return immutable view of all registered game boards with their names
     */
    public List<NamedSupplier<GameBoard>> getGameBoards() {
        return Collections.unmodifiableList(gameBoardRegistry);
    }

    /**
     * @return the currently configured number of players
     */
    public int getNumPlayers(){
        return this.numPlayers;
    }

    /**
     * Sets the number of players for subsequent boards/mini games.
     */
    public void setNumPlayers(int newNumPlayers) {
        this.numPlayers = newNumPlayers;
    }
    
    /**
     * Initializes or reinitializes shared player instances for the given count.
     * Call this when starting a new game session (after player selection).
     *
     * @param count number of players to create (1-6)
     */
    public void initializePlayers(int count) {
        if (count < 1 || count > 6) {
            throw new IllegalArgumentException("Player count must be between 1 and 6");
        }
        
        // Dispose previous textures if they exist
        disposePlayerTextures();
        
        this.numPlayers = count;
        this.players = new Player[count];
        this.playerTextures = new Texture[count];
        
        for (int i = 0; i < count; i++) {
            playerTextures[i] = new Texture(PLAYER_SPRITE_PATHS[i]);
            players[i] = new Player(i + 1, CRITTER_NAMES[i], playerTextures[i]);
        }
    }
    
    /**
     * @return the array of active players, or null if not yet initialized
     */
    public Player[] getPlayers() {
        return players;
    }
    
    /**
     * Gets a specific player by their ID (1-indexed).
     *
     * @param playerId the player ID (1-6)
     * @return the Player instance, or null if invalid ID or not initialized
     */
    public Player getPlayer(int playerId) {
        if (players == null || playerId < 1 || playerId > players.length) {
            return null;
        }
        return players[playerId - 1];
    }
    
    /**
     * Checks if players have been initialized for the current session.
     *
     * @return true if players array exists and has at least one player
     */
    public boolean hasPlayers() {
        return players != null && players.length > 0;
    }
    
    /**
     * Resets all player scores (fruit, crumbs, wins) to zero.
     * Useful when starting a new game session.
     */
    public void resetPlayerScores() {
        if (players == null) return;
        for (Player p : players) {
            p.resetScores();
        }
    }
    
    /**
     * Disposes player textures to free GPU memory.
     */
    private void disposePlayerTextures() {
        if (playerTextures != null) {
            for (Texture tex : playerTextures) {
                if (tex != null) {
                    tex.dispose();
                }
            }
            playerTextures = null;
        }
    }
    
    /**
     * @return the current player turn index (0-based)
     */
    public int getCurrentPlayerTurn() {
        return currentPlayerTurn;
    }
    
    /**
     * Sets the current player turn index.
     * @param turn the turn index (0-based)
     */
    public void setCurrentPlayerTurn(int turn) {
        this.currentPlayerTurn = turn;
    }
    
    /**
     * Advances to the next player's turn.
     */
    public void advancePlayerTurn() {
        currentPlayerTurn = (currentPlayerTurn + 1) % numPlayers;
    }
    
    /**
     * Resets the turn to player 0.
     */
    public void resetPlayerTurn() {
        currentPlayerTurn = 0;
    }
    
    /**
     * Resets all board game state for a new game.
     * Resets player turn, board positions, and scores.
     * @param startTileIndex the tile index where players start
     */
    public void resetBoardGameState(int startTileIndex) {
        currentPlayerTurn = 0;
        advanceTurnOnBoardReturn = false;
        if (players != null) {
            for (Player p : players) {
                p.resetBoardPosition(startTileIndex);
                p.resetScores();
            }
        }
    }
    
    /**
     * @return true if the turn should be advanced when returning to the board
     */
    public boolean shouldAdvanceTurnOnBoardReturn() {
        return advanceTurnOnBoardReturn;
    }
    
    /**
     * Sets the flag to advance turn when returning to board.
     * @param advance true to advance turn on return
     */
    public void setAdvanceTurnOnBoardReturn(boolean advance) {
        this.advanceTurnOnBoardReturn = advance;
    }
}
