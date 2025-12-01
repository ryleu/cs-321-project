package com.roachstudios.critterparade.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.roachstudios.critterparade.CritterParade;
import com.roachstudios.critterparade.LeaderboardManager;
import com.roachstudios.critterparade.MiniGameScore;

import java.util.List;

/**
 * Displays the high score leaderboards for all minigames.
 * Shows the top scores for each minigame in a tabbed or scrollable layout.
 */
public class LeaderboardScreen implements Screen {
    
    private final CritterParade gameInstance;
    private final Stage stage;
    
    /** Index of the currently displayed minigame tab. */
    private int currentTabIndex = 0;
    
    /** Names of all minigames for tab navigation. */
    private static final String[] MINIGAME_NAMES = {
        "Simple Racer",
        "Dodgeball",
        "Catching Stars"
    };
    
    /**
     * Constructs the leaderboard screen.
     *
     * @param gameInstance shared game instance used for navigation and skin
     */
    public LeaderboardScreen(CritterParade gameInstance) {
        this.gameInstance = gameInstance;
        stage = new Stage(new FitViewport(640, 360));
        Gdx.input.setInputProcessor(stage);
    }
    
    @Override
    public void show() {
        buildUI();
    }
    
    /**
     * Builds the leaderboard UI with tabs for each minigame.
     */
    private void buildUI() {
        stage.clear();
        
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        
        // Title
        Label title = new Label("LEADERBOARDS", gameInstance.getSkin());
        title.setFontScale(1.5f);
        root.add(title).colspan(3).padBottom(15);
        root.row();
        
        // Tab buttons row
        Table tabRow = new Table();
        for (int i = 0; i < MINIGAME_NAMES.length; i++) {
            final int tabIndex = i;
            String tabName = MINIGAME_NAMES[i];
            TextButton tabButton = new TextButton(tabName, gameInstance.getSkin());
            
            // Highlight current tab
            if (i == currentTabIndex) {
                tabButton.setColor(1f, 0.8f, 0.3f, 1f); // Gold highlight
            }
            
            tabButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    currentTabIndex = tabIndex;
                    buildUI(); // Rebuild UI with new tab
                }
            });
            tabRow.add(tabButton).padRight(10).minWidth(100);
        }
        root.add(tabRow).colspan(3).padBottom(10);
        root.row();
        
        // Content area for scores
        Table scoresTable = new Table();
        scoresTable.top();
        buildScoresTable(scoresTable, MINIGAME_NAMES[currentTabIndex]);
        
        ScrollPane scrollPane = new ScrollPane(scoresTable, gameInstance.getSkin());
        scrollPane.setFadeScrollBars(false);
        root.add(scrollPane).colspan(3).expand().fill().pad(10);
        root.row();
        
        // Back button
        TextButton backButton = new TextButton("Back to Menu", gameInstance.getSkin());
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameInstance.setScreen(new MainMenu(gameInstance));
            }
        });
        root.add(backButton).colspan(3).padTop(10);
        
        root.setDebug(gameInstance.isDebugMode(), true);
    }
    
    /**
     * Populates the scores table with leaderboard entries for a minigame.
     *
     * @param table the table to populate
     * @param minigameName the name of the minigame
     */
    private void buildScoresTable(Table table, String minigameName) {
        table.clear();
        
        LeaderboardManager leaderboard = gameInstance.getLeaderboardManager();
        if (leaderboard == null) {
            Label noData = new Label("Leaderboard unavailable", gameInstance.getSkin());
            table.add(noData);
            return;
        }
        
        List<MiniGameScore> scores = leaderboard.getScores(minigameName);
        
        if (scores.isEmpty()) {
            Label noScores = new Label("No scores yet!\nPlay some minigames to set records.", gameInstance.getSkin());
            noScores.setAlignment(Align.center);
            table.add(noScores).pad(20);
            return;
        }
        
        // Header row
        Label rankHeader = new Label("Rank", gameInstance.getSkin());
        Label playerHeader = new Label("Player", gameInstance.getSkin());
        Label scoreHeader = new Label("Score", gameInstance.getSkin());
        
        rankHeader.setFontScale(0.9f);
        playerHeader.setFontScale(0.9f);
        scoreHeader.setFontScale(0.9f);
        
        table.add(rankHeader).width(60).padRight(10);
        table.add(playerHeader).width(150).padRight(10);
        table.add(scoreHeader).width(100);
        table.row();
        
        // Divider
        Label divider = new Label("─────────────────────────────", gameInstance.getSkin());
        divider.setFontScale(0.7f);
        table.add(divider).colspan(3).padBottom(5);
        table.row();
        
        // Score entries
        int rank = 1;
        for (MiniGameScore score : scores) {
            String rankText = getRankText(rank);
            String playerName = score.getPlayerName();
            String scoreText = leaderboard.formatScore(minigameName, score.getScoreValue());
            
            Label rankLabel = new Label(rankText, gameInstance.getSkin());
            Label playerLabel = new Label(playerName, gameInstance.getSkin());
            Label scoreLabel = new Label(scoreText, gameInstance.getSkin());
            
            // Gold, silver, bronze colors for top 3
            if (rank == 1) {
                rankLabel.setColor(1f, 0.84f, 0f, 1f);   // Gold
                playerLabel.setColor(1f, 0.84f, 0f, 1f);
                scoreLabel.setColor(1f, 0.84f, 0f, 1f);
            } else if (rank == 2) {
                rankLabel.setColor(0.75f, 0.75f, 0.75f, 1f);   // Silver
                playerLabel.setColor(0.75f, 0.75f, 0.75f, 1f);
                scoreLabel.setColor(0.75f, 0.75f, 0.75f, 1f);
            } else if (rank == 3) {
                rankLabel.setColor(0.8f, 0.5f, 0.2f, 1f);   // Bronze
                playerLabel.setColor(0.8f, 0.5f, 0.2f, 1f);
                scoreLabel.setColor(0.8f, 0.5f, 0.2f, 1f);
            }
            
            table.add(rankLabel).width(60).padRight(10);
            table.add(playerLabel).width(150).padRight(10);
            table.add(scoreLabel).width(100);
            table.row();
            
            rank++;
        }
    }
    
    /**
     * Gets a formatted rank text with suffix (1st, 2nd, 3rd, etc.).
     *
     * @param rank the numeric rank
     * @return formatted rank string
     */
    private String getRankText(int rank) {
        String suffix;
        if (rank >= 11 && rank <= 13) {
            suffix = "th";
        } else {
            switch (rank % 10) {
                case 1: suffix = "st"; break;
                case 2: suffix = "nd"; break;
                case 3: suffix = "rd"; break;
                default: suffix = "th"; break;
            }
        }
        return rank + suffix;
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 0.992f, 0.816f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.act(delta);
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

