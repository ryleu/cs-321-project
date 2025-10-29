package com.roachstudios.critterparade.gameboards;

public class BoardTile {
    private final int posX;
    private final int posY;
    private final Type type;

    public BoardTile(int posX, int posY, Type type) {
        this.posX = posX;
        this.posY = posY;
        this.type = type;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        SHOP, // green
        MINIGAME, // red
        REGULAR; // blue
    }
}
