package com.roachstudios.critterparade.minigames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.roachstudios.critterparade.CritterParade;
import com.roachstudios.critterparade.Player;

/**
 * A mini game where all players simultaneously catch falling stars.
 * Each player has their own color-coded stars to catch in a shared field.
 * The player with the most stars at the end wins.
 */
public class CatchObjectsMiniGame extends MiniGame {

    /** The display name for this mini game. */
    public static final String NAME = "Catching Stars";
    /** Instructions explaining how to play this mini game. */
    public static final String INSTRUCTIONS = 
        "Catch YOUR colored stars to score!\n\n" +
        "Use your DIRECTIONAL inputs to move.\n" +
        "Only YOUR stars count - watch the colors!";

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
        // Find the player's index and return their score (stars caught)
        Player[] players = getPlayers();
        for (int i = 0; i < players.length; i++) {
            if (players[i] == player) {
                return scores[i];
            }
        }
        return -1f;
    }

    private Texture backgroundTex;
    private Texture fallingObjectTex;

    private final float playerSize = 1.0f;
    private final float starSize = 1.0f;
    
    // Per-player falling star positions and speeds
    private float[] fallingX;
    private float[] fallingY;
    private float[] fallingSpeed;
    
    // Per-player scores
    private int[] scores;
    
    // Player colors for star tinting
    private static final Color[] PLAYER_COLORS = {
        new Color(1.0f, 1.0f, 0.2f, 1f),  // Player 1: Yellow
        new Color(1.0f, 0.2f, 0.2f, 1f),  // Player 2: Red
        new Color(0.2f, 0.9f, 0.2f, 1f),  // Player 3: Green
        new Color(1.0f, 0.6f, 0.1f, 1f),  // Player 4: Orange
        new Color(0.608f, 0.678f, 0.718f, 1f),  // Player 5: Mouse (#9BADB7)
        new Color(0.6f, 0.3f, 0.1f, 1f)   // Player 6: Brown
    };
    
    // Game timing
    private float gameTimer = 0f;
    private static final float GAME_DURATION = 30f; // 30 seconds
    
    private boolean gameCompleted = false;

    public CatchObjectsMiniGame(CritterParade game) {
        super(game);
        
        backgroundTex = new Texture("MiniGames/CatchObjects/night_sky.png");
        fallingObjectTex = new Texture("MiniGames/CatchObjects/star.png");
        
        int playerCount = getPlayerCount();
        
        scores = new int[playerCount];
        fallingX = new float[playerCount];
        fallingY = new float[playerCount];
        fallingSpeed = new float[playerCount];
        
        // Initialize all stars
        for (int i = 0; i < playerCount; i++) {
            scores[i] = 0;
            resetFallingStar(i);
        }
    }

    @Override
    public void show() {
        game.viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        
        // Reset game state
        gameTimer = 0f;
        gameCompleted = false;
        
        int playerCount = getPlayerCount();
        float worldWidth = game.viewport.getWorldWidth();
        
        // Set up player positions spread across the bottom
        Player[] players = getPlayers();
        float spacing = worldWidth / (playerCount + 1);
        
        for (int i = 0; i < playerCount; i++) {
            Player player = players[i];
            player.setSpriteSize(playerSize);
            
            // Position players evenly spaced at the bottom
            float startX = spacing * (i + 1) - playerSize / 2f;
            player.getSprite().setPosition(startX, 1f);
            
            scores[i] = 0;
            resetFallingStar(i);
        }
    }

    @Override
    public void render(float delta) {
        if (!gameCompleted) {
            gameTimer += delta;
            
            if (gameTimer >= GAME_DURATION) {
                endGame();
                return;
            }
            
            input();
            logic(delta);
        }
        draw();
    }

    /**
     * Handles input for all players simultaneously.
     * Players can move freely across the entire screen.
     */
    private void input() {
        float speed = 5f;
        float delta = Gdx.graphics.getDeltaTime();
        
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();
        
        Player[] players = getPlayers();
        for (Player player : players) {
            if (player.isPressingLeft()) {
                player.getSprite().translateX(-speed * delta);
            }
            if (player.isPressingRight()) {
                player.getSprite().translateX(speed * delta);
            }
            if (player.isPressingUp()) {
                player.getSprite().translateY(speed * delta);
            }
            if (player.isPressingDown()) {
                player.getSprite().translateY(-speed * delta);
            }
            
            // Clamp to screen boundaries
            float x = player.getSprite().getX();
            float y = player.getSprite().getY();
            player.getSprite().setX(MathUtils.clamp(x, 0, worldWidth - playerSize));
            player.getSprite().setY(MathUtils.clamp(y, 0, worldHeight - playerSize));
        }
    }

    /**
     * Updates falling stars and checks for catches.
     * Each player can only catch their own colored star.
     */
    private void logic(float delta) {
        Player[] players = getPlayers();
        
        for (int i = 0; i < players.length; i++) {
            // Move this player's star down
            fallingY[i] -= fallingSpeed[i] * delta;
            
            Player player = players[i];
            float px = player.getSprite().getX();
            float py = player.getSprite().getY();
            float pw = player.getSprite().getWidth();
            float ph = player.getSprite().getHeight();
            
            // Check if THIS player catches THEIR star
            boolean caughtHoriz = fallingX[i] + starSize > px && fallingX[i] < px + pw;
            boolean caughtVert = fallingY[i] <= py + ph && fallingY[i] + starSize >= py;
            
            if (caughtHoriz && caughtVert) {
                scores[i]++;
                resetFallingStar(i);
            } else if (fallingY[i] < -starSize) {
                // Star missed, spawn a new one
                resetFallingStar(i);
            }
        }
    }

    /**
     * Draws the game state with color-coded stars.
     */
    private void draw() {
        ScreenUtils.clear(0, 0, 0, 1);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        game.batch.begin();

        // Draw background
        game.batch.draw(backgroundTex, 0, 0, worldWidth, worldHeight);
        
        Player[] players = getPlayers();
        int playerCount = players.length;
        
        // Draw all falling stars with player colors and black outlines
        float outlineSize = 0.15f; // Outline thickness
        for (int i = 0; i < playerCount; i++) {
            float x = fallingX[i];
            float y = fallingY[i];
            
            // Draw black outline (slightly larger star behind)
            game.batch.setColor(Color.BLACK);
            game.batch.draw(fallingObjectTex, 
                x - outlineSize, y - outlineSize, 
                starSize + outlineSize * 2, starSize + outlineSize * 2);
            
            // Draw colored star on top
            Color starColor = getPlayerColor(i);
            game.batch.setColor(starColor);
            game.batch.draw(fallingObjectTex, x, y, starSize, starSize);
        }
        
        // Reset color for player sprites
        game.batch.setColor(Color.WHITE);
        
        // Draw all player sprites
        for (Player player : players) {
            player.getSprite().draw(game.batch);
        }
        
        // Scale font for 16x9 viewport (font is sized for 640x360 menu viewport)
        game.font.getData().setScale(16f / 640f);
        
        // Draw color-coded score labels at the top
        float labelSpacing = worldWidth / (playerCount + 1);
        for (int i = 0; i < playerCount; i++) {
            Color playerColor = getPlayerColor(i);
            game.font.setColor(playerColor);
            
            String scoreText = "P" + (i + 1) + ": " + scores[i];
            float labelX = labelSpacing * (i + 1) - 0.5f;
            game.font.draw(game.batch, scoreText, labelX, worldHeight - 0.3f);
        }
        
        // Reset font color and draw timer
        game.font.setColor(Color.WHITE);
        int timeLeft = (int) Math.ceil(GAME_DURATION - gameTimer);
        game.font.draw(
            game.batch,
            "Time: " + timeLeft,
            worldWidth / 2f - 0.5f,
            worldHeight - 0.8f
        );

        game.batch.end();
    }

    /**
     * Gets the color for a player based on their index.
     */
    private Color getPlayerColor(int playerIndex) {
        if (playerIndex >= 0 && playerIndex < PLAYER_COLORS.length) {
            return PLAYER_COLORS[playerIndex];
        }
        return Color.WHITE;
    }

    /**
     * Resets a falling star to a random position at the top of the screen.
     */
    private void resetFallingStar(int playerIndex) {
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();
        
        fallingX[playerIndex] = MathUtils.random(0f, worldWidth - starSize);
        fallingY[playerIndex] = worldHeight + MathUtils.random(0f, 2f); // Stagger spawns
        fallingSpeed[playerIndex] = MathUtils.random(3f, 6f);
    }

    /**
     * Ends the game and determines placements based on scores.
     */
    private void endGame() {
        gameCompleted = true;
        onGameComplete(makePlacementArray());
    }

    /**
     * Creates the placement array sorted by score (highest first).
     */
    private Player[] makePlacementArray() {
        int playerCount = getPlayerCount();
        Player[] players = getPlayers();
        Player[] ordered = new Player[playerCount];

        // Create index array for sorting
        Integer[] idx = new Integer[playerCount];
        for (int i = 0; i < playerCount; i++) {
            idx[i] = i;
        }

        // Sort by score descending
        java.util.Arrays.sort(idx, (a, b) -> Integer.compare(scores[b], scores[a]));

        for (int i = 0; i < playerCount; i++) {
            ordered[i] = players[idx[i]];
        }

        return ordered;
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        if (backgroundTex != null) backgroundTex.dispose();
        if (fallingObjectTex != null) fallingObjectTex.dispose();
    }
}
