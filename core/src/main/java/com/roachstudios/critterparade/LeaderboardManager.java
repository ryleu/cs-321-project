package com.roachstudios.critterparade;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.ObjectMap;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages persistent leaderboard data stored in ~/.critterparade/leaderboard.json.
 * 
 * <p>Supports different score types per minigame:
 * <ul>
 *   <li>Lower-is-better (e.g., race times)</li>
 *   <li>Higher-is-better (e.g., survival time, catch count)</li>
 * </ul>
 */
public class LeaderboardManager {
    
    private static final String APP_FOLDER = ".critterparade";
    private static final String LEADERBOARD_FILE = "leaderboard.json";
    
    /** Maximum number of scores to keep per minigame. */
    private static final int MAX_SCORES_PER_GAME = 10;
    
    /** Map of minigame name to list of scores. */
    private ObjectMap<String, Array<MiniGameScore>> leaderboards;
    
    /** Map of minigame name to whether lower score is better. */
    private ObjectMap<String, Boolean> lowerIsBetter;
    
    /**
     * Creates the leaderboard manager and loads existing data if present.
     */
    public LeaderboardManager() {
        leaderboards = new ObjectMap<>();
        lowerIsBetter = new ObjectMap<>();
        
        // Configure score direction for each minigame
        lowerIsBetter.put("Simple Racer", true);      // Faster time is better
        lowerIsBetter.put("Dodgeball", false);        // Longer survival is better
        lowerIsBetter.put("Catching Stars", false);   // More stars is better
        
        load();
    }
    
    /**
     * Submits a new score for a minigame.
     * The score will be added and sorted, keeping only the top entries.
     *
     * @param minigameName the name of the minigame
     * @param playerName the name of the player
     * @param scoreValue the score value
     * @return true if the score made it onto the leaderboard
     */
    public boolean submitScore(String minigameName, String playerName, float scoreValue) {
        Array<MiniGameScore> scores = leaderboards.get(minigameName);
        if (scores == null) {
            scores = new Array<>();
            leaderboards.put(minigameName, scores);
        }
        
        MiniGameScore newScore = new MiniGameScore(playerName, scoreValue);
        scores.add(newScore);
        
        // Sort based on whether lower or higher is better
        Boolean lower = lowerIsBetter.get(minigameName);
        boolean isLowerBetter = lower != null ? lower : false;
        sortScores(scores, isLowerBetter);
        
        // Trim to max size
        while (scores.size > MAX_SCORES_PER_GAME) {
            scores.removeIndex(scores.size - 1);
        }
        
        // Save after each submission
        save();
        
        // Check if the new score is still in the list
        return scores.contains(newScore, false);
    }
    
    /**
     * Gets the leaderboard for a specific minigame.
     *
     * @param minigameName the name of the minigame
     * @return list of scores, sorted best to worst
     */
    public List<MiniGameScore> getScores(String minigameName) {
        Array<MiniGameScore> scores = leaderboards.get(minigameName);
        if (scores == null) {
            return Collections.emptyList();
        }
        // Convert to Java List for compatibility
        List<MiniGameScore> result = new ArrayList<>();
        for (MiniGameScore score : scores) {
            result.add(score);
        }
        return result;
    }
    
    /**
     * Gets all minigame names that have leaderboard entries.
     *
     * @return list of minigame names with scores
     */
    public List<String> getMinigamesWithScores() {
        List<String> result = new ArrayList<>();
        for (String key : leaderboards.keys()) {
            result.add(key);
        }
        return result;
    }
    
    /**
     * Gets whether lower scores are better for a minigame.
     *
     * @param minigameName the name of the minigame
     * @return true if lower is better (like race times)
     */
    public boolean isLowerBetter(String minigameName) {
        Boolean lower = lowerIsBetter.get(minigameName);
        return lower != null ? lower : false;
    }
    
    /**
     * Formats a score value for display based on the minigame type.
     *
     * @param minigameName the name of the minigame
     * @param scoreValue the score value to format
     * @return formatted string representation
     */
    public String formatScore(String minigameName, float scoreValue) {
        if (minigameName.equals("Simple Racer") || minigameName.equals("Dodgeball")) {
            // Format as time (seconds with 2 decimal places)
            return String.format("%.2fs", scoreValue);
        } else if (minigameName.equals("Catching Stars")) {
            // Format as integer count
            return String.format("%d stars", (int) scoreValue);
        }
        // Default formatting
        return String.format("%.0f", scoreValue);
    }
    
