package com.roachstudios.critterparade.gameboards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.roachstudios.critterparade.CritterParade;
import com.roachstudios.critterparade.Player;

import java.util.List;
import java.util.ArrayList;

/**
 * Picnic Pond board: static background plus Mario Party-like turn-based flow.
 */
public class PicnicPondBoard extends GameBoard {
    private final Stage stage;
    private Label infoLabel;
    private Label turnLabel;

    private boolean awaitingChoice = false;
    private List<Integer> currentOptions;
    private int selectedOptionIndex = 0;
    private boolean requestAdvanceTurn = false;

    private boolean initialized = false;
    private final ArrayList<com.badlogic.gdx.scenes.scene2d.ui.Image> playerImages = new ArrayList<>();

    private final float TILE_RADIUS = 10f;
    private ShapeRenderer shapeRenderer;

    /**
     * @param gameInstance shared game instance used for navigation and skin
     */
    public PicnicPondBoard(CritterParade gameInstance) {
        super(gameInstance);
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    /**
     * Builds a root table and sets a background texture. Initializes tile graph
     * and player turn state.
     */
    public void show() {
        if (!initialized) {
            Table root = new Table();
            stage.addActor(root);
            root.setFillParent(true);
            root.setBackground(
                new TextureRegionDrawable(
                    new TextureRegion(
                        new Texture("board/PicnicPond/background.png")
                    )
                )
            );

            infoLabel = new Label("", gameInstance.skin);
            root.add(infoLabel).left().top().pad(8f);

            // Bottom status bar
            Table bottom = new Table();
            bottom.setFillParent(true);
            bottom.bottom();
            turnLabel = new Label("", gameInstance.skin);
            bottom.add(turnLabel).expandX().left().pad(6f);
            stage.addActor(bottom);

            root.setDebug(gameInstance.isDebugMode(), true);

            // Load tiles from JSON
            loadTilesFromJson("board/PicnicPond/tiles.json");

            // Initialize players and images
            playerImages.clear();
            for (int p = 1; p <= gameInstance.getNumPlayers(); p++) {
                Player pl = gameInstance.getPlayers().get(p - 1);
                pl.setCurrentTileId(startTileId);
                com.badlogic.gdx.scenes.scene2d.ui.Image img = new com.badlogic.gdx.scenes.scene2d.ui.Image(new TextureRegionDrawable(new TextureRegion(pl.getSprite().getTexture())));
                img.setSize(24, 24);
                stage.addActor(img);
                playerImages.add(img);
            }

            initialized = true;
            // Begin first player's turn immediately
            beginTurn();
        } else {
            // Returned from a mini game; resume turn flow
            updateTurnLabel();
        }
    }

    private void beginTurn() {
        awaitingChoice = false;
        selectedOptionIndex = 0;
        Player current = gameInstance.getPlayers().get(currentPlayerTurnIndex);
        List<Integer> next = getNextTiles(current.getCurrentTileId());
        currentOptions = next;

        if (next.isEmpty()) {
            infoLabel.setText("Player " + current.getID() + ": no moves. Skipping...");
            requestAdvanceTurn = true;
            return;
        }
        if (next.size() == 1) {
            // Auto-move and resolve tile
            moveCurrentPlayerTo(next.get(0));
            return;
        }
        // Multiple options: await player choice
        awaitingChoice = true;
        infoLabel.setText("Player " + current.getID() + ": choose a path (Left/Right, Action to confirm)");
    }

    private void moveCurrentPlayerTo(int tileId) {
        Player current = gameInstance.getPlayers().get(currentPlayerTurnIndex);
        current.setCurrentTileId(tileId);
        BoardTile tile = findTileById(tileId);
        if (tile != null && tile.getType() == BoardTile.Type.MINIGAME) {
            infoLabel.setText("Player " + current.getID() + " landed on RED. Starting minigame...");
            startRandomMiniGame();
        } else {
            requestAdvanceTurn = true;
        }
    }

    private BoardTile findTileById(int id) {
        for (BoardTile t : tiles) {
            if (t.getId() == id) return t;
        }
        return null;
    }

    private void advanceTurn() {
        // Find the next player who has at least one available move.
        int tries = gameInstance.getPlayers().size();
        while (tries-- > 0) {
            currentPlayerTurnIndex = (currentPlayerTurnIndex + 1) % gameInstance.getPlayers().size();
            Player p = gameInstance.getPlayers().get(currentPlayerTurnIndex);
            List<Integer> next = getNextTiles(p.getCurrentTileId());
            if (!next.isEmpty()) {
                beginTurn();
                return;
            }
        }
        // No players have moves; pause turn progression for this round.
        awaitingChoice = false;
        infoLabel.setText("No players have available moves.");
    }

    @Override
    public void onMiniGameComplete() {
        // After a mini game, proceed to the next player's turn
        requestAdvanceTurn = true;
    }

    @Override
    /**
     * Clears the screen and renders the stage. Handles input for path choice.
     */
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 0.992f, 0.816f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (requestAdvanceTurn) {
            requestAdvanceTurn = false;
            advanceTurn();
        } else if (awaitingChoice) {
            handleChoiceInput();
        }

        updatePlayerImagePositions();
        updateTurnLabel();

        stage.act();
        stage.draw();

        // Debug draw tiles as colored circles when debug mode is enabled
        if (gameInstance.isDebugMode()) {
            shapeRenderer.setProjectionMatrix(stage.getViewport().getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (BoardTile t : tiles) {
                Color c = getColorForType(t.getType());
                shapeRenderer.setColor(c.r, c.g, c.b, 0.7f);
                float cx = t.getPosX();
                float cy = t.getPosY();
                shapeRenderer.circle(cx, cy, TILE_RADIUS, 24);
            }
            shapeRenderer.end();
        }
    }

    private void updateTurnLabel() {
        Player pl = gameInstance.getPlayers().get(currentPlayerTurnIndex);
        if (turnLabel != null) {
            turnLabel.setText("Player " + pl.getID() + "  Crumbs: " + pl.getCrumbs() + "  Fruits: " + pl.getFruit());
        }
    }

    private void updatePlayerImagePositions() {
        for (int i = 0; i < gameInstance.getPlayers().size(); i++) {
            Player pl = gameInstance.getPlayers().get(i);
            BoardTile t = findTileById(pl.getCurrentTileId());
            if (t == null) continue;
            com.badlogic.gdx.scenes.scene2d.ui.Image img = playerImages.get(i);
            float baseX = t.getPosX();
            float baseY = t.getPosY();
            float ox = (i % 3) * 12f; // simple fan-out offsets to avoid overlap
            float oy = (i / 3) * 12f;
            img.setPosition(baseX + ox, baseY + oy);
        }
    }

    private Color getColorForType(BoardTile.Type type) {
        switch (type) {
            case REGULAR:
                return Color.SKY; // blue
            case SHOP:
                return Color.GREEN; // green
            case MINIGAME:
                return Color.RED; // red
            default:
                return Color.WHITE;
        }
    }

    private void handleChoiceInput() {
        Player ps = gameInstance.getPlayers().get(currentPlayerTurnIndex);
        // Cycle selection with player-specific left/right, confirm with action
        if (playerJustPressedLeft(ps.getID())) {
            selectedOptionIndex = (selectedOptionIndex - 1 + currentOptions.size()) % currentOptions.size();
            infoLabel.setText("Player " + ps.getID() + ": option " + (selectedOptionIndex + 1) + "/" + currentOptions.size());
        } else if (playerJustPressedRight(ps.getID())) {
            selectedOptionIndex = (selectedOptionIndex + 1) % currentOptions.size();
            infoLabel.setText("Player " + ps.getID() + ": option " + (selectedOptionIndex + 1) + "/" + currentOptions.size());
        } else if (playerJustPressedAction(ps.getID())) {
            awaitingChoice = false;
            moveCurrentPlayerTo(currentOptions.get(selectedOptionIndex));
        }
    }

    // Minimal input mapping (duplicated from Player for board-only controls)
    private boolean playerJustPressedLeft(int id) {
        switch (id) {
            case 1: return Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.A);
            case 2: return Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.F);
            case 3: return Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.J);
            case 4: return Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SEMICOLON);
            case 5: return Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.LEFT);
            case 6: return Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUMPAD_4);
            default: return false;
        }
    }

    private boolean playerJustPressedRight(int id) {
        switch (id) {
            case 1: return Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.D);
            case 2: return Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.H);
            case 3: return Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.L);
            case 4: return Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER);
            case 5: return Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.RIGHT);
            case 6: return Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUMPAD_6);
            default: return false;
        }
    }

    private boolean playerJustPressedAction(int id) {
        switch (id) {
            case 1: return Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.E);
            case 2: return Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.Y);
            case 3: return Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.O);
            case 4: return Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.RIGHT_BRACKET);
            case 5: return Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE);
            case 6: return Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUMPAD_9);
            default: return false;
        }
    }

    @Override
    public void resize(int width, int height) {

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
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
