package com.roachstudios.critterparade;

/**
 * Represents a single high score entry for a minigame.
 * 
 * <p>Score values are interpreted differently per minigame:
 * <ul>
 *   <li>SimpleRacer: finish time in seconds (lower is better)</li>
 *   <li>Dodgeball: survival time in seconds (higher is better)</li>
 *   <li>Catching Stars: stars caught count (higher is better)</li>
 * </ul>
 */
public class MiniGameScore {
    
    /** Name of the critter who achieved this score. */
    private String playerName;
    
    /** The score value (interpretation depends on minigame). */
    private float scoreValue;
    
    /** Timestamp when this score was achieved (epoch millis). */
    private long timestamp;
    
    /**
     * Default constructor for JSON deserialization.
     */
    public MiniGameScore() {
        this.playerName = "";
        this.scoreValue = 0f;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * Constructs a new score entry.
     *
     * @param playerName name of the player who achieved this score
     * @param scoreValue the score value
     */
    public MiniGameScore(String playerName, float scoreValue) {
        this.playerName = playerName;
        this.scoreValue = scoreValue;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * Gets the player name.
     *
     * @return the name of the critter who achieved this score
     */
    public String getPlayerName() {
        return playerName;
    }
    
    /**
     * Sets the player name.
     *
     * @param playerName the player name
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    /**
     * Gets the score value.
     *
     * @return the score value
     */
    public float getScoreValue() {
        return scoreValue;
    }
    
    /**
     * Sets the score value.
     *
     * @param scoreValue the score value
     */
    public void setScoreValue(float scoreValue) {
        this.scoreValue = scoreValue;
    }
    
    /**
     * Gets the timestamp when this score was achieved.
     *
     * @return epoch millis timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Sets the timestamp.
     *
     * @param timestamp epoch millis timestamp
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

