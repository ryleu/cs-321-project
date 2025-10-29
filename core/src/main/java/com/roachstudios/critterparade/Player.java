package com.roachstudios.critterparade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;




public class Player {
    
    private int fruit;
    private int crumbs;
    private int numMGWins; 
    
    private int playerID;
    private Texture playerTex;
    private Sprite playerSprite;
   

    
    public Player(int id, Texture tex){
        this.fruit = 0;
        this.crumbs = 0;
        this.playerID = id;
        this.numMGWins = 0;  

        this.playerTex = tex;
        this.playerSprite = new Sprite(playerTex);
        this.playerSprite.setSize(1, 1);
        
    }
    
    public void addFruit(){
        this.fruit++;
    }
    
    public void subFruit(){
        this.fruit--;
        
        if(this.fruit < 0){
            this.fruit = 0;
        }
    }
    
    public int getFruit(){
        return this.fruit;
    }
    
    public void addCrumbs(int numToAdd){
        this.crumbs += numToAdd;
    }
    
    public void subCrumbs(int numToSub){
        this.crumbs -= numToSub;
        
        if(this.crumbs < 0){
            this.crumbs = 0;
        }
    }
    
    public int getCrumbs(){
        return this.crumbs;
    }
    
    public void addWin(){
        this.numMGWins++;
    }
    
    public int getWins(){
        return this.numMGWins;
    }
    
    public int getID(){
        return this.playerID;
    }
    
    public Sprite getSprite(){
        return this.playerSprite;
    }
    
    public void setSpriteSize(float size){
        this.playerSprite.setSize(size, size);
        
    }
    
