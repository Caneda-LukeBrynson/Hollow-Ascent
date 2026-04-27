public class Game {

    private Player player;
    private Shadow shadow;
    private Level currentLevel;
    private int currentLevelIndex;
    private boolean gameOver;
    private boolean levelComplete;

    public Game(Player player, Shadow shadow) {
        this.player = player;
        this.shadow = shadow;
        this.currentLevelIndex = 0;
        this.gameOver = false;
        this.levelComplete = false;
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
}
