package com.roachstudios.critterparade;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CritterParadeGame extends Game {

    public SpriteBatch spriteBatch;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        if (spriteBatch != null) {
            spriteBatch.dispose();
        }
        if (getScreen() != null) {
            getScreen().dispose();
        }
    }
}


