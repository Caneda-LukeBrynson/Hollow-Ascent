public class Ladder{

    private Position top;
    private Position bottom;

    public Ladder(Position top, Position bottom){

        this.top = top;
        this.bottom = bottom;
    }

    public Position getTop(){

        return top;
    }

    public Position bottom(){

        return bottom;
    }

    public boolean isAtLadder(Position pos){

        return pos.equals(top) || pos.equals(bottom);
    }
}