package com.roachstudios.critterparade.gameboards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.roachstudios.critterparade.CritterParade;
import com.roachstudios.critterparade.Player;
import com.roachstudios.critterparade.menus.MiniGameInstructionScreen;
import com.roachstudios.critterparade.menus.VictoryScreen;
import com.roachstudios.critterparade.minigames.MiniGameDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Abstract base class for board-mode screens using a Mario Party-like gameplay style.
 * 
 * <p>Players roll dice, move around the board, and trigger tile effects:
 * <ul>
 *   <li>Green tiles award crumbs</li>
 *   <li>Red tiles trigger minigames</li>
 *   <li>Blue tiles have no effect</li>
 *   <li>Shop tiles allow purchasing fruit with crumbs</li>
 * </ul>
 * 
 * <p>Subclasses must implement:
 * <ul>
 *   <li>{@link #getName()} - the display name for the board</li>
 *   <li>{@link #getBackgroundPath()} - the path to the background texture</li>
 *   <li>{@link #createBoardTiles()} - defines tile positions and connections</li>
 *   <li>{@link #getThemeColors()} - returns board-specific color theme</li>
 * </ul>
 */
public abstract class GameBoard implements Screen {
    
    /** Crumbs awarded when landing on a green tile. */
    protected static final int CRUMBS_REWARD = 3;
    
    /** Minimum dice roll value. */
    protected static final int DIE_MIN = 1;
    
    /** Maximum dice roll value. */
    protected static final int DIE_MAX = 6;
    
    /** Cost in crumbs to purchase one fruit at the shop. */
    protected static final int FRUIT_COST = 10;
    
    /** Number of fruits required to win the game. */
    protected static final int FRUITS_TO_WIN = 5;
    
    /** Number of shop tiles present on the board at any time. */
    protected static final int SHOP_COUNT = 3;
    
    /** Starting tile index for all players. */
    protected static final int START_TILE_INDEX = 0;
    
    /** Delay between movement steps in seconds. */
    protected static final float MOVE_DELAY = 0.3f;
    
    /**
     * Gets the display name for this game board.
     *
     * @return the display name for this game board
     */
    public abstract String getName();
    
    /**
     * Gets the file path to the background texture for this board.
     *
     * @return the path to the background texture asset
     */
    protected abstract String getBackgroundPath();
    
    /**
     * Creates and connects all board tiles. Subclasses should populate the
     * tiles list and establish connections using {@link #connect(int, int)}.
     */
    protected abstract void createBoardTiles();
    
    /**
     * Gets the color theme for this board's visual elements.
     *
     * @return the BoardTheme containing colors for various UI elements
     */
    protected abstract BoardTheme getThemeColors();
    
    /**
     * Color theme for board rendering.
     *
     * @param pathColor color for tile connection lines
     * @param pathOutlineColor color for tile outlines
     * @param shopColor color for the shop tile
     * @param selectionGlowColor color for the current player indicator
     * @param uiBackgroundColor color for UI overlay backgrounds
     * @param clearColor screen clear color
     */
    protected record BoardTheme(
        Color pathColor,
        Color pathOutlineColor,
        Color shopColor,
        Color selectionGlowColor,
        Color uiBackgroundColor,
        Color clearColor
    ) {}
    
    /** Reference to the main game instance. */
    protected final CritterParade gameInstance;
    
    /** Background texture for this board. */
    protected Texture backgroundTex;
    
    /** Shape renderer for geometric drawing. */
    protected ShapeRenderer shapeRenderer;
    
    /** Font for text rendering. */
    protected BitmapFont font;
    
    /** Layout helper for text measurement. */
    protected GlyphLayout glyphLayout;
    
    /** Random number generator for dice rolls and shop placement. */
    protected Random random;
    
    /** Camera for screen-coordinate rendering. */
    protected OrthographicCamera camera;
    
    /** List of all board tiles. */
    protected ArrayList<BoardTile> tiles;
    
    /** Indices of tiles currently serving as shops. */
    protected ArrayList<Integer> shopTileIndices;
    
    /** Current game state. */
    protected GameState state = GameState.WAITING_FOR_ROLL;
    
    /** Result of the last dice roll. */
    protected int dieResult = 0;
    
    /** Remaining moves for the current player. */
    protected int movesRemaining = 0;
    
    /** Available tiles at the current junction. */
    protected List<BoardTile> junctionOptions;
    
    /** Selected junction option index. */
    protected int selectedJunctionIndex = 0;
    
    /** Timer for movement animation. */
    protected float moveTimer = 0;
    
    /** Current status message to display. */
    protected String statusMessage = "";
    
    /** Timer for status message display. */
    protected float messageTimer = 0;
    
    /**
     * Possible states for board game flow.
     */
    protected enum GameState {
        /** Waiting for the current player to roll the dice. */
        WAITING_FOR_ROLL,
        /** Dice is being rolled (animation state). */
        ROLLING,
        /** Player is moving along tiles. */
        MOVING,
        /** Player must choose a direction at a junction. */
        CHOOSING_DIRECTION,
        /** Tile effect is being applied. */
        TILE_EFFECT,
        /** Transitioning to a minigame. */
        STARTING_MINIGAME,
        /** Game has ended, showing winner. */
        GAME_OVER
    }
    
    /**
     * Constructs a new game board.
     *
     * @param gameInstance the main game instance providing shared resources
     */
    protected GameBoard(CritterParade gameInstance) {
        this.gameInstance = gameInstance;
        this.random = new Random();
        this.tiles = new ArrayList<>();
        this.junctionOptions = new ArrayList<>();
        this.shopTileIndices = new ArrayList<>();
        
        createBoardTiles();
        initializeShops();
    }
    
    /**
     * Initializes all shop tiles at random positions.
     */
    protected void initializeShops() {
        shopTileIndices.clear();
        for (int i = 0; i < SHOP_COUNT; i++) {
            addNewShop();
        }
    }
    
    /**
     * Adds a new shop at a random tile, avoiding the start tile and existing shops.
     */
    protected void addNewShop() {
        int newShopIndex;
        do {
            newShopIndex = random.nextInt(tiles.size());
        } while (newShopIndex == START_TILE_INDEX || shopTileIndices.contains(newShopIndex));
        shopTileIndices.add(newShopIndex);
    }
    
    /**
     * Checks if a tile is currently a shop.
     *
     * @param tileId the tile ID to check
     * @return true if the tile is a shop
     */
    protected boolean isShopTile(int tileId) {
        return shopTileIndices.contains(tileId);
    }
    
    /**
     * Removes a shop from the given tile and adds a new shop elsewhere.
     *
     * @param tileId the tile ID of the shop that was used
     */
    protected void replaceShop(int tileId) {
        shopTileIndices.remove(Integer.valueOf(tileId));
        addNewShop();
    }
    
    /**
     * Creates a bidirectional connection between two tiles.
     *
     * @param tileA index of the first tile
     * @param tileB index of the second tile
     */
    protected void connect(int tileA, int tileB) {
        tiles.get(tileA).addNeighbor(tiles.get(tileB));
        tiles.get(tileB).addNeighbor(tiles.get(tileA));
    }

    @Override
    public void show() {
        backgroundTex = new Texture(getBackgroundPath());
        shapeRenderer = new ShapeRenderer();
        font = gameInstance.getFont();
        font.setUseIntegerPositions(false);
        glyphLayout = new GlyphLayout();
        
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(w, h);
        camera.position.set(w / 2, h / 2, 0);
        camera.update();
        
        if (gameInstance.shouldAdvanceTurnOnBoardReturn()) {
            gameInstance.setAdvanceTurnOnBoardReturn(false);
            gameInstance.advancePlayerTurn();
        } else {
            gameInstance.resetBoardGameState(START_TILE_INDEX);
        }
        
        updateStatusMessage();
    }

    @Override
    public void render(float delta) {
        handleInput();
        update(delta);
        draw();
    }
    
    /**
     * Processes player input based on the current game state.
     */
    protected void handleInput() {
        Player currentPlayer = gameInstance.getPlayers()[gameInstance.getCurrentPlayerTurn()];
        
        switch (state) {
            case WAITING_FOR_ROLL:
                if (currentPlayer.justPressedAction()) {
                    rollDie();
                }
                break;
                
            case CHOOSING_DIRECTION:
                if (currentPlayer.justPressedLeft() || currentPlayer.justPressedUp()) {
                    selectedJunctionIndex = (selectedJunctionIndex - 1 + junctionOptions.size()) % junctionOptions.size();
                }
                if (currentPlayer.justPressedRight() || currentPlayer.justPressedDown()) {
                    selectedJunctionIndex = (selectedJunctionIndex + 1) % junctionOptions.size();
                }
                if (currentPlayer.justPressedAction()) {
                    BoardTile chosenTile = junctionOptions.get(selectedJunctionIndex);
                    moveToTile(chosenTile);
                }
                break;
                
            default:
                break;
        }
    }
    
    /**
     * Rolls the dice and initiates player movement.
     */
    protected void rollDie() {
        dieResult = random.nextInt(DIE_MAX - DIE_MIN + 1) + DIE_MIN;
        movesRemaining = dieResult;
        state = GameState.MOVING;
        statusMessage = gameInstance.getPlayers()[gameInstance.getCurrentPlayerTurn()].getName() 
            + " rolled a " + dieResult + "!";
        messageTimer = 1.0f;
        moveTimer = MOVE_DELAY;
    }
    
    /**
     * Updates game state based on the current phase.
     *
     * @param delta time since last frame in seconds
     */
    protected void update(float delta) {
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
                if (messageTimer <= 0) {
                    endTurn();
                }
                break;
                
            case STARTING_MINIGAME:
                if (messageTimer <= 0) {
                    startRandomMinigame();
                }
                break;
                
            case GAME_OVER:
                if (messageTimer <= 0) {
                    Player winner = null;
                    for (Player p : gameInstance.getPlayers()) {
                        if (p.getFruit() >= FRUITS_TO_WIN) {
                            winner = p;
                            break;
                        }
                    }
                    gameInstance.setScreen(new VictoryScreen(gameInstance, winner));
                }
                break;
                
            default:
                break;
        }
    }
    
    /**
     * Attempts to move the current player to the next tile.
     */
    protected void attemptMove() {
        if (movesRemaining <= 0) {
            applyTileEffect();
            return;
        }
        
        Player currentPlayer = gameInstance.getPlayers()[gameInstance.getCurrentPlayerTurn()];
        BoardTile currentTile = tiles.get(currentPlayer.getBoardTileIndex());
        int prevIndex = currentPlayer.getPreviousTileIndex();
        BoardTile cameFrom = (prevIndex >= 0) ? tiles.get(prevIndex) : null;
        List<BoardTile> nextOptions = currentTile.getNextTiles(cameFrom);
        
        if (nextOptions.isEmpty()) {
            applyTileEffect();
            return;
        }
        
        if (nextOptions.size() == 1) {
            moveToTile(nextOptions.get(0));
        } else {
            state = GameState.CHOOSING_DIRECTION;
            junctionOptions.clear();
            junctionOptions.addAll(nextOptions);
            selectedJunctionIndex = 0;
            statusMessage = "Choose direction!";
        }
    }
    
    /**
     * Moves the current player to the specified tile.
     *
     * @param targetTile the tile to move to
     */
    protected void moveToTile(BoardTile targetTile) {
        Player currentPlayer = gameInstance.getPlayers()[gameInstance.getCurrentPlayerTurn()];
        currentPlayer.setPreviousTileIndex(currentPlayer.getBoardTileIndex());
        currentPlayer.setBoardTileIndex(targetTile.getId());
        movesRemaining--;
        moveTimer = MOVE_DELAY;
        state = GameState.MOVING;
    }
    
    /**
     * Applies the effect of the tile the current player landed on.
     */
    protected void applyTileEffect() {
        Player currentPlayer = gameInstance.getPlayers()[gameInstance.getCurrentPlayerTurn()];
        BoardTile currentTile = tiles.get(currentPlayer.getBoardTileIndex());
        
        if (isShopTile(currentTile.getId())) {
            handleShopTile(currentPlayer, currentTile.getId());
            return;
        }
        
        switch (currentTile.getType()) {
            case GREEN:
                currentPlayer.addCrumbs(CRUMBS_REWARD);
                statusMessage = currentPlayer.getName() + " got " + CRUMBS_REWARD + " crumbs!";
                messageTimer = 1.5f;
                state = GameState.TILE_EFFECT;
                break;
                
            case RED:
                statusMessage = "Minigame time!";
                messageTimer = 1.0f;
                state = GameState.STARTING_MINIGAME;
                break;
                
            case BLUE:
            default:
                statusMessage = "Nothing here...";
                messageTimer = 0.8f;
                state = GameState.TILE_EFFECT;
                break;
        }
    }
    
    /**
     * Handles the shop tile interaction for the given player.
     *
     * @param player the player who landed on the shop
     * @param shopTileId the ID of the shop tile that was landed on
     */
    protected void handleShopTile(Player player, int shopTileId) {
        if (player.getCrumbs() >= FRUIT_COST) {
            player.subCrumbs(FRUIT_COST);
            player.addFruit();
            statusMessage = player.getName() + " bought a fruit! (" 
                + player.getFruit() + "/" + FRUITS_TO_WIN + ")";
            messageTimer = 2.0f;
            
            replaceShop(shopTileId);
            
            if (player.getFruit() >= FRUITS_TO_WIN) {
                statusMessage = player.getName() + " wins with " + FRUITS_TO_WIN + " fruits!";
                messageTimer = 3.0f;
                state = GameState.GAME_OVER;
                return;
            }
        } else {
            statusMessage = player.getName() + " needs " + FRUIT_COST 
                + " crumbs for fruit! (has " + player.getCrumbs() + ")";
            messageTimer = 2.0f;
        }
        state = GameState.TILE_EFFECT;
    }
    
    /**
     * Starts a randomly selected minigame.
     */
    protected void startRandomMinigame() {
        var minigames = gameInstance.getMiniGames();
        if (!minigames.isEmpty()) {
            int index = random.nextInt(minigames.size());
            MiniGameDescriptor selectedGame = minigames.get(index);
            gameInstance.setAdvanceTurnOnBoardReturn(true);
            gameInstance.setScreen(new MiniGameInstructionScreen(gameInstance, selectedGame));
        } else {
            endTurn();
        }
    }
    
    /**
     * Ends the current player's turn and advances to the next player.
     */
    protected void endTurn() {
        gameInstance.advancePlayerTurn();
        state = GameState.WAITING_FOR_ROLL;
        movesRemaining = 0;
        dieResult = 0;
        updateStatusMessage();
    }
    
    /**
     * Updates the status message to prompt the current player.
     */
    protected void updateStatusMessage() {
        Player currentPlayer = gameInstance.getPlayers()[gameInstance.getCurrentPlayerTurn()];
        statusMessage = currentPlayer.getName() + "'s turn! Press Action to roll.";
    }
    
    /**
     * Renders the complete game board.
     */
    protected void draw() {
        BoardTheme theme = getThemeColors();
        
        Gdx.gl.glClearColor(theme.clearColor.r, theme.clearColor.g, theme.clearColor.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        
        camera.viewportWidth = screenWidth;
        camera.viewportHeight = screenHeight;
        camera.position.set(screenWidth / 2, screenHeight / 2, 0);
        camera.update();
        
        gameInstance.getBatch().setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        gameInstance.getBatch().begin();
        gameInstance.getBatch().draw(backgroundTex, 0, 0, screenWidth, screenHeight);
        gameInstance.getBatch().end();
        
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        drawTiles(screenWidth, screenHeight, theme);
        drawPlayers(screenWidth, screenHeight, theme);
        
        if (state == GameState.CHOOSING_DIRECTION) {
            drawJunctionOptions(screenWidth, screenHeight);
        }
        
        drawUI(screenWidth, screenHeight, theme);
    }
    
    /**
     * Draws all board tiles and their connections.
     *
     * @param screenWidth current screen width
     * @param screenHeight current screen height
     * @param theme the color theme to use
     */
    protected void drawTiles(float screenWidth, float screenHeight, BoardTheme theme) {
        float tileRadius = Math.min(screenWidth, screenHeight) * 0.018f;
        float outlineRadius = tileRadius * 1.3f;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        shapeRenderer.setColor(theme.pathColor);
        for (BoardTile tile : tiles) {
            float x1 = tile.getPosX() * screenWidth;
            float y1 = tile.getPosY() * screenHeight;
            for (BoardTile neighbor : tile.getNeighbors()) {
                if (tile.getId() < neighbor.getId()) {
                    float x2 = neighbor.getPosX() * screenWidth;
                    float y2 = neighbor.getPosY() * screenHeight;
                    shapeRenderer.rectLine(x1, y1, x2, y2, tileRadius * 0.5f);
                }
            }
        }
        
        for (BoardTile tile : tiles) {
            float x = tile.getPosX() * screenWidth;
            float y = tile.getPosY() * screenHeight;
            
            shapeRenderer.setColor(theme.pathOutlineColor);
            shapeRenderer.circle(x, y, outlineRadius);
            
            if (isShopTile(tile.getId())) {
                shapeRenderer.setColor(theme.shopColor);
            } else {
                switch (tile.getType()) {
                    case GREEN:
                        shapeRenderer.setColor(0.2f, 0.85f, 0.3f, 1f);
                        break;
                    case RED:
                        shapeRenderer.setColor(0.9f, 0.25f, 0.3f, 1f);
                        break;
                    case BLUE:
                    default:
                        shapeRenderer.setColor(0.3f, 0.5f, 0.9f, 1f);
                        break;
                }
            }
            shapeRenderer.circle(x, y, tileRadius);
        }
        
        shapeRenderer.end();
    }
    
    /**
     * Draws all player sprites on their current tiles.
     *
     * @param screenWidth current screen width
     * @param screenHeight current screen height
     * @param theme the color theme to use
     */
    protected void drawPlayers(float screenWidth, float screenHeight, BoardTheme theme) {
        Player[] players = gameInstance.getPlayers();
        float spriteSize = Math.min(screenWidth, screenHeight) * 0.06f;
        int currentTurn = gameInstance.getCurrentPlayerTurn();
        
        BoardTile currentTile = tiles.get(players[currentTurn].getBoardTileIndex());
        float cx = currentTile.getPosX() * screenWidth - spriteSize / 2;
        float cy = currentTile.getPosY() * screenHeight - spriteSize / 2;
        
        int currentSameCount = 0;
        for (int j = 0; j < currentTurn; j++) {
            if (players[j].getBoardTileIndex() == players[currentTurn].getBoardTileIndex()) {
                currentSameCount++;
            }
        }
        cx += currentSameCount * spriteSize * 0.3f;
        cy += currentSameCount * spriteSize * 0.3f;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(theme.selectionGlowColor);
        shapeRenderer.circle(cx + spriteSize / 2, cy + spriteSize / 2, spriteSize * 0.7f);
        shapeRenderer.end();
        
        gameInstance.getBatch().begin();
        
        for (int i = 0; i < players.length; i++) {
            BoardTile tile = tiles.get(players[i].getBoardTileIndex());
            float x = tile.getPosX() * screenWidth - spriteSize / 2;
            float y = tile.getPosY() * screenHeight - spriteSize / 2;
            
            int sameCount = 0;
            for (int j = 0; j < i; j++) {
                if (players[j].getBoardTileIndex() == players[i].getBoardTileIndex()) {
                    sameCount++;
                }
            }
            x += sameCount * spriteSize * 0.3f;
            y += sameCount * spriteSize * 0.3f;
            
            players[i].getSprite().setSize(spriteSize, spriteSize);
            players[i].getSprite().setPosition(x, y);
            players[i].getSprite().draw(gameInstance.getBatch());
        }
        
        gameInstance.getBatch().end();
    }
    
    /**
     * Draws junction selection indicators.
     *
     * @param screenWidth current screen width
     * @param screenHeight current screen height
     */
    protected void drawJunctionOptions(float screenWidth, float screenHeight) {
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
    
    /**
     * Draws the UI overlay including status message and player stats.
     *
     * @param screenWidth current screen width
     * @param screenHeight current screen height
     * @param theme the color theme to use
     */
    protected void drawUI(float screenWidth, float screenHeight, BoardTheme theme) {
        gameInstance.getBatch().begin();
        
        String fullStatus = statusMessage;
        if (dieResult > 0 && state != GameState.WAITING_FOR_ROLL) {
            fullStatus += "  |  Die: " + dieResult + " (Moves: " + movesRemaining + ")";
        }
        
        font.getData().setScale(screenHeight / 400f);
        font.setColor(Color.WHITE);
        glyphLayout.setText(font, fullStatus);
        
        gameInstance.getBatch().end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(theme.uiBackgroundColor);
        shapeRenderer.rect(0, screenHeight - glyphLayout.height - 30, screenWidth, glyphLayout.height + 30);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        
        gameInstance.getBatch().begin();
        font.draw(gameInstance.getBatch(), fullStatus, 
            (screenWidth - glyphLayout.width) / 2, 
            screenHeight - 10);
        
        gameInstance.getBatch().end();
        
        Player[] players = gameInstance.getPlayers();
        int playersPerRow = players.length > 3 ? 3 : players.length;
        int numRows = (players.length + playersPerRow - 1) / playersPerRow;
        float rowHeight = 22;
        float statsBarHeight = numRows * rowHeight + 10;
        
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(theme.uiBackgroundColor);
        shapeRenderer.rect(0, 0, screenWidth, statsBarHeight);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        
        gameInstance.getBatch().begin();
        font.getData().setScale(screenHeight / 700f);
        
        for (int i = 0; i < players.length; i++) {
            Player p = players[i];
            String stats = p.getName() + " " + p.getFruit() + "F " + p.getCrumbs() + "C";
            if (i == gameInstance.getCurrentPlayerTurn()) {
                font.setColor(Color.YELLOW);
            } else {
                font.setColor(Color.WHITE);
            }
            
            int row = i / playersPerRow;
            int col = i % playersPerRow;
            float statsX = 10 + col * (screenWidth / playersPerRow);
            float statsY = statsBarHeight - 8 - (row * rowHeight);
            
            font.draw(gameInstance.getBatch(), stats, statsX, statsY);
        }
        
        gameInstance.getBatch().end();
    }

    @Override
    public void resize(int width, int height) {
        // Handled by draw using screen dimensions
    }

    @Override
    public void pause() {
        // No action needed
    }

    @Override
    public void resume() {
        // No action needed
    }

    @Override
    public void hide() {
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
        if (backgroundTex != null) {
            backgroundTex.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
