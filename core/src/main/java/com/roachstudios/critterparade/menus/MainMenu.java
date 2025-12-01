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

/**
 * The main entry menu for the game. Presents navigation to Board mode,
 * Mini games, How To Play, and Exit.
 */
public class MainMenu implements Screen {
    private final CritterParade gameInstance;
    private final Stage stage;
    private final Texture logoTexture;

    /**
     * Constructs the main menu screen.
     *
     * @param gameInstance shared game instance used for navigation and skin
     */
    public MainMenu(CritterParade gameInstance) {
        this.gameInstance = gameInstance;
        logoTexture = new Texture("logo.png");

        // Fixed virtual size for consistent layout.
        stage = new Stage(new FitViewport(640, 360));
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Composes the menu UI using a columnar {@link Table} layout.
     */
    @Override
    public void show() {
        // Start playing intro music (managed by CritterParade)
        gameInstance.startIntroMusic();
        
        // the main element of the menu, everything else is a child of this
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Image logo = new Image(logoTexture);
        root.add(logo).fill();

        root.row();

        TextButton play = new TextButton("Play", gameInstance.getSkin());
        play.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameInstance.setMode(CritterParade.Mode.BOARD_MODE);
                gameInstance.log("Mode selected: BOARD_MODE");
                gameInstance.logModeSelected(CritterParade.Mode.BOARD_MODE);
                gameInstance.setScreen(new BoardSelectMenu(gameInstance));
            }
        });
        root.add(play).fillX().pad(5).align(Align.center);

        root.row();

        TextButton miniGames = new TextButton("Mini Games", gameInstance.getSkin());
        miniGames.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameInstance.setMode(CritterParade.Mode.PRACTICE_MODE);
                gameInstance.log("Mode selected: PRACTICE_MODE");
                gameInstance.logModeSelected(CritterParade.Mode.PRACTICE_MODE);
                gameInstance.setScreen(new MiniGameSelectMenu(gameInstance));
            }
        });
        root.add(miniGames).fill().pad(5).align(Align.center);

        root.row();

        TextButton rushMode = new TextButton("Minigame Rush", gameInstance.getSkin());
        rushMode.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameInstance.setMode(CritterParade.Mode.RUSH_MODE);
                gameInstance.log("Mode selected: RUSH_MODE");
                gameInstance.logModeSelected(CritterParade.Mode.RUSH_MODE);
                // Go to player select, then start the rush
                gameInstance.setScreen(new PlayerSelectMenu(gameInstance, () -> {
                    // Reset player scores for a fresh rush
                    gameInstance.resetPlayerScores();
                    // Create and set the rush controller
                    MiniGameRushController rushController = new MiniGameRushController(gameInstance);
                    gameInstance.setRushController(rushController);
                    // Return the instruction screen for the first minigame
                    return new MiniGameInstructionScreen(gameInstance, rushController.getCurrentMinigame());
                }));
            }
        });
        root.add(rushMode).fill().align(Align.center);

        root.row();

        TextButton howToPlay = new TextButton("How To Play", gameInstance.getSkin());
        howToPlay.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameInstance.setScreen(new HowToPlayMenu(gameInstance));
            }
        });
        root.add(howToPlay).fill().pad(5).align(Align.center);

        root.row();

        TextButton leaderboard = new TextButton("Leaderboard", gameInstance.getSkin());
        leaderboard.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameInstance.setScreen(new LeaderboardScreen(gameInstance));
            }
        });
        root.add(leaderboard).fill().align(Align.center);

        root.row();

        TextButton exit = new TextButton("Exit", gameInstance.getSkin());
        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        root.add(exit).fill().pad(5).align(Align.center);

        root.setDebug(gameInstance.isDebugMode(), true);
    }

    /**
     * Clears the screen and renders the stage.
     *
     * @param v time delta since last frame
     */
    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(1f, 0.992f, 0.816f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // make the things on the stage act and render
        stage.act();
        stage.draw();
    }

    /**
     * Updates the viewport and centers the camera.
     *
     * @param i new window width
     * @param i1 new window height
     */
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
        // Music is managed by CritterParade, don't stop it here
    }

    @Override
    public void dispose() {
        logoTexture.dispose();
    }
}
