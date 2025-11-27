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

    @Override
    /**
     * Builds the simple list UI with a title and one button per board option.
     */
    public void show() {
        Table root = new Table();
        stage.addActor(root);
        root.setFillParent(true);

        TextField title = new TextField("Select a Board", gameInstance.skin);
        title.setAlignment(Align.center);

        root.add(title).fillX();

        for (NamedSupplier<GameBoard> namedBoard : gameInstance.getGameBoards()) {
            root.row();
            TextButton changeButton = new TextButton(namedBoard.name(), gameInstance.skin);
            changeButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    gameInstance.setScreen(new PlayerSelectMenu(gameInstance, namedBoard.supplier()::get));
                }
            });
            root.add(changeButton);
        }

        root.setDebug(gameInstance.isDebugMode(), true);
    }

    @Override
    /**
     * Clears the screen and renders the stage.
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
     * Keeps the virtual size consistent and centers the camera on resize.
     */
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
