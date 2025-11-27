package com.roachstudios.critterparade.gameboards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.roachstudios.critterparade.CritterParade;
import com.roachstudios.critterparade.NamedSupplier;
import com.roachstudios.critterparade.Player;
import com.roachstudios.critterparade.menus.MiniGameInstructionScreen;
import com.roachstudios.critterparade.minigames.MiniGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Picnic Pond board - a Mario Party-like experience where players roll dice,
 * move around the board, and trigger tile effects.
 * 
 * Green tiles award crumbs, red tiles trigger minigames, blue tiles do nothing.
 */
public class PicnicPondBoard extends GameBoard {
    
    public static final String NAME = "Picnic Pond";
    private static final int CRUMBS_REWARD = 3;
    private static final int DIE_MIN = 1;
    private static final int DIE_MAX = 6;
    
    @Override
    public String getName() {
        return NAME;
    }
    
    private final CritterParade gameInstance;
    private Texture backgroundTex;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private GlyphLayout glyphLayout;
    private Random random;
    
    // Board data
    private ArrayList<BoardTile> tiles;
    private int startTileIndex = 0;
    
    // Player board state
    private int[] playerTileIndex;      // Current tile index for each player
    private BoardTile[] playerCameFrom; // The tile each player came from (for junctions)
    
    // Turn state
    private int currentPlayerIndex = 0;
    private GameState state = GameState.WAITING_FOR_ROLL;
    private int dieResult = 0;
    private int movesRemaining = 0;
    
    // Junction choice state
    private List<BoardTile> junctionOptions;
    private int selectedJunctionIndex = 0;
    
    // Animation state
    private float moveTimer = 0;
    private static final float MOVE_DELAY = 0.3f;
    
    // Message display
    private String statusMessage = "";
    private float messageTimer = 0;
    
    // Track if we need to end turn after returning from minigame
    private boolean returningFromMinigame = false;
    
    private enum GameState {
        WAITING_FOR_ROLL,
        ROLLING,
        MOVING,
        CHOOSING_DIRECTION,
        TILE_EFFECT,
        STARTING_MINIGAME
    }

    public PicnicPondBoard(CritterParade gameInstance) {
        this.gameInstance = gameInstance;
        this.random = new Random();
        this.tiles = new ArrayList<>();
        this.junctionOptions = new ArrayList<>();
        
        createBoardTiles();
        
        // Initialize player positions
        int playerCount = gameInstance.getNumPlayers();
        playerTileIndex = new int[playerCount];
        playerCameFrom = new BoardTile[playerCount];
        
        for (int i = 0; i < playerCount; i++) {
            playerTileIndex[i] = startTileIndex;
            playerCameFrom[i] = null;
        }
    }

