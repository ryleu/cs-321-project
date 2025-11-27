package com.roachstudios.critterparade;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manages persistent game settings stored in ~/.critterparade/settings.json.
 * Handles first-run detection and logging consent preferences.
 */
public class SettingsManager {
    
    private static final String APP_FOLDER = ".critterparade";
    private static final String SETTINGS_FILE = "settings.json";
    
    private boolean loggingEnabled = false;
    private boolean firstRun = true;
    
    /**
     * Creates the settings manager and loads existing settings if present.
     */
    public SettingsManager() {
        load();
    }
    
    /**
     * Loads settings from the settings file.
     */
    @SuppressWarnings("unchecked")
    private void load() {
        File settingsFile = getSettingsFile();
        
        if (!settingsFile.exists()) {
            firstRun = true;
            return;
        }
        
        try (FileReader reader = new FileReader(settingsFile)) {
            Json json = new Json();
            Map<String, Object> settings = json.fromJson(LinkedHashMap.class, reader);
            
            if (settings != null) {
                firstRun = false;
                
                if (settings.containsKey("logging_enabled")) {
                    loggingEnabled = Boolean.TRUE.equals(settings.get("logging_enabled"));
                }
            }
        } catch (IOException e) {
            System.err.println("[SettingsManager] Failed to load settings: " + e.getMessage());
            firstRun = true;
        }
    }
    
    /**
     * Saves current settings to the settings file.
     */
    public void save() {
        File settingsFile = getSettingsFile();
        
        // Ensure parent directory exists
        File parentDir = settingsFile.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            System.err.println("[SettingsManager] Failed to create settings directory");
            return;
        }
        
        Map<String, Object> settings = new LinkedHashMap<>();
        settings.put("logging_enabled", loggingEnabled);
        
        try (FileWriter writer = new FileWriter(settingsFile)) {
            Json json = new Json();
            json.setOutputType(JsonWriter.OutputType.json);
            writer.write(json.prettyPrint(settings));
        } catch (IOException e) {
            System.err.println("[SettingsManager] Failed to save settings: " + e.getMessage());
        }
    }
    
    /**
     * Gets the settings file path.
     */
    private File getSettingsFile() {
        String homeDir = System.getProperty("user.home");
        File appDir = new File(homeDir, APP_FOLDER);
        return new File(appDir, SETTINGS_FILE);
    }
    
    /**
     * @return true if this is the first time the game has been run
     */
    public boolean isFirstRun() {
        return firstRun;
    }
    
    /**
     * @return true if session logging is enabled
     */
    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }
    
    /**
     * Sets whether session logging is enabled.
     *
     * @param enabled true to enable logging
     */
    public void setLoggingEnabled(boolean enabled) {
        this.loggingEnabled = enabled;
        this.firstRun = false;
    }
}

