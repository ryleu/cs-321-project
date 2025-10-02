package com.roachstudios.critterparade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public final class FontUtil {

    private static final String ENV_TTF_PATH = "CP_MINECRAFTIA_TTF";

    private FontUtil() { }

    public static BitmapFont createUiFont(int sizePx) {
        String ttfPath = System.getenv(ENV_TTF_PATH);
        if (ttfPath == null || ttfPath.isEmpty()) {
            Gdx.app.log("FontUtil", "Env var '" + ENV_TTF_PATH + "' not set; falling back to default BitmapFont");
            return new BitmapFont();
        }

        FreeTypeFontGenerator generator = null;
        try {
            generator = new FreeTypeFontGenerator(Gdx.files.absolute(ttfPath));
            FreeTypeFontParameter param = new FreeTypeFontParameter();
            param.size = sizePx;
            param.color = Color.WHITE;
            param.shadowOffsetX = 0;
            param.shadowOffsetY = 0;
            param.borderWidth = 0f;
            param.kerning = true;
            return generator.generateFont(param);
        } catch (Exception ex) {
            Gdx.app.error("FontUtil", "Failed to load TTF from " + ttfPath + ": " + ex.getMessage());
            return new BitmapFont();
        } finally {
            if (generator != null) {
                generator.dispose();
            }
        }
    }
}


