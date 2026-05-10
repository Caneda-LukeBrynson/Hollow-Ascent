package engine;

import model.entity.Player;
import model.entity.Shadow;
import model.Level;

public class Game {

    private Player player;
    private Shadow shadow;
    private Level currentLevel;
    private int currentLevelIndex;
    private boolean gameOver;
    private boolean levelComplete;
    private CollisionManager collisionManager;
    private int tickCounter;

    public Game(Player player, Shadow shadow) {

        this.player = player;
        this.shadow = shadow;
        this.currentLevelIndex = 0;
        this.gameOver = false;
        this.levelComplete = false;
        this.collisionManager = new CollisionManager();
        this.tickCounter = 0;
    }

    public Player getPlayer() {

        return player;
    }

    public Shadow getShadow() {

        return shadow;
    }

    public Level getCurrentLevel() {

        return currentLevel;
    }

    public int getCurrentLevelIndex() {

        return currentLevelIndex;
    }

    public boolean isGameOver() {

        return gameOver;
    }

    public boolean isLevelComplete() {

        return levelComplete;
    }

    public void setCurrentLevel(Level level) {

        this.currentLevel = level;
    }

    public void setGameOver(boolean gameOver) {

        this.gameOver = gameOver;
    }

    public void setLevelComplete(boolean levelComplete) {

        this.levelComplete = levelComplete;
    }

    public CollisionManager getCollisionManager() {

        return collisionManager;
    }

    public void setCurrentLevelIndex(int index) {

        this.currentLevelIndex = index;
    }

    public int getTickCounter() {

        return tickCounter;
    }

    public void incrementTick() {

        tickCounter++;
    }

    public void resetTickCounter() {
        
        tickCounter = 0;
    }
}