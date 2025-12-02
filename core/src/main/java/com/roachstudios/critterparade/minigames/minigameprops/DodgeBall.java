package com.roachstudios.critterparade.minigames.minigameprops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

/**
 * Represents a projectile ball in the Dodgeball minigame.
 * 
 * <p>Each ball has a position, direction, and collision bounds. Balls spawn
 * from the edges of the screen and travel in one of four cardinal directions
 * (up, down, left, right) until they exit the play area.</p>
 * 
 * <p>Directions are encoded as:
 * <ul>
 *   <li>0 = moving up</li>
 *   <li>1 = moving down</li>
 *   <li>2 = moving left</li>
 *   <li>3 = moving right</li>
 * </ul>
 */
public class DodgeBall {
    
    /** Path to the ball texture asset. */
    private static final String BALL_TEXTURE_PATH = "MiniGames/DodgeBall/Ball.png";
    
    /** Default size of the ball in world units. */
    private static final float BALL_SIZE = 1.0f;
    
    private final Texture ballTex;
    private final Sprite ballSprite;
    private final Rectangle ballBounds;
    
    /** Direction of movement (0=up, 1=down, 2=left, 3=right). */
    private int direction = 0;
    
    /**
     * Constructs a new dodge ball with default texture and size.
     */
    public DodgeBall() {
        this.ballTex = new Texture(BALL_TEXTURE_PATH);
        this.ballSprite = new Sprite(ballTex);
        this.ballSprite.setSize(BALL_SIZE, BALL_SIZE);
        this.ballBounds = new Rectangle(
            this.ballSprite.getX(), 
            this.ballSprite.getY(), 
            BALL_SIZE, 
            BALL_SIZE
        );
    }
    
    /**
     * Gets the sprite for rendering this ball.
     *
     * @return the ball's sprite
     */
    public Sprite getSprite() {
        return this.ballSprite;
    }
    
    /**
     * Gets the current direction of movement.
     *
     * @return direction code (0=up, 1=down, 2=left, 3=right)
     */
    public int getDirection() {
        return this.direction;
    }
    
    /**
     * Sets the direction of movement.
     *
     * @param direction direction code (0=up, 1=down, 2=left, 3=right)
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }
    
    /**
     * Gets the collision bounds for this ball.
     *
     * @return the bounding rectangle for collision detection
     */
    public Rectangle getBounds() {
        return this.ballBounds;
    }
    
    /**
     * Moves the ball by the specified delta values and updates bounds.
     *
     * @param deltaX horizontal movement amount
     * @param deltaY vertical movement amount
     */
    public void move(float deltaX, float deltaY) {
        this.ballSprite.setX(this.ballSprite.getX() + deltaX);
        this.ballSprite.setY(this.ballSprite.getY() + deltaY);
        
        this.ballBounds.setX(this.ballBounds.getX() + deltaX);
        this.ballBounds.setY(this.ballBounds.getY() + deltaY);
    }
    
    /**
     * Disposes of the ball's texture resources.
     */
    public void dispose() {
        if (ballTex != null) {
            ballTex.dispose();
        }
    }
}

