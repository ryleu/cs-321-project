package com.roachstudios.critterparade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenuScreen implements Screen {

    private static final float VIRTUAL_WIDTH = 1280f;
    private static final float VIRTUAL_HEIGHT = 720f;

    private final CritterParadeGame game;
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final Stage stage;
    private final Skin skin;
    private BitmapFont titleFont;

    private Texture backgroundTexture;

    public MainMenuScreen(CritterParadeGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        this.stage = new Stage(viewport, game.spriteBatch);
        this.skin = createBasicSkin();

        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        titleFont = FontUtil.createUiFont(48);
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label title = new Label("Critter Parade", titleStyle);
        title.setFontScale(1.0f);
        title.setAlignment(Align.center);

        TextButton playButton = new TextButton("Play", skin);
        TextButton howToPlayButton = new TextButton("How to Play", skin);
        TextButton quitButton = new TextButton("Quit", skin);

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new PlayScreen(game));
            }
        });

        howToPlayButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new HowToPlayScreen(game));
            }
        });

        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        root.add(title).padBottom(40f).row();
        root.add(playButton).width(260f).height(60f).padBottom(20f).row();
        root.add(howToPlayButton).width(260f).height(60f).padBottom(20f).row();
        root.add(quitButton).width(260f).height(60f);
    }

    private Skin createBasicSkin() {
        Skin s = new Skin();
        BitmapFont font = FontUtil.createUiFont(28);
        s.add("default-font", font);

        com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle labelStyle = new com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        s.add("default", labelStyle);

        com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle tbs = new com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle();
        tbs.font = font;
        tbs.fontColor = Color.WHITE;
        s.add("default", tbs);
        return s;
    }

    @Override
    public void show() {
        backgroundTexture = null; // Placeholder for future background art
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.06f, 0.08f, 0.12f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (titleFont != null) {
            titleFont.dispose();
        }
    }
}


