/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.roachstudios.critterparade.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import com.roachstudios.critterparade.CritterParade;
import com.roachstudios.critterparade.Player;
import com.roachstudios.critterparade.gameboards.PicnicPondBoard;

/**
 * Displays the placements resulting from a mini game and provides a
 * context-sensitive Continue button based on the current game mode.
 */
public class MiniGameResultScreen implements Screen{
    private final CritterParade gameInstance;
    private final Stage stage;
    private Player[] placements;
    
    /**
     * @param gameInstance shared game instance used for navigation and skin
     * @param results ordered array of players from 1st to last place
     */
    public MiniGameResultScreen(CritterParade gameInstance, Player[] results){
         this.gameInstance = gameInstance;
         this.placements = results;
        // Use ScreenViewport to present results at actual pixel size regardless
        // of window dimensions; this keeps text crisp for simple static lists.
        stage = new Stage(new ScreenViewport());

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    /**
     * Builds a simple list of placements and adds a Continue button whose
     * destination depends on the active game mode.
     */
    public void show() {
        Table root = new Table();
        stage.addActor(root);
        root.setFillParent(true);
        
        TextField title = new TextField("Results:", gameInstance.skin);
        title.setAlignment(Align.center);

        root.add(title).fillX();
        
        for(int i = 0; i < placements.length; i++){
            root.row();
            
            TextField place = new TextField((i+1) + ". " + placements[i].getName(), gameInstance.skin);
            title.setAlignment(Align.center);
            root.add(place);
        }
        
        // Continue action depends on whether we are in the board flow or practice.
        if(gameInstance.mode == CritterParade.Mode.BOARD_MODE){
            root.row();
            TextButton changeButton = new TextButton("Continue", gameInstance.skin);
            changeButton.addListener(new ChangeListener() {
                
                
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    gameInstance.setScreen(new PicnicPondBoard(gameInstance));
                }
            });
            root.add(changeButton);
        
        root.setDebug(gameInstance.isDebugMode(), true);
        
        }else if(gameInstance.mode == CritterParade.Mode.PRACTICE_MODE){
            root.row();
            TextButton changeButton = new TextButton("Continue", gameInstance.skin);
            changeButton.addListener(new ChangeListener() {
                
                
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    gameInstance.setScreen(new MainMenu(gameInstance));
                }
            });
            root.add(changeButton);
        
        root.setDebug(gameInstance.isDebugMode(), true);
        }
        
    }

    @Override
    /**
     * Clears the screen and renders the stage.
     */
    public void render(float f) {
        Gdx.gl.glClearColor(1f, 0.992f, 0.816f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    /**
     * Updates the viewport and centers the camera.
     */
    public void resize(int i, int i1) {
        stage.getViewport().update(i, i1, true);
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
    }
}
