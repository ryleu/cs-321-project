package com.roachstudios.critterparade.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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
 * Displays the final results of a minigame rush, showing the winner
 * and all player standings sorted by crumbs.
 */
public class RushVictoryScreen implements Screen {
    private final CritterParade gameInstance;
    private final Stage stage;
    private final MiniGameRushController rushController;

    /**
     * Constructs the rush victory screen.
     *
     * @param gameInstance shared game instance used for navigation and skin
     * @param rushController the rush controller with final standings
     */
    public RushVictoryScreen(CritterParade gameInstance, MiniGameRushController rushController) {
        this.gameInstance = gameInstance;
        this.rushController = rushController;
        stage = new Stage(new FitViewport(640, 360));
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        Table root = new Table();
        stage.addActor(root);
        root.setFillParent(true);
        root.pad(20);

        // Title
        Label title = new Label("Minigame Rush Complete!", gameInstance.skin);
        title.setFontScale(1.5f);
        title.setAlignment(Align.center);
        title.setColor(Color.GOLD);
        root.add(title).fillX().padBottom(15);
        root.row();

        // Winner announcement
        Player[] winners = rushController.getWinners();
        String winnerText;
        if (winners.length == 1) {
            winnerText = winners[0].getName() + " wins with " + winners[0].getCrumbs() + " crumbs!";
        } else if (winners.length > 1) {
            StringBuilder sb = new StringBuilder("It's a tie! ");
            for (int i = 0; i < winners.length; i++) {
                if (i > 0) sb.append(i == winners.length - 1 ? " and " : ", ");
                sb.append(winners[i].getName());
            }
            sb.append(" with ").append(winners[0].getCrumbs()).append(" crumbs each!");
            winnerText = sb.toString();
        } else {
            winnerText = "No winner!";
        }
        
        Label winnerLabel = new Label(winnerText, gameInstance.skin);
        winnerLabel.setAlignment(Align.center);
        winnerLabel.setColor(Color.GREEN);
        winnerLabel.setWrap(true);
        root.add(winnerLabel).width(500).fillX().padBottom(20);
        root.row();

        // Divider
        Label divider = new Label("─────────── Final Standings ───────────", gameInstance.skin);
        divider.setColor(Color.GRAY);
        divider.setAlignment(Align.center);
        root.add(divider).fillX().padBottom(10);
        root.row();

        // Final standings
        Player[] standings = rushController.getFinalStandings();
        for (int i = 0; i < standings.length; i++) {
            Player player = standings[i];
            int place = i + 1;
            
            String placeEmoji = switch(place) {
                case 1 -> "🥇";
                case 2 -> "🥈";
                case 3 -> "🥉";
                default -> place + ".";
            };
            
            String standingText = placeEmoji + " " + player.getName() + 
                    " - " + player.getCrumbs() + " crumbs (" + player.getWins() + " wins)";
            
            Label standingLabel = new Label(standingText, gameInstance.skin);
            standingLabel.setAlignment(Align.center);
            
            // Color code top 3
            if (place == 1) {
                standingLabel.setColor(Color.GOLD);
            } else if (place == 2) {
                standingLabel.setColor(Color.LIGHT_GRAY);
            } else if (place == 3) {
                standingLabel.setColor(new Color(0.8f, 0.5f, 0.2f, 1f)); // Bronze
            }
            
            root.add(standingLabel).fillX();
            root.row();
        }

        // Return to main menu button
        TextButton menuButton = new TextButton("Return to Main Menu", gameInstance.skin);
        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameInstance.clearRushController();
                gameInstance.setScreen(new MainMenu(gameInstance));
            }
        });
        root.add(menuButton).padTop(20);

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

