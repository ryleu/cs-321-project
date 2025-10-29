package com.roachstudios.critterparade.gameboards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

public abstract class GameBoard implements Screen {
    public void render(float delta) {
        Gdx.gl.glClearColor(0.5f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}
