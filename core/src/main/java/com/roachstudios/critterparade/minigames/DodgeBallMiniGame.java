package com.roachstudios.critterparade.minigames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.roachstudios.critterparade.CritterParade;
import com.roachstudios.critterparade.Player;
import com.roachstudios.critterparade.minigames.minigameprops.DodgeBall;

import java.util.Random;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A survival minigame where players dodge incoming balls from all directions.
 * 
 * <p>Players move freely around the arena while balls spawn from the edges
 * and travel across the screen. Contact with a ball eliminates the player.
 * The last player standing wins.</p>
 * 
 * <p>Ball spawn rate increases over time to ensure the game eventually ends.</p>
 */
public class DodgeBallMiniGame extends MiniGame {
    
    /** The display name for this mini game. */
    public static final String NAME = "Dodgeball";
    
    /** Instructions explaining how to play this mini game. */
    public static final String INSTRUCTIONS = 
        "Don't let the balls touch you!\n\n" +
        "Use your DIRECTIONAL inputs to move around.\n" +
        "Last player standing wins!";
    
    /** Background texture path. */
    private static final String BACKGROUND_PATH = "MiniGames/SimpleRacer/Clouds.png";
    
    /** Elimination marker texture path. */
    private static final String OUT_MARKER_PATH = "MiniGames/DodgeBall/X.png";
    
    /** Player movement speed in world units per second. */
    private static final float PLAYER_SPEED = 4f;
    
    /** Ball movement speed in world units per second. */
    private static final float BALL_SPEED = 4f;
    
    /** Size of player sprites in world units. */
    private static final float PLAYER_SIZE = 1.0f;
    
    /** Random number generator for ball spawning. */
    private final Random random = new Random();
    
    private Texture backgroundTex;
    private Texture playerOutTex;
    
    /** Tracks whether each player has been eliminated. */
    private boolean[] playerEliminated;
    
    /** Ordered list of players as they are eliminated (last place first). */
    private Player[] placement;
    
    /** Number of players still alive (used as index for placement array). */
    private int remainingPlayers;
    
    /** Prevents onGameComplete from being called multiple times. */
    private boolean gameCompleted;
    
    /** Active balls currently on screen. */
    private final List<DodgeBall> activeBalls = new ArrayList<>();
    
    /** Elimination markers for eliminated players. */
    private final List<Sprite> outMarkers = new ArrayList<>();
    
    /** Time elapsed since game start. */
    private float timeElapsed = 0f;
    
    /** Cooldown timer until next ball spawn. */
    private float spawnCooldown = 1f;
    
    /** Survival time for each player (index = player array index). */
    private float[] survivalTimes;
    
    /**
     * Constructs a new Dodgeball mini game.
     *
     * @param game shared game instance providing viewport, batch, and players
     */
    public DodgeBallMiniGame(CritterParade game) {
        super(game);
        
        int playerCount = getPlayerCount();
        placement = new Player[playerCount];
        playerEliminated = new boolean[playerCount];
        survivalTimes = new float[playerCount];
        remainingPlayers = playerCount - 1;
        gameCompleted = false;
        
        backgroundTex = new Texture(BACKGROUND_PATH);
        playerOutTex = new Texture(OUT_MARKER_PATH);
        
        initializePlayerPositions();
    }
    
