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

import com.roachstudios.critterparade.menus.ConsentScreen;
import com.roachstudios.critterparade.minigames.CatchObjectsMiniGame;
import com.roachstudios.critterparade.minigames.DodgeBallMiniGame;

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
    /** Shared sprite batch used for rendering across all screens. */
    public SpriteBatch batch;
    /** Shared bitmap font used for text rendering across all screens. */
    public BitmapFont font;
    /** Shared UI skin used for scene2d widgets across all screens. */
    public Skin skin;
    /** Shared viewport used for consistent UI scaling across screen sizes. */
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
    public enum Mode {
        /** Full board game mode with minigames and fruits. */
        BOARD_MODE,
        /** Practice mode for playing individual minigames. */
        PRACTICE_MODE
    }
    
    /** Current game mode affecting navigation flow between screens. */
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

    private final boolean debugMode;
    
    private SettingsManager settings;
    private SessionLogger sessionLogger;
    
    /**
     * Creates the game with debug mode disabled.
     */
    public CritterParade() {
        this(false);
    }
    
    /**
     * Creates the game with the specified debug mode.
     *
     * @param debugMode true to enable debug visuals
     */
    public CritterParade(boolean debugMode) {
        this.debugMode = debugMode;
    }
    
    /**
     * Logs a message to the console.
     *
     * @param message the message to log
     */
    public void log(String message) {
        System.out.println("[CritterParade] " + message);
    }
    
    /**
     * Logs a formatted message to the console.
     *
     * @param format the format string
     * @param args the format arguments
     */
    public void log(String format, Object... args) {
        System.out.println("[CritterParade] " + String.format(format, args));
    }
    
    /**
     * Gets the session logger for tracking game events.
     *
     * @return the session logger for tracking game events
     */
    public SessionLogger getSessionLogger() {
        return sessionLogger;
    }
    
    /**
     * Sets the user's logging consent preference and initializes the session logger.
     *
     * @param enabled true to enable session logging
     */
    public void setLoggingConsent(boolean enabled) {
        settings.setLoggingEnabled(enabled);
        settings.save();
        sessionLogger = new SessionLogger(enabled);
    }
    
    // =========================================================================
    // Session Logging Helpers (null-safe wrappers)
    // =========================================================================
    
    /**
     * Logs a mode selection event if logging is enabled.
     *
     * @param mode the game mode that was selected
     */
    public void logModeSelected(Mode mode) {
        if (sessionLogger != null) {
            sessionLogger.logModeSelected(mode);
        }
    }
    
    /**
     * Logs player initialization if logging is enabled.
     *
     * @param count the number of players initialized
     * @param names the names of the players
     */
    public void logPlayersInitialized(int count, String[] names) {
        if (sessionLogger != null) {
            sessionLogger.logPlayersInitialized(count, names);
        }
    }
    
    /**
     * Logs a minigame starting if logging is enabled.
     *
     * @param minigameName the name of the minigame being started
     */
    public void logMinigameStart(String minigameName) {
        if (sessionLogger != null) {
            sessionLogger.logMinigameStart(minigameName);
        }
    }
    
    /**
     * Logs minigame results if logging is enabled.
     *
     * @param minigameName the name of the minigame that ended
     * @param placements the player names in placement order (1st to last)
     * @param crumbsAwarded the crumbs awarded to each player
     */
    public void logMinigameEnd(String minigameName, String[] placements, int[] crumbsAwarded) {
        if (sessionLogger != null) {
            sessionLogger.logMinigameEnd(minigameName, placements, crumbsAwarded);
        }
    }
    
    /**
     * Logs a board game starting if logging is enabled.
     *
     * @param boardName the name of the board being started
     */
    public void logBoardStart(String boardName) {
        if (sessionLogger != null) {
            sessionLogger.logBoardStart(boardName);
        }
    }
    
    /**
     * Logs a player turn if logging is enabled.
     *
     * @param playerName the name of the player taking their turn
     * @param diceRoll the result of the dice roll
     */
    public void logPlayerTurn(String playerName, int diceRoll) {
        if (sessionLogger != null) {
            sessionLogger.logPlayerTurn(playerName, diceRoll);
        }
    }
    
    /**
     * Logs navigation to a screen if logging is enabled.
     *
     * @param screenName the name of the screen being navigated to
     */
    public void logScreenChange(String screenName) {
        if (sessionLogger != null) {
            sessionLogger.logScreenChange(screenName);
        }
    }

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
        registerMiniGame(DodgeBallMiniGame.NAME, () -> new DodgeBallMiniGame(this));
        registerMiniGame(CatchObjectsMiniGame.NAME, () -> new CatchObjectsMiniGame(this));

        // Load settings and check for first run
        settings = new SettingsManager();
        
        if (settings.isFirstRun()) {
            // Show consent screen on first run
            log("First run detected, showing consent screen");
            this.setScreen(new ConsentScreen(this));
        } else {
            // Initialize session logger with saved preference
            sessionLogger = new SessionLogger(settings.isLoggingEnabled());
            log("Session logging: " + (settings.isLoggingEnabled() ? "enabled" : "disabled"));
            this.setScreen(new MainMenu(this));
        }
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
        // Save session log before disposing
        if (sessionLogger != null) {
            sessionLogger.saveSession();
        }
        
        batch.dispose();
        font.dispose();
        disposePlayerTextures();
    }

    /**
     * Checks if debug mode is enabled.
     *
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
     * Gets all registered mini games.
     *
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
     * Gets all registered game boards.
     *
     * @return immutable view of all registered game boards with their names
     */
    public List<NamedSupplier<GameBoard>> getGameBoards() {
        return Collections.unmodifiableList(gameBoardRegistry);
    }

    /**
     * Gets the currently configured number of players.
     *
     * @return the currently configured number of players
     */
    public int getNumPlayers() {
        return this.numPlayers;
    }

    /**
     * Sets the number of players for subsequent boards/mini games.
     *
     * @param newNumPlayers the new number of players
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
     * Gets the array of active players.
     *
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
     * Gets the current player turn index.
     *
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
     * Checks if the turn should be advanced when returning to the board.
     *
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
