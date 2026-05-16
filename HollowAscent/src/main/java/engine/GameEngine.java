package engine;

import java.util.List;
import javax.swing.Timer;
import model.ActionType;
import model.Level;
import model.PlayerAction;
import model.Position;
import model.Tile;
import model.entity.Player;
import model.entity.Shadow;
import model.object.Button;
import model.object.Door;

public class GameEngine {
    private Game game;
    private LevelManager levelManager;
    private boolean running;
    private InputHandler inputHandler;
    private Timer gameTimer;
    private int fallCooldown;
    private int gravityCooldown;

    public GameEngine(Game game, LevelManager levelManager) {
        this.game = game;
        this.levelManager = levelManager;
        this.running = false;
        this.inputHandler = new InputHandler();
        this.fallCooldown = 0;
        this.gravityCooldown = 0;
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

    public void restart() {
        running = true;
        if (gameTimer != null && !gameTimer.isRunning()) {
            startGameLoop();
        }
    }

    private void startGameLoop() {
        gameTimer = new Timer(16, e -> {
            if (running && !game.isGameOver() && !game.isLevelComplete()) {
                tick();
            }
        });
        gameTimer.start();
    }

    public boolean nextLevel() {
        if (levelManager.hasNextLevel()) {
            Level nextLevel = levelManager.nextLevel();
            game.setCurrentLevel(nextLevel);
            game.setLevelComplete(false);
            game.setGameOver(false);

            Position spawnPos = nextLevel.getPlayerSpawn();
            game.getPlayer().setPosition(new Position(spawnPos.getX(), spawnPos.getY()));
            game.getPlayer().clearHistory();

            game.getShadow().reset();
            game.getShadow().setPosition(new Position(spawnPos.getX(), spawnPos.getY()));

            running = true;
            return true;
        }
        return false;
    }


    private void applyGravity(Player player, Level level) {
        Position pos = player.getPosition();
        if (isOnLadder(level, pos)) return;

        int x = pos.getX();
        int y = pos.getY();
        int nextY = y + 1;

        if (nextY < level.getGrid().length && !isSolidFloor(level, x, nextY)) {
            Tile t = level.getTile(x, nextY);
            if (t != null && t.isWalkable() && !t.getType().equals("WALL")) {
                player.setPosition(new Position(x, nextY));
            }
        }
    }

    private void applyGravity(Shadow shadow, Level level) {
        if (!shadow.isActive()) return;
        Position pos = shadow.getPosition();
        if (isOnLadder(level, pos)) return;

        int x = pos.getX();
        int y = pos.getY();
        int nextY = y + 1;

        if (nextY < level.getGrid().length && !isSolidFloor(level, x, nextY)) {
            Tile t = level.getTile(x, nextY);
            if (t != null && t.isWalkable() && !t.getType().equals("WALL")) {
                shadow.setPosition(new Position(x, nextY));
            }
        }
    }


    private Door getDoorAt(Level level, int x, int y) {
        for (Door door : level.getDoors()) {
            if (door.getPosition().getX() == x && door.getPosition().getY() == y) {
                return door;
            }
        }
        return null;
    }

    private boolean isSolidFloor(Level level, int x, int y) {
        
        if (y < 0 || y >= level.getGrid().length) return true;
        Tile t = level.getTile(x, y);
        if (t == null) return true;
        String type = t.getType();
        if (type.equals("DOOR")) {
            Door door = getDoorAt(level, x, y);
            return door == null || !door.isOpen();
        }

        return !t.isWalkable() || type.equals("WALL") || type.equals("FLOOR") ||
            type.equals("LADDER") || type.equals("PLAYER_START") || type.equals("BUTTON");
    }

    private boolean hasGroundBelow(Level level, Position pos) {
        return isSolidFloor(level, pos.getX(), pos.getY() + 1);
    }

    private boolean isInPit(Level level, Position pos) {
        int x = pos.getX();
        int y = pos.getY();
        Tile current = level.getTile(x, y);
        if (current == null) return false;
 
        if (!current.getType().equals("AREA")) return false;

        Tile below = level.getTile(x, y + 1);
        if (below == null) return false;

        return below.getType().equals("WALL");
    }

    private boolean isOnLadder(Level level, Position pos) {
        CollisionManager cm = game.getCollisionManager();
        if (cm.isOutOfBounds(level, pos)) return false;
        Tile t = level.getTile(pos.getX(), pos.getY());
        return t != null && t.getType().equals("LADDER");
    }

    private boolean isWalkable(Level level, Position pos) {
        CollisionManager cm = game.getCollisionManager();
        if (cm.isOutOfBounds(level, pos)) return false;
        Tile t = level.getTile(pos.getX(), pos.getY());
        if (t == null || !t.isWalkable() || t.getType().equals("WALL")) return false;
        if (t.getType().equals("DOOR")) {
            Door door = getDoorAt(level, pos.getX(), pos.getY());
            return door != null && door.isOpen();
        }
        return true;
    }


    private boolean canMoveHorizontal(Level level, Position from, Position to) {
        return isWalkable(level, to);
    }

    private boolean canMoveUp(Level level, Position from, Position to) {
        if (!isWalkable(level, to)) return false;
        return isOnLadder(level, from);
    }

    private boolean canMoveDown(Level level, Position from, Position to) {
        if (!isWalkable(level, to)) return false;
        Tile dest = level.getTile(to.getX(), to.getY());
        return dest != null && dest.getType().equals("LADDER");
    }

    private boolean canMove(Level level, Position from, ActionType action) {
        Position to = calculateNewPosition(from, action);
        switch (action) {
            case LEFT:
            case RIGHT:  return canMoveHorizontal(level, from, to);
            case UP:     return canMoveUp(level, from, to);
            case DOWN:   return canMoveDown(level, from, to);
            default:     return false;
        }
    }


    public void tick() {
    fallCooldown--;

    
    Level level = game.getCurrentLevel();
    Position playerPos = game.getPlayer().getPosition();

    Tile playerTile = level.getTile(playerPos.getX(), playerPos.getY());

    if (playerTile != null && playerTile.getType().equals("WALL")) {

        game.setGameOver(true);
        running = false;
        return;

    }

    Shadow shadow = game.getShadow();

    if (shadow.isActive()) {

        Position shadowPos = shadow.getPosition();

        Tile shadowTile = level.getTile(shadowPos.getX(), shadowPos.getY());

        if (shadowTile != null && shadowTile.getType().equals("WALL")) {
            game.setGameOver(true);
            running = false;
            return;

        }
    }

    ActionType action = inputHandler.getLastAction();

    if (action != null && fallCooldown <= 0) {
        Player player = game.getPlayer();
        Position currentPos = player.getPosition();

        if (canMove(level, currentPos, action)) {

            Position newPos = calculateNewPosition(currentPos, action);
            player.setPosition(newPos);
            player.recordAction(action);

            if (level.isGoalReached(newPos)) {

                game.setLevelComplete(true);
                running = false;
            }

            fallCooldown = 5;
        }

        inputHandler.clearAction();
    }

    List<PlayerAction> playerActions = game.getPlayer().getActionHistory();

    Position oldShadowPos = null;

    if (shadow.isActive()) {
        oldShadowPos = new Position(shadow.getPosition().getX(), shadow.getPosition().getY());

    }

    if (playerActions.size() > 0) {

        shadow.setActionsToReplay(playerActions);
    }

    shadow.update(playerActions.size());

    if (shadow.isActive() && oldShadowPos != null) {

        Position newShadowPos = shadow.getPosition();

        if (!oldShadowPos.equals(newShadowPos)) {
            fallCooldown = 5;

        }
    }

    gravityCooldown--;
    
    if (gravityCooldown <= 0) {

        applyGravity(game.getPlayer(), level);

        if (!game.isGameOver()) {

            Position p = game.getPlayer().getPosition();
            if (isInPit(level, p)) {

                game.setGameOver(true);
                running = false;
            }
        }

        Position preFallShadowPos = shadow.isActive() ? shadow.getPosition() : null;
        applyGravity(shadow, level);

        if (shadow.isActive() && preFallShadowPos != null) {

            Position postFallShadowPos = shadow.getPosition();

            if (!game.isGameOver() && isInPit(level, postFallShadowPos)) {

                game.setGameOver(true);
                running = false;
            }
        }

        gravityCooldown = 6;
    }

    for (Button button : level.getButtons()) {

        Position btnPos = button.getPosition();
        boolean playerOn = game.getPlayer().getPosition().equals(btnPos);
        boolean shadowOn = shadow.isActive() && shadow.getPosition().equals(btnPos);

        if (playerOn || shadowOn) {
            button.onStep();

        } else {

            button.onRelease();
        }
    }

    if (shadow.isActive() && shadow.getCurrentIndex() > 0 && game.getPlayer().getPosition().equals(shadow.getPosition())) {
        
        game.setGameOver(true);
        running = false;
    }
}


    private Position calculateNewPosition(Position pos, ActionType action) {
        int newX = pos.getX();
        int newY = pos.getY();
        switch (action) {
            case UP:    newY--; break;
            case DOWN:  newY++; break;
            case LEFT:  newX--; break;
            case RIGHT: newX++; break;
            default: break;
        }
        return new Position(newX, newY);
    }

    public boolean isRunning(){ 
        
        return running; 
    
    }

    public Game getGame(){ 
        
        return game; 
    }

    public InputHandler getInputHandler()   { 
        
        return inputHandler; 
    
    }

    public LevelManager getLevelManager()   { 
        
        return levelManager; 
    
    }
}