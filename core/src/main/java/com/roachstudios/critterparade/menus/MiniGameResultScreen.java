/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
import com.roachstudios.critterparade.Player;
import com.roachstudios.critterparade.gameboards.PicnicPondBoard;

/**
 * Displays the placements resulting from a mini game and provides a
 * context-sensitive Continue button based on the current game mode.
 * 
 * <p>Automatically continues after a configurable timeout period.</p>
 */
public class MiniGameResultScreen implements Screen{
    private final CritterParade gameInstance;
    private final Stage stage;
    private Player[] placements;
    
    /**
     * Time remaining before auto-continue (in seconds).
     */
    private float autoSkipTimer = 5.0f;
    
    /**
     * Whether the screen has already triggered navigation.
     */
    private boolean hasNavigated = false;
    
    /**
     * Label showing the countdown timer.
     */
    private TextField timerLabel;
    
    /**
     * Constructs the mini game result screen.
     *
     * @param gameInstance shared game instance used for navigation and skin
     * @param results ordered array of players from 1st to last place
     */
    public MiniGameResultScreen(CritterParade gameInstance, Player[] results) {
         this.gameInstance = gameInstance;
         this.placements = results;
        // Use FitViewport for consistent layout across window sizes.
        stage = new Stage(new FitViewport(640, 360));

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    /**
     * Builds a simple list of placements and adds a Continue button whose
     * destination depends on the active game mode.
     */
    public void show() {
        Table root = new Table();
        stage.addActor(root);
        root.setFillParent(true);
        
        TextField title = new TextField("Results:", gameInstance.skin);
        title.setAlignment(Align.center);

        root.add(title).expandX().fillX();
        
        int numPlayers = placements.length;
        for(int i = 0; i < numPlayers; i++){
            root.row();
            
            // Calculate points awarded: floor(5 * (numPlayers - placement) / (numPlayers - 1))
            int placement = i + 1;
            int pointsAwarded = (5 * (numPlayers - placement)) / (numPlayers - 1);
            
            String resultText = placement + ". " + placements[i].getName() + " (+" + pointsAwarded + " crumbs)";
            TextField place = new TextField(resultText, gameInstance.skin);
            place.setAlignment(Align.center);
            root.add(place).expandX().fillX();
        }
        
        // Continue action depends on whether we are in the board flow, practice, or rush.
        if(gameInstance.mode == CritterParade.Mode.BOARD_MODE){
            root.row();
            TextButton changeButton = new TextButton("Continue", gameInstance.skin);
            changeButton.addListener(new ChangeListener() {
                
                
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    if (!hasNavigated) {
                        hasNavigated = true;
                        // Create a fresh board - player state is stored in Player objects
                        gameInstance.setScreen(new PicnicPondBoard(gameInstance));
                    }
                }
            });
            root.add(changeButton);
            
            // Add timer label
            root.row();
            timerLabel = new TextField("Continuing in 5...", gameInstance.skin);
            timerLabel.setAlignment(Align.center);
            root.add(timerLabel).expandX().fillX().padTop(10);
        
            root.setDebug(gameInstance.isDebugMode(), true);
        
        }else if(gameInstance.mode == CritterParade.Mode.PRACTICE_MODE){
            root.row();
            TextButton changeButton = new TextButton("Continue", gameInstance.skin);
            changeButton.addListener(new ChangeListener() {
                
                
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    if (!hasNavigated) {
                        hasNavigated = true;
                        gameInstance.setScreen(new MainMenu(gameInstance));
                    }
                }
            });
            root.add(changeButton);
            
            // Add timer label
            root.row();
            timerLabel = new TextField("Continuing in 5...", gameInstance.skin);
            timerLabel.setAlignment(Align.center);
            root.add(timerLabel).expandX().fillX().padTop(10);
        
            root.setDebug(gameInstance.isDebugMode(), true);
        }else if(gameInstance.mode == CritterParade.Mode.RUSH_MODE){
            // In rush mode, show progress and continue to next minigame or final results
            MiniGameRushController rushController = gameInstance.getRushController();
            
            // Show rush progress
            root.row();
            String progressText = "Minigame " + rushController.getCurrentMinigameNumber() + 
                    " of " + rushController.getTotalMinigameCount() + " complete";
            TextField progressLabel = new TextField(progressText, gameInstance.skin);
            progressLabel.setAlignment(Align.center);
            root.add(progressLabel).expandX().fillX().padTop(10);
            
            // Advance to next minigame
            rushController.advanceToNextMinigame();
            
            root.row();
            String buttonText = rushController.hasNextMinigame() ? "Next Minigame" : "See Final Results";
            TextButton changeButton = new TextButton(buttonText, gameInstance.skin);
            changeButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    if (!hasNavigated) {
                        hasNavigated = true;
                        if (rushController.hasNextMinigame()) {
                            rushController.startCurrentMinigame();
                        } else {
                            rushController.showFinalResults();
                        }
                    }
                }
            });
            root.add(changeButton);
            
            // Add timer label
            root.row();
            timerLabel = new TextField("Continuing in 5...", gameInstance.skin);
            timerLabel.setAlignment(Align.center);
            root.add(timerLabel).expandX().fillX().padTop(10);
        
            root.setDebug(gameInstance.isDebugMode(), true);
        }
        
    }

    @Override
    /**
     * Clears the screen, updates the auto-skip timer, and renders the stage.
     */
    public void render(float f) {
        // Update auto-skip timer
        if (!hasNavigated) {
            autoSkipTimer -= f;
            
            // Update timer label
            int secondsLeft = (int) Math.ceil(autoSkipTimer);
            if (secondsLeft < 0) secondsLeft = 0;
            if (timerLabel != null) {
                timerLabel.setText("Continuing in " + secondsLeft + "...");
            }
            
            // Auto-navigate when timer expires
            if (autoSkipTimer <= 0) {
                hasNavigated = true;
                if (gameInstance.mode == CritterParade.Mode.BOARD_MODE) {
                    gameInstance.setScreen(new PicnicPondBoard(gameInstance));
                } else if (gameInstance.mode == CritterParade.Mode.PRACTICE_MODE) {
                    gameInstance.setScreen(new MainMenu(gameInstance));
                } else if (gameInstance.mode == CritterParade.Mode.RUSH_MODE) {
                    MiniGameRushController rushController = gameInstance.getRushController();
                    if (rushController.hasNextMinigame()) {
                        rushController.startCurrentMinigame();
                    } else {
                        rushController.showFinalResults();
                    }
                }
                return;
            }
        }
        
        Gdx.gl.glClearColor(1f, 0.992f, 0.816f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    /**
     * Updates the viewport and centers the camera.
     */
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
    }
}
