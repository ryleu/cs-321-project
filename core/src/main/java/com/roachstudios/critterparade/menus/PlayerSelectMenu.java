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

import java.util.function.Supplier;

public class PlayerSelectMenu implements Screen {
    private final CritterParade gameInstance;
    private final Stage stage;
    private final Supplier<Screen> nextScreen;

    public PlayerSelectMenu(CritterParade gameInstance, Supplier<Screen> nextScreen) {
        this.gameInstance = gameInstance;
        this.nextScreen = nextScreen;

        stage = new Stage(new FitViewport(640, 360));
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        Table root = new Table();
        stage.addActor(root);
        root.setFillParent(true);

        TextField title = new TextField("Select Number of Players", gameInstance.skin);
        title.setAlignment(Align.center);

        root.add(title).fillX();

        for (int i = 2; i <= 6; i++) {
            root.row();
            TextButton changeButton = new TextButton("%d Players".formatted(i), gameInstance.skin);
            int finalI = i;
            changeButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    gameInstance.setNumPlayers(finalI);
                    gameInstance.setScreen(nextScreen.get());
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
