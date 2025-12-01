package com.roachstudios.critterparade.menus;

import com.roachstudios.critterparade.CritterParade;
import com.roachstudios.critterparade.Player;
import com.roachstudios.critterparade.minigames.MiniGameDescriptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controls the minigame rush mode flow, managing the sequence of minigames
 * and tracking progress through them.
 * 
 * <p>In rush mode, players compete in all registered minigames back-to-back.
 * The player with the most crumbs at the end wins.</p>
 */
public class MiniGameRushController {
    
    private final CritterParade game;
    private final List<MiniGameDescriptor> minigameQueue;
    private int currentMinigameIndex = 0;
    
    /**
     * Constructs a rush controller with a shuffled copy of all registered minigames.
     *
     * @param game the main game instance
     */
    public MiniGameRushController(CritterParade game) {
        this.game = game;
        
        // Copy and shuffle the minigame list for variety
        this.minigameQueue = new ArrayList<>(game.getMiniGames());
        Collections.shuffle(this.minigameQueue);
    }
    
    /**
     * Gets the total number of minigames in the rush.
     *
     * @return the total number of minigames
     */
    public int getTotalMinigameCount() {
        return minigameQueue.size();
    }
    
    /**
     * Gets the current minigame number (1-indexed for display).
     *
     * @return the current minigame number (1 to total)
     */
    public int getCurrentMinigameNumber() {
        return currentMinigameIndex + 1;
    }
    
    /**
     * Checks if there are more minigames to play.
     *
     * @return true if more minigames remain
     */
    public boolean hasNextMinigame() {
        return currentMinigameIndex < minigameQueue.size();
    }
    
    /**
     * Gets the current minigame descriptor without advancing.
     *
     * @return the current minigame descriptor, or null if none remain
     */
    public MiniGameDescriptor getCurrentMinigame() {
        if (!hasNextMinigame()) {
            return null;
        }
        return minigameQueue.get(currentMinigameIndex);
    }
    
    /**
     * Advances to the next minigame in the sequence.
     */
    public void advanceToNextMinigame() {
        currentMinigameIndex++;
    }
    
    /**
     * Starts the current minigame by navigating to its instruction screen.
     * Call {@link #advanceToNextMinigame()} after the minigame completes.
     */
    public void startCurrentMinigame() {
        MiniGameDescriptor current = getCurrentMinigame();
        if (current != null) {
            game.setScreen(new MiniGameInstructionScreen(game, current));
        }
    }
    
    /**
     * Finds the winner(s) of the rush based on total crumbs.
     * Returns all players tied for first place.
     *
     * @return array of players with the highest crumb count
     */
    public Player[] getWinners() {
        Player[] players = game.getPlayers();
        if (players == null || players.length == 0) {
            return new Player[0];
        }
        
        // Find the maximum crumbs
        int maxCrumbs = 0;
        for (Player p : players) {
            if (p.getCrumbs() > maxCrumbs) {
                maxCrumbs = p.getCrumbs();
            }
        }
        
        // Collect all players with max crumbs
        List<Player> winners = new ArrayList<>();
        for (Player p : players) {
            if (p.getCrumbs() == maxCrumbs) {
                winners.add(p);
            }
        }
        
        return winners.toArray(new Player[0]);
    }
    
    /**
     * Gets all players sorted by crumbs (highest first) for final standings.
     * Uses wins as a tiebreaker when crumbs are equal, then player ID for stability.
     *
     * @return players sorted by crumb count descending
     */
    public Player[] getFinalStandings() {
        Player[] players = game.getPlayers();
        if (players == null) {
            return new Player[0];
        }
        
        // Create a copy and sort by crumbs descending, with wins as tiebreaker
        Player[] sorted = players.clone();
        java.util.Arrays.sort(sorted, (a, b) -> {
            // Primary: more crumbs is better
            int crumbCompare = Integer.compare(b.getCrumbs(), a.getCrumbs());
            if (crumbCompare != 0) {
                return crumbCompare;
            }
            // Tiebreaker 1: more wins is better
            int winsCompare = Integer.compare(b.getWins(), a.getWins());
            if (winsCompare != 0) {
                return winsCompare;
            }
            // Tiebreaker 2: lower player ID for stable ordering
            return Integer.compare(a.getID(), b.getID());
        });
        return sorted;
    }
    
    /**
     * Navigates to the rush victory screen when all minigames are complete.
     */
    public void showFinalResults() {
        game.setScreen(new RushVictoryScreen(game, this));
    }
}

