package model;

public class PlayerAction {
    private ActionType type;
    private int tick;

    public PlayerAction(ActionType type, int tick) {

        this.type = type;
        this.tick = tick;
    }

    public ActionType getType() {

        return type;
    }

    public int getTick() {
        
        return tick;
    }
}