package com.roachstudios.critterparade.minigames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.roachstudios.critterparade.CritterParade;
import com.roachstudios.critterparade.Player;
import java.util.Arrays;

/**
 * A memory based game: a sequence will show on screen and each player will
 * enter the sequence that was shown.
 */
public class MemoryMatch extends MiniGame {
    
    /** The display name for this mini game. */
    public static final String NAME = "Memory Match";
    /** Instructions explaining how to play this mini game. */
    public static final String INSTRUCTIONS = 
        "Remember the Sequence as it appears and reenter it!\n\n" +
        "Use Movements Keys to Select the correct sequence.\n" +
        "Most accurate player wins!";
    
    @Override
    public String getName() {
        return NAME;
    }
    
    @Override
    public String getInstructions() {
        return INSTRUCTIONS;
    }
    
    @Override
    public float getScoreValue(Player player) {
        // Find the player's index and return their score (correct matches)
        Player[] players = getPlayers();
        for (int i = 0; i < players.length; i++) {
            if (players[i] == player) {
                return scores[i];
            }
        }
        return -1f;
    }
    
    private Texture backgroundTex;
    private Texture upArrow;
    private Texture rightArrow;
    private Texture downArrow;
    private Texture leftArrow;
    private int revealTimer = 80;
    
    
    private final float playerSize = 1.0f;
    
    /**
     * Tracks whether each player has completed the sequence for each round.
     * Index corresponds to player array index (0-based).
     */
    private boolean[] playerFinished;
    
    /**
     * Ordered list of players as they finish the final
     * sequence (used for tiebreaks)(1st place to last).
     */
    private Player[] placement;
    private int finishedCount;
    
    /**
     * Prevents onGameComplete from being called multiple times.
     */
    private boolean gameCompleted;
    
    /**
     * Prevents inputs from being read when the sequence is being presented
     */
    private boolean acceptInputs;
    
    /**
     * Used to read and compare the player inputs
     */
    private String correctSequence = "";
    private String[] playerSequence = {"", "", "", "", "", ""};
    
    private int[] scores;
    
    /**
     * Determines the length of the sequence to memorize
     */
    
    private int codeLength = 3;
    /**
     * Constructs a new Memory Match mini game.
     *
     * @param game shared game instance providing viewport, batch, and players
     */
    public MemoryMatch(CritterParade game) {
        super(game);
      
        int playerCount = getPlayerCount();
        placement = new Player[playerCount];
        playerFinished = new boolean[playerCount];
        scores = new int[playerCount];
        finishedCount = 0;
        gameCompleted = false;
        acceptInputs = false;
        
        backgroundTex = new Texture("MiniGames/MemoryMatch/forest_path.png");
        upArrow = new Texture("MiniGames/MemoryMatch/up_arrow.png");
        rightArrow = new Texture("MiniGames/MemoryMatch/right_arrow.png");
        downArrow = new Texture("MiniGames/MemoryMatch/down_arrow.png");
        leftArrow = new Texture("MiniGames/MemoryMatch/left_arrow.png");
        
        // Set up initial positions and sizes for all players
        Player[] players = getPlayers();
        for (int i = 0; i < playerCount; i++) {
            Player player = players[i];
            player.setSpriteSize(playerSize);
            player.getSprite().setX(playerSize * i);
            player.getSprite().setY(0);
            playerFinished[i] = false;
        }
    }

    @Override
    public void show() {
        // Update viewport to current screen size
        game.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        
        // Reset game state in case we're replaying
        finishedCount = 0;
        gameCompleted = false;
        codeLength = 3;
        for (int i = 0; i < playerFinished.length; i++) {
            playerFinished[i] = false;
            playerSequence[i] = "";
            scores[i] = 0;
            placement[i] = null;
        }
        correctSequence = "";
        
        // Reset positions and sizes when the minigame is shown
        Player[] players = getPlayers();
        for (int i = 0; i < players.length; i++) {
            players[i].setSpriteSize(playerSize);
            players[i].getSprite().setPosition(3 + 2 * playerSize * i, 0);
        }
    }

    @Override
    public void render(float delta) {
        input();
        logic();
        draw();
    }

    /**
     * Reads player inputs and translates them into movement when allowed.
     */
    private void input() {
        Player[] players = getPlayers();
        for (int i = 0; i < players.length; i++) {
            if (!playerFinished[i] && acceptInputs) {
                Player player = players[i];
                // Players advance by pressing their right input
                if (player.justPressedRight()) {                
                    playerSequence[i] += 'd'; 
                }
                else if (player.justPressedLeft()) {
                    playerSequence[i] += 'a';                    
                }
                else if (player.justPressedUp()) {
                    playerSequence[i] += 'w';                    
                }
                else if (player.justPressedDown()) {
                    playerSequence[i] += 's';                    
                }
            }
        }
    }
    
