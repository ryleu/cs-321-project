package com.roachstudios.critterparade.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.roachstudios.critterparade.CritterParade;

/**
 * First-run consent screen asking the user whether to enable session logging.
 * The user's choice is saved to settings and the game proceeds to the main menu.
 */
public class ConsentScreen implements Screen {
    
    private final CritterParade game;
    private final Stage stage;
    
    /**
     * Constructs the consent screen for session logging preferences.
     *
     * @param game the main game instance providing shared resources
     */
    public ConsentScreen(CritterParade game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
    }
    
    @Override
    public void show() {
        // Update viewport to current screen size to ensure proper scaling
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        // Reset font scale (minigames may have changed it for their world-unit viewports)
        game.font.getData().setScale(1.0f);
        
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        
        // Title
        Label titleLabel = new Label("Session Logging", game.getSkin());
        root.add(titleLabel).padBottom(20).row();
        
        // Description
        String description = 
            "Critter Parade can save anonymous session logs to help improve the game.\n\n" +
            "These logs record:\n" +
            "  - System info (OS type, Java version, CPU cores, memory)\n" +
            "  - Game modes and boards selected\n" +
            "  - Minigame results and scores\n" +
            "  - Player turns and dice rolls\n\n" +
            "Logs are saved locally to your computer in:\n" +
            "  ~/.critterparade/logs/\n\n" +
            "No data is sent over the internet.\n" +
            "You can change this setting later in the settings file.";
        
        Label descLabel = new Label(description, game.getSkin());
        descLabel.setWrap(true);
        descLabel.setAlignment(Align.center);
        root.add(descLabel).width(500).padBottom(40).row();
        
        // Buttons
        Table buttonTable = new Table();
        
        TextButton enableButton = new TextButton("Enable Logging", game.getSkin());
        enableButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setLoggingConsent(true);
                game.log("User enabled session logging");
                game.setScreen(new MainMenu(game));
            }
        });
        
        TextButton disableButton = new TextButton("Disable Logging", game.getSkin());
        disableButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setLoggingConsent(false);
                game.log("User disabled session logging");
                game.setScreen(new MainMenu(game));
            }
        });
        
        buttonTable.add(enableButton).padRight(20);
        buttonTable.add(disableButton);
        root.add(buttonTable);
        
        root.setDebug(game.isDebugMode(), true);
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 0.992f, 0.816f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.act(delta);
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    
    @Override
    public void pause() {}
    
    @Override
    public void resume() {}
    
    @Override
    public void hide() {}
    
    @Override
    public void dispose() {
        stage.dispose();
    }
}

