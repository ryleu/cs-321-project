package com.roachstudios.critterparade.minigames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.roachstudios.critterparade.CritterParade;
import com.roachstudios.critterparade.Player;
import com.roachstudios.critterparade.menus.MiniGameResultScreen;

public class CatchObjectsMiniGame extends MiniGame {

    private final CritterParade gameInstance;
    private Texture backgroundTex;
    private Texture fallingObjectTex;

    private Player[] players;
    private int playerCount;

    private float fallingX;
    private float fallingY;
    private float fallingSpeed;

    private int[] scores;
    private int currentPlayerIndex;

    private boolean gameOver = false;

    // turn-ending UI
    private boolean showingTurnEnd = false;
    private float turnEndTimer = 0f;

    // intro UI
    private boolean showingIntro = true;
    private float introTimer = 0f;
    private final float INTRO_DURATION = 3.5f;

    // character selection UI
    private boolean choosingCharacter = true;
    private int[] chosenCharacterIndex;

    private static final String[] CHARACTER_TEXTURE_PATHS = {
        "PlayerSprites/bumble_bee.png",
        "PlayerSprites/field_mouse.png",
        "PlayerSprites/lady_bug.png",
        "PlayerSprites/pond_frog.png",
        "PlayerSprites/red_squirrel.png",
        "PlayerSprites/solider_ant.png"
    };

    private static final String[] CHARACTER_NAMES = {
        "Bumble Bee",
        "Field Mouse",
        "Lady Bug",
        "Pond Frog",
        "Red Squirrel",
        "Solider Ant"
    };

    private Texture[] characterPreviewTextures;

    private float blinkTimer = 0f;

