package com.roachstudios.critterparade.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import com.roachstudios.critterparade.CritterParade;
import com.roachstudios.critterparade.Player;

/**
 * Displays the victory screen when a player wins the board game
 * by collecting 5 fruits.
 */
public class VictoryScreen implements Screen {
    private final CritterParade gameInstance;
    private final Stage stage;
    private final Player winner;

    /**
     * Constructs the victory screen.
     *
     * @param gameInstance shared game instance used for navigation and skin
     * @param winner the player who won the game
     */
    public VictoryScreen(CritterParade gameInstance, Player winner) {
        this.gameInstance = gameInstance;
        this.winner = winner;
        stage = new Stage(new FitViewport(640, 360));
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        Table root = new Table();
        stage.addActor(root);
        root.setFillParent(true);

        // Congratulations title
        Label title = new Label("Congratulations!", gameInstance.skin);
        title.setAlignment(Align.center);
        root.add(title).expandX().fillX();

        root.row();

        // Winner announcement
        String winnerText = winner != null ? winner.getName() + " wins!" : "Game Over!";
        Label winnerLabel = new Label(winnerText, gameInstance.skin);
        winnerLabel.setAlignment(Align.center);
        root.add(winnerLabel).expandX().fillX().padTop(20);

        root.row();

        // Stats display
        if (winner != null) {
            Label statsLabel = new Label(
                "Fruits: " + winner.getFruit() + " | Crumbs: " + winner.getCrumbs() + " | Wins: " + winner.getWins(),
                gameInstance.skin
            );
            statsLabel.setAlignment(Align.center);
            root.add(statsLabel).expandX().fillX().padTop(10);
            root.row();
        }

        // Return to main menu button
        TextButton menuButton = new TextButton("Return to Main Menu", gameInstance.skin);
        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameInstance.setScreen(new MainMenu(gameInstance));
            }
        });
        root.add(menuButton).padTop(30);

        root.setDebug(gameInstance.isDebugMode(), true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 0.992f, 0.816f, 1f);
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
        stage.dispose();
    }
}

