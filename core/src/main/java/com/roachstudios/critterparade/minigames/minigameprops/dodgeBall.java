package com.roachstudios.critterparade.minigames.minigameprops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

/**
 *
 * @author Nathan
 */
public class dodgeBall {
    
    private Texture ballTex;
    private Sprite ballSprite;
    private Rectangle ballBounds;
    
    private int direction = 0;
    
    public dodgeBall(){
        this.ballTex = new Texture("MiniGames/DodgeBall/Ball.png");
        this.ballSprite = new Sprite(ballTex);
        this.ballSprite.setSize(1, 1);
        this.ballBounds = new Rectangle(this.ballSprite.getX(), this.ballSprite.getY(), 1, 1);
    }
    
    public Sprite getSprite(){
        return this.ballSprite;
    }
    
    public int getDirection(){
        return this.direction;
    }
    
    public void changeDirection(int input){
        this.direction = input;
    }
    
    public Rectangle getBounds(){
        return this.ballBounds;
    }
    
    public void moveBall(float x, float y){
        this.ballSprite.setX(this.ballSprite.getX()+ x);
        this.ballSprite.setY(this.ballSprite.getY()+ y);
        
        this.ballBounds.setX(this.ballBounds.getX()+ x);
        this.ballBounds.setY(this.ballBounds.getY()+ y);
    }
    
}