    public CatchObjectsMiniGame(CritterParade gameInstance) {
        super(gameInstance);
        this.gameInstance = gameInstance;

        backgroundTex = new Texture("MiniGames/CatchObjects/night_sky.png");
        fallingObjectTex = new Texture("MiniGames/CatchObjects/star.png");

        characterPreviewTextures = new Texture[CHARACTER_TEXTURE_PATHS.length];
        for (int i = 0; i < CHARACTER_TEXTURE_PATHS.length; i++) {
            characterPreviewTextures[i] = new Texture(CHARACTER_TEXTURE_PATHS[i]);
        }

        playerCount = gameInstance.getNumPlayers();
        if (playerCount < 1) playerCount = 1;
        if (playerCount > 6) playerCount = 6;

        players = new Player[playerCount];
        scores = new int[playerCount];
        chosenCharacterIndex = new int[playerCount];

        for (int i = 0; i < playerCount; i++) {
            chosenCharacterIndex[i] = -1;

            // FIXED: Player now requires (id, name, texture)
            players[i] = new Player(
                i + 1,
                "Bumble Bee", // placeholder name
                new Texture("PlayerSprites/bumble_bee.png")
            );

            players[i].setSpriteSize(1f);
            players[i].getSprite().setPosition(7f, 1f);
        }

        currentPlayerIndex = 0;
        resetFallingStar();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {

        if (showingIntro) {
            introTimer += delta;
            drawIntroScreen();

            if (introTimer >= INTRO_DURATION) {
                showingIntro = false;
                choosingCharacter = true;
            }
            return;
        }

        if (choosingCharacter) {
            blinkTimer += delta;
            handleCharacterSelectionInput();
            drawCharacterSelectScreen();
            return;
        }

        if (showingTurnEnd) {
            turnEndTimer += delta;
            draw();
            if (turnEndTimer >= 1.5f) {
                showingTurnEnd = false;
                endTurn();
            }
            return;
        }

        if (!gameOver) {
            input();
            logic(delta);
            draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        gameInstance.viewport.update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (backgroundTex != null) backgroundTex.dispose();
        if (fallingObjectTex != null) fallingObjectTex.dispose();
        if (characterPreviewTextures != null) {
            for (Texture t : characterPreviewTextures) {
                if (t != null) t.dispose();
            }
        }
    }

    private void drawIntroScreen() {
        ScreenUtils.clear(0, 0, 0, 1);

        gameInstance.viewport.apply();
        gameInstance.batch.setProjectionMatrix(gameInstance.viewport.getCamera().combined);

        float w = gameInstance.viewport.getWorldWidth();
        float h = gameInstance.viewport.getWorldHeight();

        gameInstance.batch.begin();

        gameInstance.batch.draw(backgroundTex, 0, 0, w, h);

        gameInstance.font.draw(gameInstance.batch, "Welcome to Catching Stars!", w * 0.20f, h * 0.80f);
        gameInstance.font.draw(gameInstance.batch, "May the best player win!", w * 0.23f, h * 0.70f);

        gameInstance.font.draw(gameInstance.batch, "Controls:", w * 0.10f, h * 0.55f);
        gameInstance.font.draw(gameInstance.batch, "Move Left: A or LEFT ARROW", w * 0.10f, h * 0.47f);
        gameInstance.font.draw(gameInstance.batch, "Move Right: D or RIGHT ARROW", w * 0.10f, h * 0.40f);

        gameInstance.font.draw(gameInstance.batch,
            "Catch falling stars to score points.", w * 0.10f, h * 0.30f);
        gameInstance.font.draw(gameInstance.batch,
            "Missing ONE star ends your turn.", w * 0.10f, h * 0.23f);
        gameInstance.font.draw(gameInstance.batch,
            "You can catch up to 10 stars in a turn.", w * 0.10f, h * 0.16f);

        gameInstance.batch.end();
    }

    private void handleCharacterSelectionInput() {
        for (int i = 0; i < CHARACTER_TEXTURE_PATHS.length; i++) {

            int keyCode = Input.Keys.NUM_1 + i;

            if (Gdx.input.isKeyJustPressed(keyCode)) {
                chosenCharacterIndex[currentPlayerIndex] = i;

                // FIXED: Pass name + texture
                players[currentPlayerIndex] = new Player(
                    currentPlayerIndex + 1,
                    CHARACTER_NAMES[i],
                    new Texture(CHARACTER_TEXTURE_PATHS[i])
                );

                players[currentPlayerIndex].setSpriteSize(1f);
                players[currentPlayerIndex].getSprite().setPosition(7f, 1f);

                choosingCharacter = false;
                resetFallingStar();
                break;
            }
        }
    }

    private void drawCharacterSelectScreen() {
        ScreenUtils.clear(0, 0, 0, 1);

        gameInstance.viewport.apply();
        gameInstance.batch.setProjectionMatrix(gameInstance.viewport.getCamera().combined);

        float w = gameInstance.viewport.getWorldWidth();
        float h = gameInstance.viewport.getWorldHeight();

        gameInstance.batch.begin();

        gameInstance.batch.draw(backgroundTex, 0, 0, w, h);

        gameInstance.font.draw(gameInstance.batch,
            "Player " + (currentPlayerIndex + 1) + " - Select Your Character",
            w * 0.10f, h * 0.90f);

        float alpha = 0.5f + 0.5f * MathUtils.sin(blinkTimer * 4f);
        gameInstance.font.setColor(1f, 1f, 1f, alpha);
        gameInstance.font.draw(gameInstance.batch,
            "Press the NUMBER KEY (1–6) for your character",
            w * 0.10f, h * 0.82f);
        gameInstance.font.setColor(1f, 1f, 1f, 1f);

        float slotWidth = w / CHARACTER_TEXTURE_PATHS.length;
        float spriteSize = 1.5f;

        for (int i = 0; i < CHARACTER_TEXTURE_PATHS.length; i++) {
            float centerX = slotWidth * i + slotWidth * 0.5f;
            float spriteX = centerX - spriteSize * 0.5f;
            float spriteY = h * 0.45f;

            gameInstance.batch.draw(characterPreviewTextures[i], spriteX, spriteY, spriteSize, spriteSize);

            gameInstance.font.draw(gameInstance.batch,
                (i + 1) + "", centerX - 0.2f, h * 0.60f);

            gameInstance.font.draw(gameInstance.batch,
                CHARACTER_NAMES[i], centerX - 1.3f, h * 0.35f);
        }

        gameInstance.batch.end();
    }

    private void input() {
        Player p = players[currentPlayerIndex];

        float step = 0.12f;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            p.getSprite().translateX(-step);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            p.getSprite().translateX(step);
        }

        float w = gameInstance.viewport.getWorldWidth();
        float pw = p.getSprite().getWidth();
        p.getSprite().setX(MathUtils.clamp(p.getSprite().getX(), 0f, w - pw));
    }

    private void logic(float delta) {
        fallingY -= fallingSpeed * delta;

        Player p = players[currentPlayerIndex];
        float px = p.getSprite().getX();
        float py = p.getSprite().getY();
        float pw = p.getSprite().getWidth();
        float ph = p.getSprite().getHeight();

        boolean caughtHoriz = fallingX + 1f > px && fallingX < px + pw;
        boolean caughtVert = fallingY <= py + ph && fallingY + 1f >= py;

        if (caughtHoriz && caughtVert) {
            scores[currentPlayerIndex]++;
            resetFallingStar();

            if (scores[currentPlayerIndex] >= 10) startTurnEnd();
            return;
        }

        if (fallingY < -1f) startTurnEnd();
    }

    private void startTurnEnd() {
        showingTurnEnd = true;
        turnEndTimer = 0f;
    }

    private void draw() {
        ScreenUtils.clear(0, 0, 0, 1);

        gameInstance.viewport.apply();
        gameInstance.batch.setProjectionMatrix(gameInstance.viewport.getCamera().combined);

        float w = gameInstance.viewport.getWorldWidth();
        float h = gameInstance.viewport.getWorldHeight();

        gameInstance.batch.begin();

        gameInstance.batch.draw(backgroundTex, 0, 0, w, h);
        gameInstance.batch.draw(fallingObjectTex, fallingX, fallingY, 1f, 1f);

        players[currentPlayerIndex].getSprite().draw(gameInstance.batch);

        if (!showingTurnEnd) {
            gameInstance.font.draw(gameInstance.batch,
                "Player " + (currentPlayerIndex + 1) +
                    " Score: " + scores[currentPlayerIndex],
                1f, h - 1f);
        } else {
            gameInstance.font.draw(gameInstance.batch,
                "Player " + (currentPlayerIndex + 1) +
                    " got " + scores[currentPlayerIndex] + " stars!",
                w * 0.25f, h * 0.55f);
        }

        gameInstance.batch.end();
    }

    private void resetFallingStar() {
        float w = gameInstance.viewport.getWorldWidth();
        float h = gameInstance.viewport.getWorldHeight();

        fallingX = MathUtils.random(0f, w - 1f);
        fallingY = h + 1f;
        fallingSpeed = MathUtils.random(3f, 6f);
    }

    private void endTurn() {
        currentPlayerIndex++;

        if (currentPlayerIndex >= playerCount) {
            gameOver = true;
            gameInstance.setScreen(new MiniGameResultScreen(gameInstance, makePlacementArray()));
            return;
        }

        choosingCharacter = true;
    }

    private Player[] makePlacementArray() {
        Player[] ordered = new Player[playerCount];

        Integer[] idx = new Integer[playerCount];
        for (int i = 0; i < playerCount; i++) {
            idx[i] = i;
        }

        java.util.Arrays.sort(idx, (a, b) ->
            Integer.compare(scores[b], scores[a])
        );

        for (int i = 0; i < playerCount; i++) {
            ordered[i] = players[idx[i]];
        }

        return ordered;
    }

    @Override
    public String getName() {
        return "Catching Stars";
    }

    @Override
    public String getInstructions() {
        return "Move left/right to catch stars. Missing one ends your turn.";
    }
}

