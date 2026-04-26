public class GameEngine {

    private Game game;
    private LevelManager levelManager;
    private boolean running;

    public GameEngine(Game game, LevelManager levelManager) {
        this.game = game;
        this.levelManager = levelManager;
        this.running = false;
    }

    public void start() {
        running = true;
    }

    public void stop() {
        running = false;
    }

    public void tick() {
    }

    public boolean isRunning() {
        return running;
    }

    public Game getGame() {
        return game;
    }
}
