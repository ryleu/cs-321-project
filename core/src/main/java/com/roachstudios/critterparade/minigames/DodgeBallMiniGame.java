package com.roachstudios.critterparade.minigames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.roachstudios.critterparade.CritterParade;
import com.roachstudios.critterparade.Player;
import com.roachstudios.critterparade.minigames.minigameprops.dodgeBall;
import java.util.Random;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * 
 */
public class DodgeBallMiniGame extends MiniGame {
    
    /** The display name for this mini game. */
    public static final String NAME = "Dodgeball";
    /** Instructions explaining how to play this mini game. */
    public static final String INSTRUCTIONS = 
        "Don't let the balls touch you!\n\n" +
        "Use your DIRECTIONAL inputs to move around.\n" +
        "Last player standing wins!";
    
    @Override
    public String getName() {
        return NAME;
    }
    
    @Override
    public String getInstructions() {
        return INSTRUCTIONS;
    }
    
    private Texture backgroundTex;
    private Texture playerOutTex;

    
    private final float playerSize = 1.0f;
    
    /**
     * Tracks whether each player has crossed the finish line.
     * Index corresponds to player array index (0-based).
     */
    private boolean[] playerFinished;
    
    /**
     * Ordered list of players as they finish (1st place to last).
     */
    private Player[] placement;
    private int haventFinished;
    
    /**
     * Prevents onGameComplete from being called multiple times.
     */
    private boolean gameCompleted;
    
    private List<dodgeBall> activeBalls = new ArrayList();
    private List<Sprite> outSprites = new ArrayList();
    
    private float timeElapsed = 0f;
    private float coolDown = 1f;
    
    /**
     * Constructs a new Simple Racer mini game.
     *
     * @param game shared game instance providing viewport, batch, and players
     */
    public DodgeBallMiniGame(CritterParade game) {
        super(game);
        
        int playerCount = getPlayerCount();
        placement = new Player[playerCount];
        playerFinished = new boolean[playerCount];
        haventFinished = playerCount - 1;
        gameCompleted = false;
        
        backgroundTex = new Texture("MiniGames/SimpleRacer/Clouds.png");
        playerOutTex = new Texture("MiniGames/DodgeBall/X.png");

        
        // Set up initial positions and sizes for all players######################################################################################
        Player[] players = getPlayers();
        for (int i = 0; i < playerCount; i++) {
            Player player = players[i];
            player.setSpriteSize(playerSize);
            player.getSprite().setX(4);
            player.getSprite().setY(4);
            clampBounds(player);
            playerFinished[i] = false;
        }
    }
    
    @Override
    public void show() {
        // Update viewport to current screen size
        game.viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        
        // Reset game state in case we're replaying
        haventFinished = getPlayerCount() - 1;
        gameCompleted = false;
        for (int i = 0; i < playerFinished.length; i++) {
            playerFinished[i] = false;
            placement[i] = null;
        }
        
        // Reset positions and sizes when the minigame is shown
        Player[] players = getPlayers();
        for (int i = 0; i < players.length; i++) {
            players[i].setSpriteSize(playerSize);
            players[i].getSprite().setPosition((playerSize * i) + 4, 4);
        }
    }

    @Override
    public void render(float delta) {
        input();
        logic(delta);
        draw();
    }

    /**
     * Reads player inputs and translates them into movement when allowed.
     */
    private void input() {
        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime();
        
        Player[] players = getPlayers();
        for (int i = 0; i < players.length; i++) {
            if (!playerFinished[i]) {
                Player player = players[i];
                // Players advance by pressing their right input
                if (player.isPressingRight()) {
                    player.getSprite().translateX(speed * delta);
                }
                else if (player.isPressingLeft()) {
                    player.getSprite().translateX(-speed * delta);
                }
                if (player.isPressingUp()) {
                    player.getSprite().translateY(speed * delta);
                }
                else if (player.isPressingDown()) {
                    player.getSprite().translateY(-speed * delta);
                }
            }
        }
    }
    
