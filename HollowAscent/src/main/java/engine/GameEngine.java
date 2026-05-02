public class GameEngine {

    private Game game;
    private LevelManager levelManager;
    private boolean running;
    private InputHandler inputHandler;
    private Timer gameTimer;

    public GameEngine(Game game, LevelManager levelManager) {
        this.game = game;
        this.levelManager = levelManager;
        this.running = false;
        this.inputHandler = new InputHandler();
    }

    public void start() {
        running = true;
        startGameLoop();
    }

    public void stop() {
        running = false;
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    private void startGameLoop() {
        gameTimer = new Timer(16, e -> {
            if (running && !game.isGameOver()) {
                tick();
            }
        });
        gameTimer.start();
    }

    public void tick() {
        ActionType action = inputHandler.getLastAction();
        if (action != null) {
            Player player = game.getPlayer();
            Position currentPos = player.getPosition();
            Position newPos = calculateNewPosition(currentPos, action);
            
            CollisionManager cm = game.getCollisionManager();
            Level level = game.getCurrentLevel();
            
            if (!cm.isOutOfBounds(level, newPos) && cm.isWalkable(level, newPos)) {
                player.setPosition(newPos);
                player.recordAction(action);
                
                for (Button button : level.getButtons()) {
                    if (button.getPosition().equals(newPos) && !button.isPressed()) {
                        button.onStep();
                    }
                }
                
                if (level.isGoalReached(newPos)) {
                    game.setLevelComplete(true);
                    running = false;
                }
            }
            
            inputHandler.clearAction();
        }
        
        Shadow shadow = game.getShadow();
        List<PlayerAction> playerActions = game.getPlayer().getActionHistory();
        int shadowActionIndex = Math.max(0, playerActions.size() - shadow.getDelayTicks());
        
        if (shadowActionIndex > shadow.getCurrentIndex()) {
            List<PlayerAction> actionsToReplay = new ArrayList<>();
            for (int i = 0; i <= shadowActionIndex; i++) {
                actionsToReplay.add(playerActions.get(i));
            }
            shadow.setActionsToReplay(actionsToReplay);
            shadow.update();
        }
        
        if (game.getPlayer().getPosition().equals(game.getShadow().getPosition())) {
            game.setGameOver(true);
            running = false;
        }
    }

    private Position calculateNewPosition(Position pos, ActionType action) {
        int newX = pos.getX();
        int newY = pos.getY();
        
        switch (action) {
            case UP: newY--; break;
            case DOWN: newY++; break;
            case LEFT: newX--; break;
            case RIGHT: newX++; break;
            default: break;
        }
        
        return new Position(newX, newY);
    }

    public boolean isRunning() {
        return running;
    }

    public Game getGame() {
        return game;
    }
    
    public InputHandler getInputHandler() {
        return inputHandler;
    }
}
