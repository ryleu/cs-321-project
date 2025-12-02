package com.roachstudios.critterparade.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.roachstudios.critterparade.CritterParade;
import com.roachstudios.critterparade.minigames.MiniGameDescriptor;

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

    /**
     * Builds the UI tree the first time the screen is shown.
     *
     * <p>We create widgets here (rather than in the constructor) so the screen can
     * be reinstantiated or revisited without holding onto stale UI state.</p>
     */
    @Override
    public void show() {
        // Update viewport to current screen size to ensure proper scaling
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        // Reset font scale (minigames may have changed it for their world-unit viewports)
        gameInstance.getFont().getData().setScale(1.0f);
        
        // Build the UI tree on-demand to keep the constructor lightweight.
        Table root = new Table();
        stage.addActor(root);
        root.setFillParent(true);

        Label title = new Label("Select a Mini Game", gameInstance.getSkin());
        title.setAlignment(Align.center);

        root.add(title).expandX().fillX().padBottom(20);

        for (MiniGameDescriptor miniGame : gameInstance.getMiniGames()) {
            root.row();
            TextButton changeButton = new TextButton(miniGame.name(), gameInstance.getSkin());
            changeButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    // Navigate to player select, then to instruction screen before the minigame
                    gameInstance.setScreen(new PlayerSelectMenu(gameInstance, 
                        () -> new MiniGameInstructionScreen(gameInstance, miniGame),
                        () -> new MiniGameSelectMenu(gameInstance)));
                }
            });
            root.add(changeButton).pad(5);
        }

        root.row();

        TextButton backButton = new TextButton("Back", gameInstance.getSkin());
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameInstance.setScreen(new MainMenu(gameInstance));
            }
        });
        root.add(backButton).pad(5);

        root.setDebug(gameInstance.isDebugMode(), true);
    }

    /**
     * Advances and draws the stage each frame.
     *
     * @param delta time in seconds since the last frame
     */
    @Override
    public void render(float delta) {
        // Clear each frame; scene2d does not clear automatically.
        Gdx.gl.glClearColor(1f, 0.992f, 0.816f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    /**
     * Updates the viewport to maintain the virtual size and center the camera.
     *
     * @param width new window width
     * @param height new window height
     */
    @Override
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
