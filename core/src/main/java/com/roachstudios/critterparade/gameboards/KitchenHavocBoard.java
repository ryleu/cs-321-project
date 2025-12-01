package com.roachstudios.critterparade.gameboards;

import com.badlogic.gdx.graphics.Color;
import com.roachstudios.critterparade.CritterParade;

/**
 * Kitchen Havoc board - a Mario Party-like experience set in a bustling kitchen.
 * 
 * <p>Players navigate around countertops, appliances, and cooking stations.
 * The board features an outer counter loop and an inner kitchen island,
 * connected by cross paths for strategic movement options.</p>
 */
public class KitchenHavocBoard extends GameBoard {
    
    /** The display name for this board. */
    public static final String NAME = "Kitchen Havoc";
    
    /** Path to the background texture asset. */
    private static final String BACKGROUND_PATH = "board/KitchenHavoc/background.png";

    /**
     * Constructs a new Kitchen Havoc board.
     *
     * @param gameInstance the main game instance providing shared resources
     */
    public KitchenHavocBoard(CritterParade gameInstance) {
        super(gameInstance);
    }
    
    @Override
    public String getName() {
        return NAME;
    }
    
    @Override
    protected String getBackgroundPath() {
        return BACKGROUND_PATH;
    }
    
    @Override
    protected BoardTheme getThemeColors() {
        return new BoardTheme(
            new Color(0.2f, 0.15f, 0.1f, 0.8f),    // pathColor - dark brown
            new Color(0.15f, 0.1f, 0.05f, 1f),      // pathOutlineColor - darker brown
            new Color(0.9f, 0.6f, 0.2f, 1f),        // shopColor - orange (like fruit)
            new Color(1f, 0.9f, 0.3f, 0.5f),        // selectionGlowColor - warm yellow
            new Color(0.3f, 0.2f, 0.15f, 0.8f),     // uiBackgroundColor - dark brown
            new Color(0.4f, 0.35f, 0.3f, 1f)        // clearColor - kitchen brown
        );
    }

    @Override
    protected void createBoardTiles() {
        // === OUTER COUNTER LOOP (main path around the kitchen) ===
        
        // Top-left corner - near the stove
        tiles.add(new BoardTile(0, 0.08f, 0.85f, BoardTile.Type.GREEN));   // Start tile
        tiles.add(new BoardTile(1, 0.15f, 0.88f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(2, 0.22f, 0.85f, BoardTile.Type.RED));
        tiles.add(new BoardTile(3, 0.30f, 0.88f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(4, 0.38f, 0.85f, BoardTile.Type.BLUE));
        
        // Top edge - across the counter
        tiles.add(new BoardTile(5, 0.46f, 0.88f, BoardTile.Type.GREEN));
        tiles.add(new BoardTile(6, 0.54f, 0.85f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(7, 0.62f, 0.88f, BoardTile.Type.RED));
        tiles.add(new BoardTile(8, 0.70f, 0.85f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(9, 0.78f, 0.88f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(10, 0.86f, 0.82f, BoardTile.Type.GREEN));
        
        // Right side - down the refrigerator
        tiles.add(new BoardTile(11, 0.90f, 0.72f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(12, 0.92f, 0.62f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(13, 0.90f, 0.52f, BoardTile.Type.RED));
        tiles.add(new BoardTile(14, 0.92f, 0.42f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(15, 0.90f, 0.32f, BoardTile.Type.GREEN));
        tiles.add(new BoardTile(16, 0.88f, 0.22f, BoardTile.Type.BLUE));
        
        // Bottom edge - across the floor
        tiles.add(new BoardTile(17, 0.78f, 0.15f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(18, 0.68f, 0.12f, BoardTile.Type.RED));
        tiles.add(new BoardTile(19, 0.58f, 0.15f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(20, 0.48f, 0.12f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(21, 0.38f, 0.15f, BoardTile.Type.GREEN));
        tiles.add(new BoardTile(22, 0.28f, 0.12f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(23, 0.18f, 0.15f, BoardTile.Type.RED));
        
        // Left side - up the cabinet
        tiles.add(new BoardTile(24, 0.10f, 0.22f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(25, 0.08f, 0.32f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(26, 0.06f, 0.42f, BoardTile.Type.GREEN));
        tiles.add(new BoardTile(27, 0.08f, 0.52f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(28, 0.06f, 0.62f, BoardTile.Type.RED));
        tiles.add(new BoardTile(29, 0.08f, 0.72f, BoardTile.Type.BLUE));
        
        // === INNER ISLAND (kitchen island in center) ===
        
        // Top of island
        tiles.add(new BoardTile(30, 0.30f, 0.70f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(31, 0.40f, 0.72f, BoardTile.Type.GREEN));
        tiles.add(new BoardTile(32, 0.50f, 0.70f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(33, 0.60f, 0.72f, BoardTile.Type.RED));
        tiles.add(new BoardTile(34, 0.70f, 0.70f, BoardTile.Type.BLUE));
        
        // Right side of island
        tiles.add(new BoardTile(35, 0.75f, 0.60f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(36, 0.78f, 0.50f, BoardTile.Type.GREEN));
        tiles.add(new BoardTile(37, 0.75f, 0.40f, BoardTile.Type.BLUE));
        
        // Bottom of island
        tiles.add(new BoardTile(38, 0.68f, 0.32f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(39, 0.58f, 0.30f, BoardTile.Type.RED));
        tiles.add(new BoardTile(40, 0.48f, 0.32f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(41, 0.38f, 0.30f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(42, 0.28f, 0.32f, BoardTile.Type.GREEN));
        
        // Left side of island
        tiles.add(new BoardTile(43, 0.22f, 0.42f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(44, 0.20f, 0.52f, BoardTile.Type.RED));
        tiles.add(new BoardTile(45, 0.22f, 0.62f, BoardTile.Type.BLUE));
        
        // === CROSS PATHS (connecting outer and inner) ===
        
        // Top connector (from outer to island)
        tiles.add(new BoardTile(46, 0.30f, 0.78f, BoardTile.Type.BLUE));
        
        // Right connector
        tiles.add(new BoardTile(47, 0.82f, 0.50f, BoardTile.Type.BLUE));
        
        // Bottom connector
        tiles.add(new BoardTile(48, 0.48f, 0.22f, BoardTile.Type.BLUE));
        
        // Left connector
        tiles.add(new BoardTile(49, 0.14f, 0.52f, BoardTile.Type.BLUE));
        
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
        connect(29, 0);  // Close the outer loop
        
        // Inner island loop (clockwise)
        connect(30, 31);
        connect(31, 32);
        connect(32, 33);
        connect(33, 34);
        connect(34, 35);
        connect(35, 36);
        connect(36, 37);
        connect(37, 38);
        connect(38, 39);
        connect(39, 40);
        connect(40, 41);
        connect(41, 42);
        connect(42, 43);
        connect(43, 44);
        connect(44, 45);
        connect(45, 30);  // Close the inner loop
        
        // Cross paths connecting outer to inner
        connect(3, 46);   // Top-left connector
        connect(46, 30);
        
        connect(13, 47);  // Right connector
        connect(47, 36);
        
        connect(20, 48);  // Bottom connector
        connect(48, 40);
        
        connect(27, 49);  // Left connector
        connect(49, 44);
    }
}
