package com.roachstudios.critterparade;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 * Minimal {@link Skin} setup shared across menus. Keeps a single bitmap font and
 * applies basic white text styling to labels, buttons, and text fields for a
 * cohesive look without external skin JSON.
 */
public class CritterParadeSkin extends Skin {
    /**
     * Initializes and registers basic styles under the "default" name so scene2d
     * widgets can reference them implicitly.
     */
    public CritterParadeSkin() {
        BitmapFont font = new BitmapFont();
        add("default-font", font);
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.BLACK;
        add("default", labelStyle);

        TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
        tbs.font = font;
        tbs.fontColor = Color.BLACK;
        add("default", tbs);

        TextField.TextFieldStyle tfs = new TextField.TextFieldStyle();
        tfs.font = font;
        tfs.fontColor = Color.BLACK;
        add("default", tfs);

        ScrollPane.ScrollPaneStyle sps = new ScrollPane.ScrollPaneStyle();
        add("default", sps);
    }
}
