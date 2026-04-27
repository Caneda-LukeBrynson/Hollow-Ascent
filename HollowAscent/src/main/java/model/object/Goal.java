public class Goal{

    private Position position;

    public Goal(Position position){

        this.position = position;
    }

    public Position getPosition(){

        return position;
    }

    private boolean isReached(Position p) {

        return this.position.equals(p)
    }
}