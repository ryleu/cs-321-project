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

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class CritterParade extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    public Skin skin;
    public FitViewport viewport;

    private int numPlayers = 6;

    private final ArrayList<Supplier<MiniGame>> minigameRegistry = new ArrayList<>();
    private final ArrayList<Supplier<GameBoard>> gameBoardRegistry = new ArrayList<>();

    private boolean debugMode = true;

    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        skin = new CritterParadeSkin();
        viewport = new FitViewport(16,9);

        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

        // register game boards
        registerGameBoard(() -> new PicnicPondBoard(this));

        // register mini games
        registerMiniGame(() -> new SimpleRacerMiniGame(this));

        this.setScreen(new MainMenu(this));
    }

    public void render() {
        super.render();
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void registerMiniGame(Supplier<MiniGame> miniGameSupplier) {
        minigameRegistry.add(miniGameSupplier);
    }

    public List<Supplier<MiniGame>> getMiniGames() {
        return Collections.unmodifiableList(minigameRegistry);
    }

    public void registerGameBoard(Supplier<GameBoard> gameBoardSupplier) {
        gameBoardRegistry.add(gameBoardSupplier);
    }

    public List<Supplier<GameBoard>> getGameBoards() {
        return Collections.unmodifiableList(gameBoardRegistry);
    }

    public int getNumPlayers(){
        return this.numPlayers;
    }

    public void setNumPlayers(int newNumPlayers) {
        this.numPlayers = newNumPlayers;
    }
}
