package com.roachstudios.critterparade;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    private Map<String, List<MiniGameScore>> leaderboards;
    
    /** Map of minigame name to whether lower score is better. */
    private Map<String, Boolean> lowerIsBetter;
    
    /**
     * Creates the leaderboard manager and loads existing data if present.
     */
    public LeaderboardManager() {
        leaderboards = new LinkedHashMap<>();
        lowerIsBetter = new LinkedHashMap<>();
        
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
        List<MiniGameScore> scores = leaderboards.computeIfAbsent(minigameName, k -> new ArrayList<>());
        
        MiniGameScore newScore = new MiniGameScore(playerName, scoreValue);
        scores.add(newScore);
        
        // Sort based on whether lower or higher is better
        boolean isLowerBetter = lowerIsBetter.getOrDefault(minigameName, false);
        sortScores(scores, isLowerBetter);
        
        // Trim to max size
        while (scores.size() > MAX_SCORES_PER_GAME) {
            scores.remove(scores.size() - 1);
        }
        
        // Save after each submission
        save();
        
        // Check if the new score is still in the list
        return scores.contains(newScore);
    }
    
    /**
     * Gets the leaderboard for a specific minigame.
     *
     * @param minigameName the name of the minigame
     * @return unmodifiable list of scores, sorted best to worst
     */
    public List<MiniGameScore> getScores(String minigameName) {
        List<MiniGameScore> scores = leaderboards.get(minigameName);
        if (scores == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(scores);
    }
    
    /**
     * Gets all minigame names that have leaderboard entries.
     *
     * @return list of minigame names with scores
     */
    public List<String> getMinigamesWithScores() {
        return new ArrayList<>(leaderboards.keySet());
    }
    
    /**
     * Gets whether lower scores are better for a minigame.
     *
     * @param minigameName the name of the minigame
     * @return true if lower is better (like race times)
     */
    public boolean isLowerBetter(String minigameName) {
        return lowerIsBetter.getOrDefault(minigameName, false);
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
    private void sortScores(List<MiniGameScore> scores, boolean lowerIsBetter) {
        if (lowerIsBetter) {
            scores.sort(Comparator.comparingDouble(MiniGameScore::getScoreValue));
        } else {
            scores.sort((a, b) -> Float.compare(b.getScoreValue(), a.getScoreValue()));
        }
    }
    
    /**
     * Loads leaderboard data from the JSON file.
     */
    @SuppressWarnings("unchecked")
    private void load() {
        File leaderboardFile = getLeaderboardFile();
        
        if (!leaderboardFile.exists()) {
            return;
        }
        
        try (FileReader reader = new FileReader(leaderboardFile)) {
            Json json = new Json();
            Map<String, Object> data = json.fromJson(LinkedHashMap.class, reader);
            
            if (data != null) {
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    String minigameName = entry.getKey();
                    ArrayList<LinkedHashMap<String, Object>> scoreList = 
                        (ArrayList<LinkedHashMap<String, Object>>) entry.getValue();
                    
                    List<MiniGameScore> scores = new ArrayList<>();
                    for (LinkedHashMap<String, Object> scoreData : scoreList) {
                        MiniGameScore score = new MiniGameScore();
                        
                        Object playerNameObj = scoreData.get("playerName");
                        if (playerNameObj != null) {
                            score.setPlayerName(playerNameObj.toString());
                        }
                        
                        Object scoreValueObj = scoreData.get("scoreValue");
                        if (scoreValueObj != null) {
                            score.setScoreValue(((Number) scoreValueObj).floatValue());
                        }
                        
                        Object timestampObj = scoreData.get("timestamp");
                        if (timestampObj != null) {
                            score.setTimestamp(((Number) timestampObj).longValue());
                        }
                        
                        scores.add(score);
                    }
                    
                    leaderboards.put(minigameName, scores);
                }
            }
        } catch (IOException e) {
            System.err.println("[LeaderboardManager] Failed to load leaderboard: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[LeaderboardManager] Error parsing leaderboard data: " + e.getMessage());
        }
    }
    
    /**
     * Saves leaderboard data to the JSON file.
     */
    private void save() {
        File leaderboardFile = getLeaderboardFile();
        
        // Ensure parent directory exists
        File parentDir = leaderboardFile.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            System.err.println("[LeaderboardManager] Failed to create leaderboard directory");
            return;
        }
        
        // Convert to serializable format
        Map<String, List<Map<String, Object>>> data = new LinkedHashMap<>();
        for (Map.Entry<String, List<MiniGameScore>> entry : leaderboards.entrySet()) {
            List<Map<String, Object>> scoreList = new ArrayList<>();
            for (MiniGameScore score : entry.getValue()) {
                Map<String, Object> scoreData = new LinkedHashMap<>();
                scoreData.put("playerName", score.getPlayerName());
                scoreData.put("scoreValue", score.getScoreValue());
                scoreData.put("timestamp", score.getTimestamp());
                scoreList.add(scoreData);
            }
            data.put(entry.getKey(), scoreList);
        }
        
        try (FileWriter writer = new FileWriter(leaderboardFile)) {
            Json json = new Json();
            json.setOutputType(JsonWriter.OutputType.json);
            writer.write(json.prettyPrint(data));
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

