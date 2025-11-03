package com.roachstudios.critterparade.gameboards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.roachstudios.critterparade.CritterParade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for board-mode screens. Provides common rendering utilities and
 * shared state for tile graphs and turn order.
 */
public abstract class GameBoard implements Screen {
    protected final CritterParade gameInstance;

    // Configured tiles and adjacency (tileId -> next tileIds)
    protected final ArrayList<BoardTile> tiles = new ArrayList<>();
    protected final Map<Integer, List<Integer>> adjacency = new HashMap<>();
    protected int startTileId = 0;

    // Simple multiplayer turn tracking
    protected int currentPlayerTurnIndex = 0;
    protected final ArrayList<PlayerState> playerStates = new ArrayList<>();

    public GameBoard(CritterParade gameInstance) {
        this.gameInstance = gameInstance;
    }

    /**
     * Represents per-player board state.
     */
    protected static class PlayerState {
        public final int playerId;
        public int currentTileId;

        public PlayerState(int playerId, int startTileId) {
            this.playerId = playerId;
            this.currentTileId = startTileId;
        }
    }

    /**
     * Loads a tile graph from a JSON file in assets. Expected format:
     * {
     *   "start": <int>,
     *   "tiles": [ { "id":<int>, "x":<int>, "y":<int>, "type":"REGULAR|MINIGAME|SHOP", "next":[<int>...] } ]
     * }
     */
    protected void loadTilesFromJson(String internalPath) {
        tiles.clear();
        adjacency.clear();

        FileHandle fh = Gdx.files.internal(internalPath);
        JsonValue root = new JsonReader().parse(fh);

        if (root.has("start")) {
            startTileId = root.getInt("start");
        } else {
            startTileId = 0;
        }

        for (JsonValue tileVal : root.get("tiles")) {
            int id = tileVal.getInt("id");
            int x = tileVal.getInt("x");
            int y = tileVal.getInt("y");
            String typeStr = tileVal.getString("type", "REGULAR");
            BoardTile.Type type = BoardTile.Type.valueOf(typeStr);

            tiles.add(new BoardTile(x, y, type, id));

            ArrayList<Integer> nextList = new ArrayList<>();
            JsonValue next = tileVal.get("next");
            if (next != null) {
                for (JsonValue n : next) {
                    nextList.add(n.asInt());
                }
            }
            adjacency.put(id, nextList);
        }
    }

    /**
     * @return list of next tile IDs for the given tile ID
     */
    protected List<Integer> getNextTiles(int tileId) {
        return adjacency.getOrDefault(tileId, List.of());
    }

    /**
     * Starts a random mini game and configures the game to return back to this
     * board when results are acknowledged.
     */
    protected void startRandomMiniGame() {
        gameInstance.startRandomMiniGameReturningTo(this);
    }

    /**
     * Basic clear pass. Subclasses are expected to override and call super.
     */
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 0.992f, 0.816f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}
