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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.roachstudios.critterparade.CritterParade;

public class MainMenu implements Screen {
    private final CritterParade gameInstance;
    private final Stage stage;
    private final Texture logoTexture;

    public MainMenu(CritterParade gameInstance) {
        this.gameInstance = gameInstance;
        logoTexture = new Texture("logo.png");

        stage = new Stage(new FitViewport(640, 360));
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        // the main element of the menu, everything else is a child of this
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Image logo = new Image(logoTexture);
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

        root.row();

        TextButton miniGames = new TextButton("Mini Games", gameInstance.skin);
        miniGames.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameInstance.setScreen(new MiniGameSelectMenu(gameInstance));
            }
        });
        root.add(miniGames).fill().align(Align.center);

        root.row();

        TextButton howToPlay = new TextButton("How To Play", gameInstance.skin);
        howToPlay.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameInstance.setScreen(new HowToPlayMenu(gameInstance));
            }
        });
        root.add(howToPlay).fill().align(Align.center);

        root.row();

        TextButton exit = new TextButton("Exit", gameInstance.skin);
        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        root.add(exit).fill().align(Align.center);

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
        stage.getViewport().update(i, i1, true);
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
        logoTexture.dispose();
    }
}
