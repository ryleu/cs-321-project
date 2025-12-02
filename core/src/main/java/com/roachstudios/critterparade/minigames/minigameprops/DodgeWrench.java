package com.roachstudios.critterparade.minigames.minigameprops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

/**
 * Represents a rare wrench projectile in the Dodgeball minigame.
 * 
 * <p>The wrench is a special projectile with a 1% spawn chance that moves
 * at double the speed of regular balls and rotates 90 degrees every 0.5 seconds.</p>
 * 
 * <p>Directions are encoded as:
 * <ul>
 *   <li>0 = moving up</li>
 *   <li>1 = moving down</li>
 *   <li>2 = moving left</li>
 *   <li>3 = moving right</li>
 * </ul>
 */
public class DodgeWrench {
    
    /** Path to the wrench texture asset. */
    private static final String WRENCH_TEXTURE_PATH = "MiniGames/DodgeBall/Wrench.png";
    
    /** Default size of the wrench in world units. */
    private static final float WRENCH_SIZE = 1.0f;
    
    /** Time interval between 90-degree rotations in seconds. */
    private static final float ROTATION_INTERVAL = 0.5f;
    
    private final Texture wrenchTex;
    private final Sprite wrenchSprite;
    private final Rectangle wrenchBounds;
    
    /** Direction of movement (0=up, 1=down, 2=left, 3=right). */
    private int direction = 0;
    
    /** Timer tracking time since last rotation. */
    private float rotationTimer = 0f;
    
    /**
     * Constructs a new dodge wrench with default texture and size.
     */
    public DodgeWrench() {
        this.wrenchTex = new Texture(WRENCH_TEXTURE_PATH);
        this.wrenchSprite = new Sprite(wrenchTex);
        this.wrenchSprite.setSize(WRENCH_SIZE, WRENCH_SIZE);
        this.wrenchSprite.setOriginCenter();
        this.wrenchBounds = new Rectangle(
            this.wrenchSprite.getX(), 
            this.wrenchSprite.getY(), 
            WRENCH_SIZE, 
            WRENCH_SIZE
        );
    }
    
    /**
     * Gets the sprite for rendering this wrench.
     *
     * @return the wrench's sprite
     */
    public Sprite getSprite() {
        return this.wrenchSprite;
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
     * Gets the collision bounds for this wrench.
     *
     * @return the bounding rectangle for collision detection
     */
    public Rectangle getBounds() {
        return this.wrenchBounds;
    }
    
    /**
     * Updates the wrench rotation based on elapsed time.
     * Rotates 90 degrees every 0.5 seconds.
     *
     * @param delta time since last frame in seconds
     */
    public void updateRotation(float delta) {
        rotationTimer += delta;
        if (rotationTimer >= ROTATION_INTERVAL) {
            rotationTimer -= ROTATION_INTERVAL;
            wrenchSprite.rotate(90f);
        }
    }
    
    /**
     * Moves the wrench by the specified delta values and updates bounds.
     *
     * @param deltaX horizontal movement amount
     * @param deltaY vertical movement amount
     */
    public void move(float deltaX, float deltaY) {
        this.wrenchSprite.setX(this.wrenchSprite.getX() + deltaX);
        this.wrenchSprite.setY(this.wrenchSprite.getY() + deltaY);
        
        this.wrenchBounds.setX(this.wrenchBounds.getX() + deltaX);
        this.wrenchBounds.setY(this.wrenchBounds.getY() + deltaY);
    }
    
    /**
     * Disposes of the wrench's texture resources.
     */
    public void dispose() {
        if (wrenchTex != null) {
            wrenchTex.dispose();
        }
    }
}

