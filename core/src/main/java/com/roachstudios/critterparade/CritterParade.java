package com.roachstudios.critterparade;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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

    // Callback invoked by result screens when a mini game finishes in board mode
    public Runnable onMiniGameCompleteCallback = null;

    // Persistent players shared across boards and mini games
    private final ArrayList<Player> players = new ArrayList<>();

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

    /**
     * Initializes persistent players with default textures and sprite sizes.
     * Safe to call multiple times; will reinitialize to the current player count.
     */
    public void initPlayers() {
        players.clear();
        for (int id = 1; id <= numPlayers; id++) {
            String path = switch (id) {
                case 1 -> "PlayerSprites/bumble_bee.png";
                case 2 -> "PlayerSprites/lady_bug.png";
                case 3 -> "PlayerSprites/pond_frog.png";
                case 4 -> "PlayerSprites/red_squirrel.png";
                case 5 -> "PlayerSprites/field_mouse.png";
                case 6 -> "PlayerSprites/solider_ant.png";
                default -> "PlayerSprites/bumble_bee.png";
            };
            com.badlogic.gdx.graphics.Texture tex = new com.badlogic.gdx.graphics.Texture(path);
            Player p = new Player(id, tex);
            p.setSpriteSize(1.0f);
            players.add(p);
        }
    }

    /**
     * @return immutable view of persistent players
     */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Starts a random registered mini game and configures the flow to return
     * to the provided screen once the mini game results are acknowledged.
     */
    public void startRandomMiniGameReturningTo(Screen returnToScreen) {
        // ensure board mode so results route correctly
        this.mode = Mode.BOARD_MODE;
        // configure callback for MiniGameResultScreen
        this.onMiniGameCompleteCallback = () -> setScreen(returnToScreen);

        // pick any registered mini game (simple uniform random for now)
        if (minigameRegistry.isEmpty()) {
            return; // nothing to start
        }
        int idx = (int) (Math.random() * minigameRegistry.size());
        MiniGame mg = minigameRegistry.get(idx).get();
        setScreen(mg);
    }
}
