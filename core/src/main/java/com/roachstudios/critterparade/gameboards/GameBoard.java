package com.roachstudios.critterparade.gameboards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

import java.util.ArrayList;

/**
 * Base class for board-mode screens. Provides common rendering utilities and a
 * place to store board tiles as the implementation evolves.
 */
public abstract class GameBoard implements Screen {
    // Store tiles here so specific boards can share rendering/logic patterns.
    @SuppressWarnings("unused")
    private ArrayList<BoardTile> tiles = new ArrayList<>();

    /**
     * Basic clear pass. Subclasses are expected to override and call super.
     */
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 0.992f, 0.816f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}
