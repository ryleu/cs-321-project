package com.roachstudios.critterparade;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks game events during a session and saves them as JSON on game close.
 * Events are stored in memory and written to ~/.critterparade/logs/ with a
 * timestamp-based filename.
 */
public class SessionLogger {
    
    private static final String APP_FOLDER = ".critterparade";
    private static final String LOGS_FOLDER = "logs";
    private static final DateTimeFormatter FILE_DATE_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final DateTimeFormatter EVENT_DATE_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    
    private final List<Map<String, Object>> events = new ArrayList<>();
    private final LocalDateTime sessionStart;
    private final boolean enabled;
    
    /**
     * Creates a new session logger.
     *
     * @param enabled true if logging should be active
     */
    public SessionLogger(boolean enabled) {
        this.enabled = enabled;
        this.sessionStart = LocalDateTime.now();
        
        if (enabled) {
            logEvent("session_start", getSystemInfo());
        }
    }
    
    /**
     * Gathers system information including OS and hardware specs.
     * Works on Linux, macOS, and Windows.
     *
     * @return map containing system information
     */
    private Map<String, Object> getSystemInfo() {
        Map<String, Object> systemInfo = new LinkedHashMap<>();
        
        // Operating System info
        Map<String, Object> osInfo = new LinkedHashMap<>();
        osInfo.put("name", System.getProperty("os.name"));
        osInfo.put("version", System.getProperty("os.version"));
        osInfo.put("arch", System.getProperty("os.arch"));
        systemInfo.put("os", osInfo);
        
        // Java runtime info
        Map<String, Object> javaInfo = new LinkedHashMap<>();
        javaInfo.put("version", System.getProperty("java.version"));
        javaInfo.put("vendor", System.getProperty("java.vendor"));
        javaInfo.put("vm_name", System.getProperty("java.vm.name"));
        systemInfo.put("java", javaInfo);
        
        // Hardware info
        Map<String, Object> hardwareInfo = new LinkedHashMap<>();
        Runtime runtime = Runtime.getRuntime();
        hardwareInfo.put("cpu_cores", runtime.availableProcessors());
        hardwareInfo.put("max_memory_mb", runtime.maxMemory() / (1024 * 1024));
        hardwareInfo.put("total_memory_mb", runtime.totalMemory() / (1024 * 1024));
        systemInfo.put("hardware", hardwareInfo);
        
        // User locale info (useful for internationalization)
        Map<String, Object> localeInfo = new LinkedHashMap<>();
        localeInfo.put("language", System.getProperty("user.language"));
        localeInfo.put("country", System.getProperty("user.country"));
        localeInfo.put("timezone", java.util.TimeZone.getDefault().getID());
        systemInfo.put("locale", localeInfo);
        
        return systemInfo;
    }
    
    /**
     * Logs an event with the given type and optional data.
     *
     * @param eventType the type of event (e.g., "minigame_start")
     * @param data additional event data, or null
     */
    public void logEvent(String eventType, Map<String, Object> data) {
        if (!enabled) return;
        
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("timestamp", LocalDateTime.now().format(EVENT_DATE_FORMAT));
        event.put("type", eventType);
        if (data != null && !data.isEmpty()) {
            event.put("data", data);
        }
        events.add(event);
    }
    
    /**
     * Convenience method to log an event with a single key-value pair.
     *
     * @param eventType the type of event to log
     * @param key the key for the data value
     * @param value the data value to log
     */
    public void logEvent(String eventType, String key, Object value) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put(key, value);
        logEvent(eventType, data);
    }
    
    /**
     * Convenience method to log an event with no additional data.
     *
     * @param eventType the type of event to log
     */
    public void logEvent(String eventType) {
        logEvent(eventType, (Map<String, Object>) null);
    }
    
    /**
     * Logs a mode selection event.
     *
     * @param mode the game mode that was selected
     */
    public void logModeSelected(CritterParade.Mode mode) {
        logEvent("mode_selected", "mode", mode.name());
    }
    
    /**
     * Logs player initialization.
     *
     * @param count the number of players initialized
     * @param names the names of the players
     */
    public void logPlayersInitialized(int count, String[] names) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("count", count);
        data.put("players", names);
        logEvent("players_initialized", data);
    }
    
    /**
     * Logs a minigame starting.
     *
     * @param minigameName the name of the minigame being started
     */
    public void logMinigameStart(String minigameName) {
        logEvent("minigame_start", "name", minigameName);
    }
    
    /**
     * Logs minigame results.
     *
     * @param minigameName the name of the minigame that ended
     * @param placements the player names in placement order (1st to last)
     * @param crumbsAwarded the crumbs awarded to each player
     */
    public void logMinigameEnd(String minigameName, String[] placements, int[] crumbsAwarded) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("name", minigameName);
        data.put("placements", placements);
        data.put("crumbs_awarded", crumbsAwarded);
        logEvent("minigame_end", data);
    }
    
    /**
     * Logs a board game starting.
     *
     * @param boardName the name of the board being started
     */
    public void logBoardStart(String boardName) {
        logEvent("board_start", "name", boardName);
    }
    
    /**
     * Logs a player turn.
     *
     * @param playerName the name of the player taking their turn
     * @param diceRoll the result of the dice roll
     */
    public void logPlayerTurn(String playerName, int diceRoll) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("player", playerName);
        data.put("dice_roll", diceRoll);
        logEvent("player_turn", data);
    }
    
    /**
     * Logs navigation to a screen.
     *
     * @param screenName the name of the screen being navigated to
     */
    public void logScreenChange(String screenName) {
        logEvent("screen_change", "screen", screenName);
    }
    
    /**
     * Saves the session log to a JSON file.
     * Called on game dispose.
     */
    public void saveSession() {
        if (!enabled || events.isEmpty()) return;
        
        logEvent("session_end", null);
        
        try {
            File logsDir = getLogsDirectory();
            if (logsDir == null) {
                System.err.println("[SessionLogger] Failed to create logs directory");
                return;
            }
            
            String filename = sessionStart.format(FILE_DATE_FORMAT) + ".json";
            File logFile = new File(logsDir, filename);
            
            Map<String, Object> session = new LinkedHashMap<>();
            session.put("session_start", sessionStart.format(EVENT_DATE_FORMAT));
            session.put("session_end", LocalDateTime.now().format(EVENT_DATE_FORMAT));
            session.put("system", getSystemInfo());
            session.put("events", events);
            
            Json json = new Json();
            json.setOutputType(JsonWriter.OutputType.json);
            String jsonString = json.prettyPrint(session);
            
            try (FileWriter writer = new FileWriter(logFile)) {
                writer.write(jsonString);
            }
            
            System.out.println("[SessionLogger] Session saved to: " + logFile.getAbsolutePath());
            
        } catch (IOException e) {
            System.err.println("[SessionLogger] Failed to save session: " + e.getMessage());
        }
    }
    
    /**
     * Gets or creates the logs directory.
     *
     * @return the logs directory, or null if creation failed
     */
    private File getLogsDirectory() {
        String homeDir = System.getProperty("user.home");
        File appDir = new File(homeDir, APP_FOLDER);
        File logsDir = new File(appDir, LOGS_FOLDER);
        
        if (!logsDir.exists() && !logsDir.mkdirs()) {
            return null;
        }
        
        return logsDir;
    }
    
    /**
     * Checks if logging is enabled.
     *
     * @return true if logging is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
}