    /**
     * Clears all leaderboard data.
     */
    public void clearAll() {
        leaderboards.clear();
        save();
    }
    
    /**
     * Clears leaderboard data for a specific minigame.
     *
     * @param minigameName the minigame to clear
     */
    public void clearMinigame(String minigameName) {
        leaderboards.remove(minigameName);
        save();
    }
    
    /**
     * Sorts scores by value, best first.
     */
    private void sortScores(Array<MiniGameScore> scores, boolean lowerIsBetter) {
        if (lowerIsBetter) {
            scores.sort((a, b) -> Float.compare(a.getScoreValue(), b.getScoreValue()));
        } else {
            scores.sort((a, b) -> Float.compare(b.getScoreValue(), a.getScoreValue()));
        }
    }
    
    /**
     * Creates a configured Json instance for serialization.
     */
    private Json createJson() {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        return json;
    }
    
    /**
     * Loads leaderboard data from the JSON file.
     * If the file is malformed, it will be deleted and a fresh start begins.
     */
    @SuppressWarnings("unchecked")
    private void load() {
        File leaderboardFile = getLeaderboardFile();
        
        if (!leaderboardFile.exists()) {
            return;
        }
        
        try (FileReader reader = new FileReader(leaderboardFile)) {
            Json json = createJson();
            
            // Read as ObjectMap and manually convert entries
            ObjectMap<String, Array<Object>> rawData = json.fromJson(ObjectMap.class, reader);
            
            if (rawData != null) {
                for (ObjectMap.Entry<String, Array<Object>> entry : rawData.entries()) {
                    Array<MiniGameScore> scores = new Array<>();
                    
                    for (Object obj : entry.value) {
                        if (obj instanceof MiniGameScore) {
                            scores.add((MiniGameScore) obj);
                        } else if (obj instanceof ObjectMap) {
                            // Manually convert ObjectMap to MiniGameScore
                            ObjectMap<String, Object> map = (ObjectMap<String, Object>) obj;
                            MiniGameScore score = new MiniGameScore();
                            
                            Object name = map.get("playerName");
                            if (name != null) score.setPlayerName(name.toString());
                            
                            Object value = map.get("scoreValue");
                            if (value instanceof Number) score.setScoreValue(((Number) value).floatValue());
                            
                            Object time = map.get("timestamp");
                            if (time instanceof Number) score.setTimestamp(((Number) time).longValue());
                            
                            scores.add(score);
                        }
                    }
                    
                    leaderboards.put(entry.key, scores);
                }
            }
        } catch (IOException e) {
            System.err.println("[LeaderboardManager] Failed to load leaderboard: " + e.getMessage());
        } catch (Exception e) {
            // Malformed file - delete it and start fresh
            System.err.println("[LeaderboardManager] Malformed leaderboard file, deleting: " + e.getMessage());
            if (leaderboardFile.delete()) {
                System.out.println("[LeaderboardManager] Deleted malformed leaderboard file");
            }
        }
    }
    
    /**
     * Saves leaderboard data to the JSON file using libGDX types.
     */
    private void save() {
        File leaderboardFile = getLeaderboardFile();
        
        // Ensure parent directory exists
        File parentDir = leaderboardFile.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            System.err.println("[LeaderboardManager] Failed to create leaderboard directory");
            return;
        }
        
        try (FileWriter writer = new FileWriter(leaderboardFile)) {
            Json json = createJson();
            writer.write(json.prettyPrint(leaderboards));
        } catch (IOException e) {
            System.err.println("[LeaderboardManager] Failed to save leaderboard: " + e.getMessage());
        }
    }
    
    /**
     * Gets the leaderboard file path.
     */
    private File getLeaderboardFile() {
        String homeDir = System.getProperty("user.home");
        File appDir = new File(homeDir, APP_FOLDER);
        return new File(appDir, LEADERBOARD_FILE);
    }
}

