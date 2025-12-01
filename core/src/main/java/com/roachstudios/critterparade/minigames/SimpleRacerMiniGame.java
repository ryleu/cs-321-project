package com.roachstudios.critterparade.minigames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.roachstudios.critterparade.CritterParade;
import com.roachstudios.critterparade.Player;

/**
 * A simple reaction-based horizontal racer: each player advances by pressing
 * their move input. First to cross the finish line wins.
 */
public class SimpleRacerMiniGame extends MiniGame {
    
    /** The display name for this mini game. */
    public static final String NAME = "Simple Racer";
    /** Instructions explaining how to play this mini game. */
    public static final String INSTRUCTIONS = 
        "Race to the finish line!\n\n" +
        "Repeatedly tap your RIGHT input to move forward.\n" +
        "First player to cross the finish line wins!";
    
    @Override
    public String getName() {
        return NAME;
    }
    
    @Override
    public String getInstructions() {
        return INSTRUCTIONS;
    }
    
    @Override
    public float getScoreValue(Player player) {
        // Find the player's index and return their finish time
        Player[] players = getPlayers();
        for (int i = 0; i < players.length; i++) {
            if (players[i] == player) {
                return finishTimes[i];
            }
        }
        return -1f;
    }
    
    private Texture backgroundTex;
    private Texture finishLineTex;
    
    private final float playerSize = 1.0f;
    
    /**
     * Tracks whether each player has crossed the finish line.
     * Index corresponds to player array index (0-based).
     */
    private boolean[] playerFinished;
    
    /**
     * Ordered list of players as they finish (1st place to last).
     */
    private Player[] placement;
    private int finishedCount;
    
    /**
     * Prevents onGameComplete from being called multiple times.
     */
    private boolean gameCompleted;
    
    /**
     * Tracks the finish time for each player (index = player array index).
     * -1 means the player hasn't finished yet.
     */
    private float[] finishTimes;
    
    /**
     * Game clock for tracking elapsed time.
     */
    private float gameTimer;
    
    /**
     * Constructs a new Simple Racer mini game.
     *
     * @param game shared game instance providing viewport, batch, and players
     */
    public SimpleRacerMiniGame(CritterParade game) {
        super(game);
        
        int playerCount = getPlayerCount();
        placement = new Player[playerCount];
        playerFinished = new boolean[playerCount];
        finishTimes = new float[playerCount];
        finishedCount = 0;
        gameCompleted = false;
        gameTimer = 0f;
        
        backgroundTex = new Texture("MiniGames/SimpleRacer/Clouds.png");
        finishLineTex = new Texture("MiniGames/SimpleRacer/FinishLine.png");
        
        // Set up initial positions and sizes for all players
        Player[] players = getPlayers();
        for (int i = 0; i < playerCount; i++) {
            Player player = players[i];
            player.setSpriteSize(playerSize);
            player.getSprite().setX(0);
            player.getSprite().setY(playerSize * i);
            playerFinished[i] = false;
            finishTimes[i] = -1f;
        }
    }

    @Override
    public void show() {
        // Update viewport to current screen size
        game.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        // Reset font scale (other screens may have changed it)
        game.getFont().getData().setScale(1.0f);
        
        // Reset game state in case we're replaying
        finishedCount = 0;
        gameCompleted = false;
        gameTimer = 0f;
        for (int i = 0; i < playerFinished.length; i++) {
            playerFinished[i] = false;
            placement[i] = null;
            finishTimes[i] = -1f;
        }
        
        // Reset positions and sizes when the minigame is shown
        Player[] players = getPlayers();
        for (int i = 0; i < players.length; i++) {
            players[i].setSpriteSize(playerSize);
            players[i].getSprite().setPosition(0, playerSize * i);
        }
    }

    @Override
    public void render(float delta) {
        input();
        logic();
        draw();
    }

    /**
     * Reads player inputs and translates them into movement when allowed.
     */
    private void input() {
        float speed = 16f;
        float delta = Gdx.graphics.getDeltaTime();
        
        Player[] players = getPlayers();
        for (int i = 0; i < players.length; i++) {
            if (!playerFinished[i]) {
                Player player = players[i];
                // Players advance by pressing their right input
                if (player.justPressedRight()) {
                    player.getSprite().translateX(speed * delta);
                }
            }
        }
    }
    
    /**
     * Clamps sprites to the world bounds and records finish order once a
     * player crosses the line at x=14 in world units.
     */
    private void logic() {
        float worldWidth = game.getViewport().getWorldWidth();
        float delta = Gdx.graphics.getDeltaTime();
        
        // Update game timer
        gameTimer += delta;
        
        Player[] players = getPlayers();
        float playerWidth = players[0].getSprite().getWidth();
        float playerHeight = players[0].getSprite().getHeight();
        
        for (int i = 0; i < players.length; i++) {
            Player player = players[i];
            
            // Clamp X position
            player.getSprite().setX(MathUtils.clamp(
                player.getSprite().getX(), 
                0, 
                worldWidth - playerWidth
            ));
            
            // Set Y position based on player lane
            player.getSprite().setY(playerHeight * i);
            
            // Check if player crossed finish line
            if (player.getSprite().getX() >= 14 && !playerFinished[i]) {
                playerFinished[i] = true;
                finishTimes[i] = gameTimer; // Record finish time
                placement[finishedCount] = player;
                finishedCount++;
            }
        }
        
        checkGameComplete();
    }
    
    /**
     * Renders the background, finish line, and all player sprites.
     */
    private void draw() {
        ScreenUtils.clear(1f, 0.992f, 0.816f, 1f);
        game.getViewport().apply();
        game.getBatch().setProjectionMatrix(game.getViewport().getCamera().combined);
        game.getBatch().begin();
        
        float worldWidth = game.getViewport().getWorldWidth();
        float worldHeight = game.getViewport().getWorldHeight();
        
        game.getBatch().draw(backgroundTex, 0, 0, worldWidth, worldHeight);
        // Finish line at x=14 to leave 2 world units of run-up in a 16x9 world.
        game.getBatch().draw(finishLineTex, 14f, 0, 1, worldHeight);
        
        // Draw all player sprites
        Player[] players = getPlayers();
        for (Player player : players) {
            player.getSprite().draw(game.getBatch());
        }
        
        game.getBatch().end();
    }
    
    /**
     * Checks if all players have finished and triggers game completion.
     */
    private void checkGameComplete() {
        if (gameCompleted) {
            return; // Already triggered completion, don't do it again
        }
        
        if (finishedCount == getPlayerCount()) {
            gameCompleted = true;
            
            // Debug output for placements
            StringBuilder out = new StringBuilder("Placements:\n");
            for (int i = 0; i < placement.length; i++) {
                if (placement[i] != null) {
                    out.append(i + 1).append(". ").append(placement[i].getName()).append("\n");
                } else {
                    out.append(i + 1).append(". NULL\n");
                }
            }
            System.out.println(out);
            
            onGameComplete(placement);
        }
    }
    
    @Override
    public void dispose() {
        if (backgroundTex != null) {
            backgroundTex.dispose();
        }
        if (finishLineTex != null) {
            finishLineTex.dispose();
        }
    }
}
