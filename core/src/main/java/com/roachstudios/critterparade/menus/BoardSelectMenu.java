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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.roachstudios.critterparade.CritterParade;
import com.roachstudios.critterparade.gameboards.GameBoard;

import java.util.function.Supplier;

public class BoardSelectMenu implements Screen {
    private final CritterParade gameInstance;
    private final Stage stage;

    public BoardSelectMenu(CritterParade gameInstance) {
        this.gameInstance = gameInstance;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        Table root = new Table();
        stage.addActor(root);
        root.setFillParent(true);

        TextField title = new TextField("Select a Board", gameInstance.skin);
        title.setAlignment(Align.center);

        root.add(title).fillX();

        int i = 0;
        for (Supplier<GameBoard> gameBoard : gameInstance.getGameBoards()) {
            root.row();
            TextButton changeButton = new TextButton("Board %d".formatted(++i), gameInstance.skin);
            changeButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    gameInstance.setScreen(gameBoard.get());
                }
            });
            root.add(changeButton);
        }

        root.setDebug(gameInstance.isDebugMode(), true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
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