    /**
     * Sets up initial positions and sizes for all players.
     */
    private void initializePlayerPositions() {
        Player[] players = getPlayers();
        for (int i = 0; i < players.length; i++) {
            Player player = players[i];
            player.setSpriteSize(PLAYER_SIZE);
            player.getSprite().setX(4);
            player.getSprite().setY(4);
            updatePlayerBounds(player);
            playerEliminated[i] = false;
            survivalTimes[i] = -1f;
        }
    }
    
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
        Player[] players = getPlayers();
        for (int i = 0; i < players.length; i++) {
            if (players[i] == player) {
                return survivalTimes[i];
            }
        }
        return -1f;
    }
    
    @Override
    public void show() {
        game.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        
        remainingPlayers = getPlayerCount() - 1;
        gameCompleted = false;
        timeElapsed = 0f;
        activeBalls.clear();
        outMarkers.clear();
        
        for (int i = 0; i < playerEliminated.length; i++) {
            playerEliminated[i] = false;
            placement[i] = null;
            survivalTimes[i] = -1f;
        }
        
        Player[] players = getPlayers();
        for (int i = 0; i < players.length; i++) {
            players[i].setSpriteSize(PLAYER_SIZE);
            players[i].getSprite().setPosition((PLAYER_SIZE * i) + 4, 4);
        }
    }

    @Override
    public void render(float delta) {
        handleInput();
        updateLogic(delta);
        draw();
    }

    /**
     * Reads player inputs and translates them into movement.
     */
    private void handleInput() {
        float delta = Gdx.graphics.getDeltaTime();
        
        Player[] players = getPlayers();
        for (int i = 0; i < players.length; i++) {
            if (!playerEliminated[i]) {
                Player player = players[i];
                if (player.isPressingRight()) {
                    player.getSprite().translateX(PLAYER_SPEED * delta);
                } else if (player.isPressingLeft()) {
                    player.getSprite().translateX(-PLAYER_SPEED * delta);
                }
                if (player.isPressingUp()) {
                    player.getSprite().translateY(PLAYER_SPEED * delta);
                } else if (player.isPressingDown()) {
                    player.getSprite().translateY(-PLAYER_SPEED * delta);
                }
            }
        }
    }
    
    /**
     * Updates game state including player bounds, ball spawning, and collisions.
     *
     * @param delta time since last frame in seconds
     */
    private void updateLogic(float delta) {
        float worldWidth = game.getViewport().getWorldWidth();
        float worldHeight = game.getViewport().getWorldHeight();
        
        Player[] players = getPlayers();
        float playerWidth = players[0].getSprite().getWidth();
        float playerHeight = players[0].getSprite().getHeight();
        
        for (int i = 0; i < players.length; i++) {
            Player player = players[i];
            
            if (!isPlayerEliminated(player)) {
                clampPlayerPosition(player, playerWidth, playerHeight, worldWidth, worldHeight);
            }
            
            updatePlayerBounds(player);
            
            if (checkPlayerHit(player) && !playerEliminated[i]) {
                eliminatePlayer(i, player);
            }
        }
        
        timeElapsed += delta;
        spawnCooldown -= delta;
        
        if (spawnCooldown <= 0) {
            spawnBall();
            updateSpawnCooldown();
        }
        
        updateBalls();
        checkGameComplete();
    }
    
    /**
     * Eliminates a player from the game.
     *
     * @param playerIndex index in the players array
     * @param player the player to eliminate
     */
    private void eliminatePlayer(int playerIndex, Player player) {
        playerEliminated[playerIndex] = true;
        survivalTimes[playerIndex] = timeElapsed;
        placement[remainingPlayers] = player;
        remainingPlayers--;
        
        createOutMarker(player);
        player.getSprite().setPosition(-10, -10);
    }
    
    /**
     * Renders the game state.
     */
    private void draw() {
        ScreenUtils.clear(1f, 0.992f, 0.816f, 1f);
        game.getViewport().apply();
        game.getBatch().setProjectionMatrix(game.getViewport().getCamera().combined);
        game.getBatch().begin();
        
        float worldWidth = game.getViewport().getWorldWidth();
        float worldHeight = game.getViewport().getWorldHeight();
        
        game.getBatch().draw(backgroundTex, 0, 0, worldWidth, worldHeight);
        
        Player[] players = getPlayers();
        for (Player player : players) {
            player.getSprite().draw(game.getBatch());
        }
        
        for (DodgeBall ball : activeBalls) {
            ball.getSprite().draw(game.getBatch());
        }
        
        for (Sprite marker : outMarkers) {
            marker.draw(game.getBatch());
        }
        
        game.getBatch().end();
    }
    
    /**
     * Checks if the game should end and triggers completion.
     */
    private void checkGameComplete() {
        if (!gameCompleted && remainingPlayers == 0) {
            Player winner = null;
            Player[] players = getPlayers();
            for (int i = 0; i < players.length; i++) {
                if (!playerEliminated[i]) {
                    winner = players[i];
                    playerEliminated[i] = true;
                    survivalTimes[i] = timeElapsed;
                    break;
                }
            }
            
            placement[0] = winner;
            remainingPlayers--;
            onGameComplete(placement);
            gameCompleted = true;
        }
    }
    
    @Override
    public void dispose() {
        if (backgroundTex != null) {
            backgroundTex.dispose();
        }
        if (playerOutTex != null) {
            playerOutTex.dispose();
        }
        for (DodgeBall ball : activeBalls) {
            ball.dispose();
        }
    }
    
    /**
     * Creates a new ball at the specified position with the given direction.
     *
     * @param startX starting X position
     * @param startY starting Y position
     * @param direction movement direction (0=up, 1=down, 2=left, 3=right)
     * @return the created DodgeBall
     */
    private DodgeBall createBall(int startX, int startY, int direction) {
        DodgeBall ball = new DodgeBall();
        ball.getSprite().setPosition(startX, startY);
        ball.getBounds().setPosition(startX, startY);
        ball.setDirection(direction);
        return ball;
    }
    
    /**
     * Spawns a ball from a random edge with a random direction.
     */
    private void spawnBall() {
        int direction = random.nextInt(4);
        
        switch (direction) {
            case 0: // Moving up, spawn from bottom
                activeBalls.add(createBall(getRandomSpawnX(), 0, direction));
                break;
            case 1: // Moving down, spawn from top
                activeBalls.add(createBall(getRandomSpawnX(), 8, direction));
                break;
            case 2: // Moving left, spawn from right
                activeBalls.add(createBall(15, getRandomSpawnY(), direction));
                break;
            case 3: // Moving right, spawn from left
                activeBalls.add(createBall(0, getRandomSpawnY(), direction));
                break;
            default:
                break;
        }
    }
    
    /**
     * Updates all ball positions and removes off-screen balls.
     */
    private void updateBalls() {
        float delta = Gdx.graphics.getDeltaTime();
        float speed = BALL_SPEED * delta;
        
        Iterator<DodgeBall> iterator = activeBalls.iterator();
        while (iterator.hasNext()) {
            DodgeBall ball = iterator.next();
            
            switch (ball.getDirection()) {
                case 0: // Up
                    ball.move(0, speed);
                    if (ball.getSprite().getY() > game.getViewport().getWorldHeight()) {
                        iterator.remove();
                    }
                    break;
                case 1: // Down
                    ball.move(0, -speed);
                    if (ball.getSprite().getY() < -1) {
                        iterator.remove();
                    }
                    break;
                case 2: // Left
                    ball.move(-speed, 0);
                    if (ball.getSprite().getX() < -1) {
                        iterator.remove();
                    }
                    break;
                case 3: // Right
                    ball.move(speed, 0);
                    if (ball.getSprite().getX() > game.getViewport().getWorldWidth()) {
                        iterator.remove();
                    }
                    break;
                default:
                    break;
            }
        }
    }
    
    /**
     * Creates an elimination marker at the player's last position.
     *
     * @param player the eliminated player
     */
    private void createOutMarker(Player player) {
        Sprite marker = new Sprite(playerOutTex);
        marker.setSize(1, 1);
        marker.setPosition(player.getSprite().getX(), player.getSprite().getY());
        outMarkers.add(marker);
    }
    
    /**
     * Checks if a player was hit by any ball.
     *
     * @param player the player to check
     * @return true if the player was hit
     */
    private boolean checkPlayerHit(Player player) {
        for (DodgeBall ball : activeBalls) {
            if (player.getBounds().overlaps(ball.getBounds()) && !isPlayerEliminated(player)) {
                activeBalls.remove(ball);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if a player has been eliminated.
     *
     * @param playerInput the player to check
     * @return true if the player is eliminated
     */
    private boolean isPlayerEliminated(Player playerInput) {
        for (Player player : placement) {
            if (playerInput == player) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets a random X spawn position.
     *
     * @return random X coordinate between 1 and 14
     */
    private int getRandomSpawnX() {
        return random.nextInt(14) + 1;
    }
    
    /**
     * Gets a random Y spawn position.
     *
     * @return random Y coordinate between 1 and 7
     */
    private int getRandomSpawnY() {
        return random.nextInt(7) + 1;
    }
    
    /**
     * Updates spawn cooldown based on elapsed time (difficulty scaling).
     */
    private void updateSpawnCooldown() {
        if (timeElapsed < 5) {
            spawnCooldown = 0.6f;
        } else if (timeElapsed < 10) {
            spawnCooldown = 0.5f;
        } else if (timeElapsed < 15) {
            spawnCooldown = 0.4f;
        } else if (timeElapsed < 20) {
            spawnCooldown = 0.3f;
        } else if (timeElapsed < 25) {
            spawnCooldown = 0.2f;
        } else {
            spawnCooldown = 0.1f;
        }
    }
    
    /**
     * Clamps player position within world bounds.
     *
     * @param player the player to clamp
     * @param playerWidth player sprite width
     * @param playerHeight player sprite height
     * @param worldWidth world width
     * @param worldHeight world height
     */
    private void clampPlayerPosition(Player player, float playerWidth, float playerHeight,
                                     float worldWidth, float worldHeight) {
        player.getSprite().setX(MathUtils.clamp(
            player.getSprite().getX(), 
            playerWidth, 
            worldWidth - (2 * playerWidth)
        ));
        player.getSprite().setY(MathUtils.clamp(
            player.getSprite().getY(), 
            playerHeight, 
            worldHeight - (2 * playerHeight)
        ));
    }
    
    /**
     * Updates player collision bounds to match sprite position.
     *
     * @param player the player to update
     */
    private void updatePlayerBounds(Player player) {
        player.getBounds().setPosition(player.getSprite().getX(), player.getSprite().getY());
    }
}
