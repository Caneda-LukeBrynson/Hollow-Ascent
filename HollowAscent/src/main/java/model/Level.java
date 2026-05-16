package model;

import model.object.Button;
import model.object.Door;
import model.object.Ladder;
import model.object.Goal;
import model.Position;
import java.util.List;

public class Level {
    private Tile[][] grid;
    private List<Button> buttons;
    private List<Door> doors;
    private List<Ladder> ladders;
    private Goal goal;
    private Position playerSpawn;
    private int shadowDelay;

    public Level(Tile[][] grid, List<Button> buttons, List<Ladder> ladders, List<Door> doors, Goal goal, int shadowDelay) {
        this.grid = grid;
        this.buttons = buttons;
        this.ladders = ladders;
        this.doors = doors;
        this.goal = goal;
        this.shadowDelay = shadowDelay;
        this.playerSpawn = findPlayerSpawn(grid);

    }

    public int getShadowDelay() {
        
        return shadowDelay;
    }

    private Position findPlayerSpawn(Tile[][] grid) {

        for (int y = 0; y < grid.length; y++) {

            for (int x = 0; x < grid[y].length; x++) {

                if (grid[y][x].getType().equals("PLAYER_START")) {

                    return new Position(x, y);
                }
            }
        }

        return new Position(1, 1);
    }

    public Tile[][] getGrid() {

        return grid;
    }

    public List<Button> getButtons() {

        return buttons;
    }

    public List<Door> getDoors() {

        return doors;
    }

    public List<Ladder> getLadders() {

        return ladders;
    }

    public Goal getGoal() {

        return goal;
    }

    public Position getPlayerSpawn() {

        return playerSpawn;
    }

    public Tile getTile(int x, int y) {

        return grid[y][x];
    }

    public boolean isGoalReached(Position pos) {
        
        return goal != null && goal.isReached(pos);
    }
}