
public class Door{

    private Position position;
    private boolean open;
    
     public Door(Position position) {

        this.position = position;
        this.open = true; 

    }

    public void open() { 
        
        this.open = true; 
    
    }
    
    public void close() { 
        
        this.open = false; 
    
    }
    public boolean isOpen() { 
        return open; 
    
    }

    public Position getPosition() { 
        return position; 
    
    }
}