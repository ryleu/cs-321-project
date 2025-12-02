package com.roachstudios.critterparade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 * Minimal {@link Skin} setup shared across menus. Loads the VCR OSD Mono font
 * and applies basic styling to labels, buttons, and text fields for a
 * cohesive look without external skin JSON.
 */
public class CritterParadeSkin extends Skin {
    
    private static final String FONT_PATH = "fonts/VCR_OSD_MONO_1.001.ttf";
    private static final int MENU_VIEWPORT_HEIGHT = 360;
    private static final int DESIRED_VIRTUAL_HEIGHT = 16;
    private static final int MIN_FONT_SIZE = 16;
    
    private final BitmapFont font;
    
    /**
     * Initializes the skin by loading the VCR OSD Mono font and registering
     * basic styles under the "default" name so scene2d widgets can reference
     * them implicitly.
     */
    public CritterParadeSkin() {
        // Load VCR OSD Mono font using FreeType
        // Menus use 640x360 virtual viewport, so size font for that coordinate space
        int screenHeight = Gdx.graphics.getHeight();
        int fontSize = Math.round((float) DESIRED_VIRTUAL_HEIGHT * screenHeight / MENU_VIEWPORT_HEIGHT);
        
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_PATH));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = Math.max(MIN_FONT_SIZE, fontSize);
        font = generator.generateFont(parameter);
        generator.dispose();
        
        font.setUseIntegerPositions(false);
        
        // Register font and styles
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
    
    /**
     * Gets the shared font used by this skin.
     *
     * @return the BitmapFont instance
     */
    public BitmapFont getFont() {
        return font;
    }
    
    @Override
    public void dispose() {
        // Font is managed by Skin, so don't dispose it explicitly
        // super.dispose() will handle all resources added to the Skin
        super.dispose();
    }
}