    /**
     * Handles each player's ability to input when a sequence in available
     * and detects when a new sequence is needed
     */
    private void logic() {
        
        revealTimer -= 1;
                
        if (revealTimer < 0 && correctSequence.length() == codeLength){
            acceptInputs = true;
        }
        
        Player[] players = getPlayers();
        
        for (int i = 0; i < players.length; i++) {
            //Prevents players from entering a code when they already have 
            // hit the length
            if (playerSequence[i].length() == codeLength && playerFinished[i] == false){
                playerFinished[i] = true;
                finishedCount++;
            }
            
        }
        //Checks if all sequences have come in
        if (finishedCount == (getPlayerCount())){
            
            acceptInputs = false;
            revealTimer = 50;
            for (int i = 0; i < playerFinished.length; i++){
                playerFinished[i] = false;
                for (int j = 0; j < correctSequence.length(); j++){
                    if (playerSequence[i].charAt(j) == correctSequence.charAt(j)){
                        scores[i]++;
                    }
                }
                playerSequence[i] = "";
            }
            codeLength += 2;
            finishedCount = 0;
        }
        // Checks to end the Game      
        checkGameComplete();
        
    }
    
    /**
     * Renders the background and all player sprites.
     */
    private void draw() {
        ScreenUtils.clear(1f, 0.992f, 0.816f, 1f);
        game.getViewport().apply();
        game.getBatch().setProjectionMatrix(game.getViewport().getCamera().combined);
        game.getBatch().begin();
        
        float worldWidth = game.getViewport().getWorldWidth();
        float worldHeight = game.getViewport().getWorldHeight();
        
        game.getBatch().draw(backgroundTex, 0, 0, worldWidth, worldHeight);

        
        //Creates the sequence at the start of a round
        if (acceptInputs == false && revealTimer < 0){
            correctSequence = "";
            for(int i = 1; i <= codeLength; i++){
                double randArrow = Math.random();
                if (randArrow >= 0.75){
                    correctSequence += 'w';
                }
                else if (randArrow >= 0.5){
                    correctSequence += 'd';
                }
                else if (randArrow >= 0.25){
                    correctSequence += 's';
                }
                else {
                    correctSequence += 'a';
                }
            }
            
            revealTimer = (10 + 20 * codeLength);
        }
        
        //Draws the sequence
        
        if (revealTimer >= 0 && correctSequence.length() == codeLength){
            for(int i = 0; i < correctSequence.length(); i++){
                if (correctSequence.charAt(i) == 'w'){
                    game.getBatch().draw(upArrow, 2 + i + i, 4.5f, 1, 1);
                }
                if (correctSequence.charAt(i) == 'd'){
                    game.getBatch().draw(rightArrow, 2 + i+i, 4.5f, 1, 1);
                }
                if (correctSequence.charAt(i) == 's'){
                    game.getBatch().draw(downArrow, 2 + i+i, 4.5f, 1, 1);
                }
                if (correctSequence.charAt(i) == 'a'){
                    game.getBatch().draw(leftArrow, 2 + i+i, 4.5f, 1, 1);
                }
            }
        }
        // Draw all player sprites
        Player[] players = getPlayers();
        for (Player player : players) {
            player.getSprite().draw(game.getBatch());
        }
        
        game.getBatch().end();
    }
    
    /**
     * Checks if all players have finished and triggers game completion.
     */
    private void checkGameComplete() {
        
        if (gameCompleted) {
            return; // Already triggered completion, don't do it again           
        }
        if (codeLength > 7) {
            gameCompleted = true;
            Player[] players = getPlayers();
            // Determine placements by sorting players by score (highest to lowest)
            // Create array of indices to sort
            Integer[] indices = new Integer[getPlayerCount()];
            for (int i = 0; i < getPlayerCount(); i++) {
                indices[i] = i;
            }
            // Sort indices by score (descending)
            Arrays.sort(indices, (a, b) -> Integer.compare(scores[b], scores[a]));
            // Build placement array
            for (int i = 0; i < getPlayerCount(); i++) {
                placement[i] = players[indices[i]];
            }
            
            
            // Debug output for placements
            StringBuilder out = new StringBuilder("Placements:\n");
            for (int i = 0; i < placement.length; i++) {
                if (placement[i] != null) {
                    out.append(i + 1).append(". ").append(placement[i].getName()).append("\n");
                } else {
                    out.append(i + 1).append(". NULL\n");
                }
            }
            System.out.println(out);
            
            onGameComplete(placement);
        }
    }
    
    @Override
    public void dispose() {
        if (backgroundTex != null) {
            backgroundTex.dispose();
        }
    }
}
