package com.roachstudios.critterparade.gameboards;

import com.badlogic.gdx.graphics.Color;
import com.roachstudios.critterparade.CritterParade;

/**
 * Picnic Pond board - a Mario Party-like experience set in a peaceful pond area.
 * 
 * <p>Players navigate around a picnic blanket near a pond, collecting crumbs
 * and triggering minigames. The board features multiple interconnected paths
 * and junctions for strategic movement.</p>
 */
public class PicnicPondBoard extends GameBoard {
    
    /** The display name for this board. */
    public static final String NAME = "Picnic Pond";

    /**
     * Constructs a new Picnic Pond board.
     *
     * @param gameInstance the main game instance providing shared resources
     */
    public PicnicPondBoard(CritterParade gameInstance) {
        super(gameInstance, NAME, "board/PicnicPond/background.png");
    }
    
    @Override
    protected BoardTheme getThemeColors() {
        return new BoardTheme(
            new Color(0.1f, 0.1f, 0.1f, 0.8f),    // pathColor - dark gray
            new Color(0.1f, 0.1f, 0.1f, 1f),       // pathOutlineColor - dark gray
            new Color(0.7f, 0.3f, 0.9f, 1f),       // shopColor - purple
            new Color(1f, 1f, 0f, 0.5f),           // selectionGlowColor - yellow
            new Color(0.25f, 0.25f, 0.25f, 0.7f),  // uiBackgroundColor - dark gray
            new Color(0.2f, 0.6f, 0.3f, 1f)        // clearColor - green
        );
    }

    @Override
    protected void createBoardTiles() {
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
}
