package com.roachstudios.critterparade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

public class HowToPlayScreen implements Screen {
    private static final float VIRTUAL_WIDTH = 1280f;
    private static final float VIRTUAL_HEIGHT = 720f;

    private final CritterParadeGame game;
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final Stage stage;
    private final Skin skin;

    public HowToPlayScreen(CritterParadeGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        this.stage = new Stage(viewport, game.spriteBatch);
        this.skin = createBasicSkin();

        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Label title = new Label("How to Play", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        title.setFontScale(2.0f);
        title.setAlignment(Align.center);

        Label instructions = new Label(
                "Roll dice, move across tiles, and play minigames with friends!\n" +
                "First to collect the most stars wins.",
                new Label.LabelStyle(new BitmapFont(), Color.LIGHT_GRAY)
        );
        instructions.setAlignment(Align.center);

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        root.add(title).padBottom(30f).row();
        root.add(instructions).padBottom(30f).row();
        root.add(backButton).width(200f).height(50f);
    }

    private Skin createBasicSkin() {
        Skin s = new Skin();
        BitmapFont font = new BitmapFont();
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
    public void show() { }

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
    }
}