    /**
     * Clamps sprites to the world bounds and records finish order once a 
     * player crosses the line at x=14 in world units.
     */
    private void logic(float delta) {
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();
        
        Player[] players = getPlayers();
        float playerWidth = players[0].getSprite().getWidth();
        float playerHeight = players[0].getSprite().getHeight();
        
        for (int i = 0; i < players.length; i++) {
            Player player = players[i];
            
            if(!isOnOutList(player)){
                clampX(player, playerWidth, worldWidth);
                clampY(player, playerHeight, worldHeight);
            }
            
            clampBounds(player);
            
            // THIS IS THE CODE THAT FAILS====================
            if (checkIfOut(player) && !playerFinished[i]) {
                playerFinished[i] = true;
                placement[haventFinished] = player;
                haventFinished--;
            }
            //=================================================
            
        }
        
        timeElapsed += delta;
        if(coolDown <= 0){
            spawnBall(activeBalls);
            resetCoolDown();
        }
        else{
            coolDown -= delta;
        }
        
        moveBalls(4);
        
        checkGameComplete();
    }
    
    /**
     * Renders the background, finish line, and all player sprites.###################################################################################
     */
    private void draw() {
        ScreenUtils.clear(1f, 0.992f, 0.816f, 1f);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();
        
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();
        
        game.batch.draw(backgroundTex, 0, 0, worldWidth, worldHeight);
        
        // Draw all player sprites
        Player[] players = getPlayers();
        for (Player player : players) {
            player.getSprite().draw(game.batch);
        }
        
        for (dodgeBall ball : activeBalls){
            ball.getSprite().draw(game.batch);
        }
        
        if (!outSprites.isEmpty()){
            for (Sprite sprite : outSprites)
            {
                sprite.draw(game.batch);
            }
        }
        
        game.batch.end();
    }
    
    /**
     * Checks if all players have finished and triggers game completion.
     */
    private void checkGameComplete() {
        // The game ends when we are ready to place the 1st place winner (index 0)
        if (!gameCompleted && haventFinished == 0) {
            
            // Find the single player who hasn't finished yet
            Player winner = null;
            Player[] players = getPlayers();
            for (int i = 0; i < players.length; i++) {
                if (!playerFinished[i]) {
                    winner = players[i];
                    // Mark them as finished (optional, but good practice)
                    playerFinished[i] = true; 
                    break;
                }
            }
            
            // Place the winner in the final available spot (1st place)
            placement[0] = winner;
            
            // Update the state variables
            haventFinished--; // Decrement to -1 to signal completion state
            onGameComplete(placement);
            gameCompleted = true;
        }
    }
    
    @Override
    public void dispose() {
        if (backgroundTex != null) {
            backgroundTex.dispose();
        }
    }
    
    private dodgeBall createBall(int startX, int startY, int direction){
        dodgeBall newBall = new dodgeBall();
        newBall.getSprite().setX(startX);
        newBall.getSprite().setY(startY);
        newBall.getBounds().setX(startX);
        newBall.getBounds().setY(startY);
        newBall.changeDirection(direction);
        return newBall;
    }
    
    private void spawnBall(List<dodgeBall> ballList){
        int direction = getRandomDirection();
        
        if(direction == 0){
            ballList.add(createBall(getRandomSpawn(true), 0, direction));
        }
        if(direction == 1){
            ballList.add(createBall(getRandomSpawn(true), 8, direction));
        }
        if(direction == 2){
            ballList.add(createBall(15, getRandomSpawn(false), direction));
        }
        if(direction == 3){
            ballList.add(createBall(0, getRandomSpawn(false), direction));
        }
    }
    
