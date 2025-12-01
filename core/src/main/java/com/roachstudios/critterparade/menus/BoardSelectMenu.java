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
import com.roachstudios.critterparade.NamedSupplier;
import com.roachstudios.critterparade.gameboards.GameBoard;

/**
 * Presents a list of available boards and navigates to {@link PlayerSelectMenu}
 * to select the player count for the chosen board.
 */
public class BoardSelectMenu implements Screen {
    private final CritterParade gameInstance;
    private final Stage stage;

    /**
     * Constructs the board selection screen.
     *
     * @param gameInstance shared game instance used for navigation and skin
     */
    public BoardSelectMenu(CritterParade gameInstance) {
        this.gameInstance = gameInstance;

        // Fixed virtual size for predictable layout across window sizes.
        stage = new Stage(new FitViewport(640, 360));
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Builds the simple list UI with a title and one button per board option.
     */
    @Override
    public void show() {
        // Update viewport to current screen size to ensure proper scaling
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        // Reset font scale (minigames may have changed it for their world-unit viewports)
        gameInstance.font.getData().setScale(1.0f);
        
        Table root = new Table();
        stage.addActor(root);
        root.setFillParent(true);

        Label title = new Label("Select a Board", gameInstance.getSkin());
        title.setAlignment(Align.center);

        root.add(title).expandX().fillX().padBottom(20);

        for (NamedSupplier<GameBoard> namedBoard : gameInstance.getGameBoards()) {
            root.row();
            TextButton changeButton = new TextButton(namedBoard.name(), gameInstance.getSkin());
            changeButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    gameInstance.log("Board selected: %s", namedBoard.name());
                    gameInstance.logBoardStart(namedBoard.name());
                    gameInstance.setScreen(new PlayerSelectMenu(gameInstance, namedBoard.supplier()::get));
                }
            });
            root.add(changeButton).pad(5);
        }

        root.setDebug(gameInstance.isDebugMode(), true);
    }

    /**
     * Clears the screen and renders the stage.
     *
     * @param delta time since last frame
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
     * Keeps the virtual size consistent and centers the camera on resize.
     *
     * @param width new window width
     * @param height new window height
     */
    @Override
    public void resize(int width, int height) {
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
