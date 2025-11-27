package com.roachstudios.critterparade.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.roachstudios.critterparade.CritterParade;
import com.roachstudios.critterparade.Player;
import com.roachstudios.critterparade.minigames.MiniGame;

import java.util.function.Supplier;

/**
 * Displays the mini game name and instructions before starting.
 * All players must press their action button to ready up, then a 
 * 3-second countdown begins before the game starts.
 */
public class MiniGameInstructionScreen implements Screen {
    private final CritterParade gameInstance;
    private final Stage stage;
    private final Supplier<MiniGame> miniGameSupplier;
    private final String gameName;
    private final String instructions;
    
    /**
     * Tracks which players have pressed action (ready status).
     */
    private boolean[] playersReady;
    
    /**
     * Labels showing each player's ready status.
     */
    private Label[] playerStatusLabels;
    
    /**
     * Current phase: waiting for players or countdown.
     */
    private enum Phase { WAITING, COUNTDOWN }
    private Phase currentPhase = Phase.WAITING;
    
    /**
     * Time remaining in countdown (seconds).
     */
    private float countdownTimer = 3.0f;
    
    /**
     * Label showing countdown number.
     */
    private Label countdownLabel;
    
    /**
     * Whether we've already started the game.
     */
    private boolean hasStarted = false;

    /**
     * @param gameInstance shared game instance used for navigation and skin
     * @param miniGameSupplier supplier that creates the mini game instance when ready to start
     */
    public MiniGameInstructionScreen(CritterParade gameInstance, Supplier<MiniGame> miniGameSupplier) {
        this.gameInstance = gameInstance;
        this.miniGameSupplier = miniGameSupplier;
        
        // Create a temporary instance to get name and instructions
        MiniGame tempGame = miniGameSupplier.get();
        this.gameName = tempGame.getName();
        this.instructions = tempGame.getInstructions();
        tempGame.dispose();
        
        // Initialize ready status
        int numPlayers = gameInstance.getNumPlayers();
        playersReady = new boolean[numPlayers];
        playerStatusLabels = new Label[numPlayers];

        // Fixed virtual size for consistent layout.
        stage = new Stage(new FitViewport(640, 360));
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        Table root = new Table();
        stage.addActor(root);
        root.setFillParent(true);
        root.pad(20);

        // Title - game name
        Label titleLabel = new Label(gameName, gameInstance.skin);
        titleLabel.setFontScale(2.0f);
        titleLabel.setAlignment(Align.center);
        root.add(titleLabel).fillX().padBottom(20);
        root.row();

        // Instructions
        Label instructionsLabel = new Label(instructions, gameInstance.skin);
        instructionsLabel.setWrap(true);
        instructionsLabel.setAlignment(Align.center);
        root.add(instructionsLabel).width(500).fillX().padBottom(20);
        root.row();
        
        // Divider
        Label divider = new Label("─────────────────────────────────", gameInstance.skin);
        divider.setColor(Color.GRAY);
        divider.setAlignment(Align.center);
        root.add(divider).fillX().padBottom(10);
        root.row();
        
        // Ready prompt
        Label readyPrompt = new Label("Press ACTION to Ready Up!", gameInstance.skin);
        readyPrompt.setColor(Color.YELLOW);
        readyPrompt.setAlignment(Align.center);
        root.add(readyPrompt).fillX().padBottom(10);
        root.row();
        
        // Player ready status labels
        Player[] players = gameInstance.getPlayers();
        for (int i = 0; i < players.length; i++) {
            playerStatusLabels[i] = new Label(players[i].getName() + ": Waiting...", gameInstance.skin);
            playerStatusLabels[i].setColor(Color.GRAY);
            playerStatusLabels[i].setAlignment(Align.center);
            root.add(playerStatusLabels[i]).fillX();
            root.row();
        }
        
        // Countdown label (hidden initially)
        countdownLabel = new Label("", gameInstance.skin);
        countdownLabel.setFontScale(3.0f);
        countdownLabel.setColor(Color.YELLOW);
        countdownLabel.setAlignment(Align.center);
        root.add(countdownLabel).fillX().padTop(20);
        root.row();
        
        // Action key hints
        Label hint = new Label("Action keys: E, Y, O, ], Space, Num9", gameInstance.skin);
        hint.setColor(Color.LIGHT_GRAY);
        hint.setAlignment(Align.center);
        root.add(hint).fillX().padTop(10);

        root.setDebug(gameInstance.isDebugMode(), true);
    }

    @Override
    public void render(float delta) {
        // Check for player input
        if (currentPhase == Phase.WAITING) {
            updateWaitingPhase();
        } else if (currentPhase == Phase.COUNTDOWN) {
            updateCountdownPhase(delta);
        }
        
        Gdx.gl.glClearColor(1f, 0.992f, 0.816f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }
    
    /**
     * Checks for player action input and updates ready status.
     */
    private void updateWaitingPhase() {
        Player[] players = gameInstance.getPlayers();
        boolean allReady = true;
        
        for (int i = 0; i < players.length; i++) {
            if (players[i].justPressedAction()) {
                playersReady[i] = true;
            }
            
            // Update label
            if (playersReady[i]) {
                playerStatusLabels[i].setText(players[i].getName() + ": READY!");
                playerStatusLabels[i].setColor(Color.GREEN);
            } else {
                allReady = false;
            }
        }
        
        if (allReady) {
            currentPhase = Phase.COUNTDOWN;
            countdownTimer = 3.0f;
        }
    }
    
    /**
     * Updates countdown timer and starts game when done.
     */
    private void updateCountdownPhase(float delta) {
        countdownTimer -= delta;
        
        int secondsLeft = (int) Math.ceil(countdownTimer);
        if (secondsLeft < 1) secondsLeft = 1;
        countdownLabel.setText(String.valueOf(secondsLeft));
        
        if (countdownTimer <= 0 && !hasStarted) {
            hasStarted = true;
            
            // Log minigame start
            gameInstance.log("Starting minigame: %s", gameName);
            gameInstance.logMinigameStart(gameName);
            
            // Create a fresh minigame instance and start it
            gameInstance.setScreen(miniGameSupplier.get());
        }
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

