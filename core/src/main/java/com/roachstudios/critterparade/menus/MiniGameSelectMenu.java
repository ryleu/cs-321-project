package com.roachstudios.critterparade.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.roachstudios.critterparade.CritterParade;
import com.roachstudios.critterparade.NamedSupplier;
import com.roachstudios.critterparade.minigames.MiniGame;

/**
 * Presents a list of available mini games and navigates to a {@link PlayerSelectMenu}
 * for the chosen game. Uses scene2d UI with a {@link Stage} and {@link Table}-based
 * layout for simplicity and consistency.
 */
public class MiniGameSelectMenu implements Screen {
    private final CritterParade gameInstance;
    private final Stage stage;

    /**
     * Constructs the mini-game selection screen.
     *
     * @param gameInstance shared game instance providing skin, mode, and navigation
     */
    public MiniGameSelectMenu(CritterParade gameInstance) {
        this.gameInstance = gameInstance;

        // Use a fixed virtual size so UI scales consistently across aspect ratios.
        stage = new Stage(new FitViewport(640, 360));
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    /**
     * Builds the UI tree the first time the screen is shown.
     *
     * <p>We create widgets here (rather than in the constructor) so the screen can
     * be reinstantiated or revisited without holding onto stale UI state.</p>
     */
    public void show() {
        // Build the UI tree on-demand to keep the constructor lightweight.
        Table root = new Table();
        stage.addActor(root);
        root.setFillParent(true);

        TextField title = new TextField("Select a Mini Game", gameInstance.skin);
        title.setAlignment(Align.center);

        root.add(title).fillX();

        for (NamedSupplier<MiniGame> namedMiniGame : gameInstance.getMiniGames()) {
            root.row();
            TextButton changeButton = new TextButton(namedMiniGame.name(), gameInstance.skin);
            changeButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    gameInstance.setScreen(new PlayerSelectMenu(gameInstance, namedMiniGame.supplier()::get));
                }
            });
            root.add(changeButton);
        }

        root.setDebug(gameInstance.isDebugMode(), true);
    }

    @Override
    /**
     * Advances and draws the stage each frame.
     *
     * @param delta time in seconds since the last frame
     */
    public void render(float delta) {
        // Clear each frame; scene2d does not clear automatically.
        Gdx.gl.glClearColor(1f, 0.992f, 0.816f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    /**
     * Updates the viewport to maintain the virtual size and center the camera.
     */
    public void resize(int width, int height) {
        // Center the camera so the virtual area remains anchored after resize.
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
