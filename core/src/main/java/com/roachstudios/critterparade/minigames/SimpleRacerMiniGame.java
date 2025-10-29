/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.roachstudios.critterparade.minigames;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.roachstudios.critterparade.CritterParade;
import com.roachstudios.critterparade.Player;

/**
 *
 * @author Nathan
 */
public class SimpleRacerMiniGame extends MiniGame{
    
    private final CritterParade gameInstance;
    private Texture backgroundTex;
    private Texture finishLineTex;
    
    private final float playerSize; 
    private final int playerCount;
    
    private Texture player1Tex;
    private Player player1;
    private boolean player1Finished;
    
    private Texture player2Tex;
    private Player player2;
    private boolean player2Finished;
    
    private Texture player3Tex;
    private Player player3;
    private boolean player3Finished;
    
    private Texture player4Tex;
    private Player player4;
    private boolean player4Finished;
    
    private Texture player5Tex;
    private Player player5;
    private boolean player5Finished;
    
    private Texture player6Tex;
    private Player player6;
    private boolean player6Finished;
    
    public Player[] placement;
    private int finishedCount;
    
    public SimpleRacerMiniGame(CritterParade GameInstance){
        
        this.gameInstance = GameInstance;
                
        playerSize = 1.0f;
        playerCount = gameInstance.getNumPlayers();
        
        backgroundTex = new Texture("MiniGame/SimpleRacerMiniGame/Clouds.png");
        finishLineTex = new Texture("MiniGame/SimpleRacerMiniGame/FinishLine.png");
        
        player1Tex = new Texture("PlayerSprites/bumble_bee.png");
        player1 = new Player(1, player1Tex);
        player1.setSpriteSize(playerSize);
        player1Finished = false;
        
        player2Tex = new Texture("PlayerSprites/lady_bug.png");
        player2 = new Player(2, player2Tex);
        player2.setSpriteSize(playerSize);
        player2Finished = false;
        
        player3Tex = new Texture("PlayerSprites/pond_frog.png");
        player3 = new Player(3, player3Tex);
        player3.setSpriteSize(playerSize);
        player3Finished = false;
        
        player4Tex = new Texture("PlayerSprites/red_squirrel.png");
        player4 = new Player(4, player4Tex);
        player4.setSpriteSize(playerSize);
        player4Finished = false;
        
        player5Tex = new Texture("PlayerSprites/field_mouse.png");
        player5 = new Player(5, player5Tex);
        player5.setSpriteSize(playerSize);
        player5Finished = false;
        
        player6Tex = new Texture("PlayerSprites/solider_ant.png");
        player6 = new Player(6, player6Tex);
        player6.setSpriteSize(playerSize);
        player6Finished = false;
    }

    @Override
    public void show() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void render(float f) {
        input();
        logic();
        draw();    }

    @Override
    public void resize(int i, int i1) {
        gameInstance.viewport.update(i, i1, true);
    }

    @Override
    public void pause() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void resume() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void hide() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void dispose() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    private void input(){
        
        float speed = 16f;
        float delta = Gdx.graphics.getDeltaTime();
        
        if(playerCount >= 2){
            movePlayer1(speed, delta, !player1Finished);
            movePlayer2(speed, delta, !player2Finished);
        }
        if(playerCount >= 3){
            movePlayer3(speed, delta, !player3Finished);
        }
        if(playerCount >= 4){
            movePlayer4(speed, delta, !player4Finished);
        }
        if(playerCount >= 5){
            movePlayer5(speed, delta, !player5Finished);
        }
        if(playerCount >= 6){
            movePlayer6(speed, delta, !player6Finished);
        }
 
    }
    