    private void moveBalls(float speed){
        float delta = Gdx.graphics.getDeltaTime();
        
        Iterator<dodgeBall> iterator = activeBalls.iterator();
        

        while (iterator.hasNext()){

            dodgeBall ball = iterator.next();
            
            switch (ball.getDirection()) {
                case 0:
                    moveBallUp(ball, speed * delta);
                    if(ball.getSprite().getY() > game.viewport.getWorldHeight()){
                        iterator.remove();
                    }   break;
                case 1:
                    moveBallDown(ball, speed * delta);
                    if(ball.getSprite().getY() < -1){
                        iterator.remove();
                    }   break;
                case 2:
                    moveBallLeft(ball, speed * delta);
                    if(ball.getSprite().getX() < -1){
                        iterator.remove(); 
                    }   break;
                case 3:
                    moveBallRight(ball, speed * delta);
                    if(ball.getSprite().getX() > game.viewport.getWorldWidth()){
                        iterator.remove(); 
                    }   break;
                default:
                    break;
            }
        }
    }
    
    private void moveBallUp(dodgeBall ball, float speed){
        ball.moveBall(0, speed);
    }
    
    private void moveBallDown(dodgeBall ball, float speed){
        ball.moveBall(0, -speed);
    }
    
    private void moveBallLeft(dodgeBall ball, float speed){
        ball.moveBall(-speed, 0);
    }
    
    private void moveBallRight(dodgeBall ball, float speed){
        ball.moveBall(speed, 0);
    }
    
    
    private void createOutSprite(Player player){
        Sprite newOutSprite = new Sprite(playerOutTex);
        newOutSprite.setSize(1, 1);
        newOutSprite.setX(player.getSprite().getX());
        newOutSprite.setY(player.getSprite().getY());
        outSprites.add(newOutSprite);
    }
    
    private boolean checkIfOut(Player player){
        for (dodgeBall ball : activeBalls){
                if(player.getBounds().overlaps(ball.getBounds()) && !isOnOutList(player)){
                    createOutSprite(player);
                    activeBalls.remove(ball);
                    player.getSprite().setX(-10);
                    player.getSprite().setY(-10);
                    return true;
                }
                
            }
        return false;
    } 
    
    private boolean isOnOutList(Player playerInput){
        if(playerFinished.length != 0){
            for(Player player : placement){
                if(playerInput == player){
                    return true;
                }
            }
        }
        return false;
    }
    
    private int getRandomDirection(){
        Random random = new Random();
        return random.nextInt(4);
    }
    
    private int getRandomSpawn(Boolean isX){
        Random random = new Random();
        int min = 1;
        int max;
        
        if(isX){
            max = 14;
            return random.nextInt(max - min + 1) + min;
        }
        else{
            max = 7;
            return random.nextInt(max - min + 1) + min;
        }
    }
    
    private void resetCoolDown(){
        if(timeElapsed < 5){
            coolDown = 0.6f;
        }
        else if(5 <= timeElapsed && timeElapsed < 10){
            coolDown = 0.5f;
        }
        else if(10 <= timeElapsed && timeElapsed < 15){
            coolDown = 0.4f;
        }
        else if(15 <= timeElapsed && timeElapsed < 20){
            coolDown = 0.3f;
        }
        else if(20 <= timeElapsed && timeElapsed < 25){
            coolDown = 0.2f;
        }
        else if(25 <= timeElapsed){
            coolDown = 0.1f;
        }
    }
    
    private void clampX(Player player, float playerWidth, float worldWidth){
        // Clamp X position
            player.getSprite().setX(MathUtils.clamp(
                player.getSprite().getX(), 
                playerWidth, 
                worldWidth - (2 * playerWidth)
            ));
    }
    private void clampY(Player player, float playerHeight, float worldHeight){
        // Clamp Y position
            player.getSprite().setY(MathUtils.clamp(
                player.getSprite().getY(), 
                playerHeight, 
                worldHeight - (2 * playerHeight)
            ));
    }
    
    private void clampBounds(Player player){
        player.getBounds().setX(player.getSprite().getX());
        player.getBounds().setY(player.getSprite().getY());
    }
}


