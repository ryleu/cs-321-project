/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.roachstudios.critterparade.minigames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.roachstudios.critterparade.CritterParade;
import com.roachstudios.critterparade.Player;
import com.roachstudios.critterparade.menus.MiniGameResultScreen;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple reaction-based horizontal racer: each player advances by pressing
 * their move input. First to cross the finish line wins.
 */
public class SimpleRacerMiniGame extends MiniGame{
    
    private final CritterParade gameInstance;
    private Texture backgroundTex;
    private Texture finishLineTex;
    
    private final float playerSize; 
    private final int playerCount;
    private final List<Player> racers = new ArrayList<>();
    private boolean[] finished;
    private final List<Sprite> racerSprites = new ArrayList<>();
    
    public Player[] placement;
    private int finishedCount;
    private boolean resultsShown;
    
    /**
     * @param GameInstance shared game instance providing viewport, batch, and nav
     */
    public SimpleRacerMiniGame(CritterParade GameInstance){
        
        this.gameInstance = GameInstance;
                
        playerSize = 1.0f;
        playerCount = gameInstance.getNumPlayers();
        placement = new Player[playerCount];
        finishedCount = 0;
        resultsShown = false;
        finished = new boolean[playerCount];
        
        backgroundTex = new Texture("MiniGames/SimpleRacer/Clouds.png");
        finishLineTex = new Texture("MiniGames/SimpleRacer/FinishLine.png");

        // Use persistent players so crumbs/fruits persist; keep rendering state local
        for (int i = 0; i < playerCount; i++) {
            Player p = gameInstance.getPlayers().get(i);
            racers.add(p);
            finished[i] = false;
            Sprite s = new Sprite(p.getSprite().getTexture());
            s.setSize(playerSize, playerSize);
            racerSprites.add(s);
        }
    }

    @Override
    /**
     * Initializes transient state if needed when the mini game is shown.
     */
    public void show() {
        
    }

    @Override
    /**
     * Frame loop: process input, update game state, then draw.
     */
    public void render(float f) {
        input();
        logic();
        draw();
    }

    @Override
    /**
     * Keep a single shared viewport so mini games render consistently.
     */
    public void resize(int i, int i1) {
        gameInstance.viewport.update(i, i1, true);
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
    /**
     * Reads player inputs and translates them into movement when allowed.
     */
    private void input(){
        float speed = 16f;
        float delta = Gdx.graphics.getDeltaTime();
        for (int i = 0; i < racers.size(); i++) {
            if (!finished[i]) {
                Player p = racers.get(i);
                if (p.justPressedRight()) {
                    racerSprites.get(i).translateX(speed * delta);
                }
            }
        }
    }
    
    /**
     * Clamps sprites to the world bounds and records finish order once a
     * player crosses the line at x=14 in world units.
     */
    private void logic(){
        float worldWidth = gameInstance.viewport.getWorldWidth();
        
        float playerWidth = racerSprites.get(0).getWidth();
        float playerHeight = racerSprites.get(0).getHeight();
        for (int i = 0; i < racers.size(); i++) {
            Player p = racers.get(i);
            Sprite s = racerSprites.get(i);
            s.setX(MathUtils.clamp(s.getX(), 0, worldWidth - playerWidth));
            s.setY(0 + (playerHeight * (p.getID() - 1)));
            if (s.getX() >= 14 && !finished[i]) {
                finished[i] = true;
                placement[finishedCount] = p;
                finishedCount++;
            }
        }
        
        areAllFinished();
    }
    
    private void draw(){
        ScreenUtils.clear(1f, 0.992f, 0.816f, 1f);
        gameInstance.viewport.apply();
        gameInstance.batch.setProjectionMatrix(gameInstance.viewport.getCamera().combined);
        gameInstance.batch.begin();
        
        float worldWidth = gameInstance.viewport.getWorldWidth();
        float worldHeight = gameInstance.viewport.getWorldHeight();
        
        gameInstance.batch.draw(backgroundTex, 0, 0, worldWidth, worldHeight);
        // Finish line at x=14 to leave 2 world units of run-up in a 16x9 world.
        gameInstance.batch.draw(finishLineTex, 14f, 0, 1, worldHeight);
        
        for (int i = 0; i < racerSprites.size(); i++) {
            racerSprites.get(i).draw(gameInstance.batch);
        }
        
        
        gameInstance.batch.end();
    }
    
    // per-player movement handled in input() loop
    
    private void areAllFinished(){
        
        String out = "Placements:\n";
        for(int i = 0; i < placement.length ; i++)
        {
            
            if(placement[i] != null)
            {
                 out += (i + 1) + ". " + placement[i].getID() + "\n";
            }
            else
            {
                out += (i + 1) + ". NULL\n";
            }
            
            System.out.println(out);
        }
        
        if(playerCount == finishedCount && !resultsShown){
            resultsShown = true;
            awardCrumbsByPlacement();
            gameInstance.setScreen(new MiniGameResultScreen(gameInstance, placement));
        }
    }
    
    private void awardCrumbsByPlacement(){
        int[] awards = new int[]{10, 6, 4, 2, 1, 0};
        for(int i = 0; i < placement.length; i++){
            Player p = placement[i];
            if(p != null){
                int add = i < awards.length ? awards[i] : 0;
                p.addCrumbs(add);
            }
        }
    }
    
}