    private void logic(){
        float worldWidth = gameInstance.viewport.getWorldWidth();
        float worldHeight = gameInstance.viewport.getWorldHeight();
        
        float playerWidth = player1.getSprite().getWidth();
        float playerHeight = player1.getSprite().getHeight();
        
        player1.getSprite().setX(MathUtils.clamp(player1.getSprite().getX(), 0, worldWidth - playerWidth));
        player1.getSprite().setY(MathUtils.clamp(player1.getSprite().getY(), 0, worldHeight - playerHeight));
        
        if(player1.getSprite().getX() >= 14 && !player1Finished){
            player1Finished = true;
            placement[finishedCount] = player1;
            finishedCount++;
        }
        
        player2.getSprite().setX(MathUtils.clamp(player2.getSprite().getX(), 0, worldWidth - playerWidth));
        //player2.getSprite().setY(MathUtils.clamp(player2.getSprite().getY(), 0, worldHeight - playerHeight));
        player2.getSprite().setY(0 + (playerHeight * (player2.getID() - 1)));
        
        if(player2.getSprite().getX() >= 14 && !player2Finished){
            player2Finished = true;
            placement[finishedCount] = player2;
            finishedCount++;
        }
        
        player3.getSprite().setX(MathUtils.clamp(player3.getSprite().getX(), 0, worldWidth - playerWidth));
        //player3.getSprite().setY(MathUtils.clamp(player3.getSprite().getY(), 0, worldHeight - playerHeight));
        player3.getSprite().setY(0 + (playerHeight * (player3.getID() - 1)));
        
        if(player3.getSprite().getX() >= 14 && !player3Finished){
            player3Finished = true;
            placement[finishedCount] = player3;
            finishedCount++;
        }
        
        player4.getSprite().setX(MathUtils.clamp(player4.getSprite().getX(), 0, worldWidth - playerWidth));
        //player4.getSprite().setY(MathUtils.clamp(player4.getSprite().getY(), 0, worldHeight - playerHeight));
        player4.getSprite().setY(0 + (playerHeight * (player4.getID() - 1)));
        
        if(player4.getSprite().getX() >= 14 && !player4Finished){
            player4Finished = true;
            placement[finishedCount] = player4;
            finishedCount++;
        }
        
        player5.getSprite().setX(MathUtils.clamp(player5.getSprite().getX(), 0, worldWidth - playerWidth));
        //player5.getSprite().setY(MathUtils.clamp(player5.getSprite().getY(), 0, worldHeight - playerHeight));
        player5.getSprite().setY(0 + (playerHeight * (player5.getID() - 1)));
        
        if(player5.getSprite().getX() >= 14 && !player5Finished){
            player5Finished = true;
            placement[finishedCount] = player5;
            finishedCount++;
        }
        
        player6.getSprite().setX(MathUtils.clamp(player6.getSprite().getX(), 0, worldWidth - playerWidth));
        //player6.getSprite().setY(MathUtils.clamp(player6.getSprite().getY(), 0, worldHeight - playerHeight));
        player6.getSprite().setY(0 + (playerHeight * (player6.getID() - 1)));
        
        if(player6.getSprite().getX() >= 14 && !player6Finished){
            player6Finished = true;
            placement[finishedCount] = player6;
            finishedCount++;
        }
        
        areAllFinished();
    }
    
    private void draw(){
        ScreenUtils.clear(Color.BLACK);
        gameInstance.viewport.apply();
        gameInstance.batch.setProjectionMatrix(gameInstance.viewport.getCamera().combined);
        gameInstance.batch.begin();
        
        float worldWidth = gameInstance.viewport.getWorldWidth();
        float worldHeight = gameInstance.viewport.getWorldHeight();
        
        gameInstance.batch.draw(backgroundTex, 0, 0, worldWidth, worldHeight);
        gameInstance.batch.draw(finishLineTex, 14f, 0, 1, worldHeight);
        
        if(playerCount >= 2){
            player1.getSprite().draw(gameInstance.batch);
            player2.getSprite().draw(gameInstance.batch);
        }
        if(playerCount >= 3){
            player3.getSprite().draw(gameInstance.batch);
        }
        if(playerCount >= 4){
            player4.getSprite().draw(gameInstance.batch);
        }
        if(playerCount >= 5){
            player5.getSprite().draw(gameInstance.batch);
        }
        if(playerCount >= 6){
            player6.getSprite().draw(gameInstance.batch);
        }
        
        
        gameInstance.batch.end();
    }
    
    private void movePlayer1(float speed, float delta, boolean canMove){
        
        if(player1.justPressedRight() && canMove){
            player1.getSprite().translateX(speed * delta);
            System.out.println("X: " + player1.getSprite().getX() + ", Y: "
            + player1.getSprite().getY());
        } 
    }
    
    private void movePlayer2(float speed, float delta, boolean canMove){
       
        if(player2.justPressedRight() && canMove){
            
            player2.getSprite().translateX(speed * delta);
            
        }
    }
    
    private void movePlayer3(float speed, float delta, boolean canMove){
       
        if(player3.justPressedRight() && canMove){
            
            player3.getSprite().translateX(speed * delta);
            
        }
    }
    
    private void movePlayer4(float speed, float delta, boolean canMove){
       
        if(player4.justPressedRight() && canMove){
            
            player4.getSprite().translateX(speed * delta);
            
        }
    }
    
    private void movePlayer5(float speed, float delta, boolean canMove){
       
        if(player5.justPressedRight() && canMove){
            
            player5.getSprite().translateX(speed * delta);
            
        }
    }
    
    private void movePlayer6(float speed, float delta, boolean canMove){
        
        if(player6.justPressedRight() && canMove){
            
            player6.getSprite().translateX(speed * delta);
            
        }
    }
    
    private boolean areAllFinished(){
        System.out.println("finishedCount: " + finishedCount);
        for (int i = 0; i < placement.length; i++) {
            String out = (i + 1) + ". Player ";
            if(placement[i] != null){
                out += placement[i].getID();
            }
            else{
                out += "NULL";
            }
            System.out.println(out);
        }
        
        return playerCount == finishedCount;
    }
    
}

