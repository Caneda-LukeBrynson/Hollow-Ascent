import java.util.List;

public class Level{

    private Title[][] grid;
    private List<Button> buttons;
    private List<Door> doors;
    private Goal goal;

    public Level(Tile[][] grid, List<Button> buttons, List<Door> doors, Goal goal) {
        this.grid = grid;
        this.buttons = buttons;
        this.doors = doors;
        this.goal = goal;

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

    public Goal getGoal() { 
        
        return goal; 
    }
    
    public Tile getTile(int x, int y) { 
        
        return grid[y][x]; 
    
    }
}