    /**
     * Creates the board tiles based on the background image dot positions.
     * Positions are percentages of screen dimensions (0-1).
     */
    private void createBoardTiles() {
        // Create tiles based on the background image
        // Coordinates are (x%, y%) where y is from bottom (libGDX convention)
        // Colors: GREEN (crumbs), RED (minigame), BLUE (nothing)
        
        // === TOP EDGE (on blanket) ===
        tiles.add(new BoardTile(0, 0.10f, 0.87f, BoardTile.Type.GREEN));  // Start tile
        tiles.add(new BoardTile(1, 0.16f, 0.83f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(2, 0.20f, 0.78f, BoardTile.Type.RED));
        tiles.add(new BoardTile(3, 0.14f, 0.68f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(4, 0.09f, 0.60f, BoardTile.Type.BLUE));
        
        // === LEFT SIDE (going down) ===
        tiles.add(new BoardTile(5, 0.06f, 0.50f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(6, 0.08f, 0.40f, BoardTile.Type.GREEN));
        tiles.add(new BoardTile(7, 0.12f, 0.30f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(8, 0.18f, 0.22f, BoardTile.Type.BLUE));
        
        // === BOTTOM EDGE (left to right) ===
        tiles.add(new BoardTile(9, 0.25f, 0.17f, BoardTile.Type.RED));
        tiles.add(new BoardTile(10, 0.33f, 0.14f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(11, 0.40f, 0.18f, BoardTile.Type.GREEN));
        tiles.add(new BoardTile(12, 0.48f, 0.22f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(13, 0.55f, 0.18f, BoardTile.Type.BLUE));
        
        // === POND AREA (bottom right) ===
        tiles.add(new BoardTile(14, 0.62f, 0.14f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(15, 0.70f, 0.12f, BoardTile.Type.RED));
        tiles.add(new BoardTile(16, 0.78f, 0.15f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(17, 0.85f, 0.20f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(18, 0.90f, 0.28f, BoardTile.Type.GREEN));
        
        // === RIGHT SIDE (going up) ===
        tiles.add(new BoardTile(19, 0.92f, 0.38f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(20, 0.88f, 0.48f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(21, 0.82f, 0.55f, BoardTile.Type.RED));
        tiles.add(new BoardTile(22, 0.78f, 0.65f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(23, 0.85f, 0.72f, BoardTile.Type.GREEN));
        tiles.add(new BoardTile(24, 0.80f, 0.80f, BoardTile.Type.BLUE));
        
        // === TOP RIGHT (back toward start) ===
        tiles.add(new BoardTile(25, 0.72f, 0.85f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(26, 0.63f, 0.84f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(27, 0.55f, 0.82f, BoardTile.Type.RED));
        tiles.add(new BoardTile(28, 0.47f, 0.85f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(29, 0.38f, 0.87f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(30, 0.30f, 0.84f, BoardTile.Type.GREEN));
        tiles.add(new BoardTile(31, 0.24f, 0.80f, BoardTile.Type.BLUE));
        
        // === MIDDLE PATHS (cross paths through island) ===
        // Upper middle path
        tiles.add(new BoardTile(32, 0.28f, 0.70f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(33, 0.35f, 0.65f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(34, 0.45f, 0.62f, BoardTile.Type.RED));
        tiles.add(new BoardTile(35, 0.55f, 0.58f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(36, 0.65f, 0.55f, BoardTile.Type.BLUE));
        
        // Lower middle path  
        tiles.add(new BoardTile(37, 0.30f, 0.45f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(38, 0.40f, 0.40f, BoardTile.Type.GREEN));
        tiles.add(new BoardTile(39, 0.50f, 0.35f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(40, 0.60f, 0.32f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(41, 0.70f, 0.35f, BoardTile.Type.RED));
        
        // Vertical connectors
        tiles.add(new BoardTile(42, 0.35f, 0.52f, BoardTile.Type.BLUE));  // Connects upper/lower middle
        tiles.add(new BoardTile(43, 0.55f, 0.45f, BoardTile.Type.BLUE));  // Connects paths
        
        // === DEFINE CONNECTIONS ===
        // Outer loop (clockwise)
        connect(0, 1);
        connect(1, 2);
        connect(2, 3);
        connect(3, 4);
        connect(4, 5);
        connect(5, 6);
        connect(6, 7);
        connect(7, 8);
        connect(8, 9);
        connect(9, 10);
        connect(10, 11);
        connect(11, 12);
        connect(12, 13);
        connect(13, 14);
        connect(14, 15);
        connect(15, 16);
        connect(16, 17);
        connect(17, 18);
        connect(18, 19);
        connect(19, 20);
        connect(20, 21);
        connect(21, 22);
        connect(22, 23);
        connect(23, 24);
        connect(24, 25);
        connect(25, 26);
        connect(26, 27);
        connect(27, 28);
        connect(28, 29);
        connect(29, 30);
        connect(30, 31);
        connect(31, 2);  // Close the outer loop
        
        // Upper middle path connections
        connect(3, 32);   // Junction from outer loop
        connect(32, 33);
        connect(33, 42);  // Junction
        connect(42, 34);
        connect(34, 35);
        connect(35, 36);
        connect(36, 22);  // Reconnect to outer loop
        
        // Lower middle path connections
        connect(7, 37);   // Junction from outer loop
        connect(37, 42);  // Junction
        connect(42, 38);
        connect(38, 39);
        connect(39, 43);  // Junction
        connect(43, 40);
        connect(40, 41);
        connect(41, 20);  // Reconnect to outer loop
        
        // Vertical connections
        connect(34, 43);  // Connect upper and lower paths
        connect(12, 39);  // Connect bottom to middle
    }
    
    /**
     * Creates a bidirectional connection between two tiles.
     */
    private void connect(int tileA, int tileB) {
        tiles.get(tileA).addNeighbor(tiles.get(tileB));
        tiles.get(tileB).addNeighbor(tiles.get(tileA));
    }

    @Override
    public void show() {
        // Register this board as active so we can return to it after minigames
        gameInstance.setActiveBoard(this);
        
        // Only initialize resources if not already done
        if (backgroundTex == null) {
            backgroundTex = new Texture("board/PicnicPond/background.png");
        }
        if (shapeRenderer == null) {
            shapeRenderer = new ShapeRenderer();
        }
        if (font == null) {
            font = new BitmapFont();
            font.setUseIntegerPositions(false);
            font.getData().setScale(0.05f);
        }
        if (glyphLayout == null) {
            glyphLayout = new GlyphLayout();
        }
        
        // If returning from minigame, advance to next turn
        if (returningFromMinigame) {
            returningFromMinigame = false;
            endTurn();
        } else {
            updateStatusMessage();
        }
    }

    @Override
    public void render(float delta) {
        handleInput();
        update(delta);
        draw();
    }
    
    private void handleInput() {
        Player currentPlayer = gameInstance.getPlayers()[currentPlayerIndex];
        
        switch (state) {
            case WAITING_FOR_ROLL:
                // Current player presses action to roll
                if (currentPlayer.justPressedAction()) {
                    rollDie();
                }
                break;
                
            case CHOOSING_DIRECTION:
                // Navigate junction options
                if (currentPlayer.justPressedLeft() || currentPlayer.justPressedUp()) {
                    selectedJunctionIndex = (selectedJunctionIndex - 1 + junctionOptions.size()) % junctionOptions.size();
                }
                if (currentPlayer.justPressedRight() || currentPlayer.justPressedDown()) {
                    selectedJunctionIndex = (selectedJunctionIndex + 1) % junctionOptions.size();
                }
                if (currentPlayer.justPressedAction()) {
                    // Confirm direction choice
                    BoardTile chosenTile = junctionOptions.get(selectedJunctionIndex);
                    moveToTile(chosenTile);
                }
                break;
                
            default:
                break;
        }
    }
    
    private void rollDie() {
        dieResult = random.nextInt(DIE_MAX - DIE_MIN + 1) + DIE_MIN;
        movesRemaining = dieResult;
        state = GameState.MOVING;
        statusMessage = gameInstance.getPlayers()[currentPlayerIndex].getName() + " rolled a " + dieResult + "!";
        messageTimer = 1.0f;
        moveTimer = MOVE_DELAY;
    }
    
    private void update(float delta) {
        // Update message timer
        if (messageTimer > 0) {
            messageTimer -= delta;
        }
        
        switch (state) {
            case MOVING:
                moveTimer -= delta;
                if (moveTimer <= 0) {
                    attemptMove();
                }
                break;
                
            case TILE_EFFECT:
                // Wait for message to display, then proceed
                if (messageTimer <= 0) {
                    endTurn();
                }
                break;
                
            case STARTING_MINIGAME:
                if (messageTimer <= 0) {
                    startRandomMinigame();
                }
                break;
                
            default:
                break;
        }
    }
    
    private void attemptMove() {
        if (movesRemaining <= 0) {
            // Done moving, apply tile effect
            applyTileEffect();
            return;
        }
        
        BoardTile currentTile = tiles.get(playerTileIndex[currentPlayerIndex]);
        BoardTile cameFrom = playerCameFrom[currentPlayerIndex];
        List<BoardTile> nextOptions = currentTile.getNextTiles(cameFrom);
        
        if (nextOptions.isEmpty()) {
            // Dead end - shouldn't happen with proper connections
            applyTileEffect();
            return;
        }
        
        if (nextOptions.size() == 1) {
            // Only one way to go
            moveToTile(nextOptions.get(0));
        } else {
            // Junction - player must choose
            state = GameState.CHOOSING_DIRECTION;
            junctionOptions.clear();
            junctionOptions.addAll(nextOptions);
            selectedJunctionIndex = 0;
            statusMessage = "Choose direction! (Left/Right to select, Action to confirm)";
        }
    }
    
    private void moveToTile(BoardTile targetTile) {
        BoardTile currentTile = tiles.get(playerTileIndex[currentPlayerIndex]);
        playerCameFrom[currentPlayerIndex] = currentTile;
        playerTileIndex[currentPlayerIndex] = targetTile.getId();
        movesRemaining--;
        moveTimer = MOVE_DELAY;
        
        if (movesRemaining <= 0) {
            state = GameState.MOVING; // Will trigger tile effect on next update
        } else {
            state = GameState.MOVING;
        }
    }
    
    private void applyTileEffect() {
        BoardTile currentTile = tiles.get(playerTileIndex[currentPlayerIndex]);
        Player currentPlayer = gameInstance.getPlayers()[currentPlayerIndex];
        
        switch (currentTile.getType()) {
            case GREEN:
                // Award crumbs
                currentPlayer.addCrumbs(CRUMBS_REWARD);
                statusMessage = currentPlayer.getName() + " got " + CRUMBS_REWARD + " crumbs!";
                messageTimer = 1.5f;
                state = GameState.TILE_EFFECT;
                break;
                
            case RED:
                // Start minigame
                statusMessage = "Minigame time!";
                messageTimer = 1.0f;
                state = GameState.STARTING_MINIGAME;
                break;
                
            case BLUE:
            default:
                // Nothing happens
                statusMessage = "Nothing here...";
                messageTimer = 0.8f;
                state = GameState.TILE_EFFECT;
                break;
        }
    }
    
    private void startRandomMinigame() {
        var minigames = gameInstance.getMiniGames();
        if (!minigames.isEmpty()) {
            int index = random.nextInt(minigames.size());
            NamedSupplier<MiniGame> selectedGame = minigames.get(index);
            returningFromMinigame = true;  // Flag to end turn when we return
            // Go through instruction screen like the menu does
            gameInstance.setScreen(new MiniGameInstructionScreen(gameInstance, selectedGame.supplier()::get));
        } else {
            // No minigames registered, just end turn
            endTurn();
        }
    }
    
    private void endTurn() {
        // Move to next player
        currentPlayerIndex = (currentPlayerIndex + 1) % gameInstance.getNumPlayers();
        state = GameState.WAITING_FOR_ROLL;
        movesRemaining = 0;
        dieResult = 0;
        updateStatusMessage();
    }
    
    private void updateStatusMessage() {
        Player currentPlayer = gameInstance.getPlayers()[currentPlayerIndex];
        statusMessage = currentPlayer.getName() + "'s turn! Press Action to roll.";
    }
    
    private void draw() {
        Gdx.gl.glClearColor(0.2f, 0.6f, 0.3f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        
        // Draw background
        gameInstance.batch.begin();
        gameInstance.batch.draw(backgroundTex, 0, 0, screenWidth, screenHeight);
        gameInstance.batch.end();
        
        // Enable blending for transparent rendering
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        // Draw the board tiles
        drawTiles(screenWidth, screenHeight);
        
        // Draw player sprites on their tiles
        drawPlayers(screenWidth, screenHeight);
        
        // Draw junction selection indicators if choosing direction
        if (state == GameState.CHOOSING_DIRECTION) {
            drawJunctionOptions(screenWidth, screenHeight);
        }
        
        // Draw UI overlay
        drawUI(screenWidth, screenHeight);
    }
    
    private void drawTiles(float screenWidth, float screenHeight) {
        float tileRadius = Math.min(screenWidth, screenHeight) * 0.018f;
        float outlineRadius = tileRadius * 1.3f;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Draw connections between tiles first (as lines)
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 0.8f);
        for (BoardTile tile : tiles) {
            float x1 = tile.getPosX() * screenWidth;
            float y1 = tile.getPosY() * screenHeight;
            for (BoardTile neighbor : tile.getNeighbors()) {
                // Only draw each connection once (when this tile's id < neighbor's id)
                if (tile.getId() < neighbor.getId()) {
                    float x2 = neighbor.getPosX() * screenWidth;
                    float y2 = neighbor.getPosY() * screenHeight;
                    shapeRenderer.rectLine(x1, y1, x2, y2, tileRadius * 0.5f);
                }
            }
        }
        
        // Draw tile circles
        for (BoardTile tile : tiles) {
            float x = tile.getPosX() * screenWidth;
            float y = tile.getPosY() * screenHeight;
            
            // Draw dark outline
            shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1f);
            shapeRenderer.circle(x, y, outlineRadius);
            
            // Draw colored fill based on tile type
            switch (tile.getType()) {
                case GREEN:
                    shapeRenderer.setColor(0.2f, 0.85f, 0.3f, 1f);  // Bright green
                    break;
                case RED:
                    shapeRenderer.setColor(0.9f, 0.25f, 0.3f, 1f);  // Bright red
                    break;
                case BLUE:
                default:
                    shapeRenderer.setColor(0.3f, 0.5f, 0.9f, 1f);   // Bright blue
                    break;
            }
            shapeRenderer.circle(x, y, tileRadius);
        }
        
        shapeRenderer.end();
    }
    
    private void drawPlayers(float screenWidth, float screenHeight) {
        Player[] players = gameInstance.getPlayers();
        float spriteSize = Math.min(screenWidth, screenHeight) * 0.06f;
        
        // First pass: draw selection indicator for current player using ShapeRenderer
        BoardTile currentTile = tiles.get(playerTileIndex[currentPlayerIndex]);
        float cx = currentTile.getPosX() * screenWidth - spriteSize / 2;
        float cy = currentTile.getPosY() * screenHeight - spriteSize / 2;
        
        // Calculate offset for current player
        int currentSameCount = 0;
        for (int j = 0; j < currentPlayerIndex; j++) {
            if (playerTileIndex[j] == playerTileIndex[currentPlayerIndex]) {
                currentSameCount++;
            }
        }
        cx += currentSameCount * spriteSize * 0.3f;
        cy += currentSameCount * spriteSize * 0.3f;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1f, 1f, 0f, 0.5f);
        shapeRenderer.circle(cx + spriteSize / 2, cy + spriteSize / 2, spriteSize * 0.7f);
        shapeRenderer.end();
        
        // Second pass: draw all player sprites using SpriteBatch
        gameInstance.batch.begin();
        
        for (int i = 0; i < players.length; i++) {
            BoardTile tile = tiles.get(playerTileIndex[i]);
            float x = tile.getPosX() * screenWidth - spriteSize / 2;
            float y = tile.getPosY() * screenHeight - spriteSize / 2;
            
            // Offset slightly for multiple players on same tile
            int sameCount = 0;
            for (int j = 0; j < i; j++) {
                if (playerTileIndex[j] == playerTileIndex[i]) {
                    sameCount++;
                }
            }
            x += sameCount * spriteSize * 0.3f;
            y += sameCount * spriteSize * 0.3f;
            
            players[i].getSprite().setSize(spriteSize, spriteSize);
            players[i].getSprite().setPosition(x, y);
            players[i].getSprite().draw(gameInstance.batch);
        }
        
        gameInstance.batch.end();
    }
    
    private void drawJunctionOptions(float screenWidth, float screenHeight) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        float indicatorSize = Math.min(screenWidth, screenHeight) * 0.04f;
        
        for (int i = 0; i < junctionOptions.size(); i++) {
            BoardTile option = junctionOptions.get(i);
            float x = option.getPosX() * screenWidth;
            float y = option.getPosY() * screenHeight;
            
            if (i == selectedJunctionIndex) {
                shapeRenderer.setColor(1f, 1f, 0f, 0.9f);
                shapeRenderer.circle(x, y, indicatorSize);
            } else {
                shapeRenderer.setColor(1f, 1f, 1f, 0.5f);
                shapeRenderer.circle(x, y, indicatorSize * 0.7f);
            }
        }
        
        shapeRenderer.end();
    }
    
    private void drawUI(float screenWidth, float screenHeight) {
        gameInstance.batch.begin();
        
        // Draw status message at top
        font.getData().setScale(screenHeight / 400f);
        font.setColor(Color.WHITE);
        glyphLayout.setText(font, statusMessage);
        
        // Background for text
        gameInstance.batch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f);
        shapeRenderer.rect(0, screenHeight - glyphLayout.height - 30, screenWidth, glyphLayout.height + 30);
        shapeRenderer.end();
        
        gameInstance.batch.begin();
        font.draw(gameInstance.batch, statusMessage, 
            (screenWidth - glyphLayout.width) / 2, 
            screenHeight - 10);
        
        // Draw player stats at bottom
        float statsY = 80;
        float statsX = 20;
        font.getData().setScale(screenHeight / 600f);
        
        Player[] players = gameInstance.getPlayers();
        for (int i = 0; i < players.length; i++) {
            Player p = players[i];
            String stats = p.getName() + ": " + p.getCrumbs() + " crumbs";
            if (i == currentPlayerIndex) {
                font.setColor(Color.YELLOW);
            } else {
                font.setColor(Color.WHITE);
            }
            font.draw(gameInstance.batch, stats, statsX, statsY);
            statsX += screenWidth / players.length;
        }
        
        // Draw die result if rolling
        if (dieResult > 0 && state != GameState.WAITING_FOR_ROLL) {
            font.getData().setScale(screenHeight / 200f);
            font.setColor(Color.WHITE);
            String dieText = "Die: " + dieResult + " (Moves: " + movesRemaining + ")";
            glyphLayout.setText(font, dieText);
            font.draw(gameInstance.batch, dieText, screenWidth - glyphLayout.width - 20, screenHeight - 60);
        }
        
        gameInstance.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // Handled by draw using screen dimensions
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        // Reset sprite sizes to default when leaving board mode
        // This prevents the large pixel-based sizes from affecting minigames
        Player[] players = gameInstance.getPlayers();
        if (players != null) {
            for (Player player : players) {
                player.setSpriteSize(1f);
                player.getSprite().setPosition(0, 0);
            }
        }
    }

    @Override
    public void dispose() {
        if (backgroundTex != null) backgroundTex.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
    }
}
