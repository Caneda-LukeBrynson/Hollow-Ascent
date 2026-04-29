public class Goal{

    private Position position;

    public Goal(Position position){

        this.position = position;
    }

    public Position getPosition(){

        return position;
    }

    public boolean isReached(Position p) {

        return this.position.equals(p);
    } //changes were made here

    
}