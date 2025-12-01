package com.roachstudios.critterparade.gameboards;

import com.badlogic.gdx.graphics.Color;
import com.roachstudios.critterparade.CritterParade;

/**
 * Ant Farmageddon board - a Mario Party-like experience set in underground ant tunnels.
 * 
 * <p>Players navigate through winding tunnels and chambers beneath the surface.
 * The board features multiple branching paths and underground chambers
 * connected by narrow tunnel passages.</p>
 */
public class AntFarmageddonBoard extends GameBoard {
    
    /** The display name for this board. */
    public static final String NAME = "Ant Farmageddon";

    /**
     * Constructs a new Ant Farmageddon board.
     *
     * @param gameInstance the main game instance providing shared resources
     */
    public AntFarmageddonBoard(CritterParade gameInstance) {
        super(gameInstance, NAME, "board/AntFarmageddon/background.png");
    }
    
    @Override
    protected BoardTheme getThemeColors() {
        return new BoardTheme(
            new Color(0.4f, 0.25f, 0.15f, 0.8f),   // pathColor - dirt brown
            new Color(0.25f, 0.15f, 0.08f, 1f),     // pathOutlineColor - darker brown
            new Color(0.9f, 0.75f, 0.4f, 1f),       // shopColor - sandy gold
            new Color(0.8f, 0.6f, 0.2f, 0.5f),      // selectionGlowColor - warm amber
            new Color(0.2f, 0.12f, 0.08f, 0.8f),    // uiBackgroundColor - deep earth
            new Color(0.35f, 0.22f, 0.12f, 1f)      // clearColor - tunnel brown
        );
    }

    @Override
    protected void createBoardTiles() {
        // === UPPER TUNNEL NETWORK ===
        
        // Entry chamber (top left)
        tiles.add(new BoardTile(0, 0.08f, 0.82f, BoardTile.Type.GREEN));   // Start tile
        tiles.add(new BoardTile(1, 0.15f, 0.85f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(2, 0.22f, 0.80f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(3, 0.30f, 0.83f, BoardTile.Type.RED));
        tiles.add(new BoardTile(4, 0.38f, 0.78f, BoardTile.Type.BLUE));
        
        // Upper right tunnel
        tiles.add(new BoardTile(5, 0.48f, 0.82f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(6, 0.58f, 0.85f, BoardTile.Type.GREEN));
        tiles.add(new BoardTile(7, 0.68f, 0.80f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(8, 0.78f, 0.82f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(9, 0.88f, 0.78f, BoardTile.Type.RED));
        
        // Right side descent
        tiles.add(new BoardTile(10, 0.90f, 0.68f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(11, 0.85f, 0.58f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(12, 0.88f, 0.48f, BoardTile.Type.GREEN));
        tiles.add(new BoardTile(13, 0.82f, 0.38f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(14, 0.85f, 0.28f, BoardTile.Type.RED));
        tiles.add(new BoardTile(15, 0.80f, 0.18f, BoardTile.Type.BLUE));
        
        // Bottom tunnel (right to left)
        tiles.add(new BoardTile(16, 0.70f, 0.15f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(17, 0.60f, 0.12f, BoardTile.Type.GREEN));
        tiles.add(new BoardTile(18, 0.50f, 0.15f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(19, 0.40f, 0.12f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(20, 0.30f, 0.15f, BoardTile.Type.RED));
        tiles.add(new BoardTile(21, 0.20f, 0.12f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(22, 0.12f, 0.18f, BoardTile.Type.GREEN));
        
        // Left side ascent
        tiles.add(new BoardTile(23, 0.08f, 0.28f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(24, 0.12f, 0.38f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(25, 0.08f, 0.48f, BoardTile.Type.RED));
        tiles.add(new BoardTile(26, 0.10f, 0.58f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(27, 0.06f, 0.68f, BoardTile.Type.GREEN));
        tiles.add(new BoardTile(28, 0.10f, 0.75f, BoardTile.Type.BLUE));
        
        // === CENTRAL CHAMBER (queen's chamber) ===
        
        // Upper chamber ring
        tiles.add(new BoardTile(29, 0.35f, 0.68f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(30, 0.45f, 0.70f, BoardTile.Type.GREEN));
        tiles.add(new BoardTile(31, 0.55f, 0.68f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(32, 0.65f, 0.65f, BoardTile.Type.RED));
        
        // Right chamber wall
        tiles.add(new BoardTile(33, 0.70f, 0.55f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(34, 0.68f, 0.45f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(35, 0.72f, 0.35f, BoardTile.Type.GREEN));
        
        // Lower chamber ring
        tiles.add(new BoardTile(36, 0.62f, 0.28f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(37, 0.52f, 0.25f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(38, 0.42f, 0.28f, BoardTile.Type.RED));
        tiles.add(new BoardTile(39, 0.32f, 0.25f, BoardTile.Type.BLUE));
        
        // Left chamber wall
        tiles.add(new BoardTile(40, 0.25f, 0.32f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(41, 0.22f, 0.42f, BoardTile.Type.GREEN));
        tiles.add(new BoardTile(42, 0.25f, 0.52f, BoardTile.Type.BLUE));
        tiles.add(new BoardTile(43, 0.28f, 0.60f, BoardTile.Type.BLUE));
        
        // === TUNNEL CONNECTORS ===
        
        // Top connector to chamber
        tiles.add(new BoardTile(44, 0.38f, 0.72f, BoardTile.Type.BLUE));
        
        // Right connector
        tiles.add(new BoardTile(45, 0.78f, 0.55f, BoardTile.Type.BLUE));
        
        // Bottom connector
        tiles.add(new BoardTile(46, 0.42f, 0.20f, BoardTile.Type.BLUE));
        
        // Left connector
        tiles.add(new BoardTile(47, 0.18f, 0.45f, BoardTile.Type.BLUE));
        
        // === DEFINE CONNECTIONS ===
        
        // Outer tunnel loop (clockwise)
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
        connect(28, 0);  // Close the outer loop
        
        // Central chamber loop (clockwise)
        connect(29, 30);
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
        connect(43, 29);  // Close the chamber loop
        
        // Tunnel connectors to chamber
        connect(4, 44);   // Top connector
        connect(44, 29);
        
        connect(11, 45);  // Right connector
        connect(45, 33);
        
        connect(19, 46);  // Bottom connector
        connect(46, 38);
        
        connect(25, 47);  // Left connector
        connect(47, 41);
    }
}

