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

/**
 * Screen for selecting the number of players before moving to the next screen
 * (e.g., a board or a mini game).
 */
public class PlayerSelectMenu implements Screen {
    private final CritterParade gameInstance;
    private final Stage stage;
    private final Supplier<Screen> nextScreen;

    /**
     * Constructs the player selection menu screen.
     *
     * @param gameInstance shared game instance used for navigation and skin
     * @param nextScreen supplier for the next screen to show after selecting players
     */
    public PlayerSelectMenu(CritterParade gameInstance, Supplier<Screen> nextScreen) {
        this.gameInstance = gameInstance;
        this.nextScreen = nextScreen;

        // Fixed virtual size for consistent layout.
        stage = new Stage(new FitViewport(640, 360));
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    /**
     * Builds simple buttons for choosing a player count between 2 and 6.
     */
    public void show() {
        Table root = new Table();
        stage.addActor(root);
        root.setFillParent(true);

        TextField title = new TextField("Select Number of Players", gameInstance.skin);
        title.setAlignment(Align.center);

        root.add(title).expandX().fillX();

        for (int i = 2; i <= 6; i++) {
            root.row();
            TextButton changeButton = new TextButton("%d Players".formatted(i), gameInstance.skin);
            int finalI = i;
            changeButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    // Initialize shared players for the selected count
                    gameInstance.initializePlayers(finalI);
                    
                    // Log player initialization
                    gameInstance.log("Initialized %d players", finalI);
                    String[] names = new String[finalI];
                    for (int j = 0; j < finalI; j++) {
                        names[j] = gameInstance.getPlayers()[j].getName();
                    }
                    gameInstance.logPlayersInitialized(finalI, names);
                    
                    // Start board music (stops any currently playing music)
                    gameInstance.startBoardMusic();
                    
                    gameInstance.setScreen(nextScreen.get());
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

    }
}
