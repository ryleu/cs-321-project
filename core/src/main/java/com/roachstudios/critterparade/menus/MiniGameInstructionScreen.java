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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.roachstudios.critterparade.CritterParade;
import com.roachstudios.critterparade.minigames.MiniGame;

import java.util.function.Supplier;

/**
 * Displays the mini game name and instructions before starting.
 * Shows a "Start" button that launches the actual mini game.
 */
public class MiniGameInstructionScreen implements Screen {
    private final CritterParade gameInstance;
    private final Stage stage;
    private final Supplier<MiniGame> miniGameSupplier;
    private final String gameName;
    private final String instructions;

    /**
     * @param gameInstance shared game instance used for navigation and skin
     * @param miniGameSupplier supplier that creates the mini game instance when ready to start
     */
    public MiniGameInstructionScreen(CritterParade gameInstance, Supplier<MiniGame> miniGameSupplier) {
        this.gameInstance = gameInstance;
        this.miniGameSupplier = miniGameSupplier;
        
        // Create a temporary instance to get name and instructions
        MiniGame tempGame = miniGameSupplier.get();
        this.gameName = tempGame.getName();
        this.instructions = tempGame.getInstructions();
        tempGame.dispose();

        // Fixed virtual size for consistent layout.
        stage = new Stage(new FitViewport(640, 360));
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        Table root = new Table();
        stage.addActor(root);
        root.setFillParent(true);
        root.pad(20);

        // Title - game name
        Label titleLabel = new Label(gameName, gameInstance.skin);
        titleLabel.setFontScale(2.0f);
        titleLabel.setAlignment(Align.center);
        root.add(titleLabel).fillX().padBottom(30);
        root.row();

        // Instructions
        Label instructionsLabel = new Label(instructions, gameInstance.skin);
        instructionsLabel.setWrap(true);
        instructionsLabel.setAlignment(Align.center);
        root.add(instructionsLabel).width(500).fillX().padBottom(40);
        root.row();

        // Start button
        TextButton startButton = new TextButton("Start!", gameInstance.skin);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Log minigame start
                gameInstance.log("Starting minigame: %s", gameName);
                gameInstance.logMinigameStart(gameName);
                
                // Create a fresh minigame instance and start it
                gameInstance.setScreen(miniGameSupplier.get());
            }
        });
        root.add(startButton).width(150).height(50);

        root.setDebug(gameInstance.isDebugMode(), true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 0.992f, 0.816f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}

