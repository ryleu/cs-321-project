package com.roachstudios.critterparade;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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
     * High-level mode affects control flow between screens (e.g., where to go after
     * mini games). We keep it coarse-grained to simplify navigation decisions.
     */
    public enum Mode{
        BOARD_MODE, PRACTICE_MODE;
    }
    
    public Mode mode;

    private final ArrayList<Supplier<MiniGame>> minigameRegistry = new ArrayList<>();
    private final ArrayList<Supplier<GameBoard>> gameBoardRegistry = new ArrayList<>();

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
        registerGameBoard(() -> new PicnicPondBoard(this));

        // register mini games
        registerMiniGame(() -> new SimpleRacerMiniGame(this));

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
    }

    /**
     * @return true if debug visuals should be drawn for UI layout
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Registers a lazy supplier for a mini game. Suppliers are used so screens can
     * create fresh instances on demand instead of reusing stateful objects.
     */
    public void registerMiniGame(Supplier<MiniGame> miniGameSupplier) {
        minigameRegistry.add(miniGameSupplier);
    }

    /**
     * @return immutable view of all registered mini game suppliers
     */
    public List<Supplier<MiniGame>> getMiniGames() {
        return Collections.unmodifiableList(minigameRegistry);
    }

    /**
     * Registers a lazy supplier for a game board.
     */
    public void registerGameBoard(Supplier<GameBoard> gameBoardSupplier) {
        gameBoardRegistry.add(gameBoardSupplier);
    }

    /**
     * @return immutable view of all registered game board suppliers
     */
    public List<Supplier<GameBoard>> getGameBoards() {
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
}
