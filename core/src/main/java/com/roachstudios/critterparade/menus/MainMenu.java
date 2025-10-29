package com.roachstudios.critterparade.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.roachstudios.critterparade.CritterParade;

public class MainMenu implements Screen {
    private final CritterParade gameInstance;
    private final Stage stage;

    public MainMenu(CritterParade gameInstance) {
        this.gameInstance = gameInstance;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        // the main element of the menu, everything else is a child of this
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Image logo = new Image(new Texture("logo.png"));
        root.add(logo).fill();

        root.row();

        TextButton play = new TextButton("Play", gameInstance.skin);
        play.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameInstance.setScreen(new BoardSelectMenu(gameInstance));
            }
        });
        root.add(play).fillX().align(Align.center);

        root.setDebug(gameInstance.isDebugMode(), true);
    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // make the things on the stage act and render
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int i, int i1) {

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
