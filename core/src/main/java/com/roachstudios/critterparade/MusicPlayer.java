package com.roachstudios.critterparade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;

import java.util.Random;

/**
 * Centralized music player that manages all game music themes.
 * Handles loading, playing, stopping, and transitioning between tracks.
 * 
 * <p>Themes available:</p>
 * <ul>
 *   <li>INTRO - Title screen and menu music</li>
 *   <li>BOARD - Game board music</li>
 *   <li>MINIGAME - Randomly selected minigame music (3 variants)</li>
 * </ul>
 */
public class MusicPlayer implements Disposable {
    
    /**
     * Available music themes in the game.
     */
    public enum Theme {
        /** Title screen and menu music */
        INTRO,
        /** Game board music */
        BOARD,
        /** Minigame music (randomly selected from available tracks) */
        MINIGAME
    }
    
    private static final float DEFAULT_VOLUME = 0.5f;
    
    // Reference to game instance for logging
    private final CritterParade game;
    
    // Music tracks
    private Music introMusic;
    private Music boardMusic;
    private Music[] minigameMusic;
    
    // Currently playing track
    private Music currentTrack;
    private Theme currentTheme;
    
    // Random for minigame track selection
    private final Random random = new Random();
    
    /**
     * Creates a new MusicPlayer and loads all music tracks.
     * 
     * @param game the game instance for logging
     */
    public MusicPlayer(CritterParade game) {
        this.game = game;
        loadTracks();
    }
    
    /**
     * Loads all music tracks from assets.
     */
    private void loadTracks() {
        // Load intro/title music
        introMusic = Gdx.audio.newMusic(Gdx.files.internal("Music/intro.mp3"));
        introMusic.setLooping(true);
        introMusic.setVolume(DEFAULT_VOLUME);
        
        // Load board music (commented out until file exists)
        // boardMusic = Gdx.audio.newMusic(Gdx.files.internal("Music/board.mp3"));
        // boardMusic.setLooping(true);
        // boardMusic.setVolume(DEFAULT_VOLUME);
        
        // Load minigame music tracks (commented out until files exist)
        minigameMusic = new Music[3];
        // minigameMusic[0] = Gdx.audio.newMusic(Gdx.files.internal("Music/minigame0.mp3"));
        // minigameMusic[0].setLooping(true);
        // minigameMusic[0].setVolume(DEFAULT_VOLUME);
        
        // minigameMusic[1] = Gdx.audio.newMusic(Gdx.files.internal("Music/minigame1.mp3"));
        // minigameMusic[1].setLooping(true);
        // minigameMusic[1].setVolume(DEFAULT_VOLUME);
        
        // minigameMusic[2] = Gdx.audio.newMusic(Gdx.files.internal("Music/minigame2.mp3"));
        // minigameMusic[2].setLooping(true);
        // minigameMusic[2].setVolume(DEFAULT_VOLUME);
    }
    
    /**
     * Logs a music change event.
     * 
     * @param theme the theme name
     * @param action the action taken
     */
    private void logChange(String theme, String action) {
        if (game != null) {
            game.logMusicChange(theme, action);
        }
    }
    
    /**
     * Plays the specified music theme.
     * Stops any currently playing track before starting the new one.
     * 
     * @param theme the theme to play
     */
    public void play(Theme theme) {
        // Don't restart if already playing this theme
        if (currentTheme == theme && currentTrack != null && currentTrack.isPlaying()) {
            return;
        }
        
        stop();
        
        currentTheme = theme;
        currentTrack = getTrackForTheme(theme);
        
        if (currentTrack != null) {
            currentTrack.play();
            logChange(theme.name(), "play");
        }
    }
    
    /**
     * Gets the appropriate Music track for the given theme.
     * For MINIGAME theme, randomly selects one of the available tracks.
     * 
     * @param theme the theme to get a track for
     * @return the Music track, or null if not available
     */
    private Music getTrackForTheme(Theme theme) {
        switch (theme) {
            case INTRO:
                return introMusic;
            case BOARD:
                return boardMusic;
            case MINIGAME:
                return getRandomMinigameTrack();
            default:
                return null;
        }
    }
    
    /**
     * Randomly selects one of the available minigame music tracks.
     * 
     * @return a randomly selected minigame track, or null if none available
     */
    private Music getRandomMinigameTrack() {
        // Count available (non-null) tracks
        int availableCount = 0;
        for (Music track : minigameMusic) {
            if (track != null) {
                availableCount++;
            }
        }
        
        if (availableCount == 0) {
            return null;
        }
        
        // Pick a random available track
        int targetIndex = random.nextInt(availableCount);
        int currentIndex = 0;
        for (Music track : minigameMusic) {
            if (track != null) {
                if (currentIndex == targetIndex) {
                    return track;
                }
                currentIndex++;
            }
        }
        
        return null;
    }
    
    /**
     * Stops the currently playing music.
     */
    public void stop() {
        if (currentTrack != null && currentTrack.isPlaying()) {
            currentTrack.stop();
            if (currentTheme != null) {
                logChange(currentTheme.name(), "stop");
            }
        }
        currentTrack = null;
        currentTheme = null;
    }
    
    /**
     * Pauses the currently playing music.
     */
    public void pause() {
        if (currentTrack != null && currentTrack.isPlaying()) {
            currentTrack.pause();
            if (currentTheme != null) {
                logChange(currentTheme.name(), "pause");
            }
        }
    }
    
    /**
     * Resumes the currently paused music.
     */
    public void resume() {
        if (currentTrack != null && !currentTrack.isPlaying()) {
            currentTrack.play();
            if (currentTheme != null) {
                logChange(currentTheme.name(), "resume");
            }
        }
    }
    
    /**
     * Sets the volume for all music tracks.
     * 
     * @param volume the volume level (0.0 to 1.0)
     */
    public void setVolume(float volume) {
        float clampedVolume = Math.max(0f, Math.min(1f, volume));
        
        if (introMusic != null) {
            introMusic.setVolume(clampedVolume);
        }
        if (boardMusic != null) {
            boardMusic.setVolume(clampedVolume);
        }
        for (Music track : minigameMusic) {
            if (track != null) {
                track.setVolume(clampedVolume);
            }
        }
    }
    
    /**
     * Checks if any music is currently playing.
     * 
     * @return true if music is playing
     */
    public boolean isPlaying() {
        return currentTrack != null && currentTrack.isPlaying();
    }
    
    /**
     * Gets the currently playing theme.
     * 
     * @return the current theme, or null if nothing is playing
     */
    public Theme getCurrentTheme() {
        return currentTheme;
    }
    
    // =========================================================================
    // Convenience methods for common operations
    // =========================================================================
    
    /**
     * Plays the intro/title screen music.
     */
    public void playIntro() {
        play(Theme.INTRO);
    }
    
    /**
     * Plays the game board music.
     */
    public void playBoard() {
        play(Theme.BOARD);
    }
    
    /**
     * Plays a randomly selected minigame music track.
     */
    public void playMinigame() {
        play(Theme.MINIGAME);
    }
    
    @Override
    public void dispose() {
        stop();
        
        if (introMusic != null) {
            introMusic.dispose();
            introMusic = null;
        }
        if (boardMusic != null) {
            boardMusic.dispose();
            boardMusic = null;
        }
        for (int i = 0; i < minigameMusic.length; i++) {
            if (minigameMusic[i] != null) {
                minigameMusic[i].dispose();
                minigameMusic[i] = null;
            }
        }
    }
}
