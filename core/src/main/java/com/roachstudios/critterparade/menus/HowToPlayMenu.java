package com.roachstudios.critterparade.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.roachstudios.critterparade.CritterParade;

/**
 * Shows basic controls with images for each critter. Uses a scrollable grid
 * to accommodate different window sizes while keeping images at native scale.
 */
public class HowToPlayMenu implements Screen {
    private final CritterParade gameInstance;
    private final Stage stage;
    private final Texture antTexture;
    private final Texture beeTexture;
    private final Texture frogTexture;
    private final Texture ladybugTexture;
    private final Texture mouseTexture;
    private final Texture squirrelTexture;

    /**
     * Constructs the How To Play menu screen.
     *
     * @param gameInstance shared game instance providing skin and navigation
     */
    public HowToPlayMenu(CritterParade gameInstance) {
        this.gameInstance = gameInstance;

        // Fixed virtual size for consistent layout.
        stage = new Stage(new FitViewport(640, 360));
        Gdx.input.setInputProcessor(stage);

        antTexture = new Texture("HowToPlay/Controls/ant.png");
        beeTexture = new Texture("HowToPlay/Controls/bee.png");
        frogTexture = new Texture("HowToPlay/Controls/frog.png");
        ladybugTexture = new Texture("HowToPlay/Controls/ladybug.png");
        mouseTexture = new Texture("HowToPlay/Controls/mouse.png");
        squirrelTexture = new Texture("HowToPlay/Controls/squirrel.png");
    }

    @Override
    /**
     * Builds a scrollable grid of control images with labels.
     */
    public void show() {
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Label title = new Label("How To Play", gameInstance.skin);
        title.setAlignment(Align.center);
        root.add(title).fillX().padBottom(15);

        root.row();

        Table controlsGrid = new Table();
        controlsGrid.defaults().pad(5f);

        addControlCell(controlsGrid, "Ant", antTexture);
        addControlCell(controlsGrid, "Bee", beeTexture);
        addControlCell(controlsGrid, "Frog", frogTexture);
        controlsGrid.row();
        addControlCell(controlsGrid, "Ladybug", ladybugTexture);
        addControlCell(controlsGrid, "Mouse", mouseTexture);
        addControlCell(controlsGrid, "Squirrel", squirrelTexture);

        ScrollPane scrollPane = new ScrollPane(controlsGrid, gameInstance.skin);
        scrollPane.setScrollingDisabled(true, false);

        root.add(scrollPane).expand().fill().minHeight(200f);

        root.row();

        TextButton backButton = new TextButton("Back", gameInstance.skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameInstance.setScreen(new MainMenu(gameInstance));
            }
        });
        root.add(backButton).fillX().pad(10).align(Align.center);

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
    /**
     * Updates the viewport and centers the camera.
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
        antTexture.dispose();
        beeTexture.dispose();
        frogTexture.dispose();
        ladybugTexture.dispose();
        mouseTexture.dispose();
        squirrelTexture.dispose();
    }

    private void addControlCell(Table grid, String labelText, Texture texture) {
        Table cell = new Table();
        Label label = new Label(labelText, gameInstance.skin);
        label.setAlignment(Align.center);
        cell.add(label).center().padBottom(2f);
        cell.row();

        Image image = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
        image.setScaling(Scaling.none); // render at native 128x128 for sharpness
        cell.add(image).center();

        grid.add(cell).expand().fill().top();
    }
}



