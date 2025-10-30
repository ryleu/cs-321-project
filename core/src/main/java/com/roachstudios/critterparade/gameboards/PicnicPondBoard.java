package com.roachstudios.critterparade.gameboards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.roachstudios.critterparade.CritterParade;

/**
 * A simple board implementation that displays a static background image.
 */
public class PicnicPondBoard extends GameBoard {
    private final CritterParade gameInstance;
    private final Stage stage;

    /**
     * @param gameInstance shared game instance used for navigation and skin
     */
    public PicnicPondBoard(CritterParade gameInstance) {
        this.gameInstance = gameInstance;

        // ScreenViewport ensures the background is rendered pixel-perfect without
        // additional scaling logic for this simple board.
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    /**
     * Builds a root table and sets a background texture.
     */
    public void show() {
        Table root = new Table();
        stage.addActor(root);
        root.setFillParent(true);
        root.setBackground(
            new TextureRegionDrawable(
                new TextureRegion(
                    new Texture("board/PicnicPond/background.png")
                )
            )
        );

        root.add(new TextField("Under Construction", gameInstance.skin));

        root.setDebug(gameInstance.isDebugMode(), true);
    }

    @Override
    /**
     * Clears the screen and renders the stage.
     */
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 0.992f, 0.816f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

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