    public boolean isPressingUp(){
        switch(this.playerID){
            case 1:
                if(Gdx.input.isKeyPressed(Input.Keys.W)){
                   return true; 
                }
                break;
            case 2:
                if(Gdx.input.isKeyPressed(Input.Keys.T)){
                    return true;
                }
                break;
            case 3:
                if(Gdx.input.isKeyPressed(Input.Keys.I)){
                    return true;
                }
                break;
            case 4:
                if(Gdx.input.isKeyPressed(Input.Keys.LEFT_BRACKET)){
                    return true;
                }
                break;
            case 5:
                if(Gdx.input.isKeyPressed(Input.Keys.UP)){
                    return true;
                }
                break;
            case 6:
                if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_8)){
                    return true;
                }
                break;
            default:
                System.out.println("INPUT ERROR: THE ID FOR THIS CHARACTER IS SET WRONG!!!");
                
        }
        return false;
    }
    
    public boolean justPressedUp(){
        switch(this.playerID){
            case 1:
                if(Gdx.input.isKeyJustPressed(Input.Keys.W)){
                   return true; 
                }
                break;
            case 2:
                if(Gdx.input.isKeyJustPressed(Input.Keys.T)){
                    return true;
                }
                break;
            case 3:
                if(Gdx.input.isKeyJustPressed(Input.Keys.I)){
                    return true;
                }
                break;
            case 4:
                if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT_BRACKET)){
                    return true;
                }
                break;
            case 5:
                if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
                    return true;
                }
                break;
            case 6:
                if(Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_8)){
                    return true;
                }
                break;
            default:
                System.out.println("INPUT ERROR: THE ID FOR THIS CHARACTER IS SET WRONG!!!");
                
        }
        return false;
    }
    
    public boolean isPressingLeft(){
        switch(this.playerID){
            case 1:
                if(Gdx.input.isKeyPressed(Input.Keys.A)){
                   return true; 
                }
                break;
            case 2:
                if(Gdx.input.isKeyPressed(Input.Keys.F)){
                    return true;
                }
                break;
            case 3:
                if(Gdx.input.isKeyPressed(Input.Keys.J)){
                    return true;
                }
                break;
            case 4:
                if(Gdx.input.isKeyPressed(Input.Keys.SEMICOLON)){
                    return true;
                }
                break;
            case 5:
                if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
                    return true;
                }
                break;
            case 6:
                if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_4)){
                    return true;
                }
                break;
            default:
                System.out.println("INPUT ERROR: THE ID FOR THIS CHARACTER IS SET WRONG!!!");
                
        }
        return false;
    }
    
    public boolean justPressedLeft(){
        switch(this.playerID){
            case 1:
                if(Gdx.input.isKeyJustPressed(Input.Keys.A)){
                   return true; 
                }
                break;
            case 2:
                if(Gdx.input.isKeyJustPressed(Input.Keys.F)){
                    return true;
                }
                break;
            case 3:
                if(Gdx.input.isKeyJustPressed(Input.Keys.J)){
                    return true;
                }
                break;
            case 4:
                if(Gdx.input.isKeyJustPressed(Input.Keys.SEMICOLON)){
                    return true;
                }
                break;
            case 5:
                if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)){
                    return true;
                }
                break;
            case 6:
                if(Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_4)){
                    return true;
                }
                break;
            default:
                System.out.println("INPUT ERROR: THE ID FOR THIS CHARACTER IS SET WRONG!!!");
                
        }
        return false;
    }
     
    public boolean isPressingDown(){
        switch(this.playerID){
            case 1:
                if(Gdx.input.isKeyPressed(Input.Keys.S)){
                   return true; 
                }
                break;
            case 2:
                if(Gdx.input.isKeyPressed(Input.Keys.G)){
                    return true;
                }
                break;
            case 3:
                if(Gdx.input.isKeyPressed(Input.Keys.K)){
                    return true;
                }
                break;
            case 4:
                if(Gdx.input.isKeyPressed(Input.Keys.APOSTROPHE)){
                    return true;
                }
                break;
            case 5:
                if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
                    return true;
                }
                break;
            case 6:
                if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_5)){
                    return true;
                }
                break;
            default:
                System.out.println("INPUT ERROR: THE ID FOR THIS CHARACTER IS SET WRONG!!!");
                
        }
        return false;
    }
    
    public boolean justPressedDown(){
        switch(this.playerID){
            case 1:
                if(Gdx.input.isKeyJustPressed(Input.Keys.S)){
                   return true; 
                }
                break;
            case 2:
                if(Gdx.input.isKeyJustPressed(Input.Keys.G)){
                    return true;
                }
                break;
            case 3:
                if(Gdx.input.isKeyJustPressed(Input.Keys.K)){
                    return true;
                }
                break;
            case 4:
                if(Gdx.input.isKeyJustPressed(Input.Keys.APOSTROPHE)){
                    return true;
                }
                break;
            case 5:
                if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
                    return true;
                }
                break;
            case 6:
                if(Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_5)){
                    return true;
                }
                break;
            default:
                System.out.println("INPUT ERROR: THE ID FOR THIS CHARACTER IS SET WRONG!!!");
                
        }
        return false;
    }
    
    public boolean isPressingRight(){
        switch(this.playerID){
            case 1:
                if(Gdx.input.isKeyPressed(Input.Keys.D)){
                    return true; 
                }
                break;
            case 2:
                if(Gdx.input.isKeyPressed(Input.Keys.H)){
                    return true;
                }
                break;
            case 3:
                if(Gdx.input.isKeyPressed(Input.Keys.L)){
                    return true;
                }
                break;
            case 4:
                if(Gdx.input.isKeyPressed(Input.Keys.ENTER)){
                    return true;
                }
                break;
            case 5:
                if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
                    return true;
                }
                break;
            case 6:
                if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_6)){
                    return true;
                }
                break;
            default:
                System.out.println("INPUT ERROR: THE ID FOR THIS CHARACTER IS SET WRONG!!!");
                
        }
        return false;
    }
    
    public boolean justPressedRight(){
        switch(this.playerID){
            case 1:
                if(Gdx.input.isKeyJustPressed(Input.Keys.D)){
                    return true; 
                }
                break;
            case 2:
                if(Gdx.input.isKeyJustPressed(Input.Keys.H)){
                    return true;
                }
                break;
            case 3:
                if(Gdx.input.isKeyJustPressed(Input.Keys.L)){
                    return true;
                }
                break;
            case 4:
                if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
                    return true;
                }
                break;
            case 5:
                if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)){
                    return true;
                }
                break;
            case 6:
                if(Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_6)){
                    return true;
                }
                break;
            default:
                System.out.println("INPUT ERROR: THE ID FOR THIS CHARACTER IS SET WRONG!!!");
                
        }
        return false;
    }
       
    public boolean isPressingAction(){
        switch(this.playerID){
            case 1:
                if(Gdx.input.isKeyPressed(Input.Keys.E)){
                   return true; 
                }
                break;
            case 2:
                if(Gdx.input.isKeyPressed(Input.Keys.Y)){
                    return true;
                }
                break;
            case 3:
                if(Gdx.input.isKeyPressed(Input.Keys.O)){
                    return true;
                }
                break;
            case 4:
                if(Gdx.input.isKeyPressed(Input.Keys.RIGHT_BRACKET)){
                    return true;
                }
                break;
            case 5:
                if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
                    return true;
                }
                break;
            case 6:
                if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_9)){
                    return true;
                }
                break;
            default:
                System.out.println("INPUT ERROR: THE ID FOR THIS CHARACTER IS SET WRONG!!!");
                
        }
        return false;
    }
    
    public boolean justPressedAction(){
        switch(this.playerID){
            case 1:
                if(Gdx.input.isKeyJustPressed(Input.Keys.E)){
                   return true; 
                }
                break;
            case 2:
                if(Gdx.input.isKeyJustPressed(Input.Keys.Y)){
                    return true;
                }
                break;
            case 3:
                if(Gdx.input.isKeyJustPressed(Input.Keys.O)){
                    return true;
                }
                break;
            case 4:
                if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT_BRACKET)){
                    return true;
                }
                break;
            case 5:
                if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                    return true;
                }
                break;
            case 6:
                if(Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_9)){
                    return true;
                }
                break;
            default:
                System.out.println("INPUT ERROR: THE ID FOR THIS CHARACTER IS SET WRONG!!!");
                
        }
        return false;
    }
}
