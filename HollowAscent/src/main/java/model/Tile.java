public class Tile {
    private boolean walkable;
    private String type;

    public Tile(boolean walkable, String type) {
        this.walkable = walkable;
        this.type = type;
    }

    public boolean isWalkable() { 

        return walkable; 
    }

    public String getType() { 
        return type; 
    }

    public void setWalkable(boolean walkable) { 
        
        this.walkable = walkable; 
    }
}